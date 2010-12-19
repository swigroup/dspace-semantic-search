package gr.upatras.ceid.hpclab.test;

import gr.upatras.ceid.hpclab.owl.OWLDSpaceQueryManager;
import gr.upatras.ceid.hpclab.owl.SemanticSearchException;
import gr.upatras.ceid.hpclab.reasoner.SupportedReasoner;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class OWLHierarchyTest extends SemanticTest
{
    public OWLHierarchyTest() throws SemanticSearchException, InstantiationException,
            IllegalAccessException, ClassNotFoundException
    {
        super(SupportedReasoner.PELLET);
        
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
        
        Assert.assertEquals(60, classList.size());
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
       
        Assert.assertEquals(14, objectPropertiesList.size());
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
       
        Assert.assertEquals(1, dataPropertiesList.size());
    }
}
package gr.upatras.ceid.hpclab.test;

import gr.upatras.ceid.hpclab.owl.OWLDSpaceQueryManager;
import gr.upatras.ceid.hpclab.owl.SemanticSearchException;
import gr.upatras.ceid.hpclab.reasoner.SupportedReasoner;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class OWLHierarchyTest extends SemanticTest
{
    public OWLHierarchyTest() throws SemanticSearchException, InstantiationException,
            IllegalAccessException, ClassNotFoundException
    {
        super(SupportedReasoner.PELLET);
        
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
        
        Assert.assertEquals(60, classList.size());
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
       
        Assert.assertEquals(14, objectPropertiesList.size());
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
       
        Assert.assertEquals(1, dataPropertiesList.size());
    }
}
package gr.upatras.ceid.hpclab.test;

import gr.upatras.ceid.hpclab.owl.OWLDSpaceQueryManager;
import gr.upatras.ceid.hpclab.owl.SemanticSearchException;
import gr.upatras.ceid.hpclab.reasoner.SupportedReasoner;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class OWLHierarchyTest extends SemanticTest
{
    public OWLHierarchyTest() throws SemanticSearchException, InstantiationException,
            IllegalAccessException, ClassNotFoundException
    {
        super(SupportedReasoner.PELLET);
        
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
        
        Assert.assertEquals(60, classList.size());
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
       
        Assert.assertEquals(14, objectPropertiesList.size());
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
       
        Assert.assertEquals(1, dataPropertiesList.size());
    }
}