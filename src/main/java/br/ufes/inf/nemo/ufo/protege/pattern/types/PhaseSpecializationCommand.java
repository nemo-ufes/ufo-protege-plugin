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
import br.ufes.inf.nemo.ufo.protege.pattern.ui.types.PhaseSpecializationPatternFrame;
import java.awt.event.ActionEvent;
import java.util.List;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
@EditorKitMenuAction(
        id = "menuItemPhaseSpecialization",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.SpecializeMenu/SlotA-08",
        name = "Specialize a gufo:Phase"
)
public class PhaseSpecializationCommand extends PatternCommand {

    private IRI phase;
    private IRI antiRigidSortal;

    public void setPhase(IRI phase) {
        this.phase = phase;
    }

    public void setAntiRigidSortal(IRI antiRigidSortal) {
        this.antiRigidSortal = antiRigidSortal;
    }
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        applier.addSubClassTo(phase, antiRigidSortal);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        List<IRI> phaseIRIs = new EntityFilter(getOWLModelManager())
                .isOfType(GufoIris.Phase)
                .entities();
        
        IRI firstPhase = phaseIRIs.isEmpty() ? null : phaseIRIs.get(0);
        List<IRI> antiRigidSortalIRIs = new EntityFilter(getOWLModelManager())
                .isOfType(GufoIris.Phase)
                .hasSameKindOf(firstPhase)
                .isNotSuperClassByType(firstPhase, GufoIris.Role)
                .isNotDirectSubClassOf(firstPhase)
                .isDifferentFrom(firstPhase)
                .unionWith()
                .isOfType(GufoIris.Role)
                .hasSameKindOf(firstPhase)
                .isNotSuperClassOf(firstPhase)
                .isNotDirectSubClassOf(firstPhase)
                .entities();
        
        PhaseSpecializationPatternFrame frame = new PhaseSpecializationPatternFrame(this);
        frame.setPhaseIRIs(phaseIRIs);
        frame.setAntiRigidSortalIRIs(antiRigidSortalIRIs);
        frame.display();
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
