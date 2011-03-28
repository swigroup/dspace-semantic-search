package gr.upatras.ceid.hpclab.test;

import gr.upatras.ceid.hpclab.owl.SemanticSearchException;
import gr.upatras.ceid.hpclab.reasoner.SupportedReasoner;

public class PelletDSpaceTest extends OWLQueryDSpaceOntology
{
    public PelletDSpaceTest() throws SemanticSearchException, InstantiationException,
            IllegalAccessException, ClassNotFoundException
    {
        super(SupportedReasoner.PELLET);
    }
}