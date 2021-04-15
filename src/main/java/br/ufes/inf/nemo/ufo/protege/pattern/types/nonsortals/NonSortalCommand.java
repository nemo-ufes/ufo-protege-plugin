/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.types.nonsortals;

import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternApplier;
import br.ufes.inf.nemo.ufo.protege.pattern.ui.types.NonSortalPatternFrame;
import java.awt.event.ActionEvent;
import java.util.List;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
public abstract class NonSortalCommand extends PatternCommand {

    protected IRI nonsortalClass;
    protected String nonsortalClassName;
    private IRI nonsortal;
    private List<IRI> endurantClasses;

    public abstract void defineNonSortalClass();

    public String getNonSortalClassName() {
        return nonsortalClassName;
    }
    
    public void setNonSortal(IRI nonsortal) {
        this.nonsortal = nonsortal;
    }
    
    public void setEndurantClasses(List<IRI> IRIs) {
        endurantClasses = IRIs;
    }
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        applier.createNamedIndividual(nonsortal);
        applier.makeInstanceOf(nonsortalClass, nonsortal);
        
        applier.createClass(nonsortal);
        
        if(endurantClasses.size() == 1) {
            applier.addSubClassTo(endurantClasses.get(0), nonsortal);
        } else {
            IRI superClass = IRI.create(getOntologyPrefix(), nonsortal.getShortForm() + "OntologicalNature");
            applier.createDisjointUnionOfClasses(superClass, endurantClasses);
            applier.addSubClassTo(GufoIris.Endurant, superClass);
            applier.addSubClassTo(superClass, nonsortal);
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        defineNonSortalClass();
        NonSortalPatternFrame frame = new NonSortalPatternFrame(this);
        frame.display();
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
