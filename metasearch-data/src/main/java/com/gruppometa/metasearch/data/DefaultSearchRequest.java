package com.gruppometa.metasearch.data;

import com.gruppometa.metasearch.query.Query;

public class DefaultSearchRequest implements SearchRequest{
	protected Query query;
	
	public void setQuery(Query query) {
		this.query = query;
	}

	@Override
	public Query getQuery() {
		return query;
	}

}
