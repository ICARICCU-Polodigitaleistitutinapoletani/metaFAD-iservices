package com.gruppometa.mets2mag.saxon;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import com.gruppometa.mets2mag.DefaultFileDescriptionRepository;
import com.gruppometa.mets2mag.FileDescriptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.saxon.Configuration;

public class SaxonHelper {
	protected TransformerFactory factory = null; 
	protected static SaxonHelper instance = new SaxonHelper();
	protected static final Logger logger = LoggerFactory.getLogger(SaxonHelper.class);

	public FileDescriptionRepository getFileDescriptionRepository() {
		return fileDescriptionRepository;
	}

	public void setFileDescriptionRepository(FileDescriptionRepository fileDescriptionRepository) {
		this.fileDescriptionRepository = fileDescriptionRepository;
	}

	protected FileDescriptionRepository fileDescriptionRepository = new DefaultFileDescriptionRepository();

	public static SaxonHelper getInstance(){
		return instance;
	}
	public TransformerFactory getTransformerFactory(){
		if(factory!=null)
			return factory;
		factory = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl",
				SaxonHelper.class.getClassLoader());
		Configuration c = ((net.sf.saxon.TransformerFactoryImpl) factory).getConfiguration();
		c.registerExtensionFunction(new SaxonFunctionGetTypeFromLeader());
		c.registerExtensionFunction(new SaxonFunctionGetRuolo());		
		c.registerExtensionFunction(new SaxonFunctionGetGbsLibrary());
		c.registerExtensionFunction(new SaxonFunctionGetProperty("stprog","getStprog"));
		c.registerExtensionFunction(new SaxonFunctionGetProperty("agency","getAgency"));
		c.registerExtensionFunction(new SaxonFunctionGetPropertyFromJson("getJsonValue"));
		c.registerExtensionFunction(new SaxonFunctionGetFileDescription(fileDescriptionRepository));
		return factory;
	}
	public Transformer getTransformer(){ 
		TransformerFactory factory = getTransformerFactory();
		Transformer transformer = null;
		try {
			transformer = factory.newTransformer( 
				new StreamSource( SaxonHelper.class.getResourceAsStream("/xslt/mets2mag.xsl") ) );
		} catch (TransformerConfigurationException e) {
			logger.error("",e);
		}
		return transformer;
	}
}
