/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.validation.helpers;

import br.ufes.inf.nemo.ufo.protege.validation.Validation;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.semanticweb.owlapi.model.HasIRI;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiomVisitor;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.util.OWLAxiomVisitorAdapter;

/**
 *
 * @author luciano
 */
public class ObjectGraph implements Validation.Initializable {

    private final Map<OWLObject, ObjectGraphNode> nodes = new HashMap<>();

    static OWLObject toIRIWhenHasIRI(OWLObject owlObject) {
        return !(owlObject instanceof HasIRI) ? owlObject :
            ((HasIRI)owlObject).getIRI();
    }

    private ObjectGraphNode of(OWLObject owlObject) {
        owlObject = toIRIWhenHasIRI(owlObject);
        ObjectGraphNode result = nodes.get(owlObject);
        if (result == null) {
            result = new ObjectGraphNode(owlObject);
            nodes.put(owlObject, result);
        }
        return result;
    }

    public ObjectGraphNode getNode(String name) {
        return nodes.get(IRI.create(name));
    }

    public ObjectGraphNode getNode(OWLObject owlObject) {
        owlObject = toIRIWhenHasIRI(owlObject);
        ObjectGraphNode result = nodes.get(owlObject);
        return result == null ? ObjectGraphNode.empty(owlObject) : result;
    }

    public Stream<ObjectGraphNode> nodes() {
        return this.nodes.values().stream();
    }

    private OWLAxiomVisitor asAxiomVisitor() {
        return new OWLAxiomVisitorAdapter() {

            @Override
            public void visit(OWLClassAssertionAxiom axiom) {
                of(axiom.getIndividual())
                .addType(of(axiom.getClassExpression()));
            }

            @Override
            public void visit(OWLSubClassOfAxiom axiom) {
                of(axiom.getSubClass())
                .addParent(of(axiom.getSuperClass()));
            }

            @Override
            public void visit(OWLEquivalentClassesAxiom axiom) {
                Set<OWLClass> namedClasses = axiom.getNamedClasses();
                if (!namedClasses.isEmpty()) {
                    Iterator<OWLClass> it = namedClasses.iterator();
                    ObjectGraphNode base = of(it.next());
                    while (it.hasNext()) {
                        base.addEquivalent(of(it.next()));
                    }
                }
            }

            @Override
            public void visit(OWLDeclarationAxiom axiom) {
                // a owl:Class; a owl:Datatype etc
                of(axiom.getEntity());
            }
        };
    }

    @Override
    public void initialize(Validation validation) {
        OWLAxiomVisitor visitor = asAxiomVisitor();
        validation
                .getAllOntologies()
                .stream()
                .map(OWLOntology::getAxioms)
                .flatMap(Set::stream)
                .forEach(axiom -> axiom.accept(visitor))
                ;
    }
}
