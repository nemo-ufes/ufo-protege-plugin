/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui.types;

import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.EntityFilter;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.types.RoleMixinGeneralizationCommand;
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
public class RoleMixinGeneralizationPatternFrame extends JFrame implements ActionListener {
    
    private final RoleMixinGeneralizationCommand command;
    
    private JComboBox superTypeSelection;
    private JComboBox rolemixinSelection;
    
    private final JLabel superTypeLabel = new JLabel("gufo:NonSortal to specialize: ");
    private final JLabel rolemixinLabel = new JLabel("gufo:RoleMixin to generalize: ");
    
    private List<IRI> superTypeIRIs;
    private List<IRI> rolemixinIRIs;
    
    private final JPanel superTypePanel = new JPanel();
    private final JPanel rolemixinPanel = new JPanel();
    private final JPanel okPanel = new JPanel();
    
    public RoleMixinGeneralizationPatternFrame(RoleMixinGeneralizationCommand command) {
        
        this.command = command;
        
        this.setTitle("Generalize a gufo:RoleMixin");
        this.setLayout(new GridLayout(0, 1));
        this.setVisible(false);
    }
    
    public void setSuperTypeIRIs(List<IRI> IRIs) {
        superTypeIRIs = IRIs;
    }
    
    public void setRoleMixinIRIs(List<IRI> IRIs) {
        rolemixinIRIs = IRIs;
    }
    
    public void display() {
        Object[] boxList = superTypeIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.superTypeSelection = new JComboBox(boxList);
        
        boxList = rolemixinIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.rolemixinSelection = new JComboBox(boxList);
        this.rolemixinSelection.setActionCommand("RoleMixin selected");
        this.rolemixinSelection.addActionListener(this);
        
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        rolemixinPanel.add(rolemixinLabel);
        rolemixinPanel.add(rolemixinSelection);
        superTypePanel.add(superTypeLabel);
        superTypePanel.add(superTypeSelection);
        okPanel.add(ok);
        okPanel.add(cancel);
        
        this.add(rolemixinPanel);
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
        IRI rolemixin, superType;
        
        try {
            switch(action) {
                case "OK":
                    index = rolemixinSelection.getSelectedIndex();
                    rolemixin = rolemixinIRIs.get(index);
                    
                    index = superTypeSelection.getSelectedIndex();
                    superType = superTypeIRIs.get(index);

                    command.setSuperType(superType);
                    command.setRoleMixin(rolemixin);
                    command.runCommand();
                    setVisible(false);
                    
                case "RoleMixin selected":
                    index = rolemixinSelection.getSelectedIndex();
                    rolemixin = rolemixinIRIs.get(index);

                    this.superTypePanel.remove(this.superTypeSelection);

                    this.superTypeIRIs = new EntityFilter(command.getOWLModelManager())
                            .isOfType(GufoIris.NonSortal)
                            .isNotOfType(GufoIris.PhaseMixin)
                            .hasOntologicalNatureOf(rolemixin)
                            .isNotSubClassByType(rolemixin, GufoIris.PhaseMixin)
                            .isNotDirectSuperClassOf(rolemixin)
                            .isDifferentFrom(rolemixin)
                            .unionWith()
                            .isOfType(GufoIris.PhaseMixin)
                            .hasOntologicalNatureOf(rolemixin)
                            .isNotSubClassOf(rolemixin)
                            .isNotDirectSuperClassOf(rolemixin)
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
