/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege;

import com.github.jsonldjava.shaded.com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

/**
 *
 * @author luciano
 */
class UFOSuperClassFrameSectionRow extends AbstractOWLFrameSectionRow<OWLClassExpression, OWLSubClassOfAxiom, OWLClass> {

    public UFOSuperClassFrameSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection<OWLClassExpression, OWLSubClassOfAxiom, OWLClass> section, OWLOntology ontology, OWLClassExpression rootObject, OWLSubClassOfAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
    }

    @Override
    protected OWLObjectEditor<OWLClass> getObjectEditor() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected OWLSubClassOfAxiom createAxiom(OWLClass editedObject) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<? extends OWLObject> getManipulatableObjects() {
        return Arrays.asList(getAxiom());
    }
}
