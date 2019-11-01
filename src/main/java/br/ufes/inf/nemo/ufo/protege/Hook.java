/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege;

import org.protege.editor.core.ModelManager;
import org.protege.editor.core.editorkit.plugin.EditorKitHook;

/**
 *
 * @author luciano
 */
public class Hook extends EditorKitHook {

    @Override
    public void initialise() throws Exception {
        ModelManager modelManager = getEditorKit().getModelManager();
    }

    @Override
    public void dispose() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
