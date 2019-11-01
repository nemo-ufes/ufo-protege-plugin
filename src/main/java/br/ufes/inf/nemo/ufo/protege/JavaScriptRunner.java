/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.protege.editor.owl.OWLEditorKit;


/**
 *
 * @author luciano
 */
public class JavaScriptRunner {

    Context context;
    ScriptableObject global;

    public void init(OWLEditorKit owlEditorKit) {
        context = Context.enter();
        global = context.initStandardObjects();
        putProperty("editorKit", owlEditorKit);
        putEditorKitProperty("modelManager", "oWLModelManager");
        putEditorKitProperty("workspace", "oWLWorkspace");
    }

    private void putEditorKitProperty(String publicName) {
        putEditorKitProperty(publicName, publicName);
    }

    private void putEditorKitProperty(String publicName, String protegeName) {

        String getterName = new StringBuilder()
                .append("get")
                .append(publicName.substring(0,1).toUpperCase())
                .append(publicName.substring(1))
                .toString();

        evaluateString("initialization", new StringBuilder()
                .append("Object.defineProperty(global, \"")
                .append(publicName).append("\", {")
                .append(    "get: function ").append(getterName).append("() {")
                .append(        "global.editorKit.").append(protegeName).append(";")
                .append(    "}")
                .append("});")
                .toString()
        );
    }

    public void putProperty(String propertyName, Object object) {
        Object jsObject = Context.javaToJS(object, global);
        ScriptableObject.putProperty(global, propertyName, jsObject);
    }

    public Object getProperty(String propertyName) {
        return global.get(propertyName, global);
    }

    public Object callFunction(String path, Object... args) {
        Scriptable thisObj = global;
        Scriptable last = global;
        String pathArray[] = path.split("\\.");
        for (String pathItem : pathArray) {
            thisObj = last;
            last = (Scriptable) last.get(pathItem, last);
        }
        Function func = (Function) last;
        return func.call(context, global, thisObj, args);
    }

    public Object evaluateFile(File file) throws FileNotFoundException, IOException {
        try (InputStream stream = new FileInputStream(file)) {
            return evaluateStream(file.getName(), stream);
        }
    }

    public Object evaluateFile(String fileName) throws IOException {
        return evaluateFile(new File(fileName));
    }

    public Object evaluateStream(String streamName, InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return context.evaluateReader(global, reader, streamName, 1, null);
    }

    public Object evaluateResource(Class<?> clazz, String resourceName) throws IOException {
        try (InputStream stream = clazz.getResourceAsStream(resourceName)) {
            return evaluateStream(resourceName, stream);
        }
    }

    public Object evaluateString(String scriptName, String scriptSource) {
        return context.evaluateString(global, scriptSource, scriptName, 1, null);
    }

    public Context enterContext(OWLEditorKit editorKit) {
        return ContextFactory.getGlobal().enterContext();
    }
}
