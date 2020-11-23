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
        label="Every user class should specialize a gUFO class",
        description="{} should specialize a gUFO class"
)
public class AtLeastAPublicClassRule extends ClassRule {

    @Override
    public boolean isAppliable() {
        return !classNode().isInstanceOf(NonSortal);
    }

    @Override
    public void validate() {
        if (
                !classNode().isSubclassOfAny(publicClasses) &&
                // Extending internal classes is to be catched by another rule
                !classNode().isSubclassOfAny(nonPublicClasses)
        ) {
            newViolation();
        }
    }
}
