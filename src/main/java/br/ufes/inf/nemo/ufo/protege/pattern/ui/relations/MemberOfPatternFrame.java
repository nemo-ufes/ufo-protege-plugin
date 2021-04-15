/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui.relations;

import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.EntityFilter;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.relations.MemberOfCommand;
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
public class MemberOfPatternFrame extends JFrame implements ActionListener {
    
    private final MemberOfCommand command;
    
    private JComboBox collectionSelection;
    private JComboBox objectSelection;
    
    private final JLabel collectionLabel = new JLabel("gufo:Collection: ");
    private final JLabel objectLabel = new JLabel("gufo:Object to be a member: ");
    
    private List<IRI> collectionIRIs;
    private List<IRI> objectIRIs;
    
    private final JPanel collectionPanel = new JPanel();
    private final JPanel objectPanel = new JPanel();
    private final JPanel okPanel = new JPanel();
    
    public MemberOfPatternFrame(MemberOfCommand command) {
        
        this.command = command;
        
        this.setTitle("New gufo:isCollectionMemberOf relation");
        this.setLayout(new GridLayout(0, 1));
        this.setVisible(false);
    }
    
    public void setCollectionIRIs(List<IRI> IRIs) {
        collectionIRIs = IRIs;
    }

    public void setObjectIRIs(List<IRI> IRIs) {
        objectIRIs = IRIs;
    }
    
    public void display() {
        Object[] boxList;
        
        boxList = collectionIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.collectionSelection = new JComboBox(boxList);
        this.collectionSelection.setActionCommand("Collection selected");
        this.collectionSelection.addActionListener(this);
        
        boxList = objectIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.objectSelection = new JComboBox(boxList);
        
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        collectionPanel.add(collectionLabel);
        collectionPanel.add(collectionSelection);
        objectPanel.add(objectLabel);
        objectPanel.add(objectSelection);
        okPanel.add(ok);
        okPanel.add(cancel);
        
        this.add(collectionPanel);
        this.add(objectPanel);
        this.add(okPanel);
        
        this.pack();
        this.setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent event) {
        String action = event.getActionCommand();
        int index;
        IRI collection, member;
        
        try {
            switch (action) {
                case "OK":
                    index = collectionSelection.getSelectedIndex();
                    collection = collectionIRIs.get(index);
                    
                    index = objectSelection.getSelectedIndex();
                    member = objectIRIs.get(index);
                    
                    command.setCollection(collection);
                    command.setMember(member);
                    
                    command.runCommand();
                    setVisible(false);
                    break;
                case "Collection selected":
                    index = collectionSelection.getSelectedIndex();
                    collection = collectionIRIs.get(index);
                    
                    this.objectPanel.remove(this.objectSelection);
                    
                    this.objectIRIs = new EntityFilter(command.getOWLModelManager())
                            .isOfType(GufoIris.Object)
                            .isDifferentFrom(collection)
                            .entities();
                    
                    Object[] boxList = objectIRIs.stream()
                            .map(iri -> iri.getShortForm())
                            .toArray();
                    
                    this.objectSelection = new JComboBox(boxList);
                    
                    this.objectPanel.add(this.objectSelection);
                    
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
