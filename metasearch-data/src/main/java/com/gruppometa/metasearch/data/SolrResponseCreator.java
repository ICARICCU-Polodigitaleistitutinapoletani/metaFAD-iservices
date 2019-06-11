package com.gruppometa.metasearch.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;

public class SolrResponseCreator implements ResponseCreator{
	public List<Document> createDocs(QueryResponse resp, List<String> views,String profileName){
		return createDocs(resp, views, profileName, null);
	}
	public List<Document> createDocs(QueryResponse resp, List<String> views,String profileName, List<String> biblioteca){
		List<Document> docs = new ArrayList<Document>();
		for(org.apache.solr.common.SolrDocument doc: resp.getResults()){
			Document doc2 = new Document();
			doc2.setId((String)doc.getFieldValue("id"));
			for (String view : views) {
				View v = new View();				
				v.setViewType(view);
				for(String n: doc.getFieldNames()){
					Field f = new Field();
					f.setId(n);
					for(Object ob: doc.getFieldValues(n)){
						f.getValues().add(ob);
					}
					v.getNodes().add(f);
				}
				doc2.getNodes().add(v);
			}
			docs.add(doc2);
		}
		return docs;
	}

	public List<FacetField> createFacets(QueryResponse resp, List<String> views,String profileName) {
		List<FacetField> facets = new ArrayList<FacetField>();
		if(resp.getFacetFields()==null)
			return facets;
		for (org.apache.solr.client.solrj.response.FacetField facet : resp.getFacetFields()) {
			FacetField f = new FacetField();
			f.setId(facet.getName());
			List<Count> counts = new ArrayList<Count>();
			for (org.apache.solr.client.solrj.response.FacetField.Count c : facet.getValues()) {
				Count count = new Count(c.getCount(),c.getName());
				counts.add(count);
			}
			f.setValues(counts);
			facets.add(f);
		} 
		return facets;
	}

	public String getSolrNameForFacet(String facet, String profileName) throws FieldException{
		return facet;
	}

	public String getSolrNameTextFromFacet(String facet){
		return facet;
	}
	public void init(){

	}
	public void addFields(SolrQuery solrQuery, String profileName,String viewName, boolean withHtml){

	}

	public void addFacetFields(SolrQuery solrQuery, String profileName,String viewName, boolean secondLevel){

	}
}
