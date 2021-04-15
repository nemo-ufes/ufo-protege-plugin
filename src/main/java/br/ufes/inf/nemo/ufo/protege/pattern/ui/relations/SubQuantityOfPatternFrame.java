/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui.relations;

import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.EntityFilter;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.relations.SubQuantityOfCommand;
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
public class SubQuantityOfPatternFrame extends JFrame implements ActionListener {
    
    private final SubQuantityOfCommand command;
    
    private JComboBox quantitySelection;
    private JComboBox subquantitySelection;
    
    private final JLabel quantityLabel = new JLabel("Some gufo:Quantity: ");
    private final JLabel subquantityLabel = new JLabel("A gufo:Quantity to be subquantity: ");
    
    private List<IRI> quantityIRIs;
    private List<IRI> subquantityIRIs;
    
    private final JPanel quantityPanel = new JPanel();
    private final JPanel subquantityPanel = new JPanel();
    private final JPanel okPanel = new JPanel();
    
    public SubQuantityOfPatternFrame(SubQuantityOfCommand command) {
        
        this.command = command;
        
        this.setTitle("New gufo:isSubQuantityOf relation");
        this.setLayout(new GridLayout(0, 1));
        this.setVisible(false);
    }
    
    public void setQuantityIRIs(List<IRI> IRIs) {
        quantityIRIs = IRIs;
    }
    
    public void setSubQuantityIRIs(List<IRI> IRIs) {
        subquantityIRIs = IRIs;
    }
    
    public void display() {
        Object[] boxList = quantityIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.quantitySelection = new JComboBox(boxList);
        this.quantitySelection.setActionCommand("Quantity selected");
        this.quantitySelection.addActionListener(this);
        
        boxList = subquantityIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.subquantitySelection = new JComboBox(boxList);
        
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        quantityPanel.add(quantityLabel);
        quantityPanel.add(quantitySelection);
        subquantityPanel.add(subquantityLabel);
        subquantityPanel.add(subquantitySelection);
        okPanel.add(ok);
        okPanel.add(cancel);
        
        this.add(quantityPanel);
        this.add(subquantityPanel);
        this.add(okPanel);
        
        this.pack();
        this.setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent event) {
        String action = event.getActionCommand();
        int index;
        IRI quantity, subquantity;
        
        try {
            switch (action) {
                case "OK":
                    index = quantitySelection.getSelectedIndex();
                    quantity = quantityIRIs.get(index);

                    index = subquantitySelection.getSelectedIndex();
                    subquantity = subquantityIRIs.get(index);

                    command.setQuantity(quantity);
                    command.setSubQuantity(subquantity);

                    command.runCommand();
                    setVisible(false);
                    break;
                case "Quantity selected":
                    {
                        index = quantitySelection.getSelectedIndex();
                        quantity = quantityIRIs.get(index);

                        this.subquantityPanel.remove(this.subquantitySelection);

                        this.subquantityIRIs = new EntityFilter(command.getOWLModelManager())
                                .isOfType(GufoIris.Quantity)
                                .isDifferentFrom(quantity)
                                .entities();

                        Object[] boxList = subquantityIRIs.stream()
                                .map(iri -> iri.getShortForm())
                                .toArray();

                        this.subquantitySelection = new JComboBox(boxList);

                        this.subquantityPanel.add(this.subquantitySelection);

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
