package com.gruppometa.poloigitale.services.components;

import com.gruppometa.metasearch.data.Field;
import com.gruppometa.metasearch.data.FieldGroup;
import com.gruppometa.metasearch.data.View;
import com.gruppometa.metasearch.data.ViewItem;
import com.gruppometa.unimarc.profile.NaXmlProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "reformattor")
public class Reformattor {

    public boolean isWriteInventoryHeader() {
        return writeInventoryHeader;
    }

    public void setWriteInventoryHeader(boolean writeInventoryHeader) {
        this.writeInventoryHeader = writeInventoryHeader;
    }

    protected boolean writeInventoryHeader = false;

    protected String htmlEntities = "<!ENTITY nbsp   CDATA \"&#160;\" -- no-break space = non-breaking space,\n" +
            "                                  U+00A0 ISOnum -->\n" +
            "<!ENTITY iexcl  CDATA \"&#161;\" -- inverted exclamation mark, U+00A1 ISOnum -->\n" +
            "<!ENTITY cent   CDATA \"&#162;\" -- cent sign, U+00A2 ISOnum -->\n" +
            "<!ENTITY pound  CDATA \"&#163;\" -- pound sign, U+00A3 ISOnum -->\n" +
            "<!ENTITY curren CDATA \"&#164;\" -- currency sign, U+00A4 ISOnum -->\n" +
            "<!ENTITY yen    CDATA \"&#165;\" -- yen sign = yuan sign, U+00A5 ISOnum -->\n" +
            "<!ENTITY brvbar CDATA \"&#166;\" -- broken bar = broken vertical bar,\n" +
            "                                  U+00A6 ISOnum -->\n" +
            "<!ENTITY sect   CDATA \"&#167;\" -- section sign, U+00A7 ISOnum -->\n" +
            "<!ENTITY uml    CDATA \"&#168;\" -- diaeresis = spacing diaeresis,\n" +
            "                                  U+00A8 ISOdia -->\n" +
            "<!ENTITY copy   CDATA \"&#169;\" -- copyright sign, U+00A9 ISOnum -->\n" +
            "<!ENTITY ordf   CDATA \"&#170;\" -- feminine ordinal indicator, U+00AA ISOnum -->\n" +
            "<!ENTITY laquo  CDATA \"&#171;\" -- left-pointing double angle quotation mark\n" +
            "                                  = left pointing guillemet, U+00AB ISOnum -->\n" +
            "<!ENTITY not    CDATA \"&#172;\" -- not sign, U+00AC ISOnum -->\n" +
            "<!ENTITY shy    CDATA \"&#173;\" -- soft hyphen = discretionary hyphen,\n" +
            "                                  U+00AD ISOnum -->\n" +
            "<!ENTITY reg    CDATA \"&#174;\" -- registered sign = registered trade mark sign,\n" +
            "                                  U+00AE ISOnum -->\n" +
            "<!ENTITY macr   CDATA \"&#175;\" -- macron = spacing macron = overline\n" +
            "                                  = APL overbar, U+00AF ISOdia -->\n" +
            "<!ENTITY deg    CDATA \"&#176;\" -- degree sign, U+00B0 ISOnum -->\n" +
            "<!ENTITY plusmn CDATA \"&#177;\" -- plus-minus sign = plus-or-minus sign,\n" +
            "                                  U+00B1 ISOnum -->\n" +
            "<!ENTITY sup2   CDATA \"&#178;\" -- superscript two = superscript digit two\n" +
            "                                  = squared, U+00B2 ISOnum -->\n" +
            "<!ENTITY sup3   CDATA \"&#179;\" -- superscript three = superscript digit three\n" +
            "                                  = cubed, U+00B3 ISOnum -->\n" +
            "<!ENTITY acute  CDATA \"&#180;\" -- acute accent = spacing acute,\n" +
            "                                  U+00B4 ISOdia -->\n" +
            "<!ENTITY micro  CDATA \"&#181;\" -- micro sign, U+00B5 ISOnum -->\n" +
            "<!ENTITY para   CDATA \"&#182;\" -- pilcrow sign = paragraph sign,\n" +
            "                                  U+00B6 ISOnum -->\n" +
            "<!ENTITY middot CDATA \"&#183;\" -- middle dot = Georgian comma\n" +
            "                                  = Greek middle dot, U+00B7 ISOnum -->\n" +
            "<!ENTITY cedil  CDATA \"&#184;\" -- cedilla = spacing cedilla, U+00B8 ISOdia -->\n" +
            "<!ENTITY sup1   CDATA \"&#185;\" -- superscript one = superscript digit one,\n" +
            "                                  U+00B9 ISOnum -->\n" +
            "<!ENTITY ordm   CDATA \"&#186;\" -- masculine ordinal indicator,\n" +
            "                                  U+00BA ISOnum -->\n" +
            "<!ENTITY raquo  CDATA \"&#187;\" -- right-pointing double angle quotation mark\n" +
            "                                  = right pointing guillemet, U+00BB ISOnum -->\n" +
            "<!ENTITY frac14 CDATA \"&#188;\" -- vulgar fraction one quarter\n" +
            "                                  = fraction one quarter, U+00BC ISOnum -->\n" +
            "<!ENTITY frac12 CDATA \"&#189;\" -- vulgar fraction one half\n" +
            "                                  = fraction one half, U+00BD ISOnum -->\n" +
            "<!ENTITY frac34 CDATA \"&#190;\" -- vulgar fraction three quarters\n" +
            "                                  = fraction three quarters, U+00BE ISOnum -->\n" +
            "<!ENTITY iquest CDATA \"&#191;\" -- inverted question mark\n" +
            "                                  = turned question mark, U+00BF ISOnum -->\n" +
            "<!ENTITY Agrave CDATA \"&#192;\" -- latin capital letter A with grave\n" +
            "                                  = latin capital letter A grave,\n" +
            "                                  U+00C0 ISOlat1 -->\n" +
            "<!ENTITY Aacute CDATA \"&#193;\" -- latin capital letter A with acute,\n" +
            "                                  U+00C1 ISOlat1 -->\n" +
            "<!ENTITY Acirc  CDATA \"&#194;\" -- latin capital letter A with circumflex,\n" +
            "                                  U+00C2 ISOlat1 -->\n" +
            "<!ENTITY Atilde CDATA \"&#195;\" -- latin capital letter A with tilde,\n" +
            "                                  U+00C3 ISOlat1 -->\n" +
            "<!ENTITY Auml   CDATA \"&#196;\" -- latin capital letter A with diaeresis,\n" +
            "                                  U+00C4 ISOlat1 -->\n" +
            "<!ENTITY Aring  CDATA \"&#197;\" -- latin capital letter A with ring above\n" +
            "                                  = latin capital letter A ring,\n" +
            "                                  U+00C5 ISOlat1 -->\n" +
            "<!ENTITY AElig  CDATA \"&#198;\" -- latin capital letter AE\n" +
            "                                  = latin capital ligature AE,\n" +
            "                                  U+00C6 ISOlat1 -->\n" +
            "<!ENTITY Ccedil CDATA \"&#199;\" -- latin capital letter C with cedilla,\n" +
            "                                  U+00C7 ISOlat1 -->\n" +
            "<!ENTITY Egrave CDATA \"&#200;\" -- latin capital letter E with grave,\n" +
            "                                  U+00C8 ISOlat1 -->\n" +
            "<!ENTITY Eacute CDATA \"&#201;\" -- latin capital letter E with acute,\n" +
            "                                  U+00C9 ISOlat1 -->\n" +
            "<!ENTITY Ecirc  CDATA \"&#202;\" -- latin capital letter E with circumflex,\n" +
            "                                  U+00CA ISOlat1 -->\n" +
            "<!ENTITY Euml   CDATA \"&#203;\" -- latin capital letter E with diaeresis,\n" +
            "                                  U+00CB ISOlat1 -->\n" +
            "<!ENTITY Igrave CDATA \"&#204;\" -- latin capital letter I with grave,\n" +
            "                                  U+00CC ISOlat1 -->\n" +
            "<!ENTITY Iacute CDATA \"&#205;\" -- latin capital letter I with acute,\n" +
            "                                  U+00CD ISOlat1 -->\n" +
            "<!ENTITY Icirc  CDATA \"&#206;\" -- latin capital letter I with circumflex,\n" +
            "                                  U+00CE ISOlat1 -->\n" +
            "<!ENTITY Iuml   CDATA \"&#207;\" -- latin capital letter I with diaeresis,\n" +
            "                                  U+00CF ISOlat1 -->\n" +
            "<!ENTITY ETH    CDATA \"&#208;\" -- latin capital letter ETH, U+00D0 ISOlat1 -->\n" +
            "<!ENTITY Ntilde CDATA \"&#209;\" -- latin capital letter N with tilde,\n" +
            "                                  U+00D1 ISOlat1 -->\n" +
            "<!ENTITY Ograve CDATA \"&#210;\" -- latin capital letter O with grave,\n" +
            "                                  U+00D2 ISOlat1 -->\n" +
            "<!ENTITY Oacute CDATA \"&#211;\" -- latin capital letter O with acute,\n" +
            "                                  U+00D3 ISOlat1 -->\n" +
            "<!ENTITY Ocirc  CDATA \"&#212;\" -- latin capital letter O with circumflex,\n" +
            "                                  U+00D4 ISOlat1 -->\n" +
            "<!ENTITY Otilde CDATA \"&#213;\" -- latin capital letter O with tilde,\n" +
            "                                  U+00D5 ISOlat1 -->\n" +
            "<!ENTITY Ouml   CDATA \"&#214;\" -- latin capital letter O with diaeresis,\n" +
            "                                  U+00D6 ISOlat1 -->\n" +
            "<!ENTITY times  CDATA \"&#215;\" -- multiplication sign, U+00D7 ISOnum -->\n" +
            "<!ENTITY Oslash CDATA \"&#216;\" -- latin capital letter O with stroke\n" +
            "                                  = latin capital letter O slash,\n" +
            "                                  U+00D8 ISOlat1 -->\n" +
            "<!ENTITY Ugrave CDATA \"&#217;\" -- latin capital letter U with grave,\n" +
            "                                  U+00D9 ISOlat1 -->\n" +
            "<!ENTITY Uacute CDATA \"&#218;\" -- latin capital letter U with acute,\n" +
            "                                  U+00DA ISOlat1 -->\n" +
            "<!ENTITY Ucirc  CDATA \"&#219;\" -- latin capital letter U with circumflex,\n" +
            "                                  U+00DB ISOlat1 -->\n" +
            "<!ENTITY Uuml   CDATA \"&#220;\" -- latin capital letter U with diaeresis,\n" +
            "                                  U+00DC ISOlat1 -->\n" +
            "<!ENTITY Yacute CDATA \"&#221;\" -- latin capital letter Y with acute,\n" +
            "                                  U+00DD ISOlat1 -->\n" +
            "<!ENTITY THORN  CDATA \"&#222;\" -- latin capital letter THORN,\n" +
            "                                  U+00DE ISOlat1 -->\n" +
            "<!ENTITY szlig  CDATA \"&#223;\" -- latin small letter sharp s = ess-zed,\n" +
            "                                  U+00DF ISOlat1 -->\n" +
            "<!ENTITY agrave CDATA \"&#224;\" -- latin small letter a with grave\n" +
            "                                  = latin small letter a grave,\n" +
            "                                  U+00E0 ISOlat1 -->\n" +
            "<!ENTITY aacute CDATA \"&#225;\" -- latin small letter a with acute,\n" +
            "                                  U+00E1 ISOlat1 -->\n" +
            "<!ENTITY acirc  CDATA \"&#226;\" -- latin small letter a with circumflex,\n" +
            "                                  U+00E2 ISOlat1 -->\n" +
            "<!ENTITY atilde CDATA \"&#227;\" -- latin small letter a with tilde,\n" +
            "                                  U+00E3 ISOlat1 -->\n" +
            "<!ENTITY auml   CDATA \"&#228;\" -- latin small letter a with diaeresis,\n" +
            "                                  U+00E4 ISOlat1 -->\n" +
            "<!ENTITY aring  CDATA \"&#229;\" -- latin small letter a with ring above\n" +
            "                                  = latin small letter a ring,\n" +
            "                                  U+00E5 ISOlat1 -->\n" +
            "<!ENTITY aelig  CDATA \"&#230;\" -- latin small letter ae\n" +
            "                                  = latin small ligature ae, U+00E6 ISOlat1 -->\n" +
            "<!ENTITY ccedil CDATA \"&#231;\" -- latin small letter c with cedilla,\n" +
            "                                  U+00E7 ISOlat1 -->\n" +
            "<!ENTITY egrave CDATA \"&#232;\" -- latin small letter e with grave,\n" +
            "                                  U+00E8 ISOlat1 -->\n" +
            "<!ENTITY eacute CDATA \"&#233;\" -- latin small letter e with acute,\n" +
            "                                  U+00E9 ISOlat1 -->\n" +
            "<!ENTITY ecirc  CDATA \"&#234;\" -- latin small letter e with circumflex,\n" +
            "                                  U+00EA ISOlat1 -->\n" +
            "<!ENTITY euml   CDATA \"&#235;\" -- latin small letter e with diaeresis,\n" +
            "                                  U+00EB ISOlat1 -->\n" +
            "<!ENTITY igrave CDATA \"&#236;\" -- latin small letter i with grave,\n" +
            "                                  U+00EC ISOlat1 -->\n" +
            "<!ENTITY iacute CDATA \"&#237;\" -- latin small letter i with acute,\n" +
            "                                  U+00ED ISOlat1 -->\n" +
            "<!ENTITY icirc  CDATA \"&#238;\" -- latin small letter i with circumflex,\n" +
            "                                  U+00EE ISOlat1 -->\n" +
            "<!ENTITY iuml   CDATA \"&#239;\" -- latin small letter i with diaeresis,\n" +
            "                                  U+00EF ISOlat1 -->\n" +
            "<!ENTITY eth    CDATA \"&#240;\" -- latin small letter eth, U+00F0 ISOlat1 -->\n" +
            "<!ENTITY ntilde CDATA \"&#241;\" -- latin small letter n with tilde,\n" +
            "                                  U+00F1 ISOlat1 -->\n" +
            "<!ENTITY ograve CDATA \"&#242;\" -- latin small letter o with grave,\n" +
            "                                  U+00F2 ISOlat1 -->\n" +
            "<!ENTITY oacute CDATA \"&#243;\" -- latin small letter o with acute,\n" +
            "                                  U+00F3 ISOlat1 -->\n" +
            "<!ENTITY ocirc  CDATA \"&#244;\" -- latin small letter o with circumflex,\n" +
            "                                  U+00F4 ISOlat1 -->\n" +
            "<!ENTITY otilde CDATA \"&#245;\" -- latin small letter o with tilde,\n" +
            "                                  U+00F5 ISOlat1 -->\n" +
            "<!ENTITY ouml   CDATA \"&#246;\" -- latin small letter o with diaeresis,\n" +
            "                                  U+00F6 ISOlat1 -->\n" +
            "<!ENTITY divide CDATA \"&#247;\" -- division sign, U+00F7 ISOnum -->\n" +
            "<!ENTITY oslash CDATA \"&#248;\" -- latin small letter o with stroke,\n" +
            "                                  = latin small letter o slash,\n" +
            "                                  U+00F8 ISOlat1 -->\n" +
            "<!ENTITY ugrave CDATA \"&#249;\" -- latin small letter u with grave,\n" +
            "                                  U+00F9 ISOlat1 -->\n" +
            "<!ENTITY uacute CDATA \"&#250;\" -- latin small letter u with acute,\n" +
            "                                  U+00FA ISOlat1 -->\n" +
            "<!ENTITY ucirc  CDATA \"&#251;\" -- latin small letter u with circumflex,\n" +
            "                                  U+00FB ISOlat1 -->\n" +
            "<!ENTITY uuml   CDATA \"&#252;\" -- latin small letter u with diaeresis,\n" +
            "                                  U+00FC ISOlat1 -->\n" +
            "<!ENTITY yacute CDATA \"&#253;\" -- latin small letter y with acute,\n" +
            "                                  U+00FD ISOlat1 -->\n" +
            "<!ENTITY thorn  CDATA \"&#254;\" -- latin small letter thorn,\n" +
            "                                  U+00FE ISOlat1 -->\n" +
            "<!ENTITY yuml   CDATA \"&#255;\" -- latin small letter y with diaeresis,\n" +
            "                                  U+00FF ISOlat1 -->";
    protected DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    protected static final Logger  LOGGER = LoggerFactory.getLogger(Reformattor.class);

    public String reformat(String xmlString, int pos, com.gruppometa.metasearch.data.Document documentMeta, List<String> biblioteca){
        StringWriter stringWriter = new StringWriter();
        try {
            if(!xmlString.startsWith("<?xml"))
                xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE htmlCustom[\n" +
                        " <!ENTITY nbsp \"&#160;\">\n" +
                        " <!ENTITY raquo \"&#187;\">\n" +
                        " <!ENTITY laquo \"&#171;\">\n" +
                        " <!ENTITY egrave \"&#232;\">\n" +
                        " <!ENTITY agrave \"&#224;\">\n" +
                        "]>\n<root>"+
                        xmlString.replace("\\n","\n").trim()+"</root>";
            InputSource inputSource = new InputSource(new StringReader(xmlString));
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputSource);
            NodeList list = document.getDocumentElement().getChildNodes();
            String lastConsistenza = null;
            int id = 0;
            boolean doWrite = true;
            boolean divOpen = false;
            final String senzaIndicazione = "Senza indicazione";
            for(int i = 0 ;i < list.getLength();i++){
                Node node = list.item(i);
                if(node.getAttributes()!=null && node.getAttributes().getNamedItem("class")!=null){
                    if(doWrite) {
                        if (isType(documentMeta, "Livello bibliografico", "periodico")) {
                            String consistenzaEsemplare = getConsistenza(node, "Consistenza esemplare");
                            if (consistenzaEsemplare == null)
                                consistenzaEsemplare = senzaIndicazione;
                            String consistenzaCollocazione = getConsistenza(node, "Consistenza collocazione");
                            if (consistenzaCollocazione == null)
                                consistenzaCollocazione = senzaIndicazione;
                            String consistenza = consistenzaCollocazione + "::" + consistenzaEsemplare;
                            if (consistenza != null && consistenza.trim().length() == 0) {
                                consistenza = senzaIndicazione;
                            }
                            //stringWriter.append(consistenza);
                            if (consistenza != null && !consistenza.equals(lastConsistenza)) {
                                if (divOpen) {
                                    stringWriter.append("\n</div>");
                                    divOpen = false;
                                }
                                id++;
                                if (!consistenzaEsemplare.equals(senzaIndicazione) &&
                                        (consistenzaCollocazione == null || !consistenzaCollocazione.equals(consistenzaEsemplare)))
                                    stringWriter.append("\n<div class=\"group\" title=\"950.FE\">\n" +
                                            "<div>\n" +
                                            "    <div class=\"label\">Consistenza esemplare</div>\n" +
                                            "    <div class=\"value js-open-copies\" data-group=\"" + pos + "-" + id + "\">" + HtmlUtils.htmlEscape(consistenzaEsemplare) + "</div>\n" +
                                            "</div>\n");
                                if (consistenzaCollocazione != null)
                                    stringWriter.append(
                                            "<div>\n" +
                                                    "    <div class=\"label\">Consistenza collocazione</div>\n" +
                                                    "    <div class=\"value js-open-copies\" data-group=\"" + pos + "-" + id + "\">" + HtmlUtils.htmlEscape(consistenzaCollocazione) + "</div>\n" +
                                                    "</div>\n"
                                    );
                                stringWriter.append("\n<div class=\"group hidden\" data-group=\"" + pos + "-" + id + "\" title=\"950.FE\">\n");
                                divOpen = true;
                                lastConsistenza = consistenza;
                            }
                            stringWriter.append("\n" + asXmlWithoutConsistenza(node, id, true));
                        }
                        /**
                         * monografia
                         */
                        else {
                            if (divOpen) {
                                stringWriter.append("\n</div>");

                            }
                            stringWriter.append("\n<div class='group' title='950.FE'>");
                            divOpen = true;
                            stringWriter.append("\n" + asXmlWithoutConsistenza(node, id, isType(documentMeta, "Livello bibliografico", "monografia")));
                        }
                    }
                }
                /**
                 * la biblioteca
                 */
                else if(node.getNodeType()!=Node.TEXT_NODE){
                    if(divOpen) {
                        stringWriter.append("\n</div>");
                        divOpen = false;
                    }
                    id = 0;
                    String bibliotecaOfNode = getBiblioteca(node);
                    doWrite = filterBiblioteca(biblioteca, bibliotecaOfNode);
                    if(doWrite) {
                        stringWriter.append(asXml(node, 1));
                        if (writeInventoryHeader)
                            stringWriter.append("\n<div class='parent-archive'>Collocazioni e inventari</div>\n");
                    }
                }
            }
            if(divOpen) {
                stringWriter.append("\n</div>");
            }
        }
        catch(Exception e){
            LOGGER.error("",e);
            return null;
        }
        return stringWriter.toString().replaceAll("\n+","\n");
    }

    private boolean filterBiblioteca(List<String> biblioteca, String bibliotecaOfNode) {
        if(biblioteca!=null){
            if(biblioteca.contains(bibliotecaOfNode))
                return true;
            if(biblioteca.contains(NaXmlProfile.getLocalizzazioneFromLongName(bibliotecaOfNode)))
                return true;
            return false;
        }
        return true;
    }

    private boolean isType(com.gruppometa.metasearch.data.Document documentMeta, String fieldname, String name) {
        if(documentMeta==null)
            return false;
        for(ViewItem viewItem: documentMeta.getNodes()){
            if(isType(viewItem,fieldname,name))
                return true;
        }
        return false;
    }

    private boolean isType(ViewItem viewItem, String fieldname, String value) {
        if(viewItem.getType().equals("field") && viewItem.getId().equalsIgnoreCase(fieldname)
                && viewItem instanceof  Field && ((Field)viewItem).getValues()!=null
                && ((Field)viewItem).getValues().contains(value)){
            return true;
        }
        else if (viewItem instanceof FieldGroup ){
            for(ViewItem item: ((FieldGroup)viewItem).getNodes()){
                if(isType(item,fieldname,value))
                    return true;
            }
        }
        return false;
    }

    private String asXmlWithoutConsistenza(Node node, int id, boolean cut) {
        StringWriter stringWriter = new StringWriter();
        for(int i = 0; i < node.getChildNodes().getLength(); i++){
            if(!cut || !isConsistenza(node.getChildNodes().item(i))) {
                Node child = node.getChildNodes().item(i);
                if(i==node.getChildNodes().getLength()-1)
                    ((Element)child).setAttribute("class","group-last");
                stringWriter.append("\n" + asXml(child, 1));
            }
        }
        return stringWriter.toString().trim();
    }

    private String getBiblioteca(Node item){
        XPath xPath = XPathFactory.newInstance().newXPath();
        String expression = "div[@class='label' and text()='Biblioteca']/following-sibling::div[1]";
        try {
            Node node = (Node)xPath.compile(expression).evaluate(item, XPathConstants.NODE);
            if(node!=null)
                return node.getTextContent();
        } catch (XPathExpressionException e) {
            LOGGER.error("",e);
        }
        return null;
    }

    private boolean isConsistenza(Node item) {
        XPath xPath = XPathFactory.newInstance().newXPath();
        String expression = "div[@class='label']";
        try {
            Node node = (Node)xPath.compile(expression).evaluate(item, XPathConstants.NODE);
            if(node!=null)
                return node.getTextContent().equals("Consistenza esemplare")|| node.getTextContent().equals("Consistenza collocazione");
        } catch (XPathExpressionException e) {
            LOGGER.error("",e);
        }
        return false;
    }

    private String getConsistenza(Node node,String name) {
        for (int i = 0; i< node.getChildNodes().getLength(); i++) {
            if(node.getChildNodes().item(i).getNodeType()!=Node.TEXT_NODE) {
                Node div = node.getChildNodes().item(i);
                for (int j = 0; j< div.getChildNodes().getLength(); j++) {
                    if(div.getChildNodes().item(j).getNodeType()!=Node.TEXT_NODE) {
                        if (div.getChildNodes().item(j).getTextContent().equals(name)) {
                            for (int k = j+1; k< div.getChildNodes().getLength(); k++) {
                                if(div.getChildNodes().item(k).getNodeType()!=Node.TEXT_NODE) {
                                    return div.getChildNodes().item(k).getTextContent();
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public String asXml(Node node, int tabs){
        try

        {
            TransformerFactory transfac = TransformerFactory.newInstance();
            Transformer trans = transfac.newTransformer();
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            trans.setOutputProperty(OutputKeys.INDENT, "yes");
            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);
            DOMSource source = new DOMSource(node);
            trans.transform(source, result);
            String xmlString = sw.toString();
            return xmlString.trim();
        }
        catch (Exception e)
        {
            LOGGER.error("",e);
        }
        return null;
    }
}
