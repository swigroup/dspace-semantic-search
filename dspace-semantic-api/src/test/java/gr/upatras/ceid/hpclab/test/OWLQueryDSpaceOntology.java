package gr.upatras.ceid.hpclab.test;

import gr.upatras.ceid.hpclab.owl.OWLDSpaceQueryManager;
import gr.upatras.ceid.hpclab.owl.SemanticSearchException;
import gr.upatras.ceid.hpclab.reasoner.SupportedReasoner;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.SortedSet;

import org.junit.Assert;
import org.junit.Test;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.OWLIndividual;

public class OWLQueryDSpaceOntology extends SemanticTest
{
    public OWLQueryDSpaceOntology(SupportedReasoner reasoner) throws SemanticSearchException,
            InstantiationException, IllegalAccessException, ClassNotFoundException
    {
        super(reasoner, "http://apollo.hpclab.ceid.upatras.gr:8000/jspui16/dspace-ont");
    }

    private SortedSet<OWLIndividual> executeQuery(String query) throws SQLException,
            ParserException
    {
        OWLDSpaceQueryManager queryManager = new OWLDSpaceQueryManager(semanticUnit);

        return queryManager.executeQuery(query);
    }

    private void dumpResults(OutputStream out, SortedSet<OWLIndividual> result) throws IOException
    {
        out.write(("Number of results: " + result.size() + "\n\n").getBytes());

        for (OWLIndividual individual : result)
        {
            out.write((individual.toString() + "\n").getBytes());
        }
    }

    @Test
    public void executeDSpaceOntQuery1() throws IOException, SQLException, ParserException
    {
        SortedSet<OWLIndividual> result = executeQuery("dspace-ont:Item");
        dumpResults(System.out, result);

        Assert.assertTrue(result.size()>0);
    }

    @Test
    public void executeDSpaceOntQuery3() throws IOException, SQLException, ParserException
    {
        SortedSet<OWLIndividual> result = executeQuery("foaf:name some xsd:string");
        dumpResults(System.out, result);

        Assert.assertTrue(result.size()>0);
    }

    @Test
    public void executeDSpaceOntQuery4() throws IOException, SQLException, ParserException
    {
        SortedSet<OWLIndividual> result = executeQuery("inverse (dspace-ont:author) min 1 owl:Thing");
        dumpResults(System.out, result);

        Assert.assertTrue(result.size()>0);
    }

    @Test
    public void executeDSpaceOntQuery5() throws IOException, SQLException, ParserException
    {
        SortedSet<OWLIndividual> result = executeQuery("dcterms:type min 3");
        dumpResults(System.out, result);
    }

    @Test
    public void executeDSpaceOntQuery6() throws IOException, SQLException, ParserException
    {
        SortedSet<OWLIndividual> result = executeQuery("dcterms:type some lom:LearningResourceType");
        dumpResults(System.out, result);
    }
}