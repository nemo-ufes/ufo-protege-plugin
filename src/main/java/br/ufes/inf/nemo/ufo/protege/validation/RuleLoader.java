/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.validation;

import br.ufes.inf.nemo.ufo.protege.Util;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import org.protege.editor.core.ModelManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class to deal with of loading rule classes.
 * <p>
 * This class is instantiated only once during initialization of a
 * {@link Validator} object. Its method {@link loadRules(List) loadRules} 
 * loads class names from 'rules.list' text file resource and creates an
 * instance for each one of the classes.
 * <p>
 * The 'rules.list' resource is automatically generated during build time by
 * maven-antrun-plugin. It makes a scan over the source code folder of
 * package br.ufes.inf.nemo.ufo.protege.validation
 * 
 * @author luciano
 */
final class RuleLoader {
    
    private static final Logger log = LoggerFactory.getLogger(RuleLoader.class);
    
    private final ModelManager modelManager;
    
    private List<Rule> rules;
    private String ruleClassName;
    private Constructor<? extends Rule> ruleConstructor;
    private Rule rule;
    
    private RuntimeException quitException;
    
    private Class<? extends Rule> ruleClass;

    RuleLoader(ModelManager modelManager) {
        this.modelManager = modelManager;
    }

    private void logError(String message, Throwable ex) {
        log.error(String.format(message, ruleClassName), ex);
    }

    private void quit(String message, Object... args) {
        String formattedMessage = String.format(message, args);
        log.error(formattedMessage);
        throw quitException = new RuntimeException(formattedMessage);
    }
    
    private void quit(String message) {
        String formattedMessage = String.format(message, ruleClassName);
        log.error(formattedMessage);
        throw quitException = new RuntimeException(formattedMessage);
    }
    
    private void quit(String message, Throwable ex) {
        String formattedMessage = String.format(message, ruleClassName);
        log.error(formattedMessage, ex);
        throw quitException = new RuntimeException(formattedMessage, ex);
    }
    
    private void loadRule(String ruleClassName) {
        this.ruleClassName = ruleClassName;
        try {
            loadRuleClass();
            checkRuleClass();
            instantiateRule();
            initializeRule();
            addRuleToResultList();
        } catch(Exception ex) {
            if (quitException != ex) {
                logError("Unexpected error on loading rule '%s'", ex);
            }
            log.error("Not adding rule '%s' due to previous errors.", ruleClassName);
        }
    }
    
    private void loadRuleClass() {
        try {
            Class<?> aClass = getClass().getClassLoader()
                    .loadClass(ruleClassName);
            ruleClass = (Class<? extends Rule>) aClass;
            ruleConstructor = ruleClass.getDeclaredConstructor();
        } catch (ClassNotFoundException ex) {
            quit("Class was not found for rule '%s'.", ex);
        } catch (NoSuchMethodException ex) {
            quit("No-arguments constructor not found for rule class '%s'.", ex);
        }        
    }
    
    private void checkRuleClass() {
        if (!Rule.class.isAssignableFrom(ruleClass)) {
            quit("Rule class '%s' should extend '%s'",
                    ruleClassName, Rule.class.getName());
        }
    }
    
    private void instantiateRule() {
        try {
            rule = ruleConstructor.newInstance();
        } catch (IllegalAccessException ex) {
            quit("Unexpected error. Java language access control is being enforced and constructor is inaccessible for rule class '%s'.", ex);
        } catch (IllegalArgumentException ex) {
            quit("Unexpected error. Arguments passing have failed when instantiating rule class '%s'.", ex);
        } catch (InstantiationException ex) {
            quit("Rule class '%s' cannot be abstract.", ex);
        } catch (InvocationTargetException ex) {
            quit("Exception thrown by constructor of rule class '%s'.", ex);
        }
    }
    
    private void initializeRule() {
        try {
            rule.initialize(modelManager);
        } catch (Exception ex) {
            quit("Exception thrown when initalizing rule", ex);
        }
    }

    private void addRuleToResultList() {
        rules.add(rule);
    }
    
    void loadRules(List<Rule> rules) throws Exception {
        this.rules = rules;
        Util.forEachLine(getClass(), "rules.list", this::loadRule);
    }
}