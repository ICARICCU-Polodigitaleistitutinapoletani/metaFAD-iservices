package com.gruppometa.sbnmarc.mag;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;
import org.marc4j.MarcXmlWriter;
import org.marc4j.marc.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;

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
	public String getOpacSbn2Mag(MagTransformer magTransformer,
							   String bid) throws MagException {
		return getOpacSbn2X(magTransformer, bid, true, false);
	}

	public String getOpacSbn2Marcxml(MagTransformer magTransformer,
								 String bid) throws MagException {
		return getOpacSbn2X(magTransformer, bid, false, true);
	}

	protected  String getOpacSbn2X(MagTransformer magTransformer,
			 String bid, boolean toMag, boolean toMarcXml) throws MagException {
		String ret = null;
		try {
			String bid2 = bid.startsWith("IT\\ICCU\\")?bid.substring(8):bid;
			bid2 = bid2.replaceAll("\\\\","");
			bid2 = URLEncoder.encode(bid2,"UTF-8");
			InputStream bidInputStream = new URL("http://opac.sbn.it/opacsbn/opaclib?db=solr_iccu&select_db=solr_iccu&" +
					"nentries=1&from=1&searchForm=opac/iccu/error.jsp&resultForward=opac/iccu/scarico_uni.jsp" +
					"&do_cmd=search_show_cmd&format=unimarc&rpnlabel=BID%3D"+bid2+"&" +
					"rpnquery=%40attrset+bib-1++%40attr+1%3D1032+%40attr+4%3D2+%22"+bid2+"%22" +
					"&totalResult=1&fname=none").openStream();
			ret = getResponse(bidInputStream, (toMarcXml?"marc":null) );
		} catch (Exception e) {
			return "<error>"+ StringEscapeUtils.escapeXml11(e.getMessage())+"</error>";
		}
		if(ret!=null && ret.trim().length()>0) {
			if(!toMag)
				return ret;
			try {
				return magTransformer.transform(ret);
			} catch (Exception e) {
				logger.error("",e);
				throw new  MagException(e.getMessage());
			}
		}
		else{
			return null;
		}
	}

}
