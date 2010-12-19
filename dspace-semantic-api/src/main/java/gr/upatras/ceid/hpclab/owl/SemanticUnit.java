package gr.upatras.ceid.hpclab.owl;

import gr.upatras.ceid.hpclab.reasoner.OWLReasonerFactory;
import gr.upatras.ceid.hpclab.reasoner.SupportedReasoner;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.QNameShortFormProvider;
import org.semanticweb.owlapi.util.ShortFormProvider;

public class SemanticUnit
{
    private OWLOntologyManager manager;
    private OWLOntology ontology;
    private Set<OWLOntology> importsClosure;
    private ShortFormProvider shortFormProvider;
    private OWLReasoner reasoner;
    private BidirectionalShortFormProvider bidirectionalShortFormProvider;

    private static Map<String, SemanticUnit> semanticUnitMap = new HashMap<String, SemanticUnit>();

    private SemanticUnit(String url, SupportedReasoner supportedReasoner)
            throws SemanticSearchException, InstantiationException, IllegalAccessException,
            ClassNotFoundException
    {
        OWLDSpaceQueryManager owlQueryManager = new OWLDSpaceQueryManager(this);

        manager = OWLManager.createOWLOntologyManager();

        try
        {
            ontology = manager.loadOntology(IRI.create(url));
        }
        catch (OWLOntologyCreationException exception)
        {

            throw new SemanticSearchException("Could not load ontology", exception);
        }

        importsClosure = manager.getImportsClosure(ontology);

        OWLReasonerFactory owlReasonerFactory = (OWLReasonerFactory) Class.forName(
                supportedReasoner.toString()).newInstance();
        reasoner = owlReasonerFactory.getReasoner(ontology);

        Map<String, String> namespaces = owlQueryManager.buildNamespaceMapFromImportsClosure();
        shortFormProvider = new QNameShortFormProvider(namespaces);

        bidirectionalShortFormProvider = BidirectionalShortFormProviderFactory.getInstance(this);
    }

    public static SemanticUnit getInstance(String url, SupportedReasoner supportedReasoner)
            throws SemanticSearchException, InstantiationException, IllegalAccessException,
            ClassNotFoundException
    {
        if (!semanticUnitMap.containsKey(url))
        {
            semanticUnitMap.put(url, new SemanticUnit(url, supportedReasoner));
        }

        return semanticUnitMap.get(url);
    }

    public OWLOntologyManager getManager()
    {
        return manager;
    }

    public OWLOntology getOntology()
    {
        return ontology;
    }

    public Set<OWLOntology> getImportsClosure()
    {
        return importsClosure;
    }

    public ShortFormProvider getShortFormProvider()
    {
        return shortFormProvider;
    }

    public OWLReasoner getReasoner()
    {
        return reasoner;
    }

    public BidirectionalShortFormProvider getBidirectionalShortFormProvider()
    {
        return bidirectionalShortFormProvider;
    }
}