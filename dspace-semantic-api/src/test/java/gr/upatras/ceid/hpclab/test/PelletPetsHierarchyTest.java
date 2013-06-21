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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class PelletPetsHierarchyTest extends SemanticTest
{
    public PelletPetsHierarchyTest() throws SemanticSearchException, InstantiationException,
            IllegalAccessException, ClassNotFoundException
    {
        super(SupportedReasoner.PELLET,
                "http://protege.cim3.net/file/pub/ontologies/people.pets/people+pets.owl");
    }

    @Test
    public void retrieveClassList()
    {
        OWLDSpaceQueryManager queryManager = new OWLDSpaceQueryManager(semanticUnit);
        List<String> classList = queryManager.getFullClassList();

        for (String value : classList)
        {
            System.out.println(value);
        }

        Assert.assertNotNull(classList);
        Assert.assertTrue(classList.size() > 0);
    }

    @Test
    public void retrieveObjectPropertiesList()
    {
        OWLDSpaceQueryManager queryManager = new OWLDSpaceQueryManager(semanticUnit);
        List<String> objectPropertiesList = queryManager.getFullObjectPropertiesList();

        for (String value : objectPropertiesList)
        {
            System.out.println(value);
        }

        Assert.assertNotNull(objectPropertiesList);
        Assert.assertTrue(objectPropertiesList.size() > 0);
    }

    @Test
    public void retrieveDataPropertiesList()
    {
        OWLDSpaceQueryManager queryManager = new OWLDSpaceQueryManager(semanticUnit);
        List<String> dataPropertiesList = queryManager.getFullDataPropertiesList();

        for (String value : dataPropertiesList)
        {
            System.out.println(value);
        }

        Assert.assertNotNull(dataPropertiesList);
        Assert.assertTrue(dataPropertiesList.size() > 0);
    }
}