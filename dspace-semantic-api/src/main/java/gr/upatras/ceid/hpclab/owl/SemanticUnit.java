/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package gr.upatras.ceid.hpclab.owl;

import gr.upatras.ceid.hpclab.reasoner.OWLReasonerFactory;
import gr.upatras.ceid.hpclab.reasoner.SupportedReasoner;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.QNameShortFormProvider;
import org.semanticweb.owlapi.util.ShortFormProvider;

public class SemanticUnit {
	private OWLOntologyManager manager;
	private OWLOntology ontology;
	private Set<OWLOntology> importsClosure;
	private ShortFormProvider shortFormProvider;
	private OWLReasoner reasoner;
	private BidirectionalShortFormProvider bidirectionalShortFormProvider;

	private static final Logger log = Logger.getLogger(SemanticUnit.class);

	private static Map<String, SemanticUnit> semanticUnitMap = new HashMap<String, SemanticUnit>();

	private SemanticUnit(String url, SupportedReasoner supportedReasoner)
			throws SemanticSearchException, InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		OWLDSpaceQueryManager owlQueryManager = setOntology(url);

		importsClosure = manager.getImportsClosure(ontology);

		setReasoner(supportedReasoner);
		setBidirectionalShortFormProvider(owlQueryManager);
	}

	public OWLDSpaceQueryManager setOntology(String url)
			throws SemanticSearchException {
		OWLDSpaceQueryManager owlQueryManager = new OWLDSpaceQueryManager(this);

		manager = OWLManager.createOWLOntologyManager();

		try {
			ontology = manager.loadOntology(IRI.create(url));
		} catch (OWLOntologyCreationException exception) {

			throw new SemanticSearchException("Could not load ontology",
					exception);
		}

		return owlQueryManager;
	}

	public void setBidirectionalShortFormProvider(
			OWLDSpaceQueryManager owlQueryManager) {
		Map<String, String> namespaces = owlQueryManager
				.buildNamespaceMapFromImportsClosure();
		shortFormProvider = new QNameShortFormProvider(namespaces);

		bidirectionalShortFormProvider = BidirectionalShortFormProviderFactory
				.getInstance(this);
	}

	public void setReasoner(SupportedReasoner supportedReasoner)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		OWLReasonerFactory owlReasonerFactory = (OWLReasonerFactory) Class
				.forName(supportedReasoner.toString()).newInstance();

		reasoner = owlReasonerFactory.getReasoner(ontology);

	}

	/** Initialize the  reasoner by precomputing inferences and flushing changes
	 * if needed. 
	 * @param reasoner
	 * @return 0 if no initialization was needed.
	 * 
	 */
	public int initializeReasoner(OWLReasoner reasoner) {
		int status = 0;
		// in case ontology has changed.
		if (!reasoner.getPendingChanges().isEmpty()) {
			reasoner.flush();
			log.info("Reasoner flushed.");
			status = 1;
		}
		// Initialize the reasoner: Precompute all inferences. Silently ignore
		// NPEs.
		for (InferenceType t : InferenceType.values()) {
			try {
				if (!reasoner.isPrecomputed(t) && reasoner.getPrecomputableInferenceTypes().contains(t)) {
					status = 2;
					reasoner.precomputeInferences(t);
				}
			} catch (NullPointerException e) {
				log.warn("NPE precomputing inference type: " + t);
			}
			log.debug(t + " is precomputed:" + reasoner.isPrecomputed(t));
		}
		return status;
	}

	public static SemanticUnit getInstance(String url,
			SupportedReasoner supportedReasoner)
			throws SemanticSearchException, InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		if (!semanticUnitMap.containsKey(url)) {
			SemanticUnit su = new SemanticUnit(url, supportedReasoner);
			semanticUnitMap.put(url, su);
		}
		// don't re-instatiate the reasoner if you don't have to
		else if (!reasonerExists(semanticUnitMap.get(url), supportedReasoner)) {
			semanticUnitMap.get(url).setReasoner(supportedReasoner);
		}

		return semanticUnitMap.get(url);
	}

	public OWLOntologyManager getManager() {
		return manager;
	}

	public OWLOntology getOntology() {
		return ontology;
	}

	public Set<OWLOntology> getImportsClosure() {
		return importsClosure;
	}

	public ShortFormProvider getShortFormProvider() {
		return shortFormProvider;
	}

	public OWLReasoner getReasoner() {
		return reasoner;
	}

	public BidirectionalShortFormProvider getBidirectionalShortFormProvider() {
		return bidirectionalShortFormProvider;
	}

	private static boolean reasonerExists(SemanticUnit su,
			SupportedReasoner supportedReasoner) {

		String currentReasoner = su.getReasoner().getReasonerName();

		// at least works for Pellet!
		return supportedReasoner.toString().contains(currentReasoner);

	}
}