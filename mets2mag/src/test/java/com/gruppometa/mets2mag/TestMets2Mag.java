package com.gruppometa.mets2mag;

import java.io.File;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.junit.Test;

import com.gruppometa.mets2mag.saxon.SaxonHelper;

public class TestMets2Mag {
	
	@Test
	public void testMets2Mag(){
		Transformer transformer = SaxonHelper.getInstance().getTransformer();
		try {
			StringWriter stringWriter = new StringWriter();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			Source streamSource = new StreamSource(TestMets2Mag.class.getResourceAsStream(
					// ATTENZIONE al '/'
					//"/IBNN_BNLP000075080.xml"
					"/IBNN_BNVA001366035.xml" // periodo
					//"/IBNF_CF000039014.xml"
					));
			long now = System.currentTimeMillis();
			transformer.setParameter("baseDir","/home/ingo/temp");
			transformer.setParameter("stprog","Progetto X");
			transformer.setParameter("collection","Collezione Y");
			transformer.setParameter("agency","Agency Z");
			transformer.setParameter("access_rights","0");
			transformer.setParameter("completeness","1");
			transformer.transform(streamSource,
					new StreamResult(stringWriter) 
			);
			//transformer.transform(streamSource,
			//		new StreamResult(stringWriter)
			//);
			System.out.println(stringWriter.toString());
			MagValidator magValidator = new MagValidator();
			magValidator.validate(stringWriter.toString());
			//System.out.println(transformer.getClass().getName());
			//System.out.println(System.currentTimeMillis()-now);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
