/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.sandbox;

import br.ufes.inf.nemo.ufo.protege.Singleton;
import java.io.IOException;
import java.io.Reader;
import java.util.logging.Level;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import org.protege.editor.core.ModelManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author luciano
 */
public class LogDocument implements Singleton.Initializable {

    private final Logger log = LoggerFactory.getLogger(LogDocument.class);

    private HTMLEditorKit kit;
    private HTMLDocument document;

    @Override
    public void initialize(ModelManager modelManager) {
        kit = new HTMLEditorKit();
        document = (HTMLDocument) kit.createDefaultDocument();

        Element root = document.getDefaultRootElement();
        try {
            document.setInnerHTML(root, "<html>\n" +
                    "   <head>\n" +
                    "     <title>An example HTMLDocument</title>\n" +
                    "     <style type=\"text/css\">\n" +
                    "       h1 { background-color: silver; }\n" +
                    "       ul { color: blue; }\n" +
                    "     </style>\n" +
                    "   </head>\n" +
                    "   <body>\n" +
                    "     <input type=\"button\">By rule</input>\n" +
                    "     <div id=\"BOX\">\n" +
                    "       <p>Paragraph 1</p>\n" +
                    "       <p>Paragraph 2</p>\n" +
                    "     </div>\n" +
                    "   </body>\n" +
                    " </html>");
        } catch (BadLocationException | IOException ex) {
            java.util.logging.Logger.getLogger(LogDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public StyledDocument getDocument() {
        return document;
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
}
