/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.validation.ui;

import br.ufes.inf.nemo.protege.annotations.EditorKitMenuAction;
import br.ufes.inf.nemo.ufo.protege.Singleton;
import br.ufes.inf.nemo.ufo.protege.sandbox.LogDocument;
import br.ufes.inf.nemo.ufo.protege.validation.Result;
import br.ufes.inf.nemo.ufo.protege.validation.Validator;
import java.awt.event.ActionEvent;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.action.ProtegeOWLAction;
import org.protege.editor.owl.ui.view.cls.ToldOWLClassHierarchyViewComponent;

/**
 *
 * @author luciano
 */
@EditorKitMenuAction(
        id = "ufopp.validate.menuItem",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.PatternMenu/SlotB-A",
        name = "Validate GUFO rules"
)
public class ValidateCommand extends ProtegeOWLAction {

    ToldOWLClassHierarchyViewComponent test;

    public static void run(final OWLModelManager owlModelManager) {
        Validator validator = Validator.get(owlModelManager);
        LogDocument logDocument = Singleton.get(owlModelManager, LogDocument.class);
        Result result = validator.validate();
        logDocument.append(result.toString());
        logDocument.append("\n");

        ValidationResultDocument resultDocument = Singleton.get(owlModelManager, ValidationResultDocument.class);
        resultDocument.setResult(result);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        run(getOWLModelManager());
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }
}
