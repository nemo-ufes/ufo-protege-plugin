/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.validation.rules;

import static br.ufes.inf.nemo.ufo.protege.GufoIris.NonSortal;
import br.ufes.inf.nemo.ufo.protege.validation.ClassRule;
import br.ufes.inf.nemo.ufo.protege.validation.RuleInfo;

/**
 *
 * @author jeferson
 */
@RuleInfo(
    label="Nonsortals cannot specialize sortals",
    description="As a non-sortal, {} cannot specialize a sortal ({sortals})"
)
public class SortalityRule extends ClassRule {

    @Override
    public boolean isAppliable() {
        return classNode().isInstanceOf(NonSortal);
    }

    @Override
    public void validate() {
        forbidAncestors(Sortal, "sortals");
    }
}
