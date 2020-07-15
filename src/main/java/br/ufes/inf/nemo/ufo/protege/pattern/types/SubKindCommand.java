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
import br.ufes.inf.nemo.ufo.protege.pattern.ui.PatternApplicationFrame;
import java.awt.event.ActionEvent;

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
        
        EntityFilter criterion = new EntityFilter(getOWLModelManager());
        criterion.addType(GufoIris.RigidType);
        criterion.addType(GufoIris.Sortal);
        
        PatternApplicationFrame frame = 
            new PatternApplicationFrame(this, criterion.listEntities());
        frame.display();
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
