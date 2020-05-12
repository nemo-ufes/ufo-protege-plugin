/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.helpers;

import br.ufes.inf.nemo.ufo.protege.GufoIris;
import java.util.HashSet;
import java.util.Set;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.RemoveAxiom;

/**
 * 
 * @author jeferson
 */
public final class PatternApplier {
    
    private final OWLModelManager modelManager;
    private final OWLDataFactory dataFactory;
    private final Set<OWLOntology> ontologies;
    
    public PatternApplier(OWLModelManager modelManager) {
        this.modelManager = modelManager;
        this.dataFactory = modelManager.getOWLDataFactory();
        this.ontologies = modelManager.getActiveOntologies();
    }
    
    private Set<OWLClass> getSuperClasses(OWLClass subClass) {
        Set<OWLClass> superClasses = new HashSet<>();
        superClasses.add(subClass);
        superClasses = buildSuperClassSet(superClasses);
        superClasses.remove(subClass);
        return superClasses;
    }
    
    private Set<OWLClass> buildSuperClassSet(Set<OWLClass> superClasses) {
        Set<OWLClass> newSuperClasses = new HashSet<>();
        
        superClasses.stream()
            .forEach(subClass -> {
                ontologies.stream()
                    .forEach(ontology -> {
                        ontology.getSubClassAxiomsForSubClass(subClass).stream()
                            .map(axiom -> axiom.getSuperClass())
                            .filter(classExpression -> classExpression.isNamed())
                            .map(classExpression -> classExpression.asOWLClass())
                            .forEach(newSuperClasses::add);
                    });
            });
        
        if(newSuperClasses.isEmpty()) {
            return superClasses;
        } else {
            superClasses.addAll(buildSuperClassSet(newSuperClasses));
            return superClasses;
        }
    }
    
    private Set<OWLClass> getTypes(OWLNamedIndividual individual) {
        Set<OWLClass> types = new HashSet<>();
        
        ontologies.stream()
                .forEach(ontology -> {
                    ontology.getClassAssertionAxioms(individual).stream()
                            .map(axiom -> axiom.getClassExpression().asOWLClass())
                            .forEach(types::add);
                });
        
        return buildSuperClassSet(types);
    }
    
    public boolean isPublicGufoClass(IRI classIRI) {
        return GufoIris.publicClasses.contains(classIRI);
    }
    
    public void makeSubClassOf(IRI classIRI, IRI subClassIRI) {
        OWLOntology ontology = modelManager.getActiveOntology();
        
        OWLClass parent = dataFactory.getOWLClass(classIRI);
        OWLClass child = dataFactory.getOWLClass(subClassIRI);
        
        // Delete subClass axioms of child that are found (even indirectly) in parent
        getSuperClasses(parent).stream()
            .map(owlClass -> owlClass.getIRI())
            .forEach(superClassIRI -> {
                ontology.getSubClassAxiomsForSubClass(child).stream()
                    .filter(axiom -> axiom.getSuperClass().isNamed())
                    .filter(axiom ->
                            axiom.getSuperClass().asOWLClass()
                                .getIRI()
                                .toString()
                                .contentEquals(superClassIRI.toString()))
                    .forEach(axiom -> {
                        RemoveAxiom removeAxiom = new RemoveAxiom(ontology, axiom);
                        modelManager.applyChange(removeAxiom);
                    });
            });
        
        addSubClassTo(classIRI, subClassIRI);
    }
    
    public void addSubClassTo(IRI classIRI, IRI subClassIRI) {
        OWLOntology ontology = modelManager.getActiveOntology();
        
        OWLClass parent = dataFactory.getOWLClass(classIRI);
        OWLClass child = dataFactory.getOWLClass(subClassIRI);
        
        // Add desired subClassOf axiom
        OWLAxiom axiom = dataFactory.getOWLSubClassOfAxiom(child, parent);
        AddAxiom addAxiom = new AddAxiom(ontology, axiom);
        modelManager.applyChange(addAxiom);
    }
    
    public boolean isDirectSubClassOf(IRI classIRI, IRI anotherClassIRI) {
        OWLClass child = dataFactory.getOWLClass(anotherClassIRI);
        
        for(OWLOntology ontology : ontologies) {
            for(OWLSubClassOfAxiom axiom : ontology.getSubClassAxiomsForSubClass(child)) {
                OWLClassExpression classExpression = axiom.getSuperClass();
                if(classExpression.isNamed()) {
                    boolean test = classExpression.asOWLClass()
                        .getIRI()
                        .toString()
                        .contentEquals(classIRI.toString());
                    if(test) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public boolean isSubClassOf(IRI classIRI, IRI anotherClassIRI) {
        OWLClass superClass = dataFactory.getOWLClass(classIRI);
        OWLClass subClass = dataFactory.getOWLClass(anotherClassIRI);
        
        return getSuperClasses(subClass).contains(superClass);
    }
    
    public boolean isDirectInstanceOf(IRI classIRI, IRI instanceIRI) {
        OWLNamedIndividual owlInstance = dataFactory.getOWLNamedIndividual(instanceIRI);
        
        return ontologies.stream()
                .anyMatch(ontology -> ontology.getClassAssertionAxioms(owlInstance).stream()
                        .anyMatch(axiom -> axiom.getClassExpression()
                        .asOWLClass()
                        .getIRI()
                        .toString()
                        .contentEquals(classIRI.toString())));
    }
    
    public boolean isInstanceOf(IRI classIRI, IRI instanceIRI) {
        OWLClass type = dataFactory.getOWLClass(classIRI);
        OWLNamedIndividual individual = dataFactory.getOWLNamedIndividual(instanceIRI);
        
        return getTypes(individual).contains(type);
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
