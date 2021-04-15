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
import br.ufes.inf.nemo.ufo.protege.pattern.ui.types.SubKindGeneralizationPatternFrame;
import java.awt.event.ActionEvent;
import java.util.List;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
@EditorKitMenuAction(
        id = "menuItemSubKindGeneralization",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.GeneralizeMenu/SlotA-02",
        name = "Generalize a gufo:SubKind"
)
public class SubKindGeneralizationCommand extends PatternCommand {

    private IRI superType;
    private IRI subkind;

    public void setSuperType(IRI superType) {
        this.superType = superType;
    }

    public void setSubKind(IRI subkind) {
        this.subkind = subkind;
    }
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        applier.addSubClassTo(superType, subkind);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        List<IRI> subkindIRIs = new EntityFilter(getOWLModelManager())
                .isOfType(GufoIris.SubKind)
                .entities();
        
        IRI firstSubKind = subkindIRIs.isEmpty() ? null : subkindIRIs.get(0);
        List<IRI> superTypeIRIs = new EntityFilter(getOWLModelManager())
                .isOfType(GufoIris.Sortal)
                .isOfType(GufoIris.RigidType)
                .hasSameKindOf(firstSubKind)
                .isNotDirectSuperClassOf(firstSubKind)
                .isDifferentFrom(firstSubKind)
                .unionWith()
                .isOfType(GufoIris.NonSortal)
                .isNotOfType(GufoIris.AntiRigidType)
                .hasOntologicalNatureOf(firstSubKind)
                .isNotDirectSuperClassOf(firstSubKind)
                .entities();
        
        SubKindGeneralizationPatternFrame frame = new SubKindGeneralizationPatternFrame(this);
        frame.setSubKindIRIs(subkindIRIs);
        frame.setSuperTypeIRIs(superTypeIRIs);
        frame.display();
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
