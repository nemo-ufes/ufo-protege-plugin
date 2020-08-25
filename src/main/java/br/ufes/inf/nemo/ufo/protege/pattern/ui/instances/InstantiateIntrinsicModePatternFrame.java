/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui.instances;

import br.ufes.inf.nemo.ufo.protege.pattern.instances.InstantiateIntrinsicModeCommand;
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
public class InstantiateIntrinsicModePatternFrame extends JFrame implements ActionListener {

    private final InstantiateIntrinsicModeCommand command;
    
    private JComboBox intrinsicModeTypeSelection;
    private JComboBox bearerSelection;
    private JTextField instanceName;
    
    private final JLabel intrinsicModeTypeLabel = new JLabel("Intrinsic mode type: ");
    private final JLabel bearerLabel = new JLabel("Concrete individual to be the bearer: ");
    private final JLabel instanceLabel = new JLabel("Instance name: ");
    
    private List<IRI> intrinsicModeTypeIRIs;
    private List<IRI> concreteIndividualIRIs;
    
    private final JPanel intrinsicModeTypePanel = new JPanel();
    private final JPanel bearerPanel = new JPanel();
    private final JPanel instancePanel = new JPanel();
    private final JPanel okPanel = new JPanel();
    
    public InstantiateIntrinsicModePatternFrame(InstantiateIntrinsicModeCommand command) {
        this.command = command;
        
        this.setTitle("New instance of IntrinsicMode");
        this.setLayout(new GridLayout(0, 1));
        this.setVisible(false);
    }

    public void setIntrinsicModeTypeIRIs(List<IRI> IRIs) {
        this.intrinsicModeTypeIRIs = IRIs;
    }

    public void setConcreteIndividualIRIs(List<IRI> IRIs) {
        this.concreteIndividualIRIs = IRIs;
    }
    
    public void display() {
        Object[] intrinsicModeTypeList = intrinsicModeTypeIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.intrinsicModeTypeSelection = new JComboBox(intrinsicModeTypeList);
        
        Object[] bearerList = concreteIndividualIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.bearerSelection = new JComboBox(bearerList);
        
        this.instanceName = new JTextField(30);
        
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        intrinsicModeTypePanel.add(intrinsicModeTypeLabel);
        intrinsicModeTypePanel.add(intrinsicModeTypeSelection);
        bearerPanel.add(bearerLabel);
        bearerPanel.add(bearerSelection);
        instancePanel.add(instanceLabel);
        instancePanel.add(instanceName);
        okPanel.add(ok);
        okPanel.add(cancel);
        
        this.add(intrinsicModeTypePanel);
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
            index = intrinsicModeTypeSelection.getSelectedIndex();
            IRI intrinsicModeType = intrinsicModeTypeIRIs.get(index);
            
            index = bearerSelection.getSelectedIndex();
            IRI bearer = concreteIndividualIRIs.get(index);
            
            IRI instance = IRI.create(command.getOntologyPrefix(), instanceName.getText());
            
            command.setSortal(intrinsicModeType);
            command.setBearer(bearer);
            command.setIntrinsicMode(instance);           
            
            command.runCommand();
            setVisible(false);
        } else {
            setVisible(false);
        }
    }
}
