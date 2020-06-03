/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege;

import br.ufes.inf.nemo.protege.annotations.EditorKitMenuAction;
import java.awt.event.ActionEvent;
import java.util.Set;
import javax.swing.JOptionPane;
import org.protege.editor.owl.model.inference.OWLReasonerManager;
import org.protege.editor.owl.model.inference.ReasonerStatus;
import org.protege.editor.owl.model.inference.ReasonerUtilities;
import org.protege.editor.owl.ui.action.ProtegeOWLAction;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

/**
 *
 * @author jeferson
 */

public class SubClasses extends ProtegeOWLAction {
    @Override
    public void actionPerformed(ActionEvent ae) {
        OWLClass lastSelectedClass = getOWLWorkspace().getOWLSelectionModel().getLastSelectedClass();
//        OWLClass lastSelectedClass = getOWLWorkspace().getOWLModelManager().getActiveOntology().getClassesInSignature()
//                .forEach(owlClass -> owlClass.asOWLNamedIndividual().toString())
        if (lastSelectedClass != null) {
            OWLReasonerManager reasonerManager = getOWLModelManager().getOWLReasonerManager();
            ReasonerUtilities.warnUserIfReasonerIsNotConfigured(getOWLWorkspace(), reasonerManager);
            if (reasonerManager.getReasonerStatus() == ReasonerStatus.INITIALIZED) {
                OWLReasoner reasoner = reasonerManager.getCurrentReasoner();
                Set<OWLClass> subClasses = reasoner.getSubClasses(lastSelectedClass, false).getFlattened();
                
                String msg = "";
                if (!subClasses.isEmpty()) {
                    msg += "Inferred subclasses of " + getOWLModelManager().getRendering(lastSelectedClass) + " are:";
                    for (OWLClass subClass : subClasses) {
                        msg += System.lineSeparator() + getOWLModelManager().getRendering(subClass);
                    }
                }
                else {
                    msg = getOWLModelManager().getRendering(lastSelectedClass) + " has no inferred subclasses.";
                }
                JOptionPane.showMessageDialog(getOWLWorkspace(), msg);
            }
           
        }
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }
}
