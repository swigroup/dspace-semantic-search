/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package gr.upatras.ceid.hpclab.owl;

import org.semanticweb.owlapi.model.OWLException;

@SuppressWarnings("serial")
public class SemanticSearchException extends OWLException
{
    public SemanticSearchException(String message, Throwable exception)
    {
        super(message, exception);
    }
}
