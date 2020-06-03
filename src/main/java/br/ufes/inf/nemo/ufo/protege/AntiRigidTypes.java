/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege;

import br.ufes.inf.nemo.protege.annotations.EditorKitMenuAction;
import java.awt.event.ActionEvent;
import java.util.Optional;
import javax.swing.JOptionPane;
import org.protege.editor.owl.model.inference.OWLReasonerManager;
import org.protege.editor.owl.model.inference.ReasonerStatus;
import org.protege.editor.owl.model.inference.ReasonerUtilities;
import org.protege.editor.owl.ui.action.ProtegeOWLAction;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

/**
 *
 * @author jeferson
 */
public class AntiRigidTypes extends ProtegeOWLAction  {

    @Override
    public void actionPerformed(ActionEvent ae) {
        OWLReasonerManager reasonerManager = getOWLModelManager().getOWLReasonerManager();
        ReasonerUtilities.warnUserIfReasonerIsNotConfigured(getOWLWorkspace(), reasonerManager);
        if (reasonerManager.getReasonerStatus() == ReasonerStatus.INITIALIZED) {
            OWLReasoner reasoner = reasonerManager.getCurrentReasoner();

            OWLClass antiRigid = this.getOWLDataFactory()
                    .getOWLClass(IRI.create("http://purl.org/nemo/gufo#AntiRigidType"));

            Optional<String> antiRigidTypes = reasoner.getInstances(antiRigid, false).getFlattened().stream()
                    .map(antiRigidType -> antiRigidType.getIRI().toQuotedString() + System.lineSeparator())
                    .reduce((a, b) -> a + b);

            String msg;
            if(antiRigidTypes.isPresent()) {
                msg = "These are all the anti-rigid types: "
                        + System.lineSeparator()
                        + antiRigidTypes.get();
            } else {
                msg = "No anti-rigid types are present.";
            }
            JOptionPane.showMessageDialog(getOWLWorkspace(), msg);
        }
    }

    @Override
    public void initialise() throws Exception {
        
    }

    @Override
    public void dispose() throws Exception {
        
    }
}
