/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.relations;

import br.ufes.inf.nemo.protege.annotations.EditorKitMenuAction;
import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.EntityFilter;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternApplier;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.ui.relations.ComparativeRelationshipTypePatternFrame;
import java.awt.event.ActionEvent;
import java.util.List;
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
        applier.assertObjectProperty(isDerivedFrom, relationshipType, qualityType);
        applier.createObjectProperty(relationshipType);
        applier.setObjectPropertyDomain(relationshipType, domainAndRange);
        applier.setObjectPropertyRange(relationshipType, domainAndRange);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        List<IRI> qualityTypeIRIs = new EntityFilter(getOWLModelManager())
                .addSuperClass(GufoIris.Quality)
                .entities();
        
        IRI firstQualityType = qualityTypeIRIs.isEmpty() ? null : qualityTypeIRIs.get(0);
        List<IRI> domainAndRangeIRIs = new EntityFilter(getOWLModelManager())
                .addSuperClass(GufoIris.ConcreteIndividual)
                .isDifferentFrom(firstQualityType)
                .entities();
        
        ComparativeRelationshipTypePatternFrame frame = new ComparativeRelationshipTypePatternFrame(this);
        frame.setQualityTypeIRIs(qualityTypeIRIs);
        frame.setConcreteIndividualClassIRIs(domainAndRangeIRIs);
        frame.display();
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
