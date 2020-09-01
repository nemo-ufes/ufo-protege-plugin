/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.helpers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
    
    public static String showList(List<IRI> iris) {
        return "Count: " + iris.size() + System.lineSeparator()
            + iris.stream()
            .map(iri -> iri.getShortForm() + System.lineSeparator())
            .reduce("", (a, b) -> a + b);
    }
    
    public EntityFilter addSuperClass(IRI superClass) {
        entities = entities.filter(entity -> applier.isSubClassOf(superClass, entity));
        return this;
    }
    
    public EntityFilter addType(IRI type) {
        entities = entities.filter(entity -> applier.isInstanceOf(type, entity));
        return this;
    }
    
    public EntityFilter addSuperDataProperty(IRI superDataProperty) {
        entities = entities.filter(entity -> applier.isSubDataPropertyOf(superDataProperty, entity));
        return this;
    }
    
    public EntityFilter isPublicGufoClass() {
        entities = entities.filter(applier::isPublicGufoClass);
        return this;
    }
    
    public EntityFilter hasSharedSuperClasses(IRI classIRI) {
        if(classIRI != null) {
            entities = entities.filter(entity -> applier.hasSharedSuperClasses(classIRI, entity));
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
        entities = entities.filter(entity -> ! applier.isSubClassOf(entity, classIRI));
        return this;
    }
    
    public List<IRI> entities() {
        return entities
                .sorted((IRI iri1, IRI iri2) -> iri1.getShortForm()
                        .compareToIgnoreCase(iri2.getShortForm()))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
