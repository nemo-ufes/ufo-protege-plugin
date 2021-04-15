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
import br.ufes.inf.nemo.ufo.protege.pattern.ui.types.InstantiableCategoryPatternFrame;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Set;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

/**
 *
 * @author jeferson
 */
/* @EditorKitMenuAction(
        id = "menuItemInstantiableCategory",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.ForTypesMenu/SlotC-01",
        name = "Create instantiable gufo:Category"
) */
public class InstantiableCategoryCommand extends PatternCommand {

    private IRI endurantClass;
    private IRI category;
    private IRI firstRigidSortal;
    private IRI secondRigidSortal;

    public void setEndurantClass(IRI endurantClass) {
        this.endurantClass = endurantClass;
    }

    public void setCategory(IRI category) {
        this.category = category;
    }

    public void setFirstRigidSortal(IRI firstRigidSortal) {
        this.firstRigidSortal = firstRigidSortal;
    }

    public void setSecondRigidSortal(IRI secondRigidSortal) {
        this.secondRigidSortal = secondRigidSortal;
    }
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        applier.createNamedIndividual(category);
        applier.makeInstanceOf(GufoIris.Category, category);
        applier.createClass(category);
        applier.addSubClassTo(endurantClass, category);
        
        /**
         * From EntityFilter we can take that the category and the rigid sortal
         * have the same public superclass for granted
         */
        Set<OWLSubClassOfAxiom> sharedEndurantClasses = applier.sharedSuperClassAxioms(category, firstRigidSortal);
        applier.makeSubClassOf(category, firstRigidSortal, sharedEndurantClasses);
        sharedEndurantClasses = applier.sharedSuperClassAxioms(category, secondRigidSortal);
        applier.makeSubClassOf(category, secondRigidSortal, sharedEndurantClasses);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        List<IRI> endurantClassIRIs = new EntityFilter(getOWLModelManager())
                .hasSuperClass(GufoIris.Endurant)
                .isPublicGufoClass()
                .entities();
        
        IRI firstEndurantClass = endurantClassIRIs.isEmpty() ? null : endurantClassIRIs.get(0);
        List<IRI> firstRigidSortalIRIs = new EntityFilter(getOWLModelManager())
                .isOfType(GufoIris.RigidType)
                .isOfType(GufoIris.Sortal)
                .hasSuperClass(firstEndurantClass)
                .entities();
        
        IRI firstFirstRigidSortal = firstRigidSortalIRIs.isEmpty() ? null : firstRigidSortalIRIs.get(0);
        List<IRI> secondRigidSortalIRIs = new EntityFilter(getOWLModelManager())
                .isOfType(GufoIris.RigidType)
                .isOfType(GufoIris.Sortal)
                .hasSuperClass(firstEndurantClass)
                .hasDifferentKindOf(firstFirstRigidSortal)
                .entities();
        
        InstantiableCategoryPatternFrame frame = new InstantiableCategoryPatternFrame(this);
        frame.setEndurantClassIRIs(endurantClassIRIs);
        frame.setFirstRigidSortalIRIs(firstRigidSortalIRIs);
        frame.setSecondRigidSortalIRIs(secondRigidSortalIRIs);
        frame.display();
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
