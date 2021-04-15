/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui.instances;

import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.EntityFilter;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.instances.InstantiateExtrinsicModeCommand;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
public class InstantiateExtrinsicModePatternFrame extends JFrame implements ActionListener {

    private final InstantiateExtrinsicModeCommand command;
    
    private JComboBox extrinsicModeTypeSelection;
    private JComboBox bearerSelection;
    private JComboBox externalDependenceSelection;
    private JTextField instanceName;
    
    private final JLabel extrinsicModeTypeLabel = new JLabel("Extrinsic mode type: ");
    private final JLabel bearerLabel = new JLabel("Concrete individual to be the bearer: ");
    private final JLabel externalDependenceLabel = new JLabel("Endurant to be the external dependence: ");
    private final JLabel instanceLabel = new JLabel("New gufo:ExtrinsicMode short name: ");
    
    private List<IRI> extrinsicModeTypeIRIs;
    private List<IRI> concreteIndividualIRIs;
    private List<IRI> endurantIRIs;
    
    private final JPanel extrinsicModeTypePanel = new JPanel();
    private final JPanel bearerPanel = new JPanel();
    private final JPanel externalDependencePanel = new JPanel();
    private final JPanel instancePanel = new JPanel();
    private final JPanel okPanel = new JPanel();
    
    public InstantiateExtrinsicModePatternFrame(InstantiateExtrinsicModeCommand command) {
        this.command = command;
        
        this.setTitle("New instance of gufo:ExtrinsicMode");
        this.setLayout(new GridLayout(0, 1));
        this.setVisible(false);
    }

    public void setExtrinsicModeTypeIRIs(List<IRI> IRIs) {
        this.extrinsicModeTypeIRIs = IRIs;
    }

    public void setConcreteIndividualIRIs(List<IRI> IRIs) {
        this.concreteIndividualIRIs = IRIs;
    }

    public void setEndurantIRIs(List<IRI> IRIs) {
        this.endurantIRIs = IRIs;
    }
    
    public void display() {
        Object[] extrinsicModeTypeList = extrinsicModeTypeIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.extrinsicModeTypeSelection = new JComboBox(extrinsicModeTypeList);
        
        Object[] bearerList = concreteIndividualIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.bearerSelection = new JComboBox(bearerList);
        this.bearerSelection.setActionCommand("Bearer selected");
        this.bearerSelection.addActionListener(this);
        
        Object[] externalDependenceList = endurantIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.externalDependenceSelection = new JComboBox(externalDependenceList);
        
        this.instanceName = new JTextField(30);
        
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        extrinsicModeTypePanel.add(extrinsicModeTypeLabel);
        extrinsicModeTypePanel.add(extrinsicModeTypeSelection);
        bearerPanel.add(bearerLabel);
        bearerPanel.add(bearerSelection);
        externalDependencePanel.add(externalDependenceLabel);
        externalDependencePanel.add(externalDependenceSelection);
        instancePanel.add(instanceLabel);
        instancePanel.add(instanceName);
        okPanel.add(ok);
        okPanel.add(cancel);
        
        this.add(extrinsicModeTypePanel);
        this.add(bearerPanel);
        this.add(externalDependencePanel);
        this.add(instancePanel);
        this.add(okPanel);
        
        this.pack();
        this.setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent event) {
        String action = event.getActionCommand();
        int index;
        IRI extrinsicModeType, bearer, externalDependence;
        
        try {
            switch (action) {
                case "OK":
                    index = extrinsicModeTypeSelection.getSelectedIndex();
                    extrinsicModeType = extrinsicModeTypeIRIs.get(index);
                    
                    index = bearerSelection.getSelectedIndex();
                    bearer = concreteIndividualIRIs.get(index);
                    
                    index = externalDependenceSelection.getSelectedIndex();
                    externalDependence = endurantIRIs.get(index);
                    
                    String instanceStr = instanceName.getText();
                    if(instanceStr.trim().isEmpty()) {
                        setVisible(false);
                        command.showMessage(PatternCommand.NOT_ALL_FIELDS_FILLED);
                        return;
                    }
                    IRI instance = IRI.create(command.getOntologyPrefix(), instanceStr);
                    
                    command.setSortal(extrinsicModeType);
                    command.setBearer(bearer);
                    command.setExternalDependence(externalDependence);
                    command.setExtrinsicMode(instance);
                    
                    command.runCommand();
                    setVisible(false);
                    break;
                case "Bearer selected":
                    index = bearerSelection.getSelectedIndex();
                    bearer = concreteIndividualIRIs.get(index);
                    
                    this.externalDependencePanel.remove(this.externalDependenceSelection);
                    
                    this.endurantIRIs = new EntityFilter(command.getOWLModelManager())
                            .isOfType(GufoIris.Endurant)
                            .isDifferentFrom(bearer)
                            .entities();
                    
                    Object[] boxList = endurantIRIs.stream()
                            .map(iri -> iri.getShortForm())
                            .toArray();
                    
                    this.externalDependenceSelection = new JComboBox(boxList);
                    
                    this.externalDependencePanel.add(this.externalDependenceSelection);
                    
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
