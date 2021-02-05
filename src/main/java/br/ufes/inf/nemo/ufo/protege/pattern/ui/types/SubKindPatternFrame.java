/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui.types;

import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.types.SubKindCommand;
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
public class SubKindPatternFrame extends JFrame implements ActionListener {
    
    private final SubKindCommand command;
    
    private JComboBox rigidSortalSelection;
    private JTextField subkindName;
    
    private final JLabel rigidSortalLabel = new JLabel("Rigid sortal to specialize: ");
    private final JLabel subkindLabel = new JLabel("gufo:SubKind short name: ");
    
    private List<IRI> rigidSortalIRIs;
    
    private final JPanel rigidSortalPanel = new JPanel();
    private final JPanel subkindPanel = new JPanel();
    private final JPanel okPanel = new JPanel();
    
    public SubKindPatternFrame(SubKindCommand command) {
        
        this.command = command;
        
        this.setTitle("Add a gufo:SubKind to a rigid sortal");
        this.setLayout(new GridLayout(0, 1));
        this.setVisible(false);
    }
    
    public void setRigidSortalIRIs(List<IRI> IRIs) {
        rigidSortalIRIs = IRIs;
    }
    
    public void display() {
        Object[] boxList = rigidSortalIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.rigidSortalSelection = new JComboBox(boxList);
        
        subkindName = new JTextField(30);
        
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        rigidSortalPanel.add(rigidSortalLabel);
        rigidSortalPanel.add(rigidSortalSelection);
        subkindPanel.add(subkindLabel);
        subkindPanel.add(subkindName);
        okPanel.add(ok);
        okPanel.add(cancel);
        
        this.add(rigidSortalPanel);
        this.add(subkindPanel);
        this.add(okPanel);
        
        this.pack();
        this.setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent event) {
        String action = event.getActionCommand();
        
        try {
            if(action.equals("OK")) {
                int index = rigidSortalSelection.getSelectedIndex();

                IRI rigidSortal = rigidSortalIRIs.get(index);

                String subkindStr = subkindName.getText();
                if(subkindStr.trim().isEmpty()) {
                    setVisible(false);
                    command.showMessage(PatternCommand.NOT_ALL_FIELDS_FILLED);
                    return;
                }
                IRI subkind = IRI.create(command.getOntologyPrefix(), subkindStr);

                command.setRigidSortal(rigidSortal);
                command.setSubKind(subkind);
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
