/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui.instances;

import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.instances.InstantiateObjectCommand;
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
public class InstantiateObjectPatternFrame extends JFrame implements ActionListener {

    private final InstantiateObjectCommand command;
    
    private JComboBox sortalSelection;
    private JTextField instanceName;
    
    private final JLabel sortalLabel = new JLabel("Sortal: ");
    private final JLabel instanceLabel = new JLabel("New gufo:Object short name: ");
    
    private List<IRI> sortalIRIs;
    
    private final JPanel sortalPanel = new JPanel();
    private final JPanel instancePanel = new JPanel();
    private final JPanel okPanel = new JPanel();
    
    public InstantiateObjectPatternFrame(InstantiateObjectCommand command) {
        this.command = command;
        
        this.setTitle("New instance of gufo:Object");
        this.setLayout(new GridLayout(0, 1));
        this.setVisible(false);
    }

    public void setSortalIRIs(List<IRI> IRIs) {
        this.sortalIRIs = IRIs;
    }
    
    public void display() {
        Object[] sortalList = sortalIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.sortalSelection = new JComboBox(sortalList);
        
        this.instanceName = new JTextField(30);
        
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        sortalPanel.add(sortalLabel);
        sortalPanel.add(sortalSelection);
        instancePanel.add(instanceLabel);
        instancePanel.add(instanceName);
        okPanel.add(ok);
        okPanel.add(cancel);
        
        this.add(sortalPanel);
        this.add(instancePanel);
        this.add(okPanel);
        
        this.pack();
        this.setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent event) {
        String action = event.getActionCommand();
        
        try {
            if(action.equals("OK")) {
                int index = sortalSelection.getSelectedIndex();
                IRI sortal = sortalIRIs.get(index);

                String instanceStr = instanceName.getText();
                if(instanceStr.trim().isEmpty()) {
                    setVisible(false);
                    command.showMessage(PatternCommand.NOT_ALL_FIELDS_FILLED);
                    return;
                }
                IRI instance = IRI.create(command.getOntologyPrefix(), instanceStr);

                command.setSortal(sortal);
                command.setInstance(instance);

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
