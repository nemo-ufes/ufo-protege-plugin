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
import br.ufes.inf.nemo.ufo.protege.pattern.ui.types.CategoryPatternFrame;
import java.awt.event.ActionEvent;
import java.util.List;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
@EditorKitMenuAction(
        id = "menuItemCategory",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.ForTypesMenu/SlotAA-Z",
        name = "New category"
)
public class CategoryCommand extends PatternCommand {

    private IRI endurantClass;
    private IRI category;

    public void setEndurantClass(IRI endurantClass) {
        this.endurantClass = endurantClass;
    }

    public void setCategory(IRI category) {
        this.category = category;
    }
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        applier.createNamedIndividual(category);
        applier.makeInstanceOf(GufoIris.Category, category);
        applier.createClass(category);
        applier.addSubClassTo(endurantClass, category);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        List<IRI> endurantClassIRIs = new EntityFilter(getOWLModelManager())
                .addSuperClass(GufoIris.Endurant)
                .isPublicGufoClass()
                .entities();
        
        CategoryPatternFrame frame = new CategoryPatternFrame(this);
        frame.setEndurantClassIRIs(endurantClassIRIs);
        frame.display();
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
