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
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

/**
 *
 * @author jeferson
 */
@EditorKitMenuAction(
        id = "menuItemAddToCategory",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.ForTypesMenu/SlotAA-Z",
        name = "Add to category"
)
public class AddToCategoryCommand extends PatternCommand {

    @Override
    public void actionPerformed(ActionEvent ae) {
        String input =
                JOptionPane.showInputDialog(getOWLWorkspace(),
                    "Input: \"<Category> <RigidType>\". " + System.lineSeparator()
                    + "Example: \"Animal Dog\".")
                .trim();
        String[] names = input.split(" ");
        IRI category = IRI.create(getOntologyPrefix(), names[0]);
        IRI rigidType = IRI.create(getOntologyPrefix(), names[1]);

        try {
            PatternApplier applier = new PatternApplier(getOWLModelManager());
            if (applier.isInstanceOf(GufoIris.Category, category) &&
                applier.isInstanceOf(GufoIris.RigidType, rigidType)) {

                Set<OWLSubClassOfAxiom> sharedEndurantClasses = applier.sharedSuperClassAxioms(category, rigidType);
                if(!sharedEndurantClasses.isEmpty()) {
                    applier.makeSubClassOf(category, rigidType, sharedEndurantClasses);
                } else {
                    showMessage("The category and the rigid type must share an Endurant class!");
                }

            } else {
                showMessage("You must select a category and a rigid type!");
            }
        } catch (Exception ex) {
            Logger.getLogger(AddToCategoryCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
