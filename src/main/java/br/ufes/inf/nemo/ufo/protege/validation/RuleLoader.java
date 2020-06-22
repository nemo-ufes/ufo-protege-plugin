/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.validation;

import br.ufes.inf.nemo.ufo.protege.Util;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class to deal with of loading rule classes.
 * <p>
 * This class holds the boilerplate code involved in loading {@link Rule }
 * subclasses and instantiating them, while generating sensible log messages.
 *
 * @author luciano
 */
final class RuleLoader {

    private static final Logger log = LoggerFactory.getLogger(RuleLoader.class);

    private final Validation validation;

    private Collection<Constructor<? extends Rule>> ruleConstructors;
    private String ruleClassName;
    private Constructor<? extends Rule> ruleConstructor;

    private RuntimeException quitException;

    private Class<? extends Rule> ruleClass;

    RuleLoader() {
        this(null);
    }

    RuleLoader(Validation validation) {
        this.validation = validation;
    }

    private void logError(String message, Throwable ex) {
        log.error(String.format(message, ruleClassName), ex);
    }

    private void quit(String message, Object... args) {
        String formattedMessage = String.format(message, args);
        log.error(formattedMessage);
        throw quitException = new RuntimeException(formattedMessage);
    }

    private void quit(String message, Throwable ex) {
        String formattedMessage = String.format(message, ruleClassName);
        log.error(formattedMessage, ex);
        throw quitException = new RuntimeException(formattedMessage, ex);
    }

    private void loadRuleClass() {
        try {
            Class<?> aClass = getClass().getClassLoader()
                    .loadClass(ruleClassName);
            ruleClass = (Class<? extends Rule>) aClass;
        } catch (ClassNotFoundException ex) {
            quit("Class was not found for rule '%s'.", ex);
        }
    }

    private void checkRuleClass() {
        if (!Rule.class.isAssignableFrom(ruleClass)) {
            quit("Rule class '%s' should extend '%s'",
                    ruleClassName, Rule.class.getName());
        }
    }

    private void findRuleConstructor() {
        try {
            ruleConstructor = ruleClass.getDeclaredConstructor();
        } catch (NoSuchMethodException ex) {
            quit("No-arguments constructor not found for rule class '%s'.", ex);
        }
    }

    private void addRuleConstructorToResultList() {
        ruleConstructors.add(ruleConstructor);
    }

    private void processClassName(String ruleClassName) {
        this.ruleClassName = ruleClassName;
        try {
            loadRuleClass();
            checkRuleClass();
            findRuleConstructor();
            addRuleConstructorToResultList();
        } catch(Exception ex) {
            if (quitException != ex) {
                logError("Unexpected error on loading rule class '%s'", ex);
            }
            log.error("Not adding rule class '%s' due to previous errors.", ruleClassName);
        }
    }

    void loadRuleClasses(Collection<Constructor<? extends Rule>> ruleConstructors) throws IOException {
        this.ruleConstructors = ruleConstructors;
        Util.forEachLine(getClass(), "rules.list", this::processClassName);
    }

    Rule instantiateRule(Constructor<? extends Rule> ruleConstructor) {
        try {
            ruleClassName = ruleConstructor.getDeclaringClass().getName();
            return ruleConstructor.newInstance();
        } catch (IllegalAccessException ex) {
            logError("Unexpected error. Java language access control is being enforced and constructor is inaccessible for rule class '%s'.", ex);
        } catch (IllegalArgumentException ex) {
            logError("Unexpected error. Arguments passing have failed when instantiating rule class '%s'.", ex);
        } catch (InstantiationException ex) {
            logError("Rule class '%s' cannot be abstract.", ex);
        } catch (InvocationTargetException ex) {
            logError("Exception thrown by constructor of rule class '%s'.", ex);
        }
        return null;
    }

    Rule initializeRule(Rule rule) {
        if (rule != null) {
            try {
                rule.initialize(validation);
            } catch (Exception ex) {
                ruleClassName = rule.getClass().getName();
                logError("Exception thrown when initalizing rule of class '%s'", ex);
                return null;
            }
        }
        return rule;
    }
}
