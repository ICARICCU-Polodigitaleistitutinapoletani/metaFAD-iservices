<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="3.0"
                xmlns:fn="http://www.w3.org/2005/xpath-functions"
                xmlns:magextension="http://magextension.it/saxon-extension"
                xmlns:xsk="http://www.w3.org/1999/XSL/Transform" xmlns:xsò="http://www.w3.org/1999/XSL/Transform"
                exclude-result-prefixes="fn magextension"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:dc="http://purl.org/dc/elements/1.1/"
                xmlns:mag="http://www.iccu.sbn.it/metaAG1.pdf"
                xmlns:sbn="http://www.iccu.sbn.it/opencms/opencms/documenti/2016/SBNMarcv202.xsd"
>
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
    <xsl:param name="type"></xsl:param>

    <xsl:template match="/">
        <!--xsi:schmeaLocation="http://www.iccu.sbn.it/metaAG1.pdf metadigit.xsd"-->
        <mag:metadigit >
            <xsl:apply-templates/>
        </mag:metadigit>
    </xsl:template>

    <!-- -->
    <xsl:template match="sbn:Documento/sbn:DatiDocumento">
        <!--<xsl:variable name="level"><xsl:choose>
            <xsl:when test="$level eq 'M'">m</xsl:when>
            <xsl:when test="$level eq 'N'">a</xsl:when>
            <xsl:when test="$level eq 'S'">s</xsl:when>
            </xsl:choose></xsl:variable>-->
        <mag:bib>
            <xsl:attribute name="level"><xsl:value-of select="sbn:guida/@livelloBibliografico"/></xsl:attribute>
            <dc:identifier><xsl:value-of select="sbn:T001"/></dc:identifier>

            <xsl:variable name="isAntico"><xsl:choose>
                <xsl:when test="fn:matches(sbn:T001,'^...E')">true</xsl:when>
                <xsl:otherwise>false</xsl:otherwise>
            </xsl:choose></xsl:variable>
            <xsl:variable name="isMaterialeGrafico"><xsl:choose>
                <xsl:when test="magextension:getTipoDocumento(sbn:guida/@tipoRecord) eq 'materiale grafico'">true</xsl:when>
                <xsl:otherwise>false</xsl:otherwise>
            </xsl:choose></xsl:variable>
            <xsl:variable name="isCartografia"><xsl:choose>
                <xsl:when test="fn:contains(magextension:getTipoDocumento(sbn:guida/@tipoRecord),'cartografia')">true</xsl:when>
                <xsl:otherwise>false</xsl:otherwise>
            </xsl:choose></xsl:variable>
            <xsl:variable name="isMusica"><xsl:choose>
                <xsl:when test="fn:starts-with(magextension:getTipoDocumento(sbn:guida/@tipoRecord),'musica')">true</xsl:when>
                <xsl:otherwise>false</xsl:otherwise>
            </xsl:choose></xsl:variable>
            <xsl:variable name="isMusicaManoscritta"><xsl:choose>
                <xsl:when test="fn:starts-with(magextension:getTipoDocumento(sbn:guida/@tipoRecord),'musica manoscritta')">true</xsl:when>
                <xsl:otherwise>false</xsl:otherwise>
            </xsl:choose></xsl:variable>
            <xsl:variable name="isVideo"><xsl:choose>
                <xsl:when test="fn:contains(magextension:getTipoDocumento(sbn:guida/@tipoRecord),'registrazione') or
                        fn:contains(magextension:getTipoDocumento(sbn:guida/@tipoRecord),'video')">true</xsl:when>
                <xsl:otherwise>false</xsl:otherwise>
            </xsl:choose></xsl:variable>
            <xsl:variable name="isLibretto"><xsl:choose>
                <xsl:when test="sbn:T105/sbn:a_105_11 eq 'i' or sbn:T105bis/sbn:a_105_11 eq 'i' or sbn:T140/sbn:a_140_17 eq 'da'  or
                (       (not(T105/sbn:a_105_11) or normalize-space(sbn:T105/sbn:a_105_11) eq '')
                    and (not(sbn:T140/sbn:a_140_17) or normalize-space(sbn:T140/sbn:a_140_17) eq '')
                    and sbn:T125/sbn:b_125 eq 'b')">true</xsl:when>
                <xsl:otherwise>false</xsl:otherwise>
            </xsl:choose></xsl:variable>
            
            <xsl:if test="$isVideo eq 'true'">
                <xsl:for-each select="sbn:T071">
                    <xsl:variable name="temp"><xsl:value-of select="magextension:formatSpazio(.,'a|c')"/></xsl:variable>
                    <xsl:if test="fn:contains($temp,'EAN')"><dc:identifier><xsl:value-of select="fn:replace($temp,'EAN/UPC','EAN')"/></dc:identifier></xsl:if>
                </xsl:for-each>
            </xsl:if>
            <xsl:if test="sbn:guida/@tipoRecord">
                <dc:type><xsl:value-of select="magextension:getTipoDocumento(sbn:guida/@tipoRecord)"/></dc:type>
            </xsl:if>
            <xsl:if test="$isLibretto eq 'true'">
                <dc:type>libretto</dc:type>
            </xsl:if>
            <xsl:choose>
                <xsl:when test="sbn:T100/sbn:a_100_8 eq 'e'">
                    <dc:date><xsl:value-of select="sbn:T100/sbn:a_100_9"/></dc:date>
                </xsl:when>
                <!-- per i periodici -->
                <xsl:when test="sbn:T100/sbn:a_100_8 eq 'a' and sbn:guida/@livelloBibliografico eq 's'">
                    <dc:date><xsl:value-of select="sbn:T100/sbn:a_100_9"/>-</dc:date>
                </xsl:when>
                <!-- per i libri moderni -->
                <xsl:when test="sbn:T100/sbn:a_100/sbn:a_100_8 eq 'g' and not(sbn:T100/sbn:a_100_13)">
                    <dc:date><xsl:value-of select="sbn:T100/sbn:a_100_9"/>-</dc:date>
                </xsl:when>
                <xsl:otherwise>
                    <dc:date><xsl:value-of select="sbn:T100/sbn:a_100_9"/></dc:date>
                    <xsl:if test="sbn:T100/sbn:a_100_9 ne sbn:T100/sbn:a_100_13">
                        <dc:date><xsl:value-of select="sbn:T100/sbn:a_100_13"/></dc:date>
                    </xsl:if>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:for-each select="sbn:T101/sbn:a_101">
                <xsl:if test="fn:lower-case(.) ne 'abs'">
                    <dc:language><xsl:value-of select="fn:lower-case(magextension:getLanguage(fn:lower-case(.)))"/></dc:language>
                </xsl:if>
            </xsl:for-each>

            <!-- materiale grafico -->
            <xsl:for-each select="sbn:T116">
                <xsl:if test="sbn:a_116_0">
                    <dc:subject><xsl:value-of select="magextension:getSpecificaMateriale(sbn:a_116_0)"/></dc:subject>
                </xsl:if>
                <xsl:for-each select="sbn:a_116_4">
                    <dc:subject><xsl:value-of select="magextension:getTecd(.)"/></dc:subject>
                </xsl:for-each>
                <xsl:for-each select="sbn:a_116_6">
                    <dc:subject><xsl:value-of select="magextension:getTecd(.)"/></dc:subject>
                </xsl:for-each>
                <xsl:for-each select="sbn:a_116_8">
                    <dc:subject><xsl:value-of select="magextension:getTecd(.)"/></dc:subject>
                </xsl:for-each>
                <xsl:if test="sbn:a_116_10">
                    <dc:subject><xsl:value-of select="magextension:getTecs(sbn:a_116_10)"/></dc:subject>
                </xsl:if>
                <xsl:if test="sbn:a_116_12">
                    <dc:subject><xsl:value-of select="magextension:getTecs(sbn:a_116_12)"/></dc:subject>
                </xsl:if>
                <xsl:if test="sbn:a_116_14">
                    <dc:subject><xsl:value-of select="magextension:getTecs(sbn:a_116_14)"/></dc:subject>
                </xsl:if>
                <xsl:if test="sbn:a_116_16">
                    <dc:subject><xsl:value-of select="magextension:getDesf(sbn:a_116_16)"/></dc:subject>
                </xsl:if>
            </xsl:for-each>

            <!-- Title -->
            <xsl:for-each select="sbn:T200[@id1 ne '0']">
                <dc:title><xsl:value-of select="magextension:format(.,'a|c|d|e|f|g')"/></dc:title>
            </xsl:for-each>
            <xsl:if test="count(sbn:T200[@id1 ne '0']) eq 0">
                <xsl:for-each select="//sbn:LegameDoc[@tipoLegame='461' or @tipoLegame='462' or @tipoLegame='463']">
                    <dc:title>[<xsl:value-of select="magextension:format(sbn:DocumentoLegato/sbn:DatiDocumento/sbn:T200,'a|e')"/>]
                        <xsl:value-of select="//sbn:T200[@id1='0']/sbn:a_200"/></dc:title>
                </xsl:for-each>
            </xsl:if>

            <xsl:for-each select="sbn:T206">
                <mag:geo_coord><xsl:value-of select="magextension:format(.,'a')"/></mag:geo_coord>
            </xsl:for-each>

            <xsl:variable name="pubAdd"><xsl:if test="($isAntico eq 'true' or $isLibretto eq 'true') and (sbn:T620/sbn:d_620 or //sbn:LegameElementoAut[@tipoLegame='712' and (@relatorCode='610' or @relatorCode='650')])"><xsl:text> [</xsl:text><xsl:variable
                    name="temp620"><xsl:value-of select="sbn:T620/sbn:d_620"/></xsl:variable><xsl:if
                    test="count(sbn:T620/sbn:d_620[.=$temp620]) eq 0"><xsl:value-of select="sbn:T620/sbn:d_620"/></xsl:if><xsl:for-each
                    select="//sbn:LegameElementoAut[@tipoLegame='712' and (@relatorCode='610' or @relatorCode='650')]"><xsl:if test="//sbn:T620/sbn:d_620 or position() gt 1"><xsl:text> ; </xsl:text></xsl:if><xsl:value-of
                    select="sbn:ElementoAutLegato/sbn:DatiElementoAut/sbn:T200/sbn:a_200"/></xsl:for-each><xsk:text>]</xsk:text></xsl:if></xsl:variable>
            <xsl:for-each select="sbn:T210">
                <xsl:choose>
                    <xsl:when test="
                            (fn:matches(sbn:d_210,'(\d{4})')
                            and fn:string-length(normalize-space(sbn:d_210)) eq 4)
                                or
                                (fn:matches(sbn:d_210,'(\d{4}\-\d{4})')
                                and fn:string-length(normalize-space(sbn:d_210)) eq 9 )">
                        <dc:publisher><xsl:value-of select="magextension:format(.,'a|c|e|g')"/><xsl:if test="normalize-space($pubAdd) ne '[]'"><xsl:value-of
                                select="$pubAdd"/></xsl:if></dc:publisher>
                    </xsl:when>
                    <xsl:otherwise>
                        <dc:publisher><xsl:value-of select="magextension:format(.,'a|c|d|e|g')"/><xsl:if test="normalize-space($pubAdd) ne '[]'"><xsl:value-of
                                select="$pubAdd"/></xsl:if></dc:publisher>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:for-each>
            
            <xsl:if test="not(sbn:T100/sbn:a_100_9)">
                  <dc:date><xsl:value-of select="sbn:T210/sbn:d_210"/></dc:date>
            </xsl:if>
            <dc:format><xsl:value-of select="magextension:format(sbn:T215,'a|c|d|e')"/></dc:format>
            <xsl:choose>

                <!--
                nessun replace fine stringa con
                fn:replace($value,'([^\.])([\.])$','$1')
                -->
                <!--periodici -->
                <xsl:when test="sbn:guida/@livelloBibliografico eq 's'">
                    <dc:description><xsl:for-each select="sbn:T3XX[@tipoNota='326']|sbn:T3XX[@tipoNota='300']|sbn:T207|sbn:T950/sbn:b_950[1]">
                        <xsl:sort select="magextension:getOrderDescription(.)" />
                        <xsl:variable name="content"><xsl:value-of select="magextension:format(.,'a')"/></xsl:variable>
                        <xsl:variable name="value"><xsl:choose>
                        <xsl:when test="@tipoNota='326'"><xsl:if test="count(../sbn:T3XX[@tipoNota='300']/sbn:a_3XX[.= $content]) eq 0"><xsl:value-of
                                select="$content"/></xsl:if></xsl:when>
                        <xsl:when test="@tipoNota='300'"><xsl:value-of select="$content"/></xsl:when>
                        <xsl:when test="name() eq 'T207'">[numerazione] <xsl:value-of select="$content"/></xsl:when>
                        <xsl:when test="name() eq 'b_950'">[consistenza] <xsl:choose>
                                <xsl:when test="fn:string-length(.) gt 0"><xsl:value-of select="."/></xsl:when>
                            <xsl:otherwise><xsl:value-of select="../sbn:c_950[1]"/></xsl:otherwise>
                            </xsl:choose></xsl:when>
                        <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
                    </xsl:choose></xsl:variable><xsl:if test="position() gt 1 and fn:string-length($value) gt 0"><xsl:text> ; </xsl:text></xsl:if><xsl:value-of select="$value"/></xsl:for-each></dc:description>
                    <!-- i 326 doppioni -->
                    <xsl:for-each select="sbn:T3XX[@tipoNota='326']">
                        <xsl:variable name="content"><xsl:value-of select="magextension:format(.,'a')"/></xsl:variable>
                        <xsl:if test="count(../sbn:T3XX[@tipoNota='300']/sbn:a_3XX[.= $content]) gt 0">
                            <dc:description><xsl:value-of select="$content"/></dc:description>
                        </xsl:if>
                    </xsl:for-each>
                </xsl:when>

                <!--libro antico -->
                <xsl:when test="$isAntico eq 'true' or $isCartografia eq 'true'">
                    <xsl:value-of select="magextension:getCounter('0')"/>
                    <dc:description><xsl:for-each select="sbn:T950/sbn:e_950_44|sbn:T3XX[@tipoNota='316' or @tipoNota='303' or @tipoNota='300']|sbn:T921">
                        <xsl:sort select="magextension:getOrderAnticoDescription(.)" />
                        <xsl:variable name="value"><xsl:choose>
                            <xsl:when test="name() eq 'T921'"><xsl:if test="sbn:b_921 ne 'Marca non controllata'">'marca:' <xsl:value-of select="sbn:b_921"/></xsl:if></xsl:when>
                            <xsl:when test="@tipoNota='303'">'dedica:' <xsl:value-of select="magextension:format(.,'a')"/></xsl:when>
                            <xsl:when test="@tipoNota='316'"><xsl:variable name="tt"><xsl:value-of select="magextension:format(.,'a|5')"/></xsl:variable><xsl:if
                                    test="count(../sbn:T950/sbn:e_950_44[.= $tt]) eq 0"><xsl:value-of select="$tt"/></xsl:if></xsl:when>
                            <xsl:when test="@tipoNota='300'"><xsl:variable name="tempVal"><xsl:value-of select="magextension:format(.,'a')"/></xsl:variable><xsl:if
                                    test="not(fn:starts-with($tempVal,'Marca')) and not(fn:starts-with($tempVal,'Segn.')) and
                                            not(fn:starts-with($tempVal,'Colophon'))"><xsl:value-of select="$tempVal"/></xsl:if></xsl:when>
                            <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
                        </xsl:choose></xsl:variable>
                        <xsl:if test="fn:string-length(normalize-space($value)) gt 0"><xsl:value-of select="magextension:getCounter('1')"/></xsl:if>
                        <xsl:if test="fn:number(magextension:getCounter('-1')) gt 1 and fn:string-length(normalize-space($value)) gt 0"><xsl:text> ; </xsl:text></xsl:if><xsl:value-of select="$value"/></xsl:for-each></dc:description>
                </xsl:when>

                <!--libretto -->
                <xsl:when test="$isLibretto eq 'true' or $isMusica  eq 'true'">
                    <xsl:value-of select="magextension:getCounter('0')"/>
                    <xsl:value-of select="magextension:getCounter2('0')"/>
                    <xsl:variable name="regex">\s&lt;(.+)&gt;</xsl:variable>
                    <dc:description><xsl:for-each select="sbn:T950/sbn:e_950_44|sbn:T3XX[@tipoNota='316' or @tipoNota='303' or @tipoNota='300']|sbn:T921|sbn:T922|sbn:T923|sbn:T927">
                        <xsl:sort select="magextension:getOrderAnticoDescription(.)" />
                        <xsl:variable name="value"><xsl:choose>
                            <xsl:when test="name() eq 'T921'"><xsl:if test="sbn:b_921 ne 'Marca non controllata'">'marca:' <xsl:value-of select="sbn:b_921"/></xsl:if></xsl:when>
                            <xsl:when test="name() eq 'T922'"><xsl:value-of select="sbn:t_922/t_922"/></xsl:when>
                            <xsl:when test="name() eq 'T923'"><xsl:value-of select="magextension:format(.,'b!e|h')"/></xsl:when>
                            <xsl:when test="name() eq 'T927'"><xsl:if test="fn:number(magextension:getCounter2('-1')) eq 0">Personaggi e interpreti: </xsl:if>
                                <xsl:value-of select="magextension:getCounter2('1')"/>
                                <xsl:value-of select="fn:replace(magextension:format(.,'a|c'),$regex,'')"/> <xsl:if
                                    test="fn:string-length(normalize-space(sbn:b_927)) gt 0 and not(fn:matches(normalize-space(sbn:b_927),'^(\s+)$'))">, <xsl:value-of
                                    select="magextension:getOrga(sbn:b_927)"/></xsl:if></xsl:when>
                            <xsl:when test="@tipoNota='303'">'dedica:' <xsl:value-of select="magextension:format(.,'a')"/></xsl:when>
                            <xsl:when test="@tipoNota='316'"><xsl:variable name="tt"><xsl:value-of select="magextension:format(.,'a|5')"/></xsl:variable><xsl:if
                                    test="count(../sbn:T950/sbn:e_950_44[.= $tt]) eq 0"><xsl:value-of select="$tt"/></xsl:if></xsl:when>
                            <xsl:when test="@tipoNota='300'"><xsl:variable name="tempVal"><xsl:value-of select="magextension:format(.,'a')"/></xsl:variable><xsl:if
                                    test="not(fn:starts-with($tempVal,'Marca')) and not(fn:starts-with($tempVal,'Segn.')) and
                                            not(fn:starts-with($tempVal,'Colophon'))"><xsl:value-of select="$tempVal"/></xsl:if></xsl:when>
                            <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
                        </xsl:choose></xsl:variable>
                        <xsl:if test="fn:string-length(normalize-space($value)) gt 0"><xsl:value-of select="magextension:getCounter('1')"/></xsl:if>
                        <xsl:if test="fn:number(magextension:getCounter('-1')) gt 1 and fn:string-length(normalize-space($value)) gt 0"><xsl:text> ; </xsl:text></xsl:if><xsl:value-of select="$value"/></xsl:for-each></dc:description>
                </xsl:when>

                <!--Materiale Grafico -->
                <xsl:when test="$isMaterialeGrafico eq 'true'">
                    <xsl:value-of select="magextension:getCounter('0')"/>
                    <dc:description><xsl:for-each select="sbn:T950/sbn:e_950_44|sbn:T3XX[@tipoNota='316' or @tipoNota='330' or @tipoNota='327' or @tipoNota='300']">
                        <xsl:sort select="magextension:getOrderGraficoDescription(.)" />
                        <xsl:variable name="value"><xsl:choose>
                            <xsl:when test="@tipoNota='316'"><xsl:variable name="tt"><xsl:value-of select="magextension:format(.,'a|5')"/></xsl:variable><xsl:if
                                    test="count(../sbn:T950/sbn:e_950_44[.= $tt]) eq 0"><xsl:value-of select="$tt"/></xsl:if></xsl:when>
                            <xsl:when test="@tipoNota='300'"><xsl:variable name="tempVal"><xsl:value-of select="magextension:format(.,'a')"/></xsl:variable><xsl:if
                                    test="not(fn:starts-with($tempVal,'Segn.'))"><xsl:value-of select="$tempVal"/></xsl:if></xsl:when>
                            <xsl:otherwise><xsl:value-of select="magextension:format(.,'a')"/></xsl:otherwise>
                        </xsl:choose></xsl:variable>
                        <xsl:if test="fn:string-length(normalize-space($value)) gt 0"><xsl:value-of select="magextension:getCounter('1')"/></xsl:if>
                        <xsl:if test="fn:number(magextension:getCounter('-1')) gt 1 and fn:string-length(normalize-space($value)) gt 0"><xsl:text> ; </xsl:text></xsl:if><xsl:value-of select="$value"/></xsl:for-each></dc:description>
                </xsl:when>

                <!--Materiale Grafico -->
                <xsl:when test="$isVideo eq 'true'">
                    <xsl:value-of select="magextension:getCounter('0')"/>
                    <dc:description><xsl:for-each select="sbn:T950/sbn:e_950_44|sbn:T3XX[@tipoNota='316' or @tipoNota='327' or @tipoNota='300']|sbn:T927">
                        <xsl:sort select="magextension:getOrderVideoDescription(.)" />
                        <xsl:variable name="value"><xsl:choose>
                            <xsl:when test="@tipoNota='316'"><xsl:variable name="tt"><xsl:value-of select="magextension:format(.,'a|5')"/></xsl:variable><xsl:if
                                    test="count(../sbn:T950/sbn:e_950_44[.= $tt]) eq 0"><xsl:value-of select="$tt"/></xsl:if></xsl:when>
                            <xsl:when test="@tipoNota='300'"><xsl:variable name="tempVal"><xsl:value-of select="magextension:format(.,'a')"/></xsl:variable><xsl:if
                                    test="not(fn:starts-with($tempVal,'Segn.'))"><xsl:value-of select="$tempVal"/></xsl:if></xsl:when>
                            <xsl:otherwise><xsl:value-of select="magextension:format(.,'a')"/></xsl:otherwise>
                        </xsl:choose></xsl:variable>
                        <xsl:if test="fn:string-length(normalize-space($value)) gt 0"><xsl:value-of select="magextension:getCounter('1')"/></xsl:if>
                        <xsl:if test="fn:number(magextension:getCounter('-1')) gt 1 and fn:string-length(normalize-space($value)) gt 0"><xsl:text> ; </xsl:text></xsl:if><xsl:value-of select="$value"/></xsl:for-each></dc:description>
                </xsl:when>

                <!-- monografia moderna -->
                <xsl:when test="sbn:guida/@livelloBibliografico eq 'm'">
                    <dc:description><xsl:for-each select="sbn:T950/sbn:e_950_44|sbn:T3XX[@tipoNota='316']|sbn:T3XX[@tipoNota='300']">
                        <xsl:variable name="value"><xsl:choose>
                            <xsl:when test="@tipoNota='316'"><xsl:value-of select="magextension:format(.,'a|5')"/></xsl:when>
                            <xsl:when test="@tipoNota='300'"><xsl:value-of select="magextension:format(.,'a')"/></xsl:when>
                            <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
                        </xsl:choose></xsl:variable><xsl:if test="position() gt 1"><xsl:text> ; </xsl:text></xsl:if><xsl:value-of select="$value"/></xsl:for-each></dc:description>
                </xsl:when>
            </xsl:choose>

            <xsl:for-each select="//sbn:LegameDoc">
                <xsl:if test="@tipoLegame='461' or @tipoLegame='410'">
                    <dc:relation>'collana:' <xsl:value-of select="magextension:format(sbn:DocumentoLegato/sbn:DatiDocumento/sbn:T200,'a|e|v')"/></dc:relation>
                </xsl:if>
            </xsl:for-each>
            <xsl:for-each select="//sbn:LegameDoc">
                <xsl:if test="@tipoLegame='423'">
                    <dc:relation>'pubblicato con:' <xsl:value-of select="magextension:format(sbn:DocumentoLegato/sbn:DatiDocumento/sbn:T200,'a|e|v')"/></dc:relation>
                </xsl:if>
            </xsl:for-each>
            <xsl:for-each select="//sbn:LegameDoc">
                <xsl:if test="@tipoLegame='488'">
                    <dc:relation><xsl:value-of select="magextension:format(sbn:DocumentoLegato/sbn:DatiDocumento/sbn:T200,'a|e')"/></dc:relation>
                </xsl:if>
            </xsl:for-each>
            <xsl:if test="sbn:guida/@livelloBibliografico eq 's'">
                <xsl:for-each select="//sbn:LegameDoc">
                    <xsl:if test="fn:matches(@tipoLegame,'43.') or fn:matches(@tipoLegame,'44.')">
                        <dc:relation><xsl:value-of select="magextension:format(sbn:DocumentoLegato/sbn:DatiDocumento/sbn:T200,'a|e')"/></dc:relation>
                    </xsl:if>
                </xsl:for-each>
            </xsl:if>

            <!-- subtag sono diverse per tipo -->
            <xsl:variable name="partsSubTag"><xsl:choose>
                <xsl:when test="$isVideo eq 'true'">a|e|f</xsl:when>
                <xsl:otherwise>a|e</xsl:otherwise>
            </xsl:choose></xsl:variable>

            <xsl:if test="count(sbn:T200[@id1 ne '0']) gt 0">
                <xsl:for-each select="//sbn:LegameDoc">
                    <xsl:if test="@tipoLegame='461' or @tipoLegame='462'">
                        <dc:relation>'fa parte di:' <xsl:value-of select="magextension:format(sbn:DocumentoLegato/sbn:DatiDocumento/sbn:T200,$partsSubTag)"/>
                            <xsl:if test="sbn:idArrivo"><xsl:text> {</xsl:text><xsl:value-of select="sbn:idArrivo"/><xsl:text>}</xsl:text></xsl:if>
                        </dc:relation>
                    </xsl:if>
                </xsl:for-each>
                <xsl:for-each select="//sbn:LegameDoc">
                    <xsl:if test="@tipoLegame='463'">
                        <xsl:if test="//sbn:DatiDocumento[@naturaDoc eq 'A'] or //sbn:DatiDocumento/sbn:guida/@livelloBibliografico eq 'a'">
                            <dc:relation>'fa parte di:' <xsl:value-of select="magextension:format(sbn:DocumentoLegato/sbn:DatiDocumento/sbn:T200,$partsSubTag)"/>
                                <xsl:if test="sbn:idArrivo"><xsl:text> {</xsl:text><xsl:value-of select="sbn:idArrivo"/><xsl:text>}</xsl:text></xsl:if>
                            </dc:relation>
                        </xsl:if>

                        <xsl:if test="//sbn:DatiDocumento[@naturaDoc eq 'M'] or //sbn:DatiDocumento/sbn:guida/@livelloBibliografico eq 'm'">
                            <dc:relation>'comprende:' <xsl:value-of select="magextension:format(sbn:DocumentoLegato/sbn:DatiDocumento/sbn:T200,$partsSubTag)"/>
                                <xsl:if test="sbn:idArrivo"><xsl:text> {</xsl:text><xsl:value-of select="sbn:idArrivo"/><xsl:text>}</xsl:text></xsl:if>
                            </dc:relation>
                        </xsl:if>
                    </xsl:if>
                </xsl:for-each>
            </xsl:if>
            <xsl:for-each select="//sbn:LegameDoc">
                <xsl:if test="@tipoLegame='464'">
                    <dc:relation>'comprende:' <xsl:value-of select="magextension:format(sbn:DocumentoLegato/sbn:DatiDocumento/sbn:T200,$partsSubTag)"/>
                        <xsl:if test="sbn:idArrivo"><xsl:text> {</xsl:text><xsl:value-of select="sbn:idArrivo"/><xsl:text>}</xsl:text></xsl:if>
                    </dc:relation>
                </xsl:if>
            </xsl:for-each>
            <xsl:if test="$isLibretto eq 'false' and $isMusica eq 'false' and $isVideo eq 'false'">
                <xsl:for-each select="//sbn:LegameElementoAut">
                    <xsl:if test="@tipoLegame='500'">
                        <dc:relation>'titolo uniforme:' <xsl:value-of select="magextension:format(sbn:ElementoAutLegato/sbn:DatiElementoAut/sbn:T230,'a|e')"/></dc:relation>
                    </xsl:if>
                </xsl:for-each>
            </xsl:if>
            <xsl:for-each select="//sbn:LegameElementoAut">
                <xsl:if test="@tipoLegame='510'">
                    <dc:relation>'titolo parallelo:' <xsl:value-of select="magextension:format(sbn:ElementoAutLegato/sbn:DatiElementoAut/sbn:T230,'a')"/></dc:relation>
                </xsl:if>
            </xsl:for-each>
            <xsl:for-each select="//sbn:LegameElementoAut">
                <xsl:if test="@tipoLegame='517'">
                    <dc:relation><xsl:choose>
                        <xsl:when test="sbn:ElementoAutLegato/sbn:DatiElementoAut/sbn:T230/sbn_t_230 eq 'I'">'incipit: '</xsl:when>
                        <xsl:when test="sbn:ElementoAutLegato/sbn:DatiElementoAut/sbn:T230/sbn_t_230 eq 'T'">'titolo alternativo: '</xsl:when>
                        <xsl:otherwise>'variante del titolo:' </xsl:otherwise>
                    </xsl:choose><xsl:value-of select="magextension:format(sbn:ElementoAutLegato/sbn:DatiElementoAut/sbn:T230,'a')"/></dc:relation>
                </xsl:if>
            </xsl:for-each>
            <xsl:for-each select="//sbn:LegameElementoAut">
                <xsl:if test="@tipoLegame='560'">
                    <dc:relation>'fa parte di:' <xsl:value-of select="magextension:format(sbn:ElementoAutLegato/sbn:DatiElementoAut/sbn:T230,'a|e')"/></dc:relation>
                </xsl:if>
            </xsl:for-each>
            <xsl:for-each select="//sbn:LegameElementoAut">
                <!-- 607 solo per materiale cartografico? -->
                <xsl:if test="@tipoLegame='606' or @tipoLegame='607'">
                    <dc:subject><xsl:value-of select="fn:replace(magextension:format(sbn:ElementoAutLegato/sbn:DatiElementoAut/sbn:T250,'a|x'),' \- \| \- \| \- ',' - ')"/></dc:subject>
                </xsl:if>
            </xsl:for-each>
            <xsl:for-each select="//sbn:LegameElementoAut">
                <xsl:if test="@tipoLegame='676'">
                    <dc:subject><xsl:value-of select="fn:replace(magextension:format(sbn:ElementoAutLegato/sbn:DatiElementoAut/sbn:T250,'a|c'),' \- \| \- \| \- ',' - ')"/></dc:subject>
                </xsl:if>
            </xsl:for-each>
            <xsl:for-each select="//sbn:LegameElementoAut">
                <xsl:variable name="temp200"><xsl:value-of select="sbn:ElementoAutLegato/sbn:DatiElementoAut/sbn:T200/sbn:c_200"/></xsl:variable>
                <xsl:variable name="temp210"><xsl:value-of select="sbn:ElementoAutLegato/sbn:DatiElementoAut/sbn:T210/sbn:c_210"/></xsl:variable>
                <xsl:if test="@tipoLegame='700' or @tipoLegame='701' or @tipoLegame='710' or @tipoLegame='711'">
                    <xsl:if test="sbn:ElementoAutLegato/sbn:DatiElementoAut/sbn:T200">
                        <xsl:choose><xsl:when test="fn:contains($temp200,'&lt;omonini non identificati&gt;')
                                or fn:contains($temp200,'&lt;autore indifferenziato&gt;')">
                            <dc:creator><xsl:value-of select="magextension:format(sbn:ElementoAutLegato/sbn:DatiElementoAut/sbn:T200,'a|b|d|f')"/></dc:creator>
                        </xsl:when>
                            <xsl:otherwise>
                                <dc:creator><xsl:value-of select="magextension:format(sbn:ElementoAutLegato/sbn:DatiElementoAut/sbn:T200,'a|b|c|d|f')"/></dc:creator>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:if>
                    <!-- enti -->
                    <xsl:if test="sbn:ElementoAutLegato/sbn:DatiElementoAut/sbn:T210">
                        <xsl:choose><xsl:when test="fn:contains($temp210,'&lt;omonini non identificati&gt;')
                                or fn:contains($temp210,'&lt;autore indifferenziato&gt;')">
                            <dc:creator><xsl:value-of select="magextension:format(sbn:ElementoAutLegato/sbn:DatiElementoAut/sbn:T210,'a|b|d|f')"/></dc:creator>
                        </xsl:when>
                            <xsl:otherwise>
                                <dc:creator><xsl:value-of select="magextension:format(sbn:ElementoAutLegato/sbn:DatiElementoAut/sbn:T210,'a|b|c|d|f')"/></dc:creator>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:if>
                </xsl:if>
            </xsl:for-each>
            <xsl:for-each select="//sbn:LegameElementoAut">
                <xsl:variable name="temp200"><xsl:value-of select="sbn:ElementoAutLegato/sbn:DatiElementoAut/sbn:T200/sbn:c_200"/></xsl:variable>
                <xsl:variable name="temp210"><xsl:value-of select="sbn:ElementoAutLegato/sbn:DatiElementoAut/sbn:T210/sbn:c_210"/></xsl:variable>
                <xsl:if test="@tipoLegame='702' or @tipoLegame='712'">
                    <xsl:if test="sbn:ElementoAutLegato/sbn:DatiElementoAut/sbn:T200">
                        <xsl:choose><xsl:when test="fn:contains($temp210,'&lt;omonini non identificati&gt;')
                                or fn:contains($temp200,'&lt;autore indifferenziato&gt;')">
                                <dc:contributor><xsl:value-of select="magextension:format(sbn:ElementoAutLegato/sbn:DatiElementoAut/sbn:T200,'a|b|d|f')"/><xsl:if test="@relatorCode ne '' and @relatorCode ne '570'"> [<xsl:value-of select="fn:lower-case(magextension:getRuolo(fn:replace(@relatorCode,'^0','')))"/>]</xsl:if></dc:contributor>
                            </xsl:when>
                            <xsl:otherwise>
                                <dc:contributor><xsl:value-of select="magextension:format(sbn:ElementoAutLegato/sbn:DatiElementoAut/sbn:T200,'a|b|c|d|f')"/><xsl:if test="@relatorCode ne '' and @relatorCode ne '570'"> [<xsl:value-of select="fn:lower-case(magextension:getRuolo(fn:replace(@relatorCode,'^0','')))"/>]</xsl:if></dc:contributor>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:if>
                    <!-- enti -->
                    <xsl:if test="sbn:ElementoAutLegato/sbn:DatiElementoAut/sbn:T210">
                        <xsl:choose><xsl:when test="fn:contains($temp210,'&lt;omonini non identificati&gt;')
                                or fn:contains($temp210,'&lt;autore indifferenziato&gt;')">
                            <dc:contributor><xsl:value-of select="magextension:format(sbn:ElementoAutLegato/sbn:DatiElementoAut/sbn:T210,'a|b|d|f')"/><xsl:if test="@relatorCode ne '' and @relatorCode ne '570'"> [<xsl:value-of select="fn:lower-case(magextension:getRuolo(fn:replace(@relatorCode,'^0','')))"/>]</xsl:if></dc:contributor>
                        </xsl:when>
                            <xsl:otherwise>
                                <dc:contributor><xsl:value-of select="magextension:format(sbn:ElementoAutLegato/sbn:DatiElementoAut/sbn:T210,'a|b|c|d|f')"/><xsl:if test="@relatorCode ne '' and @relatorCode ne '570'"> [<xsl:value-of select="fn:lower-case(magextension:getRuolo(fn:replace(@relatorCode,'^0','')))"/>]</xsl:if></dc:contributor>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:if>
                </xsl:if>
            </xsl:for-each>

            <xsl:if test="$isLibretto eq 'true' or $isMusicaManoscritta eq 'true'">
                <xsl:for-each select="sbn:T922">
                    <dc:coverage>
                        <xsl:variable name="tempVar"><xsl:if test="sbn:s_922 and sbn:s_922 ne 'non rilevato' and sbn:s_922 ne 'abs'"><xsl:value-of select="sbn:s_922 "/>, </xsl:if>
                        <xsl:if test="sbn:r_922 and sbn:r_922 ne 'non rilevato' and sbn:r_922 ne 'abs'"><xsl:value-of select="sbn:r_922 "/>, </xsl:if>
                        <xsl:if test="sbn:q_922 and sbn:q_922 ne 'non rilevato' and sbn:q_922 ne 'abs'"><xsl:value-of select="sbn:q_922 "/><xsl:text> </xsl:text></xsl:if>
                        <xsl:if test="sbn:p_922 and sbn:p_922 ne 'non rilevato' and sbn:p_922 ne 'abs'"><xsl:value-of select="sbn:p_922 "/>, </xsl:if>
                        <xsl:if test="sbn:u_922 and sbn:u_922 ne 'non rilevato' and sbn:u_922 ne 'abs'"><xsl:value-of select="sbn:u_922 "/>, </xsl:if>
                        </xsl:variable>
                        <xsl:choose>
                            <xsl:when test="fn:ends-with($tempVar,', ')"><xsl:value-of select="fn:substring($tempVar,1,fn:string-length($tempVar)-2)"/></xsl:when>
                            <xsl:otherwise><xsl:value-of select="fn:substring($tempVar,1,fn:string-length($tempVar)-1)"/></xsl:otherwise>
                        </xsl:choose>

                    </dc:coverage>
                </xsl:for-each>
            </xsl:if>

            <xsl:if test="$isLibretto eq 'true'">
                <xsl:for-each select="//sbn:LegameElementoAut[@tipoLegame='500']">
                    <xsl:value-of select="magextension:getCounter('0')"/>
                    <xsl:variable name="pos"><xsl:value-of select="position()"/></xsl:variable>
                <dc:relation>'titolo uniforme:' <xsl:for-each select="current()|//sbn:T929[position()=$pos]|//sbn:T928[position()=$pos]">
                    <xsl:sort select="magextension:getOrderTitoloUniforme(.)" />
                    <xsl:variable name="value">
                    <xsl:choose>
                    <xsl:when test="name() eq 'T929'"><xsl:value-of select="magextension:format(.,'c|d|f|i')"/></xsl:when>
                    <xsl:when test="name() eq 'T928'"><xsl:value-of select="magextension:getFomu(sbn:a_928)"/></xsl:when>
                    <xsl:otherwise><xsl:value-of select="magextension:format(.,'a')"/></xsl:otherwise>
                </xsl:choose></xsl:variable>
                    <xsl:if test="fn:string-length(normalize-space($value)) gt 0"><xsl:value-of select="magextension:getCounter('1')"/></xsl:if>
                    <xsl:if test="fn:number(magextension:getCounter('-1')) gt 1 and fn:string-length(normalize-space($value)) gt 0"><xsl:text> ; </xsl:text></xsl:if><xsl:value-of
                            select="$value"/></xsl:for-each>
                </dc:relation></xsl:for-each>
            </xsl:if>

            <xsl:if test="$isMusica eq 'true' or $isVideo eq 'true'">
                <xsl:for-each select="//sbn:LegameElementoAut[@tipoLegame='500']">
                <xsl:value-of select="magextension:getCounter('0')"/>
                 <xsl:variable name="pos"><xsl:value-of select="position()"/></xsl:variable>
                <dc:relation>'titolo uniforme:' <xsl:for-each select="current()|//sbn:T928[position()=$pos]|//sbn:T929[position()=$pos]">
                    <xsl:sort select="magextension:getOrderTitoloUniforme(.)" />
                    <xsl:variable name="value">
                        <xsl:choose>
                            <xsl:when test="name() eq 'LegameElementoAut'"><xsl:value-of select="magextension:format(sbn:ElementoAutLegato/sbn:DatiElementoAut/sbn:T230,'a|e')"/></xsl:when>
                            <xsl:when test="name() eq 'T929'"><xsl:value-of select="magextension:formatPunto(.,'b-a|f-e[tono]-c|i-d')"/>
                            </xsl:when>
                            <xsl:when test="name() eq 'T928'"><xsl:value-of select="magextension:formatPunto(.,'a[fomu]-c')"/></xsl:when>
                            <xsl:otherwise><xsl:value-of select="magextension:format(.,'a')"/></xsl:otherwise>
                        </xsl:choose></xsl:variable>
                    <xsl:if test="fn:string-length(normalize-space($value)) gt 0"><xsl:value-of select="magextension:getCounter('1')"/></xsl:if>
                    <xsl:if test="fn:number(magextension:getCounter('-1')) gt 1 and fn:string-length(normalize-space($value)) gt 0"><xsl:text>. </xsl:text></xsl:if><xsl:value-of
                        select="$value"/>
                </xsl:for-each></dc:relation>
                </xsl:for-each>
            </xsl:if>

            <xsl:for-each select="sbn:T950">
                <xsl:for-each select="./sbn:e_950_3">
                    <mag:holdings>
                        <mag:library><xsl:value-of select="magextension:format(parent::node(),'a')"/></mag:library>
                        <mag:inventory_number><xsl:if test="fn:string-length(normalize-space(.)) gt 0"><xsl:value-of select="."/>_</xsl:if><xsl:value-of select="fn:replace((following-sibling::sbn:e_950_6)[1],'^[0]+','')"/></mag:inventory_number>
                        <mag:selfmark><xsl:value-of select="(preceding-sibling::sbn:d_950_3)[last()]"/><xsl:text> </xsl:text><xsl:value-of select="(preceding-sibling::sbn:d_950_13)[last()]"/><xsl:text> </xsl:text><xsl:value-of select="(preceding-sibling::sbn:d_950_37)[last()]"/><xsl:text> </xsl:text><xsl:value-of select="(following-sibling::e_950_24)[1]"/></mag:selfmark>
                    </mag:holdings>
                </xsl:for-each>
            </xsl:for-each>

            <xsl:for-each select="sbn:T950">
                <xsl:for-each select="./sbn:d_950/sbn:e_950">
                    <mag:holdings>
                        <mag:library><xsl:value-of select="../../sbn:a_950"/></mag:library>
                        <mag:inventory_number><xsl:if test="fn:string-length(normalize-space(sbn:e_950_3)) gt 0"><xsl:value-of select="sbn:e_950_3"/>_</xsl:if><xsl:value-of select="fn:replace(sbn:e_950_6,'^[0]+','')"/></mag:inventory_number>
                        <mag:selfmark><xsl:value-of select="(preceding-sibling::sbn:d_950_3)[last()]"/>
                            <xsl:for-each select="(preceding-sibling::sbn:d_950_13)[last()]">
                                <xsl:text> </xsl:text><xsl:value-of select="."/>
                            </xsl:for-each>
                            <xsl:for-each select="(preceding-sibling::sbn:d_950_37)[last()]">
                                <xsl:text> </xsl:text><xsl:value-of select="."/>
                            </xsl:for-each>
                            <xsl:for-each select="(sbn:e_950_23)[1]">
                                <xsl:text> </xsl:text><xsl:value-of select="."/>
                            </xsl:for-each>
                        </mag:selfmark>
                    </mag:holdings>
                </xsl:for-each>
            </xsl:for-each>

        </mag:bib>
    </xsl:template>

    <xsl:template match="text()">
    </xsl:template>
</xsl:stylesheet>