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
import br.ufes.inf.nemo.ufo.protege.pattern.ui.types.KindGeneralizationPatternFrame;
import java.awt.event.ActionEvent;
import java.util.List;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
@EditorKitMenuAction(
        id = "menuItemKindGeneralization",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.GeneralizeMenu/SlotA-01",
        name = "Generalize a gufo:Kind"
)
public class KindGeneralizationCommand extends PatternCommand {

    private IRI superType;
    private IRI kind;

    public void setSuperType(IRI superType) {
        this.superType = superType;
    }

    public void setKind(IRI kind) {
        this.kind = kind;
    }
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        applier.addSubClassTo(superType, kind);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        List<IRI> kindIRIs = new EntityFilter(getOWLModelManager())
                .isOfType(GufoIris.Kind)
                .entities();
        
        IRI firstKind = kindIRIs.isEmpty() ? null : kindIRIs.get(0);
        List<IRI> superTypeIRIs = new EntityFilter(getOWLModelManager())
                .isOfType(GufoIris.NonSortal)
                .isNotOfType(GufoIris.AntiRigidType)
                .hasOntologicalNatureOf(firstKind)
                .isNotDirectSuperClassOf(firstKind)
                .entities();
        
        KindGeneralizationPatternFrame frame = new KindGeneralizationPatternFrame(this);
        frame.setKindIRIs(kindIRIs);
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
