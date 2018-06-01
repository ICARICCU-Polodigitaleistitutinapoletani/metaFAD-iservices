package com.gruppometa.mets2mag.saxon;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SaxonFunctionGetRuolo extends SaxonFunctionGetTypeFromLeader{

	protected static Properties properties = null;
	
	protected static final Logger logger = LoggerFactory.getLogger(SaxonFunctionGetRuolo.class);
	@Override
	public String getValue(String c) {
		if(properties==null){
			properties = new Properties();
			try {
				properties.load(SaxonFunctionGetRuolo.class.getResourceAsStream("/ruoli.properties"));
			} catch (IOException e) {
				logger.error("",e);
			}
		}
		String cOri = c;
		while(c.startsWith("0"))
			c = c.substring(1);
		if(properties.getProperty(c)!=null){
			String val = properties.getProperty(c);
			if(val.lastIndexOf(" ")!=-1)
				return properties.getProperty(c).substring(0,val.lastIndexOf(" "));
			else
				return val;
		}
		return cOri;
	}

	@Override
	protected String getFunctionName() {
		return "getRuolo";
	}

}
