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
import br.ufes.inf.nemo.ufo.protege.pattern.ui.instances.InstantiateExtrinsicModePatternFrame;
import java.awt.event.ActionEvent;
import java.util.List;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
@EditorKitMenuAction(
        id = "menuItemInstantiateExtrinsicMode",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.ForInstancesMenu/SlotAA-Z",
        name = "New instance of ExtrinsicMode"
)
public class InstantiateExtrinsicModeCommand extends PatternCommand {

    private final IRI inheritance = IRI.create(GufoIris.GUFO, "inheresIn");
    private final IRI dependenceRelation = IRI.create(GufoIris.GUFO, "externallyDependsOn");
    private IRI sortal;
    private IRI extrinsicMode;
    private IRI bearer;
    private IRI externalDependence;

    public void setSortal(IRI sortal) {
        this.sortal = sortal;
    }

    public void setExtrinsicMode(IRI extrinsicMode) {
        this.extrinsicMode = extrinsicMode;
    }

    public void setBearer(IRI bearer) {
        this.bearer = bearer;
    }

    public void setExternalDependence(IRI externalDependence) {
        this.externalDependence = externalDependence;
    }
    
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        applier.createNamedIndividual(extrinsicMode);
        applier.makeInstanceOf(sortal, extrinsicMode);
        applier.assertObjectProperty(inheritance, extrinsicMode, bearer);
        applier.assertObjectProperty(dependenceRelation, extrinsicMode, externalDependence);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        List<IRI> extrinsicModeTypeIRIs = new EntityFilter(getOWLModelManager())
                .addSuperClass(GufoIris.ExtrinsicMode)
                .addType(GufoIris.Sortal)
                .entities();
        
        List<IRI> bearerIRIs = new EntityFilter(getOWLModelManager())
                .addType(GufoIris.ConcreteIndividual)
                .entities();
        
        IRI firstBearer = bearerIRIs.isEmpty() ? null : bearerIRIs.get(0);
        List<IRI> externalDependenceIRIs = new EntityFilter(getOWLModelManager())
                .addType(GufoIris.Endurant)
                .isDifferentFrom(firstBearer)
                .entities();
        
        InstantiateExtrinsicModePatternFrame frame = new InstantiateExtrinsicModePatternFrame(this);
        frame.setExtrinsicModeTypeIRIs(extrinsicModeTypeIRIs);
        frame.setConcreteIndividualIRIs(bearerIRIs);
        frame.setEndurantIRIs(externalDependenceIRIs);
        frame.display();
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
