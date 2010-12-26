package gr.upatras.ceid.hpclab.test;

import gr.upatras.ceid.hpclab.owl.SemanticSearchException;
import gr.upatras.ceid.hpclab.reasoner.SupportedReasoner;

public class OWLQueryFactPlusPlusReasonerTest extends OWLQueryTest
{
    public OWLQueryFactPlusPlusReasonerTest() throws SemanticSearchException,
            InstantiationException, IllegalAccessException, ClassNotFoundException
    {
        super(SupportedReasoner.FACTPLUSPLUS);
    }
}