/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui.types;

import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.EntityFilter;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.types.AddRoleToRoleMixinCommand;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
public class AddRoleToRoleMixinPatternFrame extends JFrame implements ActionListener {
    
    private final AddRoleToRoleMixinCommand command;
    
    private JComboBox rolemixinSelection;
    private JComboBox roleSelection;
    
    private final JLabel rolemixinLabel = new JLabel("gufo:RoleMixin: ");
    private final JLabel roleLabel = new JLabel("Short name of gufo:Role to be added: ");
    
    private List<IRI> rolemixinIRIs;
    private List<IRI> roleIRIs;
    
    private final JPanel rolemixinPanel = new JPanel();
    private final JPanel rolePanel = new JPanel();
    private final JPanel okPanel = new JPanel();
    
    public AddRoleToRoleMixinPatternFrame(AddRoleToRoleMixinCommand command) {
        
        this.command = command;
        
        this.setTitle("Add a gufo:Role to a gufo:Rolemixin");
        this.setLayout(new GridLayout(0, 1));
        this.setVisible(false);
    }
    
    public void setRoleMixinIRIs(List<IRI> IRIs) {
        rolemixinIRIs = IRIs;
    }

    public void setRoleIRIs(List<IRI> IRIs) {
        this.roleIRIs = IRIs;
    }
    
    public void display() {
        Object[] boxList;
        
        boxList = rolemixinIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.rolemixinSelection = new JComboBox(boxList);
        this.rolemixinSelection.setActionCommand("RoleMixin selected");
        this.rolemixinSelection.addActionListener(this);
        
        boxList = roleIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.roleSelection = new JComboBox(boxList);
        
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        rolemixinPanel.add(rolemixinLabel);
        rolemixinPanel.add(rolemixinSelection);
        rolePanel.add(roleLabel);
        rolePanel.add(roleSelection);
        okPanel.add(ok);
        okPanel.add(cancel);
        
        this.add(rolemixinPanel);
        this.add(rolePanel);
        this.add(okPanel);
        
        this.pack();
        this.setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent event) {
        String action = event.getActionCommand();
        int index;
        IRI rolemixin, role;
        
        try {
            switch (action) {
                case "OK":
                    index = rolemixinSelection.getSelectedIndex();
                    rolemixin = rolemixinIRIs.get(index);

                    index = roleSelection.getSelectedIndex();
                    role = roleIRIs.get(index);

                    command.setRoleMixin(rolemixin);
                    command.setRole(role);

                    command.runCommand();
                    setVisible(false);
                    break;
                case "RoleMixin selected":
                    index = rolemixinSelection.getSelectedIndex();
                    rolemixin = rolemixinIRIs.get(index);
                    
                    rolePanel.remove(roleSelection);
                    
                    this.roleIRIs = new EntityFilter(command.getOWLModelManager())
                            .isOfType(GufoIris.Role)
                            .isOfOntologicalNatureOf(rolemixin)
                            .isNotSubClassOf(rolemixin)
                            .entities();

                    Object[] boxList = roleIRIs.stream()
                            .map(iri -> iri.getShortForm())
                            .toArray();

                        this.roleSelection = new JComboBox(boxList);
                    
                    rolePanel.add(roleSelection);
                    
                    this.pack();
                    this.repaint();
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
