/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.helpers;

import javax.swing.JOptionPane;
import org.protege.editor.owl.ui.action.ProtegeOWLAction;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 *
 * @author jeferson
 */
public abstract class PatternCommand extends ProtegeOWLAction {
    public String getOntologyPrefix() {
        OWLOntology ontology = getOWLModelManager().getActiveOntology();
        String ontologyPrefix = ontology.getOWLOntologyManager()
                .getOntologyFormat(ontology)
                .asPrefixOWLOntologyFormat()
                .getDefaultPrefix();
        return ontologyPrefix;
    }
    
    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(getOWLWorkspace(), msg);
    }
}
