/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.validation;

import br.ufes.inf.nemo.ufo.protege.validation.helpers.ObjectGraph;
import br.ufes.inf.nemo.ufo.protege.validation.helpers.ObjectGraphNode;
import java.util.Set;
import java.util.stream.Collectors;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;

/**
 *
 * @author luciano
 */
public abstract class ClassRule extends Rule<OWLClass> {

    protected ObjectGraphNode classNode() {
        return get(ObjectGraph.class).getNode(getTarget());
    }

    /**
     * Register a violation when the subject subclasses an instance of an
     * specific type.
     * <br>
     * This method checks ancestor list of the subject and registers a violation
     * when given any class is in that list instantiates given type. The
     * ancestors which instantiate the type are put in a {@link Collection} and
     * that collection is stored in the field map under the given name.
     *
     * @param forbiddenType IRI of the type which cannot be instantiated by any
     *                     ancestor
     * @param fieldName Name of the field to store the list of violating
     *                     ancestors
     */
    protected void forbidAncestors(IRI forbiddenType, String fieldName) {

        Set<ObjectGraphNode> forbiddenAncestors = classNode()
                .properAncestors()
                .filter(node -> node.isInstanceOf(forbiddenType))
                .collect(Collectors.toSet())
                ;

        if (!forbiddenAncestors.isEmpty()) {
            setField(fieldName, ObjectGraphNode.closestAncestors(
                    forbiddenAncestors));
            newViolation();
        }
    }
}
