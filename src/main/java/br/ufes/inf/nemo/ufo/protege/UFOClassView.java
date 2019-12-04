/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege;

import br.ufes.inf.nemo.protege.annotations.ViewComponent;
import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import org.protege.editor.owl.ui.frame.cls.OWLClassDescriptionFrame;
import org.protege.editor.owl.ui.framelist.CreateClosureAxiomAction;
import org.protege.editor.owl.ui.framelist.CreateNewEquivalentClassAction;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.protege.editor.owl.ui.view.cls.AbstractOWLClassViewComponent;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;

/**
 *
 * @author luciano
 */
@ViewComponent(
        id = "ufopp.classView",
        label  = "UFO Class View",
        category = "@org.protege.ontologycategory"
)
public class UFOClassView extends AbstractOWLClassViewComponent {


    OWLFrameList<OWLClassExpression> list;

    @Override
    public void initialiseClassView() throws Exception {
        list = new OWLFrameList<>(getOWLEditorKit(), new UFOClassFrame(getOWLEditorKit()));
        setLayout(new BorderLayout());
        JScrollPane sp = new JScrollPane(list);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(sp);
//        list.addToPopupMenu(new CreateNewEquivalentClassAction<>());
    }

    @Override
    protected OWLClass updateView(OWLClass selectedClass) {
        list.setRootObject(selectedClass);
        return selectedClass;
    }

    @Override
    public void disposeView() {
        list.dispose();
    }
}
