package com.gruppometa.sbnmarc.mag;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;

@SuppressWarnings("unused")
public class SbnMarcClient {
	protected static final Logger logger = LoggerFactory.getLogger(SbnMarcClient.class);
	public static final String TYPE_AU="AU";
	public static final String TYPE_ITEM="ITEM";
	protected String url = "http://sbnweb.bnnonline.it/SbnMarcWeb/SbnMarcTest";
	protected String username = "pmdigi";
	protected String testBib = null;
	public String getTestBib() {
		return testBib;
	}

	public void setTestBib(String testBib) {
		this.testBib = testBib;
	}

	protected String usernamePosseduti;
	protected String passwordPosseduti;
	protected String urlPosseduti;
	
	
	public String getUsernamePosseduti() {
		return usernamePosseduti;
	}

	public void setUsernamePosseduti(String usernamePosseduti) {
		this.usernamePosseduti = usernamePosseduti;
	}

	public String getPasswordPosseduti() {
		return passwordPosseduti;
	}

	public void setPasswordPosseduti(String passwordPosseduti) {
		this.passwordPosseduti = passwordPosseduti;
	}

	public String getUrlPosseduti() {
		return urlPosseduti;
	}

	public void setUrlPosseduti(String urlPosseduti) {
		this.urlPosseduti = urlPosseduti;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getSbnMarc2Mag(MagTransformer magTransformer, String bid) throws MagException {
		try {
			Document document = getResponse(bid);
			return magTransformer.transform(document);
		}
		catch(Exception e ){
			throw  new MagException(e.getMessage());
		}
	}
	public Document getResponse(String bid) throws MagException{
		return getResponse(bid, TYPE_ITEM);
	}
	public Document getResponse(String bid, String type) throws MagException{
		// NAP0667255
		String str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<SBNMarc schemaVersion=\"2.00\">"
				+ "<SbnUser><Biblioteca>NAP PM</Biblioteca><UserId>"+username+"</UserId></SbnUser>"
				+ "<SbnMessage><SbnRequest><Cerca numPrimo=\"1\" tipoOrd=\"1\" tipoOutput=\"000\" limit=\"4000\">"+
				(type.equals(TYPE_AU)?
				 ("<CercaElementoAut><CercaDatiAut><tipoAuthority>AU</tipoAuthority>"
				 + "<canaliCercaDatiAutType><T001>"+
					StringEscapeUtils.escapeXml10(bid)
				 + "</T001></canaliCercaDatiAutType>"
				 + "</CercaDatiAut></CercaElementoAut>"):
				( "<CercaTitolo><CercaDatiTit><T001>"+
						StringEscapeUtils.escapeXml10(bid)+
						"</T001>"
						+ "</CercaDatiTit></CercaTitolo>"))+
				"</Cerca>"
				+ "</SbnRequest></SbnMessage></SBNMarc>";
		Document doc = null;
		RequestConfig requestConfig = RequestConfig.custom()
					.setConnectTimeout(5000)
					.setSocketTimeout(5000)
					.build();
		CloseableHttpClient httpclient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
		
		HttpPost httpPost = new HttpPost(url);
		CloseableHttpResponse response1 = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        MagException ex = null;
		try{
			httpPost.setEntity(new StringEntity(str)); 
			response1 = httpclient.execute(httpPost);
			//System.out.println(response1.getStatusLine());
		    HttpEntity entity2 = response1.getEntity();
		    // do something useful with the response body
		    DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(entity2.getContent());
            // and ensure it is fully consumed
		    EntityUtils.consume(entity2);
		    //if(type.equals(TYPE_ITEM) && checkResult(bib, bid, doc))
		    //	addPosseduti(bib, bid, doc);
		}
		catch(Exception e){
			logger.error("",e);
			ex =  new MagException(e.getMessage());
		}
		finally {
			try {
				if(response1!=null)
					response1.close();
			} catch (IOException e) {
				logger.error("",e);
				ex =  new MagException(e.getMessage());
			}
		}
		if(ex!=null)
			throw ex;
		/**
		 * add namespace !! Senza non funziona poi la trasformazione
		 */
		if(doc!=null)
			doc.getDocumentElement().setAttribute("xmlns", "http://www.iccu.sbn.it/opencms/opencms/documenti/2016/SBNMarcv202.xsd");
		return doc;
	}
	
	protected boolean checkResult(String bib, String bid,Document doc){
		XPath xPath = XPathFactory.newInstance().newXPath();
		try {
			String xpathStr = "//SbnResult//esito";
			Node node = (Node)xPath.compile(xpathStr).evaluate(doc, XPathConstants.NODE);
			if(node==null){
				logger.warn("Not found node '"+xpathStr+"'.");
				return false;
			}
			if(node.getTextContent().equals("0000")){
				return true;
			}
			else{
				logger.warn("Doc not found: bid="+bid+ " bib="+bib);
				return false;
			}
		} catch (Exception e) {
			logger.error("",e);
		}
		return false;
	}
	
	protected String checkType(String type, Document doc){
		if(type!=null && type.equals("AUT"))
			return "AUT";
		XPath xPath = XPathFactory.newInstance().newXPath();
		try {
			String xpathStr = "//Documento/DatiDocumento/T116/a_116_0";
			Node node = (Node)xPath.compile(xpathStr).evaluate(doc, XPathConstants.NODE);
			if(node==null){
				logger.warn("Not found node '"+xpathStr+"'.");
				return null;
			}
			String typeXml = node.getTextContent(); 
			if((typeXml.equals("e")||typeXml.equals("f")) && (type==null ||type.equals("F"))){
				return "F";
			}
			else if(typeXml.equals("b")  && (type==null||type.equals("D"))){
				return "D";
			}
			else if((typeXml.equals("d")||typeXml.equals("i"))  && (type==null||type.equals("S"))){
				return "S";
			}
			else{
				logger.warn("Wrong type iccd:"+type+"!= sbn:"+typeXml);
				return null;
			}
		} catch (Exception e) {
			logger.error("",e);
		}
		return null;
	}
	


	protected void makeSubNode(Document doc, String nodeName, String value, Node eChild) {
		if(value==null)
			return;
		Node aChild;
		aChild = doc.createElement(nodeName);
		aChild.setTextContent(value);
		eChild.appendChild(aChild);
	}

	


}
