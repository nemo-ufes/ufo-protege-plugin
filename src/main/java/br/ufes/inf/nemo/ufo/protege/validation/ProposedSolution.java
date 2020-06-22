/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege.validation;

import java.util.ArrayList;
import java.util.List;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLObject;

/**
 *
 * @author luciano
 */
public class ProposedSolution {

    public static final IRI RDFS_SUBCLASSOF =
            IRI.create("http://www.w3.org/2000/01/rdf-schema#subClassOf");

    public static final IRI RDF_TYPE =
            IRI.create("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");

    public static Builder newProposal(String title, Rule.Violation violation) {
        return new Builder(new ProposalData(
                title, violation
        ));
    }

    private final ProposalData data;

    private ProposedSolution(ProposalData data) {
        this.data = data;
    }

    private static class ProposalData {
        private final String title;
        private final Rule.Violation violation;

        public ProposalData(String title, Rule.Violation violation) {
            this.title = title;
            this.violation = violation;
        }

        public String getTitle() {
            return title;
        }

        public Rule.Violation getViolation() {
            return violation;
        }
    }

    public static class Builder {

        private final ProposalData data;
        private final List<AxiomStatementBuilder> statements = new ArrayList<>();

        private Builder(ProposalData data) {
            this.data = data;
        }

        public SubjectSpecifier addAxiom() {
            AxiomStatementBuilder result = new AxiomStatementBuilder();
            statements.add(result);
            return result;
        }

        public SubjectSpecifier removeAxiom() {
            AxiomStatementBuilder result = new AxiomStatementBuilder();
            statements.add(result);
            return result;
        }

        public ProposedSolution build() {
            return new ProposedSolution(data);
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
            Builder object(IRI owlObject);
        }

        private class AxiomStatementBuilder implements
                SubjectSpecifier, VerbSpecifier, ObjectSpecifier {

            OWLObject subject;
            OWLObject verb;
            OWLObject object;

            @Override
            public VerbSpecifier target() {
                this.subject = data.violation.getSubject();
                return this;
            }

            @Override
            public VerbSpecifier subject(OWLObject owlObject) {
                this.subject = owlObject;
                return this;
            }

            @Override
            public ObjectSpecifier subClassOf() {
                this.verb = RDFS_SUBCLASSOF;
                return this;
            }

            @Override
            public ObjectSpecifier type() {
                this.verb = RDF_TYPE;
                return this;
            }

            @Override
            public Builder object(IRI owlObject) {
                this.object = owlObject;
                return Builder.this;
            }
        }
    }
}
