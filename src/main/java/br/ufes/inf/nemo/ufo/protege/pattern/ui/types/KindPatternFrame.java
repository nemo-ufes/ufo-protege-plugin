/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui.types;

import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.types.kinds.KindCommand;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
public class KindPatternFrame extends JFrame implements ActionListener {
    
    private final String endurantClassName;
    private final KindCommand command;
    
    private JTextField kindName;
    
    private final JLabel kindLabel;
    
    private final JPanel kindPanel = new JPanel();
    private final JPanel okPanel = new JPanel();
    
    public KindPatternFrame(KindCommand command) {
        
        this.command = command;
        this.endurantClassName = command.getEndurantClassName();
        this.kindLabel = new JLabel("New " + endurantClassName + " kind short name: ");
        
        this.setTitle("New " + endurantClassName + " kind");
        this.setLayout(new GridLayout(0, 1));
        this.setVisible(false);
    }
    
    public void display() {
        kindName = new JTextField(30);
        
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        kindPanel.add(kindLabel);
        kindPanel.add(kindName);
        okPanel.add(ok);
        okPanel.add(cancel);
        
        this.add(kindPanel);
        this.add(okPanel);
        
        this.pack();
        this.setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent event) {
        String action = event.getActionCommand();
        
        try {
            if(action.equals("OK")) {
                String kindStr = kindName.getText();
                if(kindStr.trim().isEmpty()) {
                    setVisible(false);
                    command.showMessage(PatternCommand.NOT_ALL_FIELDS_FILLED);
                    return;
                }
                IRI kind = IRI.create(command.getOntologyPrefix(), kindStr);

                command.setKind(kind);

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
