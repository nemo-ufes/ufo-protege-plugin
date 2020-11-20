/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.validation.rules;

import br.ufes.inf.nemo.ufo.protege.validation.ClassRule;
import br.ufes.inf.nemo.ufo.protege.validation.RuleInfo;

/**
 *
 * @author luciano
 */
@RuleInfo(
        label="Endurant specializations not instantiating EndurantType",
        description =
            "Every Endurant specialization should instantiate EndurantType"
)
public class EndurantSubclassRule  extends ClassRule {

    @Override
    public void validate() {
        when(classNode().isSubclassOf(Endurant))
        .and(!classNode().isInstanceOf(EndurantType))
        .registerViolation();
    }
}
