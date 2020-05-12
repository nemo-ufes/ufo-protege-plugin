/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui;

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
        id = "ufopp.menuItemRoleMixinOfCategory",
        path = "org.protege.editor.core.application.menu.FileMenu/SlotAA-Z",
        name = "Add rolemixin of category"
)
public class RoleMixinOfCategoryCommand extends PatternCommand {

    @Override
    public void actionPerformed(ActionEvent ae) {
        String input =
                JOptionPane.showInputDialog(getOWLWorkspace(), "Type two names: ")
                .trim();
        String[] names = input.split(" ");
        IRI category = IRI.create(getOntologyPrefix(), names[0]);
        IRI rolemixin = IRI.create(getOntologyPrefix(), names[1]);
        
        try {
            PatternApplier applier = new PatternApplier(getOWLModelManager());
            if (applier.isInstanceOf(GufoIris.Category, category)) {
                applier.createNamedIndividual(rolemixin);
                applier.makeInstanceOf(GufoIris.RoleMixin, rolemixin);
                applier.createClass(rolemixin);
                applier.addSubClassTo(category, rolemixin);
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
