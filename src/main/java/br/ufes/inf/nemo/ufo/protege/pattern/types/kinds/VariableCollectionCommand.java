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
        id = "menuItemVariableCollection",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.NewObjectKindMenu/SlotA-03",
        name = "New gufo:VariableCollection kind"
)
public class VariableCollectionCommand extends KindCommand {
    
    @Override
    public void defineEndurantClass() {
        super.endurantClass = GufoIris.VariableCollection;
        super.endurantClassName = "gufo:VariableCollection";
    }
}
