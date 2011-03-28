package gr.upatras.ceid.hpclab.reasoner;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

public interface OWLReasonerFactory
{
    public OWLReasoner getReasoner(OWLOntology ontology);
}
