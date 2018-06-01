package com.gruppometa.unimarc.util;

import java.util.Iterator;
import java.util.List;

public class StringUtil {
	public static String filterAttrs(String str){
		if(str==null)
			return null;
		return str.replaceAll("\\[VID:(.*?)\\]", "")
			.replaceAll("\\[ROLE:(.*?)\\]", "")
			.replaceAll("\\[BID:(.*?)\\]", "")
			.replaceAll("#", " ")
			.replaceAll("_", " ");
	}

	public static String firstUpper(String nodename) {
		if(nodename==null)
			return null;
		if(nodename.length()>0)
			return nodename.substring(0,1).toUpperCase()+nodename.substring(1);
		else
			return "";
			
	}

	public static String fill(String substring, int i) {
		if(substring==null)
			return null;
		if(substring.length()>i)
			return substring;
		for (int j = 0; substring.length()<i; j++) {
			substring= "0"+substring;
		}
		return substring;
	}
	public static String join(List<String> values){
		return join(values, " ");
	}
	public static String join(List<String> values,String sep) {
		String ret = ""; 
		for (Iterator<String> iterator = values.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			if(ret.length()>0)
				ret+= sep;
			ret += string;
		}
		return ret;
	}

	public static String cut2n(String val, int maxLength, String ending) {
		if(val==null)
			return null;
		if(val.length()<=maxLength)
			return val;
		else
			return val.substring(0, maxLength)+ending;
	}

	public static String xml(String about) {		
		if(about==null)
			return "";
		return about.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;");
	}

	public static String sqlencode(String textValue) {
		if(textValue==null)
			return "";
		String ret = textValue.replaceAll("'", "''");
		return ret.replaceAll("\\\\", "\\\\\\\\");
	} 
}
