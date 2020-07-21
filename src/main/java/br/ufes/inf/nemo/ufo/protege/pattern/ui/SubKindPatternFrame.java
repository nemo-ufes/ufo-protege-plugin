/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui;

import br.ufes.inf.nemo.ufo.protege.pattern.types.SubKindCommand;
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
public class SubKindPatternFrame extends JFrame implements ActionListener {
    
    private final SubKindCommand command;
    
    private JComboBox rigidSortalSelection;
    private final JTextField subkindName;
    
    private List<IRI> rigidSortalIRIs;
    
    public SubKindPatternFrame(SubKindCommand command) {
        
        this.command = command;
        
        this.setTitle("Add subkind");
        this.setVisible(false);
        this.setLayout(new FlowLayout());
        
        this.subkindName = new JTextField(30);
    }
    
    public void setRigidSortalIRIs(List<IRI> IRIs) {
        rigidSortalIRIs = IRIs;
    }
    
    public void display() {
        Object[] boxList = rigidSortalIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.rigidSortalSelection = new JComboBox(boxList);
        
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        JPanel panel = new JPanel();
        panel.add(rigidSortalSelection);
        panel.add(subkindName);
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
            int index = rigidSortalSelection.getSelectedIndex();
            
            IRI rigidSortal = rigidSortalIRIs.get(index);
            IRI subkind = IRI.create(command.getOntologyPrefix(), subkindName.getText());
            
            command.setRigidSortal(rigidSortal);
            command.setSubKind(subkind);
            command.runCommand();
            setVisible(false);
        } else {
            setVisible(false);
        }
    }   
}
