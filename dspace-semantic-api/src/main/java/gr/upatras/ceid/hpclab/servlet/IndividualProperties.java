/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package gr.upatras.ceid.hpclab.servlet;

import gr.upatras.ceid.hpclab.owl.OWLDSpaceQueryManager;
import gr.upatras.ceid.hpclab.owl.SemanticUnit;
import java.io.IOException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

public class IndividualProperties {

    
public void getIndividualProperties(HttpServletRequest request, IRI indIRI, 
            SemanticUnit semanticUnit)
            throws ServletException, IOException {
        /*
         * IMPORTANT: reasoner implementations seem only to support the
         * getIndividuals method. It is difficult then to extract inferenced
         * assertions for individuals therefore, like in coode browser
         * implementation, we fall back to only *referenced* (explicit) axioms
         * and assertions.
         */

        OWLIndividual individual = semanticUnit.getManager()
                .getOWLDataFactory().getOWLNamedIndividual(indIRI);
        request.setAttribute("individual", individual);

        OWLDSpaceQueryManager queryManager = new OWLDSpaceQueryManager(
                semanticUnit);
        Set<OWLClass> types = queryManager.getOWLClasses(individual);
        request.setAttribute("class_types", types);
        extractAssertionsFromOntologyReferencingAxioms(request, semanticUnit,
                individual);
    }

    private void extractAssertionsFromOntologyReferencingAxioms(
            HttpServletRequest request, SemanticUnit semanticUnit,
            OWLIndividual individual) {
        SortedSet<OWLObjectPropertyAssertionAxiom> object_assertions = new TreeSet<OWLObjectPropertyAssertionAxiom>();
        SortedSet<OWLNegativeObjectPropertyAssertionAxiom> negative_object_assertions = new TreeSet<OWLNegativeObjectPropertyAssertionAxiom>();
        SortedSet<OWLDataPropertyAssertionAxiom> data_assertions = new TreeSet<OWLDataPropertyAssertionAxiom>();
        SortedSet<OWLNegativeDataPropertyAssertionAxiom> negative_data_assertions = new TreeSet<OWLNegativeDataPropertyAssertionAxiom>();
        SortedSet<OWLAnnotationAssertionAxiom> annotations = new TreeSet<OWLAnnotationAssertionAxiom>();

        for (OWLOntology ont : semanticUnit.getImportsClosure()) {
            for (OWLAxiom ax : ont.getReferencingAxioms((OWLEntity) individual)) {
                if (ax.getAxiomType() == AxiomType.OBJECT_PROPERTY_ASSERTION) {
                    object_assertions.add((OWLObjectPropertyAssertionAxiom) ax);
                }
                if (ax.getAxiomType() == AxiomType.NEGATIVE_OBJECT_PROPERTY_ASSERTION) {
                    negative_object_assertions
                            .add((OWLNegativeObjectPropertyAssertionAxiom) ax);
                }
                if (ax.getAxiomType() == AxiomType.DATA_PROPERTY_ASSERTION) {
                    data_assertions.add((OWLDataPropertyAssertionAxiom) ax);
                }
                if (ax.getAxiomType() == AxiomType.NEGATIVE_DATA_PROPERTY_ASSERTION) {
                    negative_data_assertions
                            .add((OWLNegativeDataPropertyAssertionAxiom) ax);
                }
                if (ax.getAxiomType() == AxiomType.ANNOTATION_ASSERTION) {
                    annotations.add((OWLAnnotationAssertionAxiom) ax);
                }
            }
        }

        request.setAttribute("object_assertions", object_assertions);
        request.setAttribute("negative_object_assertions",
                negative_object_assertions);
        request.setAttribute("data_assertions", data_assertions);
        request.setAttribute("negative_data_assertions",
                negative_data_assertions);
        request.setAttribute("annotations", annotations);
    }    
}
