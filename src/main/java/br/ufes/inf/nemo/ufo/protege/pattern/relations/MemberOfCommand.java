/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.relations;

import br.ufes.inf.nemo.protege.annotations.EditorKitMenuAction;
import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.EntityFilter;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternApplier;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.ui.relations.MemberOfPatternFrame;
import java.awt.event.ActionEvent;
import java.util.List;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
/* @EditorKitMenuAction(
        id = "menuItemMemberOf",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.ForRelationsMenu/SlotA-01",
        name = "New gufo:isCollectionMemberOf relation"
) */
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
        applier.assertObjectProperty(memberOfRelation, member, collection);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        List<IRI> collectionIRIs = new EntityFilter(getOWLModelManager())
                .isOfType(GufoIris.Collection)
                .entities();
        
        IRI firstCollection = collectionIRIs.isEmpty() ? null : collectionIRIs.get(0);
        List<IRI> memberIRIs = new EntityFilter(getOWLModelManager())
                .isOfType(GufoIris.Object)
                .isDifferentFrom(firstCollection)
                .entities();
        
        MemberOfPatternFrame frame = new MemberOfPatternFrame(this);
        frame.setCollectionIRIs(collectionIRIs);
        frame.setObjectIRIs(memberIRIs);
        frame.display();
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
