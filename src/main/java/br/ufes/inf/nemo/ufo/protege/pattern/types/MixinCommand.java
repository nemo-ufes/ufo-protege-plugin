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
        id = "menuItemMixin",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.ForTypesMenu/SlotAA-Z",
        name = "New mixin"
)
public class MixinCommand extends PatternCommand {

    private IRI endurantClass;
    private IRI mixin;

    public void setEndurantClass(IRI endurantClass) {
        this.endurantClass = endurantClass;
    }

    public void setMixin(IRI mixin) {
        this.mixin = mixin;
    }
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        applier.createNamedIndividual(mixin);
        applier.makeInstanceOf(GufoIris.Mixin, mixin);
        applier.createClass(mixin);
        applier.addSubClassTo(endurantClass, mixin);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        String input =
                JOptionPane.showInputDialog(getOWLWorkspace(), 
                    "Input: \"<EndurantClass> <Mixin>\". " + System.lineSeparator()
                    + "Example: \"FunctionalComplex Sitable\".")
                .trim();
        String[] names = input.split(" ");
        endurantClass = IRI.create(GufoIris.GUFO, names[0]);
        mixin = IRI.create(getOntologyPrefix(), names[1]);

        try {
            PatternApplier applier = new PatternApplier(getOWLModelManager());
            if (applier.isSubClassOf(GufoIris.Endurant, endurantClass) &&
                applier.isPublicGufoClass(endurantClass)) {
                runCommand();
            } else {
                showMessage("A mixin must be subclass of FunctionalComplex, " + System.lineSeparator()
                        + "FixedCollection, VariableCollection, Quantity, " + System.lineSeparator()
                        + "Quality, IntrinsicMode, ExtrinsicMode or Relator!");
            }
        } catch (Exception ex) {
            Logger.getLogger(MixinCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
