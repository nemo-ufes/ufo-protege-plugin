/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.validation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.protege.editor.core.Disposable;
import org.protege.editor.core.ModelManager;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a validation being applied to an ontology.
 * <p>
 * A validation is the process of verifying whether the ontology complies with
 * the active rule set. The product of a validation will be a
 * {@link Validation.Result},
 * which has a possibly empty set of Violations of the rules.
 * <p>
 *
 *
 * @author luciano
 */
public class Validation {

    private static final Logger log = LoggerFactory.getLogger(Validation.class);

    static Result on(ModelManager modelManager) {
        Validation validation = new Validation((OWLModelManager) modelManager);
        return validation.validate();
    }

    private final OWLModelManager modelManager;
    private final OWLOntology targetOntology;
    private final Set<OWLOntology> allOntologies;
    private final Set<Rule> rules = new HashSet<>();
    private final Set<Rule.Violation> violations = new HashSet<>();
    private final Map<Class<?>, Object> helpers = new HashMap<>();
    private final Validator validator;
    // Set to hold all declared classes in ontology

    public Validation(OWLModelManager modelManager) {
        this.modelManager = modelManager;
        this.targetOntology = modelManager.getActiveOntology();
        this.allOntologies = modelManager.getActiveOntologies();
        this.validator = modelManager.get(Validator.class);
    }

    public Set<OWLOntology> getAllOntologies() {
        return allOntologies;
    }

    public OWLOntology getTargetOntology() {
        return targetOntology;
    }

    private void initializeRuleSet() {
        RuleLoader ruleLoader = new RuleLoader(this);
        validator
                .ruleConstructors()
                .map(ruleLoader::instantiateRule)
                .map(ruleLoader::initializeRule)
                .filter(rule -> rule != null)
                .forEach(rules::add)
                ;
    }


    public <T> T get(Class<T> helperClass) {

        Disposable object = modelManager.get(helperClass);
        if (helperClass.isInstance(object)) {
            return (T) object;
        }
        return (T) helpers.computeIfAbsent(helperClass, clazz -> {
            try {
                Constructor<T> constructor = ((Class<T>)clazz).getConstructor();
                T result = constructor.newInstance();
                if (result instanceof Initializable) {
                    ((Initializable) result).initialize(this);
                }
                return result;
            } catch (IllegalAccessException |
                    IllegalArgumentException |
                    InstantiationException |
                    NoSuchMethodException |
                    SecurityException |
                    InvocationTargetException ex) {
                throw new RuntimeException(String.format(
                        "Exception thrown on getting helper for class %s.",
                        helperClass.getName()), ex);
            }
        });
    }

    public Result validate() {

        initializeRuleSet();
        // Validate ontology level rules
        rules
                .stream()
                .filter(rule -> rule.isAppliableTo(targetOntology))
                .forEach(rule -> validate(rule, targetOntology))
                ;

        // Validate entity level rules
        targetOntology
                .getAxioms(AxiomType.DECLARATION)
                .stream()
                .map(OWLDeclarationAxiom::getEntity)
                .forEach(entity ->
                    rules
                            .stream()
                            .filter(rule -> rule.isAppliableTo(entity))
                            .forEach(rule -> validate(rule, entity))
                )
                ;
        return new Result(targetOntology, allOntologies, violations);
    }

    private <T extends OWLObject> void validate(Rule<T> rule, T target) {
        try {
            rule.setTarget(target);
            rule.validate();
        } catch (Exception ex) {
            log.error("Exception thrown when applying rule.", ex);
        }
    }

    void addViolation(Rule.Violation violation) {
        violations.add(violation);
    }

    public interface Initializable {
        public void initialize(Validation validation);
    }

    public static class Result {
        private final OWLOntology targetOntology;
        private final Set<OWLOntology> allOntologies;
        private final Set<Rule.Violation> violations;

        public Result(
                OWLOntology targetOntology,
                Set<OWLOntology> allOntologies,
                Set<Rule.Violation> violations) {
            this.targetOntology = targetOntology;
            this.allOntologies = allOntologies;
            this.violations = violations;
        }

        public OWLOntology getTargetOntology() {
            return targetOntology;
        }

        public Set<OWLOntology> getAllOntologies() {
            return allOntologies;
        }

        public Set<Rule.Violation> getViolations() {
            return violations;
        }

        @Override
        public String toString() {
            return violations
                    .stream()
                    .map(violation ->
                        Stream.concat(
                            Stream.of(violation.getRule().getClass().getSimpleName()),
                            violation
                                    .getArguments()[0]
                                    .getSignature()
                                    .stream()
                                    .map(entity -> entity.getIRI().getShortForm())
                        ).collect(Collectors.joining(": ", "(", ")"))
                    )
                    .collect(Collectors.joining("; ", "{", "}"))
                    ;
        }
    }
}
