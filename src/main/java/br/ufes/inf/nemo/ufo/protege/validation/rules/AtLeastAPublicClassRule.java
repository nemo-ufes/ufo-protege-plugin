/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.validation.rules;

import static br.ufes.inf.nemo.ufo.protege.GufoIris.publicClasses;
import br.ufes.inf.nemo.ufo.protege.validation.ClassRule;
import br.ufes.inf.nemo.ufo.protege.validation.RuleInfo;

/**
 *
 * @author luciano
 */
@RuleInfo(
        label="Missing a public UFO supertype or type",
        description="Every class which is not an instance of NonSortal should "
                + "inherit from a public UFO supertype."
)
public class AtLeastAPublicClassRule extends ClassRule {

    @Override
    public void validate() {
        when(!classNode().isInstanceOf(NonSortal))
        .and(!classNode().isSubclassOfAny(publicClasses))
        .registerViolation();
    }
}
