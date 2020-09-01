/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui.types;

import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.EntityFilter;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.types.AddToCategoryCommand;
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
public class AddToCategoryPatternFrame extends JFrame implements ActionListener {
    
    private final AddToCategoryCommand command;
    
    private JComboBox categorySelection;
    private JComboBox rigidTypeSelection;
    
    private final JLabel categoryLabel = new JLabel("Category: ");
    private final JLabel rigidTypeLabel = new JLabel("Rigid type to be added: ");
    
    private List<IRI> categoryIRIs;
    private List<IRI> rigidTypeIRIs;
    
    private final JPanel categoryPanel = new JPanel();
    private final JPanel rigidTypePanel = new JPanel();
    private final JPanel okPanel = new JPanel();
    
    public AddToCategoryPatternFrame(AddToCategoryCommand command) {
        
        this.command = command;
        
        this.setTitle("Add to category");
        this.setLayout(new GridLayout(0, 1));
        this.setVisible(false);
    }
    
    public void setCategoryIRIs(List<IRI> IRIs) {
        categoryIRIs = IRIs;
    }

    public void setRigidTypeIRIs(List<IRI> IRIs) {
        this.rigidTypeIRIs = IRIs;
    }
    
    public void display() {
        Object[] boxList;
        
        boxList = categoryIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.categorySelection = new JComboBox(boxList);
        this.categorySelection.setActionCommand("Category selected");
        this.categorySelection.addActionListener(this);
        
        boxList = rigidTypeIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.rigidTypeSelection = new JComboBox(boxList);
        
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        categoryPanel.add(categoryLabel);
        categoryPanel.add(categorySelection);
        rigidTypePanel.add(rigidTypeLabel);
        rigidTypePanel.add(rigidTypeSelection);
        okPanel.add(ok);
        okPanel.add(cancel);
        
        this.add(categoryPanel);
        this.add(rigidTypePanel);
        this.add(okPanel);
        
        this.pack();
        this.setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent event) {
        String action = event.getActionCommand();
        int index;
        IRI category, rigidType;
        
        try {
            switch (action) {
                case "OK":
                    index = categorySelection.getSelectedIndex();
                    category = categoryIRIs.get(index);

                    index = rigidTypeSelection.getSelectedIndex();
                    rigidType = rigidTypeIRIs.get(index);

                    command.setCategory(category);
                    command.setRigidType(rigidType);

                    command.runCommand();
                    setVisible(false);
                    break;
                case "Category selected":
                    {
                        index = categorySelection.getSelectedIndex();
                        category = categoryIRIs.get(index);

                        this.rigidTypePanel.remove(this.rigidTypeSelection);

                        this.rigidTypeIRIs = new EntityFilter(command.getOWLModelManager())
                                .addType(GufoIris.RigidType)
                                .hasSharedSuperClasses(category)
                                .isNotSuperClassOf(category)
                                .isDifferentFrom(category)
                                .entities();

                        Object[] boxList = rigidTypeIRIs.stream()
                                .map(iri -> iri.getShortForm())
                                .toArray();

                        this.rigidTypeSelection = new JComboBox(boxList);

                        this.rigidTypePanel.add(this.rigidTypeSelection);

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
