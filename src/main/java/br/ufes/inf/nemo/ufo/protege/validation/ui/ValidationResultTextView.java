/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.validation.ui;

import br.ufes.inf.nemo.protege.annotations.ViewComponent;
import br.ufes.inf.nemo.ufo.protege.Singleton;
import java.awt.BorderLayout;
import java.net.URL;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import static javax.swing.event.HyperlinkEvent.EventType.ACTIVATED;
import javax.swing.event.HyperlinkListener;
import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.OWLWorkspace;
import org.protege.editor.owl.model.selection.OWLSelectionModel;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;

/**
 *
 * @author luciano
 */
@ViewComponent(
        id = "ufopp.validation.result.text",
        label = "GUFO validation result text",
        category = "@org.protege.ontologycategory"
)
public class ValidationResultTextView extends AbstractOWLViewComponent
                                      implements HyperlinkListener {

    private static final long serialVersionUID = -4515710047558710080L;
    private static final Logger log =
            Logger.getLogger(ValidationResultTextView.class);

    protected ValidationResultDocument resultDocument;

    @Override
    protected void initialiseOWLView() throws Exception {

        resultDocument = Singleton.get(
                getOWLModelManager(), ValidationResultDocument.class);

        setLayout(new BorderLayout());

        JTextPane resultTextPane = new JTextPane();
        resultTextPane.setEditable(false);
        resultTextPane.setContentType("text/html");
        resultTextPane.setDocument(resultDocument.getDocument());

        JScrollPane resultTextScrollPane = new JScrollPane(resultTextPane);
        add(resultTextScrollPane);
        resultTextPane.addHyperlinkListener(this);
    }

    @Override
    protected void disposeOWLView() {
    }

    @Override
    public void hyperlinkUpdate(HyperlinkEvent he) {
        if (ACTIVATED == he.getEventType()) {
            final OWLModelManager modelManager = getOWLModelManager();
            final OWLWorkspace workspace = getOWLWorkspace();
            final OWLSelectionModel selection = workspace.getOWLSelectionModel();
            final URL url = he.getURL();
            final StringBuilder iriBuilder = new StringBuilder(url.getQuery());
            final String hash = url.getRef();
            if (hash != null && !"".equals(hash)) {
                iriBuilder.append("#").append(hash);
            }
            final IRI selectingIRI = IRI.create(iriBuilder.toString());
            final OWLClass entity = modelManager
                    .getOWLDataFactory()
                    .getOWLEntity(EntityType.CLASS, selectingIRI);
            selection.setSelectedEntity(entity);
        }
    }
}
