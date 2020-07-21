/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.types;

import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.protege.annotations.EditorKitMenuAction;
import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternApplier;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
@EditorKitMenuAction(
        id = "menuItemRoleMixinOfCategory",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.ForTypesMenu/SlotAA-Z",
        name = "Add rolemixin of category"
)
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
        String input =
                JOptionPane.showInputDialog(getOWLWorkspace(), 
                    "Input: \"<Category> <RoleMixin>\". " + System.lineSeparator()
                    + "Example: \"LegalAgent Customer\".")
                .trim();
        String[] names = input.split(" ");
        category = IRI.create(getOntologyPrefix(), names[0]);
        rolemixin = IRI.create(getOntologyPrefix(), names[1]);

        try {
            PatternApplier applier = new PatternApplier(getOWLModelManager());
            if (applier.isInstanceOf(GufoIris.Category, category)) {
                runCommand();
            } else {
                showMessage("You must select a category to be specialized in a rolemixin!");
            }
        } catch (Exception ex) {
            Logger.getLogger(RoleMixinOfCategoryCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
