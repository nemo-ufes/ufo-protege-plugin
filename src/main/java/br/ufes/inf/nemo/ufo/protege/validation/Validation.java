/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.validation;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * This class represents a validation (being) applied to an ontology.
 * <p>
 * 
 * @author luciano
 */
public class Validation {
    
    private final OWLOntology ontology;
    private final Set<Rule.Violation> violations = new HashSet<>();
    // Set to hold all declared classes in ontology
    final Set<OWLClass> declaredClasses = new HashSet<>();


    public Validation(OWLOntology ontology) {
        this.ontology = ontology;
    }
    
    public OWLOntology getOWLOntology() {
        return ontology;
    }
    
    public void initialize() {
        
    }
    
    public Stream<OWLClass> allParents(OWLClass owlClass) {
        return declaredClasses.stream();
    }
    
    // Contexts
    public Stream<OWLClass> objects() {
        return declaredClasses.stream();
    }
    
    public Stream<OWLClass> sortals() {
        return declaredClasses.stream();
    }
    
    public Stream<OWLClass> substanceSortals() {
        return declaredClasses.stream();
    }
    
    public Stream<OWLClass> rigidSortals() {
        return declaredClasses.stream();
    }
    
    public Stream<OWLClass> mixins() {
        return declaredClasses.stream();
    }
    
    public Stream<OWLClass> categories() {
        return declaredClasses.stream();
    }
}
