/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.pattern.relations;

import br.ufes.inf.nemo.protege.annotations.EditorKitMenuAction;
import br.ufes.inf.nemo.ufo.protege.GufoIris;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.EntityFilter;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternApplier;
import br.ufes.inf.nemo.ufo.protege.pattern.helpers.PatternCommand;
import br.ufes.inf.nemo.ufo.protege.pattern.ui.relations.MaterialRelationPatternFrame;
import java.awt.event.ActionEvent;
import java.util.List;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author jeferson
 */
@EditorKitMenuAction(
        id = "menuItemMaterialRelation",
        path = "br.ufes.inf.nemo.ufo-protege-plugin.ForRelationsMenu/SlotE-08",
        name = "New material relation"
)
public class MaterialRelationCommand extends PatternCommand {

    private IRI relationType;
    private IRI subject;
    private IRI object;

    public void setRelationType(IRI relationType) {
        this.relationType = relationType;
    }

    public void setSubject(IRI subject) {
        this.subject = subject;
    }

    public void setObject(IRI object) {
        this.object = object;
    }
    
    @Override
    public void runCommand() {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        applier.assertObjectProperty(relationType, subject, object);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        PatternApplier applier = new PatternApplier(getOWLModelManager());
        
        List<IRI> relationTypeIRIs = new EntityFilter(getOWLModelManager())
                .addType(GufoIris.MaterialRelationshipType)
                .entities();
        
        IRI firstRelationType = relationTypeIRIs.isEmpty() ? null : relationTypeIRIs.get(0);
        List<IRI> subjectIRIs, objectIRIs;
        
        // Making subjectIRIs and objectIRIs empty lists if relationTypeIRIs is empty
        if(firstRelationType == null) {
            subjectIRIs = relationTypeIRIs;
            objectIRIs = relationTypeIRIs;
        } else {
            subjectIRIs = new EntityFilter(getOWLModelManager())
                .addType(applier.getObjectPropertyDomain(firstRelationType))
                .entities();
            
            objectIRIs = new EntityFilter(getOWLModelManager())
                .addType(applier.getObjectPropertyRange(firstRelationType))
                .entities();
        }
        
        MaterialRelationPatternFrame frame = new MaterialRelationPatternFrame(this);
        frame.setRelationTypeIRIs(relationTypeIRIs);
        frame.setSubjectIRIs(subjectIRIs);
        frame.setObjectIRIs(objectIRIs);
        frame.display();
    }

    @Override
    public void initialise() throws Exception {
        
    }

    @Override
    public void dispose() throws Exception {
        
    }
}
