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
import br.ufes.inf.nemo.ufo.protege.pattern.ui.instances.InstantiateQualityPatternFrame;
import java.awt.event.ActionEvent;
import java.util.List;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
@EditorKitMenuAction(
        id = "menuItemInstantiateQuality",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.ForInstancesMenu/SlotAA-Z",
        name = "New instance of Quality"
)
public class InstantiateQualityCommand extends PatternCommand {

    private final IRI inheritance = IRI.create(GufoIris.GUFO, "inheresIn");
    private final IRI hasQualityValue = IRI.create(GufoIris.GUFO, "hasQualityValue");
    private IRI sortal;
    private IRI quality;
    private String qualityValue;
    private IRI bearer;

    public void setSortal(IRI sortal) {
        this.sortal = sortal;
    }

    public void setQuality(IRI quality) {
        this.quality = quality;
    }

    public void setQualityValue(String qualityValue) {
        this.qualityValue = qualityValue;
    }

    public void setBearer(IRI bearer) {
        this.bearer = bearer;
    }
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        applier.createNamedIndividual(quality);
        applier.makeInstanceOf(sortal, quality);
        applier.createRelation(inheritance, quality, bearer);
        applier.assertDataProperty(hasQualityValue, quality, qualityValue);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        List<IRI> qualityTypeIRIs = new EntityFilter(getOWLModelManager())
                .addSuperClass(GufoIris.Quality)
                .addType(GufoIris.Sortal)
                .entities();
        
        List<IRI> bearerIRIs = new EntityFilter(getOWLModelManager())
                .addType(GufoIris.ConcreteIndividual)
                .entities();
        
        InstantiateQualityPatternFrame frame = new InstantiateQualityPatternFrame(this);
        frame.setQualityTypeIRIs(qualityTypeIRIs);
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
