/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui.instances;

import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.instances.InstantiateQualityCommand;
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
public class InstantiateQualityPatternFrame extends JFrame implements ActionListener {

    private final InstantiateQualityCommand command;
    
    private JComboBox qualityTypeSelection;
    private JComboBox bearerSelection;
    private JTextField instanceName;
    private JTextField instanceValue;
    
    private final JLabel qualityTypeLabel = new JLabel("Quality type: ");
    private final JLabel bearerLabel = new JLabel("Concrete individual to be the bearer: ");
    private final JLabel nameLabel = new JLabel("Instance name: ");
    private final JLabel valueLabel = new JLabel("Instance value: ");
    
    private List<IRI> qualityTypeIRIs;
    private List<IRI> concreteIndividualIRIs;
    
    private final JPanel qualityTypePanel = new JPanel();
    private final JPanel bearerPanel = new JPanel();
    private final JPanel namePanel = new JPanel();
    private final JPanel valuePanel = new JPanel();
    private final JPanel okPanel = new JPanel();
    
    public InstantiateQualityPatternFrame(InstantiateQualityCommand command) {
        this.command = command;
        
        this.setTitle("New instance of Quality");
        this.setLayout(new GridLayout(0, 1));
        this.setVisible(false);
    }

    public void setQualityTypeIRIs(List<IRI> IRIs) {
        this.qualityTypeIRIs = IRIs;
    }

    public void setConcreteIndividualIRIs(List<IRI> IRIs) {
        this.concreteIndividualIRIs = IRIs;
    }
    
    public void display() {
        Object[] qualityTypeList = qualityTypeIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.qualityTypeSelection = new JComboBox(qualityTypeList);
        
        Object[] bearerList = concreteIndividualIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.bearerSelection = new JComboBox(bearerList);
        
        this.instanceName = new JTextField(30);
        this.instanceValue = new JTextField(30);
        
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        qualityTypePanel.add(qualityTypeLabel);
        qualityTypePanel.add(qualityTypeSelection);
        bearerPanel.add(bearerLabel);
        bearerPanel.add(bearerSelection);
        namePanel.add(nameLabel);
        namePanel.add(instanceName);
        valuePanel.add(valueLabel);
        valuePanel.add(instanceValue);
        okPanel.add(ok);
        okPanel.add(cancel);
        
        this.add(qualityTypePanel);
        this.add(bearerPanel);
        this.add(namePanel);
        this.add(valuePanel);
        this.add(okPanel);
        
        this.pack();
        this.setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent event) {
        String action = event.getActionCommand();
        int index;
        
        try {
            if(action.equals("OK")) {
                index = qualityTypeSelection.getSelectedIndex();
                IRI qualityType = qualityTypeIRIs.get(index);

                index = bearerSelection.getSelectedIndex();
                IRI bearer = concreteIndividualIRIs.get(index);

                String instanceStr = instanceName.getText();
                if(instanceStr.trim().isEmpty()) {
                    setVisible(false);
                    command.showMessage(PatternCommand.NOT_ALL_FIELDS_FILLED);
                    return;
                }
                IRI name = IRI.create(command.getOntologyPrefix(), instanceStr);
                String value = instanceValue.getText();

                command.setSortal(qualityType);
                command.setBearer(bearer);
                command.setQuality(name);           
                command.setQualityValue(value);

                command.runCommand();
                setVisible(false);
            } else {
                setVisible(false);
            }
        } catch(IndexOutOfBoundsException e) {
            setVisible(false);
            command.showMessage(PatternCommand.NOT_ALL_FIELDS_FILLED);
        }
    }
}
