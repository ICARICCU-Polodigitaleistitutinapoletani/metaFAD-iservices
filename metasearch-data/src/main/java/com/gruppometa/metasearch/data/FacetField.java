package com.gruppometa.metasearch.data;

import java.util.ArrayList;
import java.util.List;


public class FacetField {
	protected String id;
	protected String label;
	protected List<Count> values = new ArrayList<Count>();
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public List<Count> getValues() {
		return values;
	}
	public void setValues(List<Count> values) {
		this.values = values;
	}
}
