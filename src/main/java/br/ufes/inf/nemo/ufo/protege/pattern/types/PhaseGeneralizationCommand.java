/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.types;

import br.ufes.inf.nemo.protege.annotations.EditorKitMenuAction;
import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.EntityFilter;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternApplier;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.ui.types.PhaseGeneralizationPatternFrame;
import java.awt.event.ActionEvent;
import java.util.List;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
@EditorKitMenuAction(
        id = "menuItemPhaseGeneralization",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.GeneralizeMenu/SlotA-04",
        name = "Generalize a gufo:Phase"
)
public class PhaseGeneralizationCommand extends PatternCommand {

    private IRI superType;
    private IRI phase;

    public void setSuperType(IRI superType) {
        this.superType = superType;
    }

    public void setPhase(IRI phase) {
        this.phase = phase;
    }
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        applier.addSubClassTo(superType, phase);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        List<IRI> phaseIRIs = new EntityFilter(getOWLModelManager())
                .isOfType(GufoIris.Phase)
                .entities();
        
        IRI firstPhase = phaseIRIs.isEmpty() ? null : phaseIRIs.get(0);
        List<IRI> superTypeIRIs = new EntityFilter(getOWLModelManager())
                .isOfType(GufoIris.Sortal)
                .isNotOfType(GufoIris.Role)
                .hasSameKindOf(firstPhase)
                .isNotSubClassByType(firstPhase, GufoIris.Role)
                .isNotDirectSuperClassOf(firstPhase)
                .isDifferentFrom(firstPhase)
                .unionWith()
                .isOfType(GufoIris.Sortal)
                .isOfType(GufoIris.Role)
                .hasSameKindOf(firstPhase)
                .isNotSubClassOf(firstPhase)
                .isNotDirectSuperClassOf(firstPhase)
                .unionWith()
                .isOfType(GufoIris.NonSortal)
                .hasOntologicalNatureOf(firstPhase)
                .isNotDirectSuperClassOf(firstPhase)
                .entities();
        
        PhaseGeneralizationPatternFrame frame = new PhaseGeneralizationPatternFrame(this);
        frame.setPhaseIRIs(phaseIRIs);
        frame.setSuperTypeIRIs(superTypeIRIs);
        frame.display();
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
