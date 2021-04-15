/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui.types;

import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.EntityFilter;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.types.KindSpecializationCommand;
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
public class KindSpecializationPatternFrame extends JFrame implements ActionListener {
    
    private final KindSpecializationCommand command;
    
    private JComboBox kindSelection;
    private JComboBox sortalSelection;
    
    private final JLabel kindLabel = new JLabel("gufo:Kind to be specialized: ");
    private final JLabel sortalLabel = new JLabel("gufo:Sortal to be generalized: ");
    
    private List<IRI> kindIRIs;
    private List<IRI> sortalIRIs;
    
    private final JPanel kindPanel = new JPanel();
    private final JPanel sortalPanel = new JPanel();
    private final JPanel okPanel = new JPanel();
    
    public KindSpecializationPatternFrame(KindSpecializationCommand command) {
        
        this.command = command;
        
        this.setTitle("Specialize a gufo:Kind");
        this.setLayout(new GridLayout(0, 1));
        this.setVisible(false);
    }
    
    public void setKindIRIs(List<IRI> IRIs) {
        kindIRIs = IRIs;
    }

    public void setSortalIRIs(List<IRI> IRIs) {
        this.sortalIRIs = IRIs;
    }
    
    public void display() {
        Object[] boxList;
        
        boxList = kindIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.kindSelection = new JComboBox(boxList);
        this.kindSelection.setActionCommand("Kind selected");
        this.kindSelection.addActionListener(this);
        
        boxList = sortalIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.sortalSelection = new JComboBox(boxList);
        
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        kindPanel.add(kindLabel);
        kindPanel.add(kindSelection);
        sortalPanel.add(sortalLabel);
        sortalPanel.add(sortalSelection);
        okPanel.add(ok);
        okPanel.add(cancel);
        
        this.add(kindPanel);
        this.add(sortalPanel);
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
        IRI kind, sortal;
        
        try {
            switch (action) {
                case "OK":
                    index = kindSelection.getSelectedIndex();
                    kind = kindIRIs.get(index);

                    index = sortalSelection.getSelectedIndex();
                    sortal = sortalIRIs.get(index);

                    command.setKind(kind);
                    command.setSortal(sortal);

                    command.runCommand();
                    setVisible(false);
                    break;
                case "Kind selected":
                    {
                        index = kindSelection.getSelectedIndex();
                        kind = kindIRIs.get(index);

                        this.sortalPanel.remove(this.sortalSelection);

                        this.sortalIRIs = new EntityFilter(command.getOWLModelManager())
                                .isOfType(GufoIris.Sortal)
                                .hasSameKindOf(kind)
                                .isNotDirectSubClassOf(kind)
                                .isDifferentFrom(kind)
                                .entities();

                        Object[] boxList = sortalIRIs.stream()
                                .map(iri -> iri.getShortForm())
                                .toArray();

                        this.sortalSelection = new JComboBox(boxList);

                        this.sortalPanel.add(this.sortalSelection);

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
