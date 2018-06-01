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
		<xsl:text>Campo
</xsl:text>
		<xsl:for-each select="//metafad:record/metafad:node">
		<xsl:variable name="v"><xsl:value-of select="@name"/></xsl:variable>
			<xsl:value-of select="@name"/><xsl:text>
</xsl:text>				
		<xsl:for-each select="metafad:node|xsl:*/metafad:node">
			<xsl:variable name="v2"><xsl:value-of select="$v"/>.<xsl:value-of select="@name"/></xsl:variable>
			<xsl:value-of select="$v"/>.<xsl:value-of select="@name"/><xsl:text>
</xsl:text>				
		<xsl:for-each select="metafad:node|xsl:*/metafad:node">
			<xsl:variable name="v3"><xsl:value-of select="$v2"/>.<xsl:value-of select="@name"/></xsl:variable>
			<xsl:value-of select="$v2"/>.<xsl:value-of select="@name"/><xsl:text>
</xsl:text>				
		<xsl:for-each select="metafad:node|xsl:*/metafad:node">
			<xsl:value-of select="$v2"/>.<xsl:value-of select="@name"/><xsl:text>
</xsl:text>				
		</xsl:for-each>
		</xsl:for-each>
		</xsl:for-each>
		</xsl:for-each>
	</xsl:template>
	
</xsl:stylesheet> 	 	