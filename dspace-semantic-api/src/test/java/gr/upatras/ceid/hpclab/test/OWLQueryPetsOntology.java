/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

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

public class OWLQueryPetsOntology extends SemanticTest
{
    public OWLQueryPetsOntology(SupportedReasoner reasoner) throws SemanticSearchException,
            InstantiationException, IllegalAccessException, ClassNotFoundException
    {
        super(reasoner, "http://protege.cim3.net/file/pub/ontologies/people.pets/people+pets.owl");
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
    public void executeOWLQueryIsPetOfDailyMirrorReaders() throws ParserException,
            OWLOntologyCreationException, SQLException, IOException
    {
        SortedSet<OWLIndividual> result = executeQuery("ns0:is_pet_of some (ns0:reads value ns0:Daily_Mirror)");
        dumpResults(System.out, result);

        Assert.assertEquals(1, result.size());
    }

    @Test
    public void executeOWLQueryLikesAnimalsAndHasCat() throws ParserException,
            OWLOntologyCreationException, SQLException, IOException
    {
        SortedSet<OWLIndividual> result = executeQuery("ns0:likes some ns0:animal and ns0:has_pet some ns0:cat");
        dumpResults(System.out, result);

        Assert.assertEquals(2, result.size());
    }

    @Test
    public void executeOWLQueryDrivesVehicleOrReadsDailyMirror() throws ParserException,
            OWLOntologyCreationException, SQLException, IOException
    {
        SortedSet<OWLIndividual> result = executeQuery("ns0:drives some ns0:vehicle or ns0:reads value ns0:Daily_Mirror");
        dumpResults(System.out, result);

        Assert.assertEquals(1, result.size());
    }

    @Test
    public void executeOWLQueryHasPetAnimalAndReadsOnlyTabloid() throws ParserException,
            OWLOntologyCreationException, SQLException, IOException
    {
        SortedSet<OWLIndividual> result = executeQuery("ns0:has_pet some ns0:animal and ns0:reads only ns0:tabloid");
        dumpResults(System.out, result);

        Assert.assertEquals(1, result.size());
    }

    @Test
    public void executeOWLQueryPersonAndHasOneDogAsMax() throws ParserException,
            OWLOntologyCreationException, SQLException, IOException
    {
        SortedSet<OWLIndividual> result = executeQuery("ns0:person and ns0:has_pet max 1 ns0:dog");
        dumpResults(System.out, result);

        Assert.assertEquals(2, result.size());
    }
}