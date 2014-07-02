/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package gr.upatras.ceid.hpclab.owl;

import gr.upatras.ceid.hpclab.reasoner.SupportedReasoner;
import gr.upatras.ceid.hpclab.servlet.JSPUILogger;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;
import org.dspace.core.LogManager;
import org.semanticweb.owlapi.util.Version;

public class SemanticUnitContext {

   private static Logger log = Logger.getLogger(SemanticUnitContext.class);   
    //possibly move to SemanticUnit class in order to reuse
    public static SemanticUnit getInstanceFromRequest(Context context, HttpServletRequest request) {

        String ontoURL = ConfigurationManager.getProperty("dspace.baseUrl")
                + request.getContextPath() + "/dspace-ont";
        String url = getActiveValueFromRequest(request, "URL", ontoURL);
        String reasoner = getActiveValueFromRequest(request, "reasoner",
                SupportedReasoner.PELLET.name());
        boolean reload = request.getParameter("reload") == null ? false
                : Boolean.parseBoolean((String) request.getParameter("reload"));

        String sid = request.getSession().getId();

        SemanticUnit su = null;
        try {
            su = SemanticUnit.getInstance(url, SupportedReasoner
                    .valueOf(reasoner), reload, sid);
            Version v = su.getReasoner().getReasonerVersion();
            String version = Integer.toString(v.getMajor()) + "."
                    + Integer.toString(v.getMinor()) + "."
                    + v.getPatch() + "." + v.getBuild();
            long startTime = System.nanoTime();
            if (su.initializeReasoner(su.getReasoner()) > 0) {
                double totalTime = (System.nanoTime() - startTime) / 1.0E06;
                log.info(LogManager.getHeader(context, "SemanticSearch-Init",
                        "Reasoner: "
                        + su.getReasoner().getReasonerName()
                        + " version: " + version
                        + " initialized. Total time: " + totalTime
                        + " ms."
                        + "Ontology URI: "
                        + url));
            }
        } catch (Exception exception) {
            JSPUILogger
                    .logException("Error", request, exception);
        }
        return su;
    }  
    private static String getActiveValueFromRequest(HttpServletRequest request,
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
}
