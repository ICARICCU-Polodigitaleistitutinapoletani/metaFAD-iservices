<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="3.0"
	xmlns:fn="http://www.w3.org/2005/xpath-functions"
	xmlns:polo="http://polodigitale.it/saxon-extension"
	exclude-result-prefixes="fn"
	xmlns="http://gruppometa.it/metafad"
	>
   <xsl:output method="xml" indent="yes"/>
   <xsl:param name="type"></xsl:param>
  
  <xsl:template match="/">
  		<records>
  		<xsl:apply-templates/>
  		</records>
  </xsl:template>
    
  
  <xsl:template match="ElementoAut/DatiElementoAut">
  		<record>
  			<xsl:attribute name="id"><xsl:value-of select="T001"/></xsl:attribute>
  			<xsl:attribute name="type"><xsl:value-of select="$type"/></xsl:attribute>
			<node name="AU">
				<node name="AUT">
					<node name="AUTN"><xsl:attribute name="value"><xsl:value-of select="T200/a_200"/>
						<!-- contiene la punteggiatura -->
						<xsl:if test="T200/b_200"><xsl:text></xsl:text><xsl:value-of select="T200/b_200"/></xsl:if>
						<!-- 
						<xsl:if test="T200/c_200"><xsl:text> &lt;</xsl:text><xsl:value-of select="T200/c_200"/><xsl:text> &gt;</xsl:text></xsl:if>
						 -->
					</xsl:attribute></node>
					<xsl:if test="@tipoNome='C' or @tipoNome='D'">
						<node name="AUTC"><xsl:attribute name="value"><xsl:value-of select="T200/a_200"/></xsl:attribute></node>
						<node name="AUTO"><xsl:attribute name="value">
							<xsl:choose>
								<xsl:when test="fn:starts-with(T200/b_200,', ')"><xsl:value-of select="fn:substring(T200/b_200,3)"/></xsl:when>
								<xsl:otherwise><xsl:value-of select="T200/b_200"/></xsl:otherwise>
							</xsl:choose> 
						</xsl:attribute></node>
					</xsl:if>									
					<node name="AUTQ"><xsl:attribute name="value"><xsl:value-of select="T200/c_200"/></xsl:attribute></node>															
					<xsl:if test="T200/f_200">
						<node name="AUTA"><xsl:attribute name="value"><xsl:value-of select="T200/f_200"/></xsl:attribute></node>
					</xsl:if>
					
				 	<node name="AUTB">
				 		<xsl:attribute name="value">
				 			<xsl:call-template name="Nome210">
				 				<xsl:with-param name="node" select="T210"/>
				 				<xsl:with-param name="tipo" select="@tipoNome"/>
				 			</xsl:call-template>
				 		</xsl:attribute>
			 		</node>
			 		<xsl:if test="T210/f_210">
			 			<node name="AUTA"><xsl:attribute name="value"><xsl:value-of select="T210/f_210"/></xsl:attribute></node>
			 		</xsl:if>
					<xsl:for-each select="//LegameElementoAut[@tipoLegame='4XX']/ElementoAutLegato/DatiElementoAut"> 
						<xsl:for-each select="T200">
							<node name="AUTV">
				 				<xsl:attribute name="value">
				 					<xsl:value-of select="a_200"/><xsl:value-of select="b_200"/>
				 				</xsl:attribute>
				 			</node>
				 		</xsl:for-each>
				 		<node name="AUTV">
				 			<xsl:attribute name="value">
				 				<xsl:call-template name="Nome210">
				 					<xsl:with-param name="node" select="T210"/>
				 					<xsl:with-param name="tipo" select="@tipoNome"/>
				 				</xsl:call-template>
				 			</xsl:attribute>
				 		</node>
					</xsl:for-each>				
				</node>
  			</node>
  		</record>  		  		  		
  </xsl:template>
  
  <xsl:template name="Nome210">
  	<xsl:param name="node"/>
  	<xsl:param name="tipo"/>
  	<!--  ac_210/ ? sembra che ci sia un gruppo ac_210 per a_210 e c_210 -->
  	<xsl:value-of select="$node/a_210"/><xsl:choose>
  		<xsl:when test="$tipo='G' and $node/a_210_G/b_210">. <xsl:value-of select="$node/a_210_G/b_210"/>&lt;<xsl:value-of select="$node/a_210_G/d_210"/> ; <xsl:value-of select="$node/a_210_G/g_210"/> ; <xsl:value-of select="$node/a_210_G/e_210"/>&gt;</xsl:when>
  		<xsl:when test="($tipo='E' or $tipo='R') and $node/d_210"> &lt;<xsl:value-of select="$node/d_210"/> ; <xsl:value-of select="$node/e_210"/> ; <xsl:value-of select="$node/e_210"/>&gt;</xsl:when>
  	</xsl:choose>
  </xsl:template>

  <xsl:template match="text()">
  </xsl:template>
  
</xsl:stylesheet>