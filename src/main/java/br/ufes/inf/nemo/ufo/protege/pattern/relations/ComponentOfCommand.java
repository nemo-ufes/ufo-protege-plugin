/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.relations;

import br.ufes.inf.nemo.protege.annotations.EditorKitMenuAction;
import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.EntityFilter;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternApplier;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.ui.relations.ComponentOfPatternFrame;
import java.awt.event.ActionEvent;
import java.util.List;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
@EditorKitMenuAction(
        id = "menuItemComponentOf",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.ForRelationsMenu/SlotB-03",
        name = "New gufo:isComponentOf relation"
)
public class ComponentOfCommand extends PatternCommand {

    private final IRI componentOfRelation = IRI.create(GufoIris.GUFO, "isComponentOf");
    private IRI component;
    private IRI functionalComplex;

    public void setComponent(IRI component) {
        this.component = component;
    }

    public void setFunctionalComplex(IRI functionalComplex) {
        this.functionalComplex = functionalComplex;
    }
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        applier.assertObjectProperty(componentOfRelation, component, functionalComplex);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        List<IRI> functionalComplexIRIs = new EntityFilter(getOWLModelManager())
                .addType(GufoIris.FunctionalComplex)
                .entities();
        
        IRI firstFunctionalComplex = functionalComplexIRIs.isEmpty() ? null : functionalComplexIRIs.get(0);
        List<IRI> componentIRIs = new EntityFilter(getOWLModelManager())
                .addType(GufoIris.Object)
                .isDifferentFrom(firstFunctionalComplex)
                .entities();
        
        ComponentOfPatternFrame frame = new ComponentOfPatternFrame(this);
        frame.setFunctionalComplexIRIs(functionalComplexIRIs);
        frame.setObjectIRIs(componentIRIs);
        frame.display();
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
