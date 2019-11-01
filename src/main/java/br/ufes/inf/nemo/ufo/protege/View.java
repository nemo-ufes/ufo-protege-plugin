/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege;

import br.ufes.inf.nemo.protege.annotations.ViewComponent;
import com.google.common.base.Optional;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import java.awt.BorderLayout;
import static java.awt.BorderLayout.NORTH;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.apache.log4j.Logger;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import static org.fife.ui.rsyntaxtextarea.SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.OWLWorkspaceViewsTab;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.util.OWLAxiomVisitorAdapter;

/**
 *
 * @author luciano
 */
@ViewComponent(
        id = "ufopp.view",
        label = "UFO Validation",
        category = "@org.protege.ontologycategory"
)
public class View extends AbstractOWLViewComponent {

    private static final long serialVersionUID = -4515710047558710080L;

    private static final Logger log = Logger.getLogger(View.class);

    OWLWorkspaceViewsTab workspaceViewsTab = null;

    private OWLModelManagerListener modelListener = event -> {
        switch (event.getType()) {
            case ACTIVE_ONTOLOGY_CHANGED:
                recalculate();
                break;
        }
    };
    private JTextArea textArea;
    private OWLOntology ufoOntology;
    private Set<OWLClass> leafClasses;
    private Set<String> publicUFOClasses;

    private void initializePublicUFOClassesSet() {
        publicUFOClasses = new HashSet<>();
        publicUFOClasses.add("http://purl.org/nemo/ufo#Collection");
        publicUFOClasses.add("http://purl.org/nemo/ufo#Event");
        publicUFOClasses.add("http://purl.org/nemo/ufo#Participation");
        publicUFOClasses.add("http://purl.org/nemo/ufo#Category");
        publicUFOClasses.add("http://purl.org/nemo/ufo#QualityValueAttributionSituation");
        publicUFOClasses.add("http://purl.org/nemo/ufo#Role");
        publicUFOClasses.add("http://purl.org/nemo/ufo#Phase");
        publicUFOClasses.add("http://purl.org/nemo/ufo#TemporaryParthoodSituation");
        publicUFOClasses.add("http://purl.org/nemo/ufo#FunctionalComplex");
        publicUFOClasses.add("http://purl.org/nemo/ufo#EventType");
        publicUFOClasses.add("http://purl.org/nemo/ufo#Mode");
        publicUFOClasses.add("http://purl.org/nemo/ufo#SubKind");
        publicUFOClasses.add("http://purl.org/nemo/ufo#QuaIndividual");
        publicUFOClasses.add("http://purl.org/nemo/ufo#Quantity");
        publicUFOClasses.add("http://purl.org/nemo/ufo#PhaseMixin");
        publicUFOClasses.add("http://www.w3.org/2006/time#Instant");
        publicUFOClasses.add("http://purl.org/nemo/ufo#ContingentInstantiationSituation");
        publicUFOClasses.add("http://purl.org/nemo/ufo#Quality");
        publicUFOClasses.add("http://purl.org/nemo/ufo#QualityValue");
        publicUFOClasses.add("http://purl.org/nemo/ufo#RoleMixin");
        publicUFOClasses.add("http://purl.org/nemo/ufo#Kind");
        publicUFOClasses.add("http://purl.org/nemo/ufo#Relator");
        publicUFOClasses.add("http://purl.org/nemo/ufo#AbstractIndividualType");
        publicUFOClasses.add("http://purl.org/nemo/ufo#SituationType");
        publicUFOClasses.add("http://purl.org/nemo/ufo#Mixin");
    }



    @Override
    protected void initialiseOWLView() throws Exception {

        setLayout(new BorderLayout());
        textArea = new JTextArea("OK");
        JScrollPane textAreaScrollPane = new JScrollPane(textArea);
        add(textAreaScrollPane);

        RSyntaxTextArea syntaxTextArea = new RSyntaxTextArea(20, 80);
        syntaxTextArea.setSyntaxEditingStyle(SYNTAX_STYLE_JAVASCRIPT);
        RTextScrollPane textScrollPane = new RTextScrollPane(syntaxTextArea);
        add(textScrollPane, NORTH);


        getOWLModelManager().addListener(modelListener);
        for (OWLOntology ontology : getOWLModelManager().getOntologies()) {
            checkUfoOntology(ontology);
        }

        log.info("Example View Component initialized");
    }

    @Override
    protected void disposeOWLView() {
        getOWLModelManager().removeListener(modelListener);
    }

    private void checkUfoOntology(OWLOntology ontology) {
        if (ufoOntology != null) {
            return;
        }
        Optional<IRI> ontologyIRI = ontology.getOntologyID().getOntologyIRI();
        if (!ontologyIRI.isPresent()) {
            return;
        }
        if (!"http://purl.org/nemo/ufo#".equals(ontologyIRI.get().toString())) {
            return;
        }
        initializePublicUFOClassesSet();
        ufoOntology = ontology;
        leafClasses = findLeafClasses(ufoOntology);
    }

    private Set<OWLClass> findLeafClasses(OWLOntology ontology) {
        Set<OWLClass> classes = new HashSet<>();
        Set<OWLDeclarationAxiom> declarations
                = ontology.getAxioms(AxiomType.DECLARATION, Imports.EXCLUDED);
        for (OWLDeclarationAxiom declaration : declarations) {
            final OWLEntity entity = declaration.getEntity();
            if (entity.isOWLClass()) {
                classes.add(entity.asOWLClass());
            }
        }
        Set<OWLSubClassOfAxiom> subclassAxioms
                = ontology.getAxioms(AxiomType.SUBCLASS_OF);
        for (OWLSubClassOfAxiom subclassAxiom : subclassAxioms) {
            final OWLClassExpression superClass = subclassAxiom.getSuperClass();
            if (!superClass.isAnonymous()) {
                classes.remove(superClass.asOWLClass());
            }
        }
        return classes;
    }

    private void recalculate() {
        OWLModelManager modelManager = getOWLModelManager();
        OWLOntology activeOntology = modelManager.getActiveOntology();
        if (activeOntology != null) {
            final OWLOntologyID ontologyID = activeOntology.getOntologyID();
            final Optional<IRI> ontologyIRI = ontologyID.getOntologyIRI();
            checkUfoOntology(activeOntology);
            checkLeafInheritance(activeOntology);
        }
    }

    private void setText(Collection... collections) {
        StringBuilder builder = new StringBuilder();
        for (Collection<?> collection : collections) {
            for (Object object : collection) {
                builder.append(object);
                builder.append("\n");
            }
        }
        textArea.setText(builder.toString());
    }

    private boolean isPublicUFOCLass(OWLClass owlSubClass) {
        return !owlSubClass.isAnonymous() &&
                publicUFOClasses.contains(owlSubClass.getIRI().toString());
    }

    private void checkLeafInheritance(OWLOntology ontology) {
        if (leafClasses == null) {
            return;
        }
        if (ontology == null) {
            return;
        }

        // Set to hold all declared classes in ontology
        final Set<OWLClass> declaredClasses = new HashSet<OWLClass>();
        // Set to hold classes directly subclassing an UFO leaf class
        final Set<OWLClass> ufoDirectSubclasses = new HashSet<OWLClass>();
        // Set to hold classes indirectly subclassing an UFO leaf class
        final Set<OWLClass> ufoIndirectSubclasses = new HashSet<OWLClass>();
        // Multimap to hold mapping from superclasses to subclasses
        //   Actually the multimap shall hold links between equivalent classes
        //   too
        final SetMultimap<OWLClass, OWLClass> subClasses = HashMultimap.create();

        // Process axioms, putting references in the sets declared above
        Set<OWLAxiom> axioms = ontology.getAxioms(Imports.EXCLUDED);
        for (OWLAxiom axiom : axioms) {
            axiom.accept(new OWLAxiomVisitorAdapter() {

                private void linkClasses(
                        OWLClassExpression superClass,
                        OWLClassExpression subClass) {
                    if (superClass.isAnonymous()) {
                        return;
                    }
                    if (subClass.isAnonymous()) {
                        return;
                    }
                    if (superClass.equals(subClass)) {
                        return;
                    }
                    final OWLClass owlSuperClass = superClass.asOWLClass();
                    final OWLClass owlSubClass = subClass.asOWLClass();
                    if (isPublicUFOCLass(owlSuperClass)) {
                        ufoDirectSubclasses.add(owlSubClass);
                    } else if (!isPublicUFOCLass(owlSubClass)) {
                        subClasses.put(owlSuperClass, owlSubClass);
                    }
                }

                @Override
                public void visit(OWLSubClassOfAxiom axiom) {
                    linkClasses(axiom.getSuperClass(), axiom.getSubClass());
                }

                @Override
                public void visit(OWLEquivalentClassesAxiom axiom) {
                    Set<OWLClass> namedClasses = axiom.getNamedClasses();
                    for (OWLClass fromClass : namedClasses) {
                        for (OWLClass toClass : namedClasses) {
                            linkClasses(fromClass, toClass);
                        }
                    }
                }

                @Override
                public void visit(OWLDeclarationAxiom axiom) {
                    OWLEntity entity = axiom.getEntity();
                    if (entity.isOWLClass()) {
                        declaredClasses.add(entity.asOWLClass());
                    }
                }
            });
        }
        new Object() {
            final Set<OWLClass> visited = new HashSet<>();

            public void run() {
                for (OWLClass ufoDirectSubclass : ufoDirectSubclasses) {
                    visit(ufoDirectSubclass);
                }
            }

            private void visit(OWLClass owlClass) {
                if (!visited.add(owlClass)) {
                    return;
                }
                if (!ufoDirectSubclasses.contains(owlClass)) {
                    ufoIndirectSubclasses.add(owlClass);
                }
                for (OWLClass subclass : subClasses.get(owlClass)) {
                    visit(subclass);
                }
            }
        }.run();
        setText(
                Arrays.asList("-- Declared Classes"),
                declaredClasses,
                Arrays.asList("-- UFO Direct Subclasses"),
                ufoDirectSubclasses,
                Arrays.asList("-- UFO Indirect Subclasses"),
                ufoIndirectSubclasses,
                Arrays.asList("-- Leaf Classes"),
                leafClasses,
                Arrays.asList("-- IRI of Leaf Classes"),
                Arrays.asList(
                        leafClasses
                        .stream()
                        .map(c -> c.getIRI().toString())
                        .toArray()));

    }
}
