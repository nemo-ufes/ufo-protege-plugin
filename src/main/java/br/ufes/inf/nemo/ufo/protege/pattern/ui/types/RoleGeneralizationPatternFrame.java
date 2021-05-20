/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui.types;

import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.EntityFilter;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.types.RoleGeneralizationCommand;
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
public class RoleGeneralizationPatternFrame extends JFrame implements ActionListener {
    
    private final RoleGeneralizationCommand command;
    
    private JComboBox superTypeSelection;
    private JComboBox roleSelection;
    
    private final JLabel superTypeLabel = new JLabel("General gufo:EndurantType: ");
    private final JLabel roleLabel = new JLabel("gufo:Role to be generalized: ");
    
    private List<IRI> superTypeIRIs;
    private List<IRI> roleIRIs;
    
    private final JPanel superTypePanel = new JPanel();
    private final JPanel rolePanel = new JPanel();
    private final JPanel okPanel = new JPanel();
    
    public RoleGeneralizationPatternFrame(RoleGeneralizationCommand command) {
        
        this.command = command;
        
        this.setTitle("Generalize a gufo:Role");
        this.setLayout(new GridLayout(0, 1));
        this.setVisible(false);
    }
    
    public void setSuperTypeIRIs(List<IRI> IRIs) {
        superTypeIRIs = IRIs;
    }
    
    public void setRoleIRIs(List<IRI> IRIs) {
        roleIRIs = IRIs;
    }
    
    public void display() {
        Object[] boxList = superTypeIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.superTypeSelection = new JComboBox(boxList);
        
        boxList = roleIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.roleSelection = new JComboBox(boxList);
        this.roleSelection.setActionCommand("Role selected");
        this.roleSelection.addActionListener(this);
        
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        rolePanel.add(roleLabel);
        rolePanel.add(roleSelection);
        superTypePanel.add(superTypeLabel);
        superTypePanel.add(superTypeSelection);
        okPanel.add(ok);
        okPanel.add(cancel);
        
        this.add(rolePanel);
        this.add(superTypePanel);
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
        IRI role, superType;
        
        try {
            switch(action) {
                case "OK":
                    index = roleSelection.getSelectedIndex();
                    role = roleIRIs.get(index);
                    
                    index = superTypeSelection.getSelectedIndex();
                    superType = superTypeIRIs.get(index);

                    command.setSuperType(superType);
                    command.setRole(role);
                    command.runCommand();
                    setVisible(false);
                    
                case "Role selected":
                    index = roleSelection.getSelectedIndex();
                    role = roleIRIs.get(index);

                    this.superTypePanel.remove(this.superTypeSelection);

                    this.superTypeIRIs = new EntityFilter(command.getOWLModelManager())
                            .isOfType(GufoIris.Sortal)
                            .isNotOfType(GufoIris.Phase)
                            .hasSameKindOf(role)
                            .isNotSubClassByType(role, GufoIris.Phase)
                            .isNotDirectSuperClassOf(role)
                            .isDifferentFrom(role)
                            .unionWith()
                            .isOfType(GufoIris.Sortal)
                            .isOfType(GufoIris.Phase)
                            .hasSameKindOf(role)
                            .isNotSubClassOf(role)
                            .isNotDirectSuperClassOf(role)
                            .unionWith()
                            .isOfType(GufoIris.NonSortal)
                            .hasOntologicalNatureOf(role)
                            .isNotDirectSuperClassOf(role)
                            .entities();

                    Object[] boxList = superTypeIRIs.stream()
                            .map(iri -> iri.getShortForm())
                            .toArray();

                    this.superTypeSelection = new JComboBox(boxList);

                    this.superTypePanel.add(this.superTypeSelection);

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
