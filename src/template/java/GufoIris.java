package br.ufes.inf.nemo.ufo.protege;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.parameters.Imports;

// Class generated by build.js
public class GufoIris {

    // foreach void {
    // Making Netbeans happy. These lines will not be present in the final file
    final static IRI ParentClassName = null;
    final static int INDEX = 0;
    // }

    // Namespace prefixes
    // foreach namespace
    public static final String PREFIX = "NAMESPACE";

    // IRI of classes in GUFO Ontology
    // foreach class
    public static final IRI ClassName = IRI.create(PREFIX, "ClassName");

    // Set of classes expected to be specialized
    public static final Set<IRI> publicClasses;
    static {
        Set<IRI> set = new HashSet<>();
        // foreach publicClassName
        set.add(ClassName);
        publicClasses = Collections.unmodifiableSet(set);
    }

    // Map with hierarchyview information
    public static final Map<IRI, HierarchyNode> tree;
    static {
       Map<IRI, HierarchyNode> map = new HashMap<>();
       // foreach treeNode {
       HierarchyNode nodeForClassName = new HierarchyNode(ClassName, ParentClassName, INDEX);
       map.put(null, nodeForClassName);
       // }
       tree = Collections.unmodifiableMap(map);
    }

    // Set of classes expected to be specialized
    public static final Set<IRI> nonPublicClasses;
    static {
        Set<IRI> set = new HashSet<>(tree.keySet());
        set.remove(null);
        set.removeAll(publicClasses);
        nonPublicClasses = Collections.unmodifiableSet(set);
    }

    public static boolean isPublicUFOClass(OWLClass owlClass) {
        return publicClasses.contains(owlClass.getIRI());
    }

    public static boolean isPublicUFOClass(OWLClassExpression owlClass) {
        return owlClass.isNamed() && isPublicUFOClass(owlClass.asOWLClass());
    }

    public static boolean isUFOViewRootClass(OWLClassExpression owlClass) {
        return owlClass.isNamed() && isUFOViewRootClass(owlClass.asOWLClass());
    }

    public static boolean isUFOViewRootClass(OWLClass owlClass) {
        return tree.get(null).isParentOf(owlClass.getIRI());
    }

    public static Stream<OWLClass> owlClasses(OWLOntology ontology) {
        return ontology
                .getNestedClassExpressions()
                .stream()
                .filter(OWLClassExpression::isNamed)
                .map(OWLClassExpression::asOWLClass)
                ;
    }

    public static Stream<OWLClass> publicUFOClasses(OWLOntology ontology) {
        return owlClasses(ontology).filter(GufoIris::isPublicUFOClass);
    }

    public static Stream<OWLClass> ufoViewRootClasses(OWLOntology ontology) {
        return owlClasses(ontology).filter(GufoIris::isUFOViewRootClass);
    }

    public static boolean isUFOViewClass(OWLClass n) {
        return tree.containsKey(n.getIRI());
    }

    public static void getUFOViewParents(OWLOntology ontology,
            OWLClass n, Set<OWLClass> result) {
        HierarchyNode node = tree.get(n.getIRI());
        ontology
                .getEntitiesInSignature(node.getParentIri(), Imports.INCLUDED)
                .stream()
                .filter(OWLEntity::isOWLClass)
                .map(OWLEntity::asOWLClass)
                .forEach(result::add)
                ;
    }

    public static boolean isNonLeafUFOViewClass(OWLClass owlClass) {
        HierarchyNode node = tree.get(owlClass.getIRI());
        return node != null && !node.getChildren().isEmpty();
    }

    public static Set<OWLClass> getUFOViewChildren(
            Collection<OWLOntology> ontologies, OWLClass owlClass) {
        return tree.get(owlClass.getIRI())
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

    private static HierarchyNode getHierarchyNode(OWLObject object) {
        return !(object instanceof OWLClass) ? null :
                tree.get(((OWLClass)object).getIRI())
                ;
    }

    public static int compareOWLObjects(OWLObject a, OWLObject b) {
        HierarchyNode nodeA = getHierarchyNode(a);
        HierarchyNode nodeB = getHierarchyNode(b);
        return nodeA == null ? (nodeB == null ? 0 : 1) : nodeA.compareTo(nodeB);
    }
}
