/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege;

import java.util.HashSet;
import java.util.Set;
import org.protege.editor.core.ModelManager;
import org.protege.editor.core.editorkit.plugin.EditorKitHook;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;

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

    public static UFOConfig get(ModelManager modelManager) {
        final UFOConfig result = modelManager.get(UFOConfig.class);
        if (result == null) {
            throw new RuntimeException(
                "Unexpected error. UFOConfig object not found");
        }
        return result;
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

    public boolean isPublicUFOCLass(OWLClassExpression owlClass) {
        return !owlClass.isAnonymous() &&
                publicUFOClasses.contains(
                        owlClass.asOWLClass().getIRI().toString());
    }

    public Set<OWLClass> extractUFOClasses(OWLOntology ontology) {
        return extractUFOClasses(ontology, new HashSet<>());
    }

    public Set<OWLClass> extractUFOClasses(
            OWLOntology ontology, Set<OWLClass> result) {
        ontology
                .getNestedClassExpressions()
                .stream()
                .filter(this::isPublicUFOCLass)
                .map(OWLClassExpression::asOWLClass)
                .forEach(result::add);
        return result;
    }
}
