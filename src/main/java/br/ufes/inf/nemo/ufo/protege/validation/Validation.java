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

    /**
     * Validate rules on the model manager's active ontology.
     * <br/>
     * This method instantiates a Validation object for the model manager and
     * pass control to its {@link Validation#validate(java.lang.Class)} method.
     * <br/>
     * The rule Class parameter specifies the single rule that should be
     * validated. If it's null, then all rules are validated. Currently this
     * parameter is non null only when unit testing.
     *
     * @param modelManager Model whose active ontology shall be validated.
     * @param ruleClass Optional rule class to be validated.
     * @return Object representing the validation process findings.
     */
    static Result on(ModelManager modelManager,
            Class<? extends Rule> ruleClass ) {
        Validation validation = new Validation((OWLModelManager) modelManager);
        return validation.validate(ruleClass);
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
    private HashMap<String, Object> currentArguments;

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

    private void initializeRuleSet(Class<? extends Rule> ruleClass) {
        RuleLoader ruleLoader = new RuleLoader(this);
        validator
                .ruleConstructors()
                .filter(ruleClass == null ? (constructor -> true) :
                        (ctor -> ctor.getDeclaringClass().equals(ruleClass)))
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

    private Result validate(Class<? extends Rule> ruleClass) {

        initializeRuleSet(ruleClass);

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

    public Violation registerViolation() {
        if (currentViolation != null) {
            throw new RuntimeException(String.format(
                "Unexpected error. A violation has already been created by this rule for this target. Rule class: %s; Target: %s",
                currentRule.getClass().getName(),
                (currentTarget instanceof HasIRI) ?
                        ((HasIRI)currentTarget).getIRI().toString() :
                        currentTarget.toString()
                ));

        }
        currentViolation = new Violation(currentRule, currentArguments);
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
                currentArguments = new HashMap<String, Object>();
                currentArguments.put("", currentTarget);
                rule.validate(subject);
            })
            ;
    }

    public OWLObject getCurrentTarget() {
        return currentTarget;
    }

    void setField(String name, Object value) {
        Object old = currentArguments.put(name, value);
        if (old != null) {
            throw new RuntimeException(String.format(
                "Unexpected error. A value has already been registered by this "
                + "rule for field '%s'. Rule class: '%s'; Target: '%s'",
                name,
                currentRule.getClass().getName(),
                (currentTarget instanceof HasIRI) ?
                        ((HasIRI)currentTarget).getIRI().toString() :
                        currentTarget.toString()
                ));
        }
    }

    public interface Initializable {
        public void initialize(Validation validation);
    }
}
