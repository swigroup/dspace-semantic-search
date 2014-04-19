/**
 * The contents of this file are subject to the license and copyright detailed
 * in the LICENSE and NOTICE files at the root of the source tree and available
 * online at
 *
 * http://www.dspace.org/license/
 */
package gr.upatras.ceid.hpclab.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import com.sun.jersey.api.view.Viewable;
import javax.ws.rs.PathParam;
import gr.upatras.ceid.hpclab.owl.*;
import org.dspace.app.webui.util.UIUtil;
import org.semanticweb.owlapi.model.IRI;

@Path("/semantic-search/resource/{id}")

public class ShowIndividualPropertiesModel {

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Viewable index(@Context HttpServletRequest request, @PathParam("id") String id) {
        System.out.println("/INDEX called");
        IndividualProperties indp = new IndividualProperties();
        IRI indIRI = IRI.create(id);
        request.setAttribute("indURI", id);

        try {
            SemanticUnit su = SemanticUnitContext.getInstanceFromRequest(UIUtil.obtainContext(request), request);
      
        
        indp.getIndividualProperties (request, indIRI, su);
        }
        catch (Exception exception) {
                    JSPUILogger
                    .logException("Error", request, exception);
       }
        return new Viewable("/search/showIndProperties.jsp", null);}
    
    @GET
    // ("application/rdf+xml")
    @Produces(MediaType.APPLICATION_XML)
    public Viewable index2(@Context HttpServletRequest request, @PathParam("id") String id) {
        request.setAttribute("obj", new String("IT Works"));
        System.out.println("/INDEX called");
        return new Viewable("/search/showIndProperties.jsp", null);
    }    
}