/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui.types;

import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.EntityFilter;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.types.RoleMixinSpecializationCommand;
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
public class RoleMixinSpecializationPatternFrame extends JFrame implements ActionListener {
    
    private final RoleMixinSpecializationCommand command;
    
    private JComboBox rolemixinSelection;
    private JComboBox antiRigidTypeSelection;
    
    private final JLabel rolemixinLabel = new JLabel("gufo:RoleMixin to be specialized: ");
    private final JLabel antiRigidTypeLabel = new JLabel("Specific gufo:AntiRigidType: ");
    
    private List<IRI> rolemixinIRIs;
    private List<IRI> antiRigidTypeIRIs;
    
    private final JPanel rolemixinPanel = new JPanel();
    private final JPanel antiRigidTypePanel = new JPanel();
    private final JPanel okPanel = new JPanel();
    
    public RoleMixinSpecializationPatternFrame(RoleMixinSpecializationCommand command) {
        
        this.command = command;
        
        this.setTitle("Specialize a gufo:RoleMixin");
        this.setLayout(new GridLayout(0, 1));
        this.setVisible(false);
    }
    
    public void setRoleMixinIRIs(List<IRI> IRIs) {
        rolemixinIRIs = IRIs;
    }

    public void setAntiRigidTypeIRIs(List<IRI> IRIs) {
        this.antiRigidTypeIRIs = IRIs;
    }
    
    public void display() {
        Object[] boxList;
        
        boxList = rolemixinIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.rolemixinSelection = new JComboBox(boxList);
        this.rolemixinSelection.setActionCommand("RoleMixin selected");
        this.rolemixinSelection.addActionListener(this);
        
        boxList = antiRigidTypeIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.antiRigidTypeSelection = new JComboBox(boxList);
        
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        rolemixinPanel.add(rolemixinLabel);
        rolemixinPanel.add(rolemixinSelection);
        antiRigidTypePanel.add(antiRigidTypeLabel);
        antiRigidTypePanel.add(antiRigidTypeSelection);
        okPanel.add(ok);
        okPanel.add(cancel);
        
        this.add(rolemixinPanel);
        this.add(antiRigidTypePanel);
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
        IRI rolemixin, antiRigidType;
        
        try {
            switch (action) {
                case "OK":
                    index = rolemixinSelection.getSelectedIndex();
                    rolemixin = rolemixinIRIs.get(index);

                    index = antiRigidTypeSelection.getSelectedIndex();
                    antiRigidType = antiRigidTypeIRIs.get(index);

                    command.setRoleMixin(rolemixin);
                    command.setAntiRigidType(antiRigidType);

                    command.runCommand();
                    setVisible(false);
                    break;
                case "RoleMixin selected":
                    {
                        index = rolemixinSelection.getSelectedIndex();
                        rolemixin = rolemixinIRIs.get(index);

                        this.antiRigidTypePanel.remove(this.antiRigidTypeSelection);

                        this.antiRigidTypeIRIs = new EntityFilter(command.getOWLModelManager())
                                .isOfType(GufoIris.AntiRigidType)
                                .isNotOfType(GufoIris.PhaseMixin)
                                .isOfOntologicalNatureOf(rolemixin)
                                .isNotSuperClassByType(rolemixin, GufoIris.PhaseMixin)
                                .isNotDirectSubClassOf(rolemixin)
                                .isDifferentFrom(rolemixin)
                                .unionWith()
                                .isOfType(GufoIris.PhaseMixin)
                                .isOfOntologicalNatureOf(rolemixin)
                                .isNotSuperClassOf(rolemixin)
                                .isNotDirectSubClassOf(rolemixin)
                                .entities();

                        Object[] boxList = antiRigidTypeIRIs.stream()
                                .map(iri -> iri.getShortForm())
                                .toArray();

                        this.antiRigidTypeSelection = new JComboBox(boxList);

                        this.antiRigidTypePanel.add(this.antiRigidTypeSelection);

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
