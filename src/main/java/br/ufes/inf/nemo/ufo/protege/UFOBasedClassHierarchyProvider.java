/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege;

import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.hierarchy.AbstractOWLObjectHierarchyProvider;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.model.parameters.Navigation;

/**
 *
 * @author luciano
 */
public class UFOBasedClassHierarchyProvider extends AbstractOWLObjectHierarchyProvider<OWLClass> {

    private List<OWLOntology> ontologies;
    private final UFOConfig ufo;

    UFOBasedClassHierarchyProvider(OWLModelManager owlModelManager) {
        super(owlModelManager.getOWLOntologyManager());
        this.ufo = UFOConfig.get(owlModelManager);
    }

    @Override
    public void setOntologies(Set<OWLOntology> ontologies) {
        this.ontologies = new ArrayList<>(ontologies);
    }

    private static <T> BinaryOperator<Set<T>> merge() {
        return (Set<T> a, Set<T> b) -> {
            Set<T> result = new HashSet<>(a);
            result.addAll(b);
            return result;
        };
    }

    @Override
    public Set<OWLClass> getRoots() {
        Set<OWLClass> owlThing = Collections.singleton(
                getManager().getOWLDataFactory().getOWLThing());
        if (ontologies == null) {
            return owlThing;
        }
        return ontologies.stream().reduce(
                owlThing,
                (Set<OWLClass> set, OWLOntology ontology) ->
                    ufo.extractUFOClasses(ontology, new HashSet<>(set)),
                merge());
    }

    @Override
    public Set<OWLClass> getParents(OWLClass n) {
        return ontologies.stream().reduce(
            Collections.emptySet(),
            (Set<OWLClass> set, OWLOntology ontology) -> {
                Set<OWLClass> result = new HashSet(set);
                ontology
                    .getAxioms(n, Imports.INCLUDED)
                    .stream()
                    .filter(axiom -> axiom.isOfType(AxiomType.SUBCLASS_OF))
                    .map(axiom -> (OWLSubClassOfAxiom) axiom)
                    .map(axiom -> axiom.getSuperClass())
                    .filter(expression -> !expression.isAnonymous())
                    .map(expression -> expression.asOWLClass())
                    .collect(Collectors.toCollection(()->result));
                return result;
            },
            merge());
    }

    @Override
    public Set<OWLClass> getEquivalents(OWLClass n) {
        return Sets.newHashSet(n);
    }

    @Override
    public boolean containsReference(OWLClass n) {
        return ontologies.stream().anyMatch((OWLOntology ontology) -> {
            return !ontology.getAxioms(n, Imports.INCLUDED).isEmpty();
        });
    }

    @Override
    protected Collection<OWLClass> getUnfilteredChildren(OWLClass owlClass) {
        return ontologies.stream().reduce(
                Collections.emptySet(),
                (Set<OWLClass> set, OWLOntology ontology) ->
                    ontology.getAxioms(
                            OWLSubClassOfAxiom.class, owlClass,
                            Imports.INCLUDED,Navigation.IN_SUPER_POSITION)
                        .stream()
                        .map(axiom -> axiom.getSubClass())
                        .filter(expr -> !expr.isAnonymous() && !expr.equals(owlClass))
                        .map(expr -> expr.asOWLClass())
                        .collect(Collectors
                                .toCollection(()->new HashSet<>(set))),
                merge()
        );
    }
}
