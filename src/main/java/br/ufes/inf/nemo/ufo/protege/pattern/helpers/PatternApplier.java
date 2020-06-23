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
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
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
    
    public Set<OWLSubClassOfAxiom> sharedSuperClassAxioms(IRI classIRI, IRI subClassIRI) {
        OWLOntology ontology = modelManager.getActiveOntology();
        
        OWLClass parent = dataFactory.getOWLClass(classIRI);
        OWLClass child = dataFactory.getOWLClass(subClassIRI);
        
        // Get subClass axioms of child that are found (even indirectly) in parent
        Set<OWLSubClassOfAxiom> shared = new HashSet<>();
        
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
                    .forEach(shared::add);
            });
        
        return shared;
    }
    
    public void makeSubClassOf(IRI classIRI, IRI subClassIRI,
                               Set<OWLSubClassOfAxiom> toBeRemoved) {
        OWLOntology ontology = modelManager.getActiveOntology();
        
        toBeRemoved.forEach(axiom -> {
            RemoveAxiom removeAxiom = new RemoveAxiom(ontology, axiom);
            modelManager.applyChange(removeAxiom);
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
        OWLOntology ontology = modelManager.getActiveOntology();
        
        OWLClass child = dataFactory.getOWLClass(anotherClassIRI);
        
        return ontology.getSubClassAxiomsForSubClass(child).stream()
                .map(axiom -> axiom.getSuperClass())
                .filter(classExpression -> classExpression.isNamed())
                .map(classExpression -> classExpression.asOWLClass()
                    .getIRI()
                    .toString()
                    .contentEquals(classIRI.toString()))
                .anyMatch(test -> true);
    }
    
    public boolean isSubClassOf(IRI classIRI, IRI anotherClassIRI) {
        OWLClass superClass = dataFactory.getOWLClass(classIRI);
        OWLClass subClass = dataFactory.getOWLClass(anotherClassIRI);
        
        return getSuperClasses(subClass).contains(superClass);
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
    
    public boolean isInstanceOf(IRI classIRI, IRI instanceIRI) {
        OWLClass type = dataFactory.getOWLClass(classIRI);
        OWLNamedIndividual individual = dataFactory.getOWLNamedIndividual(instanceIRI);
        
        return getTypes(individual).contains(type);
    }
    
    public void createClass(IRI classIRI) {
        OWLOntology ontology = modelManager.getActiveOntology();
        
        OWLClass newClass = dataFactory.getOWLClass(classIRI);
        
        OWLAxiom declarationAxiom = dataFactory.getOWLDeclarationAxiom(newClass);
        AddAxiom addAxiom = new AddAxiom(ontology, declarationAxiom);
        modelManager.applyChange(addAxiom);
    }
    
    public void createNamedIndividual(IRI individualIRI) {
        OWLOntology ontology = modelManager.getActiveOntology();
        
        OWLNamedIndividual newIndividual = dataFactory.getOWLNamedIndividual(individualIRI);
        
        OWLAxiom declarationAxiom = dataFactory.getOWLDeclarationAxiom(newIndividual);
        AddAxiom addAxiom = new AddAxiom(ontology, declarationAxiom);
        modelManager.applyChange(addAxiom);
    }
    
    public void makeInstanceOf(IRI classIRI, IRI individualIRI) {
        OWLOntology ontology = modelManager.getActiveOntology();
        
        OWLNamedIndividual individual = dataFactory.getOWLNamedIndividual(individualIRI);
        OWLClass owlClass = dataFactory.getOWLClass(classIRI);
        
        OWLAxiom assertionAxiom = dataFactory.getOWLClassAssertionAxiom(owlClass, individual);
        AddAxiom addAxiom = new AddAxiom(ontology, assertionAxiom);
        modelManager.applyChange(addAxiom);
    }
    
    public void createRelation(IRI propertyIRI, IRI subjectIRI, IRI objectIRI) {
        OWLOntology ontology = modelManager.getActiveOntology();
        
        OWLObjectProperty relation = dataFactory.getOWLObjectProperty(propertyIRI);
        OWLNamedIndividual subject = dataFactory.getOWLNamedIndividual(subjectIRI);
        OWLNamedIndividual object = dataFactory.getOWLNamedIndividual(objectIRI);
        
        OWLAxiom assertionAxiom = 
            dataFactory.getOWLObjectPropertyAssertionAxiom(relation, subject, object);
        AddAxiom addAxiom = new AddAxiom(ontology, assertionAxiom);
        modelManager.applyChange(addAxiom);
    }
    
    public void assertDataProperty(IRI propertyIRI, IRI instIRI, String value) {
        OWLOntology ontology = modelManager.getActiveOntology();
        
        OWLDataProperty property = dataFactory.getOWLDataProperty(propertyIRI);
        OWLNamedIndividual inst = dataFactory.getOWLNamedIndividual(instIRI);
        
        OWLAxiom assertionAxiom = 
            dataFactory.getOWLDataPropertyAssertionAxiom(property, inst, value);
        AddAxiom addAxiom = new AddAxiom(ontology, assertionAxiom);
        modelManager.applyChange(addAxiom);
    }
    
    public void createSubDataProperty(IRI propertyIRI, IRI subPropertyIRI) {
        OWLOntology ontology = modelManager.getActiveOntology();
        
        OWLDataProperty property = dataFactory.getOWLDataProperty(propertyIRI);
        OWLDataProperty subProperty = dataFactory.getOWLDataProperty(subPropertyIRI);
        
        OWLAxiom assertionAxiom = dataFactory.getOWLSubDataPropertyOfAxiom(subProperty, property);
        AddAxiom addAxiom = new AddAxiom(ontology, assertionAxiom);
        modelManager.applyChange(addAxiom);
    }
    
    public boolean isSubDataPropertyOf(IRI propertyIRI, IRI subPropertyIRI) {
        OWLOntology ontology = modelManager.getActiveOntology();
        
        OWLDataProperty property = dataFactory.getOWLDataProperty(propertyIRI);
        OWLDataProperty subProperty = dataFactory.getOWLDataProperty(subPropertyIRI);
        
        return ontology.getDataSubPropertyAxiomsForSubProperty(subProperty).stream()
                .anyMatch(axiom -> axiom.getSuperProperty()
                    .asOWLDataProperty()
                    .getIRI()
                    .toString()
                    .contentEquals(propertyIRI.toString()));
    }
    
    public void setDataPropertyDomain(IRI propertyIRI, IRI domainIRI) {
        OWLOntology ontology = modelManager.getActiveOntology();
        
        OWLDataProperty property = dataFactory.getOWLDataProperty(propertyIRI);
        OWLClass domain = dataFactory.getOWLClass(domainIRI);
        
        OWLAxiom domainAxiom = dataFactory.getOWLDataPropertyDomainAxiom(property, domain);
        AddAxiom addAxiom = new AddAxiom(ontology, domainAxiom);
        modelManager.applyChange(addAxiom);
    }
    
    public void createObjectProperty(IRI propertyIRI) {
        OWLOntology ontology = modelManager.getActiveOntology();
        
        OWLObjectProperty newProperty = dataFactory.getOWLObjectProperty(propertyIRI);
        
        OWLAxiom declarationAxiom = dataFactory.getOWLDeclarationAxiom(newProperty);
        AddAxiom addAxiom = new AddAxiom(ontology, declarationAxiom);
        modelManager.applyChange(addAxiom);
    }
    
    public void setObjectPropertyDomain(IRI propertyIRI, IRI domainIRI) {
        OWLOntology ontology = modelManager.getActiveOntology();
        
        OWLObjectProperty property = dataFactory.getOWLObjectProperty(propertyIRI);
        OWLClass domain = dataFactory.getOWLClass(domainIRI);
        
        OWLAxiom domainAxiom = dataFactory.getOWLObjectPropertyDomainAxiom(property, domain);
        AddAxiom addAxiom = new AddAxiom(ontology, domainAxiom);
        modelManager.applyChange(addAxiom);
    }
    
    public void setObjectPropertyRange(IRI propertyIRI, IRI rangeIRI) {
        OWLOntology ontology = modelManager.getActiveOntology();
        
        OWLObjectProperty property = dataFactory.getOWLObjectProperty(propertyIRI);
        OWLClass range = dataFactory.getOWLClass(rangeIRI);
        
        OWLAxiom rangeAxiom = dataFactory.getOWLObjectPropertyRangeAxiom(property, range);
        AddAxiom addAxiom = new AddAxiom(ontology, rangeAxiom);
        modelManager.applyChange(addAxiom);
    }
}
