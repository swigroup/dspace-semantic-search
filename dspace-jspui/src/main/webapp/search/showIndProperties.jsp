<%--

    The contents of this file are subject to the license and copyright
    detailed in the LICENSE and NOTICE files at the root of the source
    tree and available online at

    http://www.dspace.org/license/

--%>

<%-- Show Individual Properties JSP --%>

<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@ page import="java.net.URLEncoder"%>
<%@ page import="java.net.URI"%>
<%@ page import="java.util.Set"%>
<%@ page import="java.util.HashSet"%>
<%@ page import="java.util.SortedSet"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="java.net.URI"%>
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

<link rel="stylesheet" type="text/css" href="ss-custom.css" />


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
    
    if (!object_assertions.isEmpty())
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

<script>

function loadXMLDoc(s){
  document.getElementById('querytooltip').style.display = 'block';
  var keyword=s.replace(" ","_");              //@gs change with the keyword

  var xmlhttp;
  if (window.XMLHttpRequest)
	  {// code for IE7+, Firefox, Chrome, Opera, Safari
	   xmlhttp=new XMLHttpRequest();
	  }
	else
	  {// code for IE6, IE5
	   xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	  }
  
	var query_url = "http://lookup.dbpedia.org/api/search.asmx/KeywordSearch?QueryString="+keyword;
	xmlhttp.open("GET",query_url,false);
	xmlhttp.send();
	xmlDoc=xmlhttp.responseXML;
	
	var x=xmlDoc.getElementsByTagName("Result");
	var result="";
	var resultURI="";
	if (x.length!=0){
		for (i=0;i<x.length;i++)
		{ 
			//resultURI=encodeURI(x[i].getElementsByTagName("URI")[0].childNodes[0].nodeValue);
			resultURI=x[i].getElementsByTagName("URI")[0].childNodes[0].nodeValue;
			
			result=result+'<a href="'+resultURI+'" target="_blank">'+resultURI+'</a></br>';
		}
		document.getElementById("querytooltip").innerHTML="\n"+result;
	}
	else{
		document.getElementById("querytooltip").innerHTML="\nNo results";
	}
}

function hideTooltip(){
  document.getElementById('querytooltip').style.display = 'none';
}

</script>
<dspace:layout locbar="nolink" titlekey="jsp.search.advanced.title">
   
   <div align="center">

    <table class="show_ind_table" cellspacing="1">
      <tr>
        <td class="itemHeader">
          <fmt:message key="jsp.search.showindproperties.individual" />
          <%=request.getAttribute("indURI")%>
          <a href="<%=request.getContextPath()%>/semantic-search/data/<%=URLEncoder.encode(request.getAttribute("indURI").toString(),"UTF-8")%>">
          <div id='ld-image' style="padding:2px;">
          <img src="<%= request.getContextPath() %>/image/rdf.png" alt="RDF" height="27px" align="right" style="padding:2px;">
          </a>
          <% if (!myname.equals("")){ %>
            <div id='queryURL' onmouseover='loadXMLDoc("<%=myname%>")' onmouseout='hideTooltip()'>
            <img src="<%= request.getContextPath() %>/image/dbpedia.png" alt="DBpedia search" height="27px" align="right" style="padding:2px;">
            <span id='querytooltip' style="position:relative; rigth:1px;"> </span>
          <% } %>
            </div>
          </div>

        </td>
      </tr>
    </table>

    <%-- Display the classes of Individual --%>

    <%
        if (!types.isEmpty())
            {
    %>

    <table class="show_ind_table" cellspacing="1">
      <tr class="table_title">
        <td>
          <fmt:message key="jsp.search.showindproperties.classes" />
        </td>
      </tr>
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

    <%
        }
    %>

    <%-- Display the object properties of Individual --%>

    <%
        if (!object_assertions.isEmpty())
            {
    %>

    <table class="show_ind_table" cellspacing="1">
      <tr class="table_title">
        <td colspan="2">
          <fmt:message key="jsp.search.showindproperties.objectproperty" />
        </td>
      </tr>
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
            <div id='queryURL' onmouseover='loadXMLDoc("<%=name%>")' onmouseout='hideTooltip()'>
            <a href='<%=link%>'><%=name%></a>
          <% if ((field.equals("dcterms:contributor")||field.equals("dspace-ont:author")||field.equals("dcterms:type")||field.equals("dspace-ont:sponsorship"))&& !isInverse)  { %>
              <img src="<%= request.getContextPath() %>/image/dbpedia.png" alt="DBpedia search" height="22px" align="right">
              <span id='querytooltip'> </span>
              </div>
          <% } %>
        </td>                     
      </tr>
      <%
      row++;
      }   //end of for
      %>

    </table>

    <%
        }
    %>

    <%-- Display the negativeobject properties of Individual --%>

    <%
    if (!negative_object_assertions.isEmpty())
    {
    %>

    <table class="show_ind_table" cellspacing="1">
      <tr class="table_title">
        <td colspan="2">
          <fmt:message key="jsp.search.showindproperties.negobjectproperty" />
        </td>
      </tr>
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

    <%
    }       // end if that checks emptiness of negative_object_assertions
    %>

    <%-- Display the data properties of Individual --%>

    <%
    if (!data_assertions.isEmpty())
    {
    %>

    <table class="show_ind_table" cellspacing="1">
      <tr class="table_title">
        <td colspan="4">
          <fmt:message key="jsp.search.showindproperties.dataproperty" />
        </td>
      </tr>
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
        <div id='queryURL' onmouseover='loadXMLDoc("<%=res[0]%>")' onmouseout='hideTooltip()'>
        <%=res[0]%>
          <% if (field.equals("dcterms:subject")||field.equals("dcterms:publisher"))  { %>
              <img src="<%= request.getContextPath() %>/image/dbpedia.png" alt="DBpedia search" height="22px" align="right">
              <span id='querytooltip'> </span>
              </div>
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

    <%
    }     // end of if 
    %>

    <%-- Display the negative data properties of Individual --%>

    <%
    if (!negative_data_assertions.isEmpty())
    {
    %>

    <table class="show_ind_table" cellspacing="1">
      <tr class="table_title">
        <td colspan="4">
          <fmt:message key="jsp.search.showindproperties.dataproperty" />
        </td>
      </tr>
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

    <%
    }   // end of if
    %>


    <%-- Display the annotations of Individual --%>

    <%
    if (!annotations.isEmpty())
    {
    %>

    <table class="show_ind_table" cellspacing="1">
      <tr class="table_title">
        <td colspan="4">
          <fmt:message key="jsp.search.showindproperties.annotation" />
        </td>
      </tr>
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
    <%
      } // end of if
    %>
  </div>

</dspace:layout>


<%!/* Function to return the values of a Constant */

    private String[] getValues(Object obj, ShortFormProvider sf)
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
            /* In case Datatype is xsd:anyURI, make value a link */
            if (returnValues[2].equals("xsd:anyURI")) {
            	returnValues[0] = "<A HREF='"+returnValues[0]+"' TARGET='_blank'>"
            	+returnValues[0]+"</A>";
            }
        }

        return returnValues;
    }
  %>
