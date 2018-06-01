package com.gruppometa.poloigitale.services.components;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Base64;

import javax.annotation.PostConstruct;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.gruppometa.sbnmarc.SaxonHelper;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.marc4j.MarcPermissiveStreamReader;
import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;
import org.marc4j.MarcXmlWriter;
import org.marc4j.converter.impl.AnselToUnicode;
import org.marc4j.marc.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="unimarcClient")
public class UnimarcClient {
	
	protected String solrUrl;
	public String getSolrUrl() {
		return solrUrl;
	}

	public void setSolrUrl(String solrUrl) {
		this.solrUrl = solrUrl;
	}

	public String getUnimarcBinaryFieldName() {
		return unimarcBinaryFieldName;
	}

	public void setUnimarcBinaryFieldName(String unimarcBinaryFieldName) {
		this.unimarcBinaryFieldName = unimarcBinaryFieldName;
	}

	protected String unimarcBinaryFieldName="";
	
	
	@PostConstruct
	protected void postInit(){
	}

	protected static final Logger logger = LoggerFactory.getLogger(UnimarcClient.class);
	
	public String getResponse(String filename){
		InputStream input;
		try {
			input = new FileInputStream(filename);
	        return getResponse(input, null);
		} catch (FileNotFoundException e) {
			logger.error("",e);
		}
		return null;
	}
	public String getResponseFromSolr(String bid) throws Exception{
		return getResponseFromSolr(bid, null);
	}

	public String getResponseFromSolr(String bid, String secondTransformer2) throws Exception{
		String base64unimarc = null;
		SolrQuery query = new SolrQuery();
		query.setFields(unimarcBinaryFieldName);
		query.setQuery("id:"+bid);
		try {
			HttpSolrClient 	server =  new HttpSolrClient(solrUrl);
			server.setParser(new XMLResponseParser());
			QueryResponse resp = server.query(query);
			if(resp.getResults().getNumFound()>0){
				if(resp.getResults().get(0).getFieldValues(unimarcBinaryFieldName)!=null){
					base64unimarc = resp.getResults().get(0).getFieldValues(unimarcBinaryFieldName).iterator().next().toString();
					byte[] data = Base64.getDecoder().decode(base64unimarc);
					ByteArrayInputStream bufInput =	new ByteArrayInputStream(data);
					return getResponse(bufInput, secondTransformer2);
				}
				else
					logger.debug("No "+unimarcBinaryFieldName+ " for " + bid);
			}
			server.close();
		} catch (Exception e) {
			logger.error("",e);
			logger.error("base64: "+base64unimarc);
			throw e;
		}
		return null;
	}
	
	public String getResponse(InputStream inputStream, String secondTransformer){
		StringWriter stringWriter = new StringWriter();
        
		try{
			@SuppressWarnings("unused")
			//String stylesheetUrl = "http://www.loc.gov/standards/mods/v3/MARC21slim2MODS3.xsl";
	        InputStream inputXsl = UnimarcClient.class.getResourceAsStream(
	        		"/marcxml2sbnmarc.xsl"
			);
			boolean isMarc = (secondTransformer!=null && secondTransformer.equals("marc"));
			Source stylesheet = new StreamSource(inputXsl);
	        Result result = new StreamResult(stringWriter);
	        MarcReader reader = new MarcStreamReader(inputStream,
                    "UTF-8" // va forzato l'encoding UTF-8 perchè manca il BOM nell'array di bytes da Solr
                );
	        MarcXmlWriter writer =
					isMarc?new MarcXmlWriter(result)
							:new MarcXmlWriter(result, stylesheet);
	        // Problema di codifità è già tutto unicode? (31-08-2016): non va convertito
			//writer.setConverter(new AnselToUnicode());

			while (reader.hasNext()) {
	            Record record = (Record) reader.next();
	            writer.write(record);
	            break;
	        }
	        
	        if(writer!=null){
	        	try{
	        		writer.close();
	        	}
	        	catch(NullPointerException e){
	        	}        
	        }
		}
		catch(Exception e){
			logger.error("",e);
		}
		if(secondTransformer!=null){
			// TODO trasformazione sbnmarc to mag p.e.
			return stringWriter.toString();
		}
		else
			return stringWriter.toString();
	}
}
