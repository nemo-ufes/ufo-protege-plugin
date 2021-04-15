/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.helpers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 *
 * @author jeferson
 */
public class EntityFilter {
    private final Set<OWLOntology> ontologies;
    private final PatternApplier applier;
    private Stream<IRI> entities;
    private final List<IRI> list = new ArrayList<>();
    
    public EntityFilter(OWLModelManager modelManager) {
        this.ontologies = modelManager.getActiveOntologies();
        this.applier = new PatternApplier(modelManager);
        this.entities = getAllEntities();
    }
    
    private Stream<IRI> getAllEntities() {
        Set<IRI> allEntities = new HashSet<>();
        ontologies.stream()
            .forEach(ontology -> ontology.getSignature().stream()
                .map(entity -> entity.getIRI())
                .forEach(iri -> allEntities.add(iri)));
        
        return allEntities.stream();
    }
    
    private void moveEntitiesToOutputList() {
        // Move each filtered entity to output list
        entities.forEach(iri -> list.add(iri));
        
        // Remove duplicates from output list
        Set<IRI> set = new HashSet<>(list);
        list.clear();
        list.addAll(set);
    }
    
    public static String outputListAsString(List<IRI> iris) {
        return "Count: " + iris.size() + System.lineSeparator()
            + iris.stream()
            .map(iri -> iri.getShortForm() + System.lineSeparator())
            .reduce("", (a, b) -> a + b);
    }
    
    public EntityFilter hasSuperClass(IRI superClass) {
        entities = entities.filter(entity -> applier.isSubClassOf(superClass, entity));
        return this;
    }
    
    public EntityFilter isOfType(IRI type) {
        entities = entities.filter(entity -> applier.isInstanceOf(type, entity));
        return this;
    }
    
    public EntityFilter isNotOfType(IRI type) {
        entities = entities.filter(entity -> ! applier.isInstanceOf(type, entity));
        return this;
    }
    
    public EntityFilter hasSuperObjectProperty(IRI superObjectProperty) {
        entities = entities.filter(entity -> applier.isSubObjectPropertyOf(superObjectProperty, entity));
        return this;
    }
    
    public EntityFilter hasSuperDataProperty(IRI superDataProperty) {
        entities = entities.filter(entity -> applier.isSubDataPropertyOf(superDataProperty, entity));
        return this;
    }
    
    public EntityFilter isPublicGufoClass() {
        entities = entities.filter(applier::isPublicGufoClass);
        return this;
    }
    
    public EntityFilter isOfOntologicalNatureOf(IRI classIRI) {
        if(classIRI != null) {
            entities = entities.filter(entity -> applier.isOfOntologicalNatureOf(classIRI, entity));
        } else {
            entities = entities.limit(0);
        }
        return this;
    }
    
    public EntityFilter hasOntologicalNatureOf(IRI classIRI) {
        if(classIRI != null) {
            entities = entities.filter(entity -> applier.isOfOntologicalNatureOf(entity, classIRI));
        } else {
            entities = entities.limit(0);
        }
        return this;
    }
    
    public EntityFilter isDifferentFrom(IRI classIRI) {
        if(classIRI != null) {
            entities = entities.filter(entity -> applier.isDifferentFrom(classIRI, entity));
        }
        return this;
    }
    
    public EntityFilter isNotSuperClassOf(IRI classIRI) {
        if(classIRI != null) {
            entities = entities.filter(entity -> ! applier.isSubClassOf(entity, classIRI));
        } else {
            entities = entities.limit(0);
        }
        return this;
    }
    
    public EntityFilter isNotSubClassOf(IRI classIRI) {
        if(classIRI != null) {
            entities = entities.filter(entity -> ! applier.isSubClassOf(classIRI, entity));
        } else {
            entities = entities.limit(0);
        }
        return this;
    }
    
    public EntityFilter isNotDirectSubClassOf(IRI classIRI) {
        if(classIRI != null) {
            entities = entities.filter(entity -> ! applier.isDirectSubClassOf(classIRI, entity));
        } else {
            entities = entities.limit(0);
        }
        return this;
    }
    
    public EntityFilter isNotDirectSuperClassOf(IRI classIRI) {
        if(classIRI != null) {
            entities = entities.filter(entity -> ! applier.isDirectSubClassOf(entity, classIRI));
        } else {
            entities = entities.limit(0);
        }
        return this;
    }
    
    public EntityFilter isNotSuperClassByType(IRI classIRI, IRI type) {
        if(classIRI != null) {
            entities = entities.filter(entity -> 
                applier.getSuperClassesOfSomeType(type, classIRI).stream()
                    .allMatch(superClass -> ! applier.isSubClassOf(entity, superClass)));
        } else {
            entities = entities.limit(0);
        }
        return this;
    }
    
    public EntityFilter isNotSubClassByType(IRI classIRI, IRI type) {
        if(classIRI != null) {
            entities = entities.filter(entity -> 
                applier.getSubClassesOfSomeType(type, classIRI).stream()
                    .allMatch(subClass -> ! applier.isSubClassOf(subClass, entity)));
        } else {
            entities = entities.limit(0);
        }
        return this;
    }
    
    public EntityFilter hasDifferentKindOf(IRI classIRI) {
        if(classIRI != null) {
            entities = entities.filter(entity -> applier.haveDifferentKinds(classIRI, entity));
        } else {
            entities = entities.limit(0);
        }
        return this;
    }
    
    public EntityFilter hasSameKindOf(IRI classIRI) {
        if(classIRI != null) {
            entities = entities.filter(entity -> ! applier.haveDifferentKinds(classIRI, entity));
        } else {
            entities = entities.limit(0);
        }
        return this;
    }
    
    public EntityFilter unionWith() {
        moveEntitiesToOutputList();
        
        // Restart the filtering
        entities = getAllEntities();
        return this;
    }
    
    public List<IRI> entities() {
        moveEntitiesToOutputList();
        
        // Sort output list
        list.sort(Comparator.comparing(
                IRI::getShortForm, (a, b) -> a.compareToIgnoreCase(b)));
        
        return list;
    }
}
