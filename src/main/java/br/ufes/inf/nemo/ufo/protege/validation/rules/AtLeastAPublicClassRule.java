/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.validation.rules;

import br.ufes.inf.nemo.ufo.protege.UFOConfig;
import br.ufes.inf.nemo.ufo.protege.validation.Rule;
import br.ufes.inf.nemo.ufo.protege.validation.Rule.Violation;
import br.ufes.inf.nemo.ufo.protege.validation.RuleInfo;
import br.ufes.inf.nemo.ufo.protege.validation.Validation;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.util.OWLAxiomVisitorAdapter;

/**
 *
 * @author luciano
 */
@RuleInfo(
        label="Missing public UFO supertype"
)
public class AtLeastAPublicClassRule extends Rule {

    @Override
    public Set<Violation> validate(Validation context) {
        ValidationRule rule = new ValidationRule();
        return rule.validate(context.getOWLOntology());
    }   
    
    class ValidationRule extends OWLAxiomVisitorAdapter {

        // Reference to UFOConfig object
        private final UFOConfig ufo = getUFOConfig();

        // Set to hold all declared classes in ontology
        final Set<OWLClass> declaredClasses = new HashSet<>();
        // Set to hold classes directly subclassing an UFO public class
        final Set<OWLClass> ufoDirectSubclasses = new HashSet<>();
        // Set to hold classes indirectly subclassing an UFO public class
        final Set<OWLClass> ufoIndirectSubclasses = new HashSet<>();
        // Multimap to hold mapping from superclasses to subclasses
        //   Actually the multimap shall hold links between equivalent classes
        //   too
        final SetMultimap<OWLClass, OWLClass> subClasses = HashMultimap.create();
        // Set of classes which have already been checked for UFO inheritance
        final Set<OWLClass> checked = new HashSet<>();

        private void linkClasses(
                OWLClassExpression superClass,
                OWLClassExpression subClass) {
            if (superClass.isAnonymous()) {
                return;
            }
            if (subClass.isAnonymous()) {
                return;
            }
            if (superClass.equals(subClass)) {
                return;
            }
            final OWLClass owlSuperClass = superClass.asOWLClass();
            final OWLClass owlSubClass = subClass.asOWLClass();
            if (ufo.isPublicUFOClass(owlSuperClass)) {
                ufoDirectSubclasses.add(owlSubClass);
            } else if (!ufo.isPublicUFOClass(owlSubClass)) {
                subClasses.put(owlSuperClass, owlSubClass);
            }
        }

        @Override
        public void visit(OWLSubClassOfAxiom axiom) {
            linkClasses(axiom.getSuperClass(), axiom.getSubClass());
        }

        @Override
        public void visit(OWLEquivalentClassesAxiom axiom) {
            Set<OWLClass> namedClasses = axiom.getNamedClasses();
            for (OWLClass fromClass : namedClasses) {
                for (OWLClass toClass : namedClasses) {
                    linkClasses(fromClass, toClass);
                }
            }
        }

        @Override
        public void visit(OWLDeclarationAxiom axiom) {
            OWLEntity entity = axiom.getEntity();
            if (entity.isOWLClass()) {
                declaredClasses.add(entity.asOWLClass());
            }
        }

        private void checkSubclasses(OWLClass owlClass) {
            if (!checked.add(owlClass)) {
                return;
            }
            if (!ufoDirectSubclasses.contains(owlClass)) {
                ufoIndirectSubclasses.add(owlClass);
            }
            for (OWLClass subclass : subClasses.get(owlClass)) {
                this.checkSubclasses(subclass);
            }
        }

        public Set<Violation> validate(OWLOntology ontology) {
            // Process axioms, putting references in the sets declared above
            Set<OWLAxiom> axioms = ontology.getAxioms(Imports.EXCLUDED);
            for (OWLAxiom axiom : axioms) {
                axiom.accept(this);
            }
            for (OWLClass ufoDirectSubclass : ufoDirectSubclasses) {
                checkSubclasses(ufoDirectSubclass);
            }

            Set<OWLClass> violatingClasses = new HashSet<>(declaredClasses);
            violatingClasses.removeAll(ufoDirectSubclasses);
            violatingClasses.removeAll(ufoIndirectSubclasses);

            return violatingClasses
                    .stream()
                    .map(AtLeastAPublicClassRule.this::newViolation)
                    .collect(Collectors.toCollection(HashSet::new));                    
        }
    }
}
