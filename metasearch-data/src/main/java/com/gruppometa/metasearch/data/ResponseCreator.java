package com.gruppometa.metasearch.data;

import java.util.List;

import org.apache.solr.client.solrj.response.QueryResponse;

public interface ResponseCreator {
	public List<Document> createDocs(QueryResponse resp, List<String> views,String profileName);
	public List<Document> createDocs(QueryResponse resp, List<String> views,String profileName, List<String> biblioteca);
	public List<FacetField> createFacets(QueryResponse resp, List<String> views,String profileName);
}
