/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.helpers;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.RemoveAxiom;

/**
 * 
 * @author jeferson
 */
public final class PatternApplier {
    
    private final OWLModelManager modelManager;
    private final OWLDataFactory dataFactory;
    
    public PatternApplier(OWLModelManager modelManager) {
        this.modelManager = modelManager;
        this.dataFactory = modelManager.getOWLDataFactory();
    }
    
    public void detachSubClass(IRI subClassIRI) {
        OWLOntology ontology = modelManager.getActiveOntology();
        
        OWLClass child = dataFactory.getOWLClass(subClassIRI);
        
        // Delete current subClassOf axioms of child
        ontology.getSubClassAxiomsForSubClass(child).stream()
                .forEach(axiom -> {
                    RemoveAxiom removeAxiom = new RemoveAxiom(ontology, axiom);
                    modelManager.applyChange(removeAxiom);
                });
    }
    
    public void makeSubClassOf(IRI classIRI, IRI subClassIRI) {
        OWLOntology ontology = modelManager.getActiveOntology();
        
        OWLClass parent = dataFactory.getOWLClass(classIRI);
        OWLClass child = dataFactory.getOWLClass(subClassIRI);
        
        // Add desired subClassOf axiom
        OWLAxiom axiom = dataFactory.getOWLSubClassOfAxiom(child, parent);
        AddAxiom addAxiom = new AddAxiom(ontology, axiom);
        modelManager.applyChange(addAxiom);
    }
    
    public boolean isDirectSubClassOf(IRI classIRI, IRI anotherClassIRI) {
        OWLOntology ontology = modelManager.getActiveOntology();
        
        OWLClass child = dataFactory.getOWLClass(anotherClassIRI);
        
        return ontology.getSubClassAxiomsForSubClass(child).stream()
                .anyMatch(axiom -> axiom.getSuperClass()
                        .asOWLClass()
                        .getIRI()
                        .toString()
                        .contentEquals(classIRI.toString()));
    }
    
    public boolean isDirectInstanceOf(IRI classIRI, IRI instanceIRI) {
        OWLOntology ontology = modelManager.getActiveOntology();
        
        OWLNamedIndividual owlInstance = dataFactory.getOWLNamedIndividual(instanceIRI);
        
        return ontology.getClassAssertionAxioms(owlInstance).stream()
                .anyMatch(axiom -> axiom.getClassExpression()
                        .asOWLClass()
                        .getIRI()
                        .toString()
                        .contentEquals(classIRI.toString()));
    }
    
    public void createClass(IRI classIRI) {
        OWLOntology ontology = modelManager.getActiveOntology();
        
        OWLClass newClass = dataFactory.getOWLClass(classIRI);
        
        OWLDeclarationAxiom declarationAxiom = dataFactory.getOWLDeclarationAxiom(newClass);
        AddAxiom addAxiom = new AddAxiom(ontology, declarationAxiom);
        modelManager.applyChange(addAxiom);
    }
    
    public void createNamedIndividual(IRI individualIRI) {
        OWLOntology ontology = modelManager.getActiveOntology();
        
        OWLNamedIndividual newIndividual = dataFactory.getOWLNamedIndividual(individualIRI);
        
        OWLDeclarationAxiom declarationAxiom = dataFactory.getOWLDeclarationAxiom(newIndividual);
        AddAxiom addAxiom = new AddAxiom(ontology, declarationAxiom);
        modelManager.applyChange(addAxiom);
    }
    
    public void makeInstanceOf(IRI classIRI, IRI individualIRI) {
        OWLOntology ontology = modelManager.getActiveOntology();
        
        OWLNamedIndividual individual = dataFactory.getOWLNamedIndividual(individualIRI);
        OWLClass owlClass = dataFactory.getOWLClass(classIRI);
        
        OWLClassAssertionAxiom assertionAxiom = dataFactory.getOWLClassAssertionAxiom(owlClass, individual);
        AddAxiom addAxiom = new AddAxiom(ontology, assertionAxiom);
        modelManager.applyChange(addAxiom);
    }
}
