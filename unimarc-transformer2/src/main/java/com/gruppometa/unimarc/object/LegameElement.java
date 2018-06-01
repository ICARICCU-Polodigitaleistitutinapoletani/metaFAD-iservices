package com.gruppometa.unimarc.object;

import java.io.Serializable;

public class LegameElement implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1574719151928466198L;
	protected String sourceId;
	protected String targetId;
	protected String label;
	protected String type;
	public String getSourceId() {
		return sourceId;
	}
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}
	public String getTargetId() {
		return targetId;
	}
	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public LegameElement(){
		
	}
	public LegameElement(String sourceId, String targetId, String label, String type) {
		super();
		this.sourceId = sourceId;
		this.targetId = targetId;
		this.label = label;
		this.type = type;
	}
}
