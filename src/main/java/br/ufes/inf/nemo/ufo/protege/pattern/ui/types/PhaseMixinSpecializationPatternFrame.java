/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui.types;

import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.EntityFilter;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.types.PhaseMixinSpecializationCommand;
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
public class PhaseMixinSpecializationPatternFrame extends JFrame implements ActionListener {
    
    private final PhaseMixinSpecializationCommand command;
    
    private JComboBox phasemixinSelection;
    private JComboBox antiRigidTypeSelection;
    
    private final JLabel phasemixinLabel = new JLabel("gufo:PhaseMixin to be specialized: ");
    private final JLabel antiRigidTypeLabel = new JLabel("Specific gufo:AntiRigidType: ");
    
    private List<IRI> phasemixinIRIs;
    private List<IRI> antiRigidTypeIRIs;
    
    private final JPanel phasemixinPanel = new JPanel();
    private final JPanel antiRigidTypePanel = new JPanel();
    private final JPanel okPanel = new JPanel();
    
    public PhaseMixinSpecializationPatternFrame(PhaseMixinSpecializationCommand command) {
        
        this.command = command;
        
        this.setTitle("Specialize a gufo:PhaseMixin");
        this.setLayout(new GridLayout(0, 1));
        this.setVisible(false);
    }
    
    public void setPhaseMixinIRIs(List<IRI> IRIs) {
        phasemixinIRIs = IRIs;
    }

    public void setAntiRigidTypeIRIs(List<IRI> IRIs) {
        this.antiRigidTypeIRIs = IRIs;
    }
    
    public void display() {
        Object[] boxList;
        
        boxList = phasemixinIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.phasemixinSelection = new JComboBox(boxList);
        this.phasemixinSelection.setActionCommand("PhaseMixin selected");
        this.phasemixinSelection.addActionListener(this);
        
        boxList = antiRigidTypeIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.antiRigidTypeSelection = new JComboBox(boxList);
        
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        phasemixinPanel.add(phasemixinLabel);
        phasemixinPanel.add(phasemixinSelection);
        antiRigidTypePanel.add(antiRigidTypeLabel);
        antiRigidTypePanel.add(antiRigidTypeSelection);
        okPanel.add(ok);
        okPanel.add(cancel);
        
        this.add(phasemixinPanel);
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
        IRI phasemixin, antiRigidType;
        
        try {
            switch (action) {
                case "OK":
                    index = phasemixinSelection.getSelectedIndex();
                    phasemixin = phasemixinIRIs.get(index);

                    index = antiRigidTypeSelection.getSelectedIndex();
                    antiRigidType = antiRigidTypeIRIs.get(index);

                    command.setPhaseMixin(phasemixin);
                    command.setAntiRigidType(antiRigidType);

                    command.runCommand();
                    setVisible(false);
                    break;
                case "PhaseMixin selected":
                    {
                        index = phasemixinSelection.getSelectedIndex();
                        phasemixin = phasemixinIRIs.get(index);

                        this.antiRigidTypePanel.remove(this.antiRigidTypeSelection);

                        this.antiRigidTypeIRIs = new EntityFilter(command.getOWLModelManager())
                                .isOfType(GufoIris.AntiRigidType)
                                .isNotOfType(GufoIris.RoleMixin)
                                .isOfOntologicalNatureOf(phasemixin)
                                .isNotSuperClassByType(phasemixin, GufoIris.RoleMixin)
                                .isNotDirectSubClassOf(phasemixin)
                                .isDifferentFrom(phasemixin)
                                .unionWith()
                                .isOfType(GufoIris.RoleMixin)
                                .isOfOntologicalNatureOf(phasemixin)
                                .isNotSuperClassOf(phasemixin)
                                .isNotDirectSubClassOf(phasemixin)
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
