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
import br.ufes.inf.nemo.ufo.protege.pattern.ui.relations.SubQuantityOfPatternFrame;
import java.awt.event.ActionEvent;
import java.util.List;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
@EditorKitMenuAction(
        id = "menuItemSubQuantityOf",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.ForRelationsMenu/SlotC-04",
        name = "New subquantity-of relation"
)
public class SubQuantityOfCommand extends PatternCommand {

    private final IRI subQuantityOfRelation = IRI.create(GufoIris.GUFO, "isSubQuantityOf");
    private IRI subquantity;
    private IRI quantity;

    public void setSubQuantity(IRI subquantity) {
        this.subquantity = subquantity;
    }

    public void setQuantity(IRI quantity) {
        this.quantity = quantity;
    }
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        applier.assertObjectProperty(subQuantityOfRelation, subquantity, quantity);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        List<IRI> quantityIRIs = new EntityFilter(getOWLModelManager())
                .addType(GufoIris.Quantity)
                .entities();
        
        IRI firstQuantity = quantityIRIs.isEmpty() ? null : quantityIRIs.get(0);
        List<IRI> subquantityIRIs = new EntityFilter(getOWLModelManager())
                .addType(GufoIris.Quantity)
                .isDifferentFrom(firstQuantity)
                .entities();
        
        SubQuantityOfPatternFrame frame = new SubQuantityOfPatternFrame(this);
        frame.setQuantityIRIs(quantityIRIs);
        frame.setSubQuantityIRIs(subquantityIRIs);
        frame.display();
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
