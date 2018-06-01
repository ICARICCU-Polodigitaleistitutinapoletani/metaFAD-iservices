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
    
  
  <xsl:template match="Documento/DatiDocumento">
  		<record>
  			<xsl:attribute name="id"><xsl:value-of select="T001"/></xsl:attribute>
  			<xsl:attribute name="type"><xsl:value-of select="$type"/></xsl:attribute>
  			<node name="CD">
  				<node name="TSK"><xsl:attribute name="value"><xsl:value-of select="$type"/></xsl:attribute></node>
  				<node name="LIR"><xsl:attribute name="value">
  					<xsl:choose>
  						<xsl:when test="@livelloAutDoc='51'">I</xsl:when>
  						<xsl:when test="@livelloAutDoc='71'">P</xsl:when>
  						<xsl:when test="@livelloAutDoc='90'">C</xsl:when>
  					</xsl:choose>
  				</xsl:attribute></node>
  			</node>
  			<node name="DT">
  				<node name="DTM"><xsl:attribute name="value">
  					<xsl:choose>
  						<xsl:when test="T100/a_100_8='d'">data</xsl:when>
  						<xsl:when test="T100/a_100_8='D'">data</xsl:when>
  						<!-- 
  						<xsl:otherwise><xsl:value-of select="T100/a_100_8"/></xsl:otherwise>
  						 -->
  					</xsl:choose>  					
  				</xsl:attribute></node>
  				<node name="DTS">
  					<node name="DTSI"><xsl:attribute name="value"><xsl:value-of select="T100/a_100_9"/></xsl:attribute></node>
  					<xsl:if test="T100/a_100_13">
  						<node name="DTSF"><xsl:attribute name="value"><xsl:value-of select="T100/a_100_13"/></xsl:attribute></node>
  					</xsl:if>
  				</node>
  				<node name="DTZ">
  					<node name="DTZG">
  						<xsl:attribute name="value">
	  						<xsl:choose>
  								<xsl:when test="fn:starts-with(T100/a_100_9,'17') and (not(T100/a_100_13) or fn:starts-with(T100/a_100_13,'17'))">XVIII</xsl:when>
  								<xsl:when test="fn:starts-with(T100/a_100_9,'18') and (not(T100/a_100_13) or fn:starts-with(T100/a_100_13,'18'))">XIX</xsl:when>
  								<xsl:when test="fn:starts-with(T100/a_100_9,'19') and (not(T100/a_100_13) or fn:starts-with(T100/a_100_13,'19'))">XX</xsl:when>
  								<xsl:when test="fn:starts-with(T100/a_100_9,'20')  and (not(T100/a_100_13) or fn:starts-with(T100/a_100_13,'20'))">XXI</xsl:when>
  							</xsl:choose>
  						</xsl:attribute>
  					</node>
  				</node>
  			</node>
  			
  			<xsl:if test="$type='F'">
	  			<node name="PD">
		  			<node name="EDI">
		  				<xsl:for-each select="T205">
		  					<xsl:for-each select="a_205|b_205">
		  							<xsl:choose>
		  								<xsl:when test="fn:starts-with(.,'[')">
				  							<node name="EDIA">
		  										<xsl:attribute name="value"><xsl:value-of select="fn:replace(fn:replace(.,'^\[',''),'\]$','')"/></xsl:attribute>
		  									</node>
		  								</xsl:when>
		  								<xsl:otherwise>
				  							<node name="EDIT">
		  										<xsl:attribute name="value"><xsl:value-of select="."/></xsl:attribute>
		  									</node>
		  								</xsl:otherwise>
		  							</xsl:choose>
		  					</xsl:for-each>
		  					<xsl:if test="f_205">
		  						<node name="EDIR">
		  							<xsl:attribute name="value"><xsl:value-of select="f_205"/>
		  								<xsl:if test="g_205"><xsl:if test="f_205"><xsl:text> ; </xsl:text></xsl:if><xsl:value-of select="g_205"/></xsl:if>
		  							</xsl:attribute> 
		  						</node>
		  					</xsl:if>	
		  				</xsl:for-each>
		  			</node>
  			  		<!-- titolo serie -->
		  			<node name="SFI">
  						<xsl:for-each select="../LegamiDocumento/ArrivoLegame/LegameDoc[@tipoLegame='410']">
  							<node name="SFIT"><xsl:attribute name="value"><xsl:value-of select="T200/a_200"/><xsl:if test="T200/e_200"><xsl:text> : </xsl:text><xsl:value-of select="T200/e_200"/></xsl:if><xsl:if test="T200/f_200"><xsl:text> / </xsl:text><xsl:value-of select="T200/f_200"/></xsl:if></xsl:attribute></node>
							<xsl:if test="sequenza">
								<node name="SFIN"><xsl:attribute name="value"><xsl:value-of select="sequenza"/></xsl:attribute></node> 
							</xsl:if>  					
  						</xsl:for-each>
  					</node>

	  				<xsl:for-each select="T210">
	  					<node name="PDF">
	  						<xsl:if test="a_210">
	  							<node name="PDFL">
	  								<xsl:attribute name="value"><xsl:value-of select="a_210"/></xsl:attribute>	
	  							</node>
	  						</xsl:if>
	  						<xsl:if test="c_210">
	  							<node name="PDFN">
	  								<xsl:attribute name="value"><xsl:value-of select="c_210"/></xsl:attribute>	
	  							</node>
	  							<node name="PDFP" value="E"/>
	  							<node name="PDFR" value="Editore"/>
	  						</xsl:if>
	  						<xsl:if test="d_210">
	  							<node name="PDFD">
	  								<xsl:attribute name="value"><xsl:value-of select="d_210"/></xsl:attribute>	
	  							</node>
	  						</xsl:if>
	  						<xsl:if test="e_210">
	  							<node name="PDFL">
	  								<xsl:attribute name="value"><xsl:value-of select="e_210"/></xsl:attribute>	
	  							</node>
	  						</xsl:if>
	  						<xsl:if test="g_210">
	  							<node name="PDFN">
	  								<xsl:attribute name="value"><xsl:value-of select="g_210"/></xsl:attribute>	
	  							</node>
	  							<node name="PDFP" value="E"/>
	  							<node name="PDFR" value="Stampatore"/>
	  						</xsl:if>
	  						<xsl:if test="h_210">
	  							<node name="PDFD">
	  								<xsl:attribute name="value"><xsl:value-of select="h_210"/></xsl:attribute>	
	  							</node>
	  						</xsl:if>
	  					</node>
	  				</xsl:for-each>
	  			</node>
  			</xsl:if>
  			
  			<xsl:if test="$type='S'">
	  			<node name="AU">
	  				<xsl:for-each select="T210">
	  					<node name="EDT">
	  						<xsl:if test="ac_210/a_210">
	  							<node name="EDTL">
	  								<xsl:attribute name="value"><xsl:value-of select="ac_210/a_210"/></xsl:attribute>	
	  							</node>
	  						</xsl:if>
	  						<xsl:if test="ac_210/c_210">
	  							<node name="EDTN">
	  								<xsl:attribute name="value"><xsl:value-of select="ac_210/c_210"/></xsl:attribute>	
	  							</node>
	  						</xsl:if>
	  						<xsl:if test="d_210">
	  							<node name="EDTE">
	  								<xsl:attribute name="value"><xsl:value-of select="d_210"/></xsl:attribute>	
	  							</node>
	  						</xsl:if>
	  						<xsl:if test="e_210">
	  							<node name="EDTL">
	  								<xsl:attribute name="value"><xsl:value-of select="e_210"/></xsl:attribute>	
	  							</node>
	  						</xsl:if>
	  						<xsl:if test="g_210">
	  							<node name="EDTN">
	  								<xsl:attribute name="value"><xsl:value-of select="g_210"/></xsl:attribute>	
	  							</node>
	  						</xsl:if>
	  						<xsl:if test="h_210">
	  							<node name="EDTE">
	  								<xsl:attribute name="value"><xsl:value-of select="h_210"/></xsl:attribute>	
	  							</node>
	  						</xsl:if>
	  					</node>
	  				</xsl:for-each>
	  			</node>
  			</xsl:if>
  			<node name="OG">
  				<node name="OGT">
  					<xsl:for-each select="T215/a_215">
  							<xsl:choose>
  								<xsl:when test="$type='F' and (fn:contains(.,'album') or fn:contains(.,'Album'))">  					
  									<node name="OGTD"><xsl:attribute name="value">positivi</xsl:attribute></node>
  									<node name="OGTT"><xsl:attribute name="value">Album</xsl:attribute></node>
  									<node name="OGTV"><xsl:attribute name="value">insieme</xsl:attribute></node>
  								</xsl:when>
  								<xsl:when test="$type='F' and (fn:contains(.,'fotografia') or fn:contains(.,'Fotografia'))">
  									<node name="OGTD">
  										<xsl:attribute name="value">
  											<xsl:choose>
  												<xsl:when test="T116/a_116_0='e'">negativo</xsl:when>
  												<xsl:otherwise>positivo</xsl:otherwise> 
  											</xsl:choose>
  										</xsl:attribute>
									</node>
  								</xsl:when>
  								<xsl:when test="$type='S' or $type='D'">
  									<node name="OGTD"><xsl:attribute name="value"><xsl:value-of select="fn:replace(.,'^\d+\s+','')"/></xsl:attribute></node>
  								</xsl:when>
  								<xsl:otherwise></xsl:otherwise>
  							</xsl:choose>  							
	  						<xsl:variable name="v"><xsl:value-of select="substring-before(substring-after(.,'('),')')"/></xsl:variable>
  							<xsl:if test="$v and fn:contains(.,'(')">
  								<xsl:choose> 
  									<xsl:when test="$type='D'">
  										<node name="OGTD" value="serie"/>
  									</xsl:when>
  									<xsl:when test="$type='S'">
  										<node name="OGTV" value="serie"/>
  									</xsl:when>
  								</xsl:choose>
  							</xsl:if>
  					</xsl:for-each>
  				</node>
  				<node name="QNT">
  					<xsl:for-each select="T215/a_215">
  						<xsl:if test="$type='F' and (fn:contains(.,'album') or fn:contains(.,'Album'))">
  							<node name="QNTN"><xsl:attribute name="value">1</xsl:attribute></node>
  						</xsl:if>
  						<xsl:variable name="v"><xsl:value-of select="substring-before(substring-after(.,'('),')')"/></xsl:variable>
  						<xsl:if test="$v">
  							<xsl:choose> 
  								<xsl:when test="$type='F'">
  									<node name="QNTI">
  										<xsl:attribute name="value"><xsl:value-of select="$v"/></xsl:attribute>
  									</node>
  								</xsl:when>
  								<xsl:when test="$type='D'">
  									<node name="QNTN">
  										<xsl:attribute name="value"><xsl:value-of select="$v"/></xsl:attribute>
  									</node>
  								</xsl:when>
  								<xsl:when test="$type='S'">
  									<node name="QNTU">
  										<xsl:attribute name="value"><xsl:value-of select="$v"/></xsl:attribute>
  									</node>
  								</xsl:when>
  							</xsl:choose>
  						</xsl:if>
  					</xsl:for-each>
  				</node>
  			</node>
  			
  			<node name="MT">
  				<node name="MTC">
  					<xsl:if test="T116/a_116_1">
	  					<xsl:variable name="v"><xsl:choose><xsl:when test="$type='S' or $type='D'">MTC</xsl:when><xsl:when test="$type='F'">MTCM</xsl:when></xsl:choose></xsl:variable>
	  					<node>
		  					<xsl:attribute name="name"><xsl:value-of select="$v"/></xsl:attribute>
		  					<xsl:attribute name="value"><xsl:value-of select="polo:getSufg(T116/a_116_1)"/></xsl:attribute>
	  					</node>
	  					<xsl:choose>
	  						<!-- Disegno -->
	  						<xsl:when test="$type='D'">
	  							<xsl:for-each select="T116/a_116_4">
		  							<node><xsl:attribute name="name"><xsl:value-of select="$v"/></xsl:attribute>
	  									<xsl:attribute name="value"><xsl:value-of select="polo:getTecd(.)"/></xsl:attribute>
	  								</node>
	  							</xsl:for-each>
	  							<xsl:if test="T116/a_116_6"><node><xsl:attribute name="name"><xsl:value-of select="$v"/></xsl:attribute>
	  								<xsl:attribute name="value"><xsl:value-of select="polo:getTecd(T116/a_116_6)"/></xsl:attribute></node></xsl:if>
	  							<xsl:if test="T116/a_116_8"><node><xsl:attribute name="name"><xsl:value-of select="$v"/></xsl:attribute>
	  								<xsl:attribute name="value"><xsl:value-of select="polo:getTecd(T116/a_116_8)"/></xsl:attribute></node></xsl:if>
	  						</xsl:when>
	  						<!-- Stampa -->
	  						<xsl:when test="$type='S'">
	  							<xsl:for-each select="T116/a_116_10">
		  							<node><xsl:attribute name="name"><xsl:value-of select="$v"/></xsl:attribute>
	  								<xsl:attribute name="value"><xsl:value-of select="polo:getTecs(.)"/></xsl:attribute></node>
	  							</xsl:for-each>
	  							<xsl:if test="T116/a_116_12"><node><xsl:attribute name="name"><xsl:value-of select="$v"/></xsl:attribute>
	  								<xsl:attribute name="value"><xsl:value-of select="polo:getTecs(T116/a_116_12)"/></xsl:attribute></node></xsl:if>
	  							<xsl:if test="T116/a_116_14"><node><xsl:attribute name="name"><xsl:value-of select="$v"/></xsl:attribute>
	  								<xsl:attribute name="value"><xsl:value-of select="polo:getTecs(T116/a_116_14)"/></xsl:attribute></node></xsl:if>
	  						</xsl:when>
	  					</xsl:choose>
	  					
  					</xsl:if>
  					<!-- POLODEBUG-147: lasciamo perdere... -->
  					<!-- 
  					<xsl:for-each select="T215/c_215">
  						<node>
  							<xsl:attribute name="name"><xsl:choose><xsl:when test="$type='S' or $type='D'">MTC</xsl:when><xsl:when test="$type='F'">MTCT</xsl:when></xsl:choose></xsl:attribute>
  							<xsl:attribute name="value"><xsl:value-of select="."/></xsl:attribute>
  						</node>
  					</xsl:for-each>
  					 -->
  				</node>
  				<xsl:if test="T116/a_116_3 and $type='F'">
  				<node name="MTX"><xsl:attribute name="value"><xsl:value-of select="polo:getColo(T116/a_116_3)"/></xsl:attribute>
  				</node>
  				</xsl:if>
  				<xsl:for-each select="T215/d_215">
  					<xsl:if test="$type='D'">
  						<xsl:if test="fn:contains(.,'diam.')">
  							<node name="MISD"><xsl:attribute name="value"><xsl:value-of select="substring-before(.,'diam.')"/></xsl:attribute></node>
  						</xsl:if>
  						<xsl:if test="not(fn:contains(.,'diam.'))">
  							<node name="MISU"><xsl:attribute name="value"><xsl:value-of select="substring-after(.,' ')"/></xsl:attribute></node>
	  						<node name="MISA"><xsl:attribute name="value"><xsl:value-of select="fn:tokenize(substring-before(.,' '),'x')[1]"/></xsl:attribute></node>	  						
	  						<node name="MISL"><xsl:attribute name="value"><xsl:value-of select="fn:tokenize(substring-before(.,' '),'x')[2]"/></xsl:attribute></node>
	  					</xsl:if>	  						
  					</xsl:if>
  					<xsl:if test="$type='F'">
	  					<node name="MIS">
	  						<node name="MISM"><xsl:attribute name="value"><xsl:value-of select="substring-before(.,' ')"/></xsl:attribute></node>
	  						<node name="MISU"><xsl:attribute name="value"><xsl:value-of select="substring-after(.,' ')"/></xsl:attribute></node>
	  						<node name="MISZ"><xsl:attribute name="value"><xsl:choose><xsl:when test="fn:count(fn:tokenize(.,'x')) &gt; 2">altezzaxlunghezzaxspessore</xsl:when><xsl:otherwise>altezzaxlunghezza</xsl:otherwise></xsl:choose></xsl:attribute></node>
	  					</node>
  					</xsl:if>
  				</xsl:for-each>
  			</node>
  			
  			
  			
  			
  			<node name="RV">
  				<node name="RVE">
  						<!-- crea scheda madre -->
  						<xsl:for-each select="../LegamiDocumento/ArrivoLegame/LegameDoc[@tipoLegame='461']">
  							<xsl:if test="DocumentoLegato/DatiDocumento[@tipoMateriale='G']">
  							<node name="RVEL"><xsl:attribute name="value">0: <xsl:value-of select="DocumentoLegato/DatiDocumento/T001"/></xsl:attribute></node>
  							</xsl:if>
  						</xsl:for-each>
  						<!-- crea schede figli -->
  						<xsl:for-each select="../LegamiDocumento/ArrivoLegame/LegameDoc[@tipoLegame='463']">
  							<xsl:if test="DocumentoLegato/DatiDocumento[@tipoMateriale='G']">
  								<node name="RVEL"><xsl:attribute name="value"><xsl:value-of select="position()"/>: <xsl:value-of select="DocumentoLegato/DatiDocumento/T001"/></xsl:attribute></node>
  							</xsl:if>
  						</xsl:for-each>
  				</node>
  			</node>
  			
  			<node name="UB">
				<!-- collocazione, modificato 27-03-2017 vedi POLODEBUG-127  -->
				<xsl:if test="$type='F'">
					<xsl:for-each select="T950/d_950">
						<node name="UBF">
							<node name="UBFP"><xsl:attribute name="value"><xsl:value-of select="d_950_3"/></xsl:attribute></node>
							<node name="UBFC"><xsl:attribute name="value"><xsl:value-of select="d_950_13"/><xsl:text> </xsl:text><xsl:value-of select="d_950_37"/></xsl:attribute></node>
						</node>
					</xsl:for-each>
				</xsl:if>
				<!-- inventario -->
				<node name="INV">
					<xsl:if test="$type='D' or $type='S'">
						<xsl:for-each select="T950/d_950">
							<node name="INVC">
								<xsl:attribute name="value"><xsl:value-of select="d_950_3"/><xsl:text> </xsl:text><xsl:value-of select="d_950_13"/><xsl:text> </xsl:text><xsl:value-of select="d_950_37"/></xsl:attribute>
							</node>
						</xsl:for-each>
					</xsl:if>
					<xsl:for-each select="T950/d_950/e_950">
						<node name="INVN">
							<xsl:attribute name="value"><xsl:value-of select="e_950_3"/><xsl:text> </xsl:text><xsl:value-of select="e_950_6"/></xsl:attribute>
						</node>
					</xsl:for-each>
				</node>
				<!--
  				<node name="INP">
					<xsl:for-each select="T950/d_950/e_950">
						<node name="INPC">
							<xsl:attribute name="value"><xsl:value-of select="e_950_3"/><xsl:text> </xsl:text><xsl:value-of select="e_950_6"/></xsl:attribute>
						</node>
					</xsl:for-each>					
				</node>
							-->
  				<node name="STT">
					<xsl:for-each select="T950/d_950/e_950">
						<node name="STTS">
							<xsl:attribute name="value"><xsl:value-of select="e_950_44"/></xsl:attribute>
						</node>
					</xsl:for-each>					
				</node>			
			</node>			
				
  			<node name="CO">
				<xsl:for-each select="T950/d_950/e_950">
					<xsl:for-each select="e_950_20">
						<node name="STC">
							<node name="STCC">
								<xsl:attribute name="value"><xsl:value-of select="."/></xsl:attribute>
							</node>
						</node>
					</xsl:for-each> 
				</xsl:for-each>				
			</node>			

  			<node name="LA">
				<xsl:for-each select="T950/d_950/e_950/p_950">
					<node name="PRC">
						<node name="PRCN">
							<xsl:attribute name="value"><xsl:value-of select="."/></xsl:attribute>
						</node>
					</node>
				</xsl:for-each>					
			</node>			

			<xsl:if test="$type='D'">
				<node name="DA">			
					<node name="DES">
						<xsl:for-each select="T3XX[@tipoNota='330']">
							<node name="DESS"><xsl:attribute name="value"><xsl:value-of select="a_3XX"></xsl:value-of></xsl:attribute></node>
						</xsl:for-each>
						<!-- POLODEBUG-147 da escludere -->
						<!--  
						<xsl:for-each select="T3XX[@tipoNota='330']">
							<node name="DESO"><xsl:attribute name="value"><xsl:value-of select="a_3XX"></xsl:value-of></xsl:attribute></node>
						</xsl:for-each>
						 -->
					</node>
				</node>
			</xsl:if>
			
			<xsl:if test="$type='S'">
				<node name="DA">			
					<node name="DES">
						<xsl:for-each select="T3XX[@tipoNota='330']">
							<node name="DESS"><xsl:attribute name="value"><xsl:value-of select="a_3XX"></xsl:value-of></xsl:attribute></node>
						</xsl:for-each>
					</node>
				</node>
			</xsl:if>
			
			<!-- POLODEBUG-147 -->
			<xsl:if test="$type='D'">
				<xsl:choose>
					<xsl:when test="fn:starts-with(fn:string-join(T200/a_200,' '),'[*')">
					</xsl:when>
					<xsl:otherwise>
						<node name="DA">			
							<node name="ISR">
					  			<node name="ISRI">
									<xsl:attribute name="value"><xsl:value-of select="T200/a_200"/><xsl:if test="T200/e_200"><xsl:text> : </xsl:text><xsl:value-of select="T200/e_200"/></xsl:if><xsl:if test="T200/c_200"><xsl:text> . </xsl:text><xsl:value-of select="T200/c_200"/></xsl:if></xsl:attribute>
								</node>
							</node>
						</node>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:if>

			<node name="SG">			
				<node name="SGT">
				    <!-- titolo parallelo -->
	  				<xsl:if test="$type='S'">
	  					<xsl:for-each select="../LegamiDocumento/ArrivoLegame/LegameDoc[@tipoLegame='410']">
	  						<node name="SGTS"><xsl:attribute name="value"><xsl:value-of select="T200/a_200"/><xsl:if test="T200/e_200"><xsl:text> : </xsl:text><xsl:value-of select="T200/e_200"/></xsl:if><xsl:if test="T200/f_200"><xsl:text> / </xsl:text><xsl:value-of select="T200/f_200"/></xsl:if></xsl:attribute></node>
	  					</xsl:for-each>
		  				<xsl:for-each select="../LegamiDocumento/ArrivoLegame/LegameElementoAut[@tipoLegame='510']">
							<node name="SGTR"><xsl:attribute name="value"><xsl:value-of select="ElementoAutLegato/DatiElementoAut/T510/a_200"/>
							<xsl:if test="ElementoAutLegato/DatiElementoAut/T510/e_200">: <xsl:value-of select="ElementoAutLegato/DatiElementoAut/T510/e_200"/></xsl:if>
							</xsl:attribute>
							</node>
						</xsl:for-each>	  		
					</xsl:if>			
					
					<xsl:if test="$type='D'">
					<!-- POLODEBUG-147 -->
						<!-- POLODEBUG-196 -->
						<!--
						<xsl:choose>
							<xsl:when test="fn:starts-with(fn:string-join(T200/a_200,' '),'[*')">
								<node name="SGTI">
									<xsl:attribute name="value"><xsl:value-of select="fn:substring-before(fn:substring-after(fn:string-join(T200/a_200,' '),'[*'),']')"/></xsl:attribute>
								</node>
							</xsl:when>
						</xsl:choose>
						-->
						<xsl:for-each select="T200/a_200">
							<node name="SGTI">
								<xsl:attribute name="value"><xsl:choose>
									<xsl:when test="fn:starts-with(.,'[*')"><xsl:value-of select="fn:substring-after(.,'[*')"/></xsl:when>
									<xsl:when test="fn:ends-with(.,'] ')"><xsl:value-of select="fn:substring-before(.,'] ')"/></xsl:when>
									<xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
								</xsl:choose>
								</xsl:attribute>
							</node>
						</xsl:for-each>
					</xsl:if>
					<xsl:if test="$type='S'">
						<node name="SGTP">
							<xsl:attribute name="value"><xsl:value-of select="T200/a_200"/><xsl:if test="T200/e_200"><xsl:text> : </xsl:text><xsl:value-of select="T200/e_200"/></xsl:if><xsl:if test="T200/c_200"><xsl:text> . </xsl:text><xsl:value-of select="T200/c_200"/></xsl:if></xsl:attribute>	  					 			
						</node>
					</xsl:if>
					<xsl:if test="$type='F'">
						<xsl:for-each select="T3XX[@tipoNota='330']">
							<node name="SGTD"><xsl:attribute name="value"><xsl:value-of select="a_3XX"></xsl:value-of></xsl:attribute></node>
						</xsl:for-each>
					</xsl:if>
					<xsl:if test="$type!='D'">		<!-- POLODEBUG-147 -->			
		  				<xsl:for-each select="../LegamiDocumento/ArrivoLegame/LegameElementoAut[@tipoLegame='606']">
							<node name="SGTI"><xsl:attribute name="value"><xsl:value-of select="ElementoAutLegato/DatiElementoAut/T250/a_250"/>
							<xsl:if test="ElementoAutLegato/DatiElementoAut/T250/x_250"> - <xsl:value-of select="ElementoAutLegato/DatiElementoAut/T250/x_250"/></xsl:if>
							</xsl:attribute>
							</node>
						</xsl:for-each>	  		
					</xsl:if>				  				
				</node>
				<node name="SGL">
	  				<xsl:variable name="vTemp"><xsl:value-of select="fn:substring-before(fn:substring-after(fn:string-join(T200/a_200,' '),'['),']')"></xsl:value-of> </xsl:variable>
					<node name="SGLT"> 			
						<xsl:choose>
						  	<xsl:when test="$type='F' and fn:string-length($vTemp) &gt; 0">
						  		<xsl:attribute name="value"><xsl:if test="T200/e_200"><xsl:value-of select="T200/e_200"/></xsl:if><xsl:if test="T200/c_200"><xsl:text> . </xsl:text><xsl:value-of select="T200/c_200"/></xsl:if></xsl:attribute>						  
						  	</xsl:when>
						  	<xsl:when test="$type='F'">
	  							<xsl:attribute name="value"><xsl:value-of select="T200/a_200"/><xsl:if test="T200/e_200"><xsl:text> : </xsl:text><xsl:value-of select="T200/e_200"/></xsl:if><xsl:if test="T200/c_200"><xsl:text> . </xsl:text><xsl:value-of select="T200/c_200"/></xsl:if></xsl:attribute>
	  						</xsl:when>
	  					</xsl:choose>	  							  					
	  				</node>
	  				<xsl:if test="$type='F'">
		  				<xsl:if test="fn:string-length($vTemp) &gt; 0">
		  					<node name="SGLA">
		  						<xsl:attribute name="value"><xsl:value-of select="$vTemp"/></xsl:attribute>
		  					</node>
		  				</xsl:if>
	  				</xsl:if>
	  				<xsl:if test="$type='F'">
		  				<xsl:for-each select="../LegamiDocumento/ArrivoLegame/LegameElementoAut[@tipoLegame='510']">
							<node name="SGLL"><xsl:attribute name="value"><xsl:value-of select="ElementoAutLegato/DatiElementoAut/T510/a_200"/>
							<xsl:if test="ElementoAutLegato/DatiElementoAut/T510/e_200">: <xsl:value-of select="ElementoAutLegato/DatiElementoAut/T510/e_200"/></xsl:if>
							</xsl:attribute>
							</node>
						</xsl:for-each>	  		
					</xsl:if>			
	  			</node>
	  			<xsl:if test="$type='F'">
		  			<node name="CLF">
		  				<xsl:for-each select="../LegamiDocumento/ArrivoLegame/LegameElementoAut[@tipoLegame='676']">
		  					<node name="CLFS">
		  						<xsl:attribute name="value">
		  						<xsl:for-each select="ElementoAutLegato/DatiElementoAut/T676/c_676">
		  							<xsl:text> </xsl:text><xsl:value-of select="."/>
		  						</xsl:for-each>
		  						</xsl:attribute>
		  					</node>
		  					<node name="CLFT">
		  						<xsl:attribute name="value">Classificazione Decimale Dewey <xsl:value-of select="ElementoAutLegato/DatiElementoAut/T676/c_676"/></xsl:attribute>
		  					</node>
		  				</xsl:for-each>
		  			</node>
		  			<node name="CLF">
		  				<xsl:for-each select="../LegamiDocumento/ArrivoLegame/LegameElementoAut[@tipoLegame='686']">
		  					<node name="CLFS">
		  						<xsl:attribute name="value">
		  						<xsl:for-each select="ElementoAutLegato/DatiElementoAut/T686/c_686">
		  							<xsl:text> </xsl:text><xsl:value-of select="."/>
		  						</xsl:for-each>
		  						</xsl:attribute>
		  					</node>
		  					<node name="CLFT">
		  						<xsl:attribute name="value"><xsl:value-of select="ElementoAutLegato/DatiElementoAut/T686/c_686"/></xsl:attribute>
		  					</node>
		  				</xsl:for-each>
		  			</node>
  				</xsl:if>
			</node>
			
			<node name="AU">
 				<xsl:for-each select="../LegamiDocumento/ArrivoLegame/LegameElementoAut[@tipoLegame='700' or @tipoLegame='701' or @tipoLegame='702']">
 					<node name="AUT">
 						<xsl:attribute name="vid"><xsl:value-of select="ElementoAutLegato/DatiElementoAut/T001"/></xsl:attribute>
 						<node name="AUTN"><xsl:attribute name="value"><xsl:value-of select="ElementoAutLegato/DatiElementoAut/T200/a_200"/>
 							<!-- contiene la punteggiatura -->
 							<xsl:if test="ElementoAutLegato/DatiElementoAut/T200/b_200"><xsl:text></xsl:text><xsl:value-of select="ElementoAutLegato/DatiElementoAut/T200/b_200"/></xsl:if>
 							<xsl:if test="ElementoAutLegato/DatiElementoAut/T200/c_200"><xsl:text> &lt;</xsl:text><xsl:value-of select="ElementoAutLegato/DatiElementoAut/T200/c_200"/><xsl:text> &gt;</xsl:text></xsl:if>
 						</xsl:attribute></node> 
 						<xsl:if test="@tipoLegame='700' and $type='F'">
 							<node name="AUTP" value="P"/>
 						</xsl:if>
 						<xsl:if test="ElementoAutLegato/DatiElementoAut/T200/f_200">
 							<node name="AUTA"><xsl:attribute name="value"><xsl:value-of select="ElementoAutLegato/DatiElementoAut/T200/f_200"/></xsl:attribute></node>
 						</xsl:if>
 						<xsl:if test="@relatorCode">
 							<node name="AUTR"><xsl:attribute name="value"><xsl:value-of select="polo:getRuolo(@relatorCode)"/></xsl:attribute></node>
 						</xsl:if>
 						<xsl:if test="ElementoAutLegato/DatiElementoAut/noteLegame and (@tipoLegame='700' or @tipoLegame='701')">
 							<node name="AUTZ"><xsl:attribute name="value"><xsl:value-of select="ElementoAutLegato/DatiElementoAut/noteLegame"/></xsl:attribute></node>
 							<node name="AUTM"><xsl:attribute name="value"><xsl:value-of select="polo:getAutm(ElementoAutLegato/DatiElementoAut/noteLegame)"/></xsl:attribute></node>
 						</xsl:if>
 					</node>
 				</xsl:for-each>
 				<xsl:for-each select="T200/f_200">
 					<node name="AUT">
 						<node>
 							<xsl:attribute name="name">
 								<xsl:choose>
 									<xsl:when test="$type='F'">AUTI</xsl:when>
 									<xsl:otherwise>AUTA</xsl:otherwise>
 								</xsl:choose>
 							</xsl:attribute>
 							<xsl:attribute name="value"><xsl:value-of select="fn:replace(.,'\]$','')"/></xsl:attribute>
 						</node>  					
 					</node>
 				</xsl:for-each>
				<xsl:for-each select="../LegamiDocumento/ArrivoLegame/LegameElementoAut[@tipoLegame='710' or @tipoLegame='711' or @tipoLegame='712']">
					<node name="AUT">
 						<xsl:attribute name="vid"><xsl:value-of select="ElementoAutLegato/DatiElementoAut/T001"/></xsl:attribute> 					
					 	<node name="AUTN">
					 		<xsl:attribute name="value">
					 			<xsl:call-template name="Nome210">
					 				<xsl:with-param name="node" select="ElementoAutLegato/DatiElementoAut/T210"/>
					 				<xsl:with-param name="tipo" select="ElementoAutLegato/DatiElementoAut/@tipoNome"/>
					 			</xsl:call-template>
					 		</xsl:attribute>
					 	</node>
					 	<xsl:if test="ElementoAutLegato/DatiElementoAut/T210/f_210">
					 		<node name="AUTA"><xsl:attribute name="value"><xsl:value-of select="ElementoAutLegato/DatiElementoAut/T210/f_210"/></xsl:attribute></node>
					 	</xsl:if>
					 	<xsl:if test="@relatorCode">
							<node name="AUTR"><xsl:attribute name="value"><xsl:value-of select="polo:getRuolo(@relatorCode)"/></xsl:attribute></node>
						</xsl:if>
						<xsl:if test="@tipoLegame='710' and $type='F'">
							 <node name="AUTP" value="E"/>
						</xsl:if>
						<xsl:if test="ElementoAutLegato/DatiElementoAut/noteLegame  and (@tipoLegame='710' or @tipoLegame='711')">
							<node name="AUTZ"><xsl:attribute name="value"><xsl:value-of select="ElementoAutLegato/DatiElementoAut/noteLegame"/></xsl:attribute></node>
							<node name="AUTM"><xsl:attribute name="value"><xsl:value-of select="polo:getAutm(ElementoAutLegato/DatiElementoAut/noteLegame)"/></xsl:attribute></node>
						</xsl:if> 							
					</node>
				</xsl:for-each>
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