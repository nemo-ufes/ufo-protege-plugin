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
        id = "menuItemMemberOf",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.ForRelationsMenu/SlotAA-Z",
        name = "New member-of relation"
)
public class MemberOfCommand extends PatternCommand {

    private final IRI memberOfRelation = IRI.create(GufoIris.GUFO, "isCollectionMemberOf");
    private IRI member;
    private IRI collection;

    public void setMember(IRI member) {
        this.member = member;
    }

    public void setCollection(IRI collection) {
        this.collection = collection;
    }
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        applier.createRelation(memberOfRelation, member, collection);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        String input =
                JOptionPane.showInputDialog(getOWLWorkspace(),
                    "Input: \"<member: Object> <Collection>\"." + System.lineSeparator()
                    + "Example: \"JonForeman Switchfoot\".")
                .trim();
        String[] names = input.split(" ");
        member = IRI.create(getOntologyPrefix(), names[0]);
        collection = IRI.create(getOntologyPrefix(), names[1]);

        try {
            PatternApplier applier = new PatternApplier(getOWLModelManager());
            if (applier.isInstanceOf(GufoIris.Object, member) &&
                applier.isInstanceOf(GufoIris.Collection, collection)) {
                runCommand();
            } else {
                showMessage("Only objects can be member of collections.");
            }
        } catch (Exception ex) {
            Logger.getLogger(MemberOfCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
