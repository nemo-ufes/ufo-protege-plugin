/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.validation.ui;

import br.ufes.inf.nemo.protege.annotations.ViewComponent;
import br.ufes.inf.nemo.ufo.protege.Singleton;
import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
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
    }

    @Override
    protected void disposeOWLView() {

    }
}
