/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege;

import java.util.HashSet;
import java.util.Set;

/**
 * A node in hierarchy presented by UFO Tree View component. The
 * {@link UFOBasedHierarchyViewComponent UFO Tree View} component presents
 * a hierarchy targeted at organizing the classes by . IRI's
 * captured by instances of this class are to be part of a subtree placed
 * in top of hierarchy.
 *
 * @author luciano
 */
public class HierarchyNode {

    private final String iri;
    private final String parentIri;
    private final int index;
    private final Set<String> children = new HashSet<>();

    public HierarchyNode(String iri, String parentIri, int index) {
        this.iri = iri;
        this.parentIri = parentIri;
        this.index = index;
    }

    /**
     * @return IRI of the class corresponding to node.
     */
    public String getIri() {
        return iri;
    }

    /**
     * @return IRI of the class in the parent node.
     */
    public String getParentIri() {
        return parentIri;
    }

    /**
     * @return Index of node in its parent's children list.
     */
    public int getIndex() {
        return index;
    }

    /**
     * @return Set of children nodes.
     */
    public Set<String> getChildren() {
        return children;
    }

    /**
     * Check for parenthood relationship.
     *
     * @param iri IRI to be checked.
     * @return true, if IRI correponds to one of children nodes.
     */
    public boolean isParentOf(String iri) {
        return children.contains(iri);
    }

    /**
     * Compare to other node for sorting siblings.
     *
     * @param other Sibling node to be checked.
     * @return -1, if the other node should come later in tree. +1, otherwise.
     */
    public int compareTo(HierarchyNode other) {
        return other == null ? -1 : Integer.signum(index - other.index);
    }
}
