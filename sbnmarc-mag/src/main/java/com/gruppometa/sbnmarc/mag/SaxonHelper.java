package com.gruppometa.sbnmarc.mag;

import net.sf.saxon.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

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
		/**
		 * vocabulari
		 */
		c.registerExtensionFunction(new SaxonFunction("getLanguage", "language"));
		SaxonFunction saxonFunction =  new SaxonFunction("getTipoDocumento", "geun");
		saxonFunction.setValue("c","musica a stampa");
		saxonFunction.setValue("d","musica manoscritta");
		saxonFunction.setValue("g","registrazione sonora non musicale");
		saxonFunction.setValue("i","registrazione sonora di musica");
		saxonFunction.setValue("j","materiale video");
		c.registerExtensionFunction(saxonFunction);
		c.registerExtensionFunction(new SaxonFunction("getRuolo", "leta"));
		c.registerExtensionFunction(new SaxonFunction("getSpecificaMateriale", "magr-mag-parts2e"));
		c.registerExtensionFunction(new SaxonFunction("getTecd", "tecd-mag-parts2e"));
		c.registerExtensionFunction(new SaxonFunction("getTecs", "tecs-mag-parts2e"));
		c.registerExtensionFunction(new SaxonFunction("getDesf", "desf-mag-parts2e"));
		c.registerExtensionFunction(new SaxonFunction("getOrga", "orga", false));
		c.registerExtensionFunction(new SaxonFunction("getFomu", "fomu"));
		/**
		 * counter funzione
		 */
		c.registerExtensionFunction(new CounterFunction("getCounter"));
		c.registerExtensionFunction(new CounterFunction("getCounter2"));

		/**
		 * per ordinamento
		 */
		c.registerExtensionFunction(new OrderDescriptionFunction("getOrderDescription"));
		c.registerExtensionFunction(new OrderDescriptionFunction("getOrderAnticoDescription"));
		c.registerExtensionFunction(new OrderDescriptionFunction("getOrderGraficoDescription"));
		c.registerExtensionFunction(new OrderDescriptionFunction("getOrderTitoloUniforme"));
		c.registerExtensionFunction(new OrderDescriptionFunction("getOrderVideoDescription"));

		c.registerExtensionFunction(new SaxonTextNormFunction("format"));
		c.registerExtensionFunction(new SaxonTextNormOneSeparator("formatPunto",". "));
		c.registerExtensionFunction(new SaxonTextNormOneSeparator("formatSpazio"," "));

		return factory;
	}
	public Transformer getTransformer(String type, String version){ 
		TransformerFactory factory = getTransformerFactory();
		Transformer transformer = null;
		try {
			String xslt = "/sbnmarc2mag.xsl";
			if(type.equals("clean"))
				xslt = "/cleanVoidTags.xsl";
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
