/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui.types;

import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.types.RoleCommand;
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
public class RolePatternFrame extends JFrame implements ActionListener {
    
    private final RoleCommand command;
    
    private JComboBox sortalSelection;
    private JTextField roleName;
    
    private final JLabel sortalLabel = new JLabel("Sortal to specialize: ");
    private final JLabel roleLabel = new JLabel("Role name: ");
    
    private List<IRI> sortalIRIs;
    
    private final JPanel sortalPanel = new JPanel();
    private final JPanel rolePanel = new JPanel();
    private final JPanel okPanel = new JPanel();
    
    public RolePatternFrame(RoleCommand command) {
        
        this.command = command;
        
        this.setTitle("Add role");
        this.setLayout(new GridLayout(0, 1));
        this.setVisible(false);
    }
    
    public void setSortalIRIs(List<IRI> IRIs) {
        sortalIRIs = IRIs;
    }
    
    public void display() {
        Object[] boxList = sortalIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.sortalSelection = new JComboBox(boxList);
        
        roleName = new JTextField(30);
        
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        sortalPanel.add(sortalLabel);
        sortalPanel.add(sortalSelection);
        rolePanel.add(roleLabel);
        rolePanel.add(roleName);
        okPanel.add(ok);
        okPanel.add(cancel);
        
        this.add(sortalPanel);
        this.add(rolePanel);
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

                String roleStr = roleName.getText();
                if(roleStr.trim().isEmpty()) {
                    setVisible(false);
                    command.showMessage(PatternCommand.NOT_ALL_FIELDS_FILLED);
                    return;
                }
                IRI role = IRI.create(command.getOntologyPrefix(), roleStr);

                command.setSortal(sortal);
                command.setRole(role);
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
