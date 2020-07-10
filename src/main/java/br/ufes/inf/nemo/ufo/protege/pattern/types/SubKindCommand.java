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
        id = "menuItemSubKind",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.ForTypesMenu/SlotAA-Z",
        name = "Add subkind"
)
public class SubKindCommand extends PatternCommand {

    @Override
    public void actionPerformed(ActionEvent ae) {
        String input =
                JOptionPane.showInputDialog(getOWLWorkspace(), 
                    "Input: \"<RigidSortal> <SubKind>\". " + System.lineSeparator()
                    + "Example: \"Person Man\".")
                .trim();
        String[] names = input.split(" ");
        IRI rigidSortal = IRI.create(getOntologyPrefix(), names[0]);
        IRI subkind = IRI.create(getOntologyPrefix(), names[1]);

        try {
            PatternApplier applier = new PatternApplier(getOWLModelManager());
            if (applier.isInstanceOf(GufoIris.Kind, rigidSortal)  ||
                applier.isInstanceOf(GufoIris.SubKind, rigidSortal)) {
                applier.createNamedIndividual(subkind);
                applier.makeInstanceOf(GufoIris.SubKind, subkind);
                applier.createClass(subkind);
                applier.addSubClassTo(rigidSortal, subkind);
            } else {
                // showMessage("There are only subkinds of rigid sortals (kinds or other subkinds)!");
                EntityFilter criterion = new EntityFilter(getOWLModelManager());
                criterion.addType(GufoIris.RigidType);
                criterion.addType(GufoIris.Sortal);
                showMessage(EntityFilter.showSet(criterion.filterEntities()));
            }
        } catch (Exception ex) {
            Logger.getLogger(SubKindCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
