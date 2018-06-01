package com.gruppometa.metasearch.data;

import java.util.List;

import org.apache.solr.client.solrj.response.QueryResponse;

public class SolrSearchResponse  implements SearchResponse{
	protected String error;
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}

	protected QueryResponse resp;
	protected Response response;
	protected List<String> views;
	protected ResponseCreator responseCreator;
	public SolrSearchResponse(){
		
	}
	public SolrSearchResponse(QueryResponse resp,ResponseCreator responseCreator, List<String> views, String profileName){
		this.resp = resp; 		
		this.views = views;
		response = new SolrResponse(resp,responseCreator,views, profileName);
		this.responseCreator= responseCreator; 
	}
	
	public Response getResponse(){
		return response;
	}
	
	public int getQueryTime(){
		if(resp!=null)
			return resp.getQTime();
		else
			return 0;
	}
}

