/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.validation.solution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author luciano
 */
public class AddAxiomOperation extends ProposedOperation {

    private boolean notifying;
    private boolean applied;

    private final List<ChangeListener> listeners = new ArrayList<>();
    private final ChangeListener listener = (event) -> this.notify(event);


    public AddAxiomOperation(OperationParameter subject, OperationParameter verb, OperationParameter object) {
        super(subject, verb, object);

        subject.addListener(listener);
        verb.addListener(listener);
        object.addListener(listener);
    }

    @Override
    public boolean canApply() {
        return !applied &&
                subject.hasValue() &&
                verb.hasValue() &&
                object.hasValue()
                ;
    }

    @Override
    public void apply() {

    }

    @Override
    public boolean canUndo() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addListener(ChangeListener listener) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<OperationParameter> getParameters() {
        return Arrays.asList(subject, verb, object);
    }

    private void notify(ChangeEvent event) {
        if (!notifying) {
            notifying = true;
            try {
                for (ChangeListener l : listeners) {
                    l.stateChanged(event);
                }
            } finally {
                notifying = false;
            }
        }
    }
}
