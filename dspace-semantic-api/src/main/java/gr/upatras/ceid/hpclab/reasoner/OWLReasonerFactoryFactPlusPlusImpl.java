package gr.upatras.ceid.hpclab.reasoner;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import uk.ac.manchester.cs.factplusplus.owlapiv3.FaCTPlusPlusReasonerFactory;

import org.apache.log4j.Logger;

public class OWLReasonerFactoryFactPlusPlusImpl implements OWLReasonerFactory
{
    private static final Logger log = Logger.getLogger(OWLReasonerFactoryFactPlusPlusImpl.class);

    public OWLReasoner getReasoner(OWLOntology ontology)
    {
       log.info("New reasoner based on Fact++ was created");

        FaCTPlusPlusReasonerFactory reasonerFactory = new FaCTPlusPlusReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
        
        return reasoner;
    }
}
