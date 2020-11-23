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
 * @author jeferson
 */
@RuleInfo(
    label="Rigids cannot specialize anti-rigids",
    description="{} is a rigid type and, hence, cannot specialize any "
            + "anti-rigid type ({antirigids})"
)
public class RigidityRule extends ClassRule {

    @Override
    public boolean isAppliable() {
        return classNode().isInstanceOf(RigidType);
    }

    @Override
    public void validate() {
        forbidAncestors(AntiRigidType, "antirigids");
    }
}
