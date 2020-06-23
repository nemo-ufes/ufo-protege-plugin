/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.validation;

import java.util.List;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author luciano
 */
class Operation {

    private final Type type;
    private final List<IRI> data;

    public Operation(Type type, List<IRI> data) {
        this.type = type;
        this.data = data;
    }

    public Type getType() {
        return type;
    }

    public List<IRI> getData() {
        return data;
    }

    public static enum Type {
        DESCENDANT_OF,
        INSTANCE_OF,
        SUBCLASS_OF,

        ONE_OF
        ;
    }
}
