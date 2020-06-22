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
import java.util.Objects;
import java.util.Set;
import org.protege.editor.core.Disposable;
import org.protege.editor.core.ModelManager;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.HasIRI;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a validation (being) applied to an ontology.
 * <p>
 * A validation is the process of verifying whether the ontology complies with
 * the active rule set. The product of a validation process will be a
 * {@link Validation.Result}, which has a possibly empty set of Violations of
 * the rules.
 * <p>
 * Instances of this class are meant to be ephemeral. They are created for
 * applying the rules to an ontology. After validating the rules, references to
 * Validation results are saved but the Validation objects themselves are
 * discarded.
 * <p>
 * This fields declared in this class are object references used during
 * the validation process.
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
    private final Set<Violation> violations = new HashSet<>();
    private final Map<Class<?>, Object> helpers = new HashMap<>();
    private final Validator validator;
    // Set to hold all declared classes in ontology
    private OWLObject currentTarget;
    private Rule currentRule;
    private Violation currentViolation;

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
                .filter(Objects::nonNull)
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
        validateRulesOn(targetOntology);

        // Validate entity level rules
        targetOntology
                .getAxioms(AxiomType.DECLARATION)
                .stream()
                .map(OWLDeclarationAxiom::getEntity)
                .forEach(this::validateRulesOn)
                ;
        return new Result(targetOntology, allOntologies, violations);
    }

    public Violation newViolation(OWLObject... arguments) {
        if (currentViolation != null) {
            throw new RuntimeException(String.format(
                "Unexpected error. A violation has already been created by this rule for this target. Rule class: %s; Target: %s",
                currentTarget.getClass().getName(),
                (currentTarget instanceof HasIRI) ?
                        ((HasIRI)currentTarget).getIRI().toString() :
                        currentTarget.toString()
                ));

        }
        currentViolation = new Violation(currentRule, arguments);
        violations.add(currentViolation);
        return currentViolation;
    }

    private void validateRulesOn(OWLObject subject) {
        currentTarget = subject;
        rules
            .stream()
            .forEach(rule -> {
                currentRule = rule;
                currentViolation = null;
                rule.validate(subject);
            })
            ;
    }

    public OWLObject getCurrentTarget() {
        return currentTarget;
    }

    public interface Initializable {
        public void initialize(Validation validation);
    }
}
