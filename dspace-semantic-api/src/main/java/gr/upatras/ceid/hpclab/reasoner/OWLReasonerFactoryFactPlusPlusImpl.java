package gr.upatras.ceid.hpclab.reasoner;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import uk.ac.manchester.cs.factplusplus.owlapiv3.FaCTPlusPlusReasonerFactory;

public class OWLReasonerFactoryFactPlusPlusImpl implements OWLReasonerFactory
{
    public OWLReasoner getReasoner(OWLOntology ontology)
    {
        FaCTPlusPlusReasonerFactory reasonerFactory = new FaCTPlusPlusReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
        
        return reasoner;
    }
}
