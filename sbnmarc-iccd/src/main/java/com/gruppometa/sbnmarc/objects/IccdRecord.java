package com.gruppometa.sbnmarc.objects;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IccdRecord {
	protected String id;
	protected String type;
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	protected List<IccdField> fields = new ArrayList<IccdField>();

	@JsonProperty("nodes")
	@XmlElement(name="node",namespace="http://polodigitale.it/iccd")
	public List<IccdField> getFields() {
		return fields;
	}

	public void setFields(List<IccdField> fields) {
		this.fields = fields;
	}
}
