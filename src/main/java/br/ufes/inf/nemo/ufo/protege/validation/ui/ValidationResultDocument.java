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
import br.ufes.inf.nemo.ufo.protege.validation.helpers.ObjectGraphNode;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.regex.Matcher;
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
    private Util.StringScanner stringScanner;

    @Override
    public void initialize(ModelManager modelManager) {
        kit = new HTMLEditorKit();
        document = (HTMLDocument) kit.createDefaultDocument();
        setContent(Util.readAsString(getClass(), "resultView.html"));
        body = document.getElement("body");
        header = document.getElement("header");

        stringScanner = Util.stringScanner("\\{(.*?)(?::(.*?))?\\}");
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

        final Set<Violation> violationSet = result.getViolations();
        if (violationSet.isEmpty()) {
            printNoViolationsDetected();
            return;
        }

        ViolationTextPrinter p = new ViolationTextPrinter(stringScanner);
        Violation[] violations = sortViolations(violationSet);
        boolean odd = false;
        for (Violation violation : violations) {
            odd = !odd;
            p.clear();
            p.append("<div class=\"");
            p.append(odd ? "odd" : "even");
            p.append("\">");
            p.appendViolationText(violation);
            p.append("</div>");
            insertBeforeEnd(body, p.toString());
        }
    }

    private Violation[] sortViolations(final Set<Violation> violationSet) {
        final Violation[] violations = new Violation[violationSet.size()];
        violationSet.toArray(violations);
        Arrays.sort(violations, new Comparator<Violation>() {

            private String getIRI(OWLObject o) {
                IRI iri = null;
                if (o instanceof HasIRI) {
                    iri = ((HasIRI)o).getIRI();
                } else if (o != null && o.isIRI()) {
                    iri = (IRI)o;
                }
                return iri == null ? null : iri.toString();
            }


            @Override
            public int compare(Violation v1, Violation v2) {
                String i1 = getIRI(v1.getSubject());
                String i2 = getIRI(v2.getSubject());
                if (i1 == null) {
                    return i2 == null ? 0 : -1;
                } else {
                    return i2 == null ? 1 : i1.compareTo(i2);
                }
            }
        });

        return violations;
    }

    private void printNoViolationsDetected() {
        insertBeforeEnd(body, "<h1>No violations detected.</h1>");
    }

    private void insertBeforeEnd(Element element, String text) {
        try {
            document.insertBeforeEnd(element, text);
        } catch (BadLocationException | IOException ex) {
            log.error("Error on inserting HTML text.", ex);
        }
    }
}

class ViolationTextPrinter {

    final Util.StringScanner scanner;
    final Consumer<String> unmatch = this::append;
    final Consumer<Matcher> match = this::match;
    StringBuilder stringBuilder;
    Map<String, Object> arguments;

    ViolationTextPrinter(Util.StringScanner stringScanner) {
        this.scanner = stringScanner;
    }

    public void clear() {
        stringBuilder = new StringBuilder();
    }

    public void appendViolationText(Violation violation) {
        arguments = violation.getArguments();
        scanner.scan(violation.getRule().getDescription(), unmatch, match);
    }

    @Override
    public String toString() {
        return stringBuilder.toString();
    }

    public StringBuilder append(String string) {
        return stringBuilder.append(string);
    }

    private void appendIRILink(IRI iri, String text) {
        append("<a href=\"")
        .append("https://iri.local/iri?")
        .append(iri.toString())
        .append("\">")
        .append(text)
        .append("</a>")
        ;
    }

    private void appendIRI(IRI iri, String arg) {
        switch (arg) {
            case "":
                appendIRILink(iri, iri.getShortForm());
                break;
            case "lc":
                String text = Util.stringScanner("[A-Z]")
                        .replace(iri.getShortForm(), matcher ->
                              ((matcher.start() > 0) ? " " : "") +
                                  matcher.group().toLowerCase()
                        );
                appendIRILink(iri, text);
                break;
            default:
        }
    }

    private void appendCollection(Collection collection, String arg) {
        switch (collection.size()) {
            case 0:
                return;
            case 1:
                appendObject(collection.iterator().next(), "");
                return;
        }
        Object[] items = new Object[collection.size()];
        collection.toArray(items);
        Arrays.sort(items, new Comparator<Object>() {

            private String getIRI(Object o) {
                IRI iri = null;
                if (o instanceof ObjectGraphNode) {
                    iri = ((ObjectGraphNode)o).getIRI();
                } else if (o instanceof HasIRI) {
                    iri = ((HasIRI)o).getIRI();
                } else if (o instanceof IRI) {
                    iri = (IRI)o;
                }
                return iri == null ? null : iri.toString();
            }


            @Override
            public int compare(Object o1, Object o2) {
                String i1 = getIRI(o1);
                String i2 = getIRI(o2);
                if (i1 == null) {
                    return i2 == null ? 0 : -1;
                } else {
                    return i2 == null ? 1 : i1.compareTo(i2);
                }
            }
        });
        int last = items.length - 1;

        appendObject(items[0], "");
        for (int i = 1; i < last; i++) {
            append(", ");
            appendObject(items[i], "");
        }
        if ("".equals(arg)) append(", "); else append(arg);
        appendObject(items[last], "");
    }

    private boolean appendObject(Object obj, String arg) {
        if (obj instanceof IRI) {
            appendIRI((IRI) obj, arg);
        } else if (obj instanceof HasIRI) {
            appendIRI(((HasIRI) obj).getIRI(), arg);
        } else if (obj instanceof ObjectGraphNode) {
            ObjectGraphNode node = (ObjectGraphNode) obj;
            appendIRI(node.getIRI(), arg);
        } else if (obj instanceof Collection) {
            appendCollection((Collection) obj, arg);
        } else {
            return false;
        }
        return true;
    }

    private void match(Matcher matcher) {
        String fieldName = matcher.group(1);
        String spec = matcher.group(2);
        if (spec == null) spec = "";
        Object arg = arguments.get(fieldName);
        if (!appendObject(arg, spec)) {
            append("{")
            .append(fieldName)
            .append(":");
            if (arg != null) {
                append("(")
                .append(arg.getClass().getSimpleName())
                .append(")")
                .append(arg.toString());
            } else {
                append("null");
            }
            append("}");
        };
    }
}
