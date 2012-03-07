/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package gr.upatras.ceid.hpclab.reasoner;

public enum SupportedReasoner
{
    FACTPLUSPLUS("gr.upatras.ceid.hpclab.reasoner.OWLReasonerFactPlusPlusImpl", "FaCT++"), 
    PELLET("gr.upatras.ceid.hpclab.reasoner.OWLReasonerPelletImpl", "Pellet"),
    HERMIT("gr.upatras.ceid.hpclab.reasoner.OWLReasonerHermiTImpl", "HermiT"),
    ANY("gr.upatras.ceid.hpclab.reasoner.OWLReasonerPelletImpl", "ANY");

    private String classImpl;
    private String friendlyName;

    SupportedReasoner(String value1, String value2)
    {
        classImpl = value1;
        friendlyName = value2;
    }

    public String toString()
    {
        return classImpl;
    }
    
    public String getName()
    {
    	return friendlyName;

    }
}
