/**
 * The contents of this file are subject to the license and copyright detailed
 * in the LICENSE and NOTICE files at the root of the source tree and available
 * online at
 *
 * http://www.dspace.org/license/
 */
package gr.upatras.ceid.hpclab.servlet;

import gr.upatras.ceid.hpclab.owl.OWLDSpaceQueryManager;
import gr.upatras.ceid.hpclab.owl.SemanticUnit;
import gr.upatras.ceid.hpclab.owl.SemanticUnitContext;

import java.io.IOException;
import java.sql.SQLException;
import java.util.SortedSet;

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
import org.semanticweb.owlapi.model.OWLIndividual;

/**
 * Servlet for supporting semantic search in DSpace
 *
 * @author kotsomit
 */
@SuppressWarnings("serial")
public class SemanticSearchServlet extends DSpaceServlet {

    /**
     * Logger
     */
    private static Logger log = Logger.getLogger(SemanticSearchServlet.class);
    private SemanticUnit semanticUnit;

    private boolean isABrowsingPage(HttpServletRequest request) {
        String url = request.getParameter("indURI");

        return (url != null);
    }

    @Override
    protected void doDSGet(Context context, HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException,
            SQLException, AuthorizeException {


        try {
            semanticUnit = SemanticUnitContext.getInstanceFromRequest(context, request);
            if (isABrowsingPage(request)) {
            } else {
                if (request.getParameter("expression") != null) {

                    long startTime = System.nanoTime();

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
                            /* should be replaced (returns null. getDefaultDocumentIRI()???)*/ + (semanticUnit.getOntology().getOntologyID().getDefaultDocumentIRI())
                            + "\n" + logInfo));
                }
            }
        } catch (Exception exception) {
            JSPUILogger
                    .logException("Error", request, exception);
        } finally {

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
}
