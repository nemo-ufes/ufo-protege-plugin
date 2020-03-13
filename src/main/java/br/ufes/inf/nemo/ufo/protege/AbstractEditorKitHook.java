/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege;

import org.protege.editor.core.ModelManager;
import org.protege.editor.core.editorkit.plugin.EditorKitHook;

/**
 * Base class for implementing EditorKitHook's.
 * <p>
 * This class declares a field of type {@link ModelManager} and set its value
 * during initialization. It also declares a default empty implementation for
 * the {@link #dispose() } method.
 *
 * @author luciano
 */
public abstract class AbstractEditorKitHook extends EditorKitHook {

    static <T> T get(ModelManager modelManager, Class<T> aClass) {
        final T result = (T) modelManager.get(aClass);
        if (result == null) {
            throw new RuntimeException(String.format(
                    "Unexpected error. %s object not found",
                    aClass.getSimpleName()));
        }
        return result;

    }

    protected ModelManager modelManager;

    @Override
    public void initialise() throws Exception {
        modelManager = getEditorKit().getModelManager();
        modelManager.put(getClass(), this);
    }

    @Override
    public void dispose() throws Exception {

    }
}
