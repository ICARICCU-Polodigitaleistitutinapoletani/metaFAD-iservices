package com.gruppometa.unimarc.object;

public class LabelValuePair {
	protected double order = -1;
	protected String vocabulary = null;
	public String getVocabulary() {
		return vocabulary;
	}
	public void setVocabulary(String vocabulary) {
		this.vocabulary = vocabulary;
	}
	public double getOrder() {
		return order;
	}
	public void setOrder(double order) {
		this.order = order;
	}
	protected String label;
	protected String value;
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public LabelValuePair(String label, String value, double order) {
		super();
		this.label = label;
		this.value = value;
		this.order = order;
	}
	public LabelValuePair(String label, String value) {
		super();
		this.label = label;
		this.value = value;
	}
}
