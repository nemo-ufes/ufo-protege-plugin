/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege;

import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.hierarchy.AbstractOWLObjectHierarchyProvider;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
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

    @Override
    public Set<OWLClass> getRoots() {

        Set<OWLClass> owlThing = Sets.newHashSet(
                getManager().getOWLDataFactory().getOWLThing());

        return (ontologies == null) ? owlThing :

            ontologies
                .stream()
                .flatMap(ufo::ufoViewRootClasses)
                .collect(Collectors.toCollection(() -> owlThing))
                ;
    }

    @Override
    public Set<OWLClass> getParents(OWLClass n) {

        return ontologies
                .stream()
                .flatMap(ontology ->
                    ontology.getAxioms(
                            OWLSubClassOfAxiom.class, n,
                            Imports.INCLUDED, Navigation.IN_SUB_POSITION)
                        .stream()
                )
                .map(OWLSubClassOfAxiom::getSuperClass)
                .filter(OWLClassExpression::isNamed)
                .map(OWLClassExpression::asOWLClass)
                .collect(Collectors.toCollection(HashSet::new))
                ;

    }

    @Override
    public Set<OWLClass> getEquivalents(OWLClass n) {
        return Sets.newHashSet(n);
    }

    @Override
    public boolean containsReference(OWLClass n) {
        return ontologies
                .stream()
                .anyMatch(ontology ->
                    !ontology.getAxioms(n, Imports.INCLUDED).isEmpty()
                )
                ;
    }

    @Override
    protected Collection<OWLClass> getUnfilteredChildren(OWLClass owlClass) {

        return ufo.isNonLeafUFOViewClass(owlClass) ?

            ufo.getUFOViewChildren(ontologies, owlClass)

                :

            ontologies
                .stream()
                .flatMap(ontology ->
                    ontology.getAxioms(
                            OWLSubClassOfAxiom.class, owlClass,
                            Imports.INCLUDED, Navigation.IN_SUPER_POSITION)
                        .stream()
                )
                .map(axiom -> axiom.getSubClass())
                .filter(expr -> !expr.isAnonymous() && !expr.equals(owlClass))
                .map(expr -> expr.asOWLClass())
                .collect(Collectors.toCollection(HashSet::new))
                ;
    }
}
