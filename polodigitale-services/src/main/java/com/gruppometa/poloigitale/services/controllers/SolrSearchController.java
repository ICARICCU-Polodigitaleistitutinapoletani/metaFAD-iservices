package com.gruppometa.poloigitale.services.controllers;

import com.gruppometa.metasearch.data.*;
import com.gruppometa.poloigitale.services.components.MappedProfileDefinitor;
import com.gruppometa.poloigitale.services.components.MappedResponseCreator;
import com.gruppometa.unimarc.mapping.MappingDefinition;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@RestController
@ConfigurationProperties(prefix="solrSearchController")
@RequestMapping("/{profileName:opac|opac-au|iccdau|iccd|iccd-f|iccd-s|iccd-d|iccd-oa|archivio-ca|archivio-ud|archivio-ua|archivio-au|metaindice|metaindice-au}")
public class SolrSearchController {

	protected static final Logger logger = LoggerFactory.getLogger(SolrSearchController.class);
	
	@Autowired
	protected SolrResponseCreator responseCreator;

	public boolean isEnableLocationFilter() {
		return enableLocationFilter;
	}

	public void setEnableLocationFilter(boolean enableLocationFilter) {
		this.enableLocationFilter = enableLocationFilter;
	}

	protected boolean enableLocationFilter = true;


	@Autowired
	protected ProfileDefinitor mappedProfileDefinitor;

	@Autowired
	protected SolrQueryCreator queryCreator;// = new MappedSolrQueryCreator();
	
	protected String solrUrl = "http://54.217.205.30:8080/solr/disko";

	public HashMap<String, String> getSolrUrls() {
		return solrUrls;
	}

	public void setSolrUrls(HashMap<String, String> solrUrls) {
		this.solrUrls = solrUrls;
	}

	protected HashMap<String, String> solrUrls;

	protected List<Label> labels;

	public List<String> getUpdatableFields() {
		return updatableFields;
	}

	public void setUpdatableFields(List<String> updatableFields) {
		this.updatableFields = updatableFields;
	}

	protected List<String> updatableFields;
	
	public List<Label> getLabels() {
		return labels;
	}

	public void setLabels(List<Label> labels) {
		this.labels = labels;
	}

	public String getIccdSelector() {
		return iccdSelector;
	}

	public void setIccdSelector(String iccdSelector) {
		this.iccdSelector = iccdSelector;
	}

	protected String iccdSelector = "tipo_di_scheda_ss";

	protected String iccdAuSelector = "versione_scheda_s";

	protected String getSubProfileFromId(String id, String profileName){
		String ret = profileName;
		if(!profileName.equals("iccd") && !profileName.equals("iccdau"))
			return ret;
		try {
			boolean isAu = (profileName.equals("iccdau"));
			HttpSolrClient httpSolrClient = new HttpSolrClient(getSolrUrl(profileName));
			httpSolrClient.setParser(new XMLResponseParser());
			String query =  "id:"+ ClientUtils.escapeQueryChars(id);
			SolrQuery solrQuery = new SolrQuery();
			solrQuery.addField("id");
			String selector = isAu?iccdAuSelector:iccdSelector;
			solrQuery.addField(selector);
			solrQuery.setRows(1);
			solrQuery.setStart(0);
			solrQuery.setQuery(query);
			QueryResponse rsp = httpSolrClient.query(solrQuery,METHOD.POST);
			if(rsp.getResults().getNumFound()>0){
				Collection<Object> coll = rsp.getResults().get(0).getFieldValues(selector);
				if(coll.size()>0)
					ret =  (isAu?"iccdau":"iccd")+"-"
							+((String)coll.iterator().next()).toLowerCase();
			}
			httpSolrClient.close();
		} catch (Exception e) {
			logger.error("",e);
		}
		return ret;
	}

	@RequestMapping("/resource")
	public SearchResponse getDoc(
			@RequestParam(value="id") String id,
			@RequestParam(value="view",defaultValue="full",required=false) String view,
			@PathVariable(value="profileName") String profileName,
			@RequestParam(value="biblioteca", required = false) List<String> biblioteca
			){
		try {
			String subProfile = getSubProfileFromId(id, profileName);
			//logger.debug("Profile: "+subProfile);
			List<String> views = new ArrayList<>();
			views.add(view);
			HttpSolrClient httpSolrClient = new HttpSolrClient(getSolrUrl(subProfile));
			httpSolrClient.setParser(new XMLResponseParser());
			SolrQuery solrQuery = queryCreator.createQuery4Id(id, profileName);
			for (String viewName : views) {
				responseCreator.addFields(solrQuery,subProfile,viewName, true);
			}
			//logger.debug("Query:"+solrQuery);
			QueryResponse rsp = httpSolrClient.query(solrQuery,METHOD.POST);
			SolrSearchResponse ret =  new SolrSearchResponse(rsp, responseCreator, views, subProfile,
					isEnableLocationFilter()?biblioteca:null);
			httpSolrClient.close();
			return ret;
		} catch (Exception e) {
			logger.error("",e);
			return getErrorResponse(e.getMessage());
		}
	}
	
	@RequestMapping("/fields")
	public FieldList getFields(
			@RequestParam(value="prefix", required=false) String prefix,
			@PathVariable(value="profileName") String profileName
	){
		return mappedProfileDefinitor.getFields("search",prefix,profileName);
	}
	


	@RequestMapping("/facet")
	public SearchResponse getFacets(
			@RequestParam(value="id") String facet,
			@RequestParam(value="prefix", required=false) String prefix,
			@RequestParam(value="rows", defaultValue = "100") int rows,
			@RequestParam(value="offset", defaultValue = "0") int offset,
			@PathVariable(value="profileName") String profileName
			){
		try {
			List<String> views = new ArrayList<>();
			views.add("short");
			responseCreator.init();
			HttpSolrClient httpSolrClient = new HttpSolrClient(getSolrUrl(profileName));
			httpSolrClient.setParser(new XMLResponseParser());
			String query =  "*:*";
			SolrQuery solrQuery = queryCreator.createQuery4Facet(facet, prefix, profileName);
			solrQuery.setRows(rows);
			solrQuery.setStart(offset);
 			QueryResponse rsp = httpSolrClient.query(solrQuery);
			SolrSearchResponse response =  new SolrSearchResponse(rsp, responseCreator, views,profileName);
			/**
			 * filtro i valori
			 */
			if(prefix!=null && prefix.length()>0 && response.getResponse().getFacetsFields().size()>0) {
				FacetField facetField = response.getResponse().getFacetsFields().get(0);
				for (int i = facetField.getValues().size()-1; i>=0; i--){
					if(!facetField.getValues().get(i).getName().toLowerCase().contains(prefix))
						facetField.getValues().remove(i);
				}
			}
			httpSolrClient.close();
			return response;
		} catch (Exception e) {
			logger.error("",e);
			return getErrorResponse(e.getMessage());
		}		

	}

	@RequestMapping(value="/search",
			method=RequestMethod.POST)
	public SearchResponse getDocsPost(
			@RequestBody DefaultSearchRequest request,
			@PathVariable(value="profileName") String profileName
			){
		try{
			List<String> views = new ArrayList<>();
			views.add("short");
			HttpSolrClient httpSolrClient = new HttpSolrClient(getSolrUrl(profileName));
			httpSolrClient.setParser(new XMLResponseParser());
			SolrQuery solrQuery = queryCreator.createQuery(request.getQuery(), profileName);
			solrQuery.addField("id");
			solrQuery.addField("score");
			responseCreator.addFields(solrQuery,profileName,"short", false);
			responseCreator.addFacetFields(solrQuery,profileName,"short", false);
			logger.debug(solrQuery.toString());
			QueryResponse rsp = httpSolrClient.query(solrQuery, METHOD.POST);
			SolrSearchResponse response =  new SolrSearchResponse(rsp, responseCreator, views,profileName);
			logger.debug("Found: "+response.getResponse().getNumFound());
			httpSolrClient.close();
			return response;
		} catch (Exception e) {
			logger.error("",e);
			return getErrorResponse(e.getMessage());
		}
	}

	protected String getSolrUrl(String profileName){
		return (solrUrls!=null && solrUrls.get(profileName)!=null)?solrUrls.get(profileName):solrUrl;
	}

	protected String checkExistsDoc(String id){
		if(!checkExists(id, "opac"))
			return "{\"status\":\"ko\",\n\"error\":\""+ StringEscapeUtils.escapeJson("Doc '"+id+"' not found in opac")+"\"}";
		if(!checkExists(id, "metaindice"))
			return "{\"status\":\"ko\",\n\"error\":\""+ StringEscapeUtils.escapeJson("Doc '"+id+"' not found in metaindice")+"\"}";
		return null;
	}
	@RequestMapping(value="/updateSbnDigitale",
			method=RequestMethod.GET, produces =MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String getUpdateDigitale(
			@RequestParam(value="id") String id,
			@RequestParam(value="idpreview",required = false) String idPreview,
			@RequestParam(value="digitale") boolean digitale){
		try{
			String retMessage = checkExistsDoc(id);
			if(retMessage!=null)
				return retMessage;
			String digi = digitale?"true":null;
			boolean ret = updateField("opac",id, "digitale", digi);
			if(ret)
				ret = updateField("metaindice",id, "digitale", digi);
			if(ret) {
				String preview = (idPreview==null || !digitale)?"":idPreview;
				if(preview.equals(""))
					preview = null;
				ret = updateField("opac", id, "digitale_idpreview", preview);
				if(ret)
					ret = updateField("metaindice", id, "digitale_idpreview", preview);
			}
			if(ret)
				return "{\"status\":\"ok\"}";
			else
				return "{\"status\":\"ko\"}";
		} catch (Exception e) {
			logger.error("",e);
			return "{\"status\":\"ko\",\n\"error\":\""+ StringEscapeUtils.escapeJson(e.getMessage())+"\"}";
		}
	}

	@RequestMapping(value="/updateSbnToIccd",
			method=RequestMethod.GET, produces =MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String getUpdateASbn(
			@RequestParam(value="id") String id,
			@RequestParam(value="linkedIccd",required = false) String linkedIccd,
            @PathVariable(value="profileName") String profileName
			){
		try{
		    if(!profileName.equals("opac") && !profileName.equals("opac-au"))
                return "{\"status\":\"ko\",\n\"error\":\""+ StringEscapeUtils.escapeJson("Unsupported operation for "+profileName)+"\"}";
			boolean ret = updateField(profileName,id, "linkediccd", linkedIccd);
			if(ret)
				return "{\"status\":\"ok\"}";
			else
				return "{\"status\":\"ko\"}";
		} catch (Exception e) {
			logger.error("",e);
			return "{\"status\":\"ko\",\n\"error\":\""+ StringEscapeUtils.escapeJson(e.getMessage())+"\"}";
		}
	}


	@RequestMapping(value="/updateEcommerce",
			method=RequestMethod.POST, produces =MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String getUpdateEcommerce(
			@RequestParam(value="id") String id,
			@RequestBody(required = false) String ecommerce,
			@PathVariable(value="profileName") String profileName
	){
		try{
			String retMessage = checkExistsDoc(id);
			if(retMessage!=null)
				return retMessage;
			if(ecommerce!=null && ecommerce.length()==0)
				ecommerce = null;
			boolean ret = updateField("opac",id, "ecommerce_nxs", ecommerce, true);
			if(ret)
				ret = updateField("metaindice",id, "ecommerce_nxs", ecommerce, true);
			if(ret)
				return "{\"status\":\"ok\"}";
			else
				return "{\"status\":\"ko\"}";
		} catch (Exception e) {
			logger.error("",e);
			return "{\"status\":\"ko\",\n\"error\":\""+ StringEscapeUtils.escapeJson(e.getMessage())+"\"}";
		}
	}

	@RequestMapping(value="/updateVisibility",
			method=RequestMethod.POST, produces =MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String getUpdateVisibility(
			@RequestParam(value="id") String id,
			@RequestBody(required = false) String visibility,
			@PathVariable(value="profileName") String profileName
	){
		try{
			String retMessage = checkExistsDoc(id);
			if(retMessage!=null)
				return retMessage;
			if(visibility!=null && visibility.length()==0)
				visibility = null;
			boolean ret = updateField("opac",id, "visibility_nxs", visibility, true);
			if(ret)
				ret = updateField("metaindice",id, "visibility_nxs", visibility, true);
			if(ret)
				return "{\"status\":\"ok\"}";
			else
				return "{\"status\":\"ko\"}";
		} catch (Exception e) {
			logger.error("",e);
			return "{\"status\":\"ko\",\n\"error\":\""+ StringEscapeUtils.escapeJson(e.getMessage())+"\"}";
		}
	}

	@RequestMapping(value="/update",
			method=RequestMethod.GET, produces =MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String getUpdate(
			@RequestParam(value="id") String id,
			@RequestParam(value="field") String field,
			@RequestParam(value="value") String value,
			@PathVariable(value="profileName") String profileName
			){
		try{
			if(updatableFields==null || !updatableFields.contains(field)) {
				String ret = "Error: field '" + field + "' not updable";
				return "{\"status\":\"ko\",\n\"error\":\""+ StringEscapeUtils.escapeJson(ret)+"\"}";
			}
			boolean ret = updateField(profileName,id,field, value);
			if(ret)
				return "{\"status\":\"ok\"}";
			else
				return "{\"status\":\"ko\"}";
		} catch (Exception e) {
			logger.error("",e);
			return "{\"status\":\"ko\",\n\"error\":\""+ StringEscapeUtils.escapeJson(e.getMessage())+"\"}";
		}
	}

	protected boolean updateField(String profileName, String id, String field, String value) throws IOException, SolrServerException {
		return updateField(profileName, id, field, value, false);
	}

	protected boolean checkExists(String id, String profileName){
		HttpSolrClient httpSolrClient = new HttpSolrClient(getSolrUrl(profileName));
		httpSolrClient.setParser(new XMLResponseParser());
		SolrQuery solrQuery = new SolrQuery("id:\""+ClientUtils.escapeQueryChars(id)+"\"");
		solrQuery.setRows(1);
		solrQuery.setFields("id");
		long ret = 0;
		try {
			QueryResponse response = httpSolrClient.query(solrQuery);
			ret = response.getResults().getNumFound();
			httpSolrClient.close();
		} catch (Exception e) {
			logger.error("",e);
		}
		return ret == 1;
	}

	protected boolean updateField(String profileName, String id, String field, String value, boolean isNativeName) throws IOException, SolrServerException {
		HttpSolrClient httpSolrClient = new HttpSolrClient(getSolrUrl(profileName));
		httpSolrClient.setParser(new XMLResponseParser());
		SolrInputDocument doc = new SolrInputDocument();
		Map<String, String> partialUpdate = new HashMap<String, String>();
		partialUpdate.put("set", value);
		//Map<String, String> partialUpdateTimestamp = new HashMap<String, String>();
		//partialUpdateTimestamp.put("set", "NOW");
		doc.addField("id", id);
		doc.setField("_version_",0);
		if(isNativeName){
			doc.addField(field, partialUpdate);
		}
		else {
			doc.addField(field + "_s", partialUpdate);
			doc.addField(field + "_t", partialUpdate);
		}
		doc.setField("timestamp", "NOW");
		//doc.addField("timestamp", partialUpdateTimestamp);
		UpdateResponse rsp = httpSolrClient.add(doc);
		boolean ret = (rsp.getStatus()==0);
		httpSolrClient.commit();
		httpSolrClient.close();
		return ret;
	}

	@RequestMapping(value="/search",
			method=RequestMethod.GET)
	public SearchResponse getDocs(
			@RequestParam(value="q") String q,
			@RequestParam(value="fq",required=false) List<String> fq,
			@RequestParam(value="facets",required=false) List<String> facetsUser,
			@RequestParam(value="facetsAdd",defaultValue="true",required=false) boolean facetsAdd,
			@RequestParam(value="rows",defaultValue="10",required=false) int rows,			
			@RequestParam(value="start",defaultValue="0",required=false) int start,
			@RequestParam(value="facetLimit",defaultValue="100",required=false) int facetLimit,
			@PathVariable(value="profileName") String profileName
			) {
		try {
			List<String> views = new ArrayList<>();
			views.add("short");
			HttpSolrClient httpSolrClient = new HttpSolrClient(getSolrUrl(profileName));
			httpSolrClient.setParser(new XMLResponseParser());
			SolrQuery solrQuery = new SolrQuery();
			if(fq!=null){
				for (String fq1 : fq) {
					solrQuery.setFilterQueries(fq1);	
				}				
			}
			solrQuery.addField("id");
			solrQuery.addField("score");
			responseCreator.addFields(solrQuery,profileName,"short", false);
			if(facetsAdd)
				responseCreator.addFacetFields(solrQuery,profileName,"short", false);
			/**
			 * facette definite dal client
			 */
			if(facetsUser!=null){
				for (String str : facetsUser) {
					solrQuery.addFacetField(str);
				}
			}
			
			solrQuery.setFacetMinCount(1);
			solrQuery.setFacetLimit(facetLimit);
			solrQuery.setRows(rows);
			solrQuery.setStart(start);
			solrQuery.setQuery(q);
			//logger.debug(solrQuery.toString());
			QueryResponse rsp = httpSolrClient.query(solrQuery);
			SolrSearchResponse response =  new SolrSearchResponse(rsp, responseCreator, views,profileName);
			httpSolrClient.close();
			return response;
		} catch (Exception e) {
			logger.error("",e);
			return getErrorResponse(e.getMessage());
		}		
	}

	protected SolrSearchResponse getErrorResponse(String error){
		SolrSearchResponse rsp = new SolrSearchResponse();
		rsp.setError(error);
		return rsp;
	}


	public String getSolrUrl() {
		return solrUrl;
	}

	public void setSolrUrl(String solrUrl) {
		this.solrUrl = solrUrl;
	}
	


}