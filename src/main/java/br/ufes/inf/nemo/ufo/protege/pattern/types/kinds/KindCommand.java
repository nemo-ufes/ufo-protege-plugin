/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.types.kinds;

import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternApplier;
import br.ufes.inf.nemo.ufo.protege.pattern.ui.types.KindPatternFrame;
import java.awt.event.ActionEvent;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
public abstract class KindCommand extends PatternCommand {

    protected IRI endurantClass;
    protected String endurantClassName;
    private IRI kind;

    protected abstract void defineEndurantClass();

    public String getEndurantClassName() {
        return endurantClassName;
    }
    
    public void setKind(IRI kind) {
        this.kind = kind;
    }
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        applier.createNamedIndividual(kind);
        applier.makeInstanceOf(GufoIris.Kind, kind);
        applier.createClass(kind);
        applier.addSubClassTo(endurantClass, kind);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        defineEndurantClass();
        KindPatternFrame frame = new KindPatternFrame(this);
        frame.display();
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
