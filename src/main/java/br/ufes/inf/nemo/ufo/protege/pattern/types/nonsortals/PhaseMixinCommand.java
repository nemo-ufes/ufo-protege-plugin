/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.types.nonsortals;

import br.ufes.inf.nemo.protege.annotations.EditorKitMenuAction;
import br.ufes.inf.nemo.ufo.protege.GufoIris;

/**
 *
 * @author jeferson
 */
@EditorKitMenuAction(
        id = "menuItemPhaseMixin",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.CreateNonSortalMenu/SlotA-04",
        name = "Create gufo:PhaseMixin"
)
public class PhaseMixinCommand extends NonSortalCommand {

    @Override
    protected void defineNonSortalClass() {
        super.nonsortalClass = GufoIris.PhaseMixin;
        super.nonsortalClassName = "gufo:PhaseMixin";
    }
}
