package gr.upatras.ceid.hpclab.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { 
    PelletDSpaceTest.class,
    PelletPetsTest.class,
    PelletPetsHierarchyTest.class
})
public class AllTests
{
}