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
import br.ufes.inf.nemo.ufo.protege.pattern.ui.types.RoleMixinOfRoleMixinPatternFrame;
import java.awt.event.ActionEvent;
import java.util.List;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
/* @EditorKitMenuAction(
        id = "menuItemRoleMixinOfRoleMixin",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.ForTypesMenu/SlotE-15",
        name = "Add rolemixin of rolemixin"
) */
public class RoleMixinOfRoleMixinCommand extends PatternCommand {

    private IRI parent;
    private IRI child;

    public void setParent(IRI parent) {
        this.parent = parent;
    }

    public void setChild(IRI child) {
        this.child = child;
    }
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        applier.createNamedIndividual(child);
        applier.makeInstanceOf(GufoIris.RoleMixin, child);
        applier.createClass(child);
        applier.addSubClassTo(parent, child);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        List<IRI> rolemixinIRIs = new EntityFilter(getOWLModelManager())
                .isOfType(GufoIris.RoleMixin)
                .entities();
        
        RoleMixinOfRoleMixinPatternFrame frame = new RoleMixinOfRoleMixinPatternFrame(this);
        frame.setRoleMixinIRIs(rolemixinIRIs);
        frame.display();
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
