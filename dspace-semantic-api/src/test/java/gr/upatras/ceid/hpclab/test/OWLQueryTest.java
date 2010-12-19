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
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class OWLQueryTest extends SemanticTest
{
    public OWLQueryTest(SupportedReasoner reasoner) throws SemanticSearchException,
            InstantiationException, IllegalAccessException, ClassNotFoundException
    {
        super(reasoner);
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

    public void executeOWLQueryIsPetOfDailyMirrorReaders() throws ParserException,
            OWLOntologyCreationException, SQLException, IOException
    {
        SortedSet<OWLIndividual> result = executeQuery("ns0:is_pet_of some (ns0:reads value ns0:Daily_Mirror)");
        dumpResults(System.out, result);

        Assert.assertEquals(1, result.size());
    }

    public void executeOWLQueryLikesAnimalsAndHasCat() throws ParserException,
            OWLOntologyCreationException, SQLException, IOException
    {
        SortedSet<OWLIndividual> result = executeQuery("ns0:likes some ns0:animal and ns0:has_pet some ns0:cat");
        dumpResults(System.out, result);

        Assert.assertEquals(2, result.size());
    }

    public void executeOWLQueryDrivesVehicleOrReadsDailyMirror() throws ParserException,
            OWLOntologyCreationException, SQLException, IOException
    {
        SortedSet<OWLIndividual> result = executeQuery("ns0:drives some ns0:vehicle or ns0:reads value ns0:Daily_Mirror");
        dumpResults(System.out, result);

        Assert.assertEquals(1, result.size());
    }

    public void executeOWLQueryHasPetAnimalAndReadsOnlyTabloid() throws ParserException,
            OWLOntologyCreationException, SQLException, IOException
    {
        SortedSet<OWLIndividual> result = executeQuery("ns0:has_pet some ns0:animal and ns0:reads only ns0:tabloid");
        dumpResults(System.out, result);

        Assert.assertEquals(1, result.size());
    }

    public void executeOWLQueryPersonAndHasOneDogAsMax() throws ParserException,
            OWLOntologyCreationException, SQLException, IOException
    {
        SortedSet<OWLIndividual> result = executeQuery("ns0:person and ns0:has_pet max 1 ns0:dog");
        dumpResults(System.out, result);

        Assert.assertEquals(2, result.size());
    }

    @Test
    public void executeDSpaceOntQuery1() throws IOException, SQLException, ParserException
    {
        SortedSet<OWLIndividual> result = executeQuery("dspace-ont:Item");
        dumpResults(System.out, result);
    }

    @Test
    public void executeDSpaceOntQuery2() throws IOException, SQLException, ParserException
    {
        SortedSet<OWLIndividual> result = executeQuery("foaf:name some rdfs:Literal");
        dumpResults(System.out, result);
    }

    @Test
    public void executeDSpaceOntQuery3() throws IOException, SQLException, ParserException
    {
        SortedSet<OWLIndividual> result = executeQuery("foaf:name some xsd:string");
        dumpResults(System.out, result);
    }

    @Test
    public void executeDSpaceOntQuery4() throws IOException, SQLException, ParserException
    {
        SortedSet<OWLIndividual> result = executeQuery("inverse (dspace-ont:author) min 1 owl:Thing");
        dumpResults(System.out, result);
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
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class OWLQueryTest extends SemanticTest
{
    public OWLQueryTest(SupportedReasoner reasoner) throws SemanticSearchException,
            InstantiationException, IllegalAccessException, ClassNotFoundException
    {
        super(reasoner);
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

    public void executeOWLQueryIsPetOfDailyMirrorReaders() throws ParserException,
            OWLOntologyCreationException, SQLException, IOException
    {
        SortedSet<OWLIndividual> result = executeQuery("ns0:is_pet_of some (ns0:reads value ns0:Daily_Mirror)");
        dumpResults(System.out, result);

        Assert.assertEquals(1, result.size());
    }

    public void executeOWLQueryLikesAnimalsAndHasCat() throws ParserException,
            OWLOntologyCreationException, SQLException, IOException
    {
        SortedSet<OWLIndividual> result = executeQuery("ns0:likes some ns0:animal and ns0:has_pet some ns0:cat");
        dumpResults(System.out, result);

        Assert.assertEquals(2, result.size());
    }

    public void executeOWLQueryDrivesVehicleOrReadsDailyMirror() throws ParserException,
            OWLOntologyCreationException, SQLException, IOException
    {
        SortedSet<OWLIndividual> result = executeQuery("ns0:drives some ns0:vehicle or ns0:reads value ns0:Daily_Mirror");
        dumpResults(System.out, result);

        Assert.assertEquals(1, result.size());
    }

    public void executeOWLQueryHasPetAnimalAndReadsOnlyTabloid() throws ParserException,
            OWLOntologyCreationException, SQLException, IOException
    {
        SortedSet<OWLIndividual> result = executeQuery("ns0:has_pet some ns0:animal and ns0:reads only ns0:tabloid");
        dumpResults(System.out, result);

        Assert.assertEquals(1, result.size());
    }

    public void executeOWLQueryPersonAndHasOneDogAsMax() throws ParserException,
            OWLOntologyCreationException, SQLException, IOException
    {
        SortedSet<OWLIndividual> result = executeQuery("ns0:person and ns0:has_pet max 1 ns0:dog");
        dumpResults(System.out, result);

        Assert.assertEquals(2, result.size());
    }

    @Test
    public void executeDSpaceOntQuery1() throws IOException, SQLException, ParserException
    {
        SortedSet<OWLIndividual> result = executeQuery("dspace-ont:Item");
        dumpResults(System.out, result);
    }

    @Test
    public void executeDSpaceOntQuery2() throws IOException, SQLException, ParserException
    {
        SortedSet<OWLIndividual> result = executeQuery("foaf:name some rdfs:Literal");
        dumpResults(System.out, result);
    }

    @Test
    public void executeDSpaceOntQuery3() throws IOException, SQLException, ParserException
    {
        SortedSet<OWLIndividual> result = executeQuery("foaf:name some xsd:string");
        dumpResults(System.out, result);
    }

    @Test
    public void executeDSpaceOntQuery4() throws IOException, SQLException, ParserException
    {
        SortedSet<OWLIndividual> result = executeQuery("inverse (dspace-ont:author) min 1 owl:Thing");
        dumpResults(System.out, result);
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
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class OWLQueryTest extends SemanticTest
{
    public OWLQueryTest(SupportedReasoner reasoner) throws SemanticSearchException,
            InstantiationException, IllegalAccessException, ClassNotFoundException
    {
        super(reasoner);
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

    public void executeOWLQueryIsPetOfDailyMirrorReaders() throws ParserException,
            OWLOntologyCreationException, SQLException, IOException
    {
        SortedSet<OWLIndividual> result = executeQuery("ns0:is_pet_of some (ns0:reads value ns0:Daily_Mirror)");
        dumpResults(System.out, result);

        Assert.assertEquals(1, result.size());
    }

    public void executeOWLQueryLikesAnimalsAndHasCat() throws ParserException,
            OWLOntologyCreationException, SQLException, IOException
    {
        SortedSet<OWLIndividual> result = executeQuery("ns0:likes some ns0:animal and ns0:has_pet some ns0:cat");
        dumpResults(System.out, result);

        Assert.assertEquals(2, result.size());
    }

    public void executeOWLQueryDrivesVehicleOrReadsDailyMirror() throws ParserException,
            OWLOntologyCreationException, SQLException, IOException
    {
        SortedSet<OWLIndividual> result = executeQuery("ns0:drives some ns0:vehicle or ns0:reads value ns0:Daily_Mirror");
        dumpResults(System.out, result);

        Assert.assertEquals(1, result.size());
    }

    public void executeOWLQueryHasPetAnimalAndReadsOnlyTabloid() throws ParserException,
            OWLOntologyCreationException, SQLException, IOException
    {
        SortedSet<OWLIndividual> result = executeQuery("ns0:has_pet some ns0:animal and ns0:reads only ns0:tabloid");
        dumpResults(System.out, result);

        Assert.assertEquals(1, result.size());
    }

    public void executeOWLQueryPersonAndHasOneDogAsMax() throws ParserException,
            OWLOntologyCreationException, SQLException, IOException
    {
        SortedSet<OWLIndividual> result = executeQuery("ns0:person and ns0:has_pet max 1 ns0:dog");
        dumpResults(System.out, result);

        Assert.assertEquals(2, result.size());
    }

    @Test
    public void executeDSpaceOntQuery1() throws IOException, SQLException, ParserException
    {
        SortedSet<OWLIndividual> result = executeQuery("dspace-ont:Item");
        dumpResults(System.out, result);
    }

    @Test
    public void executeDSpaceOntQuery2() throws IOException, SQLException, ParserException
    {
        SortedSet<OWLIndividual> result = executeQuery("foaf:name some rdfs:Literal");
        dumpResults(System.out, result);
    }

    @Test
    public void executeDSpaceOntQuery3() throws IOException, SQLException, ParserException
    {
        SortedSet<OWLIndividual> result = executeQuery("foaf:name some xsd:string");
        dumpResults(System.out, result);
    }

    @Test
    public void executeDSpaceOntQuery4() throws IOException, SQLException, ParserException
    {
        SortedSet<OWLIndividual> result = executeQuery("inverse (dspace-ont:author) min 1 owl:Thing");
        dumpResults(System.out, result);
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