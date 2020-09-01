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
import br.ufes.inf.nemo.ufo.protege.pattern.ui.types.KindPatternFrame;
import java.awt.event.ActionEvent;
import java.util.List;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
@EditorKitMenuAction(
        id = "menuItemKind",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.ForTypesMenu/SlotAA-Z",
        name = "New kind"
)
public class KindCommand extends PatternCommand {

    private IRI endurantClass;
    private IRI kind;

    public void setEndurantClass(IRI endurantClass) {
        this.endurantClass = endurantClass;
    }

    public void setKind(IRI kind) {
        this.kind = kind;
    }
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        applier.createNamedIndividual(kind);
        applier.makeInstanceOf(GufoIris.Kind, kind);
        applier.createClass(kind);
        applier.addSubClassTo(endurantClass, kind);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        List<IRI> endurantClassIRIs = new EntityFilter(getOWLModelManager())
                .addSuperClass(GufoIris.Endurant)
                .isPublicGufoClass()
                .entities();
        
        KindPatternFrame frame = new KindPatternFrame(this);
        frame.setEndurantClassIRIs(endurantClassIRIs);
        frame.display();
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
