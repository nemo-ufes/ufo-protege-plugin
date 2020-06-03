/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege;

import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.plugin.AbstractProtegePlugin;
import org.protege.editor.owl.model.OWLModelManager;

/**
 *
 * @author luciano
 */
public class ValidationPlugin extends AbstractProtegePlugin<ValidationPluginInstance> {

    private final OWLModelManager modelManager;
    
    public ValidationPlugin(OWLModelManager modelManager, IExtension extension) {
        super(extension);
        this.modelManager = modelManager;
    }
}
