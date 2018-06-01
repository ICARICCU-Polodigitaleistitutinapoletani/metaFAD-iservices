package com.gruppometa.metasearch.data;

import java.util.List;



public interface Response {
	public long getNumFound();
	public long getStart();	
	public List<FacetField> getFacetsFields();
	
	public List<Document> getDocs();
}
