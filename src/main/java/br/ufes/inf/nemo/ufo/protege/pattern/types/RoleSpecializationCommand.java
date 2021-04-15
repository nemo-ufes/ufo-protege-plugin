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
import br.ufes.inf.nemo.ufo.protege.pattern.ui.types.RoleSpecializationPatternFrame;
import java.awt.event.ActionEvent;
import java.util.List;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
@EditorKitMenuAction(
        id = "menuItemRoleSpecialization",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.SpecializeMenu/SlotA-07",
        name = "Specialize a gufo:Role"
)
public class RoleSpecializationCommand extends PatternCommand {

    private IRI role;
    private IRI antiRigidSortal;

    public void setRole(IRI role) {
        this.role = role;
    }

    public void setAntiRigidSortal(IRI antiRigidSortal) {
        this.antiRigidSortal = antiRigidSortal;
    }
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        applier.addSubClassTo(role, antiRigidSortal);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        List<IRI> roleIRIs = new EntityFilter(getOWLModelManager())
                .isOfType(GufoIris.Role)
                .entities();
        
        IRI firstRole = roleIRIs.isEmpty() ? null : roleIRIs.get(0);
        List<IRI> antiRigidSortalIRIs = new EntityFilter(getOWLModelManager())
                .isOfType(GufoIris.Role)
                .hasSameKindOf(firstRole)
                .isNotSuperClassByType(firstRole, GufoIris.Phase)
                .isNotDirectSubClassOf(firstRole)
                .isDifferentFrom(firstRole)
                .unionWith()
                .isOfType(GufoIris.Phase)
                .hasSameKindOf(firstRole)
                .isNotSuperClassOf(firstRole)
                .isNotDirectSubClassOf(firstRole)
                .entities();
        
        RoleSpecializationPatternFrame frame = new RoleSpecializationPatternFrame(this);
        frame.setRoleIRIs(roleIRIs);
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
