/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.validation.rules;

import br.ufes.inf.nemo.ufo.protege.validation.ClassRule;
import br.ufes.inf.nemo.ufo.protege.validation.RuleInfo;
import br.ufes.inf.nemo.ufo.protege.validation.helpers.ObjectGraphNode;
import java.util.Optional;

/**
 *
 * @author jeferson
 */
@RuleInfo(
    label = "Sortals must specialize one kind",
    description = "As a sortal, the {sortalType:lc} {} must specialize one kind."
        // SINGLE KIND
)
public class OnlyAKindRule extends ClassRule {

    @Override
    public boolean isAppliable() {
        return classNode().isInstanceOf(Sortal)
            && !classNode().isInstanceOf(Kind);
    }

    @Override
    public void validate() {
        if (!classNode().properAncestors().anyMatch(
                node -> node.isInstanceOf(Kind))) {
            Optional<ObjectGraphNode> sortalType = classNode()
                    .types()
                    .filter(type -> type.isSubclassOf(Sortal))
                    .findFirst();
            setField("sortalType", sortalType.get());
            newViolation();
        }
    }
}
