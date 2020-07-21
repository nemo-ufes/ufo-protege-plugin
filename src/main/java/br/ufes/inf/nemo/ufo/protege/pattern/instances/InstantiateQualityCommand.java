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
        id = "menuItemInstantiateQuality",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.ForInstancesMenu/SlotAA-Z",
        name = "New instance of Quality"
)
public class InstantiateQualityCommand extends PatternCommand {

    private final IRI inheritance = IRI.create(GufoIris.GUFO, "inheresIn");
    private final IRI hasQualityValue = IRI.create(GufoIris.GUFO, "hasQualityValue");
    private IRI sortal;
    private IRI quality;
    private String qualityValue;
    private IRI bearer;

    public void setSortal(IRI sortal) {
        this.sortal = sortal;
    }

    public void setQuality(IRI quality) {
        this.quality = quality;
    }

    public void setQualityValue(String qualityValue) {
        this.qualityValue = qualityValue;
    }

    public void setBearer(IRI bearer) {
        this.bearer = bearer;
    }
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        applier.createNamedIndividual(quality);
        applier.makeInstanceOf(sortal, quality);
        applier.createRelation(inheritance, quality, bearer);
        applier.assertDataProperty(hasQualityValue, quality, qualityValue);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        String input =
                JOptionPane.showInputDialog(getOWLWorkspace(),
                    "Input: \"<sortal: Quality> <instance> <value: String> <bearer: ConcreteIndividual>\"."
                    + System.lineSeparator()
                    + "Example: \"Age AgeOfJohn 35-years John\".")
                .trim();
        String[] names = input.split(" ");
        sortal = IRI.create(getOntologyPrefix(), names[0]);
        quality = IRI.create(getOntologyPrefix(), names[1]);
        qualityValue = names[2];
        bearer = IRI.create(getOntologyPrefix(), names[3]);

        try {
            PatternApplier applier = new PatternApplier(getOWLModelManager());
            if (applier.isSubClassOf(GufoIris.Quality, sortal) &&
                applier.isInstanceOf(GufoIris.Sortal, sortal) &&
                applier.isInstanceOf(GufoIris.ConcreteIndividual, bearer)) {
                runCommand();
            } else {
                showMessage("Only sortal types of Quality can be directly instantiated." + System.lineSeparator()
                          + "A ConcreteIndividual must be chosen as bearer.");
            }
        } catch (Exception ex) {
            Logger.getLogger(InstantiateQualityCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
