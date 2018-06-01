<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"   
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:dcterms="http://purl.org/dc/terms/" 
    xmlns:xml="http://www.w3.org/XML/1998/namespace"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 	xmlns:xlink="http://www.w3.org/TR/xlink"
	xmlns:xlinkGoogle="http://www.w3.org/1999/xlink"
    xmlns:PREMIS="info:lc/xmlns/premis-v2"
    xmlns:iccu="http://www.iccu.sbn.it/metaAG1.pdf"
    xmlns:mag="http://www.iccu.sbn.it/metaAG1.pdf"
    xmlns:marc="http://www.loc.gov/MARC21/slim"
    xmlns:xalan="http://xml.apache.org/xalan"
    xmlns:gbs="http://books.google.com/gbs"
    xmlns:niso="http://www.niso.org/pdfs/DataDict.pdf"
    xmlns:METS="http://www.loc.gov/METS/"
    xmlns:fn="http://www.w3.org/2005/xpath-functions"
	xmlns:ic="http://internetculturale.it/saxon-extension"            
	xmlns:saxon="http://saxon.sf.net/"        
	extension-element-prefixes="saxon"
    exclude-result-prefixes="xalan ic saxon fn METS gbs PREMIS marc"                           
    version="2.0"
    >
    <xsl:output
        method="xml"
        encoding="UTF-8"
        
    />
	<!--
 	<xsl:param name="job">500</xsl:param>
    <xsl:param name="descSource">default</xsl:param>
    <xsl:param name="idPrefix">mets:</xsl:param>
    <xsl:param name="descSourceLevel2"></xsl:param>
    <xsl:param name="instanceName">metaindice</xsl:param>
  	-->
	<xsl:param name="baseDir"></xsl:param>
	<xsl:param name="stprog"></xsl:param>
	<xsl:param name="collection"></xsl:param>
	<xsl:param name="agency"></xsl:param>
	<xsl:param name="access_rights"></xsl:param>
	<xsl:param name="completeness"></xsl:param>

    <xsl:template match="/">
    	<mag:metadigit>
    	<mag:gen>
    		<xsl:attribute name="creation"><xsl:value-of select="//METS:metsHdr/@CREATEDATE"/></xsl:attribute>
            <xsl:choose>
                <xsl:when test="//METS:metsHdr/@LASTMODDATE"><xsl:attribute name="last_update"><xsl:value-of select="//METS:metsHdr/@LASTMODDATE"/></xsl:attribute></xsl:when>
                <xsl:otherwise><xsl:attribute name="last_update"><xsl:value-of select="//METS:metsHdr/@CREATEDATE"/></xsl:attribute></xsl:otherwise>
            </xsl:choose>
    		<mag:stprog><xsl:choose>
				<xsl:when test="fn:string-length($stprog) gt 0"><xsl:value-of select="$stprog"/></xsl:when>
				<xsl:otherwise><xsl:if test="//gbs:sourceLibrary"><xsl:value-of select="ic:getStprog(//gbs:sourceLibrary)"/></xsl:if></xsl:otherwise>
			</xsl:choose></mag:stprog>
    		<mag:collection><xsl:choose>
				<xsl:when test="fn:string-length($collection) gt 0"><xsl:value-of select="$collection"/></xsl:when>
				<xsl:otherwise>Collezione Google Books</xsl:otherwise>
			</xsl:choose></mag:collection>
    		<mag:agency><xsl:choose>
				<xsl:when test="fn:string-length($agency) gt 0"><xsl:value-of select="$agency"/></xsl:when>
				<xsl:otherwise><xsl:if test="//gbs:sourceLibrary"><xsl:value-of select="ic:getAgency(//gbs:sourceLibrary)"/></xsl:if></xsl:otherwise>
			</xsl:choose></mag:agency>
    		<mag:access_rights><xsl:choose>
				<xsl:when test="fn:string-length($access_rights) gt 0"><xsl:value-of select="$access_rights"/></xsl:when>
				<xsl:otherwise>1</xsl:otherwise>
			</xsl:choose></mag:access_rights>
    		<mag:completeness><xsl:choose>
				<xsl:when test="fn:string-length($completeness) gt 0"><xsl:value-of select="$completeness"/></xsl:when>
				<xsl:otherwise>0</xsl:otherwise>
			</xsl:choose></mag:completeness>
    	</mag:gen>
    	<xsl:variable name="level"><xsl:value-of select="fn:substring(//marc:record/marc:leader,8,1)"></xsl:value-of></xsl:variable>
    	<mag:bib>
    		<xsl:attribute name="level"><xsl:value-of select="$level"/></xsl:attribute>
    		<xsl:variable name="gbsIdentifier"><xsl:value-of select="normalize-space(//gbs:sourceIdentifier)"/></xsl:variable>   
	    	<xsl:for-each select="//marc:record">

	    		<!-- identifier -->
	    		<!-- TODO feature da verificare si perde il collegamento al BID -->
	    		<xsl:variable name="featureEnabled">false</xsl:variable>
	    		<xsl:choose>
	    			<xsl:when test="count(marc:datafield[@tag='955']) gt 1 and $featureEnabled='true' and $level ne 's'">
		   				<xsl:for-each select="marc:datafield[@tag='955']">
		   					<xsl:variable name="valB"><xsl:value-of select="fn:replace(normalize-space(marc:subfield[@code='b']),'\s+','')"/></xsl:variable>
		   					<xsl:if test="fn:replace($gbsIdentifier,'\s+','') eq $valB">
				    			<dc:identifier><xsl:value-of select="../marc:controlfield[@tag='001']"/>_<xsl:value-of select="fn:replace(fn:replace(normalize-space(marc:subfield[@code='3']),'\s+','.'),'\.+','.')"/></dc:identifier>
						    </xsl:if> 
						</xsl:for-each>
	    			</xsl:when>
	    			<!-- TODO piece per i periodici da prendere? -->
	    			<xsl:otherwise>
		    			<dc:identifier><xsl:value-of select="marc:controlfield[@tag='001']"/></dc:identifier>
	    			</xsl:otherwise>
	    		</xsl:choose>

				<xsl:variable name="testValue"><xsl:value-of select="//PREMIS:objectIdentifier/PREMIS:objectIdentifierType[text() = 'barcode']/../PREMIS:objectIdentifierValue"/></xsl:variable>
				<xsl:if test="$testValue">		    	
		    		<dc:identifier><xsl:value-of select="$testValue"/></dc:identifier>
		    	</xsl:if>

				<!-- title -->
				<xsl:for-each select="marc:datafield[@tag='245']">
					<xsl:choose>
						<xsl:when test="marc:subfield[@code='n'] and marc:subfield[@code='p']">
							<dc:title><xsl:value-of select="normalize-space(marc:subfield[@code='n'])"/> ; <xsl:value-of select="normalize-space(marc:subfield[@code='p'])"/></dc:title>
							<dc:relation>'fa parte di:' <xsl:value-of select="normalize-space(marc:subfield[@code='a'])"/></dc:relation>
						</xsl:when>
						<xsl:when test="marc:subfield[@code='n'] and not(marc:subfield[@code='p'])">
							<dc:title><xsl:value-of select="normalize-space(marc:subfield[@code='a'])"/>. <xsl:value-of select="normalize-space(marc:subfield[@code='n'])"/></dc:title>
						</xsl:when>
						<xsl:otherwise>
							<dc:title>
								<xsl:value-of select="normalize-space(marc:subfield[@code='a'])"/>
								<xsl:if test="marc:subfield[@code='b']"> : <xsl:value-of select="normalize-space(marc:subfield[@code='b'])"/></xsl:if>
								<xsl:if test="marc:subfield[@code='c']"> / <xsl:value-of select="normalize-space(marc:subfield[@code='c'])"/></xsl:if>
								<xsl:if test="marc:subfield[@code='n']"><xsl:text> </xsl:text><xsl:value-of select="normalize-space(marc:subfield[@code='n'])"/></xsl:if>
								<xsl:if test="marc:subfield[@code='p']"><xsl:text> </xsl:text><xsl:value-of select="normalize-space(marc:subfield[@code='p'])"/></xsl:if>
							</dc:title>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:for-each>

				<!-- Titolo parallelo -->
				<xsl:for-each select="marc:datafield[@tag='246']">
					<xsl:if test="@ind2 eq '1'">
						<dc:title>'titolo parallelo:' <xsl:value-of select="normalize-space(marc:subfield[@code='a'])"/></dc:title>
					</xsl:if>
				</xsl:for-each>

				<!-- creator -->
				<xsl:for-each select="marc:datafield[@tag='100']"><dc:creator><xsl:value-of select="marc:subfield[@code='a']"/> <xsl:value-of select="marc:subfield[@code='b']"/> <xsl:value-of select="marc:subfield[@code='d']"/></dc:creator></xsl:for-each>
				<xsl:for-each select="marc:datafield[@tag='110']"><dc:creator><xsl:value-of select="marc:subfield[@code='a']"/> <xsl:value-of select="marc:subfield[@code='b']"/> <xsl:value-of select="marc:subfield[@code='n']"/></dc:creator></xsl:for-each>
				<xsl:for-each select="marc:datafield[@tag='111']"><dc:creator><xsl:value-of select="marc:subfield[@code='a']"/> <xsl:value-of select="marc:subfield[@code='e']"/> <xsl:value-of select="marc:subfield[@code='n']"/></dc:creator></xsl:for-each>

				<!-- publisher -->
				<xsl:for-each select="marc:datafield[@tag='260']">
					<dc:publisher>
						<xsl:variable name="valA"><xsl:value-of select="marc:subfield[@code='a']"/></xsl:variable>
						<xsl:for-each select="marc:subfield[@code='a']"><xsl:value-of select="normalize-space(.)"/></xsl:for-each>
						<xsl:variable name="valB"><xsl:value-of select="marc:subfield[@code='b']"/></xsl:variable>
						<xsl:for-each select="marc:subfield[@code='b']"> : <xsl:value-of select="normalize-space(.)"/></xsl:for-each>
						<xsl:for-each select="marc:subfield[@code='c']"><xsl:if test="not(fn:matches(.,'^[0-9]*-[0-9]*$')) and not(fn:matches(.,'^[0-9]*$'))">, <xsl:value-of select="normalize-space(.)"/></xsl:if></xsl:for-each>
						<xsl:variable name="valE"><xsl:value-of select="marc:subfield[@code='e']"/></xsl:variable>
						<xsl:for-each select="marc:subfield[@code='e']"><xsl:if test="not(text() eq ../marc:subfield[@code='a']/text())"><xsl:text> ; </xsl:text><xsl:value-of select="normalize-space(.)"/></xsl:if></xsl:for-each>
						<xsl:variable name="valF"><xsl:value-of select="marc:subfield[@code='f']"/></xsl:variable>
						<xsl:for-each select="marc:subfield[@code='f']"><xsl:if test="not(text() eq ../marc:subfield[@code='b']/text())"><xsl:text> : </xsl:text><xsl:value-of select="normalize-space(.)"/></xsl:if></xsl:for-each>
						<xsl:for-each select="../marc:datafield[@tag='752']/marc:subfield[@code='d']">
							<xsl:variable name="valD"><xsl:value-of select="../marc:datafield[@tag='752']/marc:subfield[@code='d']"/></xsl:variable>
							<xsl:if test="not($valD eq $valA) and not($valD eq $valE)">
								[<xsl:value-of select="$valD"/>
								<xsl:variable name="val710"><xsl:value-of select="../marc:datafield[@tag='710']"/></xsl:variable>
								<xsl:if test="../marc:datafield[@tag='710']/marc:subfield[@code='4' and text()='610' or text()='650'] and not($val710 eq $valB) and not($val710 eq $valF)"> : <xsl:value-of select="$val710"/></xsl:if>]
							</xsl:if>
						</xsl:for-each>
					</dc:publisher>
				</xsl:for-each>

				<!-- subject -->
				<xsl:for-each select="marc:datafield[@tag='650']">
					<dc:subject>
						<xsl:if test="marc:subfield[@code='a']"><xsl:value-of select="normalize-space(marc:subfield[@code='a'])"/></xsl:if>
						<xsl:if test="marc:subfield[@code='x']"> - <xsl:value-of select="normalize-space(marc:subfield[@code='x'])"/></xsl:if>
						<xsl:if test="marc:subfield[@code='z']"> - <xsl:value-of select="normalize-space(marc:subfield[@code='z'])"/></xsl:if>
						<xsl:if test="marc:subfield[@code='y']"> - <xsl:value-of select="normalize-space(marc:subfield[@code='y'])"/></xsl:if>
					</dc:subject>
				</xsl:for-each>

				<!-- description -->
				<xsl:if test="marc:datafield[@tag='500']">
					<dc:description>
						<xsl:for-each select="marc:datafield[@tag='500']/marc:subfield[@code='a' and not(fn:starts-with(text(),'Segn.:')) and not(fn:starts-with(text(),'Marca')) and not(fn:starts-with(text(),'Colophon')) and not(fn:contains(text(),'[ast]'))]">
							<xsl:if test="position() ne 1"> ; </xsl:if>
							<xsl:choose>
								<xsl:when test="fn:ends-with(.,'.')"><xsl:value-of select="fn:substring(.,1,string-length(.)-1)"/></xsl:when>
								<xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>
					</dc:description>
				</xsl:if>
				<xsl:for-each select="marc:datafield[@tag='955']">
					<xsl:variable name="valB"><xsl:value-of select="fn:replace(normalize-space(marc:subfield[@code='b']),'\s+','')"/></xsl:variable>
					<xsl:if test="fn:replace($gbsIdentifier,'\s+','') eq $valB">
						<xsl:if test="marc:subfield[@code='v']">
							<dc:description><xsl:value-of select="normalize-space(marc:subfield[@code='v'])"/></dc:description>
						</xsl:if>
					</xsl:if>
				</xsl:for-each>
				<!-- numerazione -->
				<xsl:for-each select="marc:datafield[@tag='362']">
					<xsl:if test="marc:subfield[@code='a']">
						<dc:description>[numerazione] <xsl:value-of select="normalize-space(marc:subfield[@code='a'])"/></dc:description>
					</xsl:if>
				</xsl:for-each>

				<!-- contributor -->
				<xsl:for-each select="marc:datafield[@tag='700']|marc:datafield[@tag='710']">
					<xsl:variable name="type"><xsl:value-of select="marc:subfield[@code='4']"/></xsl:variable>
					<xsl:if test="not($type eq '610') and not($type eq '650') and not($type eq '750')">
						<dc:contributor>
							<xsl:value-of select="marc:subfield[@code='a']"/>
							<xsl:if test="marc:subfield[@code='b']"> <xsl:value-of select="marc:subfield[@code='b']"/></xsl:if>
							<xsl:if test="marc:subfield[@code='d']"> <xsl:value-of select="normalize-space(marc:subfield[@code='d'])"/></xsl:if>
							<xsl:if test="@tag='710'">
								<xsl:if test="marc:subfield[@code='n']"> <xsl:value-of select="normalize-space(marc:subfield[@code='n'])"/></xsl:if>
							</xsl:if>
							<xsl:variable name="v4"><xsl:value-of select="marc:subfield[@code='4']"/></xsl:variable>
							<xsl:if test="not($type eq '570') and fn:string-length($v4) gt 0 "> [<xsl:value-of select="normalize-space(fn:lower-case(ic:getRuolo($v4)))"/>]</xsl:if>
						</dc:contributor>
					</xsl:if>
				</xsl:for-each>

				<!-- date -->
				<xsl:variable name="con"><xsl:value-of select="fn:substring(marc:controlfield[@tag='008'],7,1)"/></xsl:variable>
				<xsl:variable name="date1"><xsl:value-of select="fn:substring(marc:controlfield[@tag='008'],8,4)"/></xsl:variable>
				<xsl:variable name="date2"><xsl:value-of select="fn:replace(fn:substring(marc:controlfield[@tag='008'],12,4),'\s+','')"/></xsl:variable>
				<xsl:choose>
					<xsl:when test="$date1"><dc:date><xsl:value-of select="$date1"/><xsl:if test="$con eq 'c'">-</xsl:if></dc:date></xsl:when>
					<xsl:when test="marc:datafield[@tag='260']/marc:subfield[@code='c']"><dc:date><xsl:value-of select="marc:datafield[@tag='260']/marc:subfield[@code='c']"/></dc:date></xsl:when>
				</xsl:choose>
				<xsl:if test="fn:string-length($date2) gt 0 and ($date1 ne $date2) and ($date2 ne '9999') and ($con ne 'c')">
					<dc:date><xsl:value-of select="$date2"/></dc:date>
				</xsl:if>

				<!-- type -->
				<dc:type><xsl:value-of   select="ic:getTypeFromLeader(fn:substring(marc:leader,7,1))"/></dc:type>

				<!-- format -->
				<xsl:for-each select="marc:datafield[@tag='300']">
					<dc:format>
						<xsl:value-of select="normalize-space(marc:subfield[@code='a'])"/>
						<xsl:if test="marc:subfield[@code='b']"> : <xsl:value-of select="normalize-space(marc:subfield[@code='b'])"/></xsl:if>
						<xsl:if test="marc:subfield[@code='c']"> ; <xsl:value-of select="normalize-space(marc:subfield[@code='c'])"/></xsl:if>
						<xsl:if test="marc:subfield[@code='e']"><xsl:text> + </xsl:text><xsl:value-of select="normalize-space(marc:subfield[@code='e'])"/></xsl:if>
					</dc:format>
				</xsl:for-each>

				<!-- language -->
				<xsl:for-each select="marc:datafield[@tag='041']"><dc:language><xsl:value-of select="normalize-space(.)"/></dc:language></xsl:for-each>
				

   				<!-- titolo uniforme -->
   				<xsl:for-each select="marc:datafield[@tag='240']"><dc:relation>‘titolo uniforme:’ <xsl:value-of select="normalize-space(marc:subfield[@code='a'])"/></dc:relation></xsl:for-each>

				<!-- collana -->
				<xsl:for-each select="marc:datafield[@tag='490']">
					<dc:relation>'collana:' <xsl:value-of select="marc:subfield[@code='a'][1]"/>
						<xsl:if test="marc:subfield[@code='v']"> ; <xsl:value-of select="marc:subfield[@code='v']"/></xsl:if>
					</dc:relation>
				</xsl:for-each>


				<!-- relation -->
				<xsl:for-each select="marc:datafield[@tag='777']">
					<dc:relation>‘pubblicato con:’
						<xsl:choose>
							<xsl:when test="$level eq 's'"><xsl:value-of select="normalize-space(marc:subfield[@code='a'])"/><xsl:if test="marc:subfield[@code='t']"> <xsl:value-of select="normalize-space(marc:subfield[@code='t'])"/><xsl:value-of select="normalize-space(marc:subfield[@code='t'])"/></xsl:if></xsl:when>
							<xsl:otherwise><xsl:value-of select="normalize-space(marc:subfield[@code='t'])"/></xsl:otherwise>
						</xsl:choose>
					</dc:relation>
				</xsl:for-each>
				<xsl:for-each select="marc:datafield[@tag='787']">
					<dc:relation><xsl:value-of select="normalize-space(.)"/></dc:relation>
				</xsl:for-each>

				<!-- location -->
				<mag:holdings>
					<xsl:if test="//gbs:sourceLibrary">
						<mag:library><xsl:value-of select="ic:getGbsLibrary(//gbs:sourceLibrary)"/></mag:library>
					</xsl:if>
					<xsl:for-each select="marc:datafield[@tag='955']">
						<xsl:variable name="valB"><xsl:value-of select="fn:replace(normalize-space(marc:subfield[@code='b']),'\s+','')"/></xsl:variable>
						<xsl:if test="fn:replace($gbsIdentifier,'\s+','') eq $valB">
							<xsl:if test="marc:subfield[@code='b']">
								<mag:inventory_number><xsl:value-of select="$valB"/></mag:inventory_number>
							</xsl:if>
							<xsl:if test="marc:subfield[@code='3']">
								<mag:shelfmark><xsl:value-of select="normalize-space(marc:subfield[@code='3'])"/></mag:shelfmark>
							</xsl:if>
						</xsl:if>
					</xsl:for-each>
				</mag:holdings>

				<!-- piece -->
				<xsl:if test="$level = 's'">
					<xsl:for-each select="marc:datafield[@tag='955']">
						<xsl:variable name="valB"><xsl:value-of select="fn:replace(normalize-space(marc:subfield[@code='b']),'\s+','')"/></xsl:variable>
						<xsl:if test="fn:replace($gbsIdentifier,'\s+','') eq $valB">
							<mag:piece>
								<mag:year><xsl:value-of select="fn:replace(marc:subfield[@code='v'],'([^0-9]?)([0-9]{4})(.*)','$2')"/></mag:year>
								<xsl:variable name="iss"><xsl:value-of select="fn:replace(marc:subfield[@code='v'],'(\s*)([0-9,\-]*)(.+)','$3')"/></xsl:variable>
								<xsl:variable name="iss2"><xsl:value-of select="normalize-space(fn:replace($iss,'(.+)(\([0-9]{4}\))(.+)','$1$3'))"/></xsl:variable>
								<xsl:variable name="iss3"><xsl:value-of select="normalize-space(fn:replace($iss2,'(.+)(\([0-9]{4}\-[0-9]{4}\))(.+)','$1$3'))"/></xsl:variable>
								<mag:issue><xsl:value-of select="$iss3"/></mag:issue>
							</mag:piece>
						</xsl:if>
					</xsl:for-each>
				</xsl:if>

			</xsl:for-each>
    		
    	</mag:bib>
    	<mag:stru></mag:stru>
    	
    	<!-- sezione ocr e img -->
    	<xsl:for-each select="//METS:fileSec/METS:fileGrp[@USE='image' or @USE='OCR']/METS:file">
    		<xsl:variable name="idFile"><xsl:value-of select="@ID"/></xsl:variable>
    		<xsl:variable name="name"><xsl:choose><xsl:when test="../@USE='image'">mag:img</xsl:when><xsl:otherwise>mag:ocr</xsl:otherwise></xsl:choose></xsl:variable>
    		<xsl:element name="{$name}">				    		
    			<mag:sequence_number><xsl:value-of select="//METS:div[@TYPE='page']/METS:fptr[@FILEID=$idFile]/../@ORDER"/></mag:sequence_number>
    			<mag:nomenclature><xsl:value-of select="//METS:div[@TYPE='page']/METS:fptr[@FILEID=$idFile]/../@ORDER"/></mag:nomenclature>
    			<mag:usage>3</mag:usage>
    			<mag:file Location="URL" xlink:type="simple">
    				<xsl:attribute name="xlink:href"><xsl:value-of select="METS:FLocat/@xlinkGoogle:href"/></xsl:attribute>
    			</mag:file>
    			<xsl:if test="@CHECKSUMTYPE='MD5' and not(../@USE='image')">
    				<mag:md5><xsl:value-of select="@CHECKSUM"/></mag:md5>
    			</xsl:if>

				<xsl:if test="$name='mag:ocr'">
					<mag:source xlink:type="simple">
						<xsl:attribute name="xlink:href"><xsl:value-of select="fn:replace(METS:FLocat/@xlinkGoogle:href,'.txt','.jpg')"/></xsl:attribute></mag:source>
				</xsl:if>

				<xsl:if test="number(@SIZE) gt 0 and not(../@USE='image')">
					<mag:filesize><xsl:value-of select="@SIZE"/></mag:filesize>
				</xsl:if>

				<xsl:if test="../@USE='image'">
					<xsl:variable name="jsonInfo"><xsl:value-of select="ic:getFileDescription($baseDir, METS:FLocat/@xlinkGoogle:href)"/></xsl:variable>
					<xsl:choose>
						<xsl:when test="fn:string-length($jsonInfo) gt 0">
							<mag:md5><xsl:value-of select="ic:getJsonValue($jsonInfo,'md5')"/></mag:md5>
							<mag:filesize><xsl:value-of select="ic:getJsonValue($jsonInfo,'filesize')"/></mag:filesize>
							<mag:image_dimensions>
								<xsl:if test="fn:string-length($jsonInfo) gt 0">
									<niso:imagelength><xsl:value-of select="ic:getJsonValue($jsonInfo,'height')"/></niso:imagelength>
									<niso:imagewidth><xsl:value-of select="ic:getJsonValue($jsonInfo,'width')"/></niso:imagewidth>
								</xsl:if>
							</mag:image_dimensions>
						</xsl:when>
						<xsl:otherwise>
							<mag:md5><xsl:value-of select="@CHECKSUM"/></mag:md5>
							<mag:filesize><xsl:value-of select="@SIZE"/></mag:filesize>
							<mag:image_dimensions/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:if>
				<mag:format>
					<niso:name><xsl:choose><xsl:when test="@MIMETYPE='image/jpeg'">JPG</xsl:when><xsl:otherwise>TXT</xsl:otherwise></xsl:choose></niso:name>
					<niso:mime><xsl:value-of select="@MIMETYPE"/></niso:mime>
					<niso:compression><xsl:choose><xsl:when test="@MIMETYPE='image/jpeg'">JPG</xsl:when><xsl:otherwise>Uncompressed</xsl:otherwise></xsl:choose></niso:compression>
				</mag:format>


				<mag:datetimecreated><xsl:value-of select="@CREATED"/></mag:datetimecreated>
    		</xsl:element>
    	</xsl:for-each>
    	
    	</mag:metadigit>
    </xsl:template>
</xsl:stylesheet>
