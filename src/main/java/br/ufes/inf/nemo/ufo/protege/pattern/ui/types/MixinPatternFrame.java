/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui.types;

import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.EntityFilter;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.types.MixinCommand;
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
public class MixinPatternFrame extends JFrame implements ActionListener {
    
    private final MixinCommand command;
    
    private JComboBox endurantClassSelection;
    private JComboBox rigidSortalSelection;
    private JComboBox antiRigidSortalSelection;
    private JTextField mixinName;
    
    private final JLabel endurantClassLabel = new JLabel("Endurant class to specialize: ");
    private final JLabel rigidSortalLabel = new JLabel("Rigid sortal to generalize:");
    private final JLabel antiRigidSortalLabel = new JLabel("Anti-rigid sortal to generalize: ");
    private final JLabel mixinLabel = new JLabel("Mixin name: ");
    
    private List<IRI> endurantClassIRIs;
    private List<IRI> rigidSortalIRIs;
    private List<IRI> antiRigidSortalIRIs;
    
    private final JPanel endurantClassPanel = new JPanel();
    private final JPanel rigidSortalPanel = new JPanel();
    private final JPanel antiRigidSortalPanel = new JPanel();
    private final JPanel mixinPanel = new JPanel();
    private final JPanel okPanel = new JPanel();
    
    public MixinPatternFrame(MixinCommand command) {
        
        this.command = command;
        
        this.setTitle("New mixin");
        this.setLayout(new GridLayout(0, 1));
        this.setVisible(false);
    }
    
    public void setEndurantClassIRIs(List<IRI> IRIs) {
        endurantClassIRIs = IRIs;
    }

    public void setRigidSortalIRIs(List<IRI> IRIs) {
        this.rigidSortalIRIs = IRIs;
    }

    public void setAntiRigidSortalIRIs(List<IRI> IRIs) {
        this.antiRigidSortalIRIs = IRIs;
    }
    
    public void display() {
        Object[] boxList = endurantClassIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.endurantClassSelection = new JComboBox(boxList);
        this.endurantClassSelection.setActionCommand("Endurant class selected");
        this.endurantClassSelection.addActionListener(this);
        
        boxList = rigidSortalIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.rigidSortalSelection = new JComboBox(boxList);
        this.rigidSortalSelection.setActionCommand("Rigid sortal selected");
        this.rigidSortalSelection.addActionListener(this);
        
        boxList = antiRigidSortalIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.antiRigidSortalSelection = new JComboBox(boxList);
        
        mixinName = new JTextField(30);
        
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        endurantClassPanel.add(endurantClassLabel);
        endurantClassPanel.add(endurantClassSelection);
        rigidSortalPanel.add(rigidSortalLabel);
        rigidSortalPanel.add(rigidSortalSelection);
        antiRigidSortalPanel.add(antiRigidSortalLabel);
        antiRigidSortalPanel.add(antiRigidSortalSelection);
        mixinPanel.add(mixinLabel);
        mixinPanel.add(mixinName);
        okPanel.add(ok);
        okPanel.add(cancel);
        
        this.add(endurantClassPanel);
        this.add(rigidSortalPanel);
        this.add(antiRigidSortalPanel);
        this.add(mixinPanel);
        this.add(okPanel);
        
        this.pack();
        this.setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent event) {
        String action = event.getActionCommand();
        int index;
        IRI endurantClass, rigidSortal, antiRigidSortal;
        
        try {
            switch (action) {
                case "OK":
                    index = endurantClassSelection.getSelectedIndex();
                    endurantClass = endurantClassIRIs.get(index);
                    
                    index = rigidSortalSelection.getSelectedIndex();
                    rigidSortal = rigidSortalIRIs.get(index);
                    
                    index = antiRigidSortalSelection.getSelectedIndex();
                    antiRigidSortal = antiRigidSortalIRIs.get(index);

                    String mixinStr = mixinName.getText();
                    if(mixinStr.trim().isEmpty()) {
                        setVisible(false);
                        command.showMessage(PatternCommand.NOT_ALL_FIELDS_FILLED);
                        return;
                    }
                    IRI mixin = IRI.create(command.getOntologyPrefix(), mixinStr);

                    command.setEndurantClass(endurantClass);
                    command.setRigidSortal(rigidSortal);
                    command.setAntiRigidSortal(antiRigidSortal);
                    command.setMixin(mixin);

                    command.runCommand();
                    setVisible(false);
                    break;
                case "Endurant class selected":
                    index = endurantClassSelection.getSelectedIndex();
                    endurantClass = endurantClassIRIs.get(index);

                    this.rigidSortalPanel.remove(this.rigidSortalSelection);
                    this.antiRigidSortalPanel.remove(this.antiRigidSortalSelection);

                    this.rigidSortalIRIs = new EntityFilter(command.getOWLModelManager())
                            .addType(GufoIris.RigidType)
                            .addType(GufoIris.Sortal)
                            .addSuperClass(endurantClass)
                            .entities();

                    Object[] boxList = rigidSortalIRIs.stream()
                            .map(iri -> iri.getShortForm())
                            .toArray();
                    this.rigidSortalSelection = new JComboBox(boxList);
                    this.rigidSortalSelection.setActionCommand("Rigid sortal selected");
                    this.rigidSortalSelection.addActionListener(this);
                    
                    rigidSortal = rigidSortalIRIs.isEmpty() ? null : rigidSortalIRIs.get(0);
                    this.antiRigidSortalIRIs = new EntityFilter(command.getOWLModelManager())
                            .addType(GufoIris.AntiRigidType)
                            .addType(GufoIris.Sortal)
                            .addSuperClass(endurantClass)
                            .hasDifferentKindOf(rigidSortal)
                            .entities();
                    
                    boxList = antiRigidSortalIRIs.stream()
                            .map(iri -> iri.getShortForm())
                            .toArray();
                    this.antiRigidSortalSelection = new JComboBox(boxList);
                    
                    this.rigidSortalPanel.add(this.rigidSortalSelection);
                    this.antiRigidSortalPanel.add(this.antiRigidSortalSelection);

                    this.pack();
                    this.repaint();
                    break;
                case "Rigid sortal selected":
                    index = endurantClassSelection.getSelectedIndex();
                    endurantClass = endurantClassIRIs.get(index);
                    
                    index = rigidSortalSelection.getSelectedIndex();
                    rigidSortal = rigidSortalIRIs.get(index);
                    
                    this.antiRigidSortalPanel.remove(this.antiRigidSortalSelection);
                    
                    this.antiRigidSortalIRIs = new EntityFilter(command.getOWLModelManager())
                            .addType(GufoIris.AntiRigidType)
                            .addType(GufoIris.Sortal)
                            .addSuperClass(endurantClass)
                            .hasDifferentKindOf(rigidSortal)
                            .entities();
                    
                    boxList = antiRigidSortalIRIs.stream()
                            .map(iri -> iri.getShortForm())
                            .toArray();
                    this.antiRigidSortalSelection = new JComboBox(boxList);
                    
                    this.antiRigidSortalPanel.add(this.antiRigidSortalSelection);
                    
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
