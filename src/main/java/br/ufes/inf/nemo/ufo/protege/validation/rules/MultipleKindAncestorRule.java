/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.validation.rules;

import br.ufes.inf.nemo.ufo.protege.validation.ClassRule;
import br.ufes.inf.nemo.ufo.protege.validation.RuleInfo;
import br.ufes.inf.nemo.ufo.protege.validation.helpers.ObjectGraphNode;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author jeferson
 */
@RuleInfo(
    label = "Sortals must specialize only one kind",
    description = "The {sortalType:lc} {} must specialize only one kind, but "
            + "it is specializing many ({kinds})."
        // SINGLE KIND
)
public class MultipleKindAncestorRule extends ClassRule {

    @Override
    public boolean isAppliable() {
        return classNode().isInstanceOf(Sortal)
            && !classNode().isInstanceOf(Kind);
    }

    @Override
    public void validate() {
        Set<ObjectGraphNode> kindAncestors = classNode()
                .properAncestors()
                .filter(node -> node.isInstanceOf(Kind))
                .collect(Collectors.toSet());
        if (kindAncestors.size() > 1) {
            Set<ObjectGraphNode> sortalTypes = classNode()
                    .types()
                    .filter(node -> node.isSubclassOf(Sortal))
                    .collect(Collectors.toSet())
                    ;
            setField("sortalType", ObjectGraphNode
                    .closestAncestors(sortalTypes).iterator().next());
            setField("kinds", kindAncestors);
            newViolation();
        }
    }
}
