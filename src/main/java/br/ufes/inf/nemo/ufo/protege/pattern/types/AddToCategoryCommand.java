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
import br.ufes.inf.nemo.ufo.protege.pattern.ui.types.AddToCategoryPatternFrame;
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
        id = "menuItemAddToCategory",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.ForTypesMenu/SlotC-07",
        name = "Add to category"
) */
public class AddToCategoryCommand extends PatternCommand {

    private IRI category;
    private IRI rigidType;

    public void setCategory(IRI category) {
        this.category = category;
    }

    public void setRigidType(IRI rigidType) {
        this.rigidType = rigidType;
    }
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        
        /**
         * From EntityFilter we can take that the category and the rigid type
         * have the same public superclass for granted
         */
        Set<OWLSubClassOfAxiom> sharedEndurantClasses = applier.sharedSuperClassAxioms(category, rigidType);
        applier.makeSubClassOf(category, rigidType, sharedEndurantClasses);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        List<IRI> categoryIRIs = new EntityFilter(getOWLModelManager())
                .isOfType(GufoIris.Category)
                .entities();
        
        IRI firstCategory = categoryIRIs.isEmpty() ? null : categoryIRIs.get(0);
        List<IRI> rigidTypeIRIs = new EntityFilter(getOWLModelManager())
                .isOfType(GufoIris.RigidType)
                .isOfOntologicalNatureOf(firstCategory)
                .isNotSuperClassOf(firstCategory)
                .isDifferentFrom(firstCategory)
                .entities();
        
        AddToCategoryPatternFrame frame = new AddToCategoryPatternFrame(this);
        frame.setCategoryIRIs(categoryIRIs);
        frame.setRigidTypeIRIs(rigidTypeIRIs);
        frame.display();
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
