/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.validation;

import java.util.Map;
import org.semanticweb.owlapi.model.OWLObject;

/**
 * Single violation of a rule.
 *
 * @param <R>
 */
public class Violation<T extends OWLObject> {

    private final Map<String, Object> arguments;
    private final Rule<T> rule;

    protected Violation(final Rule<T> rule, Map<String, Object> arguments) {
        this.rule = rule;
        assert arguments.get("") == rule.getTarget();
        this.arguments = arguments;
    }

    public Rule<T> getRule() {
        return (Rule<T>) rule;
    }

    public Map<String, Object> getArguments() {
        return arguments;
    }

    public T getSubject() {
        return (T) arguments.get("");
    }
}
