/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui.relations;

import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.EntityFilter;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.relations.SubCollectionOfCommand;
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
public class SubCollectionOfPatternFrame extends JFrame implements ActionListener {
    
    private final SubCollectionOfCommand command;
    
    private JComboBox collectionSelection;
    private JComboBox subcollectionSelection;
    
    private final JLabel collectionLabel = new JLabel("Collection: ");
    private final JLabel subcollectionLabel = new JLabel("Subcollection: ");
    
    private List<IRI> collectionIRIs;
    private List<IRI> subcollectionIRIs;
    
    private final JPanel collectionPanel = new JPanel();
    private final JPanel subcollectionPanel = new JPanel();
    private final JPanel okPanel = new JPanel();
    
    public SubCollectionOfPatternFrame(SubCollectionOfCommand command) {
        
        this.command = command;
        
        this.setTitle("New subcollection-of relation");
        this.setLayout(new GridLayout(0, 1));
        this.setVisible(false);
    }
    
    public void setCollectionIRIs(List<IRI> IRIs) {
        collectionIRIs = IRIs;
    }
    
    public void setSubCollectionIRIs(List<IRI> IRIs) {
        subcollectionIRIs = IRIs;
    }
    
    public void display() {
        Object[] boxList = collectionIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.collectionSelection = new JComboBox(boxList);
        this.collectionSelection.setActionCommand("Collection selected");
        this.collectionSelection.addActionListener(this);
        
        boxList = subcollectionIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.subcollectionSelection = new JComboBox(boxList);
        
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        collectionPanel.add(collectionLabel);
        collectionPanel.add(collectionSelection);
        subcollectionPanel.add(subcollectionLabel);
        subcollectionPanel.add(subcollectionSelection);
        okPanel.add(ok);
        okPanel.add(cancel);
        
        this.add(collectionPanel);
        this.add(subcollectionPanel);
        this.add(okPanel);
        
        this.pack();
        this.setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent event) {
        String action = event.getActionCommand();
        int index;
        IRI collection, subcollection;
        
        try {
            switch (action) {
                case "OK":
                    index = collectionSelection.getSelectedIndex();
                    collection = collectionIRIs.get(index);

                    index = subcollectionSelection.getSelectedIndex();
                    subcollection = subcollectionIRIs.get(index);

                    command.setCollection(collection);
                    command.setSubCollection(subcollection);

                    command.runCommand();
                    setVisible(false);
                    break;
                case "Collection selected":
                    {
                        index = collectionSelection.getSelectedIndex();
                        collection = collectionIRIs.get(index);

                        this.subcollectionPanel.remove(this.subcollectionSelection);

                        this.subcollectionIRIs = new EntityFilter(command.getOWLModelManager())
                                .addType(GufoIris.Collection)
                                .isDifferentFrom(collection)
                                .entities();

                        Object[] boxList = subcollectionIRIs.stream()
                                .map(iri -> iri.getShortForm())
                                .toArray();

                        this.subcollectionSelection = new JComboBox(boxList);

                        this.subcollectionPanel.add(this.subcollectionSelection);

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
