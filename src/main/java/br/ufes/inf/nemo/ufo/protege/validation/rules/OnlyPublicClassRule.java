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
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author luciano
 */
@RuleInfo(
        label="Classes should not extend 'internal' gUFO classes",
        description="{} should specialize a more specific type of "
                + "{internalType} ({internalType:public})"
)
public class OnlyPublicClassRule extends ClassRule {
    /* types instantiate */
    @Override
    public void validate() {

        Set<ObjectGraphNode> nonPublicParents = classNode()
                .parents()
                .filter(node -> nonPublicClasses.contains(node.getIRI()))
                .collect(Collectors.toSet());

        if (!nonPublicParents.isEmpty()) {
            setField("internalType", highestParent(nonPublicParents));
            newViolation();
        }
    }

    private ObjectGraphNode highestParent(Set<ObjectGraphNode> nonPublicParents) {
        // Try to find the highest node in topology
        if (nonPublicParents.size() > 1) {
            Set<IRI> iris = nonPublicParents
                    .stream()
                    .map(node -> node.getIRI())
                    .collect(Collectors.toSet())
                    ;
            nonPublicParents = nonPublicParents
                    .stream()
                    .filter(node -> node.isProperSubclassOfAny(iris))
                    .collect(Collectors.toSet())
                    ;
        }
        return nonPublicParents.stream().findAny().get();
    }
}
