package com.gruppometa.sbnmarc;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import com.gruppometa.unimarc.profile.IssuedAndLanguageNormalizer;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.solr.common.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.jaxb.XmlJaxbAnnotationIntrospector;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import com.gruppometa.sbnmarc.objects.IccdException;
import com.gruppometa.sbnmarc.objects.IccdField;
import com.gruppometa.sbnmarc.objects.IccdRecord;
import com.gruppometa.sbnmarc.objects.IccdRecordList;

import it.sbnweb.kardex.CollocazioneType;
import it.sbnweb.kardex.InventarioType;
import it.sbnweb.kardex.PossessoreType;
import it.sbnweb.kardex.SbnwebType;

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

	protected int timeout = 30000;

	public void setTestBib(String testBib) {
		this.testBib = testBib;
	}

	protected String usernamePosseduti;
	protected String passwordPosseduti;
	protected String urlPosseduti;

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

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

	public Document getResponse(String bib, String bid, String type) throws IccdException{
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
					.setConnectTimeout(timeout)
					.setSocketTimeout(timeout)
					.build();
		CloseableHttpClient httpclient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
		
		HttpPost httpPost = new HttpPost(url);
		CloseableHttpResponse response1 = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        IccdException ex = null;
        long breakpoint1 = 0;
        long now = System.currentTimeMillis();
        try{
		    httpPost.setEntity(new StringEntity(str));
			response1 = httpclient.execute(httpPost);
			//System.out.println(response1.getStatusLine());
		    HttpEntity entity2 = response1.getEntity();
		    // do something useful with the response body
		    DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(entity2.getContent());
            // and ensure it is fully consumed
            breakpoint1 = System.currentTimeMillis();
		    EntityUtils.consume(entity2);
		    if(type.equals(TYPE_ITEM) && checkResult(bib, bid, doc))
		    	addPosseduti(bib, bid, doc);
		}
		catch(Exception e){
		    String message = (breakpoint1>0)?
                    ("Second call "+(System.currentTimeMillis()-breakpoint1)+"ms."):
                    ("First call after "+(System.currentTimeMillis()-now)+"ms.");
			logger.error(message,e);
			ex =  new IccdException(e.getMessage());
		}
		finally {
			try {
				if(response1!=null)
					response1.close();
			} catch (IOException e) {
				logger.error("",e);
				ex =  new IccdException(e.getMessage());
			}
		}
		if(ex!=null)
			throw ex;
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
	
	private void addPosseduti(String bib, String bid, Document doc) {
		if(bib==null)
			return;
		//logger.debug("Search posseduti");
		RestTemplateBuilder restTemplateBuilder =  new RestTemplateBuilder();
		RestTemplate restTemplate  = restTemplateBuilder
				.setConnectTimeout(timeout)
				.setReadTimeout(timeout)
				.build();
		List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
		interceptors.add(new BasicAuthorizationInterceptor(getUsernamePosseduti(), getPasswordPosseduti()));
		//interceptors.add(new LoggingRequestInterceptor());
		restTemplate.setInterceptors(interceptors);
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
		JacksonXmlModule module = new JacksonXmlModule();
		module.setDefaultUseWrapper(false);
		XmlMapper xmlMapper = new XmlMapper(module);
		messageConverters.add(new MappingJackson2XmlHttpMessageConverter(xmlMapper));
		restTemplate.setMessageConverters(messageConverters);
		XPath xPath = XPathFactory.newInstance().newXPath();
		try {
			String xpathStr = "//Documento//DatiDocumento";
			Node node = (Node)xPath.compile(xpathStr).evaluate(doc, XPathConstants.NODE);
			if(node==null){
				logger.warn("Not found node '"+xpathStr+"'.");
				return;
			}
			String url =  getUrlPosseduti()+"{biblioteca}/{bid}";
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("bid", bid);
			params.put("biblioteca", getTestBib()!=null ? getTestBib(): bib);

			SbnwebType sbnweb  =  restTemplate.getForObject(url,SbnwebType.class,params);
			Node newChild = null;			
			if(sbnweb.getPosseduto()==null){
				logger.debug("Senza posseduti bid='"+bid+"', bib='"+bib+"'.");
			}
			else{
				List<CollocazioneType> colls = sbnweb.getPosseduto().getCollocazione();
				if(colls==null){
					logger.debug("Senza collezioni.");
				}
				else{
					if(newChild==null){
						newChild = doc.createElement("T950");
						node.appendChild(newChild);
					}
					for (CollocazioneType collocazioneType : colls) {
						makeInventario(doc, newChild, collocazioneType, getBiblioteca(bib));
					}
				}
			}
			/*
			List<InventarioType> invents = sbnweb.getKardex()!=null ?sbnweb.getKardex().getInventario():null;
			if(invents==null){
				logger.debug("Senza inventari.");
			}
			else{					
				if(newChild==null){
					newChild = doc.createElement("T950");
					node.appendChild(newChild);
				}
				for (InventarioType inventarioType : invents) {
					makeInventario(doc, newChild,inventarioType.getCollocazione());
				}
			}
			*/			
		} catch (Exception e) {
			logger.error(""+getUsernamePosseduti()+"::"+getPasswordPosseduti(),e);
		}
		
	}

	protected static IssuedAndLanguageNormalizer issuedAndLanguageNormalizer = new IssuedAndLanguageNormalizer();
	private String getBiblioteca(String bib) {
		Map<String,String> map =  issuedAndLanguageNormalizer.getMap("biblioteche-anagrafica", false);
		String ret = map!=null ? map.get(bib.toLowerCase()): null;
		if(ret!=null)
			return ret;
		else
			return bib;
	}

	protected void makeInventario(Document doc, Node newChild, CollocazioneType collocazioneType, String biblioteca) {
		//logger.debug("Inserimento collocazione.");
		Node child = doc.createElement("a_950");
		newChild.appendChild(child);
		child.setTextContent(biblioteca);
		Node dChild = doc.createElement("d_950");
		newChild.appendChild(dChild);						
		Node aChild = doc.createElement("d_950_3");
		aChild.setTextContent(collocazioneType.getSez());
		dChild.appendChild(aChild);
		aChild = doc.createElement("d_950_13");
		aChild.setTextContent(collocazioneType.getLoc());
		dChild.appendChild(aChild);
		aChild = doc.createElement("d_950_37");
		aChild.setTextContent(collocazioneType.getSpec());
		dChild.appendChild(aChild);
		if(collocazioneType.getInventario()!=null){
			for (InventarioType inv : collocazioneType.getInventario()) {
				Node eChild = doc.createElement("e_950");
				dChild.appendChild(eChild);						
				makeSubNode(doc, "e_950_3",inv.getSerie(), eChild);
				makeSubNode(doc, "e_950_6",inv.getNumero()==0?null:(""+inv.getNumero()), eChild);
				makeSubNode(doc, "e_950_23",inv.getSeq(), eChild);
				makeSubNode(doc, "e_950_44",inv.getPrecis(), eChild);
				makeSubNode(doc, "e_950_20",inv.getCdCons(), eChild);
				makeSubNode(doc, "e_950_20",inv.getCons(), eChild);				
				if(inv.getPossessore()!=null){
					for (PossessoreType pos : inv.getPossessore()) {
						Node pChild = doc.createElement("p_950");
						eChild.appendChild(pChild);						
						pChild.setTextContent(pos.getNome());
					}
				}
				
			}
		}
	}

	protected void makeSubNode(Document doc, String nodeName, String value, Node eChild) {
		if(value==null)
			return;
		Node aChild;
		aChild = doc.createElement(nodeName);
		aChild.setTextContent(value);
		eChild.appendChild(aChild);
	}

	
	public String getResponseAsString(String bib, String bid) throws IccdException{
		return getResponseAsString(bib, bid,"F","4", true);
	}
	public String getResponseAsString(String bib, String bid,String tipoScheda,
			String version,
			boolean transfrom) throws IccdException{
		String type = tipoScheda.equals("AUT")?TYPE_AU:TYPE_ITEM;
		Document doc = null;
		try {
			if(bid.equals("test"))			
				doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
					new File("src/main/resources/example2.xml")
					);
			else
				doc = getResponse(bib, bid, type);

		} catch (Exception e1) {
			logger.error("",e1);
			throw new IccdException(e1.getMessage());
		}  
				//getResponse();
		Transformer transformer =null;
		try {
			transformer = (transfrom?
			SaxonHelper.getInstance().getTransformer(type,version):
				SaxonHelper.getInstance().getTransformerFactory().newTransformer());
		} catch (TransformerConfigurationException e1) {
			logger.error("",e1);
			throw new IccdException(e1.getMessage());
		}
		DOMSource source = new DOMSource(doc);
		
		StringWriter writer = new StringWriter(); 
		StreamResult result = new StreamResult(writer);
		ObjectMapper mapper = new XmlMapper();
		mapper.setAnnotationIntrospector(new XmlJaxbAnnotationIntrospector(mapper.getTypeFactory()));
		ObjectMapper mapper2 = new ObjectMapper();
		mapper2.enable(SerializationFeature.INDENT_OUTPUT);		
		mapper2.setSerializationInclusion(Include.NON_NULL);
		try {
			transformer.setParameter("type", tipoScheda);
			transformer.transform(source, result);
			if(!transfrom)
				return writer.toString();
			IccdRecordList list =  mapper.readValue(writer.toString(), IccdRecordList.class);
			clearOrphans(list);
			/*
			IccdRecordList list = new IccdRecordList();
			list.getRecords().add(new IccdRecord());
			IccdField f = new IccdField();
			f.setName("name");
			f.setValue("value");
			list.getRecords().get(0).getFields().add(f);
			*/
			return mapper2.writeValueAsString(list);
		} catch (Exception e) {
			logger.error("",e);
			throw new IccdException(e.getMessage());
		}
	}
	
	public IccdRecordList getResponseAsList(String bib, String bid,String tipoScheda, String version) throws IccdException{
		Document doc = null;
		String type = (tipoScheda!=null &&tipoScheda.equals("AUT"))?TYPE_AU:TYPE_ITEM;
		try {
			if(bid.equals("test"))			
				doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
					new File("src/main/resources/example2.xml")
					);
			else
				doc = getResponse(bib,bid,type);

		} catch (Exception e1) {
			logger.error("",e1);
			throw new IccdException(e1.getMessage());
		}  
				//getResponse();
		String typeIccd = checkType(tipoScheda, doc); 
		if(typeIccd==null){
			IccdRecordList list = new IccdRecordList();
			list.setStatus("ko");
			if(tipoScheda==null){
				list.setMessage("Il tipo della scheda SBN non e' valido.");
			}
			else{
				list.setMessage("Il tipo della scheda SBN con bid '"+bid+"' non corrisponde al tipo ICCD-"+tipoScheda+".");
			}
			return list;
		}
		Transformer transformer = SaxonHelper.getInstance().getTransformer(type,version);
		DOMSource source = new DOMSource(doc);
		
		StringWriter writer = new StringWriter(); 
		StreamResult result = new StreamResult(writer);
		ObjectMapper mapper = new XmlMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.setAnnotationIntrospector(new XmlJaxbAnnotationIntrospector(mapper.getTypeFactory()));
		try {
			transformer.setParameter("type", typeIccd);
			transformer.transform(source, result);
			IccdRecordList list =  mapper.readValue(writer.toString(), IccdRecordList.class);
			clearOrphans(list);
			/*
			IccdRecordList list = new IccdRecordList();
			list.getRecords().add(new IccdRecord());
			IccdField f = new IccdField();
			f.setName("name");
			f.setValue("value");
			list.getRecords().get(0).getFields().add(f);
			*/
			return list;
		} catch (Exception e) {
			logger.error("",e);
			throw new IccdException(e.getMessage());
		}
	}
	
	protected void clearOrphans(IccdRecordList list){
		for (IccdRecord record : list.getRecords()) {
			clearFieldList(record.getFields());
		}
	}
	
	protected void clearFieldList(List<IccdField> fields){
		List<IccdField> removes = new ArrayList<IccdField>();
		for (IccdField field : fields) {
			if(field.getFields()!=null && field.getFields().size()>0){
				clearFieldList(field.getFields());
			}
			if((field.getValue()==null || field.getValue().equals(""))
						&& (field.getFields()==null || field.getFields().size()==0)){
				removes.add(field);
			}
		}
		for (IccdField iccdField : removes) {
			fields.remove(iccdField);
		}
	}

}
