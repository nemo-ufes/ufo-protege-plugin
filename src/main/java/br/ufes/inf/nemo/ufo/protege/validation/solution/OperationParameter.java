/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.validation.solution;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author luciano
 */
public class OperationParameter {

    private final List<ChangeListener> listeners = new ArrayList<>();
    private final Set<IRI> possibleValues;
    Optional<IRI> value;

    public OperationParameter(Set<IRI> possibleValues) {
        this.possibleValues = possibleValues;
        this.value = (this.possibleValues.size() == 1) ?
                Optional.of(possibleValues.iterator().next()) :
                Optional.empty();
    }

    public void addListener(ChangeListener listener) {
        this.listeners.add(listener);
    }

    public IRI getValue() {
        return value.get();
    }

    public Optional<IRI> getOptionalValue() {
        return value;
    }

    public boolean hasValue() {
        return value.isPresent();
    }

    public boolean canChange() {
        return this.possibleValues.size() > 1;
    }

    public void setValue(IRI value) {
        if (this.value.isEmpty() || !this.value.get().equals(value)) {
            this.value = Optional.of(value);
            this.fireChangeEvent();
        }
    }

    protected void fireChangeEvent() {
        ChangeEvent event = new ChangeEvent(this);
        for (ChangeListener listener : listeners) {
            listener.stateChanged(event);
        }
    }
}
