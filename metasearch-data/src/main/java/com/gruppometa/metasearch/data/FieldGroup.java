package com.gruppometa.metasearch.data;

import java.util.ArrayList;
import java.util.List;

public class FieldGroup implements ViewItem{
	
	public List<ViewItem> getNodes() {
		return fields;
	}
	public void setNodes(List<ViewItem> fields) {
		this.fields = fields;
	}
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
	protected List<ViewItem> fields = new ArrayList<ViewItem>();
	protected String id;
	protected String label;
	public String getType() {
		return "group";
	}
}
