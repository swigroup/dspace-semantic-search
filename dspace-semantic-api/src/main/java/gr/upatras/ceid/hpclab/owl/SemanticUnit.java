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
import org.dspace.core.LogManager;
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
import org.semanticweb.owlapi.util.Version;

public class SemanticUnit {
	private OWLOntologyManager manager;
	private OWLOntology ontology;
	private Set<OWLOntology> importsClosure;
	private ShortFormProvider shortFormProvider;
	private OWLReasoner reasoner;
	private BidirectionalShortFormProvider bidirectionalShortFormProvider;

	private static final Logger log = Logger.getLogger(SemanticUnit.class);

	private static Map<SemanticUnitMapKey, SemanticUnit> semanticUnitMap = new HashMap<SemanticUnitMapKey, SemanticUnit>();

	private SemanticUnit(String url, SupportedReasoner supportedReasoner)
			throws SemanticSearchException, InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		OWLDSpaceQueryManager owlQueryManager = setOntology(url);

		importsClosure = manager.getImportsClosure(ontology);

		setReasoner(supportedReasoner);
		setBidirectionalShortFormProvider(owlQueryManager);
	}

	//copying constructor, for the ontological part ('ontology registry')
	
	private SemanticUnit(SemanticUnit orig) {
		manager = orig.manager;
		ontology = orig.ontology;
		importsClosure = orig.importsClosure;
		shortFormProvider = orig.shortFormProvider;
		bidirectionalShortFormProvider = orig.bidirectionalShortFormProvider;		
	}

	public OWLDSpaceQueryManager setOntology(String url)
			throws SemanticSearchException {
		OWLDSpaceQueryManager owlQueryManager = new OWLDSpaceQueryManager(this);

		manager = OWLManager.createOWLOntologyManager();

		try {
			ontology = manager.loadOntology(IRI.create(url));
		} catch (OWLOntologyCreationException exception) {

			throw new SemanticSearchException("Failed to load ontology URL: "+url,
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

		long startTime = System.nanoTime();
		
		reasoner = owlReasonerFactory.getReasoner(ontology);
		
		double totalTime = (System.nanoTime() - startTime) / 1.0E06;
		Version v = reasoner.getReasonerVersion();
		String version = Integer.toString(v.getMajor()) + "."
				+ Integer.toString(v.getMinor())+ "."
				+ v.getPatch()+ "." + v.getBuild();
		log.info(LogManager.getHeader(null, "SemanticSearch-Inst",
				"Reasoner: "
						+ getReasoner().getReasonerName()
						+ " version: " + version
						+ " created. Total time: " + totalTime
						+ " ms."
		));
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
		//FIXME: getPendingChanges appear always empty!
		
		if (!reasoner.getPendingChanges().isEmpty()) {
			reasoner.flush();
			log.info("Reasoner flushed.");
			status = 1;
		}
		/* Initialize the reasoner: Precompute all inferences. Silently ignore
		 NPEs.
	 	UPDATE: Certain inference types (e.g. Data Property Assertions) take too
	 	long or fail (HerMiT) and it is unclear whether they contribute to query
	 	performance, since our query policy is class oriented. Therefore we 
	 	would cause precomputation for some inference types only. 
		 */
		InferenceType[] allowedInferences = 
				{InferenceType.CLASS_HIERARCHY, 
				 InferenceType.CLASS_ASSERTIONS};
		
		for (InferenceType t : allowedInferences) {
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
			SupportedReasoner supportedReasoner, boolean reload, String sid)
			throws SemanticSearchException, InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		
		SemanticUnitMapKey key = new SemanticUnitMapKey(url, supportedReasoner, sid);
		
		if (reload) { //replace your su with a fresh one
 			SemanticUnit su = new SemanticUnit(url, supportedReasoner);
			semanticUnitMap.put(key, su);
		}
		
		if (!semanticUnitMap.containsKey(key)){ //new user or reasoner change or ontology change
			SemanticUnitMapKey key2 = new SemanticUnitMapKey(url, supportedReasoner, "*");
			if (!semanticUnitMap.containsKey(key2)){ //no-one uses your ontology and reasoner.
				SemanticUnitMapKey key3 = new SemanticUnitMapKey(url, SupportedReasoner.ANY, "*");
				if (!semanticUnitMap.containsKey(key3)) {  //no-one uses your ontology.
					SemanticUnit su = new SemanticUnit(url, supportedReasoner);
					semanticUnitMap.put(key, su); 
				}
				else { //someone uses your ontology. 
					SemanticUnit su_orig = semanticUnitMap.get(key2);
					//Make a copy of the original (cached) Semantic Unit.
					SemanticUnit su = new SemanticUnit(su_orig);
					//Set the reasoner to the user-selected.
					su.setReasoner (supportedReasoner);	
					semanticUnitMap.put(key, su);
				}
			}
			else { //someone uses your ontology and your reasoner.
				semanticUnitMap.put(key, semanticUnitMap.get(key2));
			}
		}

		return semanticUnitMap.get(key);
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
	
		
}