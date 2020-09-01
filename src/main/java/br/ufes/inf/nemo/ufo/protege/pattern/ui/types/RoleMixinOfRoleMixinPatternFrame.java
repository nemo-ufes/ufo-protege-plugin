/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui.types;

import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.types.RoleMixinOfRoleMixinCommand;
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
public class RoleMixinOfRoleMixinPatternFrame extends JFrame implements ActionListener {
    
    private final RoleMixinOfRoleMixinCommand command;
    
    private JComboBox parentSelection;
    private JTextField childName;
    
    private final JLabel parentLabel = new JLabel("RoleMixin to specialize: ");
    private final JLabel childLabel = new JLabel("New RoleMixin name: ");
    
    private List<IRI> rolemixinIRIs;
    
    private final JPanel parentPanel = new JPanel();
    private final JPanel childPanel = new JPanel();
    private final JPanel okPanel = new JPanel();
    
    public RoleMixinOfRoleMixinPatternFrame(RoleMixinOfRoleMixinCommand command) {
        
        this.command = command;
        
        this.setTitle("Add rolemixin of rolemixin");
        this.setLayout(new GridLayout(0, 1));
        this.setVisible(false);
    }
    
    public void setRoleMixinIRIs(List<IRI> IRIs) {
        rolemixinIRIs = IRIs;
    }
    
    public void display() {
        Object[] boxList = rolemixinIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.parentSelection = new JComboBox(boxList);
        
        childName = new JTextField(30);
        
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        parentPanel.add(parentLabel);
        parentPanel.add(parentSelection);
        childPanel.add(childLabel);
        childPanel.add(childName);
        okPanel.add(ok);
        okPanel.add(cancel);
        
        this.add(parentPanel);
        this.add(childPanel);
        this.add(okPanel);
        
        this.pack();
        this.setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent event) {
        String action = event.getActionCommand();
        int index;
        
        try {
            switch (action) {
                case "OK":
                    index = parentSelection.getSelectedIndex();
                    IRI parent = rolemixinIRIs.get(index);

                    String childStr = childName.getText();
                    if(childStr.trim().isEmpty()) {
                        setVisible(false);
                        command.showMessage(PatternCommand.NOT_ALL_FIELDS_FILLED);
                        return;
                    }
                    IRI child = IRI.create(command.getOntologyPrefix(), childStr);

                    command.setParent(parent);
                    command.setChild(child);

                    command.runCommand();
                    setVisible(false);
                    break;
                default:
                    setVisible(false);
                    break;
            }
        } catch(IndexOutOfBoundsException e) {
            setVisible(false);
            command.showMessage(PatternCommand.NOT_ALL_FIELDS_FILLED);
        }
    }
}
