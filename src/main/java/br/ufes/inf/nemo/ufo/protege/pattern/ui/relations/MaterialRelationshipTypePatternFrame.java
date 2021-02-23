/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui.relations;

import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.EntityFilter;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.relations.MaterialRelationshipTypeCommand;
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
public class MaterialRelationshipTypePatternFrame extends JFrame implements ActionListener {
    
    private final MaterialRelationshipTypeCommand command;
    
    private JComboBox relatorTypeSelection;
    private JComboBox domainSelection;
    private JComboBox rangeSelection;
    private JTextField relationshipTypeName;
    
    private final JLabel relatorTypeLabel = new JLabel("Relator type to be derived: ");
    private final JLabel domainLabel = new JLabel("Endurant class to be the domain: ");
    private final JLabel rangeLabel = new JLabel("Endurant class to be the range: ");
    private final JLabel relationshipTypeLabel = new JLabel("New gufo:MaterialRelationshipType short name: ");
    
    private List<IRI> relatorTypeIRIs;
    private List<IRI> endurantClassIRIs;
    
    private final JPanel relatorTypePanel = new JPanel();
    private final JPanel domainPanel = new JPanel();
    private final JPanel rangePanel = new JPanel();
    private final JPanel relationshipTypePanel = new JPanel();
    private final JPanel okPanel = new JPanel();
    
    
    public MaterialRelationshipTypePatternFrame(MaterialRelationshipTypeCommand command) {
        
        this.command = command;
        
        this.setTitle("New gufo:MaterialRelationshipType");
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
        this.domainSelection = new JComboBox(boxList);
        this.rangeSelection = new JComboBox(boxList);
        
        this.relationshipTypeName = new JTextField(30);
        
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        relatorTypePanel.add(relatorTypeLabel);
        relatorTypePanel.add(relatorTypeSelection);
        domainPanel.add(domainLabel);
        domainPanel.add(domainSelection);
        rangePanel.add(rangeLabel);
        rangePanel.add(rangeSelection);
        relationshipTypePanel.add(relationshipTypeLabel);
        relationshipTypePanel.add(relationshipTypeName);
        okPanel.add(ok);
        okPanel.add(cancel);
        
        this.add(relatorTypePanel);
        this.add(domainPanel);
        this.add(rangePanel);
        this.add(relationshipTypePanel);
        this.add(okPanel);
        
        this.pack();
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        String action = event.getActionCommand();
        int index;
        IRI relatorType, domain, range;
        
        try {
            switch (action) {
                case "OK":
                    index = relatorTypeSelection.getSelectedIndex();
                    relatorType = relatorTypeIRIs.get(index);
                    
                    index = domainSelection.getSelectedIndex();
                    domain = endurantClassIRIs.get(index);
                    
                    index = rangeSelection.getSelectedIndex();
                    range = endurantClassIRIs.get(index);
                    
                    String typeStr = relationshipTypeName.getText();
                    if(typeStr.trim().isEmpty()) {
                        setVisible(false);
                        command.showMessage(PatternCommand.NOT_ALL_FIELDS_FILLED);
                        return;
                    }
                    IRI type = IRI.create(command.getOntologyPrefix(), typeStr);
                    
                    command.setRelatorType(relatorType);
                    command.setDomain(domain);
                    command.setRange(range);
                    command.setRelationshipType(type);
                    
                    command.runCommand();
                    setVisible(false);
                    break;
                case "Relator type selected":
                    index = relatorTypeSelection.getSelectedIndex();
                    relatorType = relatorTypeIRIs.get(index);
                    
                    domainPanel.remove(domainSelection);
                    rangePanel.remove(rangeSelection);
                    
                    endurantClassIRIs = new EntityFilter(command.getOWLModelManager())
                            .addSuperClass(GufoIris.Endurant)
                            .isDifferentFrom(relatorType)
                            .entities();
                    
                    Object[] boxList = endurantClassIRIs.stream()
                        .map(iri -> iri.getShortForm())
                        .toArray();
                    
                    domainSelection = new JComboBox(boxList);
                    rangeSelection = new JComboBox(boxList);
                    
                    domainPanel.add(domainSelection);
                    rangePanel.add(rangeSelection);
                    
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
