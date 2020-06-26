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
        id = "menuItemInstantiateIntrinsicMode",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.ForInstancesMenu/SlotAA-Z",
        name = "New instance of IntrinsicMode"
)
public class InstantiateIntrinsicModeCommand extends PatternCommand {

    @Override
    public void actionPerformed(ActionEvent ae) {
        String input =
                JOptionPane.showInputDialog(getOWLWorkspace(), "Type three names: ")
                .trim();
        String[] names = input.split(" ");
        IRI sortal = IRI.create(getOntologyPrefix(), names[0]);
        IRI intrinsicMode = IRI.create(getOntologyPrefix(), names[1]);
        IRI bearer = IRI.create(getOntologyPrefix(), names[2]);
        IRI inheritance = IRI.create(GufoIris.GUFO, "inheresIn");

        try {
            PatternApplier applier = new PatternApplier(getOWLModelManager());
            if (applier.isSubClassOf(GufoIris.IntrinsicMode, sortal) &&
                applier.isInstanceOf(GufoIris.Sortal, sortal) &&
                applier.isInstanceOf(GufoIris.ConcreteIndividual, bearer)) {
                applier.createNamedIndividual(intrinsicMode);
                applier.makeInstanceOf(sortal, intrinsicMode);
                applier.createRelation(inheritance, intrinsicMode, bearer);
            } else {
                showMessage("Only sortal types of IntrinsicMode can be directly instantiated." + System.lineSeparator()
                          + "A ConcreteIndividual must be chosen as bearer.");
            }
        } catch (Exception ex) {
            Logger.getLogger(InstantiateIntrinsicModeCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
