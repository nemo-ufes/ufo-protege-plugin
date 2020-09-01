/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui.types;

import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.types.NoReifiedQualityCommand;
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
public class NoReifiedQualityPatternFrame extends JFrame implements ActionListener {

    private final NoReifiedQualityCommand command;
    
    private JComboBox domainSelection;
    private JTextField qualityTypeName;
    
    private final JLabel domainLabel = new JLabel("Concrete individual class to be the domain: ");
    private final JLabel qualityTypeLabel = new JLabel("No reified quality type: ");
    
    private List<IRI> concreteIndividualClassIRIs;
    
    private final JPanel domainPanel = new JPanel();
    private final JPanel qualityTypePanel = new JPanel();
    private final JPanel okPanel = new JPanel();
    
    public NoReifiedQualityPatternFrame(NoReifiedQualityCommand command) {
        this.command = command;
        
        this.setTitle("New no reified quality type");
        this.setLayout(new GridLayout(0, 1));
        this.setVisible(false);
    }
    
    public void setConcreteIndividualClassIRIs(List<IRI> IRIs) {
        concreteIndividualClassIRIs = IRIs;
    }
    
    public void display() {
        Object[] boxList = concreteIndividualClassIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.domainSelection = new JComboBox(boxList);
        
        this.qualityTypeName = new JTextField(30);
        
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        domainPanel.add(domainLabel);
        domainPanel.add(domainSelection);
        qualityTypePanel.add(qualityTypeLabel);
        qualityTypePanel.add(qualityTypeName);
        okPanel.add(ok);
        okPanel.add(cancel);
        
        this.add(domainPanel);
        this.add(qualityTypePanel);
        this.add(okPanel);
        
        this.pack();
        this.setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent event) {
        String action = event.getActionCommand();
        
        try {
            if(action.equals("OK")) {
                int index = domainSelection.getSelectedIndex();

                IRI domain = concreteIndividualClassIRIs.get(index);

                String qualityTypeStr = qualityTypeName.getText();
                if(qualityTypeStr.trim().isEmpty()) {
                    setVisible(false);
                    command.showMessage(PatternCommand.NOT_ALL_FIELDS_FILLED);
                    return;
                }
                IRI qualityType = IRI.create(command.getOntologyPrefix(), qualityTypeStr);

                command.setDomain(domain);
                command.setQualityType(qualityType);
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
