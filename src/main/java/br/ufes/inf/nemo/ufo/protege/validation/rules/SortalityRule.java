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
        label="No non-sortal type specializing sortal type"
)
public class SortalityRule extends ClassRule {

    @Override
    public void validate() {
        when(classNode().isInstanceOf(NonSortal))
                .and(classNode().ancestors()
                .anyMatch(node -> node.isInstanceOf(Sortal)))
        .registerViolation();
    }   
}