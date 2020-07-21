/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.instances;

import br.ufes.inf.nemo.protege.annotations.EditorKitMenuAction;
import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternApplier;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
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
        id = "menuItemInstantiateObject",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.ForInstancesMenu/SlotAA-Z",
        name = "New instance of Object"
)
public class InstantiateObjectCommand extends PatternCommand {

    private IRI sortal;
    private IRI instance;

    public void setSortal(IRI sortal) {
        this.sortal = sortal;
    }

    public void setInstance(IRI instance) {
        this.instance = instance;
    }
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        applier.createNamedIndividual(instance);
        applier.makeInstanceOf(sortal, instance);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        String input =
                JOptionPane.showInputDialog(getOWLWorkspace(),
                    "Input: \"<sortal: ObjectClass> <instance>\"." + System.lineSeparator()
                    + "Example: \"Dog Jack\".")
                .trim();
        String[] names = input.split(" ");
        IRI sortal = IRI.create(getOntologyPrefix(), names[0]);
        IRI instance = IRI.create(getOntologyPrefix(), names[1]);

        try {
            PatternApplier applier = new PatternApplier(getOWLModelManager());
            if (applier.isSubClassOf(GufoIris.Object, sortal) &&
                applier.isInstanceOf(GufoIris.Sortal, sortal)) {
                runCommand();
            } else {
                showMessage("Only sortal types of Object can be directly instantiated.");
            }
        } catch (Exception ex) {
            Logger.getLogger(InstantiateObjectCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
