/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.validation.ui;

import br.ufes.inf.nemo.ufo.protege.Singleton;
import br.ufes.inf.nemo.ufo.protege.Util;
import br.ufes.inf.nemo.ufo.protege.validation.Rule.Violation;
import br.ufes.inf.nemo.ufo.protege.validation.Validation;
import java.io.IOException;
import java.io.StringWriter;
import java.util.logging.Level;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLWriter;
import org.protege.editor.core.ModelManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author luciano
 */
public class ValidationResultDocument implements Singleton.Initializable {

    private final Logger log = LoggerFactory.getLogger(ValidationResultDocument.class);

    private HTMLEditorKit kit;
    private HTMLDocument document;
    private Element body;
    private Element header;

    @Override
    public void initialize(ModelManager modelManager) {
        kit = new HTMLEditorKit();
        document = (HTMLDocument) kit.createDefaultDocument();
        setContent(Util.readAsString(getClass(), "resultView.html"));
        body = document.getElement("body");
        header = document.getElement("header");
    }

    public HTMLDocument getDocument() {
        return document;
    }

    public void addDocumentListener(DocumentListener listener) {
        document.addDocumentListener(listener);
    }

    public void setContent(String htmlContent)  {
        Element root = document.getDefaultRootElement();
        try {
            document.setInnerHTML(root, htmlContent.replaceAll("</?html>", ""));
        } catch (BadLocationException | IOException ex) {
            java.util.logging.Logger.getLogger(ValidationResultDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getContent() {
        StringWriter buffer = new StringWriter();
        HTMLWriter writer =
                new HTMLWriter(buffer, document, 0, document.getLength());
        try {
            writer.write();
        } catch (IOException | BadLocationException ex) {

        }
        return buffer.toString();
    }

    public void append(String text) {
        try {
            final Element root = document.getDefaultRootElement();
            final Element html = root.getElement(root.getElementCount() - 1);
            final Element body = html.getElement(html.getElementCount() - 1);

            document.insertBeforeEnd(body, "<p>The <a href=\"http://purl.org/nemo/gufo#Sortal\">Sortal</a> class</p>");
            document.insertBeforeEnd(body, "<p>" + text + "</p>");
        } catch (BadLocationException | IOException ex) {
            log.error("Error on generating log text.", ex);
        }
    }

    void setResult(Validation.Result result) {

        body.getElementCount();
        for (int i = body.getElementCount() - 1; i > 0; i--) {
            Element elem = body.getElement(i);
            if (!elem.equals(header)) {
                document.removeElement(elem);
            }
        }
        result
                .getViolations()
                .stream()
                .forEach(this::registerViolation);
    }

    private void registerViolation(Violation violation) {
        try {
            document.insertBeforeEnd(body,
                    "<h1>" + violation.getRule().getLabel() + "</h1>");
        } catch (BadLocationException | IOException ex) {
            log.error("Error on generating log text.", ex);
        }
    }
}
