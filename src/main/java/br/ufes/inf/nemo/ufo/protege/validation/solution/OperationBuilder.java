/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.validation.solution;

import br.ufes.inf.nemo.ufo.protege.validation.Validation;
import br.ufes.inf.nemo.ufo.protege.validation.Violation;
import java.util.List;
import java.util.Set;
import org.semanticweb.owlapi.model.HasIRI;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLObject;
import static org.semanticweb.owlapi.rdf.rdfxml.parser.RDFConstants.RDFS_SUBCLASSOF;
import static org.semanticweb.owlapi.rdf.rdfxml.parser.RDFConstants.RDF_TYPE;

/**
 *
 * @author luciano
 */
public class OperationBuilder {

    private final Validation validation;
    private final List<ProposedOperation> operations;

    private OperationBuilder(
            Validation validation, List<ProposedOperation> operations) {
        this.operations = operations;
        this.validation = validation;
    }

    public SubjectSpecifier addAxiom() {
        return new OperationParameterBuilder() {
            @Override
            protected ProposedOperation newOperationInstance() {
                return new AddAxiomOperation(subject, verb, object);
            }
        };
    }

    public SubjectSpecifier removeAxiom() {
        return new OperationParameterBuilder() {
            @Override
            protected ProposedOperation newOperationInstance() {
                return new RemoveAxiomOperation(subject, verb, object);
            }
        };
    }

    /**
     * Interface for setting the subject of axiom being built.
     *
     * The axiom building will be done in three steps: specifying subject,
     * specifying verb and specifying object. Each of these steps shall be
     * done through three interfaces (SubjectSpecifier,
     * {@link VerbSpecifier} and {@link ObjectSpecifier}, repectively),
     * which will be returned in sequence, as a result of a call to a method
     * of the previous interface, except for this interface, which is
     * accessed through two methods of {@link Builder} class, namely,
     * {@link Builder#addAxiom() } or {@link Builder#removeAxiom() }.
     */
    public interface SubjectSpecifier {

        /**
         * Set the rule's target as the axiom's subject.
         *
         * @return Verb specifier.
         */
        VerbSpecifier target();

        /**
         * Specifies the axiom's subject.
         *
         * @param owlObject Object to be used as axiom's subject
         * @return Verb specifier
         */
        VerbSpecifier subject(OWLObject owlObject);
    }

    /**
     * Interface for setting the verb of axiom being built.
     *
     * @see SubjectSpecifier
     * @see ObjectSpecifier
     */
    public interface VerbSpecifier {

        /**
         * Specify rdfs:subClassOf as the axiom's verb.
         *
         * @return Object specifier
         */
        ObjectSpecifier subClassOf();

        /**
         * Specify rdf:type as the axiom's verb.
         *
         * @return Object specifier
         */
        ObjectSpecifier type();
    }

    /**
     * Interface for setting the verb of axiom being built.
     *
     * @see SubjectSpecifier
     * @see VerbSpecifier
     */
    public interface ObjectSpecifier {

        OperationBuilder object(IRI owlObject);
    }

    private abstract class OperationParameterBuilder
            implements SubjectSpecifier, VerbSpecifier, ObjectSpecifier {

        OperationParameter subject;
        OperationParameter verb;
        OperationParameter object;

        private OperationParameter parameterFrom(OWLObject owlObject) {
            HasIRI hasIRI = (HasIRI) owlObject;
            Set<IRI> possibleValues = validation.getIRISet(hasIRI.getIRI());
            return new OperationParameter(possibleValues);
        }

        @Override
        public VerbSpecifier target() {
            return subject(validation.getCurrentTarget());
        }

        @Override
        public VerbSpecifier subject(OWLObject owlObject) {
            this.subject = parameterFrom(owlObject);
            return this;
        }

        @Override
        public ObjectSpecifier subClassOf() {
            this.verb = parameterFrom(IRI.create(RDFS_SUBCLASSOF));
            return this;
        }

        @Override
        public ObjectSpecifier type() {
            this.verb = parameterFrom(IRI.create(RDF_TYPE));
            return this;
        }

        @Override
        public OperationBuilder object(IRI owlObject) {
            this.object =  parameterFrom(owlObject);
            operations.add(newOperationInstance());
            return OperationBuilder.this;
        }

        protected abstract ProposedOperation newOperationInstance();
    }
}
