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
        String input =
                JOptionPane.showInputDialog(getOWLWorkspace(),
                    "Input: \"<EndurantClass> <Category>\". " + System.lineSeparator()
                    + "Example: \"FunctionalComplex Animal\".")
                .trim();
        String[] names = input.split(" ");
        endurantClass = IRI.create(GufoIris.GUFO, names[0]);
        category = IRI.create(getOntologyPrefix(), names[1]);

        try {
            PatternApplier applier = new PatternApplier(getOWLModelManager());
            if (applier.isSubClassOf(GufoIris.Endurant, endurantClass) &&
                applier.isPublicGufoClass(endurantClass)) {
                runCommand();
            } else {
                showMessage("A category must be subclass of FunctionalComplex, " + System.lineSeparator()
                        + "FixedCollection, VariableCollection, Quantity, " + System.lineSeparator()
                        + "Quality, IntrinsicMode, ExtrinsicMode or Relator!");
            }
        } catch (Exception ex) {
            Logger.getLogger(CategoryCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
