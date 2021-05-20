/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.relations;

import br.ufes.inf.nemo.protege.annotations.EditorKitMenuAction;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternApplier;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
// import br.ufes.inf.nemo.ufo.protege.pattern.ui.relations.MediationTypePatternFrame;
import java.awt.event.ActionEvent;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
/* @EditorKitMenuAction(
        id = "menuItemMediationRestriction",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.ForRelationsMenu/SlotE-__",
        name = "New mediation restriction"
) */
public class MediationRestrictionCommand extends PatternCommand {

    private IRI mediationType;
    private IRI relatorType;
    private IRI mediatedType;
    private int minCardinality;
    private int maxCardinality;
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        applier.addObjectPropertyCardinalityRestriction(relatorType, mediationType, minCardinality, maxCardinality, mediatedType);
    }

    public void setMediationType(IRI mediationType) {
        this.mediationType = mediationType;
    }

    public void setRelatorType(IRI relatorType) {
        this.relatorType = relatorType;
    }

    public void setMediatedType(IRI mediatedType) {
        this.mediatedType = mediatedType;
    }

    public void setMinCardinality(int minCardinality) {
        this.minCardinality = minCardinality;
    }

    public void setMaxCardinality(int maxCardinality) {
        this.maxCardinality = maxCardinality;
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        // Nothing yet
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

}
