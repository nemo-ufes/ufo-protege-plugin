/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.helpers;

import br.ufes.inf.nemo.ufo.protege.GufoIris;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
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
    
    private Set<OWLClass> getSubClasses(OWLClass superClass) {
        Set<OWLClass> subClasses = new HashSet<>();
        subClasses.add(superClass);
        subClasses = buildSubClassSet(subClasses);
        subClasses.remove(superClass);
        return subClasses;
    }

    private Set<OWLClass> buildSubClassSet(Set<OWLClass> subClasses) {
        Set<OWLClass> newSubClasses = new HashSet<>();

        subClasses.stream()
            .forEach(superClass -> {
                ontologies.stream()
                    .forEach(ontology -> {
                        ontology.getSubClassAxiomsForSuperClass(superClass).stream()
                            .map(axiom -> axiom.getSubClass())
                            .filter(classExpression -> classExpression.isNamed())
                            .map(classExpression -> classExpression.asOWLClass())
                            .forEach(newSubClasses::add);
                    });
            });

        if(newSubClasses.isEmpty()) {
            return subClasses;
        } else {
            subClasses.addAll(buildSuperClassSet(newSubClasses));
            return subClasses;
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
    
    private IRI getOntologicalNature(IRI classIRI) {
        OWLClass owlClass = dataFactory.getOWLClass(classIRI);
        
        return getSuperClasses(owlClass).stream()
                .map(superClass -> superClass.getIRI())
                .filter(superClassIRI -> isSubClassOf(GufoIris.Endurant, superClassIRI))
                .filter(superClassIRI -> isPublicGufoClass(superClassIRI) ||
                                      (hasMultipleOntologicalNature(superClassIRI) &&
                                       isDirectSubClassOf(superClassIRI, classIRI)))
                .findFirst()
                .get();
    }
    
    public Set<IRI> getSuperClassesOfSomeType(IRI type, IRI subClassIRI) {
        OWLClass subClass = dataFactory.getOWLClass(subClassIRI);
        
        Set<IRI> superClasses = getSuperClasses(subClass).stream()
                .map(superClass -> superClass.getIRI())
                .filter(superClassIRI -> isInstanceOf(type, superClassIRI))
                .collect(Collectors.toCollection(HashSet::new));
        
        return superClasses;
    }
    
    public Set<IRI> getSubClassesOfSomeType(IRI type, IRI superClassIRI) {
        OWLClass superClass = dataFactory.getOWLClass(superClassIRI);
        
        Set<IRI> subClasses = getSubClasses(superClass).stream()
                .map(subClass -> subClass.getIRI())
                .filter(subClassIRI -> isInstanceOf(type, subClassIRI))
                .collect(Collectors.toCollection(HashSet::new));
        
        return subClasses;
    }
    
    public IRI getKindOfSortal(IRI classIRI) {
        if(isInstanceOf(GufoIris.Sortal, classIRI)) {
            if(isInstanceOf(GufoIris.Kind, classIRI)) {
                return classIRI;
            } else {
                OWLClass sortal = dataFactory.getOWLClass(classIRI);
                return getSuperClasses(sortal).stream()
                    .filter(OWLClass::isNamed)
                    .map(OWLClass::getIRI)
                    .filter(parent -> isInstanceOf(GufoIris.Kind, parent))
                    .findFirst()
                    .get();
            }
        } 
        
        // If the class isn't a sortal, there is no kind for it
        else {
            return null;
        }
    }
    
    public boolean haveDifferentKinds(IRI classIRI, IRI anotherClassIRI) {
        // If at least one of the classes isn't a sortal, return false
        if(!isInstanceOf(GufoIris.Sortal, classIRI)) {
            return false;
        } else if(!isInstanceOf(GufoIris.Sortal, anotherClassIRI)) {
            return false;
        } else {
            return ! getKindOfSortal(classIRI)
                .toString()
                .contentEquals(
                    getKindOfSortal(anotherClassIRI)
                        .toString()
                );
        }
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
    
    public void createClass(IRI classIRI) {
        OWLOntology ontology = modelManager.getActiveOntology();

        OWLClass newClass = dataFactory.getOWLClass(classIRI);

        OWLAxiom declarationAxiom = dataFactory.getOWLDeclarationAxiom(newClass);
        AddAxiom addAxiom = new AddAxiom(ontology, declarationAxiom);
        modelManager.applyChange(addAxiom);
    }
    
    public void createDisjointUnionOfClasses(IRI unionOfClasses, List<IRI> IRIs) {
        OWLOntology ontology = modelManager.getActiveOntology();
        
        OWLClass newClass = dataFactory.getOWLClass(unionOfClasses);
        
        Set<OWLClass> classSet = IRIs.stream()
            .map(iri -> dataFactory.getOWLClass(iri))
            .collect(Collectors.toCollection(HashSet::new));

        OWLAxiom unionOfAxiom = dataFactory.getOWLDisjointUnionAxiom(newClass, classSet);
        AddAxiom addAxiom = new AddAxiom(ontology, unionOfAxiom);
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

    public void assertObjectProperty(IRI propertyIRI, IRI subjectIRI, IRI objectIRI) {
        OWLOntology ontology = modelManager.getActiveOntology();

        OWLObjectProperty property = dataFactory.getOWLObjectProperty(propertyIRI);
        OWLNamedIndividual subject = dataFactory.getOWLNamedIndividual(subjectIRI);
        OWLNamedIndividual object = dataFactory.getOWLNamedIndividual(objectIRI);

        OWLAxiom assertionAxiom =
            dataFactory.getOWLObjectPropertyAssertionAxiom(property, subject, object);
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
    
    public void createDataProperty(IRI propertyIRI) {
        OWLOntology ontology = modelManager.getActiveOntology();

        OWLDataProperty newProperty = dataFactory.getOWLDataProperty(propertyIRI);

        OWLAxiom declarationAxiom = dataFactory.getOWLDeclarationAxiom(newProperty);
        AddAxiom addAxiom = new AddAxiom(ontology, declarationAxiom);
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

    public void createSubObjectProperty(IRI propertyIRI, IRI subPropertyIRI) {
        OWLOntology ontology = modelManager.getActiveOntology();

        OWLObjectProperty property = dataFactory.getOWLObjectProperty(propertyIRI);
        OWLObjectProperty subProperty = dataFactory.getOWLObjectProperty(subPropertyIRI);

        OWLAxiom assertionAxiom = dataFactory.getOWLSubObjectPropertyOfAxiom(subProperty, property);
        AddAxiom addAxiom = new AddAxiom(ontology, assertionAxiom);
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
    
    public IRI getObjectPropertyDomain(IRI propertyIRI) {
        OWLOntology ontology = modelManager.getActiveOntology();

        OWLObjectProperty property = dataFactory.getOWLObjectProperty(propertyIRI);
        
        return ontology.getObjectPropertyDomainAxioms(property).stream()
                .map(axiom -> axiom.getDomain().asOWLClass().getIRI())
                .findFirst()
                .get();
    }
    
    public IRI getObjectPropertyRange(IRI propertyIRI) {
        OWLOntology ontology = modelManager.getActiveOntology();

        OWLObjectProperty property = dataFactory.getOWLObjectProperty(propertyIRI);
        
        return ontology.getObjectPropertyRangeAxioms(property).stream()
                .map(axiom -> axiom.getRange().asOWLClass().getIRI())
                .findFirst()
                .get();
    }
    
    public boolean isPublicGufoClass(IRI classIRI) {
        return ! classIRI.toString()
                .contentEquals(GufoIris.Object.toString())
        && GufoIris.publicClasses.contains(classIRI);
    }
    
    public boolean hasMultipleOntologicalNature(IRI classIRI) {
        OWLOntology ontology = modelManager.getActiveOntology();
        
        OWLClass owlClass = dataFactory.getOWLClass(classIRI);
        
        return ontology.getDisjointUnionAxioms(owlClass).stream()
                .anyMatch(axiom -> axiom.getClassExpressions().stream()
                    .map(exp -> exp.asOWLClass().getIRI())
                    .allMatch(iri -> isPublicGufoClass(iri)));
    } 

    public boolean isDirectSubClassOf(IRI classIRI, IRI subClassIRI) {
        OWLOntology ontology = modelManager.getActiveOntology();

        OWLClass child = dataFactory.getOWLClass(subClassIRI);

        return ontology.getSubClassAxiomsForSubClass(child).stream()
                .map(axiom -> axiom.getSuperClass())
                .filter(classExpression -> classExpression.isNamed())
                .anyMatch(classExpression -> classExpression.asOWLClass()
                    .getIRI()
                    .toString()
                    .contentEquals(classIRI.toString()));
    }

    public boolean isSubClassOf(IRI classIRI, IRI subClassIRI) {
        OWLClass superClass = dataFactory.getOWLClass(classIRI);
        OWLClass subClass = dataFactory.getOWLClass(subClassIRI);

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

    public boolean isSubObjectPropertyOf(IRI propertyIRI, IRI subPropertyIRI) {
        OWLOntology ontology = modelManager.getActiveOntology();

        OWLObjectProperty subProperty = dataFactory.getOWLObjectProperty(subPropertyIRI);

        return ontology.getObjectSubPropertyAxiomsForSubProperty(subProperty).stream()
                .anyMatch(axiom -> axiom.getSuperProperty()
                    .asOWLObjectProperty()
                    .getIRI()
                    .toString()
                    .contentEquals(propertyIRI.toString()));
    }
    
    public boolean isSubDataPropertyOf(IRI propertyIRI, IRI subPropertyIRI) {
        OWLOntology ontology = modelManager.getActiveOntology();

        OWLDataProperty subProperty = dataFactory.getOWLDataProperty(subPropertyIRI);

        return ontology.getDataSubPropertyAxiomsForSubProperty(subProperty).stream()
                .anyMatch(axiom -> axiom.getSuperProperty()
                    .asOWLDataProperty()
                    .getIRI()
                    .toString()
                    .contentEquals(propertyIRI.toString()));
    }
    
    public boolean isOfOntologicalNatureOf(IRI classIRI, IRI subClassIRI) {
        IRI endurantIRI = getOntologicalNature(classIRI);
        IRI endurantIRIOfSubClass = getOntologicalNature(subClassIRI);
        
        OWLOntology ontology = modelManager.getActiveOntology();
        
        if(hasMultipleOntologicalNature(endurantIRI)) {
            List<IRI> subClassOntologicalNature = new ArrayList<>();
            
            if(hasMultipleOntologicalNature(endurantIRIOfSubClass)) {
                OWLClass endurantClassOfSubClass = dataFactory.getOWLClass(endurantIRIOfSubClass);
                
                ontology.getDisjointUnionAxioms(endurantClassOfSubClass).stream()
                        .limit(1)
                        .forEach(axiom -> axiom.getClassExpressions().stream()
                            .map(exp -> exp.asOWLClass().getIRI())
                            .forEach(iri -> subClassOntologicalNature.add(iri)));
            } else {
                subClassOntologicalNature.add(endurantIRIOfSubClass);
            }
        
            OWLClass endurantClass = dataFactory.getOWLClass(endurantIRI);
            OWLDisjointUnionAxiom axiom = ontology.getDisjointUnionAxioms(endurantClass).stream()
                    .limit(1)
                    .findFirst()
                    .get();
            
            return subClassOntologicalNature.stream()
                    .allMatch(subIri -> axiom.getClassExpressions().stream()
                        .map(exp -> exp.asOWLClass().getIRI())
                        .anyMatch(iri -> iri.toString()
                            .contentEquals(subIri.toString())));
        } else {
            return endurantIRI.toString()
                .contentEquals(endurantIRIOfSubClass.toString());
        }
    }
    
    /* public boolean hasSharedSuperClasses(IRI classIRI, IRI subClassIRI) {
        OWLOntology ontology = modelManager.getActiveOntology();

        OWLClass parent = dataFactory.getOWLClass(classIRI);
        OWLClass child = dataFactory.getOWLClass(subClassIRI);

        return getSuperClasses(parent).stream()
            .map(owlClass -> owlClass.getIRI())
            .anyMatch(superClassIRI -> {
                return ontology.getSubClassAxiomsForSubClass(child).stream()
                    .filter(axiom -> axiom.getSuperClass().isNamed())
                    .anyMatch(axiom ->
                            axiom.getSuperClass().asOWLClass()
                                .getIRI()
                                .toString()
                                .contentEquals(superClassIRI.toString()));
            });
    } */
    
    public boolean isDifferentFrom(IRI classIRI, IRI anotherClassIRI) {
        return ! classIRI.toString()
            .contentEquals(anotherClassIRI.toString());
    }
}
