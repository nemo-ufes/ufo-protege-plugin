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
        id = "menuItemInstantiateRelator",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.ForInstancesMenu/SlotAA-Z",
        name = "New instance of Relator"
)
public class InstantiateRelatorCommand extends PatternCommand {

    @Override
    public void actionPerformed(ActionEvent ae) {
        String input =
                JOptionPane.showInputDialog(getOWLWorkspace(),
                    "Input: \"<sortal: Relator> <instance> <mediated: Endurant> <mediated: Endurant>\"."
                    + System.lineSeparator()
                    + "Example: \"Marriage FirstMarriage Adam Eve\".")
                .trim();
        String[] names = input.split(" ");
        IRI sortal = IRI.create(getOntologyPrefix(), names[0]);
        IRI relator = IRI.create(getOntologyPrefix(), names[1]);
        IRI mediatedA = IRI.create(getOntologyPrefix(), names[2]);
        IRI mediatedB = IRI.create(getOntologyPrefix(), names[3]);
        IRI mediates = IRI.create(GufoIris.GUFO, "mediates");

        try {
            PatternApplier applier = new PatternApplier(getOWLModelManager());
            if (applier.isSubClassOf(GufoIris.Relator, sortal) &&
                applier.isInstanceOf(GufoIris.Sortal, sortal) &&
                applier.isInstanceOf(GufoIris.Endurant, mediatedA) &&
                applier.isInstanceOf(GufoIris.Endurant, mediatedB)) {
                applier.createNamedIndividual(relator);
                applier.makeInstanceOf(sortal, relator);
                applier.createRelation(mediates, relator, mediatedA);
                applier.createRelation(mediates, relator, mediatedB);
            } else {
                showMessage("Only sortal types of Relator can be directly instantiated." + System.lineSeparator()
                          + "Two Endurants must be chosen to be mediated.");
            }
        } catch (Exception ex) {
            Logger.getLogger(InstantiateRelatorCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
