/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege;

import br.ufes.inf.nemo.protege.annotations.EditorKitMenuAction;
import java.awt.event.ActionEvent;
import java.util.Optional;
import java.util.stream.Stream;
import javax.swing.JOptionPane;
import org.protege.editor.owl.ui.action.ProtegeOWLAction;
import org.semanticweb.owlapi.model.OWLClass;

/**
 *
 * @author jbatista
 */
public class PublicClassesOnly extends ProtegeOWLAction {
    @Override
    public void actionPerformed(ActionEvent ae) {
        UFOConfig ufo = UFOConfig.get(this.getOWLModelManager());
        Stream<OWLClass> owlClasses = ufo.owlClasses(this.getOWLModelManager().getActiveOntology());
        
        // If some private gUFO class is used, show a warning.
        Optional<String> usedPrivateClasses = owlClasses
                .filter(ufo::isUFOViewClass)
                .filter(owlClass -> !ufo.isPublicUFOClass(owlClass))
                .map(owlClass -> owlClass.getIRI().toQuotedString() + System.lineSeparator())
                .reduce((a, b) -> a + b);
        
        String msg;
        if(usedPrivateClasses.isPresent()) {
            msg = "Warning: The following private classes of gUFO were used: "
                    + System.lineSeparator()
                    + usedPrivateClasses.get();
        } else {
            msg = "Only public classes of gUFO were used.";
        }
        JOptionPane.showMessageDialog(getOWLWorkspace(), msg);
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }
}
