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
import br.ufes.inf.nemo.ufo.protege.pattern.ui.types.RoleMixinOfCategoryPatternFrame;
import java.awt.event.ActionEvent;
import java.util.List;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
/* @EditorKitMenuAction(
        id = "menuItemRoleMixinOfCategory",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.ForTypesMenu/SlotE-14",
        name = "Add rolemixin of category"
) */
public class RoleMixinOfCategoryCommand extends PatternCommand {

    private IRI category;
    private IRI rolemixin;

    public void setCategory(IRI category) {
        this.category = category;
    }

    public void setRoleMixin(IRI rolemixin) {
        this.rolemixin = rolemixin;
    }
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        applier.createNamedIndividual(rolemixin);
        applier.makeInstanceOf(GufoIris.RoleMixin, rolemixin);
        applier.createClass(rolemixin);
        applier.addSubClassTo(category, rolemixin);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        List<IRI> categoryIRIs = new EntityFilter(getOWLModelManager())
                .isOfType(GufoIris.Category)
                .entities();
        
        RoleMixinOfCategoryPatternFrame frame = new RoleMixinOfCategoryPatternFrame(this);
        frame.setCategoryIRIs(categoryIRIs);
        frame.display();
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
