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
    label="Rigid types specializing anti-rigid ones",
    description="Rigid types cannot specialize anti-rigid ones"
    // "t8: ¬∃x,y (Rigid(x) ∧ AntiRigid(y) ∧ x ⊑ y)"
)
public class RigidityRule extends ClassRule {

    @Override
    public void validate() {
        when(classNode().isInstanceOf(RigidType))
                .and(classNode().ancestors()
                .anyMatch(node -> node.isInstanceOf(AntiRigidType)))
        .registerViolation();
    }
}
