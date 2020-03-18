/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.validation;

import br.ufes.inf.nemo.ufo.protege.UFOConfig;
import java.util.Set;
import org.protege.editor.core.ModelManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObject;

/**
 * Validation rule which should be followed by ontologies.
 * <p> 
 * New rules should be created in the package
 * br.ufes.inf.nemo.ufo.protege.validation.rules. Every class in that package
 * will be automatically instantiated and called for validating the active
 * ontology. 
 * <p>
 * To be considered for validation the class must extend the Rule class. It's
 * recommended to put a {@link RuleInfo} on the class.
 * <p>
 * 
 * @author luciano
 */
public abstract class Rule extends GufoIris {
    protected ModelManager modelManager;
    
    public void initialize(ModelManager modelManager) throws Exception {
        this.modelManager = modelManager;
    }

    /**
     * Validate the ontology in a given context.
     * <p>
     * 
     * @param context Context of validation
     * @return Set of violations of this rule
     * 
     * @see Validation
     * @see Violation
     */
    public abstract Set<Violation> validate(Validation context);

    /**
     * @return Label of this rule
     */
    public String getLabel() {
        RuleInfo annotation = getClass().getAnnotation(RuleInfo.class);
        if (annotation != null) {
            return annotation.label();
        } else {
            return getClass().getName();
        }
    }

    protected UFOConfig getUFOConfig() {
        return UFOConfig.get(modelManager);
    }

    protected boolean isPublicUFOClass(OWLClassExpression owlClass) {
        return getUFOConfig().isPublicUFOClass(owlClass);
    }

    protected boolean isPublicUFOClass(OWLClass owlClass) {
        return getUFOConfig().isPublicUFOClass(owlClass);
    }
    
    protected Violation newViolation(OWLObject... arguments) {
        return new Violation(arguments);
    }
    
    public class Violation {

        private final OWLObject[] arguments;

        protected Violation(OWLObject... arguments) {
            this.arguments = arguments;
        }

        public Rule getRule() {
            return Rule.this;
        }

        public OWLObject[] getArguments() {
            return arguments;
        }
        
        public OWLObject getMainObject() {
            return arguments[0];
        }
    }
}
