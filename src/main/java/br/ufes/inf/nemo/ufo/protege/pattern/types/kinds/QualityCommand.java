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
        id = "menuItemQuality",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.CreateAspectKindMenu/SlotA-01",
        name = "Create gufo:Kind specializing gufo:Quality"
)
public class QualityCommand extends KindCommand {
    
    @Override
    public void defineEndurantClass() {
        super.endurantClass = GufoIris.Quality;
        super.endurantClassName = "gufo:Quality";
    }
}
