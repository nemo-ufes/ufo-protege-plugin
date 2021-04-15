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
        id = "menuItemExtrinsicMode",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.CreateAspectKindMenu/SlotA-03",
        name = "Create gufo:Kind specializing gufo:ExtrinsicMode"
)
public class ExtrinsicModeCommand extends KindCommand {
    
    @Override
    public void defineEndurantClass() {
        super.endurantClass = GufoIris.ExtrinsicMode;
        super.endurantClassName = "gufo:ExtrinsicMode";
    }
}
