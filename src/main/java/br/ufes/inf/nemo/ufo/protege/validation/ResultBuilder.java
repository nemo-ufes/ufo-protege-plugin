/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.validation;

import java.util.function.Supplier;
import org.semanticweb.owlapi.model.OWLObject;

/**
 *
 * @author luciano
 */
public class ResultBuilder<T extends OWLObject> {

    boolean result = true;
    private final Rule<T> rule;

    ResultBuilder(boolean b, final Rule<T> rule) {
        this.rule = rule;
        result = b;
    }

    public ResultBuilder and(boolean b) {
        result &= b;
        return this;
    }

    public ResultBuilder or(boolean b) {
        result |= b;
        return this;
    }

    public ResultBuilder and(Supplier<Boolean> s) {
        result &= s.get();
        return this;
    }

    public ResultBuilder or(Supplier<Boolean> s) {
        result |= s.get();
        return this;
    }

    public ResultBuilder registerViolationFor(OWLObject... arguments) {
        if (result) {
            rule.newViolation(arguments);
        }
        return this;
    }

    public ResultBuilder registerViolation() {
        if (result) {
            rule.newViolation(rule.getTarget());
        }
        return this;
    }
}
