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
import br.ufes.inf.nemo.ufo.protege.pattern.ui.SubKindPatternFrame;
import java.awt.event.ActionEvent;
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
    
    private IRI rigidSortal;
    private IRI subkind;

    public void setRigidSortal(IRI rigidSortal) {
        this.rigidSortal = rigidSortal;
    }

    public void setSubKind(IRI subkind) {
        this.subkind = subkind;
    }
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        applier.createNamedIndividual(subkind);
        applier.makeInstanceOf(GufoIris.SubKind, subkind);
        applier.createClass(subkind);
        applier.addSubClassTo(rigidSortal, subkind);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        
        EntityFilter criterion = new EntityFilter(getOWLModelManager());
        criterion.addType(GufoIris.RigidType);
        criterion.addType(GufoIris.Sortal);
        
        SubKindPatternFrame frame = new SubKindPatternFrame(this);
        frame.setRigidSortalIRIs(criterion.filterEntities());
        frame.display();
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
