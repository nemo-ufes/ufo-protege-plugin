/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege;

import br.ufes.inf.nemo.protege.annotations.EditorKitMenuAction;
import java.awt.event.ActionEvent;
import org.protege.editor.owl.model.entity.OWLEntityFactory;
import org.protege.editor.owl.model.inference.OWLReasonerManager;
import org.protege.editor.owl.model.inference.ReasonerStatus;
import org.protege.editor.owl.model.inference.ReasonerUtilities;
import org.protege.editor.owl.ui.action.ProtegeOWLAction;
import org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

/**
 *
 * @author jeferson
 */
public class AddProperties extends ProtegeOWLAction {

    @Override
    public void actionPerformed(ActionEvent ae) {
        OWLReasonerManager reasonerManager = getOWLModelManager().getOWLReasonerManager();
        ReasonerUtilities.warnUserIfReasonerIsNotConfigured(getOWLWorkspace(), reasonerManager);
        if (reasonerManager.getReasonerStatus() == ReasonerStatus.INITIALIZED) {
            OWLReasoner reasoner = reasonerManager.getCurrentReasoner();
            OWLDataFactory dataFactory = this.getOWLDataFactory();
            OWLOntologyManager ontologyManager = this.getOWLModelManager().getOWLOntologyManager();
            OWLEntityFactory entityFactory = this.getOWLModelManager().getOWLEntityFactory();
            
            OWLObjectProperty memberOf = OWLFunctionalSyntaxFactory.createObjectProperty();
            
            // I want to modify the object property memberOf.
        }
    }

    @Override
    public void initialise() throws Exception {
        
    }

    @Override
    public void dispose() throws Exception {
        
    }
    
}
