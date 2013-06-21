/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package gr.upatras.ceid.hpclab.servlet;

import gr.upatras.ceid.hpclab.owl.BidirectionalShortFormProviderFactory;
import gr.upatras.ceid.hpclab.owl.SemanticUnit;
import gr.upatras.ceid.hpclab.reasoner.SupportedReasoner;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class Find extends HttpServlet
{
    public void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        String url = (String) request.getSession().getAttribute("URL");
        SemanticUnit semanticUnit = null;

        try
        {
            semanticUnit = SemanticUnit.getInstance(url, SupportedReasoner.FACTPLUSPLUS,false, request.getSession().getId());
        }
        catch (Exception exception)
        {
            throw new ServletException(exception);
        }

        Set<String> myWords = BidirectionalShortFormProviderFactory.getEntityNames(semanticUnit);

        if (myWords == null)
        {
            myWords = new HashSet<String>();
        }

        if (request.getCharacterEncoding() == null)
        {
            request.setCharacterEncoding("UTF-8");
        }

        String input = new String(request.getParameter("input").getBytes("ISO-8859-1"), "UTF-8");

        if (input.length() > 0)
        {
            response.setContentType("text/xml;charset=UTF-8");

            Set<String> results = getResults(input, myWords);
            renderXMLResults(results, response.getWriter());
        }
    }

    private Set<String> getResults(String word, Set<String> myWords)
    {
        Set<String> results = new HashSet<String>();

        for (String rendering : myWords)
        {
            if (rendering.toLowerCase().startsWith(word.toLowerCase()))
            {
                results.add(rendering);
            }
        }

        return results;
    }

    private void renderXMLResults(Set<String> results, PrintWriter out)
    {
        out.println("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
        out.println("<results>");

        for (String result : results)
        {
            out.println("<rs id=\"\" info=\"\">" + result + "</rs>");
        }

        out.println("</results>");
    }
}