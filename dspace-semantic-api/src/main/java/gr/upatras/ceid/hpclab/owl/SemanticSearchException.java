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
