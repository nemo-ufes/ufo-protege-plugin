/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.validation;

import br.ufes.inf.nemo.ufo.protege.GufoIris;
import java.lang.reflect.ParameterizedType;
import java.util.function.Supplier;
import org.semanticweb.owlapi.model.HasIRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObject;

/**
 * Validation rule which should be followed by ontologies.
 * <p>
 * New rules should be created in the package
 * br.ufes.inf.nemo.ufo.protege.validation.rules. Every class in that package
 * will be automatically instantiated and called for validating the active
 * ontology.
 * <p>
 * To be considered for validation the class must extend the Rule class. It's
 * recommended to put a {@link RuleInfo} on the class.
 * <p>
 *
 * @author luciano
 */
public abstract class Rule<T extends OWLObject> extends GufoIris {

    protected Validation validation;
    protected Class<T> targetType;

    protected T target;
    protected Violation violation;

    public void initialize(Validation validation) throws Exception {
        this.validation = validation;
    }

    protected Class<T> getTargetType() {
        if (targetType == null) {
            try {
                Class<?> clazz = getClass();
                while (clazz.getSuperclass() != Rule.class) {
                    clazz = clazz.getSuperclass();
                }
                ParameterizedType type = (ParameterizedType) clazz.getGenericSuperclass();
                targetType = (Class<T>) type.getActualTypeArguments()[0];
            } catch (Exception ex) {
                throw new RuntimeException(String.format(
                    "Unexpected error. Could not infer target type for rule. Rule class: %s",
                    getClass().getName()), ex);
            }
            if (targetType == null) {
                throw new RuntimeException(String.format(
                    "Unexpected error. Target type not found for rule. Rule class: %s",
                    getClass().getName()));
            }

        }
        return targetType;
    }

    /**
     * Test whether this rule is apppliable to given subject.
     * <p>
     * Before calling {@link #validate} the validation process checks whether
     * this rule is appliable to given subject. It does this checking the
     * whether the subject class matches the target type of the rule and then
     * calling this method.
     * <p>
     * The subject is referenced by the <i>target</i> field, and when this
     * method is called, it is already known that the subject is an instance of
     * target type.
     * <p>
     * The target type is given by the value of the generic type argument
     * specified when subclassing this class, while the aplicability of the rule
     * may be changed by overriding this method.
     * <p>
     * For example, a rule may want to check whether a NonSortal class is
     * specializing a Sortal one. The target type is {@link OWLClass}, and the
     * rule is appliable when the subject is instance of NonSortal. So this
     * method would be overriden and return true only when the subject is
     * instance of NonSortal. The {@link #validate} method whould
     * <p>
     * The default implementation of this method returns true.
     *
     * @return true, if the subject, referenced by the <i>target</i> field,
     * should be validated by this rule
     */
    public boolean isAppliable() {
        return true;
    }

    /**
     * Validate given subject.
     * <p>
     * If the subject is of type targeted by this rule, the <i>target</i> field
     * is updated to reference the subject and a check is done, bycalling
     * {@link #isAppliable() } to ensure that the subject should be validated
     * by this rule. If the call returns true, the method {@link #validate} is
     * called to effectively validate the subject.
     *
     * @param context Subject being validated
     */
    void validate(OWLObject subject) {
        if (getTargetType().isInstance(subject)) {
            this.target = (T) subject;
            if (isAppliable()) {
                this.violation = null;
                validate();
            }
        }
    }

    /**
     * Validate subject referenced by <i>target</i> field.
     * <p>
     *
     * Implementations of this method validade the subject (referenced by the
     * <i>target</i> field) and put the result of validation in the object
     * referenced by the <i>validation</i> field. The class
     * {@link ResultBuilder}, instantiated by calling {@link #when(boolean)},
     * can be used in this task.
     */
    public abstract void validate();

    /**
     * @return Label of this rule
     */
    public String getLabel() {
        RuleInfo annotation = getClass().getAnnotation(RuleInfo.class);
        if (annotation != null) {
            return annotation.label();
        } else {
            return getClass().getName();
        }
    }

    public <T> T get(Class<T> helperClass) {
        return validation.get(helperClass);
    }

    protected Violation newViolation(OWLObject... arguments) {
        if (violation != null) {
            throw new RuntimeException(String.format(
                "Unexpected error. A violation has already been created by this rule for this target. Rule class: %s; Target: %s",
                getClass().getName(),
                (target instanceof HasIRI) ?
                        ((HasIRI)target).getIRI().toString() :
                        target.toString()
                ));

        }
        violation = new Violation(arguments);
        validation.addViolation(violation);
        return violation;
    }

    /**
     * Single violation of a rule.
     *
     * @param <R>
     */
    public class Violation<R extends Rule> {

        private final OWLObject[] arguments;

        protected Violation(OWLObject... arguments) {
            assert arguments[0] == target;
            this.arguments = arguments;
        }

        public R getRule() {
            return (R) Rule.this;
        }

        public OWLObject[] getArguments() {
            return arguments;
        }

        public T getSubject() {
            return (T) arguments[0];
        }
    }

    protected ResultBuilder when(boolean b) {
        return new ResultBuilder(b);
    }

    public class ResultBuilder {

        boolean result = true;

        private ResultBuilder(boolean b) {
            result = b;
        }

        public ResultBuilder and(boolean b) {
            result &= b;
            return this;
        }

        public ResultBuilder or(boolean b) {
            result |= b;
            return this;
        }

        public ResultBuilder and(Supplier<Boolean> s) {
            result &= s.get();
            return this;
        }

        public ResultBuilder or(Supplier<Boolean> s) {
            result |= s.get();
            return this;
        }

        public ResultBuilder registerViolationFor(OWLObject... arguments) {
            if (result) {
                newViolation(arguments);
            }
            return this;
        }

        public ResultBuilder registerViolation() {
            if (result) {
                newViolation(target);
            }
            return this;
        }
    }
}
