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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.apache.log4j.Logger;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;

/**
 *
 * @author luciano
 */
@ViewComponent(
        id = "ufopp.validation.result.text",
        label = "GUFO validation result text",
        category = "@org.protege.ontologycategory"
)
public class ValidationResultTextView extends AbstractOWLViewComponent {

    private static final long serialVersionUID = -4515710047558710080L;
    private static final Logger log =
            Logger.getLogger(ValidationResultTextView.class);
    private JTextArea textArea;

    @Override
    protected void initialiseOWLView() throws Exception {

        LogDocument logDocument = Singleton.get(
                getOWLModelManager(), LogDocument.class);

        setLayout(new BorderLayout());
        textArea = new JTextArea(logDocument.getDocument());
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane textAreaScrollPane = new JScrollPane(textArea);
        add(textAreaScrollPane);
    }

    @Override
    protected void disposeOWLView() {

    }
}
