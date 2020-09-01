/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui.types;

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
    private JTextField categoryName;
    
    private final JLabel endurantClassLabel = new JLabel("Endurant class to specialize: ");
    private final JLabel categoryLabel = new JLabel("Category name: ");
    
    private List<IRI> endurantClassIRIs;
    
    private final JPanel endurantClassPanel = new JPanel();
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
    
    public void display() {
        Object[] boxList = endurantClassIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.endurantClassSelection = new JComboBox(boxList);
        
        categoryName = new JTextField(30);
        
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        endurantClassPanel.add(endurantClassLabel);
        endurantClassPanel.add(endurantClassSelection);
        categoryPanel.add(categoryLabel);
        categoryPanel.add(categoryName);
        okPanel.add(ok);
        okPanel.add(cancel);
        
        this.add(endurantClassPanel);
        this.add(categoryPanel);
        this.add(okPanel);
        
        this.pack();
        this.setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent event) {
        String action = event.getActionCommand();
        
        try {
            if(action.equals("OK")) {
                int index = endurantClassSelection.getSelectedIndex();

                IRI endurantClass = endurantClassIRIs.get(index);

                String categoryStr = categoryName.getText();
                if(categoryStr.trim().isEmpty()) {
                    setVisible(false);
                    command.showMessage(PatternCommand.NOT_ALL_FIELDS_FILLED);
                    return;
                }
                IRI category = IRI.create(command.getOntologyPrefix(), categoryStr);

                command.setEndurantClass(endurantClass);
                command.setCategory(category);

                command.runCommand();
                setVisible(false);
            } else {
                setVisible(false);
            }
        } catch(IndexOutOfBoundsException e) {
            setVisible(false);
            command.showMessage(PatternCommand.NOT_ALL_FIELDS_FILLED);
        }
    }   
}
