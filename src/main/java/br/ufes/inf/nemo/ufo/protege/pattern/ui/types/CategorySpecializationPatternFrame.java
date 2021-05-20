/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui.types;

import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.EntityFilter;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.types.CategorySpecializationCommand;
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
public class CategorySpecializationPatternFrame extends JFrame implements ActionListener {
    
    private final CategorySpecializationCommand command;
    
    private JComboBox categorySelection;
    private JComboBox endurantTypeSelection;
    
    private final JLabel categoryLabel = new JLabel("gufo:Category to be specialized: ");
    private final JLabel endurantTypeLabel = new JLabel("Specific gufo:EndurantType: ");
    
    private List<IRI> categoryIRIs;
    private List<IRI> endurantTypeIRIs;
    
    private final JPanel categoryPanel = new JPanel();
    private final JPanel endurantTypePanel = new JPanel();
    private final JPanel okPanel = new JPanel();
    
    public CategorySpecializationPatternFrame(CategorySpecializationCommand command) {
        
        this.command = command;
        
        this.setTitle("Specialize a gufo:Category");
        this.setLayout(new GridLayout(0, 1));
        this.setVisible(false);
    }
    
    public void setCategoryIRIs(List<IRI> IRIs) {
        categoryIRIs = IRIs;
    }

    public void setEndurantTypeIRIs(List<IRI> IRIs) {
        this.endurantTypeIRIs = IRIs;
    }
    
    public void display() {
        Object[] boxList;
        
        boxList = categoryIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.categorySelection = new JComboBox(boxList);
        this.categorySelection.setActionCommand("Category selected");
        this.categorySelection.addActionListener(this);
        
        boxList = endurantTypeIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.endurantTypeSelection = new JComboBox(boxList);
        
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        categoryPanel.add(categoryLabel);
        categoryPanel.add(categorySelection);
        endurantTypePanel.add(endurantTypeLabel);
        endurantTypePanel.add(endurantTypeSelection);
        okPanel.add(ok);
        okPanel.add(cancel);
        
        this.add(categoryPanel);
        this.add(endurantTypePanel);
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
        IRI category, endurantType;
        
        try {
            switch (action) {
                case "OK":
                    index = categorySelection.getSelectedIndex();
                    category = categoryIRIs.get(index);

                    index = endurantTypeSelection.getSelectedIndex();
                    endurantType = endurantTypeIRIs.get(index);

                    command.setCategory(category);
                    command.setEndurantType(endurantType);

                    command.runCommand();
                    setVisible(false);
                    break;
                case "Category selected":
                    {
                        index = categorySelection.getSelectedIndex();
                        category = categoryIRIs.get(index);

                        this.endurantTypePanel.remove(this.endurantTypeSelection);

                        this.endurantTypeIRIs = new EntityFilter(command.getOWLModelManager())
                                .isOfType(GufoIris.EndurantType)
                                .isNotOfType(GufoIris.Mixin)
                                .isOfOntologicalNatureOf(category)
                                .isNotSuperClassByType(category, GufoIris.Mixin)
                                .isNotDirectSubClassOf(category)
                                .isDifferentFrom(category)
                                .unionWith()
                                .isOfType(GufoIris.Mixin)
                                .isOfOntologicalNatureOf(category)
                                .isNotSuperClassOf(category)
                                .isNotDirectSubClassOf(category)
                                .entities();

                        Object[] boxList = endurantTypeIRIs.stream()
                                .map(iri -> iri.getShortForm())
                                .toArray();

                        this.endurantTypeSelection = new JComboBox(boxList);

                        this.endurantTypePanel.add(this.endurantTypeSelection);

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
