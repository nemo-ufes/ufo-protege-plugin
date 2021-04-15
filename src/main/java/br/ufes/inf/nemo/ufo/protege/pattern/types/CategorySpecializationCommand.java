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
import br.ufes.inf.nemo.ufo.protege.pattern.ui.types.CategorySpecializationPatternFrame;
import java.awt.event.ActionEvent;
import java.util.List;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
@EditorKitMenuAction(
        id = "menuItemCategorySpecialization",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.SpecializeMenu/SlotA-01",
        name = "Specialize a gufo:Category"
)
public class CategorySpecializationCommand extends PatternCommand {

    private IRI category;
    private IRI endurantType;

    public void setCategory(IRI category) {
        this.category = category;
    }

    public void setEndurantType(IRI endurantType) {
        this.endurantType = endurantType;
    }
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        
        /**
         * From EntityFilter we can take that the category and the endurant type
         * have the same public superclass for granted
         */
        // Set<OWLSubClassOfAxiom> sharedEndurantClasses = applier.sharedSuperClassAxioms(category, endurantType);
        applier.addSubClassTo(category, endurantType);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        List<IRI> categoryIRIs = new EntityFilter(getOWLModelManager())
                .isOfType(GufoIris.Category)
                .entities();
        
        IRI firstCategory = categoryIRIs.isEmpty() ? null : categoryIRIs.get(0);
        List<IRI> endurantTypeIRIs = new EntityFilter(getOWLModelManager())
                .isOfType(GufoIris.EndurantType)
                .isNotOfType(GufoIris.Mixin)
                .isOfOntologicalNatureOf(firstCategory)
                .isNotSuperClassByType(firstCategory, GufoIris.Mixin)
                .isNotDirectSubClassOf(firstCategory)
                .isDifferentFrom(firstCategory)
                .unionWith()
                .isOfType(GufoIris.Mixin)
                .isOfOntologicalNatureOf(firstCategory)
                .isNotSuperClassOf(firstCategory)
                .isNotDirectSubClassOf(firstCategory)
                .entities();
        
        CategorySpecializationPatternFrame frame = new CategorySpecializationPatternFrame(this);
        frame.setCategoryIRIs(categoryIRIs);
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
