<?xml version="1.0" encoding="UTF-8"?>
<!--



  - qdc.xsl



  - @ dc:hasVersion   1.0



  - @ dc:date         2013-10-16



  - @ dc:creator      Dimitrios Koutsomitropoulos



  - @ dc:description  XSL transformation to properly export xoai to qualified Dublin Core (QDC) XML format.



  - @ dc:rights       University of Patras, High Performance Information Systems Laboratory (HPCLab)



-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:doc="http://www.lyncode.com/xoai" xmlns:dspace-ont="http://swig.hpclab.ceid.upatras.gr/dspace-ont/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:oai="http://www.openarchives.org/OAI/2.0/" xmlns:xsd="http://http://www.w3.org/2001/XMLSchema#" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="2.0" exclude-result-prefixes="xsl oai doc ">
  <xsl:output omit-xml-declaration="yes" method="xml" indent="yes"/>
  <xsl:template match="//oai:metadata">
   <xsl:apply-templates select=".//oai:header"/>
    <xsl:apply-templates select=".//doc:element[@name='dc']"/>
    <xsl:apply-templates select=".//doc:element[@name='lom']"/>
    <xsl:apply-templates select=".//doc:element[@name='bundle' and doc:field='ORIGINAL']"/>
  </xsl:template>
  <!-- absorb unmatched text nodes-->
  <xsl:template match="text()"/>
  
<!-- header info are considered text nodes -->  
  <xsl:template match="oai:header">
 <xsl:value-of select="."/> 
  </xsl:template>
  
  <!-- General qdc element matcher. In XOAI, the parent is the lang attribute, and the two furthest ancestors are

  the qualifier and dc root element respectively. In the general case, qualifiers are absorbed by their root elements.

  In special cases (see below) qualifiers may be treated as types, as per the DCAM specs. -->
  <xsl:template match="doc:element[@name='dc']//doc:field">
    <xsl:variable name="lang" select="../@name"/>
    <xsl:variable name="qualifier" select="ancestor::doc:element[2]/@name"/>
    <xsl:variable name="element" select="ancestor::doc:element[last()-1]/@name"/>
    <xsl:variable name="value" select="."/>
    <xsl:variable name="type">
      <xsl:choose>
        <xsl:when test="$element=$qualifier"/>
        <xsl:otherwise/>
      </xsl:choose>
    </xsl:variable>
    <xsl:element name="dcterms:{$element}">
      <xsl:if test="$lang!='' and $lang!='none'">
        <xsl:attribute name="xml:lang">
          <xsl:value-of select="$lang"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="$type!=''">
        <xsl:attribute name="type">
          <xsl:value-of select="$type"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:attribute name="xsi:schemaLocation">http://purl.org/dc/terms/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dcterms.xsd http://purl.org/dc/elements/1.1/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dc.xsd</xsl:attribute>
      <xsl:value-of select="$value"/>
    </xsl:element>
  </xsl:template>
  <!-- These are not compatible with qdc. Assign own prefix (dspace-ont). Still, maintain backwards compatibility by replicating these elements
  under qdc closest matches. Should prioritize this special case-->
  <xsl:template match="doc:element[@name='sponsorship' or @name='author' or @name='statementofresponsibility']//doc:field" priority="1">
    <xsl:variable name="lang" select="../@name"/>
    <xsl:variable name="element" select="ancestor::*[2]/@name"/>
    <xsl:variable name="value" select="."/>
    <xsl:element name="dspace-ont:{$element}">
      <xsl:if test="$lang!='' and $lang!='none'">
        <xsl:attribute name="xml:lang">
          <xsl:value-of select="$lang"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:attribute name="xsi:schemaLocation">http://purl.org/dc/terms/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dcterms.xsd http://purl.org/dc/elements/1.1/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dc.xsd</xsl:attribute>
      <xsl:value-of select="$value"/>
    </xsl:element>
<xsl:choose>
    <xsl:when test="$element='author'">
    <xsl:element name="dcterms:contributor">
          <xsl:if test="$lang!='' and $lang!='none'">
        <xsl:attribute name="xml:lang">
          <xsl:value-of select="$lang"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:attribute name="xsi:schemaLocation">http://purl.org/dc/terms/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dcterms.xsd http://purl.org/dc/elements/1.1/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dc.xsd</xsl:attribute>
      <xsl:value-of select="$value"/>
    </xsl:element>   
    </xsl:when>
    <xsl:otherwise>
       <xsl:element name="dcterms:description">
          <xsl:if test="$lang!='' and $lang!='none'">
        <xsl:attribute name="xml:lang">
          <xsl:value-of select="$lang"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:attribute name="xsi:schemaLocation">http://purl.org/dc/terms/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dcterms.xsd http://purl.org/dc/elements/1.1/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dc.xsd</xsl:attribute>
      <xsl:value-of select="$value"/>
    </xsl:element>   
    </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <!-- special case for mapping identifier.citation to qdc. Should prioritize this special case-->
  <xsl:template match="doc:element[@name='citation']//doc:field" priority="1">
    <xsl:variable name="lang" select="../@name"/>
    <xsl:variable name="element" select="ancestor::*[2]/@name"/>
    <xsl:variable name="value" select="."/>
    <xsl:element name="dcterms:bibliographicCitation">
      <xsl:if test="$lang!='' and $lang!='none'">
        <xsl:attribute name="xml:lang">
          <xsl:value-of select="$lang"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:attribute name="xsi:schemaLocation">http://purl.org/dc/terms/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dcterms.xsd http://purl.org/dc/elements/1.1/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dc.xsd</xsl:attribute>
      <xsl:value-of select="$value"/>
    </xsl:element>
  </xsl:template>
  <!-- dcterms:identifier-->
  <xsl:template match="doc:element[@name='identifier']//doc:field">
    <xsl:variable name="lang" select="../@name"/>
    <xsl:variable name="qualifier" select="ancestor::doc:element[2]/@name"/>
    <xsl:variable name="element" select="ancestor::doc:element[last()-1]/@name"/>
    <xsl:variable name="value" select="."/>
    <xsl:variable name="type">
      <xsl:choose>
        <xsl:when test="$element=$qualifier"/>
        <xsl:otherwise>
          <xsl:if test="$qualifier='other'"/>
          <xsl:if test="$qualifier='uri'">
            <xsl:text>http://www.w3.org/2001/XMLSchema#anyURI</xsl:text>
          </xsl:if>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:element name="dcterms:{$element}">
      <xsl:if test="$lang!='' and $lang!='none'">
        <xsl:attribute name="xml:lang">
          <xsl:value-of select="$lang"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="$type!=''">
        <xsl:attribute name="type">
          <xsl:value-of select="$type"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:attribute name="xsi:schemaLocation">http://purl.org/dc/terms/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dcterms.xsd http://purl.org/dc/elements/1.1/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dc.xsd</xsl:attribute>
      <xsl:value-of select="$value"/>
    </xsl:element>
  </xsl:template>
  <!-- dcterms:date-->
  <xsl:template match="doc:element[@name='date']//doc:field">
    <xsl:variable name="lang" select="../@name"/>
    <xsl:variable name="qualifier" select="ancestor::doc:element[2]/@name"/>
    <xsl:variable name="element" select="ancestor::doc:element[last()-1]/@name"/>
    <xsl:variable name="value" select="."/>
    <xsl:variable name="root">
      <xsl:choose>
        <xsl:when test="$element=$qualifier">
          <xsl:value-of select="$element"/>
        </xsl:when>
        <xsl:when test="$qualifier='accessioned'">
          <xsl:text>dateAccepted</xsl:text>
        </xsl:when>
        <xsl:when test="$qualifier='available'">
          <xsl:text>available</xsl:text>
        </xsl:when>
        <xsl:when test="$qualifier='copyright'">
          <xsl:text>dateCopyrighted</xsl:text>
        </xsl:when>
        <xsl:when test="$qualifier='created'">
          <xsl:text>created</xsl:text>
        </xsl:when>
        <xsl:when test="$qualifier='issued'">
          <xsl:text>issued</xsl:text>
        </xsl:when>
        <xsl:when test="$qualifier='submitted'">
          <xsl:text>dateSubmitted</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$element"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:element name="dcterms:{$root}">
      <xsl:if test="$lang!='' and $lang!='none'">
        <xsl:attribute name="xml:lang">
          <xsl:value-of select="$lang"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:attribute name="xsi:schemaLocation">http://purl.org/dc/terms/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dcterms.xsd http://purl.org/dc/elements/1.1/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dc.xsd</xsl:attribute>
      <xsl:value-of select="$value"/>
    </xsl:element>
  </xsl:template>
  <!-- dcterms:description-->
  <xsl:template match="doc:element[@name='description']//doc:field">
    <xsl:variable name="lang" select="../@name"/>
    <xsl:variable name="qualifier" select="ancestor::doc:element[2]/@name"/>
    <xsl:variable name="element" select="ancestor::doc:element[last()-1]/@name"/>
    <xsl:variable name="value" select="."/>
    <xsl:variable name="root">
      <xsl:choose>
        <xsl:when test="$element=$qualifier">
          <xsl:value-of select="$element"/>
        </xsl:when>
        <xsl:when test="$qualifier='abstract'">
          <xsl:text>abstract</xsl:text>
        </xsl:when>
        <xsl:when test="$qualifier='provenance'">
          <xsl:text>provenance</xsl:text>
        </xsl:when>
        <xsl:when test="$qualifier='tableofcontents'">
          <xsl:text>tableOfContents</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$element"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="type">
      <xsl:if test="$qualifier='uri'">
        <xsl:text>http://www.w3.org/2001/XMLSchema#anyURI</xsl:text>
      </xsl:if>
    </xsl:variable>
    <xsl:element name="dcterms:{$root}">
      <xsl:if test="$lang!='' and $lang!='none'">
        <xsl:attribute name="xml:lang">
          <xsl:value-of select="$lang"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="$type!=''">
        <xsl:attribute name="type">
          <xsl:value-of select="$type"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:attribute name="xsi:schemaLocation">http://purl.org/dc/terms/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dcterms.xsd http://purl.org/dc/elements/1.1/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dc.xsd</xsl:attribute>
      <xsl:value-of select="$value"/>
    </xsl:element>
  </xsl:template>
  <!-- dcterms:format-->
  <xsl:template match="doc:element[@name='format']//doc:field">
    <xsl:variable name="lang" select="../@name"/>
    <xsl:variable name="qualifier" select="ancestor::doc:element[2]/@name"/>
    <xsl:variable name="element" select="ancestor::doc:element[last()-1]/@name"/>
    <xsl:variable name="value" select="."/>
    <xsl:variable name="root">
      <xsl:choose>
        <xsl:when test="$element=$qualifier">
          <xsl:value-of select="$element"/>
        </xsl:when>
        <xsl:when test="$qualifier='extent'">
          <xsl:text>extent</xsl:text>
        </xsl:when>
        <xsl:when test="$qualifier='medium'">
          <xsl:text>medium</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$element"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="type">
      <xsl:if test="$qualifier='mimetype'">
        <xsl:text>dspace-ont:MimeType</xsl:text>
      </xsl:if>
    </xsl:variable>
    <xsl:element name="dcterms:{$root}">
      <xsl:if test="$lang!='' and $lang!='none'">
        <xsl:attribute name="xml:lang">
          <xsl:value-of select="$lang"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="$type!=''">
        <xsl:attribute name="type">
          <xsl:value-of select="$type"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:attribute name="xsi:schemaLocation">http://purl.org/dc/terms/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dcterms.xsd http://purl.org/dc/elements/1.1/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dc.xsd</xsl:attribute>
      <xsl:value-of select="$value"/>
    </xsl:element>
  </xsl:template>
  <!--There are some format info in the item's bitstream metadata, so consider them as well-->
  <xsl:template match="doc:element[@name='bundle' and doc:field='ORIGINAL']">
    <xsl:variable name="format" select="//doc:field[@name='format']"/>
    <xsl:variable name="extent" select="//doc:field[@name='size']"/>
    <xsl:if test="$format!=''">
      <xsl:element name="dcterms:format">
        <xsl:attribute name="type">
          <xsl:text>dspace-ont:MimeType</xsl:text>
        </xsl:attribute>
        <xsl:value-of select="$format"/>
      </xsl:element>
    </xsl:if>
    <xsl:if test="$extent!=''">
      <xsl:element name="dcterms:extent">
        <xsl:value-of select="$extent"/>
      </xsl:element>
    </xsl:if>
  </xsl:template>
  <!-- dcterms:language-->
  <xsl:template match="doc:element[@name='language']//doc:field">
    <xsl:variable name="lang" select="../@name"/>
    <xsl:variable name="qualifier" select="ancestor::doc:element[2]/@name"/>
    <xsl:variable name="element" select="ancestor::doc:element[last()-1]/@name"/>
    <xsl:variable name="value" select="."/>
    <xsl:variable name="type">
      <xsl:choose>
        <xsl:when test="$element=$qualifier"/>
        <xsl:otherwise>
          <xsl:if test="$qualifier='iso'">
            <xsl:text>http://www.w3.org/2001/XMLSchema#language</xsl:text>
          </xsl:if>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:element name="dcterms:{$element}">
      <xsl:if test="$lang!='' and $lang!='none'">
        <xsl:attribute name="xml:lang">
          <xsl:value-of select="$lang"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="$type!=''">
        <xsl:attribute name="type">
          <xsl:value-of select="$type"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:attribute name="xsi:schemaLocation">http://purl.org/dc/terms/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dcterms.xsd http://purl.org/dc/elements/1.1/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dc.xsd</xsl:attribute>
      <xsl:value-of select="$value"/>
    </xsl:element>
  </xsl:template>
  <!--dcterms:coverage-->
  <xsl:template match="doc:element[@name='coverage']//doc:field">
    <xsl:variable name="lang" select="../@name"/>
    <xsl:variable name="qualifier" select="ancestor::doc:element[2]/@name"/>
    <xsl:variable name="element" select="ancestor::doc:element[last()-1]/@name"/>
    <xsl:variable name="value" select="."/>
    <xsl:variable name="root">
      <xsl:choose>
        <xsl:when test="$element=$qualifier">
          <xsl:value-of select="$element"/>
        </xsl:when>
        <xsl:when test="$qualifier='spatial'">
          <xsl:text>spatial</xsl:text>
        </xsl:when>
        <xsl:when test="$qualifier='temporal'">
          <xsl:text>temporal</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$element"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:element name="dcterms:{$root}">
      <xsl:if test="$lang!='' and $lang!='none'">
        <xsl:attribute name="xml:lang">
          <xsl:value-of select="$lang"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:attribute name="xsi:schemaLocation">http://purl.org/dc/terms/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dcterms.xsd http://purl.org/dc/elements/1.1/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dc.xsd</xsl:attribute>
      <xsl:value-of select="$value"/>
    </xsl:element>
  </xsl:template>
  <!--dcterms:relation-->
  <xsl:template match="doc:element[@name='relation']//doc:field">
    <xsl:variable name="lang" select="../@name"/>
    <xsl:variable name="qualifier" select="ancestor::doc:element[2]/@name"/>
    <xsl:variable name="element" select="ancestor::doc:element[last()-1]/@name"/>
    <xsl:variable name="value" select="."/>
    <xsl:variable name="root">
      <xsl:choose>
        <xsl:when test="$element=$qualifier">
          <xsl:value-of select="$element"/>
        </xsl:when>
        <xsl:when test="$qualifier='isformatof'">
          <xsl:text>isFormatOf</xsl:text>
        </xsl:when>
        <xsl:when test="$qualifier='ispartof'">
          <xsl:text>isPartOf</xsl:text>
        </xsl:when>
        <xsl:when test="$qualifier='ispartofseries'">
          <xsl:text>isPartOf</xsl:text>
        </xsl:when>
        <xsl:when test="$qualifier='haspart'">
          <xsl:text>hasPart</xsl:text>
        </xsl:when>
        <xsl:when test="$qualifier='isversionof'">
          <xsl:text>isVersionOf</xsl:text>
        </xsl:when>
        <xsl:when test="$qualifier='hasversion'">
          <xsl:text>hasVersion</xsl:text>
        </xsl:when>
        <xsl:when test="$qualifier='isreferencedby'">
          <xsl:text>isReferencedBy</xsl:text>
        </xsl:when>
        <xsl:when test="$qualifier='requires'">
          <xsl:text>requires</xsl:text>
        </xsl:when>
        <xsl:when test="$qualifier='replaces'">
          <xsl:text>replaces</xsl:text>
        </xsl:when>
        <xsl:when test="$qualifier='isreplacedby'">
          <xsl:text>isReplacedBy</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$element"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="type">
      <xsl:if test="$qualifier='uri'">
        <xsl:text>http://www.w3.org/2001/XMLSchema#anyURI</xsl:text>
      </xsl:if>
    </xsl:variable>
    <xsl:element name="dcterms:{$root}">
      <xsl:if test="$lang!='' and $lang!='none'">
        <xsl:attribute name="xml:lang">
          <xsl:value-of select="$lang"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="$type!=''">
        <xsl:attribute name="type">
          <xsl:value-of select="$type"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:attribute name="xsi:schemaLocation">http://purl.org/dc/terms/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dcterms.xsd http://purl.org/dc/elements/1.1/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dc.xsd</xsl:attribute>
      <xsl:value-of select="$value"/>
    </xsl:element>
  </xsl:template>
  <!--dcterms:rights, dcterms:source-->
  <xsl:template match="doc:element[@name='rights' or @name='source']//doc:field">
    <xsl:variable name="lang" select="../@name"/>
    <xsl:variable name="qualifier" select="ancestor::doc:element[2]/@name"/>
    <xsl:variable name="element" select="ancestor::doc:element[last()-1]/@name"/>
    <xsl:variable name="value" select="."/>
    <xsl:variable name="type">
      <xsl:choose>
        <xsl:when test="$element=$qualifier"/>
        <xsl:otherwise>
          <xsl:if test="$qualifier='uri'">
            <xsl:text>http://www.w3.org/2001/XMLSchema#anyURI</xsl:text>
          </xsl:if>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:element name="dcterms:{$element}">
      <xsl:if test="$lang!='' and $lang!='none'">
        <xsl:attribute name="xml:lang">
          <xsl:value-of select="$lang"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="$type!=''">
        <xsl:attribute name="type">
          <xsl:value-of select="$type"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:attribute name="xsi:schemaLocation">http://purl.org/dc/terms/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dcterms.xsd http://purl.org/dc/elements/1.1/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dc.xsd</xsl:attribute>
      <xsl:value-of select="$value"/>
    </xsl:element>
  </xsl:template>
  <!--dcterms:subject-->
  <xsl:template match="doc:element[@name='subject']//doc:field">
    <xsl:variable name="lang" select="../@name"/>
    <xsl:variable name="qualifier" select="ancestor::doc:element[2]/@name"/>
    <xsl:variable name="element" select="ancestor::doc:element[last()-1]/@name"/>
    <xsl:variable name="value" select="."/>
    <xsl:variable name="type">
      <xsl:choose>
        <xsl:when test="$element=$qualifier"/>
        <xsl:otherwise>
          <xsl:if test="$qualifier='ddc'">
            <xsl:text>dcterms:DDC</xsl:text>
          </xsl:if>
          <xsl:if test="$qualifier='lcc'">
            <xsl:text>dcterms:LCC</xsl:text>
          </xsl:if>
          <xsl:if test="$qualifier='lcsh'">
            <xsl:text>dcterms:LCSH</xsl:text>
          </xsl:if>
          <xsl:if test="$qualifier='mesh'">
            <xsl:text>dcterms:MESH</xsl:text>
          </xsl:if>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:element name="dcterms:{$element}">
      <xsl:if test="$lang!='' and $lang!='none'">
        <xsl:attribute name="xml:lang">
          <xsl:value-of select="$lang"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="$type!=''">
        <xsl:attribute name="type">
          <xsl:value-of select="$type"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:attribute name="xsi:schemaLocation">http://purl.org/dc/terms/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dcterms.xsd http://purl.org/dc/elements/1.1/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dc.xsd</xsl:attribute>
      <xsl:value-of select="$value"/>
    </xsl:element>
  </xsl:template>
  <!--dcterms:title-->
  <xsl:template match="doc:element[@name='title']//doc:field">
    <xsl:variable name="lang" select="../@name"/>
    <xsl:variable name="qualifier" select="ancestor::doc:element[2]/@name"/>
    <xsl:variable name="element" select="ancestor::doc:element[last()-1]/@name"/>
    <xsl:variable name="value" select="."/>
    <xsl:variable name="root">
      <xsl:choose>
        <xsl:when test="$element=$qualifier">
          <xsl:value-of select="$element"/>
        </xsl:when>
        <xsl:when test="$qualifier='alternative'">
          <xsl:text>alternative</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$element"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:element name="dcterms:{$root}">
      <xsl:if test="$lang!='' and $lang!='none'">
        <xsl:attribute name="xml:lang">
          <xsl:value-of select="$lang"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:attribute name="xsi:schemaLocation">http://purl.org/dc/terms/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dcterms.xsd http://purl.org/dc/elements/1.1/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dc.xsd</xsl:attribute>
      <xsl:value-of select="$value"/>
    </xsl:element>
  </xsl:template>
  <!--dcterms:type-->
  <xsl:template match="doc:element[@name='type']//doc:field">
    <xsl:variable name="lang" select="../@name"/>
    <xsl:variable name="qualifier" select="ancestor::doc:element[2]/@name"/>
    <xsl:variable name="element" select="ancestor::doc:element[last()-1]/@name"/>
    <xsl:variable name="value" select="."/>
    <xsl:variable name="type">
      <xsl:text>dspace-ont:DspaceType</xsl:text>
    </xsl:variable>
    <xsl:element name="dcterms:{$element}">
      <xsl:if test="$lang!='' and $lang!='none'">
        <xsl:attribute name="xml:lang">
          <xsl:value-of select="$lang"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="$type!=''">
        <xsl:attribute name="type">
          <xsl:value-of select="$type"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:attribute name="xsi:schemaLocation">http://purl.org/dc/terms/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dcterms.xsd http://purl.org/dc/elements/1.1/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dc.xsd</xsl:attribute>
      <xsl:value-of select="$value"/>
    </xsl:element>
  </xsl:template>
  <!-- LOM -->
  <xsl:template match="doc:element[@name='intendedenduserrole']//doc:field">
    <xsl:variable name="lang" select="../@name"/>
    <xsl:variable name="value" select="."/>
    <xsl:element name="dcterms:audience">
      <xsl:if test="$lang!='' and $lang!='none'">
        <xsl:attribute name="xml:lang">
          <xsl:value-of select="$lang"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:attribute name="type">
        <xsl:text>lom:IntendedEndUserRole</xsl:text>
      </xsl:attribute>
      <xsl:attribute name="xsi:schemaLocation">http://purl.org/dc/terms/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dcterms.xsd http://purl.org/dc/elements/1.1/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dc.xsd</xsl:attribute>
      <xsl:value-of select="$value"/>
    </xsl:element>
  </xsl:template>
  <xsl:template match="doc:element[@name='context']//doc:field">
    <xsl:variable name="lang" select="../@name"/>
    <xsl:variable name="value" select="."/>
    <xsl:element name="dcterms:educationLevel">
      <xsl:if test="$lang!='' and $lang!='none'">
        <xsl:attribute name="xml:lang">
          <xsl:value-of select="$lang"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:attribute name="type">
        <xsl:text>lom:Context</xsl:text>
      </xsl:attribute>
      <xsl:attribute name="xsi:schemaLocation">http://purl.org/dc/terms/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dcterms.xsd http://purl.org/dc/elements/1.1/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dc.xsd</xsl:attribute>
      <xsl:value-of select="$value"/>
    </xsl:element>
  </xsl:template>
  <xsl:template match="doc:element[@name='difficulty']//doc:field">
    <xsl:variable name="lang" select="../@name"/>
    <xsl:variable name="value" select="."/>
    <xsl:element name="dcterms:type">
      <xsl:if test="$lang!='' and $lang!='none'">
        <xsl:attribute name="xml:lang">
          <xsl:value-of select="$lang"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:attribute name="type">
        <xsl:text>lom:Difficulty</xsl:text>
      </xsl:attribute>
      <xsl:attribute name="xsi:schemaLocation">http://purl.org/dc/terms/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dcterms.xsd http://purl.org/dc/elements/1.1/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dc.xsd</xsl:attribute>
      <xsl:value-of select="$value"/>
    </xsl:element>
  </xsl:template>
  <xsl:template match="doc:element[@name='interactivitytype']//doc:field">
    <xsl:variable name="lang" select="../@name"/>
    <xsl:variable name="value" select="."/>
    <xsl:element name="dcterms:instructionalMethod">
      <xsl:if test="$lang!='' and $lang!='none'">
        <xsl:attribute name="xml:lang">
          <xsl:value-of select="$lang"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:attribute name="type">
        <xsl:text>lom:InteractivityType</xsl:text>
      </xsl:attribute>
      <xsl:attribute name="xsi:schemaLocation">http://purl.org/dc/terms/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dcterms.xsd http://purl.org/dc/elements/1.1/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dc.xsd</xsl:attribute>
      <xsl:value-of select="$value"/>
    </xsl:element>
  </xsl:template>
  <xsl:template match="doc:element[@name='learningresourcetype']//doc:field">
    <xsl:variable name="lang" select="../@name"/>
    <xsl:variable name="value" select="."/>
    <xsl:element name="dcterms:type">
      <xsl:if test="$lang!='' and $lang!='none'">
        <xsl:attribute name="xml:lang">
          <xsl:value-of select="$lang"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:attribute name="type">
        <xsl:text>lom:LearningResourceType</xsl:text>
      </xsl:attribute>
      <xsl:attribute name="xsi:schemaLocation">http://purl.org/dc/terms/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dcterms.xsd http://purl.org/dc/elements/1.1/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dc.xsd</xsl:attribute>
      <xsl:value-of select="$value"/>
    </xsl:element>
  </xsl:template>
  <xsl:template match="doc:element[@name='typicallearningtime']//doc:field">
    <xsl:variable name="lang" select="../@name"/>
    <xsl:variable name="value" select="."/>
    <xsl:element name="dcterms:extent">
      <xsl:if test="$lang!='' and $lang!='none'">
        <xsl:attribute name="xml:lang">
          <xsl:value-of select="$lang"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:attribute name="type">
        <xsl:text>lom:TypicalLearningTime</xsl:text>
      </xsl:attribute>
      <xsl:attribute name="xsi:schemaLocation">http://purl.org/dc/terms/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dcterms.xsd http://purl.org/dc/elements/1.1/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dc.xsd</xsl:attribute>
      <xsl:value-of select="$value"/>
    </xsl:element>
  </xsl:template>
  <xsl:template match="doc:element[@name='status']//doc:field">
    <xsl:variable name="lang" select="../@name"/>
    <xsl:variable name="value" select="."/>
    <xsl:element name="dcterms:type">
      <xsl:if test="$lang!='' and $lang!='none'">
        <xsl:attribute name="xml:lang">
          <xsl:value-of select="$lang"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:attribute name="type">
        <xsl:text>lom:Status</xsl:text>
      </xsl:attribute>
      <xsl:attribute name="xsi:schemaLocation">http://purl.org/dc/terms/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dcterms.xsd http://purl.org/dc/elements/1.1/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dc.xsd</xsl:attribute>
      <xsl:value-of select="$value"/>
    </xsl:element>
  </xsl:template>
  <xsl:template match="doc:element[@name='version']//doc:field">
    <xsl:variable name="lang" select="../@name"/>
    <xsl:variable name="value" select="."/>
    <xsl:element name="dcterms:hasVersion">
      <xsl:if test="$lang!='' and $lang!='none'">
        <xsl:attribute name="xml:lang">
          <xsl:value-of select="$lang"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:attribute name="xsi:schemaLocation">http://purl.org/dc/terms/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dcterms.xsd http://purl.org/dc/elements/1.1/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dc.xsd</xsl:attribute>
      <xsl:value-of select="$value"/>
    </xsl:element>
  </xsl:template>
  <xsl:template match="doc:element[@name='annotation']//doc:field">
    <xsl:variable name="lang" select="../@name"/>
    <xsl:variable name="value" select="."/>
    <xsl:element name="dcterms:description">
      <xsl:if test="$lang!='' and $lang!='none'">
        <xsl:attribute name="xml:lang">
          <xsl:value-of select="$lang"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:attribute name="xsi:schemaLocation">http://purl.org/dc/terms/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dcterms.xsd http://purl.org/dc/elements/1.1/ http://dublincore.org/schemas/xmls/qdc/2006/01/06/dc.xsd</xsl:attribute>
      <xsl:value-of select="$value"/>
    </xsl:element>
  </xsl:template>
</xsl:stylesheet>
