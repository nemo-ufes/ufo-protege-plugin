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
        id = "menuItemComparativeRelationshipType",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.ForRelationsMenu/SlotAA-Z",
        name = "New type of comparative relationship"
)
public class ComparativeRelationshipTypeCommand extends PatternCommand {

    private final IRI isDerivedFrom = IRI.create(GufoIris.GUFO, "isDerivedFrom");
    private IRI qualityType;
    private IRI relationshipType;
    private IRI domainAndRange;

    public void setQualityType(IRI qualityType) {
        this.qualityType = qualityType;
    }

    public void setRelationshipType(IRI relationshipType) {
        this.relationshipType = relationshipType;
    }

    public void setDomainAndRange(IRI domainAndRange) {
        this.domainAndRange = domainAndRange;
    }
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        applier.createNamedIndividual(relationshipType);
        applier.makeInstanceOf(GufoIris.ComparativeRelationshipType, relationshipType);
        applier.createRelation(isDerivedFrom, relationshipType, qualityType);
        applier.createObjectProperty(relationshipType);
        applier.setObjectPropertyDomain(relationshipType, domainAndRange);
        applier.setObjectPropertyRange(relationshipType, domainAndRange);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        String input =
                JOptionPane.showInputDialog(getOWLWorkspace(),
                    "Input: \"<derivation: Quality> <RelationshipType> <domainAndRange: ConcreteIndividualClass>\"." + System.lineSeparator()
                    + "Example: \"Weight heavierThan PhysicalObject\".")
                .trim();
        String[] names = input.split(" ");
        qualityType = IRI.create(getOntologyPrefix(), names[0]);
        relationshipType = IRI.create(getOntologyPrefix(), names[1]);
        domainAndRange = IRI.create(getOntologyPrefix(), names[2]);

        try {
            PatternApplier applier = new PatternApplier(getOWLModelManager());
            if (applier.isSubClassOf(GufoIris.Quality, qualityType) &&
                applier.isSubClassOf(GufoIris.ConcreteIndividual, domainAndRange)) {
                runCommand();
            } else {
                showMessage("A ComparativeRelationshipType must be derived from a Quality type " + System.lineSeparator()
                          + "and have a single Endurant type as domain and range.");
            }
        } catch (Exception ex) {
            Logger.getLogger(ComparativeRelationshipTypeCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
