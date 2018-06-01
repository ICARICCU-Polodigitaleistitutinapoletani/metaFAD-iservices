<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"   
    xmlns:metafad="http://gruppometa.it/metafad"
    version="1.0"
    >
    <xsl:output
        method="text"
        encoding="UTF-8"
    />

	
	<xsl:template match="/">
.Table Profilo
|===
|Nome del campo|Label|Multivalore|sintetica|dettaglio|Commento
		<xsl:for-each select="/metafad:mappings/metafad:mapping">
			<xsl:text>
			</xsl:text>|<xsl:value-of select="@destination"></xsl:value-of>|<xsl:value-of select="@label"></xsl:value-of>|<xsl:value-of select="@multiple"></xsl:value-of>|<xsl:choose><xsl:when test="@vistaShort and not(@vistaShort ='-1')">true</xsl:when><xsl:otherwise>false</xsl:otherwise></xsl:choose>|<xsl:choose><xsl:when test="@vistaEtichette and not(@vistaEtichette='-1')">true</xsl:when><xsl:otherwise>false</xsl:otherwise></xsl:choose>|<xsl:value-of select="@comment"></xsl:value-of>
		</xsl:for-each>
|===
	</xsl:template>
	 
	
</xsl:stylesheet> 	 	
