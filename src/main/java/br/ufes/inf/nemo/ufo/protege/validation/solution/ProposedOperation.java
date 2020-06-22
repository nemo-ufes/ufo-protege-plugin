/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.validation.solution;

import java.util.List;
import javax.swing.event.ChangeListener;

/**
 *
 * @author luciano
 */
public abstract class ProposedOperation {


    protected final OperationParameter subject;
    protected final OperationParameter verb;
    protected final OperationParameter object;

    public ProposedOperation(OperationParameter subject, OperationParameter verb, OperationParameter object) {
        this.subject = subject;
        this.verb = verb;
        this.object = object;
    }

    /**
     * Flag indicating whether the operation is ready to be applied. The
     * operation is ready to be applied when all of its parameters have defined
     * values, and any of mutually exclusive operation has not been applied.
     *
     * @return true, if the operation can be applied.
     */
    public abstract boolean canApply();

    /**
     * Execute the operation, performing the changes to ontology.
     */
    public abstract void apply();

    /**
     * Flag indicating whether the operation can be undone. The operation can
     * be undone if it has been applied.
     *
     * @return true, if the operation can be undone.
     */
    public abstract boolean canUndo();

    /**
     * Register a listener to watch for change events on the operation. Change
     * events are fired when value of any parameter or the value of
     * {@link #canApply()} change. The value change of a parameter can result in
     * changing the canApply() value. Also, applying a mutually exclusive
     * operation, can make this operation unappliable.
     *
     * @param listener Reference to listener.
     */
    public abstract void addListener(ChangeListener listener);

    /**
     * @return List of parameters of operation.
     */
    public abstract List<OperationParameter> getParameters();
}
