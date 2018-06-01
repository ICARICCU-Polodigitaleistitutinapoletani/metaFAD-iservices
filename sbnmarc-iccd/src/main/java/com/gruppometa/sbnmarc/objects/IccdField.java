package com.gruppometa.sbnmarc.objects;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;



@XmlRootElement(namespace="http://polodigitale.it/iccd")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IccdField {
	protected String name;
	protected String value = null;
	protected String vid = null;
	
	@XmlAttribute(name="vid",namespace="http://polodigitale.it/iccd")
	public String getVid() {
		return vid;
	}
	public void setVid(String vid) {
		this.vid = vid;
	}
	protected List<IccdField> fields = null;
	@XmlAttribute(name="name",namespace="http://polodigitale.it/iccd")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@XmlAttribute(name="value",namespace="http://polodigitale.it/iccd")
	public String getValue() {
		return value;
	}
	@JsonProperty("nodes")
	@XmlElement(name="node",namespace="http://polodigitale.it/iccd")
	public List<IccdField> getFields() {
		return fields;
	}
	public void setFields(List<IccdField> fields) {
		this.fields = fields;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
