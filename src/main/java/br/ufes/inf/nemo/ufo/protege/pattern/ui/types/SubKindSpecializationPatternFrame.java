/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui.types;

import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.EntityFilter;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.types.SubKindSpecializationCommand;
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
public class SubKindSpecializationPatternFrame extends JFrame implements ActionListener {
    
    private final SubKindSpecializationCommand command;
    
    private JComboBox subkindSelection;
    private JComboBox nonkindSortalSelection;
    
    private final JLabel subkindLabel = new JLabel("gufo:SubKind to be specialized: ");
    private final JLabel nonkindSortalLabel = new JLabel("Non-kind gufo:Sortal to be generalized: ");
    
    private List<IRI> subkindIRIs;
    private List<IRI> nonkindSortalIRIs;
    
    private final JPanel subkindPanel = new JPanel();
    private final JPanel nonkindSortalPanel = new JPanel();
    private final JPanel okPanel = new JPanel();
    
    public SubKindSpecializationPatternFrame(SubKindSpecializationCommand command) {
        
        this.command = command;
        
        this.setTitle("Specialize a gufo:SubKind");
        this.setLayout(new GridLayout(0, 1));
        this.setVisible(false);
    }
    
    public void setSubKindIRIs(List<IRI> IRIs) {
        subkindIRIs = IRIs;
    }

    public void setNonKindSortalIRIs(List<IRI> IRIs) {
        this.nonkindSortalIRIs = IRIs;
    }
    
    public void display() {
        Object[] boxList;
        
        boxList = subkindIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.subkindSelection = new JComboBox(boxList);
        this.subkindSelection.setActionCommand("SubKind selected");
        this.subkindSelection.addActionListener(this);
        
        boxList = nonkindSortalIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.nonkindSortalSelection = new JComboBox(boxList);
        
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        subkindPanel.add(subkindLabel);
        subkindPanel.add(subkindSelection);
        nonkindSortalPanel.add(nonkindSortalLabel);
        nonkindSortalPanel.add(nonkindSortalSelection);
        okPanel.add(ok);
        okPanel.add(cancel);
        
        this.add(subkindPanel);
        this.add(nonkindSortalPanel);
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
        IRI subkind, nonkindSortal;
        
        try {
            switch (action) {
                case "OK":
                    index = subkindSelection.getSelectedIndex();
                    subkind = subkindIRIs.get(index);

                    index = nonkindSortalSelection.getSelectedIndex();
                    nonkindSortal = nonkindSortalIRIs.get(index);

                    command.setSubKind(subkind);
                    command.setNonKindSortal(nonkindSortal);

                    command.runCommand();
                    setVisible(false);
                    break;
                case "SubKind selected":
                    {
                        index = subkindSelection.getSelectedIndex();
                        subkind = subkindIRIs.get(index);

                        this.nonkindSortalPanel.remove(this.nonkindSortalSelection);

                        this.nonkindSortalIRIs = new EntityFilter(command.getOWLModelManager())
                                .isOfType(GufoIris.Sortal)
                                .isNotOfType(GufoIris.Kind)
                                .hasSameKindOf(subkind)
                                .isNotDirectSubClassOf(subkind)
                                .isDifferentFrom(subkind)
                                .entities();

                        Object[] boxList = nonkindSortalIRIs.stream()
                                .map(iri -> iri.getShortForm())
                                .toArray();

                        this.nonkindSortalSelection = new JComboBox(boxList);

                        this.nonkindSortalPanel.add(this.nonkindSortalSelection);

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
