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
        id = "menuItemPhaseMixin",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.ForTypesMenu/SlotAA-Z",
        name = "Add phasemixin"
)
public class PhaseMixinCommand extends PatternCommand {

    private IRI nonsortal;
    private IRI phasemixin;

    public void setNonSortal(IRI nonsortal) {
        this.nonsortal = nonsortal;
    }

    public void setPhaseMixin(IRI phasemixin) {
        this.phasemixin = phasemixin;
    }
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        applier.createNamedIndividual(phasemixin);
        applier.makeInstanceOf(GufoIris.PhaseMixin, phasemixin);
        applier.createClass(phasemixin);
        applier.addSubClassTo(nonsortal, phasemixin);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        String input =
                JOptionPane.showInputDialog(getOWLWorkspace(), 
                    "Input: \"<NonSortal> <PhaseMixin>\". " + System.lineSeparator()
                    + "Example: \"Animal Alive\".")
                .trim();
        String[] names = input.split(" ");
        nonsortal = IRI.create(getOntologyPrefix(), names[0]);
        phasemixin = IRI.create(getOntologyPrefix(), names[1]);

        try {
            PatternApplier applier = new PatternApplier(getOWLModelManager());
            if (applier.isInstanceOf(GufoIris.NonSortal, nonsortal)) {
                runCommand();
            } else {
                showMessage("There are only phasemixins of non-sortals!");
            }
        } catch (Exception ex) {
            Logger.getLogger(PhaseMixinCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
