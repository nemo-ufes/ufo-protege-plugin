/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.treeview;

import br.ufes.inf.nemo.ufo.protege.GufoIris;
import static br.ufes.inf.nemo.ufo.protege.GufoIris.getUFOViewChildren;
import static br.ufes.inf.nemo.ufo.protege.GufoIris.isNonLeafUFOViewClass;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.hierarchy.AbstractOWLObjectHierarchyProvider;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;

/**
 * Hierarchy Provider used by {@link UFOBasedHierarchyViewComponent
 * UFO Tree View}.
 * <p>
 * This class mostly delegates methods to implementations provided by
 * {@link OWLObjectHierarchyProvider} and provides some specializations by
 * using {@link UFOConfig} class functionality.
 * <p>
 * The nodes at the top of hierarchy are defined by UFOConfig, and the remaining
 * of hierarchy is just the same as that provided by OWLObjectHierarchyProvider.
 *
 * @author luciano
 */
public class UFOBasedClassHierarchyProvider
        extends AbstractOWLObjectHierarchyProvider<OWLClass> {

    private List<OWLOntology> ontologies;
    private final OWLModelManager owlModelManager;

    public UFOBasedClassHierarchyProvider(OWLModelManager owlModelManager) {
        super(owlModelManager.getOWLOntologyManager());
        this.owlModelManager = owlModelManager;
        this.updateOntologies();
        owlModelManager.addListener(owlModelManagerListener);
        owlModelManager.addOntologyChangeListener(owlOntologyChangeListener);
    }

    @Override
    public void dispose() {
        owlModelManager.removeListener(owlModelManagerListener);
        owlModelManager.removeOntologyChangeListener(owlOntologyChangeListener);
        super.dispose();
    }

    @Override
    public void setOntologies(Set<OWLOntology> ontologies) {
        this.ontologies = new ArrayList<>(ontologies);
    }

    @Override
    public Set<OWLClass> getRoots() {

        Set<OWLClass> owlThing = Sets.newHashSet(
                getManager().getOWLDataFactory().getOWLThing());

        return (ontologies == null) ? owlThing :
            ontologies
                .stream()
                .flatMap(GufoIris::ufoViewRootClasses)
                .collect(Collectors.toCollection(() -> owlThing))
                ;
    }

    protected OWLObjectHierarchyProvider<OWLClass> getOWLClassHierarchyProvider() {
        return owlModelManager
                .getOWLHierarchyManager()
                .getOWLClassHierarchyProvider();
    }

    @Override
    public Set<OWLClass> getParents(OWLClass n) {
        return getOWLClassHierarchyProvider().getParents(n);
    }

    @Override
    public Set<OWLClass> getEquivalents(OWLClass n) {
        return getOWLClassHierarchyProvider().getEquivalents(n);
    }

    @Override
    public boolean containsReference(OWLClass n) {
        return getOWLClassHierarchyProvider().containsReference(n);
    }

    @Override
    public Set<OWLClass> getChildren(OWLClass owlClass) {
        return isNonLeafUFOViewClass(owlClass) ?
            getUFOViewChildren(ontologies, owlClass) :
            getOWLClassHierarchyProvider().getChildren(owlClass);
    }

    final OWLOntologyChangeListener owlOntologyChangeListener = changes -> {
        changes
            .stream()
            .filter(change -> ontologies.contains(change.getOntology()))
            .filter(change -> change.isAxiomChange())
            .flatMap(change -> change.getSignature().stream())
            .filter(entity -> entity.isOWLClass())
            .map(entity -> entity.asOWLClass())
            .distinct()
            .forEach(this::fireNodeChanged);
    };

    private void updateOntologies() {
        setOntologies(owlModelManager.getActiveOntologies());
    }

    final OWLModelManagerListener owlModelManagerListener = event -> {
        switch (event.getType()) {
            case ACTIVE_ONTOLOGY_CHANGED:
            case ONTOLOGY_RELOADED:
                updateOntologies();
                break;
        }
    };
}
