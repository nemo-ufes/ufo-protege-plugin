/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui.types;

import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.types.RoleMixinOfCategoryCommand;
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
public class RoleMixinOfCategoryPatternFrame extends JFrame implements ActionListener {
    
    private final RoleMixinOfCategoryCommand command;
    
    private JComboBox categorySelection;
    private JTextField rolemixinName;
    
    private final JLabel categoryLabel = new JLabel("Category to specialize: ");
    private final JLabel rolemixinLabel = new JLabel("RoleMixin name: ");
    
    private List<IRI> categoryIRIs;
    
    private final JPanel categoryPanel = new JPanel();
    private final JPanel rolemixinPanel = new JPanel();
    private final JPanel okPanel = new JPanel();
    
    public RoleMixinOfCategoryPatternFrame(RoleMixinOfCategoryCommand command) {
        
        this.command = command;
        
        this.setTitle("Add rolemixin of category");
        this.setLayout(new GridLayout(0, 1));
        this.setVisible(false);
    }
    
    public void setCategoryIRIs(List<IRI> IRIs) {
        categoryIRIs = IRIs;
    }
    
    public void display() {
        Object[] boxList = categoryIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.categorySelection = new JComboBox(boxList);
        
        rolemixinName = new JTextField(30);
        
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        categoryPanel.add(categoryLabel);
        categoryPanel.add(categorySelection);
        rolemixinPanel.add(rolemixinLabel);
        rolemixinPanel.add(rolemixinName);
        okPanel.add(ok);
        okPanel.add(cancel);
        
        this.add(categoryPanel);
        this.add(rolemixinPanel);
        this.add(okPanel);
        
        this.pack();
        this.setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent event) {
        String action = event.getActionCommand();
        int index;
        
        try {
            switch (action) {
                case "OK":
                    index = categorySelection.getSelectedIndex();
                    IRI category = categoryIRIs.get(index);

                    String rolemixinStr = rolemixinName.getText();
                    if(rolemixinStr.trim().isEmpty()) {
                        setVisible(false);
                        command.showMessage(PatternCommand.NOT_ALL_FIELDS_FILLED);
                        return;
                    }
                    IRI rolemixin = IRI.create(command.getOntologyPrefix(), rolemixinStr);

                    command.setCategory(category);
                    command.setRoleMixin(rolemixin);

                    command.runCommand();
                    setVisible(false);
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
