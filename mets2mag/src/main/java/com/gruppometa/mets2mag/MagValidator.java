package com.gruppometa.mets2mag;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

public class MagValidator {
	static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
	static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";


	public void parseString(String string) throws SAXException, IOException {
		try {
			boolean validate = true;
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setValidating(validate);
			factory.setNamespaceAware(true);
			SAXParser parser;
			parser = factory.newSAXParser();
			if (validate) {
				parser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
						"http://www.w3.org/2001/XMLSchema");
				parser.setProperty(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
				//InputStream xmlFile = MagValidator.class.getResourceAsStream("/mag/metadigit.xsd");
				URL xmlFile = MagValidator.class.getResource("/mag/metadigit.xsd");
				parser.setProperty(JAXP_SCHEMA_SOURCE, xmlFile.toExternalForm());
			}
			// parser.setProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation",
			// "file:///D:/progetti/promedia-git2/pmedia-importer/src/main/resources/legfisco.xsd");

			XMLReader reader = null;
			reader = parser.getXMLReader();
			reader.setErrorHandler(new ErrorHandler() {

					public void error(SAXParseException arg0) throws SAXException {
						throw new SAXException(new Exception("(1) "+arg0.getMessage() + " line " + arg0.getLineNumber()
								+ " column " + arg0.getColumnNumber()));
					}

					public void fatalError(SAXParseException arg0) throws SAXException {
						throw new SAXException(new Exception("(2) "+arg0.getMessage() + " line " + arg0.getLineNumber()
								+ " column " + arg0.getColumnNumber()));
					}

					public void warning(SAXParseException arg0) throws SAXException {
						throw new SAXException(new Exception("(3) "+arg0.getMessage() + " line " + arg0.getLineNumber()
								+ " column " + arg0.getColumnNumber()));
					}
				});
			// validateFile(file);
			reader.parse(new InputSource(new StringReader(string)));
		} catch (ParserConfigurationException e) {
			new SAXException(e);
		}
	}

	public void validate(String metadataAsString) throws SAXException, IOException {
		parseString(metadataAsString);
	}

}
