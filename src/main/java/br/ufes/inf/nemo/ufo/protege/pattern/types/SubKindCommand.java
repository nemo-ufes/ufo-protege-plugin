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
import br.ufes.inf.nemo.ufo.protege.pattern.ui.types.SubKindPatternFrame;
import java.awt.event.ActionEvent;
import java.util.List;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
@EditorKitMenuAction(
        id = "menuItemSubKind",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.ForTypesMenu/SlotA-02",
        name = "Add a gufo:SubKind to a rigid sortal"
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
        
        List<IRI> rigidSortalIRIs = new EntityFilter(getOWLModelManager())
                .addType(GufoIris.RigidType)
                .addType(GufoIris.Sortal)
                .entities();
        
        SubKindPatternFrame frame = new SubKindPatternFrame(this);
        frame.setRigidSortalIRIs(rigidSortalIRIs);
        frame.display();
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
