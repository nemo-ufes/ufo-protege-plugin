/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.validation.rules;

import br.ufes.inf.nemo.ufo.protege.validation.Rule;
import br.ufes.inf.nemo.ufo.protege.validation.RuleInfo;
import br.ufes.inf.nemo.ufo.protege.validation.Validation;
import java.util.Collections;
import java.util.Set;

/**
 *
 * @author luciano
 */
@RuleInfo(
        label="Extending internal UFO type"
)
public class OnlyPublicClassRule extends Rule {

    @Override
    public Set<Violation> validate(Validation ontology) {
        return Collections.EMPTY_SET;
    }
}
