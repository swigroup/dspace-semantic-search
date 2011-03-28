package gr.upatras.ceid.hpclab.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { 
    OWLQueryFactPlusPlusReasonerTest.class, 
    OWLQueryPelletReasonerTest.class,
    OWLHierarchyTest.class
})
public class AllTests
{
}