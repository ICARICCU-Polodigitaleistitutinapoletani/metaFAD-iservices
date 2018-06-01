package com.gruppometa.mets2mag.saxon;


import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.value.StringValue;

import java.util.HashMap;

public class SaxonFunctionGetTypeFromLeader extends ExtensionFunctionDefinition {
	public String getValue(String c) {
		return tipoType.get(c);
	}

	public static HashMap<String, String> tipoType;
	protected static boolean isMetaindice = true;
	static{
		tipoType = new HashMap<String, String>();
		tipoType.put("A", "Testo a stampa");
		tipoType.put("a", "Testo a stampa");
		tipoType.put("books", "Testo a stampa");
		tipoType.put("documents", "Testo a stampa");
		tipoType.put("digitalisations", "Testo a stampa");
		tipoType.put("B", "Manoscritto");
		tipoType.put("b", "Manoscritto");
		tipoType.put("manuscripts", "Manoscritto");
		tipoType.put("C", "Musica a stampa");
		tipoType.put("c", "Musica a stampa");
		tipoType.put("D", "Musica manoscritta");
		tipoType.put("d", "Musica manoscritta");
		tipoType.put("E", "Cartografia a stampa");
		tipoType.put("maps", "Cartografia a stampa");
		tipoType.put("e", "Cartografia a stampa");
		tipoType.put("G", "Materiale video");
		tipoType.put("g", "Materiale video");
		tipoType.put("i", "Registrazione sonora non musicale");
		tipoType.put("J", "Registrazione sonora musicale");
		tipoType.put("j", "Registrazione sonora musicale");
		tipoType.put("K", "Materiale grafico");
		tipoType.put("k", "Materiale grafico");
		tipoType.put("L", "Archivio elettronico");
		tipoType.put("l", "Archivio elettronico");
		tipoType.put("M", "Materiale multimediale");
		tipoType.put("m", "Materiale multimediale");
		tipoType.put("R", "Oggetto a tre dimensioni");
		tipoType.put("r", "Oggetto a tre dimensioni");
		tipoType.put("text", "Testo digitale");
		tipoType.put("F", "Fascicolo");
		tipoType.put("f", "Fascicolo");
		tipoType.put("fascicolo", "Manoscritto");
		tipoType.put("grafica bidimensionale (disegni, dipinti etc.)", "Materiale grafico");
		tipoType.put("grafics", "Materiale grafico");
		tipoType.put("image", isMetaindice?"Musica manoscritta":"Image");
		tipoType.put("libretto", "Libretto per musica");
		tipoType.put("libretti", "Libretto per musica");
		tipoType.put("libro corale", "Musica manoscritta");
		tipoType.put("manifesto-locandina", isMetaindice?"Materiale grafico":"Manifesto-locandina");
		tipoType.put("monografia", "Manoscritto");
		tipoType.put("printed music", "Musica a stampa");
		tipoType.put("registrazione sonora", "Registrazione sonora musicale");
		tipoType.put("serials", "Testo a stampa");
		tipoType.put("stampato", "Testo a stampa");
		tipoType.put("testo digitale", "Text");
		tipoType.put("testo manoscritto", "Manoscritto");
		tipoType.put("altro", "Musica a stampa");
		tipoType.put("archivio elettronico", "Archivio elettronico");
		tipoType.put("biglietto", "Lettera manoscritta");
		tipoType.put("biglietto da visita", "Lettera manoscritta");
		tipoType.put("bozzetto", "Materiale grafico");
		tipoType.put("busta", "Lettera manoscritta");
		tipoType.put("cartografia a stampa", "Cartografia a stampa");
		tipoType.put("cartografia manoscritta", "Cartografia manoscritta");
		tipoType.put("cartolina illustrata", "Lettera manoscritta");
		tipoType.put("cartolina postale", "Lettera manoscritta");
		tipoType.put("copialettere", "Lettera manoscritta");
		tipoType.put("disposizioni sceniche", "Materiale grafico");
		tipoType.put("documenti vari", "Documenti vari");
		tipoType.put("documento grafico", "Materiale grafico");
		tipoType.put("figurino", "Materiale grafico");
		tipoType.put("fotografia", isMetaindice?"Materiale grafico":"Fotografia");
		tipoType.put("grafica bidimensionale (disegni, dipinti etc.)", "Materiale grafico");
		tipoType.put("graphics", "Materiale grafico");
		tipoType.put("lettera", "Lettera manoscritta");
		tipoType.put("lettera manoscritta", "Lettera manoscritta");
		tipoType.put("musica a stampa con correzioni mss.", "Musica a stampa");
		tipoType.put("musica a stampa con correzioni mss.e autografe", "Musica a stampa");
		tipoType.put("pianta scenica", "Materiale grafico");
		tipoType.put("riduzione canto e pianoforte", "Musica a stampa");
		tipoType.put("risorsa elettronica", "Risorsa elettronica");
		tipoType.put("tavola di attrezzeria", "Materiale grafico");
		tipoType.put("telegramma", "Testo a stampa");
		tipoType.put("testo a stampa", "Testo a stampa");

	}
	
	protected String getFunctionName(){
		return "getTypeFromLeader";
	}


	@Override
	public SequenceType getResultType(SequenceType[] suppliedArgumentTypes) {
		return SequenceType.SINGLE_STRING;
	}

	public SequenceType[] getArgumentTypes() {
		return new SequenceType[] { SequenceType.SINGLE_STRING};
	}
	

	@Override 
	 public StructuredQName getFunctionQName() { 
		 return new StructuredQName("ic", "http://internetculturale.it/saxon-extension", getFunctionName()); 
	 }

	@Override
	public ExtensionFunctionCall makeCallExpression() {
		 return new ExtensionFunctionCall() { 
			 @Override public Sequence call(XPathContext context, Sequence[] arguments) throws XPathException {
				 String v0 = ((StringValue)arguments[0].head()).getStringValue(); 
				 String result = getValue(v0); 
				 return StringValue.makeStringValue(result);
			 }
		};		 
	}

}
