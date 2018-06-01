package com.gruppometa.metasearch.query;

import java.util.List;

public class Query {
	protected Clause clause;
	protected int start;
	protected int rows = 10;
	protected int facetLimit = 100;
	protected int facetMinimum = 1;
	//

	public int getFacetMinimum() {
		return facetMinimum;
	}

	public void setFacetMinimum(int facetMinimum) {
		this.facetMinimum = facetMinimum;
	}

	protected List<Clause> filters;
	protected List<String> facets;

	public List<OrderClause> getOrderClauses() {
		return orderClauses;
	}

	public void setOrderClauses(List<OrderClause> orderClauses) {
		this.orderClauses = orderClauses;
	}

	protected List<OrderClause> orderClauses;
	protected List<FieldValuePair> fq;
	public List<FieldValuePair> getFq() {
		return fq;
	}
	public void setFq(List<FieldValuePair> fq) {
		this.fq = fq;
	}
	protected boolean fieldNamesAreNative = false;
	public boolean isFieldNamesAreNative() {
		return fieldNamesAreNative;
	}
	public void setFieldNamesAreNative(boolean fieldNamesAreNative) {
		this.fieldNamesAreNative = fieldNamesAreNative;
	}
	public int getFacetLimit() {
		return facetLimit;
	}
	public void setFacetLimit(int facetLimit) {
		this.facetLimit = facetLimit;
	}
	public List<Clause> getFilters() {
		return filters;
	}
	public void setFilters(List<Clause> filters) {
		this.filters = filters;
	}
	public Clause getClause() {
		return clause;
	}
	public void setClause(Clause clause) {
		this.clause = clause;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getRows() {
		return rows;
	}
	public void setRows(int rows) {
		this.rows = rows;
	}
	public List<String> getFacets() {
		return facets;
	}
	public void setFacets(List<String> facets) {
		this.facets = facets;
	}
}
