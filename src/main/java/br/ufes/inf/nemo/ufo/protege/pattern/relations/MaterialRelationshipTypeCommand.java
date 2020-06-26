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
        id = "menuItemMaterialRelationshipType",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.ForRelationsMenu/SlotAA-Z",
        name = "New type of material relationship"
)
public class MaterialRelationshipTypeCommand extends PatternCommand {

    @Override
    public void actionPerformed(ActionEvent ae) {
        String input =
                JOptionPane.showInputDialog(getOWLWorkspace(), "Type four names: ")
                .trim();
        String[] names = input.split(" ");
        IRI isDerivedFrom = IRI.create(GufoIris.GUFO, "isDerivedFrom");
        IRI relatorType = IRI.create(getOntologyPrefix(), names[0]);
        IRI relationshipType = IRI.create(getOntologyPrefix(), names[1]);
        IRI domain = IRI.create(getOntologyPrefix(), names[2]);
        IRI range = IRI.create(getOntologyPrefix(), names[3]);

        try {
            PatternApplier applier = new PatternApplier(getOWLModelManager());
            if (applier.isSubClassOf(GufoIris.Relator, relatorType) &&
                applier.isSubClassOf(GufoIris.Endurant, domain) &&
                applier.isSubClassOf(GufoIris.Endurant, range)) {
                applier.createNamedIndividual(relationshipType);
                applier.makeInstanceOf(GufoIris.MaterialRelationshipType, relationshipType);
                applier.createRelation(isDerivedFrom, relationshipType, relatorType);
                applier.createObjectProperty(relationshipType);
                applier.setObjectPropertyDomain(relationshipType, domain);
                applier.setObjectPropertyRange(relationshipType, range);
            } else {
                showMessage("A MaterialRelationshipType must be derived from a Relator type " + System.lineSeparator()
                          + "and have an Endurant type as domain and as range.");
            }
        } catch (Exception ex) {
            Logger.getLogger(MaterialRelationshipTypeCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
