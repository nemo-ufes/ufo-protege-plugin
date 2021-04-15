/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui.types;

import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.EntityFilter;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.types.PhaseGeneralizationCommand;
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
public class PhaseGeneralizationPatternFrame extends JFrame implements ActionListener {
    
    private final PhaseGeneralizationCommand command;
    
    private JComboBox superTypeSelection;
    private JComboBox phaseSelection;
    
    private final JLabel superTypeLabel = new JLabel("gufo:EndurantType to specialize: ");
    private final JLabel phaseLabel = new JLabel("gufo:Phase to generalize: ");
    
    private List<IRI> superTypeIRIs;
    private List<IRI> phaseIRIs;
    
    private final JPanel superTypePanel = new JPanel();
    private final JPanel phasePanel = new JPanel();
    private final JPanel okPanel = new JPanel();
    
    public PhaseGeneralizationPatternFrame(PhaseGeneralizationCommand command) {
        
        this.command = command;
        
        this.setTitle("Generalize a gufo:Phase");
        this.setLayout(new GridLayout(0, 1));
        this.setVisible(false);
    }
    
    public void setSuperTypeIRIs(List<IRI> IRIs) {
        superTypeIRIs = IRIs;
    }
    
    public void setPhaseIRIs(List<IRI> IRIs) {
        phaseIRIs = IRIs;
    }
    
    public void display() {
        Object[] boxList = superTypeIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.superTypeSelection = new JComboBox(boxList);
        
        boxList = phaseIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.phaseSelection = new JComboBox(boxList);
        this.phaseSelection.setActionCommand("Phase selected");
        this.phaseSelection.addActionListener(this);
        
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        phasePanel.add(phaseLabel);
        phasePanel.add(phaseSelection);
        superTypePanel.add(superTypeLabel);
        superTypePanel.add(superTypeSelection);
        okPanel.add(ok);
        okPanel.add(cancel);
        
        this.add(phasePanel);
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
        IRI phase, superType;
        
        try {
            switch(action) {
                case "OK":
                    index = phaseSelection.getSelectedIndex();
                    phase = phaseIRIs.get(index);
                    
                    index = superTypeSelection.getSelectedIndex();
                    superType = superTypeIRIs.get(index);

                    command.setSuperType(superType);
                    command.setPhase(phase);
                    command.runCommand();
                    setVisible(false);
                    
                case "Phase selected":
                    index = phaseSelection.getSelectedIndex();
                    phase = phaseIRIs.get(index);

                    this.superTypePanel.remove(this.superTypeSelection);

                    this.superTypeIRIs = new EntityFilter(command.getOWLModelManager())
                            .isOfType(GufoIris.Sortal)
                            .isNotOfType(GufoIris.Role)
                            .hasSameKindOf(phase)
                            .isNotSubClassByType(phase, GufoIris.Role)
                            .isNotDirectSuperClassOf(phase)
                            .isDifferentFrom(phase)
                            .unionWith()
                            .isOfType(GufoIris.Sortal)
                            .isOfType(GufoIris.Role)
                            .hasSameKindOf(phase)
                            .isNotSubClassOf(phase)
                            .isNotDirectSuperClassOf(phase)
                            .unionWith()
                            .isOfType(GufoIris.NonSortal)
                            .hasOntologicalNatureOf(phase)
                            .isNotDirectSuperClassOf(phase)
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
