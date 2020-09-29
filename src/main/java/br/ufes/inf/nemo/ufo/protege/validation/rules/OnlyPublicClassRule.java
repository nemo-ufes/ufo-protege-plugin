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
 * @author luciano
 */
@RuleInfo(
        label="Classes extending internal UFO types",
        description="Some UFO types are not meant to be specialized by "
                + "domain ontologies."
)
public class OnlyPublicClassRule extends ClassRule {

    @Override
    public void validate() {
        when(classNode().isDirectSubclassOfAny(nonPublicClasses))
        .registerViolation();
    }
}