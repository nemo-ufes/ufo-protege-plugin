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

    private final IRI isDerivedFrom = IRI.create(GufoIris.GUFO, "isDerivedFrom");
    private IRI relatorType;
    private IRI relationshipType;
    private IRI domain;
    private IRI range;
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        applier.createNamedIndividual(relationshipType);
        applier.makeInstanceOf(GufoIris.MaterialRelationshipType, relationshipType);
        applier.createRelation(isDerivedFrom, relationshipType, relatorType);
        applier.createObjectProperty(relationshipType);
        applier.setObjectPropertyDomain(relationshipType, domain);
        applier.setObjectPropertyRange(relationshipType, range);
    }

    public void setRelatorType(IRI relatorType) {
        this.relatorType = relatorType;
    }

    public void setRelationshipType(IRI relationshipType) {
        this.relationshipType = relationshipType;
    }

    public void setDomain(IRI domain) {
        this.domain = domain;
    }

    public void setRange(IRI range) {
        this.range = range;
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        String input =
                JOptionPane.showInputDialog(getOWLWorkspace(),
                    "Input: \"<derivation: Relator> <RelationshipType> <domain: EndurantClass> <range: EndurantClass>\"."
                    + System.lineSeparator()
                    + "Example: \"Marriage wifeOf Woman Man\".")
                .trim();
        String[] names = input.split(" ");
        relatorType = IRI.create(getOntologyPrefix(), names[0]);
        relationshipType = IRI.create(getOntologyPrefix(), names[1]);
        domain = IRI.create(getOntologyPrefix(), names[2]);
        range = IRI.create(getOntologyPrefix(), names[3]);

        try {
            PatternApplier applier = new PatternApplier(getOWLModelManager());
            if (applier.isSubClassOf(GufoIris.Relator, relatorType) &&
                applier.isSubClassOf(GufoIris.Endurant, domain) &&
                applier.isSubClassOf(GufoIris.Endurant, range)) {
                runCommand();
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
