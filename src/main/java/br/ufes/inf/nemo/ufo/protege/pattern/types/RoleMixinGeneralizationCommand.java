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
import br.ufes.inf.nemo.ufo.protege.pattern.ui.types.RoleMixinGeneralizationPatternFrame;
import java.awt.event.ActionEvent;
import java.util.List;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
@EditorKitMenuAction(
        id = "menuItemRoleMixinGeneralization",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.GeneralizeMenu/SlotA-07",
        name = "Generalize a gufo:RoleMixin"
)
public class RoleMixinGeneralizationCommand extends PatternCommand {

    private IRI superType;
    private IRI rolemixin;

    public void setSuperType(IRI superType) {
        this.superType = superType;
    }

    public void setRoleMixin(IRI rolemixin) {
        this.rolemixin = rolemixin;
    }
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        applier.addSubClassTo(superType, rolemixin);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        List<IRI> rolemixinRIs = new EntityFilter(getOWLModelManager())
                .isOfType(GufoIris.RoleMixin)
                .entities();
        
        IRI firstRoleMixin = rolemixinRIs.isEmpty() ? null : rolemixinRIs.get(0);
        List<IRI> superTypeIRIs = new EntityFilter(getOWLModelManager())
                .isOfType(GufoIris.NonSortal)
                .isNotOfType(GufoIris.PhaseMixin)
                .hasOntologicalNatureOf(firstRoleMixin)
                .isNotSubClassByType(firstRoleMixin, GufoIris.PhaseMixin)
                .isNotDirectSuperClassOf(firstRoleMixin)
                .isDifferentFrom(firstRoleMixin)
                .unionWith()
                .isOfType(GufoIris.PhaseMixin)
                .hasOntologicalNatureOf(firstRoleMixin)
                .isNotSubClassOf(firstRoleMixin)
                .isNotDirectSuperClassOf(firstRoleMixin)
                .entities();
        
        RoleMixinGeneralizationPatternFrame frame = new RoleMixinGeneralizationPatternFrame(this);
        frame.setRoleMixinIRIs(rolemixinRIs);
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
