package com.gruppometa.unimarc.object;

import java.util.HashMap;
import java.util.List;

public interface Field {

	String getTextValue();
	String getBinaryValue();
	
	String getTextValueWithBidAndRole();
	
	String getTextValueWithBidAndRole(HashMap<String, String> roleMap);

	void setTextValue(String out);
	void setBinaryValue(String out);

	String getName();

	String getQualifier();
	
	String getBid();
	String getVid();
	
	String getRole();
	
	int getMultiple();
	
	double getOrderEtichette();
	double getOrderIsbd();
	void setGroup(String group);
	void setOrderEtichette(double order);
	void setOrderIsbd(double order);
	String getGroup();
	
	public List<LabelValuePair> getLabelValuePairs();
	
	public void setLabelValuePairs(List<LabelValuePair> pairs);
}
