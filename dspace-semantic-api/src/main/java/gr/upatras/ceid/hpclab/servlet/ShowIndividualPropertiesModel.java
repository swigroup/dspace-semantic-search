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
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import org.dspace.app.webui.util.UIUtil;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

@Path("/semantic-search/")
public class ShowIndividualPropertiesModel {

    @Context
    UriInfo uriInfo;

  /*  @GET
    @Path("/resource/")
    @Produces(MediaType.TEXT_HTML)
    public Response redirectHTML() {
        UriBuilder addressBuilder = uriInfo.getBaseUriBuilder();
        addressBuilder.path("/page/");
        return Response.seeOther(addressBuilder.build()).build();
    }
    
    @GET
    @Path("/resource/")
    @Produces(MediaType.APPLICATION_XML)
    public Response redirectXML() {
        UriBuilder addressBuilder = uriInfo.getBaseUriBuilder();
        addressBuilder.path("/data/");
        return Response.seeOther(addressBuilder.build()).build();
    }
*/
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/{parameter: resource|page}/{id}")
    public Viewable showHTML(@Context HttpServletRequest request,
            @PathParam("id") String id) {
        IndividualProperties indp = new IndividualProperties();
        IRI indIRI = IRI.create(id);
        request.setAttribute("indURI", id);

        try {
            SemanticUnit su = SemanticUnitContext.getInstanceFromRequest(UIUtil.obtainContext(request), request);
            indp.getIndividualProperties(request, indIRI, su);
        } catch (Exception exception) {
            JSPUILogger
                    .logException("Error", request, exception);
        }
        return new Viewable("/search/showIndProperties.jsp", null);
    }

    @GET
    @Path("/{parameter: resource|data}/{id}")
    @Produces("application/rdf+xml")
    public Response showRDF(@Context
            final HttpServletRequest request,
            @PathParam("id") String id) throws SQLException {
        final IRI indIRI = IRI.create(id);
        final SemanticUnit su = SemanticUnitContext.getInstanceFromRequest(UIUtil.obtainContext(request), request);

        StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(OutputStream out) throws IOException, WebApplicationException {

                PrintWriter print = new PrintWriter(out, true);
                try {
                    OWLNamedIndividual individual = su.getManager()
                            .getOWLDataFactory().getOWLNamedIndividual(indIRI);

                    RDFXMLIndividualRenderer output = new RDFXMLIndividualRenderer(su.getManager(), su.getOntology(), print);
                    output.render(individual);

                } catch (Exception exception) {
                    JSPUILogger
                            .logException("Error", request, exception);
                }

            }
        };


        return Response.ok(stream).build();
    }
}