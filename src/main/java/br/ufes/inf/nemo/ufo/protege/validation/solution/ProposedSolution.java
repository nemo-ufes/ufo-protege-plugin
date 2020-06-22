/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.validation.solution;

import br.ufes.inf.nemo.ufo.protege.validation.Violation;
import java.util.List;

/**
 *
 * @author luciano
 */
public class ProposedSolution {
    private final String title;
    private final Violation violation;
    private final List<ProposedOperation> operations;

    public ProposedSolution(String title, Violation violation,
            List<ProposedOperation> operations) {
        this.title = title;
        this.violation = violation;
        this.operations = operations;
    }
}
