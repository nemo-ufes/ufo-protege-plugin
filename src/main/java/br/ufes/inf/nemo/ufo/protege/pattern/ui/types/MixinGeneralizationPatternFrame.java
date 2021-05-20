/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui.types;

import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.EntityFilter;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.types.MixinGeneralizationCommand;
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
public class MixinGeneralizationPatternFrame extends JFrame implements ActionListener {
    
    private final MixinGeneralizationCommand command;
    
    private JComboBox superTypeSelection;
    private JComboBox mixinSelection;
    
    private final JLabel superTypeLabel = new JLabel("General non-anti-rigid gufo:NonSortal: ");
    private final JLabel mixinLabel = new JLabel("gufo:Mixin to be generalized: ");
    
    private List<IRI> superTypeIRIs;
    private List<IRI> mixinIRIs;
    
    private final JPanel superTypePanel = new JPanel();
    private final JPanel mixinPanel = new JPanel();
    private final JPanel okPanel = new JPanel();
    
    public MixinGeneralizationPatternFrame(MixinGeneralizationCommand command) {
        
        this.command = command;
        
        this.setTitle("Generalize a gufo:Mixin");
        this.setLayout(new GridLayout(0, 1));
        this.setVisible(false);
    }
    
    public void setSuperTypeIRIs(List<IRI> IRIs) {
        superTypeIRIs = IRIs;
    }
    
    public void setMixinIRIs(List<IRI> IRIs) {
        mixinIRIs = IRIs;
    }
    
    public void display() {
        Object[] boxList = superTypeIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.superTypeSelection = new JComboBox(boxList);
        
        boxList = mixinIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.mixinSelection = new JComboBox(boxList);
        this.mixinSelection.setActionCommand("Mixin selected");
        this.mixinSelection.addActionListener(this);
        
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        mixinPanel.add(mixinLabel);
        mixinPanel.add(mixinSelection);
        superTypePanel.add(superTypeLabel);
        superTypePanel.add(superTypeSelection);
        okPanel.add(ok);
        okPanel.add(cancel);
        
        this.add(mixinPanel);
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
        IRI mixin, superType;
        
        try {
            switch(action) {
                case "OK":
                    index = mixinSelection.getSelectedIndex();
                    mixin = mixinIRIs.get(index);
                    
                    index = superTypeSelection.getSelectedIndex();
                    superType = superTypeIRIs.get(index);

                    command.setSuperType(superType);
                    command.setMixin(mixin);
                    command.runCommand();
                    setVisible(false);
                    
                case "Mixin selected":
                    index = mixinSelection.getSelectedIndex();
                    mixin = mixinIRIs.get(index);

                    this.superTypePanel.remove(this.superTypeSelection);

                    this.superTypeIRIs = new EntityFilter(command.getOWLModelManager())
                            .isOfType(GufoIris.Mixin)
                            .hasOntologicalNatureOf(mixin)
                            .isNotSubClassByType(mixin, GufoIris.Category)
                            .isNotDirectSuperClassOf(mixin)
                            .isDifferentFrom(mixin)
                            .unionWith()
                            .isOfType(GufoIris.Category)
                            .hasOntologicalNatureOf(mixin)
                            .isNotSubClassOf(mixin)
                            .isNotDirectSuperClassOf(mixin)
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
