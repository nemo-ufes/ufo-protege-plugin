/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui;

import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternApplier;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
public class SubKindPatternApplication extends Thread {
    private final PatternCommand command;
    private IRI rigidSortal;
    private IRI subkind;
    
    public SubKindPatternApplication(PatternCommand command) {
        this.command = command;
    }
    
    public String getOntologyPrefix() {
        return command.getOntologyPrefix();
    }
    
    public void setRigidSortal(IRI iri) {
        rigidSortal = iri;
    }
    
    public void setSubKind(IRI iri) {
        subkind = iri;
    }
    
    @Override
    public void run() {
        PatternApplier applier = new PatternApplier(command.getOWLModelManager());
        applier.createNamedIndividual(subkind);
        applier.makeInstanceOf(GufoIris.SubKind, subkind);
        applier.createClass(subkind);
        applier.addSubClassTo(rigidSortal, subkind);
    }
}
