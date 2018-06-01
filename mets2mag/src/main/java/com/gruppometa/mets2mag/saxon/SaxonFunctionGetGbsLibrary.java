package com.gruppometa.mets2mag.saxon;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SaxonFunctionGetGbsLibrary extends SaxonFunctionGetTypeFromLeader{

	protected static Properties properties = null;
	
	protected static final Logger logger = LoggerFactory.getLogger(SaxonFunctionGetGbsLibrary.class);
	@Override
	public String getValue(String c) {
		if(properties==null){
			properties = new Properties();
			try {
				properties.load(SaxonFunctionGetGbsLibrary.class.getResourceAsStream("/gbslibraries.properties"));
			} catch (IOException e) {
				logger.error("",e);
			}
		}
		return properties.getProperty(c)!=null?properties.getProperty(c):c;
	}

	@Override
	protected String getFunctionName() {
		return "getGbsLibrary";
	}

}
