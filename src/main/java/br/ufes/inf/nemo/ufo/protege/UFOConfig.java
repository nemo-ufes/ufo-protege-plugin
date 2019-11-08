/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.protege.editor.core.ModelManager;
import org.protege.editor.core.editorkit.plugin.EditorKitHook;
import org.semanticweb.owlapi.model.OWLClass;

/**
 *
 * @author luciano
 */
@br.ufes.inf.nemo.protege.annotations.EditorKitHook(
        id = "ufopp.hook"
)
public class UFOConfig extends EditorKitHook {

    private ModelManager modelManager;
    private Set<String> publicUFOClasses;

    @Override
    public void initialise() throws Exception {
        modelManager = getEditorKit().getModelManager();
        modelManager.put(getClass(), this);
        initializePublicUFOClassesSet();
    }

    @Override
    public void dispose() throws Exception {
        if (modelManager.get(getClass()) == this) {
            modelManager.put(getClass(), null);
        }
    }

    public static Optional<UFOConfig> getHook(ModelManager modelManager) {
        return Optional.ofNullable(modelManager.get(UFOConfig.class));
    }


    private void initializePublicUFOClassesSet() {
        publicUFOClasses = new HashSet<>();
        publicUFOClasses.add("http://purl.org/nemo/ufo#Collection");
        publicUFOClasses.add("http://purl.org/nemo/ufo#Event");
        publicUFOClasses.add("http://purl.org/nemo/ufo#Participation");
        publicUFOClasses.add("http://purl.org/nemo/ufo#Category");
        publicUFOClasses.add("http://purl.org/nemo/ufo#QualityValueAttributionSituation");
        publicUFOClasses.add("http://purl.org/nemo/ufo#Role");
        publicUFOClasses.add("http://purl.org/nemo/ufo#Phase");
        publicUFOClasses.add("http://purl.org/nemo/ufo#TemporaryParthoodSituation");
        publicUFOClasses.add("http://purl.org/nemo/ufo#FunctionalComplex");
        publicUFOClasses.add("http://purl.org/nemo/ufo#EventType");
        publicUFOClasses.add("http://purl.org/nemo/ufo#Mode");
        publicUFOClasses.add("http://purl.org/nemo/ufo#SubKind");
        publicUFOClasses.add("http://purl.org/nemo/ufo#QuaIndividual");
        publicUFOClasses.add("http://purl.org/nemo/ufo#Quantity");
        publicUFOClasses.add("http://purl.org/nemo/ufo#PhaseMixin");
        publicUFOClasses.add("http://www.w3.org/2006/time#Instant");
        publicUFOClasses.add("http://purl.org/nemo/ufo#ContingentInstantiationSituation");
        publicUFOClasses.add("http://purl.org/nemo/ufo#Quality");
        publicUFOClasses.add("http://purl.org/nemo/ufo#QualityValue");
        publicUFOClasses.add("http://purl.org/nemo/ufo#RoleMixin");
        publicUFOClasses.add("http://purl.org/nemo/ufo#Kind");
        publicUFOClasses.add("http://purl.org/nemo/ufo#Relator");
        publicUFOClasses.add("http://purl.org/nemo/ufo#AbstractIndividualType");
        publicUFOClasses.add("http://purl.org/nemo/ufo#SituationType");
        publicUFOClasses.add("http://purl.org/nemo/ufo#Mixin");
    }

    public boolean isPublicUFOCLass(OWLClass owlSubClass) {
        return !owlSubClass.isAnonymous() &&
                publicUFOClasses.contains(owlSubClass.getIRI().toString());
    }
}
