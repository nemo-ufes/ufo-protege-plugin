/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege;

import br.ufes.inf.nemo.protege.annotations.EditorKitMenuAction;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import org.protege.editor.owl.model.inference.OWLReasonerManager;
import org.protege.editor.owl.model.inference.ReasonerStatus;
import org.protege.editor.owl.model.inference.ReasonerUtilities;
import org.protege.editor.owl.ui.action.ProtegeOWLAction;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

/**
 * 
 * @author jeferson
 */
public class RigidityValidation extends ProtegeOWLAction  {

    @Override
    public void actionPerformed(ActionEvent ae) {
        OWLReasonerManager reasonerManager = getOWLModelManager().getOWLReasonerManager();
        ReasonerUtilities.warnUserIfReasonerIsNotConfigured(getOWLWorkspace(), reasonerManager);
        if (reasonerManager.getReasonerStatus() == ReasonerStatus.INITIALIZED) {
            OWLReasoner reasoner = reasonerManager.getCurrentReasoner();
            OWLDataFactory dataFactory = this.getOWLDataFactory();
            
            // Set of AntiRigidType instances IRIs
            OWLClass antiRigid = dataFactory
                    .getOWLClass(IRI.create("http://purl.org/nemo/gufo#AntiRigidType"));
            
            Set<OWLClass> antiRigidTypes = reasoner.getInstances(antiRigid, false)
                    .getFlattened().stream()
                    .map(owlIndividual -> owlIndividual.getIRI())
                    .map(iri -> dataFactory.getOWLClass(iri))
                    .collect(Collectors.toCollection(HashSet::new));
            
            // Set of RigidType instances IRIs
            OWLClass rigid = dataFactory
                    .getOWLClass(IRI.create("http://purl.org/nemo/gufo#RigidType"));
            
            Set<OWLClass> rigidTypes = reasoner.getInstances(rigid, false)
                    .getFlattened().stream()
                    .map(owlIndividual -> owlIndividual.getIRI())
                    .map(iri -> dataFactory.getOWLClass(iri))
                    .collect(Collectors.toCollection(HashSet::new));
            
            // Checking for specialization problems
            String problems = "";
            for(OWLClass antiRigidType : antiRigidTypes) {
               Set<OWLClass> subClasses =
                       reasoner.getSubClasses(antiRigidType, false).getFlattened();
               for(OWLClass subClass : subClasses) {
                   if(rigidTypes.contains(subClass)) {
                       problems += "The rigid type "
                               + subClass.getIRI().getShortForm()
                               + " is specialization of the anti-rigid type "
                               + antiRigidType.getIRI().getShortForm()
                               + System.lineSeparator();
                   }
               }
            }
                    
            String msg;
            if(problems.isEmpty()) {
                msg = "No rigidity problems are present.";
            } else {
                msg = "These are all rigidity problems: "
                        + System.lineSeparator()
                        + problems;
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
