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
import br.ufes.inf.nemo.ufo.protege.pattern.ui.types.MixinGeneralizationPatternFrame;
import java.awt.event.ActionEvent;
import java.util.List;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
@EditorKitMenuAction(
        id = "menuItemMixinGeneralization",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.GeneralizeMenu/SlotA-06",
        name = "Generalize a gufo:Mixin"
)
public class MixinGeneralizationCommand extends PatternCommand {

    private IRI superType;
    private IRI mixin;

    public void setSuperType(IRI superType) {
        this.superType = superType;
    }

    public void setMixin(IRI mixin) {
        this.mixin = mixin;
    }
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        applier.addSubClassTo(superType, mixin);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        List<IRI> mixinRIs = new EntityFilter(getOWLModelManager())
                .isOfType(GufoIris.Mixin)
                .entities();
        
        IRI firstMixin = mixinRIs.isEmpty() ? null : mixinRIs.get(0);
        List<IRI> superTypeIRIs = new EntityFilter(getOWLModelManager())
                .isOfType(GufoIris.Mixin)
                .hasOntologicalNatureOf(firstMixin)
                .isNotSubClassByType(firstMixin, GufoIris.Category)
                .isNotDirectSuperClassOf(firstMixin)
                .isDifferentFrom(firstMixin)
                .unionWith()
                .isOfType(GufoIris.Category)
                .hasOntologicalNatureOf(firstMixin)
                .isNotSubClassOf(firstMixin)
                .isNotDirectSuperClassOf(firstMixin)
                .entities();
        
        MixinGeneralizationPatternFrame frame = new MixinGeneralizationPatternFrame(this);
        frame.setMixinIRIs(mixinRIs);
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
