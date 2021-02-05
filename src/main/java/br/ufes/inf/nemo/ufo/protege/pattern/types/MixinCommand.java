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
import br.ufes.inf.nemo.ufo.protege.pattern.ui.types.MixinPatternFrame;
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
        id = "menuItemMixin",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.ForTypesMenu/SlotD-07",
        name = "New mixin"
)
public class MixinCommand extends PatternCommand {

    private IRI endurantClass;
    private IRI mixin;
    private IRI rigidSortal;
    private IRI antiRigidSortal;

    public void setEndurantClass(IRI endurantClass) {
        this.endurantClass = endurantClass;
    }

    public void setMixin(IRI mixin) {
        this.mixin = mixin;
    }

    public void setRigidSortal(IRI rigidSortal) {
        this.rigidSortal = rigidSortal;
    }

    public void setAntiRigidSortal(IRI antiRigidSortal) {
        this.antiRigidSortal = antiRigidSortal;
    }
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        applier.createNamedIndividual(mixin);
        applier.makeInstanceOf(GufoIris.Mixin, mixin);
        applier.createClass(mixin);
        applier.addSubClassTo(endurantClass, mixin);
        
        // From EntityFilter we can take that sharedEndurantClasses isn't empty for granted
        Set<OWLSubClassOfAxiom> sharedEndurantClasses = applier.sharedSuperClassAxioms(mixin, rigidSortal);
        applier.makeSubClassOf(mixin, rigidSortal, sharedEndurantClasses);
        sharedEndurantClasses = applier.sharedSuperClassAxioms(mixin, antiRigidSortal);
        applier.makeSubClassOf(mixin, antiRigidSortal, sharedEndurantClasses);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        List<IRI> endurantClassIRIs = new EntityFilter(getOWLModelManager())
                .addSuperClass(GufoIris.Endurant)
                .isPublicGufoClass()
                .entities();
        
        IRI firstEndurantClass = endurantClassIRIs.isEmpty() ? null : endurantClassIRIs.get(0);
        List<IRI> rigidSortalIRIs = new EntityFilter(getOWLModelManager())
                .addType(GufoIris.RigidType)
                .addType(GufoIris.Sortal)
                .addSuperClass(firstEndurantClass)
                .entities();
        
        IRI firstRigidSortal = rigidSortalIRIs.isEmpty() ? null : rigidSortalIRIs.get(0);
        List<IRI> antiRigidSortalIRIs = new EntityFilter(getOWLModelManager())
                .addType(GufoIris.AntiRigidType)
                .addType(GufoIris.Sortal)
                .addSuperClass(firstEndurantClass)
                .hasDifferentKindOf(firstRigidSortal)
                .entities();
        
        MixinPatternFrame frame = new MixinPatternFrame(this);
        frame.setEndurantClassIRIs(endurantClassIRIs);
        frame.setRigidSortalIRIs(rigidSortalIRIs);
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
