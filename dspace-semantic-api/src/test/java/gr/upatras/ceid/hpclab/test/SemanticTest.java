package gr.upatras.ceid.hpclab.test;

import gr.upatras.ceid.hpclab.owl.SemanticSearchException;
import gr.upatras.ceid.hpclab.owl.SemanticUnit;
import gr.upatras.ceid.hpclab.reasoner.SupportedReasoner;

public class SemanticTest
{
    protected SemanticUnit semanticUnit;

    public SemanticTest(SupportedReasoner reasoner, String ontology)
            throws SemanticSearchException, InstantiationException, IllegalAccessException,
            ClassNotFoundException
    {
        semanticUnit = SemanticUnit.getInstance(ontology, reasoner);
    }
}