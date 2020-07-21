/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.relations;

import br.ufes.inf.nemo.protege.annotations.EditorKitMenuAction;
import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternApplier;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
@EditorKitMenuAction(
        id = "menuItemComponentOf",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.ForRelationsMenu/SlotAA-Z",
        name = "New component-of relation"
)
public class ComponentOfCommand extends PatternCommand {

    private final IRI componentOfRelation = IRI.create(GufoIris.GUFO, "isComponentOf");
    private IRI component;
    private IRI functionalComplex;

    public void setComponent(IRI component) {
        this.component = component;
    }

    public void setFunctionalComplex(IRI functionalComplex) {
        this.functionalComplex = functionalComplex;
    }
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        applier.createRelation(componentOfRelation, component, functionalComplex);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        String input =
                JOptionPane.showInputDialog(getOWLWorkspace(),
                    "Input: \"<component: Object> <FunctionalComplex>\"." + System.lineSeparator()
                    + "Example: \"Heart Person\".")
                .trim();
        String[] names = input.split(" ");
        IRI component = IRI.create(getOntologyPrefix(), names[0]);
        IRI functionalComplex = IRI.create(getOntologyPrefix(), names[1]);

        try {
            PatternApplier applier = new PatternApplier(getOWLModelManager());
            if (applier.isInstanceOf(GufoIris.Object, component) &&
                applier.isInstanceOf(GufoIris.FunctionalComplex, functionalComplex)) {
                runCommand();
            } else {
                showMessage("Only objects can be component of functional complexes.");
            }
        } catch (Exception ex) {
            Logger.getLogger(ComponentOfCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
