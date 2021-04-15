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
import br.ufes.inf.nemo.ufo.protege.pattern.ui.types.CategoryGeneralizationPatternFrame;
import java.awt.event.ActionEvent;
import java.util.List;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
@EditorKitMenuAction(
        id = "menuItemCategoryGeneralization",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.GeneralizeMenu/SlotA-05",
        name = "Generalize a gufo:Category"
)
public class CategoryGeneralizationCommand extends PatternCommand {

    private IRI superType;
    private IRI category;

    public void setSuperType(IRI superType) {
        this.superType = superType;
    }

    public void setCategory(IRI category) {
        this.category = category;
    }
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        applier.addSubClassTo(superType, category);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        List<IRI> categoryRIs = new EntityFilter(getOWLModelManager())
                .isOfType(GufoIris.Category)
                .entities();
        
        IRI firstCategory = categoryRIs.isEmpty() ? null : categoryRIs.get(0);
        List<IRI> superTypeIRIs = new EntityFilter(getOWLModelManager())
                .isOfType(GufoIris.Category)
                .hasOntologicalNatureOf(firstCategory)
                .isNotSubClassByType(firstCategory, GufoIris.Mixin)
                .isNotDirectSuperClassOf(firstCategory)
                .isDifferentFrom(firstCategory)
                .unionWith()
                .isOfType(GufoIris.Mixin)
                .hasOntologicalNatureOf(firstCategory)
                .isNotSubClassOf(firstCategory)
                .isNotDirectSuperClassOf(firstCategory)
                .entities();
        
        CategoryGeneralizationPatternFrame frame = new CategoryGeneralizationPatternFrame(this);
        frame.setCategoryIRIs(categoryRIs);
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
