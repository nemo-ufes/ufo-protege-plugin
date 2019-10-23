/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege;

import br.ufes.inf.nemo.protege.annotations.ViewComponent;
import java.awt.BorderLayout;
import org.apache.log4j.Logger;
import org.protege.editor.owl.ui.OWLWorkspaceViewsTab;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;

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

//    @Override
//    public void initialiseOWLView() throws Exception {
//        setLayout(new BorderLayout());
//        log.info("Example View Component initialized");
//    }
//
//    @Override
//    public void disposeOWLView() {
////		metricsComponent.dispose();
//    }

    @Override
    protected void initialiseOWLView() throws Exception {
        setLayout(new BorderLayout());
//        metricsComponent = new Metrics(getOWLModelManager());
//        add(metricsComponent, BorderLayout.CENTER);
        log.info("Example View Component initialized");
    }

    @Override
    protected void disposeOWLView() {

    }
}
