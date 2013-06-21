/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package gr.upatras.ceid.hpclab.servlet;

import gr.upatras.ceid.hpclab.owl.OWLDSpaceQueryManager;
import gr.upatras.ceid.hpclab.owl.SemanticUnit;
import gr.upatras.ceid.hpclab.reasoner.SupportedReasoner;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.dspace.app.webui.servlet.DSpaceServlet;
import org.dspace.app.webui.util.JSPManager;
import org.dspace.app.webui.util.UIUtil;
import org.dspace.authorize.AuthorizeException;
import org.dspace.core.Context;
import org.dspace.core.LogManager;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.Version;

import org.dspace.core.ConfigurationManager;

/**
 * Servlet for supporting semantic search in DSpace
 * 
 * @author kotsomit
 */

@SuppressWarnings("serial")
public class SemanticSearchServlet extends DSpaceServlet {
	/** Logger */
	private static Logger log = Logger.getLogger(SemanticSearchServlet.class);

	private boolean isABrowsingPage(HttpServletRequest request) {
		String url = request.getParameter("indURI");

		return (url != null);
	}

	private String getActiveValueFromRequest(HttpServletRequest request,
			String param, String defaultValue) {
		String finalValue = defaultValue;

		if (request.getParameter(param) != null) {
			finalValue = request.getParameter(param);
		} else if (request.getSession().getAttribute(param) != null) {
			finalValue = (String) request.getSession().getAttribute(param);
		}

		request.getSession().setAttribute(param, finalValue);

		return finalValue;
	}

	protected void doDSGet(Context context, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException,
			SQLException, AuthorizeException {
		String ontoURL = ConfigurationManager.getProperty("dspace.baseUrl")
				+ request.getContextPath() + "/dspace-ont";
		String url = getActiveValueFromRequest(request, "URL", ontoURL);
		String reasoner = getActiveValueFromRequest(request, "reasoner",
				SupportedReasoner.PELLET.name());
		boolean reload = request.getParameter("reload") == null ? false 
				: Boolean.parseBoolean((String) request.getParameter("reload")) ;

		String sid = request.getSession().getId();
		
		SemanticUnit semanticUnit = null;

		try {
			semanticUnit = SemanticUnit.getInstance(url, SupportedReasoner
					.valueOf(reasoner), reload, sid);
			Version v = semanticUnit.getReasoner().getReasonerVersion();
			String version = Integer.toString(v.getMajor()) + "."
					+ Integer.toString(v.getMinor()) + "."
					+ v.getPatch()+ "." + v.getBuild();
			long startTime = System.nanoTime();
			if (semanticUnit.initializeReasoner(semanticUnit.getReasoner()) > 0) {
				double totalTime = (System.nanoTime() - startTime) / 1.0E06;
				log.info(LogManager.getHeader(context, "SemanticSearch-Init",
						"Reasoner: "
								+ semanticUnit.getReasoner().getReasonerName()
								+ " version: " + version
								+ " initialized. Total time: " + totalTime
								+ " ms."
								+ "Ontology URI: "
								+ url
				));
			}
			if (isABrowsingPage(request)) {
				showIndividualProperties(request, response, semanticUnit);
			} else {
				if (request.getParameter("expression") != null) {

					startTime = System.nanoTime();

					executeQueryExpression(request, semanticUnit);

					double totalTime = (System.nanoTime() - startTime) / 1.0E06;
          
          //need to pass this parameter in order to print the total query time (new feature in dspace-3.x)
          request.getSession().setAttribute("totalTime", totalTime);

					// TODO: Number of results should be returned by appropriate
					// method (executeQueryExpression)
					Object[] results = (Object[]) request.getSession()
							.getAttribute("ResultSet");
					String resultsSize = "No results!";
					if (results != null) {
						resultsSize = Integer.toString(results.length);
					}
					String logInfo = UIUtil.getRequestLogInfo(request);
					log.info(LogManager.getHeader(context, "SemanticSearch",
							"query=\"" + request.getParameter("expression")
									+ "\", reasoner="
									+ semanticUnit.getReasoner().getReasonerName()
									+ ", results=(" + resultsSize + "), time= "
									+ totalTime + " ms." 
									+ "Ontology URL: "
									+ url
									+ "\n" + logInfo));
				}
			}
		} catch (Exception exception) {
			JSPUILogger
					.logException("Error", request, exception);
		}
          finally {    

			JSPManager.showJSP(request, response, "/search/semantic.jsp");
		}
	}

	private void executeQueryExpression(HttpServletRequest request,
			SemanticUnit semanticUnit) {
		String expression = request.getParameter("expression");

		if (expression != null) {
			OWLDSpaceQueryManager owlQueryManager = new OWLDSpaceQueryManager(
					semanticUnit);

			try {
				// clear result set in case executeQuery throws errors
				request.getSession().removeAttribute("ResultSet");
				SortedSet<OWLIndividual> result = owlQueryManager
						.executeQuery(expression);
				request.getSession()
						.setAttribute("ResultSet", result.toArray());

			} catch (SQLException exception) {
				JSPUILogger.logException(
						"Could not resolve reference to a DSpace Handle",
						request, exception);
			} catch (ParserException exception) {
				JSPUILogger.logException(
						"The input expression can not be parsed", request,
						exception);
			} catch (NullPointerException exception) {
				JSPUILogger.logException(
						"Reasoner threw Null Pointer Exception", request,
						exception);
				log.error("reasoner NPE", exception);
			} catch (Exception exception) {
				JSPUILogger.logException(
						"Reasoner threw an Exception of " + exception.getClass(), request,
						exception);
				log.error("reasoner Exception", exception);
			}

		}
	}

	public void showIndividualProperties(HttpServletRequest request,
			HttpServletResponse response, SemanticUnit semanticUnit)
			throws ServletException, IOException {
		/*
		 * IMPORTANT: reasoner implementations seem only to support the
		 * getIndividuals method. It is difficult then to extract inferenced
		 * assertions for individuals therefore, like in coode browser
		 * implementation, we fall back to only *referenced* (explicit) axioms
		 * and assertions.
		 */

		IRI indIRI = IRI.create(request.getParameter("indURI"));

		OWLIndividual individual = semanticUnit.getManager()
				.getOWLDataFactory().getOWLNamedIndividual(indIRI);
		request.setAttribute("individual", individual);

		OWLDSpaceQueryManager queryManager = new OWLDSpaceQueryManager(
				semanticUnit);
		Set<OWLClass> types = queryManager.getOWLClasses(individual);
		request.setAttribute("class_types", types);
		extractAssertionsFromOntologyReferencingAxioms(request, semanticUnit,
				individual);

		JSPManager.showJSP(request, response, "/search/showIndProperties.jsp");
	}

	private void extractAssertionsFromOntologyReferencingAxioms(
			HttpServletRequest request, SemanticUnit semanticUnit,
			OWLIndividual individual) {
		SortedSet<OWLObjectPropertyAssertionAxiom> object_assertions = new TreeSet<OWLObjectPropertyAssertionAxiom>();
		SortedSet<OWLNegativeObjectPropertyAssertionAxiom> negative_object_assertions = new TreeSet<OWLNegativeObjectPropertyAssertionAxiom>();
		SortedSet<OWLDataPropertyAssertionAxiom> data_assertions = new TreeSet<OWLDataPropertyAssertionAxiom>();
		SortedSet<OWLNegativeDataPropertyAssertionAxiom> negative_data_assertions = new TreeSet<OWLNegativeDataPropertyAssertionAxiom>();
		SortedSet<OWLAnnotationAssertionAxiom> annotations = new TreeSet<OWLAnnotationAssertionAxiom>();

		for (OWLOntology ont : semanticUnit.getImportsClosure()) {
			for (OWLAxiom ax : ont.getReferencingAxioms((OWLEntity) individual)) {
				if (ax.getAxiomType() == AxiomType.OBJECT_PROPERTY_ASSERTION)
					object_assertions.add((OWLObjectPropertyAssertionAxiom) ax);
				if (ax.getAxiomType() == AxiomType.NEGATIVE_OBJECT_PROPERTY_ASSERTION)
					negative_object_assertions
							.add((OWLNegativeObjectPropertyAssertionAxiom) ax);
				if (ax.getAxiomType() == AxiomType.DATA_PROPERTY_ASSERTION)
					data_assertions.add((OWLDataPropertyAssertionAxiom) ax);
				if (ax.getAxiomType() == AxiomType.NEGATIVE_DATA_PROPERTY_ASSERTION)
					negative_data_assertions
							.add((OWLNegativeDataPropertyAssertionAxiom) ax);
				if (ax.getAxiomType() == AxiomType.ANNOTATION_ASSERTION)
					annotations.add((OWLAnnotationAssertionAxiom) ax);
			}
		}

		request.setAttribute("object_assertions", object_assertions);
		request.setAttribute("negative_object_assertions",
				negative_object_assertions);
		request.setAttribute("data_assertions", data_assertions);
		request.setAttribute("negative_data_assertions",
				negative_data_assertions);
		request.setAttribute("annotations", annotations);
	}
}
