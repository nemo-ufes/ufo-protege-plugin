/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.types;

import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
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
        id = "menuItemKind",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.ForTypesMenu/SlotAA-Z",
        name = "New kind"
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
        
        try {
            PatternApplier applier = new PatternApplier(getOWLModelManager());
            if (applier.isSubClassOf(GufoIris.Endurant, endurantClass) &&
                applier.isPublicGufoClass(endurantClass)) {
                applier.createNamedIndividual(kind);
                applier.makeInstanceOf(GufoIris.Kind, kind);
                applier.createClass(kind);
                applier.addSubClassTo(endurantClass, kind);
            } else {
                showMessage("A kind must be subclass of FunctionalComplex, " + System.lineSeparator()
                        + "FixedCollection, VariableCollection, Quantity, " + System.lineSeparator()
                        + "Quality, IntrinsicMode, ExtrinsicMode or Relator!");
            }
        } catch (Exception ex) {
            Logger.getLogger(KindCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initialise() throws Exception {
        
    }

    @Override
    public void dispose() throws Exception {
        
    }
    
}
