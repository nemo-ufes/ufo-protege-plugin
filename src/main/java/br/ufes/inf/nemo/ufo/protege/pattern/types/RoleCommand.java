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
import br.ufes.inf.nemo.ufo.protege.pattern.ui.types.RolePatternFrame;
import java.awt.event.ActionEvent;
import java.util.List;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
@EditorKitMenuAction(
        id = "menuItemRole",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.ForTypesMenu/SlotAA-Z",
        name = "Add role"
)
public class RoleCommand extends PatternCommand {

    private IRI sortal;
    private IRI role;

    public void setSortal(IRI sortal) {
        this.sortal = sortal;
    }

    public void setRole(IRI role) {
        this.role = role;
    }
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        applier.createNamedIndividual(role);
        applier.makeInstanceOf(GufoIris.Role, role);
        applier.createClass(role);
        applier.addSubClassTo(sortal, role);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        List<IRI> sortalIRIs = new EntityFilter(getOWLModelManager())
                .addType(GufoIris.Sortal)
                .entities();
        
        RolePatternFrame frame = new RolePatternFrame(this);
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
