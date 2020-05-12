/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.helpers;

import br.ufes.inf.nemo.protege.annotations.EditorKitMenuAction;
import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.ui.MixinCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.ui.PatternCommand;
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
        id = "ufopp.menuItemRoleMixin",
        path = "org.protege.editor.core.application.menu.FileMenu/SlotAA-Z",
        name = "New rolemixin"
)
public class RoleMixinCommand extends PatternCommand {

    @Override
    public void actionPerformed(ActionEvent ae) {
        String input =
                JOptionPane.showInputDialog(getOWLWorkspace(), "Type two names: ")
                .trim();
        String[] names = input.split(" ");
        IRI endurantClass = IRI.create(GufoIris.GUFO, names[0]);
        IRI rolemixin = IRI.create(getOntologyPrefix(), names[1]);
        
        try {
            PatternApplier applier = new PatternApplier(getOWLModelManager());
            if (applier.isSubClassOf(GufoIris.Endurant, endurantClass) &&
                applier.isPublicGufoClass(endurantClass)) {
                applier.createNamedIndividual(rolemixin);
                applier.makeInstanceOf(GufoIris.RoleMixin, rolemixin);
                applier.createClass(rolemixin);
                applier.addSubClassTo(endurantClass, rolemixin);
            } else {
                showMessage("A rolemixin must be subclass of FunctionalComplex, " + System.lineSeparator()
                        + "FixedCollection, VariableCollection, Quantity, " + System.lineSeparator()
                        + "Quality, IntrinsicMode, ExtrinsicMode or Relator!");
            }
        } catch (Exception ex) {
            Logger.getLogger(RoleMixinCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initialise() throws Exception {
        
    }

    @Override
    public void dispose() throws Exception {
        
    }
    
}
