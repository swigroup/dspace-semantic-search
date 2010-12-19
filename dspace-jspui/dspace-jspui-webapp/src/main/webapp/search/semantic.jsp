<%-- Semantic Search JSP --%>

<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@ page import="java.net.URLEncoder"%>
<%@ page import="java.net.URI"%>
<%@ page import="java.util.regex.Matcher"%>
<%@ page import="java.util.regex.Pattern"%>
<%@page import="org.semanticweb.owlapi.model.OWLIndividual"%>
<%@page import="org.semanticweb.owlapi.model.OWLNamedIndividual"%>
<%@page import="org.semanticweb.owlapi.model.IRI"%>
<%@page import="gr.upatras.ceid.hpclab.owl.SemanticUnit"%>
<%@page import="gr.upatras.ceid.hpclab.reasoner.SupportedReasoner"%>
<%@page import="gr.upatras.ceid.hpclab.owl.OWLDSpaceQueryManager"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Set"%>
<%@page import="org.semanticweb.owlapi.model.OWLClass"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.dspace.org/dspace-tags.tld" prefix="dspace"%>

<%
    String URL = session.getAttribute("URL") == null ? "" : (String) session
            .getAttribute("URL");
    String expression = request.getParameter("expression") == null ? "" : request
            .getParameter("expression");
    String reasoner = (String) session.getAttribute("reasoner");

    String localContext = "&expression=" + expression;

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
    
    SemanticUnit semanticUnit = SemanticUnit.getInstance(URL, SupportedReasoner.valueOf(reasoner));
    OWLDSpaceQueryManager queryManager = new OWLDSpaceQueryManager(semanticUnit);
%>

<dspace:layout locbar="nolink" titlekey="jsp.search.semantic.title">

    <div id="destino" style="width:500px; margin:auto; margin-top:20px;"></div>
        
  <link rel="stylesheet" type="text/css" href="ext-3.3.0/resources/css/ext-all.css" />
  <link rel="stylesheet" type="text/css" href="ext-3.3.0/resources/css/xtheme-gray.css" />  
  <script type="text/javascript" src="ext-3.3.0/adapter/prototype/ext-prototype-adapter.js"></script>
  <script type="text/javascript" src="ext-3.3.0/ext-all.js"></script>
  <script type="text/javascript" src="grouping-combobox.js"></script>  
  <script type="text/javascript" src="ui.js"></script>  

<style type="text/css">
.x-combo-list-group {
  font: bold 12px tahoma,arial,helvetica,sans-serif;
  padding: 2px;
  border: 1px solid #fff;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>

    <script type="text/javascript">      
      var dataClasses = [ 
      <% List<String> classList = queryManager.getFullClassList();
      
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
      
      var dataObjectProperties = [ 
      <% List<String> objectPropertiesList = queryManager.getFullObjectPropertiesList();
      
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
              String error = (String) request.getAttribute("error");
              Pattern p = Pattern.compile("\n");
              Matcher m = p.matcher(error);
              StringBuffer sb = new StringBuffer();

              while (m.find())
              {
                  m.appendReplacement(sb, "<br>");
              }

              m.appendTail(sb);
  %>

  <p align="center" class="submitFormWarn"><%=sb.toString()%></p>

  <%
              items = null;
          }

          if (length > 0 && items != null)
          {
  %>

  <br />


  <p align="center">
    <fmt:message key="jsp.search.results.results">
      <fmt:param><%=offset + 1%></fmt:param>
      <fmt:param><%=offset + step%></fmt:param>
      <fmt:param><%=length%></fmt:param>
    </fmt:message>
  </p>

  <%-- Previous page/Next page --%>
  <table align="center" border="0" width="70%">
    <tr>
      <td class="standard" align="left">
        <%
            if (offset >= step)
                    {
        %>
        <a
          href="<%=request.getContextPath()%>/semantic-search?offset=<%=offset - 20%><%=localContext%>"><fmt:message
            key="jsp.browse.general.previous" /> </a>
        <%
            }
        %>
      </td>
      <td class="standard" align="right">
        <%
            if (offset + step < length)
                    {
        %>
        <a
          href="<%=request.getContextPath()%>/semantic-search?offset=<%=offset + 20%><%=localContext%>"><fmt:message
            key="jsp.browse.general.next" /> </a>
        <%
            }
        %>
      </td>
    </tr>
  </table>

  <%-- The subjects --%>

  <table align="center" class="miscTable" summary="This table displays a list of subjects">
    <tr>
      <td width="150">Type</td>
      <td width="250">Value</td>
    </tr>
    
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
          href="<%=request.getContextPath()%>/semantic-search?indURI=<%=URLEncoder.encode(iri.toString(), "UTF-8")%>">
          
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
  </table>
  <%-- Previous page/Next page --%>
  <table align="center" border="0" width="70%">
    <tr>
      <td class="standard" align="left">
        <%
            if (offset >= step)
                    {
        %>
        <a
          href="<%=request.getContextPath()%>/semantic-search?offset=<%=offset - 20%><%=localContext%>"><fmt:message
            key="jsp.browse.general.previous" /> </a>
        <%
            }
        %>
      </td>
      <td class="standard" align="right">
        <%
            if (offset + step < length)
                    {
        %>
        <a
          href="<%=request.getContextPath()%>/semantic-search?offset=<%=offset + 20%><%=localContext%>"><fmt:message
            key="jsp.browse.general.next" /> </a>
        <%
            }
        %>
      </td>
    </tr>
  </table>

  <%
      }
          else if (request.getAttribute("error") == null && !expression.equals(""))
          {
  %>
  <br />
  <p align="center">
    <fmt:message key="jsp.search.semantic.noresults" />
  </p>

  <%
      }
  %>

</dspace:layout>
<%-- Semantic Search JSP --%>

<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@ page import="java.net.URLEncoder"%>
<%@ page import="java.net.URI"%>
<%@ page import="java.util.regex.Matcher"%>
<%@ page import="java.util.regex.Pattern"%>
<%@page import="org.semanticweb.owlapi.model.OWLIndividual"%>
<%@page import="org.semanticweb.owlapi.model.OWLNamedIndividual"%>
<%@page import="org.semanticweb.owlapi.model.IRI"%>
<%@page import="gr.upatras.ceid.hpclab.owl.SemanticUnit"%>
<%@page import="gr.upatras.ceid.hpclab.reasoner.SupportedReasoner"%>
<%@page import="gr.upatras.ceid.hpclab.owl.OWLDSpaceQueryManager"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Set"%>
<%@page import="org.semanticweb.owlapi.model.OWLClass"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.dspace.org/dspace-tags.tld" prefix="dspace"%>

<%
    String URL = session.getAttribute("URL") == null ? "" : (String) session
            .getAttribute("URL");
    String expression = request.getParameter("expression") == null ? "" : request
            .getParameter("expression");
    String reasoner = (String) session.getAttribute("reasoner");

    String localContext = "&expression=" + expression;

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
    
    SemanticUnit semanticUnit = SemanticUnit.getInstance(URL, SupportedReasoner.valueOf(reasoner));
    OWLDSpaceQueryManager queryManager = new OWLDSpaceQueryManager(semanticUnit);
%>

<dspace:layout locbar="nolink" titlekey="jsp.search.semantic.title">

    <div id="destino" style="width:500px; margin:auto; margin-top:20px;"></div>
        
  <link rel="stylesheet" type="text/css" href="ext-3.3.0/resources/css/ext-all.css" />
  <link rel="stylesheet" type="text/css" href="ext-3.3.0/resources/css/xtheme-gray.css" />  
  <script type="text/javascript" src="ext-3.3.0/adapter/prototype/ext-prototype-adapter.js"></script>
  <script type="text/javascript" src="ext-3.3.0/ext-all.js"></script>
  <script type="text/javascript" src="grouping-combobox.js"></script>  
  <script type="text/javascript" src="ui.js"></script>  

<style type="text/css">
.x-combo-list-group {
  font: bold 12px tahoma,arial,helvetica,sans-serif;
  padding: 2px;
  border: 1px solid #fff;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>

    <script type="text/javascript">      
      var dataClasses = [ 
      <% List<String> classList = queryManager.getFullClassList();
      
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
      
      var dataObjectProperties = [ 
      <% List<String> objectPropertiesList = queryManager.getFullObjectPropertiesList();
      
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
              String error = (String) request.getAttribute("error");
              Pattern p = Pattern.compile("\n");
              Matcher m = p.matcher(error);
              StringBuffer sb = new StringBuffer();

              while (m.find())
              {
                  m.appendReplacement(sb, "<br>");
              }

              m.appendTail(sb);
  %>

  <p align="center" class="submitFormWarn"><%=sb.toString()%></p>

  <%
              items = null;
          }

          if (length > 0 && items != null)
          {
  %>

  <br />


  <p align="center">
    <fmt:message key="jsp.search.results.results">
      <fmt:param><%=offset + 1%></fmt:param>
      <fmt:param><%=offset + step%></fmt:param>
      <fmt:param><%=length%></fmt:param>
    </fmt:message>
  </p>

  <%-- Previous page/Next page --%>
  <table align="center" border="0" width="70%">
    <tr>
      <td class="standard" align="left">
        <%
            if (offset >= step)
                    {
        %>
        <a
          href="<%=request.getContextPath()%>/semantic-search?offset=<%=offset - 20%><%=localContext%>"><fmt:message
            key="jsp.browse.general.previous" /> </a>
        <%
            }
        %>
      </td>
      <td class="standard" align="right">
        <%
            if (offset + step < length)
                    {
        %>
        <a
          href="<%=request.getContextPath()%>/semantic-search?offset=<%=offset + 20%><%=localContext%>"><fmt:message
            key="jsp.browse.general.next" /> </a>
        <%
            }
        %>
      </td>
    </tr>
  </table>

  <%-- The subjects --%>

  <table align="center" class="miscTable" summary="This table displays a list of subjects">
    <tr>
      <td width="150">Type</td>
      <td width="250">Value</td>
    </tr>
    
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
          href="<%=request.getContextPath()%>/semantic-search?indURI=<%=URLEncoder.encode(iri.toString(), "UTF-8")%>">
          
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
  </table>
  <%-- Previous page/Next page --%>
  <table align="center" border="0" width="70%">
    <tr>
      <td class="standard" align="left">
        <%
            if (offset >= step)
                    {
        %>
        <a
          href="<%=request.getContextPath()%>/semantic-search?offset=<%=offset - 20%><%=localContext%>"><fmt:message
            key="jsp.browse.general.previous" /> </a>
        <%
            }
        %>
      </td>
      <td class="standard" align="right">
        <%
            if (offset + step < length)
                    {
        %>
        <a
          href="<%=request.getContextPath()%>/semantic-search?offset=<%=offset + 20%><%=localContext%>"><fmt:message
            key="jsp.browse.general.next" /> </a>
        <%
            }
        %>
      </td>
    </tr>
  </table>

  <%
      }
          else if (request.getAttribute("error") == null && !expression.equals(""))
          {
  %>
  <br />
  <p align="center">
    <fmt:message key="jsp.search.semantic.noresults" />
  </p>

  <%
      }
  %>

</dspace:layout>
<%-- Semantic Search JSP --%>

<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@ page import="java.net.URLEncoder"%>
<%@ page import="java.net.URI"%>
<%@ page import="java.util.regex.Matcher"%>
<%@ page import="java.util.regex.Pattern"%>
<%@page import="org.semanticweb.owlapi.model.OWLIndividual"%>
<%@page import="org.semanticweb.owlapi.model.OWLNamedIndividual"%>
<%@page import="org.semanticweb.owlapi.model.IRI"%>
<%@page import="gr.upatras.ceid.hpclab.owl.SemanticUnit"%>
<%@page import="gr.upatras.ceid.hpclab.reasoner.SupportedReasoner"%>
<%@page import="gr.upatras.ceid.hpclab.owl.OWLDSpaceQueryManager"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Set"%>
<%@page import="org.semanticweb.owlapi.model.OWLClass"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.dspace.org/dspace-tags.tld" prefix="dspace"%>

<%
    String URL = session.getAttribute("URL") == null ? "" : (String) session
            .getAttribute("URL");
    String expression = request.getParameter("expression") == null ? "" : request
            .getParameter("expression");
    String reasoner = (String) session.getAttribute("reasoner");

    String localContext = "&expression=" + expression;

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
    
    SemanticUnit semanticUnit = SemanticUnit.getInstance(URL, SupportedReasoner.valueOf(reasoner));
    OWLDSpaceQueryManager queryManager = new OWLDSpaceQueryManager(semanticUnit);
%>

<dspace:layout locbar="nolink" titlekey="jsp.search.semantic.title">

    <div id="destino" style="width:500px; margin:auto; margin-top:20px;"></div>
        
  <link rel="stylesheet" type="text/css" href="ext-3.3.0/resources/css/ext-all.css" />
  <link rel="stylesheet" type="text/css" href="ext-3.3.0/resources/css/xtheme-gray.css" />  
  <script type="text/javascript" src="ext-3.3.0/adapter/prototype/ext-prototype-adapter.js"></script>
  <script type="text/javascript" src="ext-3.3.0/ext-all.js"></script>
  <script type="text/javascript" src="grouping-combobox.js"></script>  
  <script type="text/javascript" src="ui.js"></script>  

<style type="text/css">
.x-combo-list-group {
  font: bold 12px tahoma,arial,helvetica,sans-serif;
  padding: 2px;
  border: 1px solid #fff;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>

    <script type="text/javascript">      
      var dataClasses = [ 
      <% List<String> classList = queryManager.getFullClassList();
      
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
      
      var dataObjectProperties = [ 
      <% List<String> objectPropertiesList = queryManager.getFullObjectPropertiesList();
      
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
              String error = (String) request.getAttribute("error");
              Pattern p = Pattern.compile("\n");
              Matcher m = p.matcher(error);
              StringBuffer sb = new StringBuffer();

              while (m.find())
              {
                  m.appendReplacement(sb, "<br>");
              }

              m.appendTail(sb);
  %>

  <p align="center" class="submitFormWarn"><%=sb.toString()%></p>

  <%
              items = null;
          }

          if (length > 0 && items != null)
          {
  %>

  <br />


  <p align="center">
    <fmt:message key="jsp.search.results.results">
      <fmt:param><%=offset + 1%></fmt:param>
      <fmt:param><%=offset + step%></fmt:param>
      <fmt:param><%=length%></fmt:param>
    </fmt:message>
  </p>

  <%-- Previous page/Next page --%>
  <table align="center" border="0" width="70%">
    <tr>
      <td class="standard" align="left">
        <%
            if (offset >= step)
                    {
        %>
        <a
          href="<%=request.getContextPath()%>/semantic-search?offset=<%=offset - 20%><%=localContext%>"><fmt:message
            key="jsp.browse.general.previous" /> </a>
        <%
            }
        %>
      </td>
      <td class="standard" align="right">
        <%
            if (offset + step < length)
                    {
        %>
        <a
          href="<%=request.getContextPath()%>/semantic-search?offset=<%=offset + 20%><%=localContext%>"><fmt:message
            key="jsp.browse.general.next" /> </a>
        <%
            }
        %>
      </td>
    </tr>
  </table>

  <%-- The subjects --%>

  <table align="center" class="miscTable" summary="This table displays a list of subjects">
    <tr>
      <td width="150">Type</td>
      <td width="250">Value</td>
    </tr>
    
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
          href="<%=request.getContextPath()%>/semantic-search?indURI=<%=URLEncoder.encode(iri.toString(), "UTF-8")%>">
          
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
  </table>
  <%-- Previous page/Next page --%>
  <table align="center" border="0" width="70%">
    <tr>
      <td class="standard" align="left">
        <%
            if (offset >= step)
                    {
        %>
        <a
          href="<%=request.getContextPath()%>/semantic-search?offset=<%=offset - 20%><%=localContext%>"><fmt:message
            key="jsp.browse.general.previous" /> </a>
        <%
            }
        %>
      </td>
      <td class="standard" align="right">
        <%
            if (offset + step < length)
                    {
        %>
        <a
          href="<%=request.getContextPath()%>/semantic-search?offset=<%=offset + 20%><%=localContext%>"><fmt:message
            key="jsp.browse.general.next" /> </a>
        <%
            }
        %>
      </td>
    </tr>
  </table>

  <%
      }
          else if (request.getAttribute("error") == null && !expression.equals(""))
          {
  %>
  <br />
  <p align="center">
    <fmt:message key="jsp.search.semantic.noresults" />
  </p>

  <%
      }
  %>

</dspace:layout>
