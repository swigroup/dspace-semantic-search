package gr.upatras.ceid.hpclab.owl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.Map.Entry;

import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;

public class OWLDSpaceQueryManager
{
    private SemanticUnit semanticUnit;

    public OWLDSpaceQueryManager(SemanticUnit semanticUnit)
    {
        this.semanticUnit = semanticUnit;
    }

    private void checkIfInstancesCanBeResolvedToDSpaceItems(SortedSet<OWLIndividual> instances)
            throws SQLException
    {
        // TODO: identify between items, coms, cols and other types of instances

        for (OWLIndividual instance : instances)
        {
            String handle = instance.toString();

            if (handle == null)
            {
                throw new SQLException("Unresolvable handle: " + handle);
            }
        }
    }

    public Map<String, String> buildNamespaceMapFromImportsClosure()
    {
        Map<String, String> namespaceMap = new HashMap<String, String>();

        PrefixOWLOntologyFormat namespaceFormat = (PrefixOWLOntologyFormat) semanticUnit
                 .getManager().getOntologyFormat(semanticUnit.getOntology());

        Map<String, String> ontologyNamespacesMap = namespaceFormat.getPrefixName2PrefixMap();

        for (String prefix : ontologyNamespacesMap.keySet())
        {
             if (!prefix.equals(":"))
             {
                 namespaceMap.put(prefix.split(":")[0], ontologyNamespacesMap.get(prefix));
             }
        }

        return namespaceMap;
    }

    public String getPrefixFromNamespace(Map<String, String> namespaces, String namespace)
    {
        for (Entry<String, String> entry : namespaces.entrySet())
        {
            if (entry.getValue().equals(namespace))
            {
                return entry.getKey();
            }
        }

        return null;
    }

    private String getFragment(OWLEntity owlEntity)
    {
        IRI iri = owlEntity.getIRI();
        String name = iri.getFragment();
        if (name == null)
        {
            name = iri.toString();
            if (name.endsWith("/"))
            {
                name = name.substring(0, name.length() - 1);
            }
            int index = name.lastIndexOf("/");
            if (index >= 0)
            {
                name = name.substring(index + 1, name.length());
            }
        }
        return name;
    }

    public List<String> getFullClassList()
    {
        Map<String, String> namespaces = buildNamespaceMapFromImportsClosure();

        List<String> classList = new ArrayList<String>();

        for (OWLClass owlClass : semanticUnit.getOntology().getClassesInSignature(true))
        {
            classList.add(getPrefixFromNamespace(namespaces, owlClass.getIRI().getStart()) + ":"
                    + getFragment(owlClass));
        }

        return classList;
    }

    public SortedSet<OWLIndividual> executeQuery(String expression) throws SQLException,
            ParserException
    {
        ManchesterOWLSyntaxParser parser = new ManchesterOWLSyntaxParser(semanticUnit);
        SortedSet<OWLIndividual> instances = parser.executeQuery(expression);

        checkIfInstancesCanBeResolvedToDSpaceItems(instances);

        return instances;
    }

    private Set<OWLClass> getOWLAssertedMembershipRelationsClasses(OWLIndividual individual)
    {
        Set<OWLClass> types = new HashSet<OWLClass>();

        for (OWLClassExpression d : individual.getTypes(semanticUnit.getImportsClosure()))
        {
            if (!d.isAnonymous())
            {
                types.add(d.asOWLClass());
            }
        }

        return types;
    }

    public Set<OWLClass> getOWLClasses(OWLIndividual individual)
    {
        Set<OWLClass> types = new HashSet<OWLClass>();

        for (Node<OWLClass> node : semanticUnit.getReasoner().getTypes(
                (OWLNamedIndividual) individual, false))
        {
            types.addAll(node.getEntities());
        }

        Set<OWLClass> assertedClasses = getOWLAssertedMembershipRelationsClasses(individual);

        types.addAll(assertedClasses);

        return types;
    }

    public List<String> getFullObjectPropertiesList()
    {
        // TODO: Make some sort of singleton access to namespace map
        Map<String, String> namespaces = buildNamespaceMapFromImportsClosure();

        List<String> propertiesList = new ArrayList<String>();

        for (OWLObjectProperty owlObjectProperty : semanticUnit.getOntology()
                .getObjectPropertiesInSignature(true))
        {
            propertiesList.add(getPrefixFromNamespace(namespaces, owlObjectProperty.getIRI()
                    .getStart())
                    + ":" + getFragment(owlObjectProperty));
        }

        return propertiesList;
    }

    public List<String> getFullDataPropertiesList()
    {
        // TODO Auto-generated method stub
        Map<String, String> namespaces = buildNamespaceMapFromImportsClosure();

        List<String> propertiesList = new ArrayList<String>();

        for (OWLDataProperty owlDataProperty : semanticUnit.getOntology()
                .getDataPropertiesInSignature(true))
        {
            propertiesList.add(getPrefixFromNamespace(namespaces, owlDataProperty.getIRI()
                    .getStart())
                    + ":" + getFragment(owlDataProperty));
        }

        return propertiesList;
    }
}
package gr.upatras.ceid.hpclab.owl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.Map.Entry;

import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;

public class OWLDSpaceQueryManager
{
    private SemanticUnit semanticUnit;

    public OWLDSpaceQueryManager(SemanticUnit semanticUnit)
    {
        this.semanticUnit = semanticUnit;
    }

    private void checkIfInstancesCanBeResolvedToDSpaceItems(SortedSet<OWLIndividual> instances)
            throws SQLException
    {
        // TODO: identify between items, coms, cols and other types of instances

        for (OWLIndividual instance : instances)
        {
            String handle = instance.toString();

            if (handle == null)
            {
                throw new SQLException("Unresolvable handle: " + handle);
            }
        }
    }

    public Map<String, String> buildNamespaceMapFromImportsClosure()
    {
        Map<String, String> namespaceMap = new HashMap<String, String>();

        PrefixOWLOntologyFormat namespaceFormat = (PrefixOWLOntologyFormat) semanticUnit
                 .getManager().getOntologyFormat(semanticUnit.getOntology());

        Map<String, String> ontologyNamespacesMap = namespaceFormat.getPrefixName2PrefixMap();

        for (String prefix : ontologyNamespacesMap.keySet())
        {
             if (!prefix.equals(":"))
             {
                 namespaceMap.put(prefix.split(":")[0], ontologyNamespacesMap.get(prefix));
             }
        }

        return namespaceMap;
    }

    public String getPrefixFromNamespace(Map<String, String> namespaces, String namespace)
    {
        for (Entry<String, String> entry : namespaces.entrySet())
        {
            if (entry.getValue().equals(namespace))
            {
                return entry.getKey();
            }
        }

        return null;
    }

    private String getFragment(OWLEntity owlEntity)
    {
        IRI iri = owlEntity.getIRI();
        String name = iri.getFragment();
        if (name == null)
        {
            name = iri.toString();
            if (name.endsWith("/"))
            {
                name = name.substring(0, name.length() - 1);
            }
            int index = name.lastIndexOf("/");
            if (index >= 0)
            {
                name = name.substring(index + 1, name.length());
            }
        }
        return name;
    }

    public List<String> getFullClassList()
    {
        Map<String, String> namespaces = buildNamespaceMapFromImportsClosure();

        List<String> classList = new ArrayList<String>();

        for (OWLClass owlClass : semanticUnit.getOntology().getClassesInSignature(true))
        {
            classList.add(getPrefixFromNamespace(namespaces, owlClass.getIRI().getStart()) + ":"
                    + getFragment(owlClass));
        }

        return classList;
    }

    public SortedSet<OWLIndividual> executeQuery(String expression) throws SQLException,
            ParserException
    {
        ManchesterOWLSyntaxParser parser = new ManchesterOWLSyntaxParser(semanticUnit);
        SortedSet<OWLIndividual> instances = parser.executeQuery(expression);

        checkIfInstancesCanBeResolvedToDSpaceItems(instances);

        return instances;
    }

    private Set<OWLClass> getOWLAssertedMembershipRelationsClasses(OWLIndividual individual)
    {
        Set<OWLClass> types = new HashSet<OWLClass>();

        for (OWLClassExpression d : individual.getTypes(semanticUnit.getImportsClosure()))
        {
            if (!d.isAnonymous())
            {
                types.add(d.asOWLClass());
            }
        }

        return types;
    }

    public Set<OWLClass> getOWLClasses(OWLIndividual individual)
    {
        Set<OWLClass> types = new HashSet<OWLClass>();

        for (Node<OWLClass> node : semanticUnit.getReasoner().getTypes(
                (OWLNamedIndividual) individual, false))
        {
            types.addAll(node.getEntities());
        }

        Set<OWLClass> assertedClasses = getOWLAssertedMembershipRelationsClasses(individual);

        types.addAll(assertedClasses);

        return types;
    }

    public List<String> getFullObjectPropertiesList()
    {
        // TODO: Make some sort of singleton access to namespace map
        Map<String, String> namespaces = buildNamespaceMapFromImportsClosure();

        List<String> propertiesList = new ArrayList<String>();

        for (OWLObjectProperty owlObjectProperty : semanticUnit.getOntology()
                .getObjectPropertiesInSignature(true))
        {
            propertiesList.add(getPrefixFromNamespace(namespaces, owlObjectProperty.getIRI()
                    .getStart())
                    + ":" + getFragment(owlObjectProperty));
        }

        return propertiesList;
    }

    public List<String> getFullDataPropertiesList()
    {
        // TODO Auto-generated method stub
        Map<String, String> namespaces = buildNamespaceMapFromImportsClosure();

        List<String> propertiesList = new ArrayList<String>();

        for (OWLDataProperty owlDataProperty : semanticUnit.getOntology()
                .getDataPropertiesInSignature(true))
        {
            propertiesList.add(getPrefixFromNamespace(namespaces, owlDataProperty.getIRI()
                    .getStart())
                    + ":" + getFragment(owlDataProperty));
        }

        return propertiesList;
    }
}
package gr.upatras.ceid.hpclab.owl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.Map.Entry;

import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;

public class OWLDSpaceQueryManager
{
    private SemanticUnit semanticUnit;

    public OWLDSpaceQueryManager(SemanticUnit semanticUnit)
    {
        this.semanticUnit = semanticUnit;
    }

    private void checkIfInstancesCanBeResolvedToDSpaceItems(SortedSet<OWLIndividual> instances)
            throws SQLException
    {
        // TODO: identify between items, coms, cols and other types of instances

        for (OWLIndividual instance : instances)
        {
            String handle = instance.toString();

            if (handle == null)
            {
                throw new SQLException("Unresolvable handle: " + handle);
            }
        }
    }

    public Map<String, String> buildNamespaceMapFromImportsClosure()
    {
        Map<String, String> namespaceMap = new HashMap<String, String>();

        PrefixOWLOntologyFormat namespaceFormat = (PrefixOWLOntologyFormat) semanticUnit
                 .getManager().getOntologyFormat(semanticUnit.getOntology());

        Map<String, String> ontologyNamespacesMap = namespaceFormat.getPrefixName2PrefixMap();

        for (String prefix : ontologyNamespacesMap.keySet())
        {
             if (!prefix.equals(":"))
             {
                 namespaceMap.put(prefix.split(":")[0], ontologyNamespacesMap.get(prefix));
             }
        }

        return namespaceMap;
    }

    public String getPrefixFromNamespace(Map<String, String> namespaces, String namespace)
    {
        for (Entry<String, String> entry : namespaces.entrySet())
        {
            if (entry.getValue().equals(namespace))
            {
                return entry.getKey();
            }
        }

        return null;
    }

    private String getFragment(OWLEntity owlEntity)
    {
        IRI iri = owlEntity.getIRI();
        String name = iri.getFragment();
        if (name == null)
        {
            name = iri.toString();
            if (name.endsWith("/"))
            {
                name = name.substring(0, name.length() - 1);
            }
            int index = name.lastIndexOf("/");
            if (index >= 0)
            {
                name = name.substring(index + 1, name.length());
            }
        }
        return name;
    }

    public List<String> getFullClassList()
    {
        Map<String, String> namespaces = buildNamespaceMapFromImportsClosure();

        List<String> classList = new ArrayList<String>();

        for (OWLClass owlClass : semanticUnit.getOntology().getClassesInSignature(true))
        {
            classList.add(getPrefixFromNamespace(namespaces, owlClass.getIRI().getStart()) + ":"
                    + getFragment(owlClass));
        }

        return classList;
    }

    public SortedSet<OWLIndividual> executeQuery(String expression) throws SQLException,
            ParserException
    {
        ManchesterOWLSyntaxParser parser = new ManchesterOWLSyntaxParser(semanticUnit);
        SortedSet<OWLIndividual> instances = parser.executeQuery(expression);

        checkIfInstancesCanBeResolvedToDSpaceItems(instances);

        return instances;
    }

    private Set<OWLClass> getOWLAssertedMembershipRelationsClasses(OWLIndividual individual)
    {
        Set<OWLClass> types = new HashSet<OWLClass>();

        for (OWLClassExpression d : individual.getTypes(semanticUnit.getImportsClosure()))
        {
            if (!d.isAnonymous())
            {
                types.add(d.asOWLClass());
            }
        }

        return types;
    }

    public Set<OWLClass> getOWLClasses(OWLIndividual individual)
    {
        Set<OWLClass> types = new HashSet<OWLClass>();

        for (Node<OWLClass> node : semanticUnit.getReasoner().getTypes(
                (OWLNamedIndividual) individual, false))
        {
            types.addAll(node.getEntities());
        }

        Set<OWLClass> assertedClasses = getOWLAssertedMembershipRelationsClasses(individual);

        types.addAll(assertedClasses);

        return types;
    }

    public List<String> getFullObjectPropertiesList()
    {
        // TODO: Make some sort of singleton access to namespace map
        Map<String, String> namespaces = buildNamespaceMapFromImportsClosure();

        List<String> propertiesList = new ArrayList<String>();

        for (OWLObjectProperty owlObjectProperty : semanticUnit.getOntology()
                .getObjectPropertiesInSignature(true))
        {
            propertiesList.add(getPrefixFromNamespace(namespaces, owlObjectProperty.getIRI()
                    .getStart())
                    + ":" + getFragment(owlObjectProperty));
        }

        return propertiesList;
    }

    public List<String> getFullDataPropertiesList()
    {
        // TODO Auto-generated method stub
        Map<String, String> namespaces = buildNamespaceMapFromImportsClosure();

        List<String> propertiesList = new ArrayList<String>();

        for (OWLDataProperty owlDataProperty : semanticUnit.getOntology()
                .getDataPropertiesInSignature(true))
        {
            propertiesList.add(getPrefixFromNamespace(namespaces, owlDataProperty.getIRI()
                    .getStart())
                    + ":" + getFragment(owlDataProperty));
        }

        return propertiesList;
    }
}
