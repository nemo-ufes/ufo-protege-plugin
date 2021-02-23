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
import br.ufes.inf.nemo.ufo.protege.pattern.ui.relations.MaterialRelationshipTypePatternFrame;
import java.awt.event.ActionEvent;
import java.util.List;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
@EditorKitMenuAction(
        id = "menuItemMaterialRelationshipType",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.ForRelationsMenu/SlotE-07",
        name = "New gufo:MaterialRelationshipType"
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
        applier.assertObjectProperty(isDerivedFrom, relationshipType, relatorType);
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
        List<IRI> relatorTypeIRIs = new EntityFilter(getOWLModelManager())
                .addSuperClass(GufoIris.Relator)
                .addType(GufoIris.Sortal)
                .entities();
        
        IRI firstRelatorType = relatorTypeIRIs.isEmpty() ? null : relatorTypeIRIs.get(0);
        List<IRI> endurantClassIRIs = new EntityFilter(getOWLModelManager())
                .addSuperClass(GufoIris.Endurant)
                .isDifferentFrom(firstRelatorType)
                .entities();
        
        MaterialRelationshipTypePatternFrame frame = new MaterialRelationshipTypePatternFrame(this);
        frame.setRelatorTypeIRIs(relatorTypeIRIs);
        frame.setEndurantClassIRIs(endurantClassIRIs);
        frame.display();
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
