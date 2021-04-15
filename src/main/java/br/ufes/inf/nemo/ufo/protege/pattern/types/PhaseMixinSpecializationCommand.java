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
import br.ufes.inf.nemo.ufo.protege.pattern.ui.types.PhaseMixinSpecializationPatternFrame;
import java.awt.event.ActionEvent;
import java.util.List;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
@EditorKitMenuAction(
        id = "menuItemPhaseMixinSpecialization",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.SpecializeMenu/SlotA-04",
        name = "Specialize a gufo:PhaseMixin"
)
public class PhaseMixinSpecializationCommand extends PatternCommand {

    private IRI phasemixin;
    private IRI antiRigidType;

    public void setPhaseMixin(IRI phasemixin) {
        this.phasemixin = phasemixin;
    }

    public void setAntiRigidType(IRI antiRigidType) {
        this.antiRigidType = antiRigidType;
    }
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        
        /**
         * From EntityFilter we can take that the category and the endurant type
         * have the same public superclass for granted
         */
        // Set<OWLSubClassOfAxiom> sharedEndurantClasses = applier.sharedSuperClassAxioms(phasemixin, antiRigidType);
        applier.addSubClassTo(phasemixin, antiRigidType);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        List<IRI> phasemixinIRIs = new EntityFilter(getOWLModelManager())
                .isOfType(GufoIris.PhaseMixin)
                .entities();
        
        IRI firstPhaseMixin = phasemixinIRIs.isEmpty() ? null : phasemixinIRIs.get(0);
        List<IRI> antiRigidTypeIRIs = new EntityFilter(getOWLModelManager())
                .isOfType(GufoIris.AntiRigidType)
                .isNotOfType(GufoIris.RoleMixin)
                .isOfOntologicalNatureOf(firstPhaseMixin)
                .isNotSuperClassByType(firstPhaseMixin, GufoIris.RoleMixin)
                .isNotDirectSubClassOf(firstPhaseMixin)
                .isDifferentFrom(firstPhaseMixin)
                .unionWith()
                .isOfType(GufoIris.RoleMixin)
                .isOfOntologicalNatureOf(firstPhaseMixin)
                .isNotSuperClassOf(firstPhaseMixin)
                .isNotDirectSubClassOf(firstPhaseMixin)
                .entities();
        
        PhaseMixinSpecializationPatternFrame frame = new PhaseMixinSpecializationPatternFrame(this);
        frame.setPhaseMixinIRIs(phasemixinIRIs);
        frame.setAntiRigidTypeIRIs(antiRigidTypeIRIs);
        frame.display();
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
