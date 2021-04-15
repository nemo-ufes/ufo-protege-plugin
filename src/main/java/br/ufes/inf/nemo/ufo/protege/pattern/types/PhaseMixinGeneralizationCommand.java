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
import br.ufes.inf.nemo.ufo.protege.pattern.ui.types.PhaseMixinGeneralizationPatternFrame;
import java.awt.event.ActionEvent;
import java.util.List;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
@EditorKitMenuAction(
        id = "menuItemPhaseMixinGeneralization",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.GeneralizeMenu/SlotA-08",
        name = "Generalize a gufo:PhaseMixin"
)
public class PhaseMixinGeneralizationCommand extends PatternCommand {

    private IRI superType;
    private IRI phasemixin;

    public void setSuperType(IRI superType) {
        this.superType = superType;
    }

    public void setPhaseMixin(IRI phasemixin) {
        this.phasemixin = phasemixin;
    }
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        applier.addSubClassTo(superType, phasemixin);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        List<IRI> phasemixinRIs = new EntityFilter(getOWLModelManager())
                .isOfType(GufoIris.PhaseMixin)
                .entities();
        
        IRI firstPhaseMixin = phasemixinRIs.isEmpty() ? null : phasemixinRIs.get(0);
        List<IRI> superTypeIRIs = new EntityFilter(getOWLModelManager())
                .isOfType(GufoIris.NonSortal)
                .isNotOfType(GufoIris.RoleMixin)
                .hasOntologicalNatureOf(firstPhaseMixin)
                .isNotSubClassByType(firstPhaseMixin, GufoIris.RoleMixin)
                .isNotDirectSuperClassOf(firstPhaseMixin)
                .isDifferentFrom(firstPhaseMixin)
                .unionWith()
                .isOfType(GufoIris.RoleMixin)
                .hasOntologicalNatureOf(firstPhaseMixin)
                .isNotSubClassOf(firstPhaseMixin)
                .isNotDirectSuperClassOf(firstPhaseMixin)
                .entities();
        
        PhaseMixinGeneralizationPatternFrame frame = new PhaseMixinGeneralizationPatternFrame(this);
        frame.setPhaseMixinIRIs(phasemixinRIs);
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
