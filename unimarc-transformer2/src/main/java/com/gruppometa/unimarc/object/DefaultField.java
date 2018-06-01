package com.gruppometa.unimarc.object;

import java.util.HashMap;
import java.util.List;

import com.gruppometa.unimarc.util.StringUtil;

public class DefaultField implements Field{

	protected List<LabelValuePair> labelValuePairs;
	
	public List<LabelValuePair> getLabelValuePairs() {
		return labelValuePairs;
	}

	public void setLabelValuePairs(List<LabelValuePair> labelValuePairs) {
		this.labelValuePairs = labelValuePairs;
	}

	
	protected String group=null;
	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}


	protected String name="";
	protected String qualifier=null;
	protected String textValue="";
	protected String binaryValue=null;
	public String getBinaryValue() {
		return binaryValue;
	}

	public void setBinaryValue(String binaryValue) {
		this.binaryValue = binaryValue;
	}


	protected String bid = null;
	protected String vid = null;
	protected double orderEtichette;
	protected double orderIsbd;
	
	public double getOrderEtichette() {
		return orderEtichette;
	}


	public void setOrderEtichette(double orderEtichette) {
		this.orderEtichette = orderEtichette;
	}


	public double getOrderIsbd() {
		return orderIsbd;
	}


	public void setOrderIsbd(double orderIsbd) {
		this.orderIsbd = orderIsbd;
	}


	public String getVid() {
		return vid;
	}


	public void setVid(String vid) {
		this.vid = vid;
	}


	protected String role = null;
	protected int multiple = 0;
	/**
	 * @return the multiple
	 */
	public int getMultiple() {
		return multiple;
	}


	/**
	 * @param multiple the multiple to set
	 */
	public void setMultiple(int multiple) {
		this.multiple = multiple;
	}


	/**
	 * @return the bid
	 */
	public String getBid() {
		return bid;
	}


	/**
	 * @param bid the bid to set
	 */
	public void setBid(String bid) {
		this.bid = bid;
	}


	/**
	 * @return the role
	 */
	public String getRole() {
		return role;
	}


	/**
	 * @param role the role to set
	 */
	public void setRole(String role) {
		this.role = role;
	}


	public DefaultField(String name, String qualifier) {
		this.name = StringUtil.firstUpper(name);
		this.qualifier = qualifier;
	}


	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}


	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}


	/**
	 * @return the textValue
	 */
	public String getTextValue() {
		return textValue;
	}

	public String getTextValueWithBidAndRole(){
		return getTextValueWithBidAndRole(null);
	}
	public String getTextValueWithBidAndRole(HashMap<String, String> roleMap){
		return (textValue!=null?textValue:"")+
				((roleMap!=null && roleMap.get(getRole())!=null)
						?(" [ROLE: "+roleMap.get(getRole())+"]")
				:(getRole()!=null?(" [ROLE: "+getRole()+"]"):""))
				+	(getVid()!=null?(" [VID:"+getVid()+"]"):"") 
				+	(getBid()!=null?(" [BID:"+getBid()+"]"):"") 
				;
	}

	
	/**
	 * @param textValue the textValue to set
	 */
	public void setTextValue(String textValue) {
		if(textValue!=null){
			if(textValue.matches("(.*)\\[VID:(.*?)\\](.*)")){
				setVid(textValue.substring(textValue.indexOf("[VID:")+5));
				setVid(getVid().substring(0,getVid().indexOf("]")).trim());
			}
			if(textValue.matches("(.*)\\[BID:(.*?)\\](.*)")){
				setBid(textValue.substring(textValue.indexOf("[BID:")+5));
				setBid(getBid().substring(0,getBid().indexOf("]")).trim());
			}
			if(textValue.matches("(.*)\\[ROLE:(.*?)\\](.*)")){
				setRole(textValue.substring(textValue.indexOf("[ROLE:")+6));
				setRole(getRole().substring(0,getRole().indexOf("]")).trim());
			}
				
		}
		this.textValue = filterAttrs( textValue );
	}

	public static String filterAttrs(String str){
		if(str==null)
			return null;
		return str.replaceAll("\\[VID:(.*?)\\]", "")
			.replaceAll("\\[ROLE:(.*?)\\]", "")
			.replaceAll("\\[BID:(.*?)\\]", "")
			.replaceAll("#", " ")
			.replaceAll("_", " ")
			.trim();
	}

	/**
	 * @return the qualifier
	 */
	public String getQualifier() {
		return qualifier;
	}


	/**
	 * @param qualifier the qualifier to set
	 */
	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}


	
}
