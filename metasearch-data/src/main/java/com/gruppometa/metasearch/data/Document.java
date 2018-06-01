package com.gruppometa.metasearch.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Document {
	protected String id;
	protected float score;
	protected List<ViewItem> fields = new ArrayList<ViewItem>();
	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}

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

	protected HashMap<String, String> attributes;

	public HashMap<String, String> getAttributes() {
		if(attributes==null)
			attributes = new HashMap<String,String>();
		return attributes;
	}

	public void setAttributes(HashMap<String, String> attributes) {
		this.attributes = attributes;
	}
}
