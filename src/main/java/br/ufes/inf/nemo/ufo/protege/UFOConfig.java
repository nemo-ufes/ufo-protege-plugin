/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege;

import br.ufes.inf.nemo.protege.annotations.EditorKitHook;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.protege.editor.core.ModelManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.parameters.Imports;

/**
 *
 * @author luciano
 */
@EditorKitHook(id = "ufopp.hook")
public class UFOConfig extends AbstractEditorKitHook {

    private final Set<IRI> publicUFOClasses = GufoIris.publicClasses;
    private final Map<IRI, HierarchyNode> ufoHierarchyView = GufoIris.tree;

    public static UFOConfig get(ModelManager modelManager) {
        return AbstractEditorKitHook.get(modelManager, UFOConfig.class);
    }

    public boolean isPublicUFOClass(OWLClassExpression owlClass) {
        return owlClass.isNamed() && isPublicUFOClass(owlClass.asOWLClass());
    }

    public boolean isPublicUFOClass(OWLClass owlClass) {
        return publicUFOClasses.contains(owlClass.getIRI());
    }

    public boolean isUFOViewRootClass(OWLClassExpression owlClass) {
        return owlClass.isNamed() && isUFOViewRootClass(owlClass.asOWLClass());
    }

    public boolean isUFOViewRootClass(OWLClass owlClass) {
        return ufoHierarchyView.get(null).isParentOf(owlClass.getIRI());
    }

    public Stream<OWLClass> owlClasses(OWLOntology ontology) {
        return ontology
                .getNestedClassExpressions()
                .stream()
                .filter(OWLClassExpression::isNamed)
                .map(OWLClassExpression::asOWLClass)
                ;
    }

    public Stream<OWLClass> publicUFOClasses(OWLOntology ontology) {
        return owlClasses(ontology).filter(this::isPublicUFOClass);
    }

    public Stream<OWLClass> ufoViewRootClasses(OWLOntology ontology) {
        return owlClasses(ontology).filter(this::isUFOViewRootClass);
    }

    public boolean isUFOViewClass(OWLClass n) {
        return ufoHierarchyView.containsKey(n.getIRI());
    }

    public void getUFOViewParents(OWLOntology ontology,
            OWLClass n, Set<OWLClass> result) {
        HierarchyNode node = ufoHierarchyView.get(n.getIRI());
        ontology
                .getEntitiesInSignature(node.getParentIri(), Imports.INCLUDED)
                .stream()
                .filter(OWLEntity::isOWLClass)
                .map(OWLEntity::asOWLClass)
                .forEach(result::add)
                ;
    }

    public boolean isNonLeafUFOViewClass(OWLClass owlClass) {
        HierarchyNode node = ufoHierarchyView.get(owlClass.getIRI());
        return node != null && !node.getChildren().isEmpty();
    }

    public Set<OWLClass> getUFOViewChildren(
            Collection<OWLOntology> ontologies, OWLClass owlClass) {
        return ufoHierarchyView.get(owlClass.getIRI())
            .getChildren()
            .stream()
            .flatMap(iri ->
                ontologies.stream().flatMap(ontology ->
                    ontology.getEntitiesInSignature(iri, Imports.INCLUDED)
                            .stream()
                )
            )
            .filter(OWLEntity::isOWLClass)
            .map(OWLEntity::asOWLClass)
            .collect(Collectors.toCollection(HashSet::new))
            ;
    }

    public int compareOWLObjects(OWLObject a, OWLObject b) {
        HierarchyNode nodeA = getHierarchyNode(a);
        HierarchyNode nodeB = getHierarchyNode(b);
        return nodeA == null ? (nodeB == null ? 0 : 1) : nodeA.compareTo(nodeB);
    }

    private HierarchyNode getHierarchyNode(OWLObject object) {
        return !(object instanceof OWLClass) ? null :
                ufoHierarchyView.get(((OWLClass)object).getIRI())
                ;
    }

    public Set<IRI> getPublicUFOClasses() {
        return publicUFOClasses;
    }
}
