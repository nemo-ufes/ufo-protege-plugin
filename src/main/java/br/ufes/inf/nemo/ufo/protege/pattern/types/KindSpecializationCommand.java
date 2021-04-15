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
import br.ufes.inf.nemo.ufo.protege.pattern.ui.types.KindSpecializationPatternFrame;
import java.awt.event.ActionEvent;
import java.util.List;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
@EditorKitMenuAction(
        id = "menuItemKindSpecialization",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.SpecializeMenu/SlotA-05",
        name = "Specialize a gufo:Kind"
)
public class KindSpecializationCommand extends PatternCommand {

    private IRI kind;
    private IRI sortal;

    public void setKind(IRI kind) {
        this.kind = kind;
    }

    public void setSortal(IRI sortal) {
        this.sortal = sortal;
    }
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        applier.addSubClassTo(kind, sortal);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        List<IRI> kindIRIs = new EntityFilter(getOWLModelManager())
                .isOfType(GufoIris.Kind)
                .entities();
        
        IRI firstKind = kindIRIs.isEmpty() ? null : kindIRIs.get(0);
        List<IRI> sortalIRIs = new EntityFilter(getOWLModelManager())
                .isOfType(GufoIris.Sortal)
                .hasSameKindOf(firstKind)
                .isNotDirectSubClassOf(firstKind)
                .isDifferentFrom(firstKind)
                .entities();
        
        KindSpecializationPatternFrame frame = new KindSpecializationPatternFrame(this);
        frame.setKindIRIs(kindIRIs);
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
