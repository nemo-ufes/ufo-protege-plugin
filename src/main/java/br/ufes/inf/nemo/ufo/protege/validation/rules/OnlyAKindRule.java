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
    label = "Sortals not specializing exactly one kind",
    description = "Every sortal must be a kind or specialize exactly one kind"
)
public class OnlyAKindRule extends ClassRule {

    @Override
    public void validate() {
        // Every sortal type...
        when(classNode().isInstanceOf(Sortal))
        .and(!(
            // ...must be a kind type...
            classNode().isInstanceOf(Kind)
            || // ...or...
            // specialize exactly one kind type
            classNode().properAncestors()
                .filter(node -> node.isInstanceOf(Kind))
                .count() == 1
        ))
        .registerViolation();
    }
}
