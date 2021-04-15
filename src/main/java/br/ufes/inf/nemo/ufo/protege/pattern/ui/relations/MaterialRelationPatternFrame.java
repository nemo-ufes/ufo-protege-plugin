/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui.relations;

import br.ufes.inf.nemo.ufo.protege.pattern.helpers.EntityFilter;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternApplier;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.relations.MaterialRelationCommand;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
public class MaterialRelationPatternFrame extends JFrame implements ActionListener {
    
    private final MaterialRelationCommand command;
    
    private JComboBox relationTypeSelection;
    private JComboBox subjectSelection;
    private JComboBox objectSelection;
    
    private final JLabel relationTypeLabel = new JLabel("Material relation type: ");
    private final JLabel subjectLabel = new JLabel("Subject of the relation: ");
    private final JLabel objectLabel = new JLabel("Object of the relation: ");
    
    private List<IRI> relationTypeIRIs;
    private List<IRI> subjectIRIs;
    private List<IRI> objectIRIs;
    
    private final JPanel relationTypePanel = new JPanel();
    private final JPanel subjectPanel = new JPanel();
    private final JPanel objectPanel = new JPanel();
    private final JPanel okPanel = new JPanel();
    
    public MaterialRelationPatternFrame(MaterialRelationCommand command) {
        
        this.command = command;
        
        this.setTitle("New material relation");
        this.setLayout(new GridLayout(0, 1));
        this.setVisible(false);
    }
    
    public void setRelationTypeIRIs(List<IRI> IRIs) {
        relationTypeIRIs = IRIs;
    }

    public void setSubjectIRIs(List<IRI> IRIs) {
        this.subjectIRIs = IRIs;
    }

    public void setObjectIRIs(List<IRI> objectIRIs) {
        this.objectIRIs = objectIRIs;
    }
    
    public void display() {
        Object[] boxList;
                
        boxList = relationTypeIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.relationTypeSelection = new JComboBox(boxList);
        this.relationTypeSelection.setActionCommand("Relation type selected");
        this.relationTypeSelection.addActionListener(this);
        
        boxList = subjectIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.subjectSelection = new JComboBox(boxList);
        
        boxList = objectIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.objectSelection = new JComboBox(boxList);
        
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        relationTypePanel.add(relationTypeLabel);
        relationTypePanel.add(relationTypeSelection);
        subjectPanel.add(subjectLabel);
        subjectPanel.add(subjectSelection);
        objectPanel.add(objectLabel);
        objectPanel.add(objectSelection);
        okPanel.add(ok);
        okPanel.add(cancel);
        
        this.add(relationTypePanel);
        this.add(subjectPanel);
        this.add(objectPanel);
        this.add(okPanel);
        
        this.pack();
        this.setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent event) {
        String action = event.getActionCommand();
        int index;
        IRI relationType, subject, object;
        
        try {
            switch (action) {
                case "OK":
                    index = relationTypeSelection.getSelectedIndex();
                    relationType = relationTypeIRIs.get(index);

                    index = subjectSelection.getSelectedIndex();
                    subject = subjectIRIs.get(index);
                    
                    index = objectSelection.getSelectedIndex();
                    object = objectIRIs.get(index);

                    command.setRelationType(relationType);
                    command.setSubject(subject);
                    command.setObject(object);

                    command.runCommand();
                    setVisible(false);
                    break;
                case "Relation type selected":
                    {
                        index = relationTypeSelection.getSelectedIndex();
                        relationType = relationTypeIRIs.get(index);

                        this.subjectPanel.remove(this.subjectSelection);
                        this.objectPanel.remove(this.objectSelection);

                        PatternApplier applier = new PatternApplier(command.getOWLModelManager());
                        Object[] boxList;
                        
                        this.subjectIRIs = new EntityFilter(command.getOWLModelManager())
                                .isOfType(applier.getObjectPropertyDomain(relationType))
                                .entities();

                        boxList = subjectIRIs.stream()
                                .map(iri -> iri.getShortForm())
                                .toArray();
                        this.subjectSelection = new JComboBox(boxList);
                        
                        this.objectIRIs = new EntityFilter(command.getOWLModelManager())
                                .isOfType(applier.getObjectPropertyRange(relationType))
                                .entities();

                        boxList = objectIRIs.stream()
                                .map(iri -> iri.getShortForm())
                                .toArray();
                        this.objectSelection = new JComboBox(boxList);

                        this.subjectPanel.add(this.subjectSelection);
                        this.objectPanel.add(this.objectSelection);
                        
                        this.pack();
                        this.repaint();
                        break;
                    }
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
