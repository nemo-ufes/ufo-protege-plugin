/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui.types;

import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.types.PhaseMixinCommand;
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
public class PhaseMixinPatternFrame extends JFrame implements ActionListener {
    
    private final PhaseMixinCommand command;
    
    private JComboBox nonsortalSelection;
    private JTextField phasemixinName;
    
    private final JLabel nonsortalLabel = new JLabel("Non-sortal to specialize: ");
    private final JLabel phasemixinLabel = new JLabel("PhaseMixin name: ");
    
    private List<IRI> nonsortalIRIs;
    
    private final JPanel nonsortalPanel = new JPanel();
    private final JPanel phasemixinPanel = new JPanel();
    private final JPanel okPanel = new JPanel();
    
    public PhaseMixinPatternFrame(PhaseMixinCommand command) {
        
        this.command = command;
        
        this.setTitle("Add phasemixin");
        this.setLayout(new GridLayout(0, 1));
        this.setVisible(false);
    }
    
    public void setNonSortalIRIs(List<IRI> IRIs) {
        nonsortalIRIs = IRIs;
    }
    
    public void display() {
        Object[] boxList = nonsortalIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.nonsortalSelection = new JComboBox(boxList);
        
        phasemixinName = new JTextField(30);
        
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        nonsortalPanel.add(nonsortalLabel);
        nonsortalPanel.add(nonsortalSelection);
        phasemixinPanel.add(phasemixinLabel);
        phasemixinPanel.add(phasemixinName);
        okPanel.add(ok);
        okPanel.add(cancel);
        
        this.add(nonsortalPanel);
        this.add(phasemixinPanel);
        this.add(okPanel);
        
        this.pack();
        this.setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent event) {
        String action = event.getActionCommand();
        
        try {
            if(action.equals("OK")) {
                int index = nonsortalSelection.getSelectedIndex();

                IRI nonsortal = nonsortalIRIs.get(index);

                String phasemixinStr = phasemixinName.getText();
                if(phasemixinStr.trim().isEmpty()) {
                    setVisible(false);
                    command.showMessage(PatternCommand.NOT_ALL_FIELDS_FILLED);
                    return;
                }
                IRI phasemixin = IRI.create(command.getOntologyPrefix(), phasemixinStr);

                command.setNonSortal(nonsortal);
                command.setPhaseMixin(phasemixin);
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
