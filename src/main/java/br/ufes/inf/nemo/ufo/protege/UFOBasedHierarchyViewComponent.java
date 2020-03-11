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
 * View component presenting some specific classes from gUFO at top of
 * hierarchy. The top subtree shown by this component is intended to give
 * quick access to specializations of key types from gUFO hierarchy.
 * <p>
 * The hierarchy of nodes is guided by {@link UFOBasedClassHierarchyProvider}
 * which, in turn, uses functionality provided by {@link UFOConfig} class. *
 * <p>
 * The order of nodes is provided by a custom comparator
 * defined during {@link #performExtraInitialisation() initialization}, which
 * delegates to UFOConfig's method
 * {@link UFOConfig#compareOWLObjects(OWLObject, OWLObject)}
 * <p>
 * {@link UFOViewTreeNodeRenderer} instances are used to define the look of
 * nodes in the view.
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
