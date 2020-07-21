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

    private final IRI hasQualityValue = IRI.create(GufoIris.GUFO, "hasQualityValue");
    private IRI qualityType;
    private String noReifiedQuality;
    private IRI bearer;

    public void setQualityType(IRI qualityType) {
        this.qualityType = qualityType;
    }

    public void setNoReifiedQuality(String noReifiedQuality) {
        this.noReifiedQuality = noReifiedQuality;
    }

    public void setBearer(IRI bearer) {
        this.bearer = bearer;
    }
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        applier.assertDataProperty(qualityType, bearer, noReifiedQuality);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        String input =
                JOptionPane.showInputDialog(getOWLWorkspace(),
                    "Input: \"<qualityType: DataProperty> <value: String> <bearer: ConcreteIndividual>\"." + System.lineSeparator()
                    + "Example: \"hasMass 30-tons LittlePrincePlanet\".")
                .trim();
        String[] names = input.split(" ");

        qualityType = IRI.create(getOntologyPrefix(), names[0]);
        noReifiedQuality = names[1];
        bearer = IRI.create(getOntologyPrefix(), names[2]);

        try {
            PatternApplier applier = new PatternApplier(getOWLModelManager());
            if (applier.isSubDataPropertyOf(hasQualityValue, qualityType) &&
                applier.isInstanceOf(GufoIris.ConcreteIndividual, bearer)) {
                runCommand();
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
