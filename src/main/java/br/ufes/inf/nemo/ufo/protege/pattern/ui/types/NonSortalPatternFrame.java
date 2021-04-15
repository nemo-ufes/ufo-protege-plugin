/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui.types;

import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.types.nonsortals.NonSortalCommand;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
public class NonSortalPatternFrame extends JFrame implements ActionListener {
    
    private final String nonsortalClassName;
    private final NonSortalCommand command;
    
    private JTextField nonsortalName;
    private final JLabel nonsortalLabel;
    
    // A check box for each endurant class
    private final List<IRI> endurantClasses = new ArrayList<>();
    private final List<JCheckBox> endurantBoxes = new ArrayList<>();
    
    private final JLabel endurantClassesLabel;
    
    private final JPanel mainPanel = new JPanel();
    private final JPanel nonsortalPanel = new JPanel();
    private final JPanel endurantClassesPanel = new JPanel();
    private final JPanel okPanel = new JPanel();
    
    public NonSortalPatternFrame(NonSortalCommand command) {
        
        this.command = command;
        this.nonsortalClassName = command.getNonSortalClassName();
        this.nonsortalLabel = new JLabel("New " + nonsortalClassName + " short name: ");
        
        this.endurantClassesLabel = new JLabel(
            "The new " + nonsortalClassName + " will specialize (the union of):");
        
        this.endurantClasses.add(GufoIris.FunctionalComplex);
        this.endurantClasses.add(GufoIris.FixedCollection);
        this.endurantClasses.add(GufoIris.VariableCollection);
        this.endurantClasses.add(GufoIris.Quantity);
        this.endurantClasses.add(GufoIris.Quality);
        this.endurantClasses.add(GufoIris.IntrinsicMode);
        this.endurantClasses.add(GufoIris.ExtrinsicMode);
        this.endurantClasses.add(GufoIris.Relator);
        
        this.setTitle("Create " + nonsortalClassName);
        
        BoxLayout boxLayout = new BoxLayout(mainPanel, BoxLayout.Y_AXIS);
        mainPanel.setLayout(boxLayout);
        
        this.setVisible(false);
    }
    
    public void display() {
        nonsortalName = new JTextField(30);
        
        endurantClasses.stream()
            .forEach(endurant -> {
                JCheckBox box = new JCheckBox(endurant.getShortForm(), false);
                endurantBoxes.add(box);
            });
        endurantBoxes.get(0).setSelected(true);
        
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        nonsortalPanel.add(nonsortalLabel);
        nonsortalPanel.add(nonsortalName);
        
        endurantClassesPanel.setLayout(new BoxLayout(endurantClassesPanel, BoxLayout.Y_AXIS));
        endurantClassesPanel.add(endurantClassesLabel);
        endurantBoxes.stream()
            .forEach(box -> {
                endurantClassesPanel.add(box);
            });
        endurantClassesPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        endurantClassesPanel.setAlignmentX(LEFT_ALIGNMENT);
        
        okPanel.add(ok);
        okPanel.add(cancel);
        
        mainPanel.add(nonsortalPanel);
        mainPanel.add(endurantClassesPanel);
        mainPanel.add(okPanel);
        this.add(mainPanel);
        
        this.setPreferredSize(new Dimension(520, 380));
        this.setResizable(false);
        this.pack();
        this.setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent event) {
        String action = event.getActionCommand();
        
        try {
            if(action.equals("OK")) {
                String kindStr = nonsortalName.getText();
                if(kindStr.trim().isEmpty()) {
                    setVisible(false);
                    command.showMessage(PatternCommand.NOT_ALL_FIELDS_FILLED);
                    return;
                }
                
                IRI nonsortal = IRI.create(command.getOntologyPrefix(), kindStr);
                
                List<IRI> classes = new ArrayList<>();
                int num = this.endurantClasses.size();
                for(int i = 0; i < num; i++) {
                    if(endurantBoxes.get(i).isSelected()) {
                        classes.add(endurantClasses.get(i));
                    }
                }
                
                if(endurantClasses.isEmpty()) {
                    setVisible(false);
                    command.showMessage(PatternCommand.NOT_ALL_FIELDS_FILLED);
                    return;
                }
                
                command.setNonSortal(nonsortal);
                command.setEndurantClasses(classes);

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
