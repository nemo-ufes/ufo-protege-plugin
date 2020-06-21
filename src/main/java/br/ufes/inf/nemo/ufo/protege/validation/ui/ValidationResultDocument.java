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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
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

    void setResult(Result result) {

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
            .collect(
                HashMap<Rule, Set<Violation>>::new,
                (map, violation) -> {
                    Set<Violation> set = map.computeIfAbsent(
                            violation.getRule(), (rule) -> new HashSet<>());
                    set.add(violation);
                },
                (a, b) -> {
                    for (Map.Entry<Rule, Set<Violation>> entry : b.entrySet()) {
                        if (a.containsKey(entry.getKey())) {
                            a.get(entry.getKey()).addAll(entry.getValue());
                        } else {
                            a.put(entry.getKey(), entry.getValue());
                        }
                    }
                }
            )
            .forEach(this::printViolationsForRule);
    }

    private void printViolationsForRule(Rule rule, Set<Violation> violations) {
        try {
            document.insertBeforeEnd(body, "<h1>" + rule.getLabel() + "</h1>");
            for (Violation violation : violations) {

            }
        } catch (BadLocationException | IOException ex) {
            log.error("Error on generating log text.", ex);
        }
    }
}
