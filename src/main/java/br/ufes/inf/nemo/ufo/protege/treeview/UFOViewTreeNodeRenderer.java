/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.treeview;

import static br.ufes.inf.nemo.ufo.protege.GufoIris.isPublicUFOClass;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTree;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.ProtegeTreeNodeRenderer;
import org.protege.editor.owl.ui.tree.OWLObjectTreeNode;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObject;

/**
 * Renderer for tree nodes in UFO Tree View.
 * <p>
 * This renderer by now only changes the tree node color for nodes representing
 * public UFO types.
 *
 * @author luciano
 */
public class UFOViewTreeNodeRenderer extends ProtegeTreeNodeRenderer {

    protected final OWLEditorKit editorKit;

    public UFOViewTreeNodeRenderer(OWLEditorKit editorKit) {
        super(editorKit);
        this.editorKit = editorKit;
    }

    @Override
    public Component getTreeCellRendererComponent(
            JTree tree, Object value, boolean selected, boolean expanded,
            boolean leaf, int row, boolean hasFocus) {
        Component result = super.getTreeCellRendererComponent(
                tree, value, selected, expanded,
                leaf, row, hasFocus);
        if(value instanceof OWLObjectTreeNode) {
            OWLObjectTreeNode<?> node = (OWLObjectTreeNode<?>) value;
            OWLObject object = node.getOWLObject();
            if (object instanceof OWLClass) {
                return editComponent(result, (OWLClass) object);
            }
        }
        return result;
    }

    protected Component editComponent(Component component, OWLClass owlClass) {

        if (isPublicUFOClass(owlClass)) {
            component.setForeground(Color.blue);
        }
        return component;
    }

}
