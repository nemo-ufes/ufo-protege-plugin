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
        id = "menuItemInstantiateNoReifiedQuality",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.ForInstancesMenu/SlotAA-Z",
        name = "New instance of no reified Quality"
)
public class InstantiateNoReifiedQualityCommand extends PatternCommand {

    @Override
    public void actionPerformed(ActionEvent ae) {
        String input =
                JOptionPane.showInputDialog(getOWLWorkspace(),
                    "Input: \"<qualityType: DataProperty> <value: String> <bearer: ConcreteIndividual>\"." + System.lineSeparator()
                    + "Example: \"hasMass 30-tons LittlePrincePlanet\".")
                .trim();
        String[] names = input.split(" ");

        IRI hasQualityValue = IRI.create(GufoIris.GUFO, "hasQualityValue");
        IRI type = IRI.create(getOntologyPrefix(), names[0]);
        String noReifiedQuality = names[1];
        IRI bearer = IRI.create(getOntologyPrefix(), names[2]);

        try {
            PatternApplier applier = new PatternApplier(getOWLModelManager());
            if (applier.isSubDataPropertyOf(hasQualityValue, type) &&
                applier.isInstanceOf(GufoIris.ConcreteIndividual, bearer)) {
                applier.assertDataProperty(type, bearer, noReifiedQuality);
            } else {
                showMessage("A subproperty of hasQualityValue must be chosen as property" + System.lineSeparator()
                          + "and a ConcreteIndividual must be chosen as bearer.");
            }
        } catch (Exception ex) {
            Logger.getLogger(InstantiateNoReifiedQualityCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
