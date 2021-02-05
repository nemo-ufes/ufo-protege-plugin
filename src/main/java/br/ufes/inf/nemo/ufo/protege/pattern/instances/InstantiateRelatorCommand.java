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
import br.ufes.inf.nemo.ufo.protege.pattern.ui.instances.InstantiateRelatorPatternFrame;
import java.awt.event.ActionEvent;
import java.util.List;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
@EditorKitMenuAction(
        id = "menuItemInstantiateRelator",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.ForInstancesMenu/SlotD-6",
        name = "New instance of Relator"
)
public class InstantiateRelatorCommand extends PatternCommand {

    private final IRI mediates = IRI.create(GufoIris.GUFO, "mediates");
    private IRI sortal;
    private IRI relator;
    private IRI mediatedA;
    private IRI mediatedB;

    public void setSortal(IRI sortal) {
        this.sortal = sortal;
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
        applier.makeInstanceOf(sortal, relator);
        applier.assertObjectProperty(mediates, relator, mediatedA);
        applier.assertObjectProperty(mediates, relator, mediatedB);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        List<IRI> relatorIRIs = new EntityFilter(getOWLModelManager())
                .addSuperClass(GufoIris.Relator)
                .addType(GufoIris.Sortal)
                .entities();
        
        List<IRI> mediatedIRIs = new EntityFilter(getOWLModelManager())
                .addType(GufoIris.Endurant)
                .entities();
        
        InstantiateRelatorPatternFrame frame = new InstantiateRelatorPatternFrame(this);
        frame.setRelatorTypeIRIs(relatorIRIs);
        frame.setEndurantIRIs(mediatedIRIs);
        frame.display();
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
