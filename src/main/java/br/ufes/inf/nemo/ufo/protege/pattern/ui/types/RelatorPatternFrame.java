/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui.types;

import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.EntityFilter;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.types.kinds.RelatorCommand;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
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
public class RelatorPatternFrame extends JFrame implements ActionListener {
    
    private final List<IRI> mediatedIRIs;
    // private final List<JPanel> mediationPanels = new ArrayList<>();
    
    private final List<JTextField> mediationNames = new ArrayList<>();
    private final List<JTextField> minCards = new ArrayList<>();
    private final List<JTextField> maxCards = new ArrayList<>();
    private final List<JComboBox> mediatedTypes = new ArrayList<>();
    private final List<JTextField> roleNames = new ArrayList<>();
    
    private final String endurantClassName;
    private final RelatorCommand command;
    
    private JTextField kindName;
    private final JLabel kindLabel;
    
    private final JPanel mainPanel = new JPanel();
    private final JPanel kindPanel = new JPanel();
    private final JPanel addMediationPanel = new JPanel();
    private JPanel mediationsPanel = null;
    private final JPanel okPanel = new JPanel();
    
    public RelatorPatternFrame(RelatorCommand command) {
        
        this.command = command;
        this.endurantClassName = command.getEndurantClassName();
        this.kindLabel = new JLabel("New " + endurantClassName + " kind short name: ");
        
        this.mediatedIRIs = new EntityFilter(command.getOWLModelManager())
                .isOfType(GufoIris.EndurantType)
                .entities();
        
        this.setTitle("Create gufo:Kind specializing " + endurantClassName);
        
        BoxLayout boxLayout = new BoxLayout(mainPanel, BoxLayout.Y_AXIS);
        mainPanel.setLayout(boxLayout);
        
        this.setVisible(false);
    }
    
    public void display() {
        kindName = new JTextField(30);
        
        JButton addMediation = new JButton("Add a mediation restriction");
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        
        JLabel addMediationLabel = new JLabel(
                "(You can optionally create a sub-property of gufo:mediates and a gufo:Role)");
        
        addMediation.addActionListener(this);
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        kindPanel.add(kindLabel);
        kindPanel.add(kindName);
        addMediationPanel.add(addMediation);
        addMediationPanel.add(addMediationLabel);
        okPanel.add(ok);
        okPanel.add(cancel);
        
        mainPanel.add(kindPanel);
        mainPanel.add(addMediationPanel);
        mainPanel.add(okPanel);
        
        this.add(mainPanel);
        
        this.setPreferredSize(new Dimension(1200, 150));
        this.setResizable(false);
        this.pack();
        this.setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent event) {
        String action = event.getActionCommand();
        
        try {
            switch(action) {
                case "OK":
                    String kindStr = kindName.getText();
                    if(kindStr.trim().isEmpty()) {
                        setVisible(false);
                        command.showMessage(PatternCommand.NOT_ALL_FIELDS_FILLED);
                        return;
                    }
                    IRI kind = IRI.create(command.getOntologyPrefix(), kindStr);

                    command.setKind(kind);
                    command.runCommand();
                    
                    /* for(JPanel panel : mediationPanels) {
                        JTextField minCardField = (JTextField) panel.getComponent(4);
                        JTextField maxCardField = (JTextField) panel.getComponent(5);
                        JComboBox box = (JComboBox) panel.getComponent(6);
                        JTextField roleField = (JTextField) panel.getComponent(7); */
                    
                    int num = mediatedTypes.size();
                    for(int i = 0; i < num; i++) {
                        JTextField mediationField = mediationNames.get(i);
                        JTextField minCardField = minCards.get(i);
                        JTextField maxCardField = maxCards.get(i);
                        JComboBox box = mediatedTypes.get(i);
                        JTextField roleField = roleNames.get(i); 
                        
                        String mediationNameStr = mediationField.getText();
                        IRI mediationName;
                        if(mediationNameStr.trim().isEmpty()) {
                            mediationName = null;
                        } else {
                            mediationName = IRI.create(command.getOntologyPrefix(), mediationNameStr);
                        }
                        command.setMediationType(mediationName);
                        
                        String minCardStr = minCardField.getText();
                        int minCard;
                        if(minCardStr.trim().isEmpty()) {
                            minCard = 0;
                        } else {
                            minCard = Integer.valueOf(minCardStr);
                        }
                        
                        String maxCardStr = maxCardField.getText();
                        int maxCard;
                        if(maxCardStr.trim().isEmpty()) {
                            maxCard = -1;
                        } else {
                            maxCard = Integer.valueOf(maxCardStr);
                        }
                        
                        // Handle exception here?
                        if(minCard < 0 | maxCard == 0 | (maxCard > 0 && maxCard < minCard)) {
                            continue;
                        }
                        command.setMinCardinality(minCard);
                        command.setMaxCardinality(maxCard);
                        
                        int mediatedIndex = box.getSelectedIndex();
                        IRI mediatedType = mediatedIRIs.get(mediatedIndex);
                        
                        String roleStr = roleField.getText();
                        IRI role;
                        if(roleStr.trim().isEmpty()) {
                            role = null;
                        } else {
                            role = IRI.create(command.getOntologyPrefix(), roleStr);
                        }
                        
                        command.setMediatedType(mediatedType, role);
                        
                        command.runCommand();
                    }
                    setVisible(false);
                    break;
                case "Add a mediation restriction":
                    /* JPanel mediationPanel = new JPanel();
                    mediationPanel.setLayout(new GridLayout(0, 4));
                    
                    // Mediation Panel Header
                    mediationPanel.add(new JLabel("minCard: "));
                    mediationPanel.add(new JLabel("maxCard: "));
                    mediationPanel.add(new JLabel("Mediated type: "));
                    mediationPanel.add(new JLabel("gufo:Role short name: ")); */
                    
                    JTextField mediationNameField = new JTextField(30);
                    JTextField minCardField = new JTextField(2);
                    JTextField maxCardField = new JTextField(2);
                    
                    Object[] boxList = mediatedIRIs.stream()
                                .map(iri -> iri.getShortForm())
                                .toArray();

                    JComboBox mediatedTypeSelection = new JComboBox(boxList);
                    
                    JTextField roleField = new JTextField(30);
                    
                    /* mediationPanel.add(minCardField);
                    mediationPanel.add(maxCardField);
                    mediationPanel.add(mediatedTypeSelection);
                    mediationPanel.add(roleField); */
                    
                    if(mediationsPanel == null) {
                        mediationsPanel = new JPanel();
                        
                        mediationsPanel.setLayout(new GridLayout(0, 5));
                        mediationsPanel.add(new JLabel("Sub-property of gufo:mediates: "));
                        mediationsPanel.add(new JLabel("minCard: "));
                        mediationsPanel.add(new JLabel("maxCard: "));
                        mediationsPanel.add(new JLabel("Mediated type: "));
                        mediationsPanel.add(new JLabel("gufo:Role short name: "));
                    
                        mainPanel.remove(okPanel);
                        mainPanel.add(mediationsPanel);
                        mainPanel.add(okPanel);
                    }
                    
                    mediationsPanel.add(mediationNameField);
                    mediationsPanel.add(minCardField);
                    mediationsPanel.add(maxCardField);
                    mediationsPanel.add(mediatedTypeSelection);
                    mediationsPanel.add(roleField);
                    
                    // Dimension resizing = new Dimension(this.getWidth(), this.getHeight() + 40);
                    Dimension resizing = new Dimension(this.getWidth(), this.getHeight() + 30);
                    this.setPreferredSize(resizing);
                    this.pack();
                    this.repaint();
                    
                    // mediationPanels.add(mediationPanel);
                    mediationNames.add(mediationNameField);
                    minCards.add(minCardField);
                    maxCards.add(maxCardField);
                    mediatedTypes.add(mediatedTypeSelection);
                    roleNames.add(roleField);
                    
                    break;
                default:
                    setVisible(false);
            }
        } catch(IndexOutOfBoundsException e) {
            setVisible(false);
            command.showMessage(PatternCommand.NOT_ALL_FIELDS_FILLED);
        }
    }   
}
