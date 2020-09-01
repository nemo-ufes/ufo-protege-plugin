/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui.types;

import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.types.PhaseCommand;
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
public class PhasePatternFrame extends JFrame implements ActionListener {
    
    private final PhaseCommand command;
    
    private JComboBox sortalSelection;
    private JTextField phaseName;
    
    private final JLabel sortalLabel = new JLabel("Sortal to specialize: ");
    private final JLabel phaseLabel = new JLabel("Phase name: ");
    
    private List<IRI> sortalIRIs;
    
    private final JPanel sortalPanel = new JPanel();
    private final JPanel phasePanel = new JPanel();
    private final JPanel okPanel = new JPanel();
    
    public PhasePatternFrame(PhaseCommand command) {
        
        this.command = command;
        
        this.setTitle("Add phase");
        this.setLayout(new GridLayout(0, 1));
        this.setVisible(false);
    }
    
    public void setSortalIRIs(List<IRI> IRIs) {
        sortalIRIs = IRIs;
    }
    
    public void display() {
        Object[] boxList = sortalIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.sortalSelection = new JComboBox(boxList);
        
        phaseName = new JTextField(30);
        
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        sortalPanel.add(sortalLabel);
        sortalPanel.add(sortalSelection);
        phasePanel.add(phaseLabel);
        phasePanel.add(phaseName);
        okPanel.add(ok);
        okPanel.add(cancel);
        
        this.add(sortalPanel);
        this.add(phasePanel);
        this.add(okPanel);
        
        this.pack();
        this.setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent event) {
        String action = event.getActionCommand();
        
        try {
            if(action.equals("OK")) {
                int index = sortalSelection.getSelectedIndex();

                IRI sortal = sortalIRIs.get(index);

                String phaseStr = phaseName.getText();
                if(phaseStr.trim().isEmpty()) {
                    setVisible(false);
                    command.showMessage(PatternCommand.NOT_ALL_FIELDS_FILLED);
                    return;
                }
                IRI phase = IRI.create(command.getOntologyPrefix(), phaseStr);

                command.setSortal(sortal);
                command.setPhase(phase);
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
