<?xml version="1.0"?>
<xsl:stylesheet xmlns:mods="http://www.loc.gov/mods/v3"
    xmlns="http://www.iccu.sbn.it/opencms/opencms/documenti/2016/SBNMarcv202.xsd"
	xmlns:marc="http://www.loc.gov/MARC21/slim"
	xmlns:xlink="http://www.w3.org/1999/xlink" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fn="http://www.w3.org/2005/xpath-functions"	
	exclude-result-prefixes="xlink marc" version="1.0">
	<xsl:output indent="yes"/>
  <xsl:template match="/">
  		<SBNMarc schemaVersion="2.00"><SbnUser>
  		<Biblioteca>NAP PM</Biblioteca>
  		<UserId>pmdigi</UserId></SbnUser>
  		<SbnMessage><SbnResponse>
  		<SbnResult><esito>0000</esito><testoEsito>OK</testoEsito>
  		</SbnResult><SbnOutput totRighe="1" tipoOrd="1" tipoOutput="000"><Documento nLista="0">
  			<xsl:apply-templates/>
  		</Documento>
  		</SbnOutput>
  		</SbnResponse>
  		</SbnMessage>
  		</SBNMarc>
  		
  </xsl:template>
  
  <xsl:template match="marc:record">
  	<DatiDocumento xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
				   livelloAutDoc="51"
				   Condiviso="s" livelloAut="51">
		<xsl:variable name="level"><xsl:value-of select="fn:substring(marc:leader,8,1)"/></xsl:variable>
		<xsl:variable name="tipo"><xsl:value-of select="fn:substring(marc:leader,7,1)"/></xsl:variable>
		<xsl:attribute name="naturaDoc"><xsl:choose>
			<xsl:when test="$level eq 'm'">M</xsl:when>
			<xsl:when test="$level eq 'a'">N</xsl:when>
			<xsl:when test="$level eq 's'">S</xsl:when>
		</xsl:choose></xsl:attribute>
		<xsl:attribute name="tipoMateriale"><xsl:value-of select="$tipo"/></xsl:attribute>
		<guida>
			<xsl:attribute name="livelloBibliografico"><xsl:value-of select="$level"/></xsl:attribute>
			<xsl:attribute name="tipoRecord"><xsl:value-of select="$tipo"/></xsl:attribute>
		</guida>
		<xsl:apply-templates/>

	</DatiDocumento>
	  <LegamiDocumento>
		  <idPartenza><xsl:value-of select="marc:controlfield[@tag='001']"/></idPartenza>
		  <xsl:for-each select="marc:datafield">
			  <xsl:if test="fn:matches(@tag,'7..')">
				  <ArrivoLegame>
					  <LegameElementoAut tipoAuthority="AU">
							<xsl:attribute name="tipoLegame"><xsl:value-of select="@tag"/></xsl:attribute>
							<xsl:attribute name="relatorCode"><xsl:value-of select="marc:subfield[@code='4']"/></xsl:attribute>
						  <idArrivo><xsl:value-of select="marc:subfield[@code='3']"/></idArrivo>
						  <ElementoAutLegato>
							  <DatiElementoAut tipoAuthority="AU">
								  	<T200>
										<xsl:for-each select="marc:subfield">
											<xsl:variable name="name3"><xsl:value-of select="@code"/>_200</xsl:variable>
											<!-- campi senza numeri-->
											<xsl:choose>
												<xsl:when test="not(fn:matches($name3,'^[0-9].*'))">
														<xsl:call-template name="printValue">
															<xsl:with-param name="tag">200</xsl:with-param>
															<xsl:with-param name="code"><xsl:value-of select="@code"/></xsl:with-param>
															<xsl:with-param name="value"><xsl:value-of select="."/></xsl:with-param>
														</xsl:call-template>
												</xsl:when>
												<xsl:otherwise>
												</xsl:otherwise>
											</xsl:choose>
										</xsl:for-each>
									</T200>
							  </DatiElementoAut>
						  </ElementoAutLegato>
					  </LegameElementoAut>
				  </ArrivoLegame>
			  </xsl:if>
		  </xsl:for-each>
	  </LegamiDocumento>
	  <LegamiDocumento>
		  <idPartenza><xsl:value-of select="marc:controlfield[@tag='001']"/></idPartenza>
		  <xsl:for-each select="marc:datafield">
			  <xsl:if test="fn:matches(@tag,'6..')">
				  <ArrivoLegame>
					  <LegameElementoAut tipoAuthority="CL">
						  <xsl:attribute name="tipoLegame"><xsl:value-of select="@tag"/></xsl:attribute>
						  <idArrivo><xsl:value-of select="marc:subfield[@code='3']"/></idArrivo>
						  <ElementoAutLegato>
							  <DatiElementoAut tipoAuthority="CL">
								  <T250>
									  <xsl:for-each select="marc:subfield">
										  <xsl:variable name="name3"><xsl:value-of select="@code"/>250</xsl:variable>
										  <!-- campi senza numeri-->
										  <xsl:choose>
											  <xsl:when test="not(fn:matches($name3,'^[0-9].*'))">
													  <xsl:call-template name="printValue">
														  <xsl:with-param name="tag">250</xsl:with-param>
														  <xsl:with-param name="code"><xsl:value-of select="@code"/></xsl:with-param>
														  <xsl:with-param name="value"><xsl:value-of select="."/></xsl:with-param>
													  </xsl:call-template>
											  </xsl:when>
											  <xsl:otherwise>
											  </xsl:otherwise>
										  </xsl:choose>
									  </xsl:for-each>
								  </T250>
							  </DatiElementoAut>
						  </ElementoAutLegato>
					  </LegameElementoAut>
				  </ArrivoLegame>
			  </xsl:if>
		  </xsl:for-each>
	  </LegamiDocumento>
	  <LegamiDocumento>
		  <idPartenza><xsl:value-of select="marc:controlfield[@tag='001']"/></idPartenza>
		  <xsl:for-each select="marc:datafield">
			  <xsl:if test="fn:matches(@tag,'5..')">
				  <ArrivoLegame>
					  <LegameElementoAut tipoAuthority="TU">
						  <xsl:attribute name="tipoLegame"><xsl:value-of select="@tag"/></xsl:attribute>
						  <idArrivo><xsl:value-of select="marc:subfield[@code='3']"/></idArrivo>
						  <ElementoAutLegato>
							  <DatiElementoAut tipoAuthority="TU" livelloAut="51">
								  <T230>
									  <xsl:for-each select="marc:subfield">
										  <xsl:variable name="name3"><xsl:value-of select="@code"/>230</xsl:variable>
										  <!-- campi senza numeri-->
										  <xsl:choose>
											  <xsl:when test="not(fn:matches($name3,'^[0-9].*'))">
												  <xsl:call-template name="printValue">
													  <xsl:with-param name="tag">250</xsl:with-param>
													  <xsl:with-param name="code"><xsl:value-of select="@code"/></xsl:with-param>
													  <xsl:with-param name="value"><xsl:value-of select="."/></xsl:with-param>
												  </xsl:call-template>
											  </xsl:when>
											  <xsl:otherwise>
											  </xsl:otherwise>
										  </xsl:choose>
									  </xsl:for-each>
								  </T230>
							  </DatiElementoAut>
						  </ElementoAutLegato>
					  </LegameElementoAut>
				  </ArrivoLegame>
			  </xsl:if>
		  </xsl:for-each>
	  </LegamiDocumento>
	  <LegamiDocumento>
		  <idPartenza><xsl:value-of select="marc:controlfield[@tag='001']"/></idPartenza>
		  <xsl:for-each select="marc:datafield">
			  <xsl:if test="fn:matches(@tag,'4..')">
				  <ArrivoLegame>
					  <LegameDoc>
						  <xsl:attribute name="tipoLegame"><xsl:value-of select="@tag"/></xsl:attribute>
						  <xsl:variable name="bid"><xsl:choose>
								  <xsl:when test="fn:substring((marc:subfield[@code='1'])[1],1,3) eq '001' "><xsl:value-of select="fn:substring((marc:subfield[@code='1'])[1],4)"/></xsl:when>
								  <xsl:otherwise></xsl:otherwise>
							  </xsl:choose></xsl:variable>
						  <xsl:if test="fn:substring((marc:subfield[@code='1'])[1],1,3) eq '001'">
						  	<idArrivo><xsl:value-of select="$bid"/></idArrivo>
						  </xsl:if>
						  <xsl:variable name="tagg">T<xsl:choose>
							  <xsl:when test="count(marc:subfield[@code='1']) lt 2"><xsl:value-of select="fn:substring((marc:subfield[@code='1'])[1],1,3)"/></xsl:when>
							  <xsl:otherwise><xsl:value-of select="fn:substring((marc:subfield[@code='1'])[2],1,3)"/></xsl:otherwise>
						  </xsl:choose></xsl:variable>
						  <DocumentoLegato>
							  <DatiDocumento livelloAutDoc="71">
								  <T001><xsl:value-of select="$bid"/></T001>
								  <xsl:element name="{$tagg}">
									  <xsl:for-each select="marc:subfield">
										  <xsl:if test="fn:substring((preceding-sibling::marc:subfield[@code='1'])[last()],1,3) eq '200' ">
											  <xsl:variable name="name3"><xsl:value-of select="@code"/>200</xsl:variable>
											  <!-- campi senza numeri-->
											  <xsl:choose>
												  <xsl:when test="not(fn:matches($name3,'^[0-9].*'))">
													  <xsl:call-template name="printValue">
														  <xsl:with-param name="tag"><xsl:value-of select="fn:substring($tagg,2)"/></xsl:with-param>
														  <xsl:with-param name="code"><xsl:value-of select="@code"/></xsl:with-param>
														  <xsl:with-param name="value"><xsl:value-of select="."/></xsl:with-param>
													  </xsl:call-template>
												  </xsl:when>
												  <xsl:otherwise>
												  </xsl:otherwise>
											  </xsl:choose>
										  </xsl:if>
									  </xsl:for-each>
								  </xsl:element>
							  </DatiDocumento>
						  </DocumentoLegato>
					  </LegameDoc>
				  </ArrivoLegame>
			  </xsl:if>
		  </xsl:for-each>
	  </LegamiDocumento>
  </xsl:template>
  <xsl:template match="marc:leader">
  </xsl:template>
  <xsl:template match="marc:controlfield">
  	<xsl:variable name="name2">T<xsl:value-of select="@tag"/></xsl:variable>
	<xsl:element name="{$name2}">
	 	<xsl:value-of select="."></xsl:value-of> 
  	</xsl:element>
  </xsl:template>
  <xsl:template match="marc:datafield[not(fn:matches(@tag,'7..')) and not(fn:matches(@tag,'6..')) and not(fn:matches(@tag,'5..')) and not(fn:matches(@tag,'4..')) ]">
  	<xsl:variable name="a3xx"><xsl:value-of select="fn:matches(@tag,'3..')"/></xsl:variable>
  	<xsl:variable name="tagParent"><xsl:value-of select="@tag"/></xsl:variable>
  	<xsl:variable name="tag2"><xsl:choose>
  			<xsl:when test="$a3xx='true'">3XX</xsl:when>
  			<xsl:otherwise><xsl:value-of select="@tag"/></xsl:otherwise>
  		</xsl:choose></xsl:variable>  		  	
  	<xsl:variable name="name2">T<xsl:value-of select="$tag2"/></xsl:variable>
	<xsl:element name="{$name2}">
		<xsl:if test="$a3xx ='true'"><xsl:attribute name="tipoNota"><xsl:value-of select="@tag"/></xsl:attribute></xsl:if>
		<xsl:attribute name="id1"><xsl:value-of select="@ind1"/></xsl:attribute>
		<xsl:attribute name="id2"><xsl:value-of select="@ind2"/></xsl:attribute>
		<xsl:for-each select="marc:subfield">
  			<xsl:variable name="name3"><xsl:value-of select="@code"/><xsl:value-of select="$tag2"/></xsl:variable>
			<!-- campi senza numeri-->
			<xsl:choose>
				<xsl:when test="not(fn:matches($name3,'^[0-9].*'))">
						<xsl:call-template name="printValue">
							<xsl:with-param name="tag"><xsl:value-of select="$tag2"/></xsl:with-param>
							<xsl:with-param name="code"><xsl:value-of select="@code"/></xsl:with-param>
							<xsl:with-param name="value"><xsl:value-of select="."/></xsl:with-param>
						</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
				</xsl:otherwise>
			</xsl:choose>
  		</xsl:for-each>
  	</xsl:element>
  </xsl:template>
  
  <xsl:template name="printValue">
  	<xsl:param name="tag"></xsl:param>
  	<xsl:param name="code"></xsl:param>
  	<xsl:param name="value"></xsl:param>
	  <xsl:variable name="nameHere"><xsl:value-of select="$code"/>_<xsl:value-of select="$tag"/></xsl:variable>
  	<xsl:choose>
  		<xsl:when test="$tag='100' and $code='a'">
  			<a_100_0><xsl:value-of select="fn:substring($value,1,9)"/></a_100_0>
  			<a_100_8><xsl:value-of select="fn:substring($value,9,1)"/></a_100_8>
  			<a_100_9><xsl:value-of select="fn:substring($value,10,4)"/></a_100_9>
			<a_100_13><xsl:value-of select="fn:substring($value,14,4)"/></a_100_13>
  		</xsl:when>
		<xsl:when test="$tag='105' and $code='a'">
			<a_105_11><xsl:value-of select="fn:substring($value,12,1)"/></a_105_11>
		</xsl:when>
		<xsl:when test="$tag='116' and $code='a'">
			<a_116_0><xsl:value-of select="fn:substring($value,1,1)"/></a_116_0>
			<a_116_1><xsl:value-of select="fn:substring($value,2,1)"/></a_116_1>
			<a_116_3><xsl:value-of select="fn:substring($value,4,1)"/></a_116_3>
			<a_116_4><xsl:value-of select="fn:substring($value,5,2)"/></a_116_4>
			<a_116_6><xsl:value-of select="fn:substring($value,7,2)"/></a_116_6>
			<a_116_8><xsl:value-of select="fn:substring($value,9,2)"/></a_116_8>
			<a_116_10><xsl:value-of select="fn:substring($value,11,2)"/></a_116_10>
			<a_116_12><xsl:value-of select="fn:substring($value,13,2)"/></a_116_12>
			<a_116_14><xsl:value-of select="fn:substring($value,15,2)"/></a_116_14>
			<a_116_16><xsl:value-of select="fn:substring($value,17,2)"/></a_116_16>
		</xsl:when>
		<xsl:when test="$tag='140' and $code='a'">
			<a_140_17><xsl:value-of select="fn:substring($value,18,2)"/></a_140_17>
		</xsl:when>
		<xsl:when test="$tag='950' and $code='d'">
			<d_950_0><xsl:value-of select="fn:substring($value,1,4)"/></d_950_0>
			<d_950_3><xsl:value-of select="fn:substring($value,4,9)"/></d_950_3>
			<d_950_13><xsl:value-of select="fn:substring($value,14,23)"/></d_950_13>
			<d_950_37><xsl:value-of select="fn:substring($value,38)"/></d_950_37>
		</xsl:when>
		<xsl:when test="$tag='950' and $code='e'">
			<e_950_0><xsl:value-of select="fn:substring($value,1,4)"/></e_950_0>
			<e_950_3><xsl:value-of select="fn:substring($value,4,3)"/></e_950_3>
			<e_950_6><xsl:value-of select="fn:substring($value,7,8)"/></e_950_6>
			<e_950_24><xsl:value-of select="fn:substring($value,25,19)"/></e_950_24>
			<e_950_44><xsl:value-of select="fn:substring($value,45)"/></e_950_44>
		</xsl:when>
  		<xsl:otherwise><xsl:element name="{$nameHere}"><xsl:value-of select="$value"/></xsl:element></xsl:otherwise>
  	</xsl:choose>
  </xsl:template>


 <xsl:template match="text()">

 </xsl:template>
</xsl:stylesheet>
