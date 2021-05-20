/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui.types;

import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.EntityFilter;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.types.RoleSpecializationCommand;
import java.awt.Dimension;
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
public class RoleSpecializationPatternFrame extends JFrame implements ActionListener {
    
    private final RoleSpecializationCommand command;
    
    private JComboBox roleSelection;
    private JComboBox antiRigidSortalSelection;
    
    private final JLabel roleLabel = new JLabel("gufo:Role to be specialized: ");
    private final JLabel antiRigidSortalLabel = new JLabel("Specific anti-rigid gufo:Sortal: ");
    
    private List<IRI> roleIRIs;
    private List<IRI> antiRigidSortalIRIs;
    
    private final JPanel rolePanel = new JPanel();
    private final JPanel antiRigidSortalPanel = new JPanel();
    private final JPanel okPanel = new JPanel();
    
    public RoleSpecializationPatternFrame(RoleSpecializationCommand command) {
        
        this.command = command;
        
        this.setTitle("Specialize a gufo:Role");
        this.setLayout(new GridLayout(0, 1));
        this.setVisible(false);
    }
    
    public void setRoleIRIs(List<IRI> IRIs) {
        roleIRIs = IRIs;
    }

    public void setAntiRigidSortalIRIs(List<IRI> IRIs) {
        this.antiRigidSortalIRIs = IRIs;
    }
    
    public void display() {
        Object[] boxList;
        
        boxList = roleIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.roleSelection = new JComboBox(boxList);
        this.roleSelection.setActionCommand("Role selected");
        this.roleSelection.addActionListener(this);
        
        boxList = antiRigidSortalIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.antiRigidSortalSelection = new JComboBox(boxList);
        
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        rolePanel.add(roleLabel);
        rolePanel.add(roleSelection);
        antiRigidSortalPanel.add(antiRigidSortalLabel);
        antiRigidSortalPanel.add(antiRigidSortalSelection);
        okPanel.add(ok);
        okPanel.add(cancel);
        
        this.add(rolePanel);
        this.add(antiRigidSortalPanel);
        this.add(okPanel);
        
        this.setPreferredSize(new Dimension(600, 130));
        this.setResizable(false);
        this.pack();
        this.setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent event) {
        String action = event.getActionCommand();
        int index;
        IRI role, antiRigidSortal;
        
        try {
            switch (action) {
                case "OK":
                    index = roleSelection.getSelectedIndex();
                    role = roleIRIs.get(index);

                    index = antiRigidSortalSelection.getSelectedIndex();
                    antiRigidSortal = antiRigidSortalIRIs.get(index);

                    command.setRole(role);
                    command.setAntiRigidSortal(antiRigidSortal);

                    command.runCommand();
                    setVisible(false);
                    break;
                case "Role selected":
                    {
                        index = roleSelection.getSelectedIndex();
                        role = roleIRIs.get(index);

                        this.antiRigidSortalPanel.remove(this.antiRigidSortalSelection);

                        this.antiRigidSortalIRIs = new EntityFilter(command.getOWLModelManager())
                                .isOfType(GufoIris.Role)
                                .hasSameKindOf(role)
                                .isNotSuperClassByType(role, GufoIris.Phase)
                                .isNotDirectSubClassOf(role)
                                .isDifferentFrom(role)
                                .unionWith()
                                .isOfType(GufoIris.Phase)
                                .hasSameKindOf(role)
                                .isNotSuperClassOf(role)
                                .isNotDirectSubClassOf(role)
                                .entities();

                        Object[] boxList = antiRigidSortalIRIs.stream()
                                .map(iri -> iri.getShortForm())
                                .toArray();

                        this.antiRigidSortalSelection = new JComboBox(boxList);

                        this.antiRigidSortalPanel.add(this.antiRigidSortalSelection);

                        this.pack();
                        this.repaint();
                        break;
                    }
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
