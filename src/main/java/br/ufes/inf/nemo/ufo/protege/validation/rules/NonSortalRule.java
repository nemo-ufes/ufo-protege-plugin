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
        label="Not instantiable nonsortals",
        description="As a non-sortal, {} should be specialized by a "
                + "sortal (or specialize another non-sortal which is "
                + "in its turn specialized by a sortal)."
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
    public boolean isAppliable() {
        return classNode().isInstanceOf(NonSortal);
    }

    @Override
    public void validate() {
        if (!classNode().ancestors().anyMatch(this::isSubclassedBySortal)) {
            newViolation();
        }
    }
}
