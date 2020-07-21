/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.relations;

import br.ufes.inf.nemo.protege.annotations.EditorKitMenuAction;
import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternApplier;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
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
        id = "menuItemSubCollectionOf",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.ForRelationsMenu/SlotAA-Z",
        name = "New subcollection-of relation"
)
public class SubCollectionOfCommand extends PatternCommand {
    
    private final IRI subCollectionOfRelation = IRI.create(GufoIris.GUFO, "isSubCollectionOf");
    private IRI subcollection;
    private IRI collection;

    public void setSubcollection(IRI subcollection) {
        this.subcollection = subcollection;
    }

    public void setCollection(IRI collection) {
        this.collection = collection;
    }
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        applier.createRelation(subCollectionOfRelation, subcollection, collection);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String input =
                JOptionPane.showInputDialog(getOWLWorkspace(),
                    "Input: \"<subCollection: Collection> <Collection>\"." + System.lineSeparator()
                    + "Example: \"SpadeCards Deck\".")
                .trim();
        String[] names = input.split(" ");
        subcollection = IRI.create(getOntologyPrefix(), names[0]);
        collection = IRI.create(getOntologyPrefix(), names[1]);

        try {
            PatternApplier applier = new PatternApplier(getOWLModelManager());
            if (applier.isInstanceOf(GufoIris.Collection, subcollection) &&
                applier.isInstanceOf(GufoIris.Collection, collection)) {
                runCommand();
            } else {
                showMessage("Only collections can be subcollection of another collections.");
            }
        } catch (Exception ex) {
            Logger.getLogger(SubCollectionOfCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
