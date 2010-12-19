package gr.upatras.ceid.hpclab.owl;

import java.util.SortedSet;
import java.util.TreeSet;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxEditorParser;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.Node;

public class ManchesterOWLSyntaxParser
{
    public SemanticUnit semanticUnit;

    public ManchesterOWLSyntaxParser(SemanticUnit semanticUnit)
    {
        this.semanticUnit = semanticUnit;
    }

    private ManchesterOWLSyntaxEditorParser getParser(String expression)
    {
        ShortFormEntityChecker checker = new ShortFormEntityChecker(semanticUnit
                .getBidirectionalShortFormProvider());

        ManchesterOWLSyntaxEditorParser parser = new ManchesterOWLSyntaxEditorParser(semanticUnit
                .getManager().getOWLDataFactory(), expression);
        parser.setOWLEntityChecker(checker);

        semanticUnit.getBidirectionalShortFormProvider().dispose();

        return parser;
    }

    private OWLClassExpression parseQuery(String expression) throws ParserException
    {
        ManchesterOWLSyntaxEditorParser parser = getParser(expression);
        OWLClassExpression item = parser.parseClassExpression();

        return item;
    }

    private SortedSet<OWLIndividual> getInferredInstancesOfClassExpression(OWLClassExpression item)
    {
        SortedSet<OWLIndividual> instanceSet = new TreeSet<OWLIndividual>();

        // This was getIndividuals
        for (Node<OWLNamedIndividual> ind : semanticUnit.getReasoner().getInstances(item, false))
        {
            instanceSet.add((OWLIndividual) ind.getRepresentativeElement());
        }

        return instanceSet;
    }

    private SortedSet<OWLIndividual> getAssertedInstancesOfClassExpression(OWLClassExpression item)
    {
        SortedSet<OWLIndividual> instances = new TreeSet<OWLIndividual>();

        if (!item.isAnonymous())
        {
            for (OWLOntology ont : semanticUnit.getImportsClosure())
            {
                for (OWLClassAssertionAxiom ax : ont.getClassAssertionAxioms(item.asOWLClass()))
                {
                    instances.add(ax.getIndividual());
                }
            }
        }

        return instances;
    }

    public SortedSet<OWLIndividual> executeQuery(String expression) throws ParserException
    {
        OWLClassExpression classExpression = parseQuery(expression);

        SortedSet<OWLIndividual> instances = getInferredInstancesOfClassExpression(classExpression);
        instances.addAll(getAssertedInstancesOfClassExpression(classExpression));

        return instances;
    }
}