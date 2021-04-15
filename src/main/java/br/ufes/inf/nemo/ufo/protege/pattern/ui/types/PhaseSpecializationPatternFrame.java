/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui.types;

import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.EntityFilter;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.types.PhaseSpecializationCommand;
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
public class PhaseSpecializationPatternFrame extends JFrame implements ActionListener {
    
    private final PhaseSpecializationCommand command;
    
    private JComboBox phaseSelection;
    private JComboBox antiRigidSortalSelection;
    
    private final JLabel phaseLabel = new JLabel("gufo:Phase to be specialized: ");
    private final JLabel antiRigidSortalLabel = new JLabel("Anti-rigid gufo:Sortal to be generalized: ");
    
    private List<IRI> phaseIRIs;
    private List<IRI> antiRigidSortalIRIs;
    
    private final JPanel phasePanel = new JPanel();
    private final JPanel antiRigidSortalPanel = new JPanel();
    private final JPanel okPanel = new JPanel();
    
    public PhaseSpecializationPatternFrame(PhaseSpecializationCommand command) {
        
        this.command = command;
        
        this.setTitle("Specialize a gufo:Phase");
        this.setLayout(new GridLayout(0, 1));
        this.setVisible(false);
    }
    
    public void setPhaseIRIs(List<IRI> IRIs) {
        phaseIRIs = IRIs;
    }

    public void setAntiRigidSortalIRIs(List<IRI> IRIs) {
        this.antiRigidSortalIRIs = IRIs;
    }
    
    public void display() {
        Object[] boxList;
        
        boxList = phaseIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.phaseSelection = new JComboBox(boxList);
        this.phaseSelection.setActionCommand("Phase selected");
        this.phaseSelection.addActionListener(this);
        
        boxList = antiRigidSortalIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.antiRigidSortalSelection = new JComboBox(boxList);
        
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        phasePanel.add(phaseLabel);
        phasePanel.add(phaseSelection);
        antiRigidSortalPanel.add(antiRigidSortalLabel);
        antiRigidSortalPanel.add(antiRigidSortalSelection);
        okPanel.add(ok);
        okPanel.add(cancel);
        
        this.add(phasePanel);
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
        IRI phase, antiRigidSortal;
        
        try {
            switch (action) {
                case "OK":
                    index = phaseSelection.getSelectedIndex();
                    phase = phaseIRIs.get(index);

                    index = antiRigidSortalSelection.getSelectedIndex();
                    antiRigidSortal = antiRigidSortalIRIs.get(index);

                    command.setPhase(phase);
                    command.setAntiRigidSortal(antiRigidSortal);

                    command.runCommand();
                    setVisible(false);
                    break;
                case "Phase selected":
                    {
                        index = phaseSelection.getSelectedIndex();
                        phase = phaseIRIs.get(index);

                        this.antiRigidSortalPanel.remove(this.antiRigidSortalSelection);

                        this.antiRigidSortalIRIs = new EntityFilter(command.getOWLModelManager())
                                .isOfType(GufoIris.Phase)
                                .hasSameKindOf(phase)
                                .isNotSuperClassByType(phase, GufoIris.Role)
                                .isNotDirectSubClassOf(phase)
                                .isDifferentFrom(phase)
                                .unionWith()
                                .isOfType(GufoIris.Role)
                                .hasSameKindOf(phase)
                                .isNotSuperClassOf(phase)
                                .isNotDirectSubClassOf(phase)
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
