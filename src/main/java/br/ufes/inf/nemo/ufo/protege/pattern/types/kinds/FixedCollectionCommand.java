/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.types.kinds;

import br.ufes.inf.nemo.protege.annotations.EditorKitMenuAction;
import br.ufes.inf.nemo.ufo.protege.GufoIris;

/**
 *
 * @author jeferson
 */
@EditorKitMenuAction(
        id = "menuItemFixedCollection",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.NewObjectKindMenu/SlotA-02",
        name = "New gufo:FixedCollection kind"
)
public class FixedCollectionCommand extends KindCommand {
    
    @Override
    public void defineEndurantClass() {
        super.endurantClass = GufoIris.FixedCollection;
        super.endurantClassName = "gufo:FixedCollection";
    }
}
