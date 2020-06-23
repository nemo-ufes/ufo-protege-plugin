/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.types;

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
        id = "menuItemNoReifiedQuality",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.ForTypesMenu/SlotAA-Z",
        name = "New no reified quality type"
)
public class NoReifiedQualityCommand extends PatternCommand {

    @Override
    public void actionPerformed(ActionEvent ae) {
        String input =
                JOptionPane.showInputDialog(getOWLWorkspace(), "Type two name: ")
                .trim();
        String[] names = input.split(" ");
        IRI domain = IRI.create(getOntologyPrefix(), names[0]);
        IRI qualityType = IRI.create(getOntologyPrefix(), names[1]);
        IRI hasQualityValue = IRI.create(GufoIris.GUFO, "hasQualityValue");
        
        try {
            PatternApplier applier = new PatternApplier(getOWLModelManager());
            if (applier.isSubClassOf(GufoIris.ConcreteIndividual, domain)) {
                applier.createSubDataProperty(hasQualityValue, qualityType);
                applier.setDataPropertyDomain(qualityType, domain);
            } else {
                showMessage("A type of ConcreteIndividual must be chosen as domain.");
            }
        } catch (Exception ex) {
            Logger.getLogger(NoReifiedQualityCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initialise() throws Exception {
        
    }

    @Override
    public void dispose() throws Exception {
        
    }
    
}
