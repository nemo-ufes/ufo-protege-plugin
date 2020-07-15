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
    private final OWLModelManager modelManager;
    private final Set<OWLOntology> ontologies;
    private final Set<IRI> superClasses = new HashSet<>();
    private final Set<IRI> types = new HashSet<>();
    private boolean isPublicGufoClass = false;
    
    public EntityFilter(OWLModelManager modelManager) {
        this.modelManager = modelManager;
        this.ontologies = modelManager.getActiveOntologies();
    }
    
    private Stream<IRI> getAllEntities() {
        Set<IRI> allEntities = new HashSet<>();
        ontologies.stream()
            .forEach(ontology -> ontology.getSignature().stream()
                .map(entity -> entity.getIRI())
                .forEach(iri -> allEntities.add(iri)));
        
        return allEntities.stream();
    }
    
    public static String showSet(Set<IRI> iris) {
        return "Count: " + iris.size() + System.lineSeparator()
            + iris.stream()
            .map(iri -> iri.getShortForm() + System.lineSeparator())
            .reduce("", (a, b) -> a + b);
    }
    
    public void addSuperClass(IRI superClass) {
        superClasses.add(superClass);
    }
    
    public void addType(IRI type) {
        types.add(type);
    }
    
    public void setIsPublicGufoClass(boolean isPublic) {
        isPublicGufoClass = isPublic;
    }
    
    public Set<IRI> filterEntities() {
        PatternApplier applier = new PatternApplier(modelManager);
        
        Stream<IRI> entities = getAllEntities();
        for(IRI superClass: superClasses) {
            entities = entities.filter(entity -> applier.isInstanceOf(superClass, entity));
        }
        for(IRI type: types) {
            entities = entities.filter(entity -> applier.isInstanceOf(type, entity));
        }
        if(isPublicGufoClass) {
            entities = entities.filter(applier::isPublicGufoClass);
        }
        
        return entities.collect(Collectors.toCollection(HashSet::new));
    }
    
    public List<IRI> listEntities() {
        List<IRI> list = new ArrayList<>();
        list.addAll(filterEntities());
        return list;
    }
    
    /* public static Collection<IRI> getAllClasses() {
        Collection<IRI> allClasses = new HashSet<>();
        ontologies.stream()
            .forEach(ontology -> GufoIris.owlClasses(ontology)
                .map(owlClass -> owlClass.getIRI())
                .forEach(iri -> allClasses.add(iri)));
        
        return allClasses;
    }
    
    public static Collection<IRI> getAllNamedIndividuals() {
        Collection<IRI> allNamedIndividuals = new HashSet<>();
        ontologies.stream()
            .forEach(ontology -> ontology.getIndividualsInSignature().stream()
                .filter(individual -> individual.isNamed())
                .map(individual -> individual.getIRI())
                .forEach(iri -> allNamedIndividuals.add(iri)));
        
        return allNamedIndividuals;
    }
    
    public static Collection<IRI> getPublicEndurantClasses() {
        PatternApplier applier = new PatternApplier(modelManager);
        return GufoIris.publicClasses.stream()
            .filter(iri -> applier.isSubClassOf(GufoIris.Endurant, iri))
            .collect(Collectors.toCollection(ArrayList::new));
    }
    
    public static Collection<IRI> getCategories() {
        PatternApplier applier = new PatternApplier(modelManager);
        IRI categoryIRI = GufoIris.Category;
        
        Collection<IRI> categories = new ArrayList<>();
        ontologies.stream()
            .forEach(ontology -> GufoIris.owlClasses(ontology)
                .map(owlClass -> owlClass.getIRI())
                .filter(iri -> applier.isInstanceOf(categoryIRI, iri))
                .forEach(iri -> categories.add(iri)));
        
        return categories;
    }
    
    public static Collection<IRI> getRigidTypes() {
        PatternApplier applier = new PatternApplier(modelManager);
        IRI rigidTypeIRI = GufoIris.RigidType;
        
        Collection<IRI> rigidTypes = new ArrayList<>();
        ontologies.stream()
            .forEach(ontology -> GufoIris.owlClasses(ontology)
                .map(owlClass -> owlClass.getIRI())
                .filter(iri -> applier.isInstanceOf(rigidTypeIRI, iri))
                .forEach(iri -> rigidTypes.add(iri)));
        
        return rigidTypes;
    } */
}
