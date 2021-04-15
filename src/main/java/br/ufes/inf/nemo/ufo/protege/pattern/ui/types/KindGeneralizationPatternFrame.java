/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui.types;

import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.EntityFilter;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.types.KindGeneralizationCommand;
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
public class KindGeneralizationPatternFrame extends JFrame implements ActionListener {
    
    private final KindGeneralizationCommand command;
    
    private JComboBox superTypeSelection;
    private JComboBox kindSelection;
    
    private final JLabel superTypeLabel = new JLabel("Non-anti-rigid gufo:NonSortal to specialize: ");
    private final JLabel kindLabel = new JLabel("gufo:Kind to generalize: ");
    
    private List<IRI> superTypeIRIs;
    private List<IRI> kindIRIs;
    
    private final JPanel superTypePanel = new JPanel();
    private final JPanel kindPanel = new JPanel();
    private final JPanel okPanel = new JPanel();
    
    public KindGeneralizationPatternFrame(KindGeneralizationCommand command) {
        
        this.command = command;
        
        this.setTitle("Generalize a gufo:Kind");
        this.setLayout(new GridLayout(0, 1));
        this.setVisible(false);
    }
    
    public void setSuperTypeIRIs(List<IRI> IRIs) {
        superTypeIRIs = IRIs;
    }
    
    public void setKindIRIs(List<IRI> IRIs) {
        kindIRIs = IRIs;
    }
    
    public void display() {
        Object[] boxList = superTypeIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.superTypeSelection = new JComboBox(boxList);
        
        boxList = kindIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.kindSelection = new JComboBox(boxList);
        this.kindSelection.setActionCommand("Kind selected");
        this.kindSelection.addActionListener(this);
        
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        kindPanel.add(kindLabel);
        kindPanel.add(kindSelection);
        superTypePanel.add(superTypeLabel);
        superTypePanel.add(superTypeSelection);
        okPanel.add(ok);
        okPanel.add(cancel);
        
        this.add(kindPanel);
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
        IRI kind, superType;
        
        try {
            switch(action) {
                case "OK":
                    index = kindSelection.getSelectedIndex();
                    kind = kindIRIs.get(index);
                    
                    index = superTypeSelection.getSelectedIndex();
                    superType = superTypeIRIs.get(index);

                    command.setSuperType(superType);
                    command.setKind(kind);
                    command.runCommand();
                    setVisible(false);
                    
                case "Kind selected":
                    index = kindSelection.getSelectedIndex();
                    kind = kindIRIs.get(index);

                    this.superTypePanel.remove(this.superTypeSelection);

                    this.superTypeIRIs = new EntityFilter(command.getOWLModelManager())
                            .isOfType(GufoIris.NonSortal)
                            .isNotOfType(GufoIris.AntiRigidType)
                            .hasOntologicalNatureOf(kind)
                            .isNotDirectSuperClassOf(kind)
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
