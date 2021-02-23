/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.instances;

import br.ufes.inf.nemo.protege.annotations.EditorKitMenuAction;
import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.EntityFilter;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternApplier;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.ui.instances.InstantiateIntrinsicModePatternFrame;
import java.awt.event.ActionEvent;
import java.util.List;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
@EditorKitMenuAction(
        id = "menuItemInstantiateIntrinsicMode",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.ForInstancesMenu/SlotC-4",
        name = "New instance of gufo:IntrinsicMode"
)
public class InstantiateIntrinsicModeCommand extends PatternCommand {

    private final IRI inheritance = IRI.create(GufoIris.GUFO, "inheresIn");
    private IRI sortal;
    private IRI intrinsicMode;
    private IRI bearer;

    public void setSortal(IRI sortal) {
        this.sortal = sortal;
    }

    public void setIntrinsicMode(IRI intrinsicMode) {
        this.intrinsicMode = intrinsicMode;
    }

    public void setBearer(IRI bearer) {
        this.bearer = bearer;
    }
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        applier.createNamedIndividual(intrinsicMode);
        applier.makeInstanceOf(sortal, intrinsicMode);
        applier.assertObjectProperty(inheritance, intrinsicMode, bearer);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        List<IRI> intrinsicModeTypeIRIs = new EntityFilter(getOWLModelManager())
                .addSuperClass(GufoIris.IntrinsicMode)
                .addType(GufoIris.Sortal)
                .entities();
        
        List<IRI> bearerIRIs = new EntityFilter(getOWLModelManager())
                .addType(GufoIris.ConcreteIndividual)
                .entities();
        
        InstantiateIntrinsicModePatternFrame frame = new InstantiateIntrinsicModePatternFrame(this);
        frame.setIntrinsicModeTypeIRIs(intrinsicModeTypeIRIs);
        frame.setConcreteIndividualIRIs(bearerIRIs);
        frame.display();
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
