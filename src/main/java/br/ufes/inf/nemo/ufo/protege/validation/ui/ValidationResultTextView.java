/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.validation.ui;

import br.ufes.inf.nemo.protege.annotations.ViewComponent;
import br.ufes.inf.nemo.ufo.protege.Singleton;
import br.ufes.inf.nemo.ufo.protege.sandbox.LogDocument;
import java.awt.BorderLayout;
import static java.awt.BorderLayout.SOUTH;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import static javax.swing.event.HyperlinkEvent.EventType.ACTIVATED;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;
import org.apache.log4j.Logger;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import static org.fife.ui.rsyntaxtextarea.SyntaxConstants.SYNTAX_STYLE_HTML;
import org.fife.ui.rtextarea.RTextScrollPane;
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
    List<DocumentUpdater> updaters = new ArrayList();

    protected ValidationResultDocument resultDocument;
    Semaphore lock = new Semaphore(1);

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

        RSyntaxTextArea htmlSourceTextArea = new RSyntaxTextArea(20, 80);
        htmlSourceTextArea.setSyntaxEditingStyle(SYNTAX_STYLE_HTML);
        RTextScrollPane htmlScrollPane = new RTextScrollPane(htmlSourceTextArea);
        add(htmlScrollPane, SOUTH);

        onUpdate(resultDocument.getDocument(), () -> {
            if (!htmlSourceTextArea.hasFocus()) {
                htmlSourceTextArea.setText(resultDocument.getContent());
            }
        });
        onUpdate(htmlSourceTextArea.getDocument(), () ->
            resultDocument.setContent(htmlSourceTextArea.getText()));

        resultTextPane.addHyperlinkListener(this);
    }

    private void onUpdate(Document document, Runnable runnable) {
        DocumentUpdater documentUpdater = new DocumentUpdater(runnable);
        updaters.add(documentUpdater);
        documentUpdater.listenTo(document);
    }

    @Override
    protected void disposeOWLView() {
        updaters.stream().forEach(DocumentUpdater::unlisten);
    }

    @Override
    public void hyperlinkUpdate(HyperlinkEvent he) {
        if (ACTIVATED == he.getEventType()) {
            final OWLModelManager modelManager = getOWLModelManager();
            final OWLWorkspace workspace = getOWLWorkspace();
            final OWLSelectionModel selection = workspace.getOWLSelectionModel();
            final URL url = he.getURL();
            final LogDocument logDocument = Singleton.get(
                    getOWLModelManager(), LogDocument.class);
            final IRI selectingIRI = IRI.create(url.getQuery());
            final OWLClass entity = modelManager
                    .getOWLDataFactory()
                    .getOWLEntity(EntityType.CLASS, selectingIRI);
            logDocument.append(url.toString());
            selection.setSelectedEntity(entity);
        }
    }

    private class DocumentUpdater implements DocumentListener {

        private final Runnable doUpdate;
        private Document document;

        public DocumentUpdater(Runnable doUpdate) {
            this.doUpdate = doUpdate;
        }

        private void update(DocumentEvent de) {
            if (lock.tryAcquire()) {
                try {
                    doUpdate.run();
                } finally {
                    lock.release();
                }
            }
        }

        @Override
        public void insertUpdate(DocumentEvent de) {
            update(de);
        }

        @Override
        public void removeUpdate(DocumentEvent de) {
            update(de);
        }

        @Override
        public void changedUpdate(DocumentEvent de) {
            update(de);
        }

        private void listenTo(Document document) {
            document.addDocumentListener(this);
            this.document = document;
        }

        private void unlisten() {
            if (document != null) {
                document.removeDocumentListener(this);
            } else {
                log.warn(
                    "DocumentUpdater has not been added to any document. ");
            }
        }
    }
}
