/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.validation;

import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.validation.helpers.ObjectGraph;
import br.ufes.inf.nemo.ufo.protege.validation.helpers.ObjectGraphNode;
import br.ufes.inf.nemo.ufo.protege.validation.solution.OperationBuilder;
import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.semanticweb.owlapi.model.IRI;
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

    protected OWLObject getTarget() {
        return validation.getCurrentTarget();
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
            if (isAppliable()) {
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

    /**
     * @return Description of this rule
     */
    public String getDescription() {
        RuleInfo annotation = getClass().getAnnotation(RuleInfo.class);
        if (annotation != null) {
            return annotation.description();
        } else {
            return "";
        }
    }

    public <T> T get(Class<T> helperClass) {
        return validation.get(helperClass);
    }

    protected Violation newViolation(OWLObject... arguments) {
        return validation.newViolation(arguments);
    }

    protected ResultBuilder when(boolean b) {
        return new ResultBuilder(b, this);
    }

    protected OperationBuilder solution(String title) {
        return validation.solution(title);
    }

    private IRI newIRI(Set<IRI> set) {
        return validation.newIRI(set);
    }

    private Set<IRI> getSet(IRI iri) {
        return validation.getIRISet(iri);
    }

    private Set<IRI> getSet(Set<IRI> iriSet) {
        return iriSet
            .stream()
            .map(this::getSet)
            .flatMap(Set::stream)
            .collect(Collectors.toSet())
            ;
    }

    protected IRI nodeNavigation(
            Set<IRI> baseClasses,
            Function<ObjectGraphNode, Stream<ObjectGraphNode>> navigate) {
        final ObjectGraph graph = validation.get(ObjectGraph.class);
        Set<IRI> set = baseClasses
                .stream()
                .map(iri -> graph.getNode(iri))
                .flatMap(node -> navigate.apply(node))
                .filter(node -> node.isIRI())
                .map(node -> node.getIRI())
                .collect(Collectors.toSet())
                ;
        return newIRI(set);
    }

    protected IRI nodeNavigation(IRI baseIRI,
            Function<ObjectGraphNode, Stream<ObjectGraphNode>> navigate) {
        return nodeNavigation(getSet(baseIRI), navigate);
    }

    protected IRI instanceOf(IRI typeClass) {
        return nodeNavigation(typeClass, ObjectGraphNode::instances);
    }

    protected IRI subClassOf(IRI superClass) {
        return nodeNavigation(superClass, ObjectGraphNode::descendants);
    }

    protected IRI oneOf(Set<IRI> classSet) {
        return newIRI(getSet(classSet));
    }

    protected IRI oneOf(IRI... classSet) {
        Set<IRI> set = new HashSet<>();
        Collections.addAll(set, classSet);
        return oneOf(set);
    }

    protected IRI subclassesOfAny(Set<IRI> classSet) {
        return nodeNavigation(
                getSet(classSet),
                ObjectGraphNode::descendants);
    }

    protected IRI subclassesOfAny(IRI... classSet) {
        Set<IRI> set = new HashSet<>();
        Collections.addAll(set, classSet);
        return subclassesOfAny(set);
    }
}
