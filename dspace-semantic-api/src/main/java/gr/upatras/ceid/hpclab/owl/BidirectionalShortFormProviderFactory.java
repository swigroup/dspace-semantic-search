/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package gr.upatras.ceid.hpclab.owl;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;

public class BidirectionalShortFormProviderFactory
{
    public static BidirectionalShortFormProvider getInstance(SemanticUnit semanticUnit)
    {
        BidirectionalShortFormProvider providerAdapter = new BidirectionalShortFormProviderAdapter(
                semanticUnit.getManager(), semanticUnit.getImportsClosure(), semanticUnit
                        .getShortFormProvider());

        return providerAdapter;
    }

    public static Set<String> getEntityNames(SemanticUnit semanticUnit)
    {
        Set<String> names = new HashSet<String>();

        BidirectionalShortFormProvider shortFormProvider = semanticUnit
                .getBidirectionalShortFormProvider();

        names = shortFormProvider.getShortForms();
        shortFormProvider.dispose();

        return names;
    }
}