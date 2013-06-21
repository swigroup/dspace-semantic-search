/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

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