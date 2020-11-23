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
    label = "Semi-rigid types specializing anti-rigid ones",
    description = "{} is a semi-rigid type and, hence, cannot specialize any "
            + "anti-rigid type ({antirigid})"
)
public class SemiRigidityRule extends ClassRule {

    @Override
    public boolean isAppliable() {
        return classNode().isInstanceOf(SemiRigidType);
    }

    @Override
    public void validate() {
        forbidAncestors(AntiRigidType, "antirigid");
    }
}
