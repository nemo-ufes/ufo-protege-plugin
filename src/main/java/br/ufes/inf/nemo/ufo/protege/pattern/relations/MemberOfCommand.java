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

    @Override
    public void actionPerformed(ActionEvent ae) {
        String input =
                JOptionPane.showInputDialog(getOWLWorkspace(), "Type two names: ")
                .trim();
        String[] names = input.split(" ");
        IRI member = IRI.create(getOntologyPrefix(), names[0]);
        IRI collection = IRI.create(getOntologyPrefix(), names[1]);
        IRI memberOfRelation = IRI.create(GufoIris.GUFO, "isCollectionMemberOf");
        
        try {
            PatternApplier applier = new PatternApplier(getOWLModelManager());
            if (applier.isInstanceOf(GufoIris.Object, member) &&
                applier.isInstanceOf(GufoIris.Collection, collection)) {
                applier.createRelation(memberOfRelation, member, collection);
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
