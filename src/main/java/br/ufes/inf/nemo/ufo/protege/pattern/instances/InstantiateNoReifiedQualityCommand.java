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
import br.ufes.inf.nemo.ufo.protege.pattern.ui.instances.InstantiateNoReifiedQualityPatternFrame;
import java.awt.event.ActionEvent;
import java.util.List;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
@EditorKitMenuAction(
        id = "menuItemInstantiateNoReifiedQuality",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.ForInstancesMenu/SlotAA-Z",
        name = "New instance of no reified Quality"
)
public class InstantiateNoReifiedQualityCommand extends PatternCommand {

    private final IRI hasQualityValue = IRI.create(GufoIris.GUFO, "hasQualityValue");
    private IRI qualityType;
    private String noReifiedQuality;
    private IRI bearer;

    public void setQualityType(IRI qualityType) {
        this.qualityType = qualityType;
    }

    public void setNoReifiedQuality(String noReifiedQuality) {
        this.noReifiedQuality = noReifiedQuality;
    }

    public void setBearer(IRI bearer) {
        this.bearer = bearer;
    }
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        applier.assertDataProperty(qualityType, bearer, noReifiedQuality);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        List<IRI> qualityTypeIRIs = new EntityFilter(getOWLModelManager())
                .addSuperDataProperty(hasQualityValue)
                .entities();
        
        List<IRI> bearerIRIs = new EntityFilter(getOWLModelManager())
                .addType(GufoIris.ConcreteIndividual)
                .entities();
        
        InstantiateNoReifiedQualityPatternFrame frame = new InstantiateNoReifiedQualityPatternFrame(this);
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
