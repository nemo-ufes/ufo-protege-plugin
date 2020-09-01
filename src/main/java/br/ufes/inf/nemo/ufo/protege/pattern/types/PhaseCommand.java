/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.types;

import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.protege.annotations.EditorKitMenuAction;
import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.EntityFilter;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternApplier;
import br.ufes.inf.nemo.ufo.protege.pattern.ui.types.PhasePatternFrame;
import java.awt.event.ActionEvent;
import java.util.List;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
@EditorKitMenuAction(
        id = "menuItemPhase",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.ForTypesMenu/SlotAA-Z",
        name = "Add phase"
)
public class PhaseCommand extends PatternCommand {
    
    private IRI sortal;
    private IRI phase;

    public void setSortal(IRI sortal) {
        this.sortal = sortal;
    }

    public void setPhase(IRI phase) {
        this.phase = phase;
    }
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        applier.createNamedIndividual(phase);
        applier.makeInstanceOf(GufoIris.Phase, phase);
        applier.createClass(phase);
        applier.addSubClassTo(sortal, phase);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        List<IRI> sortalIRIs = new EntityFilter(getOWLModelManager())
                .addType(GufoIris.Sortal)
                .entities();
        
        PhasePatternFrame frame = new PhasePatternFrame(this);
        frame.setSortalIRIs(sortalIRIs);
        frame.display();
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
