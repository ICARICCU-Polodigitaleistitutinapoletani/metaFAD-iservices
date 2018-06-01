package com.gruppometa.mets2mag.saxon;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SaxonFunctionGetProperty extends SaxonFunctionGetTypeFromLeader{

	protected Properties properties = null;
	
	protected String name;
	protected String functionName;
	public SaxonFunctionGetProperty(String name, String functionName){
		this.name = name;
		this.functionName = functionName;
	}
	
	protected static final Logger logger = LoggerFactory.getLogger(SaxonFunctionGetProperty.class);
	@Override
	public String getValue(String c) {
		if(properties==null){
			properties = new Properties();
			try {
				properties.load(SaxonFunctionGetProperty.class.getResourceAsStream("/"+name+".properties"));
			} catch (IOException e) {
				logger.error("",e);
			}
		}
		return properties.getProperty(c)!=null?properties.getProperty(c):c;
	}

	@Override
	protected String getFunctionName() {
		return functionName;
	}

}
