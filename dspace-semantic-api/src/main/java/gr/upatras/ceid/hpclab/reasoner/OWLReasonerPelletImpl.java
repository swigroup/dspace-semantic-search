package gr.upatras.ceid.hpclab.reasoner;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

public class OWLReasonerPelletImpl implements OWLReasonerFactory
{
    public OWLReasoner getReasoner(OWLOntology ontology)
    {
        PelletReasonerFactory reasonerFactory = PelletReasonerFactory.getInstance();
        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
        
        return reasoner;
    }
}
package gr.upatras.ceid.hpclab.reasoner;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

public class OWLReasonerPelletImpl implements OWLReasonerFactory
{
    public OWLReasoner getReasoner(OWLOntology ontology)
    {
        PelletReasonerFactory reasonerFactory = PelletReasonerFactory.getInstance();
        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
        
        return reasoner;
    }
}
package gr.upatras.ceid.hpclab.reasoner;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

public class OWLReasonerPelletImpl implements OWLReasonerFactory
{
    public OWLReasoner getReasoner(OWLOntology ontology)
    {
        PelletReasonerFactory reasonerFactory = PelletReasonerFactory.getInstance();
        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
        
        return reasoner;
    }
}