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
import br.ufes.inf.nemo.ufo.protege.pattern.ui.types.SubKindSpecializationPatternFrame;
import java.awt.event.ActionEvent;
import java.util.List;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
@EditorKitMenuAction(
        id = "menuItemSubKindSpecialization",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.SpecializeMenu/SlotA-06",
        name = "Specialize a gufo:SubKind"
)
public class SubKindSpecializationCommand extends PatternCommand {

    private IRI subkind;
    private IRI nonkindSortal;

    public void setSubKind(IRI subkind) {
        this.subkind = subkind;
    }

    public void setNonKindSortal(IRI nonkindSortal) {
        this.nonkindSortal = nonkindSortal;
    }
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        applier.addSubClassTo(subkind, nonkindSortal);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        List<IRI> subkindIRIs = new EntityFilter(getOWLModelManager())
                .isOfType(GufoIris.SubKind)
                .entities();
        
        IRI firstSubKind = subkindIRIs.isEmpty() ? null : subkindIRIs.get(0);
        List<IRI> nonkindSortalIRIs = new EntityFilter(getOWLModelManager())
                .isOfType(GufoIris.Sortal)
                .isNotOfType(GufoIris.Kind)
                .hasSameKindOf(firstSubKind)
                .isNotDirectSubClassOf(firstSubKind)
                .isDifferentFrom(firstSubKind)
                .entities();
        
        SubKindSpecializationPatternFrame frame = new SubKindSpecializationPatternFrame(this);
        frame.setSubKindIRIs(subkindIRIs);
        frame.setNonKindSortalIRIs(nonkindSortalIRIs);
        frame.display();
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
