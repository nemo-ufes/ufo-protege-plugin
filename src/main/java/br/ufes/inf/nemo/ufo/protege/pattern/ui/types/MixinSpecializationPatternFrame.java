/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui.types;

import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.EntityFilter;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.types.MixinSpecializationCommand;
import java.awt.Dimension;
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
public class MixinSpecializationPatternFrame extends JFrame implements ActionListener {
    
    private final MixinSpecializationCommand command;
    
    private JComboBox mixinSelection;
    private JComboBox endurantTypeSelection;
    
    private final JLabel mixinLabel = new JLabel("gufo:Mixin to be specialized: ");
    private final JLabel endurantTypeLabel = new JLabel("gufo:EndurantType to be generalized: ");
    
    private List<IRI> mixinIRIs;
    private List<IRI> endurantTypeIRIs;
    
    private final JPanel mixinPanel = new JPanel();
    private final JPanel endurantTypePanel = new JPanel();
    private final JPanel okPanel = new JPanel();
    
    public MixinSpecializationPatternFrame(MixinSpecializationCommand command) {
        
        this.command = command;
        
        this.setTitle("Specialize a gufo:Mixin");
        this.setLayout(new GridLayout(0, 1));
        this.setVisible(false);
    }
    
    public void setMixinIRIs(List<IRI> IRIs) {
        mixinIRIs = IRIs;
    }

    public void setEndurantTypeIRIs(List<IRI> IRIs) {
        this.endurantTypeIRIs = IRIs;
    }
    
    public void display() {
        Object[] boxList;
        
        boxList = mixinIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.mixinSelection = new JComboBox(boxList);
        this.mixinSelection.setActionCommand("Mixin selected");
        this.mixinSelection.addActionListener(this);
        
        boxList = endurantTypeIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.endurantTypeSelection = new JComboBox(boxList);
        
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        mixinPanel.add(mixinLabel);
        mixinPanel.add(mixinSelection);
        endurantTypePanel.add(endurantTypeLabel);
        endurantTypePanel.add(endurantTypeSelection);
        okPanel.add(ok);
        okPanel.add(cancel);
        
        this.add(mixinPanel);
        this.add(endurantTypePanel);
        this.add(okPanel);
        
        this.setPreferredSize(new Dimension(600, 130));
        this.setResizable(false);
        this.pack();
        this.setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent event) {
        String action = event.getActionCommand();
        int index;
        IRI mixin, endurantType;
        
        try {
            switch (action) {
                case "OK":
                    index = mixinSelection.getSelectedIndex();
                    mixin = mixinIRIs.get(index);

                    index = endurantTypeSelection.getSelectedIndex();
                    endurantType = endurantTypeIRIs.get(index);

                    command.setMixin(mixin);
                    command.setEndurantType(endurantType);

                    command.runCommand();
                    setVisible(false);
                    break;
                case "Mixin selected":
                    {
                        index = mixinSelection.getSelectedIndex();
                        mixin = mixinIRIs.get(index);

                        this.endurantTypePanel.remove(this.endurantTypeSelection);

                        this.endurantTypeIRIs = new EntityFilter(command.getOWLModelManager())
                                .isOfType(GufoIris.EndurantType)
                                .isNotOfType(GufoIris.Category)
                                .isOfOntologicalNatureOf(mixin)
                                .isNotSuperClassByType(mixin, GufoIris.Category)
                                .isNotDirectSubClassOf(mixin)
                                .isDifferentFrom(mixin)
                                .unionWith()
                                .isOfType(GufoIris.Category)
                                .isOfOntologicalNatureOf(mixin)
                                .isNotSuperClassOf(mixin)
                                .isNotDirectSubClassOf(mixin)
                                .entities();

                        Object[] boxList = endurantTypeIRIs.stream()
                                .map(iri -> iri.getShortForm())
                                .toArray();

                        this.endurantTypeSelection = new JComboBox(boxList);

                        this.endurantTypePanel.add(this.endurantTypeSelection);

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
