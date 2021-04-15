/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.instances;

import br.ufes.inf.nemo.protege.annotations.EditorKitMenuAction;
import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.EntityFilter;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternApplier;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.ui.instances.InstantiateRelatorByMediationsPatternFrame;
import java.awt.event.ActionEvent;
import java.util.List;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
/* @EditorKitMenuAction(
        id = "menuItemInstantiateRelatorByMediations",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.ForInstancesMenu/SlotD-07",
        name = "New instance of gufo:Relator by mediations"
) */
public class InstantiateRelatorByMediationsCommand extends PatternCommand {

    private final IRI mediates = IRI.create(GufoIris.GUFO, "mediates");
    private IRI mediationType;
    private IRI relatorType;
    private IRI relator;
    private IRI mediatedA;
    private IRI mediatedB;

    public void setMediationType(IRI mediationType) {
        this.mediationType = mediationType;
    }
    
    public void setRelatorType(IRI relatorType) {
        this.relatorType = relatorType;
    }

    public void setRelator(IRI relator) {
        this.relator = relator;
    }

    public void setMediatedA(IRI mediatedA) {
        this.mediatedA = mediatedA;
    }

    public void setMediatedB(IRI mediatedB) {
        this.mediatedB = mediatedB;
    }
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        applier.createNamedIndividual(relator);
        applier.makeInstanceOf(relatorType, relator);
        applier.assertObjectProperty(mediationType, relator, mediatedA);
        applier.assertObjectProperty(mediationType, relator, mediatedB);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        List<IRI> mediationTypeIRIs = new EntityFilter(getOWLModelManager())
                .hasSuperObjectProperty(mediates)
                .entities();
        
        IRI firstMediationType = mediationTypeIRIs.isEmpty() ? null : mediationTypeIRIs.get(0);
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        List<IRI> mediatedIRIs;
        if(firstMediationType == null) {
            mediatedIRIs = mediationTypeIRIs;
        } else {
            IRI mediatedType = applier.getObjectPropertyRange(firstMediationType);
            mediatedIRIs = new EntityFilter(getOWLModelManager())
                    .isOfType(mediatedType)
                    .entities();
        }
        
        InstantiateRelatorByMediationsPatternFrame frame = new InstantiateRelatorByMediationsPatternFrame(this);
        frame.setMediationTypeIRIs(mediationTypeIRIs);
        frame.setMediatedIRIs(mediatedIRIs);
        frame.display();
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
