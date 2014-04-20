/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.upatras.ceid.hpclab.owl;

import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;
import org.coode.owlapi.rdf.model.RDFResourceNode;
import org.coode.owlapi.rdf.rdfxml.RDFXMLRenderer;
import org.semanticweb.owlapi.io.RDFOntologyFormat;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEntityVisitor;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;

/**
 *
 * @author kotsomit
 */
public class RDFXMLIndividualRenderer extends RDFXMLRenderer {

    public RDFXMLIndividualRenderer(OWLOntologyManager manager, OWLOntology ontology, Writer w) {
        super(manager, ontology, w);
    }

    public void render(OWLNamedIndividual ind) throws IOException {
        beginDocument();
        if (createGraph(ind)) {
            beginObject();
            writeIndividualComments(ind);
            render(new RDFResourceNode(ind.getIRI()));
            renderAnonRoots();
            endObject();
        }
        endDocument();
    }

 //simplify towards only for individuals (contains too much uneccessary code)   
    private boolean createGraph(OWLEntity entity) {
        final Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
        axioms.addAll(entity.getAnnotationAssertionAxioms(ontology));
        axioms.addAll(ontology.getDeclarationAxioms(entity));

        entity.accept(new OWLEntityVisitor() {
            public void visit(OWLClass cls) {
                for (OWLAxiom ax : ontology.getAxioms(cls)) {
                    if (ax instanceof OWLDisjointClassesAxiom) {
                        OWLDisjointClassesAxiom disjAx = (OWLDisjointClassesAxiom) ax;
                        if (disjAx.getClassExpressions().size() > 2) {
                            continue;
                        }
                    }
                    axioms.add(ax);
                }
                for (OWLHasKeyAxiom ax : ontology.getAxioms(AxiomType.HAS_KEY)) {
                    if (ax.getClassExpression().equals(cls)) {
                        axioms.add(ax);
                    }
                }

            }

            public void visit(OWLDatatype datatype) {
                axioms.addAll(ontology.getDatatypeDefinitions(datatype));
                createGraph(axioms);
            }

            public void visit(OWLNamedIndividual individual) {
                for (OWLAxiom ax : ontology.getAxioms(individual)) {
                    if (ax instanceof OWLDifferentIndividualsAxiom) {
                        continue;
                    }
                    axioms.add(ax);
                }
            }

            public void visit(OWLDataProperty property) {
                for (OWLAxiom ax : ontology.getAxioms(property)) {
                    if (ax instanceof OWLDisjointDataPropertiesAxiom) {
                        if (((OWLDisjointDataPropertiesAxiom) ax).getProperties().size() > 2) {
                            continue;
                        }
                    }
                    axioms.add(ax);
                }
            }

            public void visit(OWLObjectProperty property) {
                for (OWLAxiom ax : ontology.getAxioms(property)) {
                    if (ax instanceof OWLDisjointObjectPropertiesAxiom) {
                        if (((OWLDisjointObjectPropertiesAxiom) ax).getProperties().size() > 2) {
                            continue;
                        }
                    }
                    axioms.add(ax);
                }
                for (OWLSubPropertyChainOfAxiom ax : ontology.getAxioms(AxiomType.SUB_PROPERTY_CHAIN_OF)) {
                    if (ax.getSuperProperty().equals(property)) {
                        axioms.add(ax);
                    }
                }
                axioms.addAll(ontology.getAxioms(manager.getOWLDataFactory().getOWLObjectInverseOf(property)));
            }

            public void visit(OWLAnnotationProperty property) {
                axioms.addAll(ontology.getAxioms(property));
            }
        });

        if (axioms.isEmpty() && shouldInsertDeclarations()) {
            if (RDFOntologyFormat.isMissingType(entity, ontology)) {
                axioms.add(ontology.getOWLOntologyManager().getOWLDataFactory().getOWLDeclarationAxiom(entity));
            }
        }
        createGraph(axioms);

        return !axioms.isEmpty();
    }
}