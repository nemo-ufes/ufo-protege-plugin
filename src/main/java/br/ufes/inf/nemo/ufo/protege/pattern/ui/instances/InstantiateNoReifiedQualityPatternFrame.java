/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui.instances;

import br.ufes.inf.nemo.ufo.protege.pattern.instances.InstantiateNoReifiedQualityCommand;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.semanticweb.owlapi.model.IRI;
import javax.swing.JLabel;

/**
 *
 * @author jeferson
 */
public class InstantiateNoReifiedQualityPatternFrame extends JFrame implements ActionListener {

    private final InstantiateNoReifiedQualityCommand command;
    
    private JComboBox qualityTypeSelection;
    private JComboBox bearerSelection;
    private JTextField instanceName;
    
    private final JLabel qualityTypeLabel = new JLabel("No reified quality type to instantiate: ");
    private final JLabel bearerLabel = new JLabel("Concrete individual to be the bearer: ");
    private final JLabel instanceLabel = new JLabel("Value of the instance of no reified quality: ");
    
    private List<IRI> qualityTypeIRIs;
    private List<IRI> concreteIndividualIRIs;
    
    private final JPanel qualityTypePanel = new JPanel();
    private final JPanel bearerPanel = new JPanel();
    private final JPanel instancePanel = new JPanel();
    private final JPanel okPanel = new JPanel();
    
    public InstantiateNoReifiedQualityPatternFrame(InstantiateNoReifiedQualityCommand command) {
        
        this.command = command;
        
        this.setTitle("New instance of no reified Quality");
        this.setLayout(new GridLayout(0, 1));
        this.setVisible(false);
    }
    
    public void setQualityTypeIRIs(List<IRI> IRIs) {
        qualityTypeIRIs = IRIs;
    }
    
    public void setConcreteIndividualIRIs(List<IRI> IRIs) {
        concreteIndividualIRIs = IRIs;
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
        
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        qualityTypePanel.add(qualityTypeLabel);
        qualityTypePanel.add(qualityTypeSelection);
        bearerPanel.add(bearerLabel);
        bearerPanel.add(bearerSelection);
        instancePanel.add(instanceLabel);
        instancePanel.add(instanceName);
        okPanel.add(ok);
        okPanel.add(cancel);
        
        this.add(qualityTypePanel);
        this.add(bearerPanel);
        this.add(instancePanel);
        this.add(okPanel);
        
        this.pack();
        this.setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent event) {
        String action = event.getActionCommand();
        int index;
        
        if(action.equals("OK")) {
            index = qualityTypeSelection.getSelectedIndex();
            IRI qualityType = qualityTypeIRIs.get(index);
            
            index = bearerSelection.getSelectedIndex();
            IRI bearer = concreteIndividualIRIs.get(index);
            
            String instance = instanceName.getText();
            
            command.setQualityType(qualityType);
            command.setBearer(bearer);
            command.setNoReifiedQuality(instance);
            
            command.runCommand();
            setVisible(false);
        } else {
            setVisible(false);
        }
    }
}
