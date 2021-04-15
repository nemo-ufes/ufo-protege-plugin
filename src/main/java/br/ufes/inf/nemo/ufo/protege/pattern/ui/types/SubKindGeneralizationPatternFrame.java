/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui.types;

import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.EntityFilter;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.types.SubKindGeneralizationCommand;
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
public class SubKindGeneralizationPatternFrame extends JFrame implements ActionListener {
    
    private final SubKindGeneralizationCommand command;
    
    private JComboBox superTypeSelection;
    private JComboBox subkindSelection;
    
    private final JLabel superTypeLabel = new JLabel("Non-anti-rigid gufo:EndurantType to specialize: ");
    private final JLabel subkindLabel = new JLabel("gufo:SubKind to generalize: ");
    
    private List<IRI> superTypeIRIs;
    private List<IRI> subkindIRIs;
    
    private final JPanel superTypePanel = new JPanel();
    private final JPanel subkindPanel = new JPanel();
    private final JPanel okPanel = new JPanel();
    
    public SubKindGeneralizationPatternFrame(SubKindGeneralizationCommand command) {
        
        this.command = command;
        
        this.setTitle("Generalize a gufo:SubKind");
        this.setLayout(new GridLayout(0, 1));
        this.setVisible(false);
    }
    
    public void setSuperTypeIRIs(List<IRI> IRIs) {
        superTypeIRIs = IRIs;
    }
    
    public void setSubKindIRIs(List<IRI> IRIs) {
        subkindIRIs = IRIs;
    }
    
    public void display() {
        Object[] boxList = superTypeIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.superTypeSelection = new JComboBox(boxList);
        
        boxList = subkindIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.subkindSelection = new JComboBox(boxList);
        this.subkindSelection.setActionCommand("SubKind selected");
        this.subkindSelection.addActionListener(this);
        
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        subkindPanel.add(subkindLabel);
        subkindPanel.add(subkindSelection);
        superTypePanel.add(superTypeLabel);
        superTypePanel.add(superTypeSelection);
        okPanel.add(ok);
        okPanel.add(cancel);
        
        this.add(subkindPanel);
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
        IRI subkind, superType;
        
        try {
            switch(action) {
                case "OK":
                    index = subkindSelection.getSelectedIndex();
                    subkind = subkindIRIs.get(index);
                    
                    index = superTypeSelection.getSelectedIndex();
                    superType = superTypeIRIs.get(index);

                    command.setSuperType(superType);
                    command.setSubKind(subkind);
                    command.runCommand();
                    setVisible(false);
                    
                case "SubKind selected":
                    index = subkindSelection.getSelectedIndex();
                    subkind = subkindIRIs.get(index);

                    this.superTypePanel.remove(this.superTypeSelection);

                    this.superTypeIRIs = new EntityFilter(command.getOWLModelManager())
                            .isOfType(GufoIris.Sortal)
                            .isOfType(GufoIris.RigidType)
                            .hasSameKindOf(subkind)
                            .isNotDirectSuperClassOf(subkind)
                            .isDifferentFrom(subkind)
                            .unionWith()
                            .isOfType(GufoIris.NonSortal)
                            .isNotOfType(GufoIris.AntiRigidType)
                            .hasOntologicalNatureOf(subkind)
                            .isNotDirectSuperClassOf(subkind)
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
