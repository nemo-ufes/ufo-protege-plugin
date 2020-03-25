/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.validation;

import br.ufes.inf.nemo.protege.annotations.EditorKitHook;
import br.ufes.inf.nemo.ufo.protege.AbstractEditorKitHook;
import br.ufes.inf.nemo.ufo.protege.validation.Validation.Result;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.protege.editor.core.ModelManager;

/**
 *
 * @author luciano
 */
@EditorKitHook(id = "ufopp.validator")
public class Validator extends AbstractEditorKitHook {

    private final List<Constructor<? extends Rule>> ruleConstructors = new ArrayList<>();

    public static Validator get(ModelManager modelManager) {
        return AbstractEditorKitHook.get(modelManager, Validator.class);
    }

    public Result validate() {
        return Validation.on(modelManager);
    }

    Stream<Constructor<? extends Rule>> ruleConstructors() {
        return ruleConstructors.stream();
    }

    @Override
    public void initialise() throws Exception {
        super.initialise();
        initializeRuleConstructors();
    }

    private void initializeRuleConstructors() throws Exception {
        RuleLoader ruleLoader = new RuleLoader();
        ruleLoader.loadRuleClasses(ruleConstructors);
    }
}
