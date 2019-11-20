/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege;

import java.util.HashSet;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLClass;

/**
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

    public String getIri() {
        return iri;
    }

    public String getParentIri() {
        return parentIri;
    }

    public int getIndex() {
        return index;
    }

    public Set<String> getChildren() {
        return children;
    }

    public boolean contains(String iri) {
        return children.contains(iri);
    }
}
