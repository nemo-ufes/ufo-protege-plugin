/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.types;

import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.protege.annotations.EditorKitMenuAction;
import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.EntityFilter;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternApplier;
import br.ufes.inf.nemo.ufo.protege.pattern.ui.types.PhaseMixinPatternFrame;
import java.awt.event.ActionEvent;
import java.util.List;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
/* @EditorKitMenuAction(
        id = "menuItemPhaseMixin",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.ForTypesMenu/SlotE-12",
        name = "Add phasemixin"
) */
public class PhaseMixinCommand extends PatternCommand {

    private IRI nonsortal;
    private IRI phasemixin;

    public void setNonSortal(IRI nonsortal) {
        this.nonsortal = nonsortal;
    }

    public void setPhaseMixin(IRI phasemixin) {
        this.phasemixin = phasemixin;
    }
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        applier.createNamedIndividual(phasemixin);
        applier.makeInstanceOf(GufoIris.PhaseMixin, phasemixin);
        applier.createClass(phasemixin);
        applier.addSubClassTo(nonsortal, phasemixin);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        List<IRI> nonsortalIRIs = new EntityFilter(getOWLModelManager())
                .isOfType(GufoIris.NonSortal)
                .entities();
        
        PhaseMixinPatternFrame frame = new PhaseMixinPatternFrame(this);
        frame.setNonSortalIRIs(nonsortalIRIs);
        frame.display();
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
