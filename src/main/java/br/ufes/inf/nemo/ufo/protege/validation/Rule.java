/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.validation;

import br.ufes.inf.nemo.ufo.protege.GufoIris;
import java.lang.reflect.ParameterizedType;
import java.util.function.Supplier;
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
    protected T target;
    protected Class<T> targetType;

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
     * Validate given subject.
     * <p>
     *
     * @param context Subject being validated
     * @return Set of violations of this rule
     *
     * @see RuleSubject
     * @see Violation
     */
    public boolean isAppliableTo(T subject) {
        return getTargetType().isInstance(subject);
    }

    /**
     * Validate given subject.
     * <p>
     *
     * @param context Subject being validated
     * @return Set of violations of this rule
     *
     * @see RuleSubject
     * @see Violation
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

    void setTarget(T target) {
        this.target = target;
    }

    protected Violation newViolation(OWLObject... arguments) {
        Violation violation = new Violation(arguments);
        validation.addViolation(violation);
        return violation;
    }

    public class Violation<T extends Rule> {

        private final OWLObject[] arguments;

        protected Violation(OWLObject... arguments) {
            this.arguments = arguments;
        }

        public T getRule() {
            return (T) Rule.this;
        }

        public OWLObject[] getArguments() {
            return arguments;
        }

        public OWLObject getMainObject() {
            return arguments[0];
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
