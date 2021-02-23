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
import br.ufes.inf.nemo.ufo.protege.pattern.ui.relations.SubCollectionOfPatternFrame;
import java.awt.event.ActionEvent;
import java.util.List;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
@EditorKitMenuAction(
        id = "menuItemSubCollectionOf",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.ForRelationsMenu/SlotA-02",
        name = "New gufo:isSubCollectionOf relation"
)
public class SubCollectionOfCommand extends PatternCommand {
    
    private final IRI subCollectionOfRelation = IRI.create(GufoIris.GUFO, "isSubCollectionOf");
    private IRI subcollection;
    private IRI collection;

    public void setSubCollection(IRI subcollection) {
        this.subcollection = subcollection;
    }

    public void setCollection(IRI collection) {
        this.collection = collection;
    }
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        applier.assertObjectProperty(subCollectionOfRelation, subcollection, collection);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        List<IRI> collectionIRIs = new EntityFilter(getOWLModelManager())
                .addType(GufoIris.Collection)
                .entities();
        
        IRI firstCollection = collectionIRIs.isEmpty() ? null : collectionIRIs.get(0);
        List<IRI> subcollectionIRIs = new EntityFilter(getOWLModelManager())
                .addType(GufoIris.Collection)
                .isDifferentFrom(firstCollection)
                .entities();
        
        SubCollectionOfPatternFrame frame = new SubCollectionOfPatternFrame(this);
        frame.setCollectionIRIs(collectionIRIs);
        frame.setSubCollectionIRIs(subcollectionIRIs);
        frame.display();
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
