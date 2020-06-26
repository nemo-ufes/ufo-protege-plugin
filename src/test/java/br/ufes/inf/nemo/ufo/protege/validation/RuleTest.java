/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.validation;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import static org.mockito.Mockito.*;
import org.protege.editor.core.Disposable;
import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.parameters.Imports;

/**
 *
 * @author luciano
 */

public class RuleTest {

    private final EditorKit editorKit;
    private final OWLModelManager owlModelManager;
    private final Map<Object, Disposable> disposableMap;
    private final Validator validator;
    private final OWLOntology activeOntology;
    private final Set<OWLOntology> activeOntologies;
    private final Map<String, Class<? extends Rule>> ruleClasses;

    public RuleTest() {
        this.editorKit = mock(EditorKit.class);
        this.owlModelManager = mock(OWLModelManager.class);
        this.disposableMap = new HashMap<>();
        this.validator = new Validator();

        try {
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            loadOntology(manager, "gufo.ttl");
            activeOntology = loadOntology(manager, "rule-tests.ttl");
            activeOntologies = manager.getOntologies();
        } catch (OWLOntologyCreationException ex) {
            throw new RuntimeException(ex);
        }

        // Mock access to model manager
        when(editorKit.getModelManager()).thenReturn(owlModelManager);
        // Mock access to get and put methods of model manager
        doAnswer(
            iom -> disposableMap.put(iom.getArgument(0), iom.getArgument(1))
        ).when(owlModelManager).put(any(), any());
        when(owlModelManager.get(any())).thenAnswer(
            iom -> disposableMap.get(iom.getArgument(0))
        );
        when(owlModelManager.getActiveOntology())
            .thenReturn(activeOntology);
        when(owlModelManager.getActiveOntologies())
            .thenReturn(activeOntologies);

        try {
            validator.initialise(editorKit);
            ruleClasses = validator
                    .ruleConstructors()
                    .map(constructor -> constructor.getDeclaringClass())
                    .collect(Collectors.toMap(
                        ruleClass -> ruleClass.getSimpleName(),
                        ruleClass -> ruleClass))
                    ;
        } catch (Exception  ex) {
            throw new RuntimeException(ex);
        }
    }

    @TestFactory
    public Stream<DynamicTest> tests() {

        OWLAnnotationProperty violates =
                activeOntology.getEntitiesInSignature(IRI.create(
                        "local://ufo-protege-plugin/unit-testing#violates"))
                .iterator()
                .next()
                .asOWLAnnotationProperty()
                ;

        Map<String, List<OWLAnnotationAssertionAxiom>> annotationsByRuleName =
            activeOntology
                .getAxioms(AxiomType.ANNOTATION_ASSERTION, Imports.EXCLUDED)
                .stream()
                .filter(axiom -> axiom.getSubject().isIRI())
                .filter(axiom -> violates.equals(axiom.getProperty()))
                .collect(Collectors.groupingBy(axiom ->
                        axiom.getValue().asLiteral().get().getLiteral()))
                ;

        {
            Set<String> notFoundRules = new HashSet<>(
                    annotationsByRuleName.keySet());
            notFoundRules.removeAll(ruleClasses.keySet());
            if (!notFoundRules.isEmpty()) {
                Assertions.fail("The following set of rule classes has " +
                        "not been loaded by RuleLoader: " + notFoundRules);

            }
        }

        final List<OWLAnnotationAssertionAxiom> EMPTY_LIST =
                Collections.EMPTY_LIST;

        return ruleClasses
                .entrySet()
                .stream()
                .map(entry -> {
                    final String ruleClassName = entry.getKey();
                    return DynamicTest.dynamicTest(ruleClassName, () -> {

                        Class<? extends Rule> ruleClass = entry.getValue();

                        Set<String> expectedViolators = annotationsByRuleName
                                .getOrDefault(ruleClassName, EMPTY_LIST)
                                .stream()
                                .map(OWLAnnotationAssertionAxiom::getSubject)
                                .map(subject -> (IRI) subject)
                                .map(IRI::getFragment)
                                .collect(Collectors.toSet())
                                ;
                        Result result =
                                Validation.on(owlModelManager, ruleClass);
                        Set<String> realViolators = result.getViolations()
                                .stream()
                                .map(violation -> violation.getSubject())
                                .map(obj -> ((OWLClass)obj).getIRI().getFragment())
                                .collect(Collectors.toSet())
                                ;

                        checkViolators(ruleClassName,
                                expectedViolators, realViolators);
                    });
                });
    }

    private OWLOntology loadOntology(
            OWLOntologyManager manager, String resourceName)
            throws OWLOntologyCreationException {
        return manager.loadOntologyFromOntologyDocument(
                getClass().getResourceAsStream(resourceName));
    }

    private void checkViolators(String ruleClassName,
            Set<String> expected, Set<String> found) {
        if (expected.equals(found)) {
            return;
        }
        final StringBuilder message = new StringBuilder();
        message.append("Test failure for rule ");
        message.append(ruleClassName);
        message.append(".");
        {
            Set<String> unexpectedViolators = new HashSet<>(found);
            unexpectedViolators.removeAll(expected);
            if (!unexpectedViolators.isEmpty()) {
                message.append(" The following classes were NOT expected to ");
                message.append("generate any violation: ");
                message.append(unexpectedViolators);
                message.append(".");
            }
        }
        {
            Set<String> unexpectedConformants = new HashSet<>(expected);
            unexpectedConformants.removeAll(found);
            if (!unexpectedConformants.isEmpty()) {
                message.append(" The following classes were expected to ");
                message.append("generate violations, but haven't: ");
                message.append(unexpectedConformants);
                message.append(".");
            }
        }

        Assertions.fail(message.toString());
    }
}
