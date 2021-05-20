/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.types.kinds;

import br.ufes.inf.nemo.protege.annotations.EditorKitMenuAction;
import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.relations.MediationRestrictionCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.relations.MediationTypeCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.types.RoleCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.ui.types.RelatorPatternFrame;
import java.awt.event.ActionEvent;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
@EditorKitMenuAction(
        id = "menuItemRelator",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.CreateAspectKindMenu/SlotA-04",
        name = "Create gufo:Kind specializing gufo:Relator"
)
public class RelatorCommand extends KindCommand {
    private MediationTypeCommand mediationTypeCommand;
    private MediationRestrictionCommand mediationRestrictionCommand;
    private RoleCommand roleCommand;
    
    private boolean firstRun;
    private boolean roleCreation;
    private boolean mediationTypeCreation;
    private boolean mediationRestrictionCreation;
    
    @Override
    protected void defineEndurantClass() {
        roleCommand = new RoleCommand();
        roleCommand.setEditorKit(this.getOWLEditorKit());
        
        mediationTypeCommand = new MediationTypeCommand();
        mediationTypeCommand.setEditorKit(this.getOWLEditorKit());
        
        mediationRestrictionCommand = new MediationRestrictionCommand();
        mediationRestrictionCommand.setEditorKit(this.getOWLEditorKit());
        
        firstRun = true;
        roleCreation = false;
        mediationTypeCreation = false;
        mediationRestrictionCreation = false;
        
        super.endurantClass = GufoIris.Relator;
        super.endurantClassName = "gufo:Relator";
    }
    
    @Override
    public void setKind(IRI kind) {
        super.setKind(kind);
        mediationTypeCommand.setRelatorType(kind);
        mediationRestrictionCommand.setRelatorType(kind);
    }
    
    public void setMediationType(IRI mediationType) {
        if(mediationType == null) {
            mediationTypeCreation = false;
            mediationRestrictionCommand.setMediationType(IRI.create(GufoIris.GUFO, "mediates"));
        } else {
            mediationTypeCommand.setMediationType(mediationType);
            mediationRestrictionCommand.setMediationType(mediationType);

            mediationTypeCreation = true;
        }
    }
    
    public void setMediatedType(IRI mediatedType, IRI role) {
        if(role == null) {
            mediationTypeCommand.setMediatedType(mediatedType);
            mediationRestrictionCommand.setMediatedType(mediatedType);
            roleCreation = false;
        } else {
            roleCommand.setSortal(mediatedType);
            roleCommand.setRole(role);
            roleCreation = true;
            mediationTypeCommand.setMediatedType(role);
            mediationRestrictionCommand.setMediatedType(role);
        }
    }
    
    public void setMinCardinality(int minCard) {
        mediationRestrictionCommand.setMinCardinality(minCard);
        
        mediationRestrictionCreation = true;
    }
    
    public void setMaxCardinality(int maxCard) {
        mediationRestrictionCommand.setMaxCardinality(maxCard);
    }
    
    @Override
    public void runCommand() {
        if(firstRun) {
            super.runCommand();
            firstRun = false;
        }
        
        if(roleCreation) {
            roleCommand.runCommand();
        }
        
        if(mediationTypeCreation) {
            mediationTypeCommand.runCommand();
        }
        
        if(mediationRestrictionCreation) {
            mediationRestrictionCommand.runCommand();
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        defineEndurantClass();
        RelatorPatternFrame frame = new RelatorPatternFrame(this);
        frame.display();
    }
}
