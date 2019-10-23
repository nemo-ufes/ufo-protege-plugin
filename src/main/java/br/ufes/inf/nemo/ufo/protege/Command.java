/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege;

import br.ufes.inf.nemo.protege.annotations.EditorKitMenuAction;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import org.protege.editor.owl.ui.action.ProtegeOWLAction;
import org.protege.editor.owl.ui.view.cls.ToldOWLClassHierarchyViewComponent;

/**
 *
 * @author luciano
 */
@EditorKitMenuAction(
        id = "ufopp.menuItem",
        editorKitId = "OWLEditorKit",
        path = "org.protege.editor.core.application.menu.FileMenu/SlotAA-Z",
        name = "Do something, please!"
)
public class Command extends ProtegeOWLAction {

    ToldOWLClassHierarchyViewComponent test;

    public void actionPerformed(ActionEvent ae) {
        JOptionPane.showMessageDialog(getOWLWorkspace(), "Command test!");
    }

    public void initialise() throws Exception {

    }

    public void dispose() throws Exception {

    }
}
