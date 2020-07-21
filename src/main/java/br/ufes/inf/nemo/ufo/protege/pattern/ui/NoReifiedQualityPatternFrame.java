/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui;

import br.ufes.inf.nemo.ufo.protege.pattern.types.NoReifiedQualityCommand;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
public class NoReifiedQualityPatternFrame extends JFrame implements ActionListener {

    private final NoReifiedQualityCommand command;
    
    private JComboBox domainSelection;
    private final JTextField qualityTypeName;
    
    private List<IRI> concreteIndividualIRIs;
    
    public NoReifiedQualityPatternFrame(NoReifiedQualityCommand command) {
        this.command = command;
        
        this.setTitle("New no reified quality type");
        this.setVisible(false);
        this.setLayout(new FlowLayout());
        
        this.qualityTypeName = new JTextField(30);
    }
    
    public void setConcreteIndividualIRIs(List<IRI> IRIs) {
        concreteIndividualIRIs = IRIs;
    }
    
    public void display() {
        Object[] boxList = concreteIndividualIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.domainSelection = new JComboBox(boxList);
        
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        JPanel panel = new JPanel();
        panel.add(domainSelection);
        panel.add(qualityTypeName);
        panel.add(ok);
        panel.add(cancel);
        this.add(panel);
        
        this.setSize(800, 100);
        this.setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        String button = ae.getActionCommand();
        if(button.equals("OK")) {
            int index = domainSelection.getSelectedIndex();
            
            IRI domain = concreteIndividualIRIs.get(index);
            IRI qualityType = IRI.create(command.getOntologyPrefix(), qualityTypeName.getText());
            
            command.setDomain(domain);
            command.setQualityType(qualityType);
            command.runCommand();
            setVisible(false);
        } else {
            setVisible(false);
        }
    }
    
}
