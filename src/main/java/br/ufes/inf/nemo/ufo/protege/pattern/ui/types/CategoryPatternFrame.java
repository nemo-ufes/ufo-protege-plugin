/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui.types;

import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.EntityFilter;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.types.CategoryCommand;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
public class CategoryPatternFrame extends JFrame implements ActionListener {
    
    private final CategoryCommand command;
    
    private JComboBox endurantClassSelection;
    private JComboBox firstRigidSortalSelection;
    private JComboBox secondRigidSortalSelection;
    private JTextField categoryName;
    
    private final JLabel endurantClassLabel = new JLabel("Endurant class to specialize: ");
    private final JLabel firstRigidSortalLabel = new JLabel("First rigid sortal to generalize: ");
    private final JLabel secondRigidSortalLabel = new JLabel("Second rigid sortal to generalize: ");
    private final JLabel categoryLabel = new JLabel("Category name: ");
    
    private List<IRI> endurantClassIRIs;
    private List<IRI> firstRigidSortalIRIs;
    private List<IRI> secondRigidSortalIRIs;
    
    private final JPanel endurantClassPanel = new JPanel();
    private final JPanel firstRigidSortalPanel = new JPanel();
    private final JPanel secondRigidSortalPanel = new JPanel();
    private final JPanel categoryPanel = new JPanel();
    private final JPanel okPanel = new JPanel();
    
    public CategoryPatternFrame(CategoryCommand command) {
        
        this.command = command;
        
        this.setTitle("New category");
        this.setLayout(new GridLayout(0, 1));
        this.setVisible(false);
    }
    
    public void setEndurantClassIRIs(List<IRI> IRIs) {
        endurantClassIRIs = IRIs;
    }

    public void setFirstRigidSortalIRIs(List<IRI> IRIs) {
        firstRigidSortalIRIs = IRIs;
    }

    public void setSecondRigidSortalIRIs(List<IRI> IRIs) {
        secondRigidSortalIRIs = IRIs;
    }
    
    public void display() {
        Object[] boxList = endurantClassIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.endurantClassSelection = new JComboBox(boxList);
        this.endurantClassSelection.setActionCommand("Endurant class selected");
        this.endurantClassSelection.addActionListener(this);
        
        boxList = firstRigidSortalIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.firstRigidSortalSelection = new JComboBox(boxList);
        this.firstRigidSortalSelection.setActionCommand("First rigid sortal selected");
        this.firstRigidSortalSelection.addActionListener(this);
        
        boxList = secondRigidSortalIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.secondRigidSortalSelection = new JComboBox(boxList);
        
        categoryName = new JTextField(30);
        
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        endurantClassPanel.add(endurantClassLabel);
        endurantClassPanel.add(endurantClassSelection);
        firstRigidSortalPanel.add(firstRigidSortalLabel);
        firstRigidSortalPanel.add(firstRigidSortalSelection);
        secondRigidSortalPanel.add(secondRigidSortalLabel);
        secondRigidSortalPanel.add(secondRigidSortalSelection);
        categoryPanel.add(categoryLabel);
        categoryPanel.add(categoryName);
        okPanel.add(ok);
        okPanel.add(cancel);
        
        this.add(endurantClassPanel);
        this.add(firstRigidSortalPanel);
        this.add(secondRigidSortalPanel);
        this.add(categoryPanel);
        this.add(okPanel);
        
        this.pack();
        this.setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent event) {
        String action = event.getActionCommand();
        int index;
        IRI endurantClass, firstRigidSortal, secondRigidSortal;
        
        try {
            switch (action) {
                case "OK":
                    index = endurantClassSelection.getSelectedIndex();
                    endurantClass = endurantClassIRIs.get(index);
                    
                    index = firstRigidSortalSelection.getSelectedIndex();
                    firstRigidSortal = firstRigidSortalIRIs.get(index);
                    
                    index = secondRigidSortalSelection.getSelectedIndex();
                    secondRigidSortal = secondRigidSortalIRIs.get(index);

                    String categoryStr = categoryName.getText();
                    if(categoryStr.trim().isEmpty()) {
                        setVisible(false);
                        command.showMessage(PatternCommand.NOT_ALL_FIELDS_FILLED);
                        return;
                    }
                    IRI category = IRI.create(command.getOntologyPrefix(), categoryStr);

                    command.setEndurantClass(endurantClass);
                    command.setFirstRigidSortal(firstRigidSortal);
                    command.setSecondRigidSortal(secondRigidSortal);
                    command.setCategory(category);

                    command.runCommand();
                    setVisible(false);
                    break;
                case "Endurant class selected":
                    index = endurantClassSelection.getSelectedIndex();
                    endurantClass = endurantClassIRIs.get(index);

                    this.firstRigidSortalPanel.remove(this.firstRigidSortalSelection);
                    this.secondRigidSortalPanel.remove(this.secondRigidSortalSelection);

                    this.firstRigidSortalIRIs = new EntityFilter(command.getOWLModelManager())
                            .addType(GufoIris.RigidType)
                            .addType(GufoIris.Sortal)
                            .addSuperClass(endurantClass)
                            .entities();

                    Object[] boxList = firstRigidSortalIRIs.stream()
                            .map(iri -> iri.getShortForm())
                            .toArray();
                    this.firstRigidSortalSelection = new JComboBox(boxList);
                    this.firstRigidSortalSelection.setActionCommand("First rigid sortal selected");
                    this.firstRigidSortalSelection.addActionListener(this);
                    
                    firstRigidSortal = firstRigidSortalIRIs.isEmpty() ? null : firstRigidSortalIRIs.get(0);
                    this.secondRigidSortalIRIs = new EntityFilter(command.getOWLModelManager())
                            .addType(GufoIris.RigidType)
                            .addType(GufoIris.Sortal)
                            .addSuperClass(endurantClass)
                            .hasDifferentKindOf(firstRigidSortal)
                            .entities();
                    
                    boxList = secondRigidSortalIRIs.stream()
                            .map(iri -> iri.getShortForm())
                            .toArray();
                    this.secondRigidSortalSelection = new JComboBox(boxList);
                    
                    this.firstRigidSortalPanel.add(this.firstRigidSortalSelection);
                    this.secondRigidSortalPanel.add(this.secondRigidSortalSelection);

                    this.pack();
                    this.repaint();
                    break;
                case "First rigid sortal selected":
                    index = endurantClassSelection.getSelectedIndex();
                    endurantClass = endurantClassIRIs.get(index);
                    
                    index = firstRigidSortalSelection.getSelectedIndex();
                    firstRigidSortal = firstRigidSortalIRIs.get(index);
                    
                    this.secondRigidSortalPanel.remove(this.secondRigidSortalSelection);
                    
                    this.secondRigidSortalIRIs = new EntityFilter(command.getOWLModelManager())
                            .addType(GufoIris.RigidType)
                            .addType(GufoIris.Sortal)
                            .addSuperClass(endurantClass)
                            .hasDifferentKindOf(firstRigidSortal)
                            .entities();
                    
                    boxList = secondRigidSortalIRIs.stream()
                            .map(iri -> iri.getShortForm())
                            .toArray();
                    this.secondRigidSortalSelection = new JComboBox(boxList);
                    
                    this.secondRigidSortalPanel.add(this.secondRigidSortalSelection);
                    
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
