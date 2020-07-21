/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.types;

import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.protege.annotations.EditorKitMenuAction;
import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternApplier;
import java.awt.event.ActionEvent;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

/**
 *
 * @author jeferson
 */
@EditorKitMenuAction(
        id = "menuItemAddToMixin",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.ForTypesMenu/SlotAA-Z",
        name = "Add to mixin"
)
public class AddToMixinCommand extends PatternCommand {
    
    private IRI mixin;
    private IRI endurantType;

    public void setMixin(IRI mixin) {
        this.mixin = mixin;
    }

    public void setEndurantType(IRI endurantType) {
        this.endurantType = endurantType;
    }
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        
        Set<OWLSubClassOfAxiom> sharedEndurantClasses = applier.sharedSuperClassAxioms(mixin, endurantType);
        if(!sharedEndurantClasses.isEmpty()) {
            applier.makeSubClassOf(mixin, endurantType, sharedEndurantClasses);
        } else {
            showMessage("The mixin and the endurant type must share an Endurant class!");
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String input =
                JOptionPane.showInputDialog(getOWLWorkspace(), 
                    "Input: \"<Mixin> <EndurantType>\". " + System.lineSeparator()
                    + "Example: \"Sitable Chair\".")
                .trim();
        String[] names = input.split(" ");
        mixin = IRI.create(getOntologyPrefix(), names[0]);
        endurantType = IRI.create(getOntologyPrefix(), names[1]);

        try {
            PatternApplier applier = new PatternApplier(getOWLModelManager());
            if (applier.isInstanceOf(GufoIris.Mixin, mixin) &&
                applier.isInstanceOf(GufoIris.EndurantType, endurantType)) {
                runCommand();
            } else {
                showMessage("You must select a mixin and an endurant type!");
            }
        } catch (Exception ex) {
            Logger.getLogger(AddToMixinCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
