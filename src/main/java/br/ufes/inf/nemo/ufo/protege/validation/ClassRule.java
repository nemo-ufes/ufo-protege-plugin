/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.validation;

import br.ufes.inf.nemo.ufo.protege.validation.helpers.ObjectGraph;
import br.ufes.inf.nemo.ufo.protege.validation.helpers.ObjectGraphNode;
import org.semanticweb.owlapi.model.OWLClass;

/**
 *
 * @author luciano
 */
public abstract class ClassRule extends Rule<OWLClass> {

    protected ObjectGraphNode classNode() {
        return get(ObjectGraph.class).getNode(target);
    }
}
