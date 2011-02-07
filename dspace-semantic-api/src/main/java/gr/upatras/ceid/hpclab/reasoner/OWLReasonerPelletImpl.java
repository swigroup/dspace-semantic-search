package gr.upatras.ceid.hpclab.reasoner;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

import org.apache.log4j.Logger;

public class OWLReasonerPelletImpl implements OWLReasonerFactory
{
    private static final Logger log = Logger.getLogger(OWLReasonerPelletImpl.class);

    public OWLReasoner getReasoner(OWLOntology ontology)
    {
        log.info("New reasoner based on Pellet was created");

        PelletReasonerFactory reasonerFactory = PelletReasonerFactory.getInstance();
        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
        
        return reasoner;
    }
}
