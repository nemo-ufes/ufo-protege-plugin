/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.AbstractOWLFrame;
import org.semanticweb.owlapi.model.OWLClassExpression;

/**
 *
 * @author luciano
 */
public class UFOClassFrame extends AbstractOWLFrame<OWLClassExpression> {

    public UFOClassFrame(OWLEditorKit editorKit) {
        super(editorKit.getModelManager().getOWLOntologyManager());
        addSection(new UFOSuperClassesFrameSection(editorKit, this));
    }
}
