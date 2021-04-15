/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui.relations;

import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.EntityFilter;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.relations.ComparativeRelationshipTypeCommand;
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
public class ComparativeRelationshipTypePatternFrame extends JFrame implements ActionListener {
    
    private final ComparativeRelationshipTypeCommand command;
    
    private JComboBox qualityTypeSelection;
    private JComboBox domainAndRangeSelection;
    private JTextField relationshipTypeName;
    
    private final JLabel qualityTypeLabel = new JLabel("Quality type to be compared: ");
    private final JLabel domainAndRangeLabel = new JLabel("Class of concrete individuals to be compared: ");
    private final JLabel relationshipTypeLabel = new JLabel("New gufo:ComparativeRelationshipType short name: ");
    
    private List<IRI> qualityTypeIRIs;
    private List<IRI> concreteIndividualClassIRIs;
    
    private final JPanel qualityTypePanel = new JPanel();
    private final JPanel domainAndRangePanel = new JPanel();
    private final JPanel relationshipTypePanel = new JPanel();
    private final JPanel okPanel = new JPanel();
    
    
    public ComparativeRelationshipTypePatternFrame(ComparativeRelationshipTypeCommand command) {
        
        this.command = command;
        
        this.setTitle("New gufo:ComparativeRelationshipType");
        this.setLayout(new GridLayout(0, 1));
        this.setVisible(false);
    }

    public void setQualityTypeIRIs(List<IRI> IRIs) {
        this.qualityTypeIRIs = IRIs;
    }

    public void setConcreteIndividualClassIRIs(List<IRI> IRIs) {
        this.concreteIndividualClassIRIs = IRIs;
    }
    
    public void display() {
        Object[] boxList;
        
        boxList = qualityTypeIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.qualityTypeSelection = new JComboBox(boxList);
        this.qualityTypeSelection.setActionCommand("Quality type selected");
        this.qualityTypeSelection.addActionListener(this);
        
        boxList = concreteIndividualClassIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.domainAndRangeSelection = new JComboBox(boxList);
        
        this.relationshipTypeName = new JTextField(30);
        
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        qualityTypePanel.add(qualityTypeLabel);
        qualityTypePanel.add(qualityTypeSelection);
        domainAndRangePanel.add(domainAndRangeLabel);
        domainAndRangePanel.add(domainAndRangeSelection);
        relationshipTypePanel.add(relationshipTypeLabel);
        relationshipTypePanel.add(relationshipTypeName);
        okPanel.add(ok);
        okPanel.add(cancel);
        
        this.add(qualityTypePanel);
        this.add(domainAndRangePanel);
        this.add(relationshipTypePanel);
        this.add(okPanel);
        
        this.pack();
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        String action = event.getActionCommand();
        int index;
        IRI qualityType, domainAndRange;
        
        try {
            switch (action) {
                case "OK":
                    index = qualityTypeSelection.getSelectedIndex();
                    qualityType = qualityTypeIRIs.get(index);
                    
                    index = domainAndRangeSelection.getSelectedIndex();
                    domainAndRange = concreteIndividualClassIRIs.get(index);
                    
                    String typeStr = relationshipTypeName.getText();
                    if(typeStr.trim().isEmpty()) {
                        setVisible(false);
                        command.showMessage(PatternCommand.NOT_ALL_FIELDS_FILLED);
                        return;
                    }
                    IRI type = IRI.create(command.getOntologyPrefix(), typeStr);
                    
                    command.setQualityType(qualityType);
                    command.setDomainAndRange(domainAndRange);
                    command.setRelationshipType(type);
                    
                    command.runCommand();
                    setVisible(false);
                    break;
                case "Quality type selected":
                    index = qualityTypeSelection.getSelectedIndex();
                    qualityType = qualityTypeIRIs.get(index);
                    
                    this.domainAndRangePanel.remove(this.domainAndRangeSelection);
                    
                    this.concreteIndividualClassIRIs = new EntityFilter(command.getOWLModelManager())
                            .hasSuperClass(GufoIris.ConcreteIndividual)
                            .isDifferentFrom(qualityType)
                            .entities();
                    
                    Object[] boxList = concreteIndividualClassIRIs.stream()
                        .map(iri -> iri.getShortForm())
                        .toArray();
                    
                    this.domainAndRangeSelection = new JComboBox(boxList);
                    
                    this.domainAndRangePanel.add(this.domainAndRangeSelection);
                    
                    this.pack();
                    this.repaint();
                    break;
                default:
                    setVisible(false);
                    break;
            }
        } catch (IndexOutOfBoundsException e) {
            setVisible(false);
            command.showMessage(PatternCommand.NOT_ALL_FIELDS_FILLED);
        }
    }
}
