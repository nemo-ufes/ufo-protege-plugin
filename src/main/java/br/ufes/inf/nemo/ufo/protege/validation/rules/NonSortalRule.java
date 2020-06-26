/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.validation.rules;

import br.ufes.inf.nemo.ufo.protege.validation.ClassRule;
import br.ufes.inf.nemo.ufo.protege.validation.RuleInfo;
import br.ufes.inf.nemo.ufo.protege.validation.helpers.ObjectGraphNode;

/**
 *
 * @author luciano
 */
@RuleInfo(
        label="Not instantiable NonSortal",
        description="Every NonSortal must be specialized by a Sortal or "
                + "specialize a NonSortal which is specialized by a Sortal"
)
public class NonSortalRule extends ClassRule {

    private boolean isSubclassedBySortal(ObjectGraphNode node) {
        return
            node.isInstanceOf(NonSortal) &&
            node.descendants().anyMatch(
                    descendant -> descendant.isInstanceOf(Sortal))
            ;
    }

    @Override
    public void validate() {
        when(classNode().isInstanceOf(NonSortal))
        .and(!classNode().ancestors().anyMatch(this::isSubclassedBySortal))
        .registerViolation();
    }
}
