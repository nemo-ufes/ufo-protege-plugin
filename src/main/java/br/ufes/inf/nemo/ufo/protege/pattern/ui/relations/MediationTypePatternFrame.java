/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui.relations;

import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.EntityFilter;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.relations.MediationTypeCommand;
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
public class MediationTypePatternFrame extends JFrame implements ActionListener {
    
    private final MediationTypeCommand command;
    
    private JComboBox relatorTypeSelection;
    private JComboBox mediatedTypeSelection;
    private JTextField mediationTypeName;
    
    private final JLabel relatorTypeLabel = new JLabel("Relator type to be the domain: ");
    private final JLabel mediatedTypeLabel = new JLabel("Endurant class to be the range (mediated type): ");
    private final JLabel mediationTypeLabel = new JLabel("Mediation type name: ");
    
    private List<IRI> relatorTypeIRIs;
    private List<IRI> endurantClassIRIs;
    
    private final JPanel relatorTypePanel = new JPanel();
    private final JPanel mediatedTypePanel = new JPanel();
    private final JPanel mediationTypePanel = new JPanel();
    private final JPanel okPanel = new JPanel();
    
    
    public MediationTypePatternFrame(MediationTypeCommand command) {
        
        this.command = command;
        
        this.setTitle("New type of mediation");
        this.setLayout(new GridLayout(0, 1));
        this.setVisible(false);
    }

    public void setRelatorTypeIRIs(List<IRI> IRIs) {
        this.relatorTypeIRIs = IRIs;
    }

    public void setEndurantClassIRIs(List<IRI> IRIs) {
        this.endurantClassIRIs = IRIs;
    }
    
    public void display() {
        Object[] boxList;
        
        boxList = relatorTypeIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.relatorTypeSelection = new JComboBox(boxList);
        this.relatorTypeSelection.setActionCommand("Relator type selected");
        this.relatorTypeSelection.addActionListener(this);
        
        boxList = endurantClassIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.mediatedTypeSelection = new JComboBox(boxList);
        
        this.mediationTypeName = new JTextField(30);
        
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        relatorTypePanel.add(relatorTypeLabel);
        relatorTypePanel.add(relatorTypeSelection);
        mediatedTypePanel.add(mediatedTypeLabel);
        mediatedTypePanel.add(mediatedTypeSelection);
        mediationTypePanel.add(mediationTypeLabel);
        mediationTypePanel.add(mediationTypeName);
        okPanel.add(ok);
        okPanel.add(cancel);
        
        this.add(relatorTypePanel);
        this.add(mediatedTypePanel);
        this.add(mediationTypePanel);
        this.add(okPanel);
        
        this.pack();
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        String action = event.getActionCommand();
        int index;
        IRI relatorType, mediatedType;
        
        try {
            switch (action) {
                case "OK":
                    index = relatorTypeSelection.getSelectedIndex();
                    relatorType = relatorTypeIRIs.get(index);
                    
                    index = mediatedTypeSelection.getSelectedIndex();
                    mediatedType = endurantClassIRIs.get(index);
                    
                    String typeStr = mediationTypeName.getText();
                    if(typeStr.trim().isEmpty()) {
                        setVisible(false);
                        command.showMessage(PatternCommand.NOT_ALL_FIELDS_FILLED);
                        return;
                    }
                    IRI mediationType = IRI.create(command.getOntologyPrefix(), typeStr);
                    
                    command.setRelatorType(relatorType);
                    command.setMediatedType(mediatedType);
                    command.setMediationType(mediationType);
                    
                    command.runCommand();
                    setVisible(false);
                    break;
                case "Relator type selected":
                    index = relatorTypeSelection.getSelectedIndex();
                    relatorType = relatorTypeIRIs.get(index);
                    
                    mediatedTypePanel.remove(mediatedTypeSelection);
                    
                    endurantClassIRIs = new EntityFilter(command.getOWLModelManager())
                            .hasSuperClass(GufoIris.Endurant)
                            .isDifferentFrom(relatorType)
                            .entities();
                    
                    Object[] boxList = endurantClassIRIs.stream()
                        .map(iri -> iri.getShortForm())
                        .toArray();
                    
                    mediatedTypeSelection = new JComboBox(boxList);
                    
                    mediatedTypePanel.add(mediatedTypeSelection);
                    
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
