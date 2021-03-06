/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.relations;

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
        id = "menuItemSubQuantityOf",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.ForRelationsMenu/SlotAA-Z",
        name = "New subquantity-of relation"
)
public class SubQuantityOfCommand extends PatternCommand {

    @Override
    public void actionPerformed(ActionEvent ae) {
        String input =
                JOptionPane.showInputDialog(getOWLWorkspace(),
                    "Input: \"<subQuantity: Quantity> <Quantity>\"." + System.lineSeparator()
                    + "Example: \"AlcoholInCupOfWine CupOfWine\".")
                .trim();
        String[] names = input.split(" ");
        IRI subquantity = IRI.create(getOntologyPrefix(), names[0]);
        IRI quantity = IRI.create(getOntologyPrefix(), names[1]);
        IRI subQuantityOfRelation = IRI.create(GufoIris.GUFO, "isSubQuantityOf");

        try {
            PatternApplier applier = new PatternApplier(getOWLModelManager());
            if (applier.isInstanceOf(GufoIris.Quantity, subquantity) &&
                applier.isInstanceOf(GufoIris.Quantity, quantity)) {
                applier.createRelation(subQuantityOfRelation, subquantity, quantity);
            } else {
                showMessage("Only quantities can be subquantity of another quantities.");
            }
        } catch (Exception ex) {
            Logger.getLogger(SubQuantityOfCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
