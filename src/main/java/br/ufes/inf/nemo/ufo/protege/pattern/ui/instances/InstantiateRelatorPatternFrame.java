/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui.instances;

import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.instances.InstantiateRelatorCommand;
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
public class InstantiateRelatorPatternFrame extends JFrame implements ActionListener {

    private final InstantiateRelatorCommand command;
    
    private JComboBox relatorTypeSelection;
    private JComboBox mediatedASelection;
    private JComboBox mediatedBSelection;
    private JTextField instanceName;
    
    private final JLabel relatorTypeLabel = new JLabel("Relator type: ");
    private final JLabel mediatedALabel = new JLabel("First mediated endurant: ");
    private final JLabel mediatedBLabel = new JLabel("Second mediated endurant: ");
    private final JLabel instanceLabel = new JLabel("Instance name: ");
    
    private List<IRI> relatorTypeIRIs;
    private List<IRI> endurantIRIs;
    
    private final JPanel relatorTypePanel = new JPanel();
    private final JPanel mediatedAPanel = new JPanel();
    private final JPanel mediatedBPanel = new JPanel();
    private final JPanel instancePanel = new JPanel();
    private final JPanel okPanel = new JPanel();
    
    public InstantiateRelatorPatternFrame(InstantiateRelatorCommand command) {
        this.command = command;
        
        this.setTitle("New instance of Relator");
        this.setLayout(new GridLayout(0, 1));
        this.setVisible(false);
    }

    public void setRelatorTypeIRIs(List<IRI> IRIs) {
        this.relatorTypeIRIs = IRIs;
    }

    public void setEndurantIRIs(List<IRI> IRIs) {
        this.endurantIRIs = IRIs;
    }
    
    public void display() {
        Object[] relatorTypeList = relatorTypeIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.relatorTypeSelection = new JComboBox(relatorTypeList);        
        
        Object[] mediatedList = endurantIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.mediatedASelection = new JComboBox(mediatedList);
        this.mediatedBSelection = new JComboBox(mediatedList);
        
        this.instanceName = new JTextField(30);
        
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        relatorTypePanel.add(relatorTypeLabel);
        relatorTypePanel.add(relatorTypeSelection);
        mediatedAPanel.add(mediatedALabel);
        mediatedAPanel.add(mediatedASelection);
        mediatedBPanel.add(mediatedBLabel);
        mediatedBPanel.add(mediatedBSelection);
        instancePanel.add(instanceLabel);
        instancePanel.add(instanceName);
        okPanel.add(ok);
        okPanel.add(cancel);
        
        this.add(relatorTypePanel);
        this.add(mediatedAPanel);
        this.add(mediatedBPanel);
        this.add(instancePanel);
        this.add(okPanel);
        
        this.pack();
        this.setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent event) {
        String action = event.getActionCommand();
        int index;
        
        try {
            if(action.equals("OK")) {
                index = relatorTypeSelection.getSelectedIndex();
                IRI relatorType = relatorTypeIRIs.get(index);

                index = mediatedASelection.getSelectedIndex();
                IRI mediatedA = endurantIRIs.get(index);

                index = mediatedBSelection.getSelectedIndex();
                IRI mediatedB = endurantIRIs.get(index);

                String instanceStr = instanceName.getText();
                if(instanceStr.trim().isEmpty()) {
                    setVisible(false);
                    command.showMessage(PatternCommand.NOT_ALL_FIELDS_FILLED);
                    return;
                }
                IRI instance = IRI.create(command.getOntologyPrefix(), instanceStr);

                command.setSortal(relatorType);
                command.setMediatedA(mediatedA);
                command.setMediatedB(mediatedB);
                command.setRelator(instance);           

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
