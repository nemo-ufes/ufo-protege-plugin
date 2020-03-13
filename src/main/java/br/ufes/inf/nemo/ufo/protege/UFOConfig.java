/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege;

import br.ufes.inf.nemo.protege.annotations.EditorKitHook;
import br.ufes.inf.nemo.ufo.protege.treeview.HierarchyNode;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.protege.editor.core.ModelManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.parameters.Imports;

/**
 *
 * @author luciano
 */
@EditorKitHook(id = "ufopp.hook")
public class UFOConfig extends AbstractEditorKitHook {

    private Set<String> publicUFOClasses;
    private Map<String, HierarchyNode> ufoHierarchyView;

    @Override
    public void initialise() throws Exception {
        super.initialise();
        initializePublicUFOClassesSet();
    }

    public static UFOConfig get(ModelManager modelManager) {
        return AbstractEditorKitHook.get(modelManager, UFOConfig.class);
    }

    private void initializePublicUFOClassesSet() throws Exception {

        publicUFOClasses = new HashSet<>();
        ufoHierarchyView = new HashMap<>();

        new Util() {

            Pattern pattern;
            Consumer<Matcher> onMatch;
            Map<String, String> prefixes = new HashMap<>();
            List<Integer> indentations = Lists.newArrayList(-1);
            HierarchyNode last;
            int level = 0;
            int currentState = -1;

            String lastIRI = "";

            Set<String> nonPublicUFOClasses = new HashSet<>();

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
                    pattern("\\s*(-|\\+)\\s+(.*?):(.*?)\\s*");
                    onMatch((Matcher matcher) -> {
                        boolean isPublic = matcher.group(1).equals("+");
                        String namespace = prefixes.get(matcher.group(2));
                        String suffix = matcher.group(3);
                        String iri = namespace + suffix;

                        Set<String> targetSet, otherSet;
                        if (isPublic) {
                            targetSet = publicUFOClasses;
                            otherSet = nonPublicUFOClasses;
                        } else {
                            targetSet = nonPublicUFOClasses;
                            otherSet = publicUFOClasses;
                        }
                        if (otherSet.contains(iri)) {
                            throw new RuntimeException(
                                String.format("Unexpected internal error. IRI '%s' is being declared inconsistently as public and non public gUFO class in ufo-config file", iri));
                        }
                        targetSet.add(iri);
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
                        children.add(thisIRI);
                        HierarchyNode previous = ufoHierarchyView.put(thisIRI,
                                new HierarchyNode(thisIRI, parentIRI, index));
                        if (previous != null) {
                            throw new RuntimeException(
                                    "Internal error. Class is being declared in more than one place in hierarchy view.");
                        }
                        lastIRI = thisIRI;
                    });
                })
            };

            public void run() throws Exception {

                ufoHierarchyView.put("", new HierarchyNode("", "", 0));
                Predicate<String> skipLine
                        = Pattern.compile("^\\s*(--|#|$)").asPredicate();
                readLines("ufo-config", lines -> {
                    lines
                            // Check for state change
                            .peek(line -> {
                                if (line.startsWith("--")) {
                                    states[++currentState].run();
                                }
                            })
                            // Skip comment, blank and state changing lines
                            .filter(skipLine.negate())
                            // Process lines
                            .map(line -> {
                                Matcher matcher = pattern.matcher(line);
                                if (!matcher.matches()) {
                                    throw new RuntimeException(
                                            String.format("Unexpected internal error. gufo-config file error. Line with error follows.\n%s\n", line));
                                }
                                return matcher;
                            })
                            .forEach(matcher -> onMatch.accept(matcher))
                            ;

                });
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
        return ufoHierarchyView.get("").isParentOf(owlClass.getIRI().toString());
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

    public boolean isUFOViewClass(OWLClass n) {
        return ufoHierarchyView.containsKey(n.getIRI().toString());
    }

    public void getUFOViewParents(OWLOntology ontology,
            OWLClass n, Set<OWLClass> result) {
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

    public boolean isNonLeafUFOViewClass(OWLClass owlClass) {
        HierarchyNode node = ufoHierarchyView.get(owlClass.getIRI().toString());
        return node != null && !node.getChildren().isEmpty();
    }

    public Set<OWLClass> getUFOViewChildren(
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

    public int compareOWLObjects(OWLObject a, OWLObject b) {
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
