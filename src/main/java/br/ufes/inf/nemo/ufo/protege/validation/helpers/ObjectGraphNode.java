/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.validation.helpers;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.semanticweb.owlapi.model.HasIRI;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObject;

/**
 *
 * @author luciano
 */
public class ObjectGraphNode {

    private static final int EQUIVALENTS = 0;
    private static final int PARENTS = 1;
//    private static final int CHILDREN = ;
    private static final int TYPES = 2;
//    private static final int INSTANCES = ;
    private static final int ARRAY_SIZE = 3;

    public boolean isIRI() {
        return this.owlObject.isIRI();
    }

    public IRI getIRI() {
        return this.owlObject.isIRI() ? (IRI) owlObject : null;
    }

    private final OWLObject owlObject;
    private Set<ObjectGraphNode>[] relata = new Set[5];

    ObjectGraphNode(OWLObject owlObject) {
        if (owlObject instanceof HasIRI) {
            throw new AssertionError(
                    "An object with IRI was not supposed to be here. It's IRI should be instead.");
        }
        this.owlObject = owlObject;
    }

    static ObjectGraphNode empty(OWLObject owlObject) {
        ObjectGraphNode result = new ObjectGraphNode(owlObject);
        for (int i = 0; i < result.relata.length; i++) {
            result.relata[i] = Collections.EMPTY_SET;
        }
        return result;
    }

    private static Predicate<ObjectGraphNode> is(OWLObject owlObject) {
        final OWLObject objectOrIRI = ObjectGraph.toIRIWhenHasIRI(owlObject);
        return node -> node.owlObject.equals(objectOrIRI);
    }

    @SuppressWarnings("unchecked")
    private static Predicate<ObjectGraphNode> isIn(Set<IRI> iris) {
        return node -> iris.contains((IRI)node.owlObject);
    }

    private Set<ObjectGraphNode> getSet(int index) {
        Set<ObjectGraphNode> result = relata[index];
        return result == null ? relata[index] = new HashSet<>() : result;
    }

    private Stream<ObjectGraphNode> getStream(int index) {
        Set<ObjectGraphNode> result = relata[index];
        return result == null ? Stream.empty() : result.stream();
    }

    /**
     * @return Stream consisting of this node and equivalent nodes
     */
    public Stream<ObjectGraphNode> equivalents() {
        Set<ObjectGraphNode> result = relata[EQUIVALENTS];
        return result == null ? Stream.of(this) : result.stream();
    }

    public Stream<ObjectGraphNode> parents() {
        return getStream(PARENTS);
    }

    public Stream<ObjectGraphNode> types() {
        return getStream(TYPES);
    }

    public void addEquivalent(ObjectGraphNode equivalent) {
        Set<ObjectGraphNode> equivalentsToThis = getSet(EQUIVALENTS);
        if (equivalentsToThis.add(equivalent)) {
            equivalentsToThis.add(this);
            for (int i = 0; i < relata.length; i++) {
                if (this.relata[i] != null) {
                    if (equivalent.relata[i] != null) {
                        equivalent.relata[i].addAll(this.relata[i]);
                    } else {
                        equivalent.relata[i] = this.relata[i];
                    }
                }
            }
            for (ObjectGraphNode node : equivalentsToThis) {
                node.relata = equivalent.relata;
            }
        }
    }

    /**
     * Return this node, equivalent nodes and ancestor nodes.
     * <p>
     *
     * @param visited Predicate to protect against infinite recursion
     * @return Stream containing this node, equivalent nodes and respactive
     * ancestor nodes
     */
    private Stream<ObjectGraphNode> ancestors(Predicate<ObjectGraphNode> visited) {
        return equivalents()
                .filter(visited)
                .flatMap(node -> Stream.concat(
                    Stream.of(node),
                    parents().flatMap(parent -> parent.ancestors(visited))
                ));

    }

    /**
     * @return Stream containing the parents of the node, and the parents of the
     * parents.
     */
    public Stream<ObjectGraphNode> properAncestors() {
        return parents().flatMap(parent ->
                parent.ancestors(new HashSet<>()::add));
    }

    public Stream<ObjectGraphNode> ancestors() {
        return ancestors(new HashSet<>()::add);
    }

    public boolean isSubclassOf(OWLClass owlClass) {
        return ancestors().anyMatch(is(owlClass));
    }

    public boolean isSubclassOf(IRI iri) {
        return ancestors().anyMatch(is(iri));
    }

    public boolean isSubclassOfAny(Set<IRI> iris) {
        return ancestors().anyMatch(isIn(iris));
    }

    public boolean isDirectSubclassOfAny(Set<IRI> iris) {
        return parents().anyMatch(isIn(iris));
    }

    public boolean isProperSubclassOf(OWLClass owlClass) {
        return properAncestors().anyMatch(is(owlClass));
    }

    public boolean isProperSubclassOf(IRI iri) {
        return properAncestors().anyMatch(is(iri));
    }

    public boolean isInstanceOf(OWLClass owlClass) {
        return types().anyMatch(node -> node.isSubclassOf(owlClass));
    }

    public boolean isInstanceOf(IRI iri) {
        return types().anyMatch(node -> node.isSubclassOf(iri));
    }

    void addParent(ObjectGraphNode parent) {
        getSet(PARENTS).add(parent);
    }

    void addType(ObjectGraphNode type) {
        getSet(TYPES).add(type);
    }
}
