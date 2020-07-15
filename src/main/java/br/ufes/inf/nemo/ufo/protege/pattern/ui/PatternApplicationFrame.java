/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui;

import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
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
public class PatternApplicationFrame extends JFrame implements ActionListener {

    private final SubKindPatternApplication application;
    private final JComboBox box;
    private final JTextField field;
    
    private final List<IRI> iris;
    
    public PatternApplicationFrame(PatternCommand command, List<IRI> iris) {
        this.application = new SubKindPatternApplication(command);
        this.iris = iris;
        this.setTitle("Add subkind");
        this.setVisible(false);
        this.setLayout(new FlowLayout());
        
        Object[] boxList = iris.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.box = new JComboBox(boxList);
        this.field = new JTextField(30);
    }
    
    public void display() {
        this.setVisible(true);
        
        // Add buttons
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        // Build and add panel
        JPanel panel = new JPanel();
        panel.add(box);
        panel.add(field);
        panel.add(ok);
        panel.add(cancel);
        this.add(panel);
        
        this.setSize(600, 100);
        this.setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        String command = ae.getActionCommand();
        if(command.equals("OK")) {
            int index = box.getSelectedIndex();
            
            IRI rigidSortal = iris.get(index);
            IRI subkind = IRI.create(application.getOntologyPrefix(), field.getText());
            
            application.setRigidSortal(rigidSortal);
            application.setSubKind(subkind);
            application.start();
            setVisible(false);
        } else {
            setVisible(false);
        }
    }   
}
