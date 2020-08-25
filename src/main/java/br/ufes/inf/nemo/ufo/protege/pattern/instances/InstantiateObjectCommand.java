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
import br.ufes.inf.nemo.ufo.protege.pattern.ui.instances.InstantiateObjectPatternFrame;
import java.awt.event.ActionEvent;
import java.util.List;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
@EditorKitMenuAction(
        id = "menuItemInstantiateObject",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.ForInstancesMenu/SlotAA-Z",
        name = "New instance of Object"
)
public class InstantiateObjectCommand extends PatternCommand {

    private IRI sortal;
    private IRI instance;

    public void setSortal(IRI sortal) {
        this.sortal = sortal;
    }

    public void setInstance(IRI instance) {
        this.instance = instance;
    }
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        applier.createNamedIndividual(instance);
        applier.makeInstanceOf(sortal, instance);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        List<IRI> sortalIRIs = new EntityFilter(getOWLModelManager())
                .addSuperClass(GufoIris.Object)
                .addType(GufoIris.Sortal)
                .entities();
        
        InstantiateObjectPatternFrame frame = new InstantiateObjectPatternFrame(this);
        frame.setSortalIRIs(sortalIRIs);
        frame.display();
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
