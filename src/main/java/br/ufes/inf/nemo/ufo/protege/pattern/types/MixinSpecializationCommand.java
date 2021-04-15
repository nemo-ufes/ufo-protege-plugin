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
import br.ufes.inf.nemo.ufo.protege.pattern.ui.types.MixinSpecializationPatternFrame;
import java.awt.event.ActionEvent;
import java.util.List;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
@EditorKitMenuAction(
        id = "menuItemMixinSpecialization",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.SpecializeMenu/SlotA-02",
        name = "Specialize a gufo:Mixin"
)
public class MixinSpecializationCommand extends PatternCommand {
    
    private IRI mixin;
    private IRI endurantType;

    public void setMixin(IRI mixin) {
        this.mixin = mixin;
    }

    public void setEndurantType(IRI endurantType) {
        this.endurantType = endurantType;
    }
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        
        // From EntityFilter we can take that sharedEndurantClasses isn't empty for granted
        // Set<OWLSubClassOfAxiom> sharedEndurantClasses = applier.sharedSuperClassAxioms(mixin, endurantType);
        applier.addSubClassTo(mixin, endurantType);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        List<IRI> mixinIRIs = new EntityFilter(getOWLModelManager())
                .isOfType(GufoIris.Mixin)
                .entities();
        
        IRI firstMixin = mixinIRIs.isEmpty() ? null : mixinIRIs.get(0);
        List<IRI> endurantTypeIRIs = new EntityFilter(getOWLModelManager())
                .isOfType(GufoIris.EndurantType)
                .isNotOfType(GufoIris.Category)
                .isOfOntologicalNatureOf(firstMixin)
                .isNotSuperClassByType(firstMixin, GufoIris.Category)
                .isNotDirectSubClassOf(firstMixin)
                .isDifferentFrom(firstMixin)
                .unionWith()
                .isOfType(GufoIris.Category)
                .isOfOntologicalNatureOf(firstMixin)
                .isNotSuperClassOf(firstMixin)
                .isNotDirectSubClassOf(firstMixin)
                .entities();
        
        MixinSpecializationPatternFrame frame = new MixinSpecializationPatternFrame(this);
        frame.setMixinIRIs(mixinIRIs);
        frame.setEndurantTypeIRIs(endurantTypeIRIs);
        frame.display();
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
