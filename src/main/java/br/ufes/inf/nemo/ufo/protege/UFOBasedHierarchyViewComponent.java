/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege;

import br.ufes.inf.nemo.protege.annotations.ViewComponent;
import java.util.Comparator;
import java.util.Optional;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.ui.view.cls.ToldOWLClassHierarchyViewComponent;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObject;

/**
 *
 * @author luciano
 */
@ViewComponent(
        id = "ufopp.tree",
        label = "UFO Tree",
        category = "@org.protege.ontologycategory"
)
public class UFOBasedHierarchyViewComponent extends ToldOWLClassHierarchyViewComponent {

    @Override
    protected Optional<OWLObjectHierarchyProvider<OWLClass>> getInferredHierarchyProvider() {
        return Optional.empty();
    }

    @Override
    protected OWLObjectHierarchyProvider<OWLClass> getHierarchyProvider() {
        final OWLModelManager owlModelManager = getOWLEditorKit().getOWLModelManager();
        final UFOBasedClassHierarchyProvider result
                = new UFOBasedClassHierarchyProvider(owlModelManager);
        result.setOntologies(owlModelManager.getActiveOntologies());
        return result;
    }

    @Override
    public void performExtraInitialisation() throws Exception {

        super.performExtraInitialisation();

        initializeCellRenderer();
        initializeComparator();
    }

    protected void initializeCellRenderer() {
        getTree().setCellRenderer(
                new UFOViewTreeNodeRenderer(getOWLEditorKit()));
    }

    private void initializeComparator() {
        final OWLModelManager owlModelManager
                = getOWLEditorKit().getOWLModelManager();
        final Comparator<OWLObject> original
                = owlModelManager.getOWLObjectComparator();
        final UFOConfig ufo = UFOConfig.get(owlModelManager);
        getTree().setOWLObjectComparator((a, b) -> {
            int result = ufo.compareOWLObjects(a, b);
            return result != 0 ? result : original.compare(a, b);
        });
    }
}
