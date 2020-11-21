/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.validation.rules;

import static br.ufes.inf.nemo.ufo.protege.GufoIris.Kind;
import static br.ufes.inf.nemo.ufo.protege.GufoIris.Sortal;
import br.ufes.inf.nemo.ufo.protege.validation.ClassRule;
import br.ufes.inf.nemo.ufo.protege.validation.RuleInfo;

/**
 *
 * @author luciano
 */
@RuleInfo(
        label = "Kinds  specializing sortals",
        description = "A kind cannot specialize a sortal"
)
public class KindCannotSubclassSortalRule extends ClassRule {

    @Override
    public void validate() {
        when(classNode().isInstanceOf(Kind))
        .and(classNode().properAncestors()
                .anyMatch(node -> node.isInstanceOf(Sortal)))
        .registerViolation();
    }
}
