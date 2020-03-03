/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege;

import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.protege.editor.core.ModelManager;
import org.protege.editor.core.editorkit.plugin.EditorKitHook;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.parameters.Imports;

/**
 *
 * @author luciano
 */
@br.ufes.inf.nemo.protege.annotations.EditorKitHook(
        id = "ufopp.hook"
)
public class UFOConfig extends EditorKitHook {

    private ModelManager modelManager;
    private Set<String> publicUFOClasses;
    private Map<String, HierarchyNode> ufoHierarchyView;

    @Override
    public void initialise() throws Exception {
        modelManager = getEditorKit().getModelManager();
        modelManager.put(getClass(), this);
        initializePublicUFOClassesSet();
    }

    @Override
    public void dispose() throws Exception {

    }

    public static UFOConfig get(ModelManager modelManager) {
        final UFOConfig result = modelManager.get(UFOConfig.class);
        if (result == null) {
            throw new RuntimeException(
                    "Unexpected error. UFOConfig object not found");
        }
        return result;
    }

    private void initializePublicUFOClassesSet()
            throws IOException, OWLOntologyCreationException {

        publicUFOClasses = new HashSet<>();
        ufoHierarchyView = new HashMap<>();

        new Object() {

            Pattern pattern;
            Consumer<Matcher> onMatch;
            Map<String, String> prefixes = new HashMap<>();
            List<Integer> indentations = Lists.newArrayList(-1);
            HierarchyNode last;
            int level = 0;
            int currentState = -1;

            String lastIRI = "";

            private void pattern(String pattern) {
                this.pattern = Pattern.compile(pattern);
            }

            private void onMatch(Consumer<Matcher> processMatch) {
                this.onMatch = processMatch;
            }

            Runnable[] states = {
                (Runnable) (() -> {
                    pattern("\\s*@prefix\\s+(.*?):\\s*<(.*?)>\\s*\\.\\s*$");
                    onMatch(matcher -> {
                        prefixes.put(matcher.group(1), matcher.group(2));
                    });
                }),
                (Runnable) (() -> {
                    pattern("(.*?):(.*?)\\s*");
                    onMatch((Matcher matcher) -> {
                        String namespace = prefixes.get(matcher.group(1));
                        String suffix = matcher.group(2);
                        publicUFOClasses.add(namespace + suffix);
                    });
                }),
                (Runnable) (() -> {
                    pattern("(\\s*)(.*?):(.*?)\\s*");
                    onMatch((Matcher matcher) -> {
                        String iri = lastIRI;
                        int indentation = matcher.group(1).length();
                        while (!indentations.isEmpty()) {
                            int lastIndent = indentations.get(
                                    indentations.size() - 1);
                            if (indentation > lastIndent) {
                                indentations.add(indentation);
                                break;
                            }
                            iri = ufoHierarchyView.get(iri).getParentIri();
                            if (indentation == lastIndent) {
                                break;
                            }
                            indentations.remove(indentations.size() - 1);
                        }
                        String namespace = prefixes.get(matcher.group(2));
                        String suffix = matcher.group(3);
                        String thisIRI = namespace + suffix;
                        String parentIRI = iri;

                        HierarchyNode parent = ufoHierarchyView.get(parentIRI);
                        Set<String> children = parent.getChildren();
                        int index = children.size();
                        if (!children.add(thisIRI)) {
                            throw new RuntimeException(
                                    "Internal error. Class is being declared in more than one place in hierarchy view.");
                        }
                        ufoHierarchyView.put(thisIRI,
                                new HierarchyNode(thisIRI, parentIRI, index));
                        lastIRI = thisIRI;
                    });
                })
            };

            public void run() throws IOException {

                ufoHierarchyView.put("", new HierarchyNode("", "", 0));
                try (
                        InputStream inputStream
                            = getClass().getResourceAsStream("ufo-config");
                        InputStreamReader unbuf
                            = new InputStreamReader(inputStream);
                        BufferedReader reader = new LineNumberReader(unbuf);
                ) {
                    Predicate<String> skipLine
                            = Pattern.compile("^\\s*(--|#|$)").asPredicate();

                    Function<String, Consumer<Object>> print = (prefix) ->
                            (o) -> System.out.append(prefix).append(": ").println(o);
                    reader.lines()
                            // Check for state change
                            .peek(line -> {
                                if (line.startsWith("--")) {
                                    states[++currentState].run();
                                }
                            })
                            // Skip comment, blank and state changing lines
                            .filter(skipLine.negate())
                            // Process lines
                            .map(line -> pattern.matcher(line))
                            .filter(matcher -> matcher.matches())
                            .forEach(matcher -> onMatch.accept(matcher))
                            ;
                }
                System.out.println("==== UFO HIERARCHY VIEW ===========");
                System.out.println(UFOConfig.this.hashCode());
                System.out.println("");
                for (Map.Entry<String, HierarchyNode> entry : ufoHierarchyView.entrySet()) {
                    System.out.print(" * ");
                    System.out.println(entry.getKey());
                    HierarchyNode node = entry.getValue();
                    System.out.print("      - Parent: ");
                    System.out.println(node.getParentIri());
                    System.out.print("      - Index: ");
                    System.out.println(node.getIndex());
                    System.out.println("      - Children: ");
                    for (String string : node.getChildren()) {
                        System.out.print("          ");
                        System.out.println(string);
                    }
                }
                System.out.println("==== /UFO HIERARCHY VIEW ===========");
            }
        }.run();
    }

    public boolean isPublicUFOClass(OWLClassExpression owlClass) {
        return owlClass.isNamed() && isPublicUFOClass(owlClass.asOWLClass());
    }

    public boolean isPublicUFOClass(OWLClass owlClass) {
        return publicUFOClasses.contains(owlClass.getIRI().toString());
    }

    public boolean isUFOViewRootClass(OWLClassExpression owlClass) {
        return owlClass.isNamed() && isUFOViewRootClass(owlClass.asOWLClass());
    }

    public boolean isUFOViewRootClass(OWLClass owlClass) {
        return ufoHierarchyView.get("").contains(owlClass.getIRI().toString());
    }

    public Stream<OWLClass> owlClasses(OWLOntology ontology) {
        return ontology
                .getNestedClassExpressions()
                .stream()
                .filter(OWLClassExpression::isNamed)
                .map(OWLClassExpression::asOWLClass)
                ;
    }

    public Stream<OWLClass> publicUFOClasses(OWLOntology ontology) {
        return owlClasses(ontology).filter(this::isPublicUFOClass);
    }

    public Stream<OWLClass> ufoViewRootClasses(OWLOntology ontology) {
        return owlClasses(ontology).filter(this::isUFOViewRootClass);
    }

    boolean isUFOViewClass(OWLClass n) {
        return ufoHierarchyView.containsKey(n.getIRI().toString());
    }

    void getUFOViewParents(OWLOntology ontology, OWLClass n, Set<OWLClass> result) {
        HierarchyNode node = ufoHierarchyView.get(n.getIRI().toString());
        IRI iri = IRI.create(node.getParentIri());
        ontology
                .getEntitiesInSignature(iri, Imports.INCLUDED)
                .stream()
                .filter(OWLEntity::isOWLClass)
                .map(OWLEntity::asOWLClass)
                .forEach(result::add)
                ;
    }

    boolean isNonLeafUFOViewClass(OWLClass owlClass) {
        HierarchyNode node = ufoHierarchyView.get(owlClass.getIRI().toString());
        return node != null && !node.getChildren().isEmpty();
    }

    Set<OWLClass> getUFOViewChildren(
            Collection<OWLOntology> ontologies, OWLClass owlClass) {
        return ufoHierarchyView.get(owlClass.getIRI().toString())
            .getChildren()
            .stream()
            .map(IRI::create)
            .flatMap(iri ->
                ontologies.stream().flatMap(ontology ->
                    ontology.getEntitiesInSignature(iri, Imports.INCLUDED)
                            .stream()
                )
            )
            .filter(OWLEntity::isOWLClass)
            .map(OWLEntity::asOWLClass)
            .collect(Collectors.toCollection(HashSet::new))
            ;
    }

    int compareOWLObjects(OWLObject a, OWLObject b) {
        HierarchyNode nodeA = getHierarchyNode(a);
        HierarchyNode nodeB = getHierarchyNode(b);
        return nodeA == null ? (nodeB == null ? 0 : 1) : nodeA.compareTo(nodeB);
    }

    private HierarchyNode getHierarchyNode(OWLObject object) {
        return !(object instanceof OWLClass) ? null :
                ufoHierarchyView.get(((OWLClass)object).getIRI().toString())
                ;
    }
}
