/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.validation;

import br.ufes.inf.nemo.ufo.protege.validation.solution.ProposedSolution;
import java.util.ArrayList;
import java.util.List;
import org.semanticweb.owlapi.model.OWLObject;

/**
 * Single violation of a rule.
 *
 * @param <R>
 */
public class Violation<T extends OWLObject> {

    private final OWLObject[] arguments;
    private final Rule<T> rule;
    private final List<ProposedSolution> solutions;

    protected Violation(final Rule<T> rule, OWLObject... arguments) {
        this.rule = rule;
        assert arguments[0] == rule.getTarget();
        this.arguments = arguments;
        this.solutions = new ArrayList<>();
    }

    public Rule<T> getRule() {
        return (Rule<T>) rule;
    }

    public OWLObject[] getArguments() {
        return arguments;
    }

    public T getSubject() {
        return (T) arguments[0];
    }

    public List<ProposedSolution> getSolutions() {
        return solutions;
    }

    public boolean addProposal(ProposedSolution e) {
        return solutions.add(e);
    }
}
