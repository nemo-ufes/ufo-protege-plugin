/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege;

import java.util.Comparator;
import java.util.List;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.model.parameters.Navigation;

/**
 *
 * @author luciano
 */
public class UFOSuperClassesFrameSection extends
        AbstractOWLFrameSection<OWLClassExpression, OWLSubClassOfAxiom, OWLClass>{

    public UFOSuperClassesFrameSection(OWLEditorKit editorKit, UFOClassFrame frame) {
        super(editorKit, "UFO Super Classes", frame);
    }

    @Override
    protected OWLSubClassOfAxiom createAxiom(OWLClass object) {
        //To change body of generated methods, choose Tools | Templates.
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public OWLObjectEditor<OWLClass> getObjectEditor() {
        //To change body of generated methods, choose Tools | Templates.
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void refill(OWLOntology ontology) {

        UFOConfig ufo = UFOConfig.get(getOWLModelManager());
        OWLClassExpression owlClass = getRootObject();
        ontology.getAxioms(OWLSubClassOfAxiom.class,owlClass,
                Imports.INCLUDED, Navigation.IN_SUB_POSITION)
                .stream()
                .filter(a -> a.getSubClass().equals(owlClass))
                .filter(a -> ufo.isPublicUFOClass(a.getSuperClass()))
                .forEach((axiom) -> {
                    addRow(new UFOSuperClassFrameSectionRow(getOWLEditorKit(), this, ontology, owlClass, axiom));
                });
    }

    @Override
    protected void clear() {

    }

    @Override
    public Comparator<OWLFrameSectionRow<OWLClassExpression, OWLSubClassOfAxiom, OWLClass>> getRowComparator() {
        return (a,b) -> -1;
    }

}
