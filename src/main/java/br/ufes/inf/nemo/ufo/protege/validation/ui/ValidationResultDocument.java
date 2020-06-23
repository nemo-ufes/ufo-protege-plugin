/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.validation.ui;

import br.ufes.inf.nemo.ufo.protege.Singleton;
import br.ufes.inf.nemo.ufo.protege.Util;
import br.ufes.inf.nemo.ufo.protege.validation.Result;
import br.ufes.inf.nemo.ufo.protege.validation.Rule;
import br.ufes.inf.nemo.ufo.protege.validation.Violation;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLWriter;
import org.protege.editor.core.ModelManager;
import org.semanticweb.owlapi.model.HasIRI;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLObject;
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

    private void clearPreviousResults() {
        body.getElementCount();
        for (int i = body.getElementCount() - 1; i > 0; i--) {
            Element elem = body.getElement(i);
            if (!elem.equals(header)) {
                document.removeElement(elem);
            }
        }
    }

    void setResult(Result result) {

        clearPreviousResults();

        final Set<Violation> violations = result.getViolations();
        if (!violations.isEmpty()) {
            violations
                .stream()
                .collect(Collectors.groupingBy(Violation::getRule))
                .forEach(this::printViolationsForRule)
                ;
        } else {
            printNoViolationsDetected();
        }
    }

    private void printNoViolationsDetected() {
        try {
            document.insertBeforeEnd(body, "<h1>No violations detected.</h1>");
        } catch (BadLocationException | IOException ex) {
            log.error("Error on generating log text.", ex);
        }
    }

    private void printViolationsForRule(Rule rule, List<Violation> violations) {
        try {
            violations.sort((a, b) -> {
                OWLObject subjA = a.getSubject();
                OWLObject subjB = b.getSubject();
                if (subjA instanceof HasIRI) {
                    if (subjB instanceof HasIRI) {
                        IRI iriA = ((HasIRI) subjA).getIRI();
                        IRI iriB = ((HasIRI) subjB).getIRI();
                        return iriA.compareTo(iriB);
                    }
                    return -1;
                }
                return 1;
            });
            document.insertBeforeEnd(body, "<h1>" + rule.getLabel() + "</h1>");
            for (Violation violation : violations) {
                OWLObject subject = violation.getSubject();
                if (subject instanceof HasIRI) {
                    printIRI((HasIRI) subject);
                }
            }
        } catch (BadLocationException | IOException ex) {
            log.error("Error on generating log text.", ex);
        }
    }

    private void printIRI(HasIRI hasIRI) throws BadLocationException, IOException {
        IRI iri = hasIRI.getIRI();
        StringBuilder str = new StringBuilder();
        str.append("<p><a href=\"");
        str.append(iri.toString());
        str.append("\">");
        str.append(iri.getShortForm());
        str.append("</a></p>");
        document.insertBeforeEnd(body, str.toString());
    }
}
