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
import br.ufes.inf.nemo.ufo.protege.pattern.ui.types.RoleMixinSpecializationPatternFrame;
import java.awt.event.ActionEvent;
import java.util.List;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
@EditorKitMenuAction(
        id = "menuItemRoleMixinSpecialization",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.SpecializeMenu/SlotA-03",
        name = "Specialize a gufo:RoleMixin"
)
public class RoleMixinSpecializationCommand extends PatternCommand {

    private IRI rolemixin;
    private IRI antiRigidType;

    public void setRoleMixin(IRI rolemixin) {
        this.rolemixin = rolemixin;
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
        // Set<OWLSubClassOfAxiom> sharedEndurantClasses = applier.sharedSuperClassAxioms(rolemixin, antiRigidType);
        applier.addSubClassTo(rolemixin, antiRigidType);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        List<IRI> rolemixinIRIs = new EntityFilter(getOWLModelManager())
                .isOfType(GufoIris.RoleMixin)
                .entities();
        
        IRI firstRoleMixin = rolemixinIRIs.isEmpty() ? null : rolemixinIRIs.get(0);
        List<IRI> antiRigidTypeIRIs = new EntityFilter(getOWLModelManager())
                .isOfType(GufoIris.AntiRigidType)
                .isNotOfType(GufoIris.PhaseMixin)
                .isOfOntologicalNatureOf(firstRoleMixin)
                .isNotSuperClassByType(firstRoleMixin, GufoIris.PhaseMixin)
                .isNotDirectSubClassOf(firstRoleMixin)
                .isDifferentFrom(firstRoleMixin)
                .unionWith()
                .isOfType(GufoIris.PhaseMixin)
                .isOfOntologicalNatureOf(firstRoleMixin)
                .isNotSuperClassOf(firstRoleMixin)
                .isNotDirectSubClassOf(firstRoleMixin)
                .entities();
        
        RoleMixinSpecializationPatternFrame frame = new RoleMixinSpecializationPatternFrame(this);
        frame.setRoleMixinIRIs(rolemixinIRIs);
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
