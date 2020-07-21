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
        id = "menuItemAddRoleToRoleMixin",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.ForTypesMenu/SlotAA-Z",
        name = "Add role to rolemixin"
)
public class AddRoleToRoleMixinCommand extends PatternCommand {

    private IRI rolemixin;
    private IRI role;

    public void setRoleMixin(IRI rolemixin) {
        this.rolemixin = rolemixin;
    }

    public void setRole(IRI role) {
        this.role = role;
    }
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        applier.addSubClassTo(rolemixin, role);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        String input =
                JOptionPane.showInputDialog(getOWLWorkspace(),
                    "Input: \"<RoleMixin> <Role>\". " + System.lineSeparator()
                    + "Example: \"Customer CorporateCustomer\".")
                .trim();
        String[] names = input.split(" ");
        rolemixin = IRI.create(getOntologyPrefix(), names[0]);
        role = IRI.create(getOntologyPrefix(), names[1]);

        try {
            PatternApplier applier = new PatternApplier(getOWLModelManager());
            if (applier.isInstanceOf(GufoIris.RoleMixin, rolemixin) &&
                applier.isInstanceOf(GufoIris.Role, role)) {
                runCommand();
            } else {
                showMessage("You must select a rolemixin and a role!");
            }
        } catch (Exception ex) {
            Logger.getLogger(AddRoleToRoleMixinCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
