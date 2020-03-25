/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.sandbox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.protege.editor.owl.OWLEditorKit;


/**
 *
 * @author luciano
 */
public class JavaScriptRunner {

    /**
     * Javascript context for running scripts.
     */
    Context context;
    /**
     * Global object (scope) for Javascript code.
     */
    ScriptableObject global;

    /**
     * Initialize context and create properties in global object.
     * <p>
     * This methos prepare the Javascript environment for running code. The
     * Javascript context is initialized and the global object/scope is prepared
     * with the standard global objects for Javascript Language.
     *
     * @param owlEditorKit
     */
    public void initialize(OWLEditorKit owlEditorKit) {
        context = Context.enter();
        global = context.initStandardObjects();
        putProperty("global", global);
        putProperty("editorKit", owlEditorKit);
        evaluateResourceEx(getClass(), "initialization.js");
    }

    /**
     * Add to global scope a property with given name and value.
     *
     * @param propertyName Name of the property
     * @param object Value of the property
     */
    public void putProperty(String propertyName, Object object) {
        Object jsObject = Context.javaToJS(object, global);
        ScriptableObject.putProperty(global, propertyName, jsObject);
    }

    /**
     * Get the value of a property from global scope.
     *
     * @param propertyName Name of the property
     * @return Value of the property
     */
    public Object getProperty(String propertyName) {
        return global.get(propertyName, global);
    }

    /**
     * Call a function in Javascript.
     * <p>
     * This method runs a function of Javascript environment and return its
     * result. The name of the function is passed in the form of a path, and
     * the method will walk through the Javascript object tree in order to run
     * the given function.
     * <p>
     * For example, if the function name is passed as 'foo.bar.qux', this would
     * be exactly the same as running the Javascript code 'foo.bar.qux()'. Thus,
     * the 'qux' function in the 'bar' object will be called with the later as
     * the argument for 'this'.
     *
     * @param path Property path pointing to a function object
     * @param args Arguments to be passed to function
     * @return Function call result
     */
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

    /**
     * Evaluate Javscript code from given file .
     *
     * @param file File object pointing to a file containing Javascript code
     * @return Evaluation result
     * @throws FileNotFoundException If file was not found
     * @throws IOException If any other error on reading file happens
     */
    public Object evaluateFile(File file) throws FileNotFoundException, IOException {
        try (InputStream stream = new FileInputStream(file)) {
            return evaluateStream(file.getName(), stream);
        }
    }

    /**
     * Evaluate Javascript code from given file .
     * <p>
     * This method delegates to {@link #evaluateFile(java.io.File)}
     *
     * @param file String with name of a file containing Javascript code
     * @return Evaluation result
     * @throws FileNotFoundException If file was not found
     * @throws IOException If any other error on reading file happens
     */
    public Object evaluateFile(String fileName) throws IOException {
        return evaluateFile(new File(fileName));
    }

    /**
     * Evaluate Javascript code from given InputStream.
     * <p>
     * Evaluate Javascript code from stream.
     *
     * @param streamName Name of stream for error reporting
     * @param stream Stream containing Javascript code
     * @return Evaluation result
     * @throws IOException If any error on reading stream happens
     */
    public Object evaluateStream(String streamName, InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return context.evaluateReader(global, reader, streamName, 1, null);
    }

    /**
     * Evaluate Javascript code from given Resource.

     *
     * @param clazz Class whose class loader will be used to load the resource
     * @param resource Name of the resource
     * @return Evaluation result
     * @throws IOException If any error on reading resource happens
     */
    public Object evaluateResource(Class<?> clazz, String resourceName) throws IOException {
        try (InputStream stream = clazz.getResourceAsStream(resourceName)) {
            return evaluateStream(resourceName, stream);
        }
    }

    private Object evaluateResourceEx(Class<?> clazz, String resourceName) {
        try {
            return evaluateResource(clazz, resourceName);
        } catch (IOException ex) {
            return null;
        }
    }

    /**
     * Evaluate Javascript code from given String.
     *
     * @param scriptName Name of script for error reporting
     * @param scriptSource String containing Javascript code
     * @return Evaluation result
     */
    public Object evaluateString(String scriptName, String scriptSource) {
        return context.evaluateString(global, scriptSource, scriptName, 1, null);
    }
}
