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
import br.ufes.inf.nemo.ufo.protege.pattern.ui.types.AddToMixinPatternFrame;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Set;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

/**
 *
 * @author jeferson
 */
@EditorKitMenuAction(
        id = "menuItemAddToMixin",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.ForTypesMenu/SlotD-08",
        name = "Add to mixin"
)
public class AddToMixinCommand extends PatternCommand {
    
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
        Set<OWLSubClassOfAxiom> sharedEndurantClasses = applier.sharedSuperClassAxioms(mixin, endurantType);
        applier.makeSubClassOf(mixin, endurantType, sharedEndurantClasses);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        List<IRI> mixinIRIs = new EntityFilter(getOWLModelManager())
                .addType(GufoIris.Mixin)
                .entities();
        
        IRI firstMixin = mixinIRIs.isEmpty() ? null : mixinIRIs.get(0);
        List<IRI> endurantTypeIRIs = new EntityFilter(getOWLModelManager())
                .addType(GufoIris.EndurantType)
                .hasSamePublicSuperClass(firstMixin)
                .isNotSuperClassOf(firstMixin)
                .isDifferentFrom(firstMixin)
                .entities();
        
        AddToMixinPatternFrame frame = new AddToMixinPatternFrame(this);
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
