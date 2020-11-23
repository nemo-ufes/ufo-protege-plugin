/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.validation;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 *
 * @author luciano
 */
public class Result {

    private final OWLOntology targetOntology;
    private final Set<OWLOntology> allOntologies;
    private final Set<Violation> violations;

    public Result(OWLOntology targetOntology, Set<OWLOntology> allOntologies, Set<Violation> violations) {
        this.targetOntology = targetOntology;
        this.allOntologies = allOntologies;
        this.violations = violations;
    }

    public OWLOntology getTargetOntology() {
        return targetOntology;
    }

    public Set<OWLOntology> getAllOntologies() {
        return allOntologies;
    }

    public Set<Violation> getViolations() {
        return violations;
    }

    @Override
    public String toString() {
        return violations
            .stream()
            .map(violation ->
                Stream.concat(
                    Stream.of(violation.getRule().getClass().getSimpleName()),
                    violation.getSubject()
                        .getSignature().stream()
                        .map(entity -> entity.getIRI().getShortForm())
                )
                .collect(Collectors.joining(": ", "(", ")"))
            )
            .collect(Collectors.joining("; ", "{", "}"))
            ;
    }
}