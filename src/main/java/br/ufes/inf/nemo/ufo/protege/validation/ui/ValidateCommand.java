/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.validation.ui;

import br.ufes.inf.nemo.protege.annotations.EditorKitMenuAction;
import br.ufes.inf.nemo.ufo.protege.Singleton;
import br.ufes.inf.nemo.ufo.protege.sandbox.LogDocument;
import br.ufes.inf.nemo.ufo.protege.validation.Validation;
import br.ufes.inf.nemo.ufo.protege.validation.Validator;
import java.awt.event.ActionEvent;
import org.protege.editor.owl.ui.action.ProtegeOWLAction;
import org.protege.editor.owl.ui.view.cls.ToldOWLClassHierarchyViewComponent;

/**
 *
 * @author luciano
 */
@EditorKitMenuAction(
        id = "ufopp.validate.menuItem",
        path = "org.protege.editor.core.application.menu.FileMenu/SlotAA-Z",
        name = "Validate GUFO rules"
)
public class ValidateCommand extends ProtegeOWLAction {

    ToldOWLClassHierarchyViewComponent test;

    @Override
    public void actionPerformed(ActionEvent ae) {
        Validator validator = Validator.get(getOWLModelManager());
        LogDocument logDocument = Singleton.get(
                getOWLModelManager(), LogDocument.class);
        Validation.Result result = validator.validate();
        logDocument.append(result.toString());
        logDocument.append("\n");

        ValidationResultDocument resultDocument = Singleton.get(
                getOWLModelManager(), ValidationResultDocument.class);
        resultDocument.setResult(result);
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }
}
