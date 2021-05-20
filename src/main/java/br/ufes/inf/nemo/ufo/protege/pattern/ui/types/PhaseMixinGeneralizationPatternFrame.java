/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui.types;

import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.EntityFilter;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.types.PhaseMixinGeneralizationCommand;
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
public class PhaseMixinGeneralizationPatternFrame extends JFrame implements ActionListener {
    
    private final PhaseMixinGeneralizationCommand command;
    
    private JComboBox superTypeSelection;
    private JComboBox phasemixinSelection;
    
    private final JLabel superTypeLabel = new JLabel("General gufo:NonSortal: ");
    private final JLabel phasemixinLabel = new JLabel("gufo:PhaseMixin to be generalized: ");
    
    private List<IRI> superTypeIRIs;
    private List<IRI> phasemixinIRIs;
    
    private final JPanel superTypePanel = new JPanel();
    private final JPanel phasemixinPanel = new JPanel();
    private final JPanel okPanel = new JPanel();
    
    public PhaseMixinGeneralizationPatternFrame(PhaseMixinGeneralizationCommand command) {
        
        this.command = command;
        
        this.setTitle("Generalize a gufo:PhaseMixin");
        this.setLayout(new GridLayout(0, 1));
        this.setVisible(false);
    }
    
    public void setSuperTypeIRIs(List<IRI> IRIs) {
        superTypeIRIs = IRIs;
    }
    
    public void setPhaseMixinIRIs(List<IRI> IRIs) {
        phasemixinIRIs = IRIs;
    }
    
    public void display() {
        Object[] boxList = superTypeIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.superTypeSelection = new JComboBox(boxList);
        
        boxList = phasemixinIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.phasemixinSelection = new JComboBox(boxList);
        this.phasemixinSelection.setActionCommand("PhaseMixin selected");
        this.phasemixinSelection.addActionListener(this);
        
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        phasemixinPanel.add(phasemixinLabel);
        phasemixinPanel.add(phasemixinSelection);
        superTypePanel.add(superTypeLabel);
        superTypePanel.add(superTypeSelection);
        okPanel.add(ok);
        okPanel.add(cancel);
        
        this.add(phasemixinPanel);
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
        IRI phasemixin, superType;
        
        try {
            switch(action) {
                case "OK":
                    index = phasemixinSelection.getSelectedIndex();
                    phasemixin = phasemixinIRIs.get(index);
                    
                    index = superTypeSelection.getSelectedIndex();
                    superType = superTypeIRIs.get(index);

                    command.setSuperType(superType);
                    command.setPhaseMixin(phasemixin);
                    command.runCommand();
                    setVisible(false);
                    
                case "PhaseMixin selected":
                    index = phasemixinSelection.getSelectedIndex();
                    phasemixin = phasemixinIRIs.get(index);

                    this.superTypePanel.remove(this.superTypeSelection);

                    this.superTypeIRIs = new EntityFilter(command.getOWLModelManager())
                            .isOfType(GufoIris.NonSortal)
                            .isNotOfType(GufoIris.RoleMixin)
                            .hasOntologicalNatureOf(phasemixin)
                            .isNotSubClassByType(phasemixin, GufoIris.RoleMixin)
                            .isNotDirectSuperClassOf(phasemixin)
                            .isDifferentFrom(phasemixin)
                            .unionWith()
                            .isOfType(GufoIris.RoleMixin)
                            .hasOntologicalNatureOf(phasemixin)
                            .isNotSubClassOf(phasemixin)
                            .isNotDirectSuperClassOf(phasemixin)
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
