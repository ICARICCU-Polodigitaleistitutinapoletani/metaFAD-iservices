package com.gruppometa.sbnmarc;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.saxon.Configuration;

public class SaxonHelper {
	protected TransformerFactory factory = null; 
	protected static SaxonHelper instance = new SaxonHelper();
	protected static final Logger logger = LoggerFactory.getLogger(SaxonHelper.class);
 
	public static SaxonHelper getInstance(){
		return instance;
	}
	public TransformerFactory getTransformerFactory(){
		if(factory!=null)
			return factory;
		factory = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl",
				SaxonHelper.class.getClassLoader());
		Configuration c = ((net.sf.saxon.TransformerFactoryImpl) factory).getConfiguration();
		c.registerExtensionFunction(new SaxonFunction("getSufg", "sufg"));
		c.registerExtensionFunction(new SaxonFunction("getTecd", "tecd"));
		c.registerExtensionFunction(new SaxonFunction("getTecs", "tecs"));
		c.registerExtensionFunction(new SaxonFunction("getAutm", "autm-iccd-parts2"));
		c.registerExtensionFunction(new SaxonFunction("getColo", "colo-iccd"));
		c.registerExtensionFunction(new SaxonFunction("getRuolo", "leta"));
		//c.registerExtensionFunction(new SaxonFunctionGetTypeFromLeader());
		//c.registerExtensionFunction(new SaxonFunctionGetRuolo());		
		//c.registerExtensionFunction(new SaxonFunctionGetGbsLibrary());
		//c.registerExtensionFunction(new SaxonFunctionGetProperty("stprog","getStprog"));
		//c.registerExtensionFunction(new SaxonFunctionGetProperty("agency","getAgency"));
		return factory;
	}
	public Transformer getTransformer(String type, String version){ 
		TransformerFactory factory = getTransformerFactory();
		Transformer transformer = null;
		try {
			String xslt = "/sbnmarc2iccd.xsl";
			if(type.equals(SbnMarcClient.TYPE_AU)){
				if(version!=null && version.startsWith("4"))
					xslt = "/sbnmarc-aut2iccd4.xsl";
				else
					xslt = "/sbnmarc-aut2iccd.xsl";
			}
			transformer = factory.newTransformer( 
				new StreamSource( SaxonHelper.class.getResourceAsStream(xslt)) );
		} catch (TransformerConfigurationException e) {
			logger.error("",e);
		}
		return transformer;
	}
	public Transformer getTransformer2Json(){ 
		TransformerFactory factory = getTransformerFactory();
		Transformer transformer = null;
		try {
			transformer = factory.newTransformer( 
				new StreamSource( SaxonHelper.class.getResourceAsStream("/xml-to-json.xsl") ) );
		} catch (TransformerConfigurationException e) {
			logger.error("",e);
		}
		return transformer;
	}
}
