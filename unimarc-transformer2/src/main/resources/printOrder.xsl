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
		<xsl:text>Campo	Ordine BE	Ordine  FE	Gruppo BE	Ripetibile
</xsl:text>
		<xsl:for-each select="/metafad:mappings/metafad:mapping">
			<xsl:value-of select="@destination"/><xsl:text>	</xsl:text><xsl:value-of select="position()"/><xsl:text>	</xsl:text><xsl:value-of select="@vistaEtichette"/><xsl:text>	</xsl:text><xsl:value-of select="@group"/><xsl:text>	</xsl:text><xsl:value-of select="@multiple"/><xsl:text>
</xsl:text>				
		</xsl:for-each>
	</xsl:template>
	
</xsl:stylesheet> 	 	