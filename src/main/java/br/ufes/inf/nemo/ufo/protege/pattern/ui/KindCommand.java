/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui;

import br.ufes.inf.nemo.protege.annotations.EditorKitMenuAction;
import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternApplier;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
@EditorKitMenuAction(
        id = "ufopp.menuItem9",
        path = "org.protege.editor.core.application.menu.FileMenu/SlotAA-Z",
        name = "Add kind"
)
public class KindCommand extends PatternCommand {

    @Override
    public void actionPerformed(ActionEvent ae) {
        String input =
                JOptionPane.showInputDialog(getOWLWorkspace(), "Type two names: ")
                .trim();
        String[] names = input.split(" ");
        IRI endurantClass = IRI.create(GufoIris.GUFO, names[0]);
        IRI kind = IRI.create(getOntologyPrefix(), names[1]);
        
        /* FIXME: the method isDirectSubClassOf doesn't work here, because
         * endurantClass is of the gUFO ontology, not of the active ontology.
         */
        try {
            PatternApplier applier = new PatternApplier(getOWLModelManager());
            // if (applier.isDirectSubClassOf(GufoIris.Object, endurantClass)) {
            if (names[0].contentEquals("FunctionalComplex") ||
                names[0].contentEquals("Collection") ||
                names[0].contentEquals("Quantity")) {
                applier.createNamedIndividual(kind);
                applier.makeInstanceOf(GufoIris.Kind, kind);
                applier.createClass(kind);
                applier.makeSubClassOf(endurantClass, kind);
            } else {
                showError("A kind must be subclass of FunctionalComplex, Collection or Quantity!");
            }
        } catch (Exception ex) {
            Logger.getLogger(SubClassCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initialise() throws Exception {
        
    }

    @Override
    public void dispose() throws Exception {
        
    }
    
}
