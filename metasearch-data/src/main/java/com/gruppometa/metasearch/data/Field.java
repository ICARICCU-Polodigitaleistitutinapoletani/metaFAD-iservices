package com.gruppometa.metasearch.data;

import java.util.ArrayList;
import java.util.List;

public class Field implements ViewItem{
	protected String id;
	protected String label;
	protected String datatype = "string";
	protected String listType = null;
	protected String searchType = null;
	protected String parentLabel = null;
	protected String parentId = null;
	protected boolean startsWith = false;

	public boolean isStartsWith() {
		return startsWith;
	}

	public void setStartsWith(boolean startsWith) {
		this.startsWith = startsWith;
	}

	public String getParentLabel() {
		return parentLabel;
	}
	public void setParentLabel(String parentLabel){
		this.parentLabel = parentLabel;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId){
		this.parentId = parentId;
	}
	protected boolean multiple = false;

	public String getSearchType() {
		return searchType;
	}

	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	protected boolean fulltext = true;
	public boolean isMultiple() {
		return multiple;
	}

	public String getListType() {
		return listType;
	}

	public void setListType(String listType) {
		this.listType = listType;
	}

	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}
	public boolean isFulltext() {
		return fulltext;
	}
	public void setFulltext(boolean fulltext) {
		this.fulltext = fulltext;
	}
	protected boolean facet = false;
	protected boolean hideLabel = false;

	public boolean isHideLabel() {
		return hideLabel;
	}

	public void setHideLabel(boolean hideLabel) {
		this.hideLabel = hideLabel;
	}

	public boolean isFacet() {
		return facet;
	}
	public void setFacet(boolean facet) {
		this.facet = facet;
	}
	
	public String getDatatype() {
		return datatype;
	}
	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}
	protected String mimetype="plain"; 
	public String getMimetype() {
		return mimetype;
	}
	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	protected List<Object> values = new ArrayList<Object>();
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<Object> getValues() {
		return values;
	}
	public void setValues(List<Object> values) {
		this.values = values;
	}
	public String getType() {
		return "field";
	}
	protected boolean range;

	public boolean isRange() {
		return range;
	}

	public void setRange(boolean range) {
		this.range = range;
	}

	protected List<Object> objectValues = new ArrayList<Object>();

	public List<Object> getObjectValues() {
		return objectValues;
	}

	public void setObjectValues(List<Object> objectValues) {
		this.objectValues = objectValues;
	}

	public String getObjectValueType() {
		return objectValueType;
	}

	public void setObjectValueType(String objectValueType) {
		this.objectValueType = objectValueType;
	}

	protected String objectValueType;
}
