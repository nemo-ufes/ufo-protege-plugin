/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui.types;

import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.EntityFilter;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.types.CategoryGeneralizationCommand;
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
public class CategoryGeneralizationPatternFrame extends JFrame implements ActionListener {
    
    private final CategoryGeneralizationCommand command;
    
    private JComboBox superTypeSelection;
    private JComboBox categorySelection;
    
    private final JLabel superTypeLabel = new JLabel("General non-anti-rigid gufo:NonSortal: ");
    private final JLabel categoryLabel = new JLabel("gufo:Category to be generalized: ");
    
    private List<IRI> superTypeIRIs;
    private List<IRI> categoryIRIs;
    
    private final JPanel superTypePanel = new JPanel();
    private final JPanel categoryPanel = new JPanel();
    private final JPanel okPanel = new JPanel();
    
    public CategoryGeneralizationPatternFrame(CategoryGeneralizationCommand command) {
        
        this.command = command;
        
        this.setTitle("Generalize a gufo:Category");
        this.setLayout(new GridLayout(0, 1));
        this.setVisible(false);
    }
    
    public void setSuperTypeIRIs(List<IRI> IRIs) {
        superTypeIRIs = IRIs;
    }
    
    public void setCategoryIRIs(List<IRI> IRIs) {
        categoryIRIs = IRIs;
    }
    
    public void display() {
        Object[] boxList = superTypeIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.superTypeSelection = new JComboBox(boxList);
        
        boxList = categoryIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.categorySelection = new JComboBox(boxList);
        this.categorySelection.setActionCommand("Category selected");
        this.categorySelection.addActionListener(this);
        
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        categoryPanel.add(categoryLabel);
        categoryPanel.add(categorySelection);
        superTypePanel.add(superTypeLabel);
        superTypePanel.add(superTypeSelection);
        okPanel.add(ok);
        okPanel.add(cancel);
        
        this.add(categoryPanel);
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
        IRI category, superType;
        
        try {
            switch(action) {
                case "OK":
                    index = categorySelection.getSelectedIndex();
                    category = categoryIRIs.get(index);
                    
                    index = superTypeSelection.getSelectedIndex();
                    superType = superTypeIRIs.get(index);

                    command.setSuperType(superType);
                    command.setCategory(category);
                    command.runCommand();
                    setVisible(false);
                    
                case "Category selected":
                    index = categorySelection.getSelectedIndex();
                    category = categoryIRIs.get(index);

                    this.superTypePanel.remove(this.superTypeSelection);

                    this.superTypeIRIs = new EntityFilter(command.getOWLModelManager())
                            .isOfType(GufoIris.Category)
                            .hasOntologicalNatureOf(category)
                            .isNotSubClassByType(category, GufoIris.Mixin)
                            .isNotDirectSuperClassOf(category)
                            .isDifferentFrom(category)
                            .unionWith()
                            .isOfType(GufoIris.Mixin)
                            .hasOntologicalNatureOf(category)
                            .isNotSubClassOf(category)
                            .isNotDirectSuperClassOf(category)
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
