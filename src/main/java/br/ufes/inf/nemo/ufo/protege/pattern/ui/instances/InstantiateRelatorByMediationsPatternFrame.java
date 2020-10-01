/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.ui.instances;

import br.ufes.inf.nemo.ufo.protege.pattern.helpers.EntityFilter;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternApplier;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.instances.InstantiateRelatorByMediationsCommand;
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
public class InstantiateRelatorByMediationsPatternFrame extends JFrame implements ActionListener {

    private final InstantiateRelatorByMediationsCommand command;
    
    private JComboBox mediationTypeSelection;
    private JComboBox mediatedASelection;
    private JComboBox mediatedBSelection;
    private JTextField relatorName;
    
    private final JLabel mediationTypeLabel = new JLabel("Mediation type: ");
    private final JLabel relatorTypeLabel = new JLabel("Relator type: ");
    private JLabel relatorTypeSelected;
    private final JLabel mediatedALabel = new JLabel("First mediated endurant: ");
    private final JLabel mediatedBLabel = new JLabel("Second mediated endurant: ");
    private final JLabel relatorLabel = new JLabel("Relator name: ");
    
    private List<IRI> mediationTypeIRIs;
    private List<IRI> mediatedIRIs;
    
    private final JPanel mediationTypePanel = new JPanel();
    private final JPanel relatorTypePanel = new JPanel();
    private final JPanel mediatedAPanel = new JPanel();
    private final JPanel mediatedBPanel = new JPanel();
    private final JPanel relatorPanel = new JPanel();
    private final JPanel okPanel = new JPanel();
    
    public InstantiateRelatorByMediationsPatternFrame(InstantiateRelatorByMediationsCommand command) {
        this.command = command;
        
        this.setTitle("New instance of relator by mediations");
        this.setLayout(new GridLayout(0, 1));
        this.setVisible(false);
    }

    public void setMediationTypeIRIs(List<IRI> IRIs) {
        this.mediationTypeIRIs = IRIs;
    }

    public void setMediatedIRIs(List<IRI> IRIs) {
        this.mediatedIRIs = IRIs;
    }
    
    public void display() {
        Object[] mediationTypeList = mediationTypeIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.mediationTypeSelection = new JComboBox(mediationTypeList);
        this.mediationTypeSelection.setActionCommand("Mediation type selected");
        this.mediationTypeSelection.addActionListener(this);

        IRI firstMediationType = mediationTypeIRIs.isEmpty() ? null : mediationTypeIRIs.get(0);
        if(firstMediationType == null) {
            relatorTypeSelected = new JLabel("");
        } else {
            PatternApplier applier = new PatternApplier(command.getOWLModelManager());
            relatorTypeSelected = new JLabel(
                    applier
                        .getObjectPropertyDomain(firstMediationType)
                        .getShortForm()
            );
        }
        
        Object[] mediatedList = mediatedIRIs.stream()
            .map(iri -> iri.getShortForm())
            .toArray();
        this.mediatedASelection = new JComboBox(mediatedList);
        this.mediatedBSelection = new JComboBox(mediatedList);
        
        this.relatorName = new JTextField(30);
        
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        mediationTypePanel.add(mediationTypeLabel);
        mediationTypePanel.add(mediationTypeSelection);
        relatorTypePanel.add(relatorTypeLabel);
        relatorTypePanel.add(relatorTypeSelected);
        mediatedAPanel.add(mediatedALabel);
        mediatedAPanel.add(mediatedASelection);
        mediatedBPanel.add(mediatedBLabel);
        mediatedBPanel.add(mediatedBSelection);
        relatorPanel.add(relatorLabel);
        relatorPanel.add(relatorName);
        okPanel.add(ok);
        okPanel.add(cancel);
        
        this.add(mediationTypePanel);
        this.add(relatorTypePanel);
        this.add(mediatedAPanel);
        this.add(mediatedBPanel);
        this.add(relatorPanel);
        this.add(okPanel);
        
        this.pack();
        this.setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent event) {
        String action = event.getActionCommand();
        int index;
        IRI mediationType, relatorType, mediatedA, mediatedB;
        PatternApplier applier = new PatternApplier(command.getOWLModelManager());
        
        try {
            switch(action) {
                case "OK":
                    index = mediationTypeSelection.getSelectedIndex();
                    mediationType = mediationTypeIRIs.get(index);

                    
                    relatorType = applier.getObjectPropertyDomain(mediationType);
                    
                    index = mediatedASelection.getSelectedIndex();
                    mediatedA = mediatedIRIs.get(index);

                    index = mediatedBSelection.getSelectedIndex();
                    mediatedB = mediatedIRIs.get(index);

                    String relatorStr = relatorName.getText();
                    if(relatorStr.trim().isEmpty()) {
                        setVisible(false);
                        command.showMessage(PatternCommand.NOT_ALL_FIELDS_FILLED);
                        return;
                    }
                    IRI relator = IRI.create(command.getOntologyPrefix(), relatorStr);

                    command.setMediationType(mediationType);
                    command.setRelatorType(relatorType);
                    command.setMediatedA(mediatedA);
                    command.setMediatedB(mediatedB);
                    command.setRelator(relator);           

                    command.runCommand();
                    setVisible(false);
                    break;
                case "Mediation type selected":
                    index = mediationTypeSelection.getSelectedIndex();
                    mediationType = mediationTypeIRIs.get(index);
                    
                    relatorTypePanel.remove(relatorTypeSelected);
                    mediatedAPanel.remove(mediatedASelection);
                    mediatedBPanel.remove(mediatedBSelection);
                    
                    relatorTypeSelected = new JLabel(
                            applier
                                .getObjectPropertyDomain(mediationType)
                                .getShortForm()
                    );
                    
                    IRI mediatedType = applier.getObjectPropertyRange(mediationType);
                    mediatedIRIs = new EntityFilter(command.getOWLModelManager())
                            .addType(mediatedType)
                            .entities();
                    
                    Object[] mediatedList = mediatedIRIs.stream()
                        .map(iri -> iri.getShortForm())
                        .toArray();
                    this.mediatedASelection = new JComboBox(mediatedList);
                    this.mediatedBSelection = new JComboBox(mediatedList);
                    
                    relatorTypePanel.add(relatorTypeSelected);
                    mediatedAPanel.add(mediatedASelection);
                    mediatedBPanel.add(mediatedBSelection);
                    
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
