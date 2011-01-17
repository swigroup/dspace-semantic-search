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

import org.dspace.app.webui.servlet.DSpaceServlet;
import org.dspace.app.webui.util.JSPManager;
import org.dspace.authorize.AuthorizeException;
import org.dspace.core.Context;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import org.dspace.core.ConfigurationManager;

/**
 * Servlet for supporting semantic search in DSpace
 * 
 * @author kotsomit
 */

@SuppressWarnings("serial")
public class SemanticSearchServlet extends DSpaceServlet
{
    private boolean isABrowsingPage(HttpServletRequest request)
    {
        String url = request.getParameter("indURI");

        return (url != null);
    }

    private String getActiveValueFromRequest(HttpServletRequest request, String param,
            String defaultValue)
    {
        String finalValue = defaultValue;

        if (request.getParameter(param) != null)
        {
            finalValue = request.getParameter(param);
        }
        else if (request.getSession().getAttribute(param) != null)
        {
            finalValue = (String) request.getSession().getAttribute(param);
        }

        request.getSession().setAttribute(param, finalValue);

        return finalValue;
    }

    protected void doDSGet(Context context, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException, AuthorizeException
    {
        String ontoURL = ConfigurationManager.getProperty("dspace.baseUrl") + request.getContextPath() + "/dspace-ont";
        String url = getActiveValueFromRequest(request, "URL", ontoURL);
        
        String reasoner = getActiveValueFromRequest(request, "reasoner",
                SupportedReasoner.PELLET.name());

        SemanticUnit semanticUnit = null;

        try
        {
            semanticUnit = SemanticUnit.getInstance(url, SupportedReasoner.valueOf(reasoner));
        }
        catch (Exception exception)
        {
            JSPUILogger.logException(exception.getMessage(), request, exception);
        }

        if (isABrowsingPage(request))
        {
            showIndividualProperties(request, response, semanticUnit);
        }
        else
        {
            executeQueryExpression(request, semanticUnit);

            JSPManager.showJSP(request, response, "/search/semantic.jsp");
        }
    }

    private void executeQueryExpression(HttpServletRequest request, SemanticUnit semanticUnit)
    {
        String expression = request.getParameter("expression");

        if (expression != null)
        {
            OWLDSpaceQueryManager owlQueryManager = new OWLDSpaceQueryManager(semanticUnit);

            try
            {
                SortedSet<OWLIndividual> result = owlQueryManager.executeQuery(expression);

                request.getSession().setAttribute("ResultSet", result.toArray());
            }
            catch (SQLException exception)
            {
                JSPUILogger.logException("Could not resolve reference to a DSpace Handle", request,
                        exception);
            }
            catch (ParserException exception)
            {
                JSPUILogger.logException("The input expression can not be parsed", request,
                        exception);
            }
        }
    }

    public void showIndividualProperties(HttpServletRequest request, HttpServletResponse response,
            SemanticUnit semanticUnit) throws ServletException, IOException
    {
        /*
         * IMPORTANT: reasoner implementations seem only to support the getIndividuals method. It is
         * difficult then to extract inferenced assertions for individuals therefore, like in coode
         * browser implementation, we fall back to only *referenced* (explicit) axioms and
         * assertions.
         */

        IRI indIRI = IRI.create(request.getParameter("indURI"));

        OWLIndividual individual = semanticUnit.getManager().getOWLDataFactory()
                .getOWLNamedIndividual(indIRI);
        request.setAttribute("individual", individual);

        OWLDSpaceQueryManager queryManager = new OWLDSpaceQueryManager(semanticUnit);
        Set<OWLClass> types = queryManager.getOWLClasses(individual);
        request.setAttribute("class_types", types);
        request.setAttribute("ont_uri", semanticUnit.getOntology().getOntologyID().getOntologyIRI()
                .toString());

        extractAssertionsFromOntologyRefereningAxioms(request, semanticUnit, individual);

        JSPManager.showJSP(request, response, "/search/showIndProperties.jsp");
    }

    private void extractAssertionsFromOntologyRefereningAxioms(HttpServletRequest request,
            SemanticUnit semanticUnit, OWLIndividual individual)
    {
        SortedSet<OWLObjectPropertyAssertionAxiom> object_assertions = new TreeSet<OWLObjectPropertyAssertionAxiom>();
        SortedSet<OWLNegativeObjectPropertyAssertionAxiom> negative_object_assertions = new TreeSet<OWLNegativeObjectPropertyAssertionAxiom>();
        SortedSet<OWLDataPropertyAssertionAxiom> data_assertions = new TreeSet<OWLDataPropertyAssertionAxiom>();
        SortedSet<OWLNegativeDataPropertyAssertionAxiom> negative_data_assertions = new TreeSet<OWLNegativeDataPropertyAssertionAxiom>();
        SortedSet<OWLAnnotationAxiom> annotations = new TreeSet<OWLAnnotationAxiom>();

        for (OWLOntology ont : semanticUnit.getImportsClosure())
        {
            for (OWLAxiom ax : ont.getReferencingAxioms((OWLEntity) individual))
            {
                if (ax.getAxiomType() == AxiomType.OBJECT_PROPERTY_ASSERTION)
                    object_assertions.add((OWLObjectPropertyAssertionAxiom) ax);
                if (ax.getAxiomType() == AxiomType.NEGATIVE_OBJECT_PROPERTY_ASSERTION)
                    negative_object_assertions.add((OWLNegativeObjectPropertyAssertionAxiom) ax);
                if (ax.getAxiomType() == AxiomType.DATA_PROPERTY_ASSERTION)
                    data_assertions.add((OWLDataPropertyAssertionAxiom) ax);
                if (ax.getAxiomType() == AxiomType.NEGATIVE_DATA_PROPERTY_ASSERTION)
                    negative_data_assertions.add((OWLNegativeDataPropertyAssertionAxiom) ax);
                if (ax.getAxiomType() == AxiomType.ANNOTATION_ASSERTION)
                    annotations.add((OWLAnnotationAssertionAxiom) ax);
            }
        }

        request.setAttribute("object_assertions", object_assertions);
        request.setAttribute("negative_object_assertions", negative_object_assertions);
        request.setAttribute("data_assertions", data_assertions);
        request.setAttribute("negative_data_assertions", negative_data_assertions);
        request.setAttribute("annotations", annotations);
    }
}
