package com.gruppometa.unimarc.object;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public class JsonOutItem {
	protected String id;
	protected String filename;
	protected String isbd;
	protected Timestamp created;
	protected String mapVersion;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getIsbd() {
		return isbd;
	}
	public void setIsbd(String isbd) {
		this.isbd = isbd;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="CET")
	public Timestamp getCreated() {
		return created;
	}
	public void setCreated(Timestamp created) {
		this.created = created;
	}
	public String getMapVersion() {
		return mapVersion;
	}
	public void setMapVersion(String mapVersion) {
		this.mapVersion = mapVersion;
	}
	public List<JsonOutField> getFields() {
		return fields;
	}
	public void setFields(List<JsonOutField> fields) {
		this.fields = fields;
	}
	protected List<JsonOutField> fields = new ArrayList<JsonOutField>();
}
