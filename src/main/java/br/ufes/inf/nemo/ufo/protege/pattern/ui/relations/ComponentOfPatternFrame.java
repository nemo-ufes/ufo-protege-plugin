/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui.relations;

import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.EntityFilter;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.relations.ComponentOfCommand;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
public class ComponentOfPatternFrame extends JFrame implements ActionListener {
    
    private final ComponentOfCommand command;
    
    private JComboBox functionalComplexSelection;
    private JComboBox objectSelection;
    
    private final JLabel functionalComplexLabel = new JLabel("Functional Complex: ");
    private final JLabel objectLabel = new JLabel("Object to be a component: ");
    
    private List<IRI> functionalComplexIRIs;
    private List<IRI> objectIRIs;
    
    private final JPanel functionalComplexPanel = new JPanel();
    private final JPanel objectPanel = new JPanel();
    private final JPanel okPanel = new JPanel();
    
    public ComponentOfPatternFrame(ComponentOfCommand command) {
        
        this.command = command;
        
        this.setTitle("New component-of relation");
        this.setLayout(new GridLayout(0, 1));
        this.setVisible(false);
    }
    
    public void setFunctionalComplexIRIs(List<IRI> IRIs) {
        functionalComplexIRIs = IRIs;
    }

    public void setObjectIRIs(List<IRI> IRIs) {
        objectIRIs = IRIs;
    }
    
    public void display() {
        Object[] boxList;
        
        boxList = functionalComplexIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.functionalComplexSelection = new JComboBox(boxList);
        this.functionalComplexSelection.setActionCommand("Functional Complex selected");
        this.functionalComplexSelection.addActionListener(this);
        
        boxList = objectIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.objectSelection = new JComboBox(boxList);
        
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        functionalComplexPanel.add(functionalComplexLabel);
        functionalComplexPanel.add(functionalComplexSelection);
        objectPanel.add(objectLabel);
        objectPanel.add(objectSelection);
        okPanel.add(ok);
        okPanel.add(cancel);
        
        this.add(functionalComplexPanel);
        this.add(objectPanel);
        this.add(okPanel);
        
        this.pack();
        this.setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent event) {
        String action = event.getActionCommand();
        int index;
        IRI functionalComplex, component;
        
        try {
            switch (action) {
                case "OK":
                    index = functionalComplexSelection.getSelectedIndex();
                    functionalComplex = functionalComplexIRIs.get(index);
                    
                    index = objectSelection.getSelectedIndex();
                    component = objectIRIs.get(index);
                    
                    command.setFunctionalComplex(functionalComplex);
                    command.setComponent(component);
                    
                    command.runCommand();
                    setVisible(false);
                    break;
                case "Functional Complex selected":
                    index = functionalComplexSelection.getSelectedIndex();
                    functionalComplex = functionalComplexIRIs.get(index);
                    
                    this.objectPanel.remove(this.objectSelection);
                    
                    this.objectIRIs = new EntityFilter(command.getOWLModelManager())
                            .addType(GufoIris.Object)
                            .isDifferentFrom(functionalComplex)
                            .entities();
                    
                    Object[] boxList = objectIRIs.stream()
                            .map(iri -> iri.getShortForm())
                            .toArray();
                    
                    this.objectSelection = new JComboBox(boxList);
                    
                    this.objectPanel.add(this.objectSelection);
                    
                    this.pack();
                    this.repaint();
                    break;
                default:
                    setVisible(false);
                    break;
            }
        } catch(IndexOutOfBoundsException e) {
            setVisible(false);
            command.showMessage(PatternCommand.NOT_ALL_FIELDS_FILLED);
        }
    }   
}
