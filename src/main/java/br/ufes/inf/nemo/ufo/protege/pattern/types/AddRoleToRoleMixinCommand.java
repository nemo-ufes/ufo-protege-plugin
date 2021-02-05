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
import br.ufes.inf.nemo.ufo.protege.pattern.ui.types.AddRoleToRoleMixinPatternFrame;
import java.awt.event.ActionEvent;
import java.util.List;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
@EditorKitMenuAction(
        id = "menuItemAddRoleToRoleMixin",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.ForTypesMenu/SlotE-13",
        name = "Add a gufo:Role to a gufo:Rolemixin"
)
public class AddRoleToRoleMixinCommand extends PatternCommand {

    private IRI rolemixin;
    private IRI role;

    public void setRoleMixin(IRI rolemixin) {
        this.rolemixin = rolemixin;
    }

    public void setRole(IRI role) {
        this.role = role;
    }
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        applier.addSubClassTo(rolemixin, role);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        List<IRI> rolemixinIRIs = new EntityFilter(getOWLModelManager())
                .addType(GufoIris.RoleMixin)
                .entities();
        
        IRI firstRoleMixin = rolemixinIRIs.isEmpty() ? null : rolemixinIRIs.get(0);
        List<IRI> roleIRIs = new EntityFilter(getOWLModelManager())
                .addType(GufoIris.Role)
                .hasSamePublicSuperClass(firstRoleMixin)
                .isNotSubClassOf(firstRoleMixin)
                .entities();
        
        AddRoleToRoleMixinPatternFrame frame = new AddRoleToRoleMixinPatternFrame(this);
        frame.setRoleMixinIRIs(rolemixinIRIs);
        frame.setRoleIRIs(roleIRIs);
        frame.display();
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
