<%--

    The contents of this file are subject to the license and copyright
    detailed in the LICENSE and NOTICE files at the root of the source
    tree and available online at

    http://www.dspace.org/license/

--%>

<%-- Semantic Search JSP --%>
 
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="javax.servlet.jsp.jstl.fmt.LocaleSupport" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@ page import="java.net.URLEncoder"%>
<%@ page import="java.net.URI"%>
<%@ page import="java.util.regex.Matcher"%>
<%@ page import="java.util.regex.Pattern"%>
<%@ page import="java.text.DecimalFormat"%>
<%@page import="org.semanticweb.owlapi.model.OWLIndividual"%>
<%@page import="org.semanticweb.owlapi.model.OWLNamedIndividual"%>
<%@page import="org.semanticweb.owlapi.model.IRI"%>
<%@page import="gr.upatras.ceid.hpclab.owl.SemanticUnit"%>
<%@page import="gr.upatras.ceid.hpclab.reasoner.SupportedReasoner"%>
<%@page import="gr.upatras.ceid.hpclab.owl.OWLDSpaceQueryManager"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Locale"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.HashSet"%>
<%@page import="org.semanticweb.owlapi.model.OWLClass"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.dspace.org/dspace-tags.tld" prefix="dspace"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
    String URL = session.getAttribute("URL") == null ? "" : (String) session
            .getAttribute("URL");
	  boolean reload = request.getParameter("reload") == null ? false : Boolean.parseBoolean((String) request.getParameter("reload")) ;
    String expression = request.getParameter("expression") == null ? "" : request
            .getParameter("expression");
    String reasoner = (String) session.getAttribute("reasoner");

    String localContext = "&expression=" + URLEncoder.encode(expression, "UTF-8");

    double totalTime = session.getAttribute("totalTime") == null ? 0 : (Double) session
            .getAttribute("totalTime");
    String time = null;
    if (totalTime != 0){
      DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance(Locale.US);
      df.setMinimumFractionDigits(4);
      df.setMaximumFractionDigits(4);
      time = df.format(totalTime/1000); // divide with 1000 because totaltime is in the form of milliseconds
    }

    if (URL.equals(""))
    {
        session.removeAttribute("ResultSet");
        session.removeAttribute("items");
        session.removeAttribute("names");
    }
    if (expression.equals(""))
    {
        session.removeAttribute("ResultSet");
        session.removeAttribute("items");
    }

    Object[] items = (Object[]) request.getAttribute("ResultSet");
    if (items == null)
        items = (Object[]) session.getAttribute("ResultSet");
    session.setAttribute("ResultSet", items);

    int length = items == null ? 0 : items.length;
    int offset = request.getParameter("offset") == null ? 0 : Integer
            .parseInt((String) request.getParameter("offset"));
    if (offset > length)
        offset = length;
    int step = (length - offset) < 20 ? length - offset : 20;
  
    List<String> classList = new ArrayList<String>();
    List<String> objectPropertiesList = new ArrayList<String>();
    SemanticUnit semanticUnit = null;
    OWLDSpaceQueryManager queryManager = null;
    try {  
    semanticUnit = SemanticUnit.getInstance(URL, SupportedReasoner.valueOf(reasoner),false, session.getId());
    queryManager = new OWLDSpaceQueryManager(semanticUnit);
    classList = queryManager.getFullClassList();
    objectPropertiesList = queryManager.getFullObjectPropertiesList();
    
    }
    catch (Exception e) {
    }
%>

<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/semantic.css" />

<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/ext-3.3.0/resources/css/ext-all.css" /> 
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/ext-3.3.0/resources/css/xtheme-gray.css" />

<link rel="stylesheet" href="<%=request.getContextPath()%>/codemirror/lib/codemirror.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/codemirror/addon/hint/show-hint.css">
    
<script type="text/javascript" src="<%=request.getContextPath()%>/ext-3.3.0/adapter/prototype/ext-prototype-adapter.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/ext-3.3.0/jquery/ext-jquery-adapter.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/ext-3.3.0/ext-all.js"></script> 
  
<script type="text/javascript" src="<%=request.getContextPath()%>/codemirror/lib/codemirror.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/codemirror/addon/edit/matchbrackets.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/codemirror/mode/sparql/sparql.js"></script> 
<script type="text/javascript" src="<%=request.getContextPath()%>/codemirror/mode/ms/ms.js"></script>      
<script type="text/javascript" src="<%=request.getContextPath()%>/codemirror/addon/display/placeholder.js"></script> 

<script type="text/javascript" src="<%=request.getContextPath()%>/codemirror/addon/hint/sparql-hint.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/codemirror/addon/hint/ms-hint.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/codemirror/addon/hint/show-hint.js"></script>

<script type="text/javascript" src="<%=request.getContextPath()%>/grouping-combobox.js"></script>  
<script type="text/javascript" src="<%=request.getContextPath()%>/ui.js"></script>

<dspace:layout locbar="nolink" titlekey="jsp.layout.navbar-default.semantic">

<h2><fmt:message key="jsp.layout.navbar-default.semantic"/></h2>


      <div align="center">
        <font size=3><fmt:message key="jsp.search.semantic.questionnaire.info"/>
        <a href="http://goo.gl/Zt0aP"><fmt:message key="jsp.search.semantic.questionnaire.click" /></a> <font>
      </div>
      
    <div id="destino" class="panel panel-default">
      <div align="right"><dspace:popup page="<%= LocaleSupport.getLocalizedMessage(pageContext,\"help.index\") + \"#semantic\"%>"><fmt:message key="jsp.help"/></dspace:popup> </div>
    </div>

<script type="text/javascript">    
         
    var dataClasses = [ 
      <% 
      
         for (int i=0 ; i<classList.size() ; i++)
         {
      %>
              [ '<%= classList.get(i) %>', '<%= classList.get(i).split(":")[1] %>', 'Types' ]
      <%      if (i < classList.size()-1) 
              {
      %>          ,
      <%      }
         } %>
      ];
 	 <% if (classList.isEmpty()) 
	 	{
	 %>
	 	dataClasses = [['','','Types']];
	 <%	}
	 %>
   
      var dataObjectProperties = [ 
      <% 
      
         for (int i=0 ; i<objectPropertiesList.size() ; i++)
         {
      %>
              [ '<%= objectPropertiesList.get(i) %>', '<%= objectPropertiesList.get(i).split(":")[1] %>', 'Relations' ]
      <%      if (i < objectPropertiesList.size()-1) 
              {
      %>          ,
      <%      }
         } %>
      ];
  	 <% if (objectPropertiesList.isEmpty()) 
	 	{
	 %>
	 	dataObjectProperties = [['','','Relations']];
	 <%	}
	 %>
     
      var dataFull = dataClasses.concat(dataObjectProperties);
                  
     Ext.onReady(function() {
         Ext.BLANK_IMAGE_URL = 'ext-3.3.0/resources/images/default/s.gif';         
         Ext.QuickTips.init();
         
         appInit('<%= expression %>', '<%= reasoner %>', '<%= URL %>');
         
      }); 
      
</script>
 
 <%
      if (request.getAttribute("error") != null)
          {
              String error_ss = (String) request.getAttribute("error");
              error_ss = StringEscapeUtils.escapeHtml (error_ss);
              Pattern p = Pattern.compile("\n");
              Matcher m = p.matcher(error_ss);
              StringBuffer sb = new StringBuffer();

              while (m.find())
              {
                  m.appendReplacement(sb, "<br>");
              }

              m.appendTail(sb);
  %>

  <p align="center" class="text-warning"><%=sb.toString()%></p>
  
  

  <%
              items = null;
          }

          if (length > 0 && items != null)
          {
  %>
             <script type="text/javascript"> 
                saveMSQuery();
             </script>

  <br />


   <div class="alert alert-info">
    <fmt:message key="jsp.search.results.results">
      <fmt:param><%=offset + 1%></fmt:param>
      <fmt:param><%=offset + step%></fmt:param>
      <fmt:param><%=length%></fmt:param>
      <fmt:param><%=time%></fmt:param>
    </fmt:message>
    </div>
    
    <ul class="pagination pull-right">
  
  <%-- Previous page/Next page --%>
        <%
            if (offset >= step)
                    {
        %>
        <li><a
          href="<%=request.getContextPath()%>/semantic-search?offset=<%=offset - 20%><%=localContext%>"><fmt:message
            key="jsp.browse.general.previous" /> </a></li>
        <%
            }
            if (offset + step < length)
              {
        %>
        <li><a
          href="<%=request.getContextPath()%>/semantic-search?offset=<%=offset + 20%><%=localContext%>"><fmt:message
            key="jsp.browse.general.next" /> </a></li>
        <%
            }
        %>
        </ul>
  
   </br></br></br></br>
  <%-- The subjects --%>
  <div class="panel panel-info">

  
   <table align="center" class="table table-bordered table-striped">
        <thead style="font-size: 18px;">
            <tr>
                <th width="30%"><b>Type</b></th>
                <th width="70%"><b>Value</b></th>
            </tr>
        </thead>   
        <tbody style="font-size: 14px;">   
   
     
    <%
        // Row: toggles between Odd and Even
                String row = "odd";
                Object[] results = items;

                for (int i = offset; i < offset + step; i++)
                {
                    OWLNamedIndividual individual = (OWLNamedIndividual) results[i];
                    IRI iri = individual.getIRI();
    %>
    <tr>
      <td class="<%=row%>RowOddCol">
        <%
             Set<OWLClass> types = queryManager.getOWLClasses(individual);
                          
             if (types.size() > 0) 
             {
                 OWLClass lastClass = (OWLClass) types.toArray()[types.size()-1];
                 
        IRI newIri = lastClass.getIRI();
        String name = newIri.getFragment();
        if (name == null)
        {
            name = newIri.toString();
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
                 <%= name %>
        <%   }
        %>
      </td>
      <td class="<%=row%>RowOddCol">
        <a
         href="<%=request.getContextPath()%>/semantic-search/resource/<%=URLEncoder.encode(iri.toString(), "UTF-8")%>">
          
          <%
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
          
          
           %>
          <%=name%> </a>
      </td>
    </tr>
    <%
        row = (row.equals("odd") ? "even" : "odd");
                }
    %>
    </tbody> 
  </table>
  </div>
  
  
  <ul class="pagination pull-right">
  <%-- Previous page/Next page --%>
        <%
            if (offset >= step)
                    {
        %>
        <li><a
          href="<%=request.getContextPath()%>/semantic-search?offset=<%=offset - 20%><%=localContext%>"><fmt:message
            key="jsp.browse.general.previous" /> </a></li>
        <%
            }
            if (offset + step < length)
                    {
        %>
        <li><a
          href="<%=request.getContextPath()%>/semantic-search?offset=<%=offset + 20%><%=localContext%>"><fmt:message
            key="jsp.browse.general.next" /> </a></li>
        <%
            }
        %>
    </ul>

  <%
      }
          else if (request.getAttribute("error") == null && !expression.equals(""))
          {
  %>
             <script type="text/javascript"> 
               saveMSQuery();
             </script>
  <br />
  <p align="center">
    <fmt:message key="jsp.search.semantic.noresults" />   
  </p>

  <%
      }
  %> 
</div> 
</dspace:layout>