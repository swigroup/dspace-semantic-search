<%--

    The contents of this file are subject to the license and copyright
    detailed in the LICENSE and NOTICE files at the root of the source
    tree and available online at

    http://www.dspace.org/license/

--%>

<%-- Show Individual Properties JSP --%>

<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@ page import="javax.servlet.jsp.jstl.fmt.LocaleSupport" %>
<%@ page import="java.net.URLEncoder"%>
<%@ page import="java.net.URI"%>
<%@ page import="java.util.Set"%>
<%@ page import="java.util.HashSet"%>
<%@ page import="java.util.SortedSet"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="java.net.URI"%>
<%@ page import="java.net.URISyntaxException"%>
<%@ page import="org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom"%>
<%@ page import="org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom"%>
<%@ page import="org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom"%>
<%@ page import="org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom"%>
<%@ page import="org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom"%>
<%@ page import="org.semanticweb.owlapi.model.OWLClass"%>
<%@page import="org.semanticweb.owlapi.model.OWLOntology"%>
<%@page import="org.semanticweb.owlapi.util.ShortFormProvider"%>
<%@page import="org.semanticweb.owlapi.model.OWLEntity"%>
<%@page import="org.semanticweb.owlapi.model.OWLIndividual"%>
<%@page import="org.semanticweb.owlapi.model.OWLObjectPropertyExpression"%>
<%@page import="org.semanticweb.owlapi.model.OWLNamedIndividual"%>
<%@page import="gr.upatras.ceid.hpclab.owl.SemanticUnit"%>
<%@page import="org.semanticweb.owlapi.model.OWLLiteral"%>
<%@page import="org.semanticweb.owlapi.model.IRI"%>
<%@page import="org.semanticweb.owlapi.model.OWLAnnotation"%>
<%@page import="gr.upatras.ceid.hpclab.reasoner.SupportedReasoner"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.dspace.org/dspace-tags.tld" prefix="dspace"%>


<%
    Set<OWLObjectPropertyAssertionAxiom> object_assertions = (Set<OWLObjectPropertyAssertionAxiom>) request
            .getAttribute("object_assertions");
    Set<OWLNegativeObjectPropertyAssertionAxiom> negative_object_assertions = (Set<OWLNegativeObjectPropertyAssertionAxiom>) request
            .getAttribute("negative_object_assertions");
    Set<OWLDataPropertyAssertionAxiom> data_assertions = (Set<OWLDataPropertyAssertionAxiom>) request
            .getAttribute("data_assertions");
    Set<OWLNegativeDataPropertyAssertionAxiom> negative_data_assertions = (Set<OWLNegativeDataPropertyAssertionAxiom>) request
            .getAttribute("negative_data_assertions");
    Set<OWLAnnotationAssertionAxiom> annotations = (Set<OWLAnnotationAssertionAxiom>) request
            .getAttribute("annotations");
    Set<OWLClass> types = (Set<OWLClass>) request.getAttribute("class_types");

    String url = (String) request.getSession().getAttribute("URL");
    String reasoner = (String) request.getSession().getAttribute("reasoner");

    SemanticUnit semanticUnit = SemanticUnit.getInstance(url, SupportedReasoner
            .valueOf(reasoner), false, request.getSession().getId());

    Set<OWLOntology> importsClosure = semanticUnit.getImportsClosure();
    ShortFormProvider shortFormProvider = semanticUnit.getShortFormProvider();
                
    //Check if the individual has particular inverse properties in order to 
    //trigger dbpedia URL injection.         
             
    boolean hasInverses = false;
    String myfield="";
    String myname="" ;
    
    if (object_assertions!=null && !object_assertions.isEmpty())
    {   
      OWLIndividual individual = (OWLIndividual) request.getAttribute("individual");           
      
      for (OWLObjectPropertyAssertionAxiom a : object_assertions)
      {
        OWLIndividual owlin = a.getObject();
        myfield = shortFormProvider.getShortForm((OWLEntity) a.getProperty());
        if (owlin == individual)
        {   
          if (myfield.equals("dcterms:contributor")||myfield.equals("dspace-ont:author")||myfield.equals("dcterms:type")||myfield.equals("dspace-ont:sponsorship")) {
            hasInverses = true;
          }
        }
      }
    
      if (hasInverses) {
        myname = individual.getSignature().iterator().next().getIRI().getFragment();
        if (myname == null)
        {
          myname = individual.toStringID();
          if (myname.endsWith("/"))
          {
            myname = myname.substring(0, myname.length() - 1);
          }
          int index = myname.lastIndexOf("/");
          if (index >= 0)
          {
            myname = myname.substring(index + 1, myname.length());
          }
        }
      } 
    }
%>

<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/semantic.css" />
<script type="text/javascript" src="<%=request.getContextPath()%>/ui.js"></script>

<dspace:layout locbar="nolink" titlekey="jsp.search.advanced.title">


   <div class="panel panel-info">
      <div class="panel-heading">
          <fmt:message key="jsp.search.showindproperties.individual" />
          <%=request.getAttribute("indURI")%>
          <a href="<%=request.getContextPath()%>/semantic-search/data/<%=URLEncoder.encode(request.getAttribute("indURI").toString(),"UTF-8")%>">
      
          <div class="btn-group pull-right"> 
            <a href="<%=request.getContextPath()%>/semantic-search/data/<%=URLEncoder.encode(request.getAttribute("indURI").toString(),"UTF-8")%>" class="btn btn-default btn-ss">
            <img src="<%= request.getContextPath() %>/image/rdf.png" alt="RDF" height="27px" class="btn btn-default btn-ss">
            </a>
            <% if (!myname.equals("")){ %>
            <span id="queryURL" onclick="loadXMLDoc('<%=URLEncoder.encode(myname,"UTF-8")%>', this)">
              <img src="<%= request.getContextPath() %>/image/dbpedia.png" alt="DBpedia search" height="27px" class="btn btn-default btn-ss">
              <div id="querytooltip" class="left"> </div>
            </span>
          <% } %>
          </div>
        
      </div>
    </div>
   <!-- <table align="center" class="table" style="font-weight: bold;">
          <fmt:message key="jsp.search.showindproperties.individual" />
          <%=request.getAttribute("indURI")%>
          <a href="<%=request.getContextPath()%>/semantic-search/data/<%=URLEncoder.encode(request.getAttribute("indURI").toString(),"UTF-8")%>">
          <div id='ld-image' style="padding:2px;">
          <img src="<%= request.getContextPath() %>/image/rdf.png" alt="RDF" height="27px" align="right" style="padding:2px;">
          </a>
          <% if (!myname.equals("")){ %>
            <div id="queryURL" onclick="loadXMLDoc('<%=URLEncoder.encode(myname,"UTF-8")%>', this)">
            <img src="<%= request.getContextPath() %>/image/dbpedia.png" alt="DBpedia search" height="27px" align="right" style="padding:2px;">
            <div id="querytooltip"> </div>
          <% } %>
            </div>
          </div>

    </table> -->

    </br>
    <%-- Display the classes of Individual --%>


    <%
        if (types!=null && !types.isEmpty())
            {
    %>
    <div class="panel panel-info">
      <table align="center" class="table table-bordered table-striped" cellspacing="1">
      <div class="panel-heading" align="center">
          <fmt:message key="jsp.search.showindproperties.classes" />
      </div>
      <tr class='row0'>
        <td>
          <%
              int size = types.size();
                      int i = 1;
                      for (OWLClass owl_class : types)
                      {
          %>
          <a
            href='<%=request.getContextPath()+"/semantic-search?expression="
                                + URLEncoder.encode(shortFormProvider
                                        .getShortForm((OWLEntity) owl_class), "UTF-8")%>'><%=shortFormProvider.getShortForm((OWLEntity) owl_class)%></a>
          <%
              if (i < size)
                          {
                              out.print(", ");
                              i++;
                          }
                      }
          %>
        </td>
      </tr>

      </table>
    </div>

    <%
        }
    %>

    <%-- Display the object properties of Individual --%>

    <%
        if (object_assertions!=null && !object_assertions.isEmpty())
            {
    %>

    <div class="panel panel-info">
      <table align="center" class="table table-bordered table-striped" cellspacing="1">
        <div class="panel-heading" align="center">
          <fmt:message key="jsp.search.showindproperties.objectproperty" />
        </div>
        <tr class="header">
          <td>
            <fmt:message key="jsp.search.showindproperties.field" />
          </td>
          <td>
            <fmt:message key="jsp.search.showindproperties.value" />
          </td>
       </tr>


      <%
      int row = 0;
      for (OWLObjectPropertyAssertionAxiom a : object_assertions)
      {
        String field = "";
        OWLIndividual owlin = a.getObject();

        OWLIndividual individual = (OWLIndividual) request.getAttribute("individual");

        Set<OWLObjectPropertyExpression> inverses = new HashSet<OWLObjectPropertyExpression>();

                      String link = "";
                      boolean isInverse = false;
                      if (owlin == individual)
                      {
                          owlin = a.getSubject();
                          inverses = a.getProperty().getInverses(importsClosure);
                          for (OWLObjectPropertyExpression exp : inverses)
                          {
                              field = field + ""
                                      + shortFormProvider.getShortForm((OWLEntity) exp) + "<br>";
                          }
                          if (field.equals(""))
                          {
                              field = shortFormProvider.getShortForm((OWLEntity) a.getProperty());
                              isInverse = true;
                          }
                      }
                      else
                      {
                          field = shortFormProvider.getShortForm((OWLEntity) a.getProperty());
                      }

                      link = request.getContextPath()+"/semantic-search/resource/"
                              + URLEncoder.encode(((OWLNamedIndividual) owlin).getIRI()
                                      .toString(), "UTF-8");
      %>
      <tr class='row<%=row % 2%>'>
        <td class="property">
          <%
          if (isInverse)
          {
          %>
          <span id="inverse">(<fmt:message key="jsp.search.showindproperties.inverse" />)</span>
          <%
          }
          %>

          <%=field%></td>
        <td>
        <%
          String name = owlin.getSignature().iterator().next().getIRI().getFragment();
          if (name == null)
          {
            name = owlin.toStringID();
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
          %>
             <a href='<%=link%>'><%=name%></a>
          <% if ((field.equals("dcterms:contributor")||field.equals("dspace-ont:author")||field.equals("dcterms:type")||field.equals("dspace-ont:sponsorship"))&& !isInverse)  { %>
              <span id="queryURL" onclick="loadXMLDoc('<%=URLEncoder.encode(name,"UTF-8")%>', this)">
              <img src="<%= request.getContextPath() %>/image/dbpedia.png" alt="DBpedia search" height="22px" align="right" class="btn btn-default btn-ss">
              <div id="querytooltip"></div>
              </span>
          <% } %>
        </td>                     
      </tr>
      <%
      row++;
      }   //end of for
      %>

    </table>
    </div>

    <%
        }
    %>

    <%-- Display the negativeobject properties of Individual --%>

    <%
    if (negative_object_assertions!=null && !negative_object_assertions.isEmpty())
    {
    %>

    <div class="panel panel-info">
      <table align="center" class="table table-bordered table-stripped" cellspacing="1">
        <div class="panel-heading" align="center">
          <fmt:message key="jsp.search.showindproperties.negobjectproperty" />
        </div>
        <tr class="header">
          <td>
            <fmt:message key="jsp.search.showindproperties.field" />
          </td>
          <td>
           <fmt:message key="jsp.search.showindproperties.value" />
          </td>
        </tr>

      <%
        int row = 0;
        for (OWLNegativeObjectPropertyAssertionAxiom a : negative_object_assertions)
        {
          String field = "";
          OWLIndividual owlin = a.getObject();

          OWLIndividual individual = (OWLIndividual) request.getAttribute("individual");
          Set<OWLObjectPropertyExpression> inverses = new HashSet();

                      String link = "";
                      boolean isInverse = false;
                      if (owlin == individual)
                      {
                          owlin = a.getSubject();
                          inverses = a.getProperty().getInverses(importsClosure);
                          for (OWLObjectPropertyExpression exp : inverses)
                          {
                              field = field + ""
                                      + shortFormProvider.getShortForm((OWLEntity) exp) + "<br>";
                          }
                          if (field.equals(""))
                          {
                              field = shortFormProvider.getShortForm((OWLEntity) a.getProperty());
                              isInverse = true;
                          }
                      }
                      else
                      {
                          field = shortFormProvider.getShortForm((OWLEntity) a.getProperty());
                      }
                      link = request.getContextPath()+"/semantic-search/resource/"
                              + URLEncoder.encode(((OWLNamedIndividual) owlin).getIRI()
                                      .toString(), "UTF-8");
      %>
      <tr class='row<%=row % 2%>'>
        <td class="property"><%=field%></td>
        <td>
          <a href='<%=link%>'><%=owlin%></a>
        </td>
      </tr>
      <%
      row++;
      } //end of for
      %>

    </table>
    </div>

    <%
    }       // end if that checks emptiness of negative_object_assertions
    %>

    <%-- Display the data properties of Individual --%>

    <%
    if (data_assertions!=null && !data_assertions.isEmpty())
    {
    %>

    <div class="panel panel-info">
      <table align="center" class="table table-bordered table-striped" cellspacing="1">
        <div class="panel-heading" align="center">
          <fmt:message key="jsp.search.showindproperties.dataproperty" />
        </div>
        <tr class="header">
          <td>
            <fmt:message key="jsp.search.showindproperties.field" />
          </td>
          <td>
            <fmt:message key="jsp.search.showindproperties.value" />
          </td>
          <td>
            <fmt:message key="jsp.search.showindproperties.type" />
          </td>
          <td>
            <fmt:message key="jsp.search.showindproperties.language" />
          </td>
        </tr>
        
        
      <%
      int row = 0;
      for (OWLDataPropertyAssertionAxiom a : data_assertions)
      {
        String field = shortFormProvider.getShortForm((OWLEntity) a.getProperty());

        Object obj = a.getObject();

        String value = "-";
        String lang = "-";
        String type = "-";
        String[] res = new String[3];

        res = getValues(obj, shortFormProvider);
      %>
      <tr class='row<%=row % 2%>'>
        <td class="property"><%=field%></td>
        </td>
        <td>
        <%=res[0]%>
          <% if (field.equals("dcterms:subject")||field.equals("dcterms:publisher"))  { %>
              <span id="queryURL" onclick="loadXMLDoc('<%=URLEncoder.encode(res[0],"UTF-8")%>', this)">
              <img src="<%= request.getContextPath() %>/image/dbpedia.png" alt="DBpedia search" height="22px" align="right" class="btn btn-default btn-ss">
              <div id="querytooltip"> </div>
              </span>
          <% } %>
        </td>
        <td><%=res[2]%></td>
        <td class="language"><%=res[1]%></td>
      </tr>
      <%
        row++;
      }  // end of for
      %>

    </table>
    </div>

    <%
    }     // end of if 
    %>

    <%-- Display the negative data properties of Individual --%>

    <%
    if (negative_data_assertions!=null && !negative_data_assertions.isEmpty())
    {
    %>

    <div class="panel panel-info">
      <table align="center" class="table table-bordered table-striped" cellspacing="1">
        <div class="panel-heading" align="center">
          <fmt:message key="jsp.search.showindproperties.dataproperty" />
        </div>
        <tr class="header">
          <td>
            <fmt:message key="jsp.search.showindproperties.field" />
          </td>
          <td>
            <fmt:message key="jsp.search.showindproperties.value" />
          </td>
          <td>
            <fmt:message key="jsp.search.showindproperties.type" />
          </td>
          <td>
            <fmt:message key="jsp.search.showindproperties.language" />
          </td>
        </tr>
      <%
      int row = 0;
      for (OWLNegativeDataPropertyAssertionAxiom a : negative_data_assertions)
      {
        String field = a.getProperty().toString();

        Object obj = a.getObject();

        String value = "-";
        String lang = "-";
        String type = "-";
        String[] res = new String[3];

        res = getValues(obj, shortFormProvider);
      %>
      <tr class='row<%=row % 2%>'>
        <td class="property"><%=field%></td>
        </td>
        <td><%=res[0]%></td>
        <td><%=res[2]%></td>
        <td class="language"><%=res[1]%></td>
      </tr>
      <%
        row++;
      }   // end of for
      %>

    </table>
    </div>

    <%
    }   // end of if
    %>


    <%-- Display the annotations of Individual --%>

    <%
    if (annotations!=null && !annotations.isEmpty())
    {
    %>

    <div class="panel panel-info">
      <table align="center" class="table table-bordered table-striped" cellspacing="1">
        <div class="panel-heading" align="center">
          <fmt:message key="jsp.search.showindproperties.annotation" />
        </div>
      <tr class="header">
        <td>
          <fmt:message key="jsp.search.showindproperties.field" />
        </td>
        <td>
          <fmt:message key="jsp.search.showindproperties.value" />
        </td>
        <td>
          <fmt:message key="jsp.search.showindproperties.type" />
        </td>
        <td>
          <fmt:message key="jsp.search.showindproperties.language" />
        </td>
      </tr>

      <%
      int row = 0;
      for (OWLAnnotationAssertionAxiom a : annotations)
      {
        OWLAnnotation annotation = a.getAnnotations().iterator().next();
        IRI annotation_uri = annotation.getProperty().getIRI();
        String annotion_property = "";

        if (annotation_uri.getFragment() != null)
        {
          annotion_property = annotation_uri.getFragment();
        }
        else
        {
          annotion_property = annotation_uri.toString();
        }

        String value = "-";
        String lang = "-";
        String type = "-";

        value = annotation.getValue().toString();

        /*
        if (annotation.isAnnotationByConstant())
        {
          String[] res = new String[3];
          OWLConstant owlc = a.getAnnotation().getAnnotationValueAsConstant();
          res = getValues(owlc, shortFormProvider);
          value = res[0];
          lang = res[1];
          type = res[2];
        }
        else
        {
          value = annotation.getAnnotationValue().toString();
        }
      */
      %>
      <tr class='row<%=row % 2%>'>
        <td class="property"><%=annotion_property%></td>
        <td><%=""/*value*/%></td>
        <td><%=""/*type*/%></td>
        <td class="language"><%=lang%></td>
      </tr>

      <%
        row++;
       } // end of for
      %>

    </table>
    </div>
    <%
      } // end of if
    %>

</dspace:layout>


<%!/* Function to return the values of a Constant */

    private String[] getValues(Object obj, ShortFormProvider sf) throws URISyntaxException
    {
        String[] returnValues = new String[3];

        String value = "-";
        String lang = "-";
        String type = "-";

        if (obj instanceof OWLLiteral)
        {
            OWLLiteral literal = (OWLLiteral) obj;

            returnValues[0] = literal.getLiteral();
            returnValues[1] = literal.getLang();
            returnValues[2] = literal.getDatatype().toString();
            /* In case Datatype is xsd:anyURI, make value a link 
               and try escaping it first*/
            if (returnValues[2].equals("xsd:anyURI")) {
            	returnValues[0] = "<A HREF=\""+new URI(returnValues[0]).toString()+"\" TARGET='_blank'>"
            	+returnValues[0]+"</A>";
            }
        }

        return returnValues;
    }
  %>
