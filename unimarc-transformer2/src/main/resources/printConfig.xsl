<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"   
    xmlns:metafad="http://gruppometa.it/metafad"
    version="1.0"
    >
    <xsl:output
        method="xml"
        encoding="UTF-8"
    />

	
	<xsl:template match="/">
	<html>
		<table border="1">
		<tr><th>Campo</th><th>sotto-campo</th><th>section</th><th>inizio</th><th>fine</th><th>Vocabolario</th><th>Commento</th></tr>
		<xsl:for-each select="/metafad:mappings/metafad:mapping">
			<tr><td><xsl:value-of select="@destination"></xsl:value-of></td><td></td><td><xsl:value-of select="@marcSection"></xsl:value-of></td><td><xsl:value-of select="@posInit"></xsl:value-of></td><td><xsl:value-of select="@posEnd"></xsl:value-of></td><td><xsl:value-of select="@vocabulary"></xsl:value-of></td><td><xsl:value-of select="@comment"></xsl:value-of></td>				
			</tr><xsl:text>
			</xsl:text>
			<xsl:for-each select="metafad:mapping">
			<tr><td><xsl:value-of select="../@destination"></xsl:value-of></td><td><xsl:value-of select="@destination"></xsl:value-of></td><td><xsl:value-of select="@marcSection"></xsl:value-of></td><td><xsl:value-of select="@posInit"></xsl:value-of></td><td><xsl:value-of select="@posEnd"></xsl:value-of></td><td><xsl:value-of select="@vocabulary"></xsl:value-of></td><td></td><td><xsl:value-of select="@comment"></xsl:value-of></td>				
			</tr><xsl:text>
			</xsl:text>
			</xsl:for-each>
		</xsl:for-each>
		</table>
	</html>
	</xsl:template>
	 
	
</xsl:stylesheet> 	 	