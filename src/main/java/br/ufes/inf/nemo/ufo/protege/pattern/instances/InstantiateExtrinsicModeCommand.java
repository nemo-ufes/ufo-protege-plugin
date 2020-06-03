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
        id = "menuItemInstantiateExtrinsicMode",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.ForInstancesMenu/SlotAA-Z",
        name = "New instance of ExtrinsicMode"
)
public class InstantiateExtrinsicModeCommand extends PatternCommand {

    @Override
    public void actionPerformed(ActionEvent ae) {
        String input =
                JOptionPane.showInputDialog(getOWLWorkspace(), "Type four names: ")
                .trim();
        String[] names = input.split(" ");
        IRI sortal = IRI.create(getOntologyPrefix(), names[0]);
        IRI extrinsicMode = IRI.create(getOntologyPrefix(), names[1]);
        IRI bearer = IRI.create(getOntologyPrefix(), names[2]);
        IRI externalDependence = IRI.create(getOntologyPrefix(), names[3]);
        IRI inheritance = IRI.create(GufoIris.GUFO, "inheresIn");
        IRI dependenceRelation = IRI.create(GufoIris.GUFO, "externallyDependsOn");
        
        try {
            PatternApplier applier = new PatternApplier(getOWLModelManager());
            if (applier.isSubClassOf(GufoIris.ExtrinsicMode, sortal) &&
                applier.isInstanceOf(GufoIris.Sortal, sortal) &&
                applier.isInstanceOf(GufoIris.ConcreteIndividual, bearer) &&
                applier.isInstanceOf(GufoIris.Endurant, externalDependence)) {
                applier.createNamedIndividual(extrinsicMode);
                applier.makeInstanceOf(sortal, extrinsicMode);
                applier.createRelation(inheritance, extrinsicMode, bearer);
                applier.createRelation(dependenceRelation, extrinsicMode, externalDependence);
            } else {
                showMessage("Only sortal types of ExtrinsicMode can be directly instantiated." + System.lineSeparator()
                          + "A ConcreteIndividual must be chosen as bearer." + System.lineSeparator()
                          + "An Endurant must be chosen as external dependence.");
            }
        } catch (Exception ex) {
            Logger.getLogger(InstantiateExtrinsicModeCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initialise() throws Exception {
        
    }

    @Override
    public void dispose() throws Exception {
        
    }
    
}
