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
import br.ufes.inf.nemo.ufo.protege.pattern.ui.types.RoleGeneralizationPatternFrame;
import java.awt.event.ActionEvent;
import java.util.List;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
@EditorKitMenuAction(
        id = "menuItemRoleGeneralization",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.GeneralizeMenu/SlotA-03",
        name = "Generalize a gufo:Role"
)
public class RoleGeneralizationCommand extends PatternCommand {

    private IRI superType;
    private IRI role;

    public void setSuperType(IRI superType) {
        this.superType = superType;
    }

    public void setRole(IRI role) {
        this.role = role;
    }
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        applier.addSubClassTo(superType, role);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        List<IRI> roleIRIs = new EntityFilter(getOWLModelManager())
                .isOfType(GufoIris.Role)
                .entities();
        
        IRI firstRole = roleIRIs.isEmpty() ? null : roleIRIs.get(0);
        List<IRI> superTypeIRIs = new EntityFilter(getOWLModelManager())
                .isOfType(GufoIris.Sortal)
                .isNotOfType(GufoIris.Phase)
                .hasSameKindOf(firstRole)
                .isNotSubClassByType(firstRole, GufoIris.Phase)
                .isNotDirectSuperClassOf(firstRole)
                .isDifferentFrom(firstRole)
                .unionWith()
                .isOfType(GufoIris.Sortal)
                .isOfType(GufoIris.Phase)
                .hasSameKindOf(firstRole)
                .isNotSubClassOf(firstRole)
                .isNotDirectSuperClassOf(firstRole)
                .unionWith()
                .isOfType(GufoIris.NonSortal)
                .hasOntologicalNatureOf(firstRole)
                .isNotDirectSuperClassOf(firstRole)
                .entities();
        
        RoleGeneralizationPatternFrame frame = new RoleGeneralizationPatternFrame(this);
        frame.setRoleIRIs(roleIRIs);
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
