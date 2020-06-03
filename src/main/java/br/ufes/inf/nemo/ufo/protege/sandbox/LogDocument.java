/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.sandbox;

import br.ufes.inf.nemo.ufo.protege.Singleton;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import org.protege.editor.core.ModelManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author luciano
 */
public class LogDocument implements Singleton.Initializable {

    private final Logger log = LoggerFactory.getLogger(LogDocument.class);

    private Document document;

    @Override
    public void initialize(ModelManager modelManager) {
        document = new PlainDocument();
    }

    public Document getDocument() {
        return document;
    }

    public void append(String text) {
        try {
            document.insertString(document.getLength(), text, null);
        } catch (BadLocationException ex) {
            log.error("Error on generating log text.", ex);
        }
    }
}
