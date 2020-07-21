/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.types;

import br.ufes.inf.nemo.protege.annotations.EditorKitMenuAction;
import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.EntityFilter;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternApplier;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.ui.NoReifiedQualityPatternFrame;
import java.awt.event.ActionEvent;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
@EditorKitMenuAction(
        id = "menuItemNoReifiedQuality",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.ForTypesMenu/SlotAA-Z",
        name = "New no reified quality type"
)
public class NoReifiedQualityCommand extends PatternCommand {

    private final IRI hasQualityValue = IRI.create(GufoIris.GUFO, "hasQualityValue");
    private IRI domain;
    private IRI qualityType;

    public void setDomain(IRI domain) {
        this.domain = domain;
    }

    public void setQualityType(IRI qualityType) {
        this.qualityType = qualityType;
    }
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        applier.createSubDataProperty(hasQualityValue, qualityType);
        applier.setDataPropertyDomain(qualityType, domain);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {        
        
        EntityFilter criterion = new EntityFilter(getOWLModelManager());
        criterion.addSuperClass(GufoIris.ConcreteIndividual);
        
        NoReifiedQualityPatternFrame frame = new NoReifiedQualityPatternFrame(this);
        frame.setConcreteIndividualIRIs(criterion.filterEntities());
        frame.display();
    }

    @Override
    public void initialise() throws Exception {
        
    }

    @Override
    public void dispose() throws Exception {

    }

}
