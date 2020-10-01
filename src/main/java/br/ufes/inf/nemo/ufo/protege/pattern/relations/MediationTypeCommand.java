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
import br.ufes.inf.nemo.ufo.protege.pattern.ui.relations.MediationTypePatternFrame;
import java.awt.event.ActionEvent;
import java.util.List;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
@EditorKitMenuAction(
        id = "menuItemMediationType",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.ForRelationsMenu/SlotAA-Z",
        name = "New type of mediation"
)
public class MediationTypeCommand extends PatternCommand {

    private final IRI mediates = IRI.create(GufoIris.GUFO, "mediates");
    private IRI mediationType;
    private IRI relatorType;
    private IRI mediatedType;
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        applier.createSubObjectProperty(mediates, mediationType);
        applier.setObjectPropertyDomain(mediationType, relatorType);
        applier.setObjectPropertyRange(mediationType, mediatedType);
    }

    public void setMediationType(IRI mediationType) {
        this.mediationType = mediationType;
    }

    public void setRelatorType(IRI relatorType) {
        this.relatorType = relatorType;
    }

    public void setMediatedType(IRI mediatedType) {
        this.mediatedType = mediatedType;
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
        
        MediationTypePatternFrame frame = new MediationTypePatternFrame(this);
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
