/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.validation;

import br.ufes.inf.nemo.protege.annotations.EditorKitHook;
import br.ufes.inf.nemo.ufo.protege.AbstractEditorKitHook;
import java.util.ArrayList;
import java.util.List;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 *
 * @author luciano
 */
@EditorKitHook(id = "ufopp.validator")
public class Validator extends AbstractEditorKitHook {
    
    private final List<Rule> rules = new ArrayList<>();
    
    @Override
    public void initialise() throws Exception {
        super.initialise();
        initializeRules();
    }

    private void initializeRules() throws Exception {
        RuleLoader ruleLoader = new RuleLoader(modelManager);        
        ruleLoader.loadRules(rules);
    }
    
    Validation validate(OWLOntology ontology) throws Exception {
        return null;
    }
}
