package com.gruppometa.metasearch.data;

import java.util.List;

import org.apache.solr.client.solrj.response.QueryResponse;


public class SolrResponse implements Response{
	protected QueryResponse resp;
	protected ResponseCreator responseCreator;
	protected List<String> views;
	protected List<FacetField> facetFields;
	protected List<Document> docs;
	public SolrResponse(QueryResponse resp,ResponseCreator responseCreator, List<String> views, String profileName){
		this.resp = resp; 
		this.views = views;
		this.responseCreator = responseCreator;
		this.facetFields = responseCreator.createFacets(resp,views, profileName);
		this.docs = responseCreator.createDocs(resp,views, profileName);
	}
	public long getNumFound(){
		return resp.getResults().getNumFound();
	}
	
	public long getStart(){
		return resp.getResults().getStart();
	}
		
	public List<FacetField> getFacetsFields(){
		return facetFields;
	}
	
	public List<Document> getDocs(){
		return docs;
	}

}
