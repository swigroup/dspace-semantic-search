/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package gr.upatras.ceid.hpclab.reasoner;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import uk.ac.manchester.cs.factplusplus.owlapiv3.FaCTPlusPlusReasonerFactory;

import org.apache.log4j.Logger;

public class OWLReasonerFactPlusPlusImpl implements OWLReasonerFactory
{
    private static final Logger log = Logger.getLogger(OWLReasonerFactPlusPlusImpl.class);

    public OWLReasoner getReasoner(OWLOntology ontology)
    {
       log.info("New reasoner based on Fact++ was created");

        FaCTPlusPlusReasonerFactory reasonerFactory = new FaCTPlusPlusReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
        
        return reasoner;
    }
}
