package com.gruppometa.unimarc.profile;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.marc4j.marc.*;
import org.marc4j.marc.impl.SubfieldImpl;

import com.gruppometa.unimarc.handlers.Handler;
import com.gruppometa.unimarc.mapping.MappingDefinition;
import com.gruppometa.unimarc.maps.AnagraficaBiblioteche;
import com.gruppometa.unimarc.maps.Genere;
import com.gruppometa.unimarc.object.Field;
import com.gruppometa.unimarc.object.LabelPairGroup;
import com.gruppometa.unimarc.object.LabelValuePair;
import com.gruppometa.unimarc.object.OutItem;
import com.gruppometa.unimarc.util.StringUtil;


public class SbnProfile implements Profile {
	protected ParentCache parentCache; 
	public ParentCache getParentCache() {
		return parentCache;
	}

	public void setParentCache(ParentCache parentCache) {
		this.parentCache = parentCache;
	}

	protected static Log logger = LogFactory.getLog(SbnProfile.class);
	protected boolean isEnabledLibroAntico = false;
	protected IssuedAndLanguageNormalizer normalizer = new IssuedAndLanguageNormalizer();
	protected String filename;
	protected boolean forFe;

	public boolean isForFe() {
		return forFe;
	}

	public void setForFe(boolean forFe) {
		this.forFe = forFe;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public void notifyFullFilename(String filename){
		
	}
	
	protected HashMap<String, MappingDefinition> defsMap = new HashMap<String, MappingDefinition>();

	/***
	 *
	 * @param destiantion
	 * @return
	 *
	 * questo metodo viene chiamato anche con il nome del campo solr
	 * 116_specifica_del_materiale_ss
	 */
	public MappingDefinition getDefinition(String destiantion){
		if(defsMap.get(destiantion)!=null || defsMap.size()>0)
			return defsMap.get(destiantion);
		for (int i = 0; i < defs.length; i++) {
			MappingDefinition def = defs[i];
			defsMap.put(def.getDestination(), def);
			if(def.isFacets()){
				for (int j = 0; j < def.getSubDefs().size(); j++) {
					def.getSubDefs().get(j).setFacet("");
					def.getSubDefs().get(j).setSubfield(true);
					defsMap.put(def.getMarcField()+"_"+def.getSubDefs().get(j).getDestination(), def.getSubDefs().get(j));
					defsMap.put(def.getMarcField()+"_"+ filter4Solr(def.getSubDefs().get(j).getDestination(),"_s"), def.getSubDefs().get(j));
					defsMap.put(def.getMarcField()+"_"+ filter4Solr(def.getSubDefs().get(j).getDestination(),"_ss"), def.getSubDefs().get(j));
				}
			}
		}
		return defsMap.get(destiantion);
	}

	protected String filter4Solr(String label, String suffix){
		if(label==null)
			return null;
		return label.toLowerCase().replace(' ','_')+suffix;
	}

	protected HashMap<String, MappingDefinition> defsMap2 = new HashMap<String, MappingDefinition>();
	public MappingDefinition getDefinitionFromMarcField(String marcField){
		if(defsMap2.get(marcField)!=null || defsMap2.size()>0)
			return defsMap2.get(marcField);
		for (int i = 0; i < defs.length; i++) {
			defsMap2.put(defs[i].getMarcField(), defs[i]);
		}
		return defsMap2.get(marcField);
	}

	public static Map<String, String> tipoLivello = new HashMap<String, String>();	
	static{
		tipoLivello.put("1" , "Manoscritto" );
		tipoLivello.put("2" , "Spoglio Manoscritto");
		tipoLivello.put("3" , "Codice Liturgico");
		tipoLivello.put("C" , "Raccolta dell'istituzione"/*"Collezione"*/); // 29-06-2010: diventa raccolta
		tipoLivello.put("c" , "Raccolta dell'istituzione"/*"Collezione"*/); // rimodificato 23/07/2010
		tipoLivello.put("M" , "Monografia");
		tipoLivello.put("m" , "Monografia");
		tipoLivello.put("N" , "Titolo analitico");
		tipoLivello.put("n" , "Titolo analitico");
		tipoLivello.put("S" , "Periodico");
		tipoLivello.put("s" , "Periodico");
		tipoLivello.put("W" , "Volume senza titolo significativo");
		tipoLivello.put("w" , "Volume senza titolo significativo");
		tipoLivello.put("A" , "Spoglio");
		tipoLivello.put("a" , "Spoglio");
		
		// per MAG
		tipoLivello.put("d" , "Unità documentaria" /* modifica 23/07/2010 "Documento d'archivio" */);
		tipoLivello.put("D" , "Unità documentaria" /* modifica 23/07/2010 "Documento d'archivio" */);
		tipoLivello.put("f" , "Unità archivistica");/* aggiunta 23/07/2010 */ 
		tipoLivello.put("F" , "Unità archivistica");
	}
	public static Map<String, String> tipoType = new HashMap<String, String>();
	static{
		tipoType.put( "A" , "Testo a stampa");
		tipoType.put( "a" ,  "Testo a stampa");
		tipoType.put( "books" ,  "Testo a stampa");
		tipoType.put( "B" ,  "Manoscritto");
		tipoType.put( "b" ,  "Manoscritto");
		tipoType.put( "C" ,  "Musica a stampa");
		tipoType.put( "c" ,  "Musica a stampa");
		tipoType.put( "D" ,  "Musica manoscritta");
		tipoType.put( "d" ,  "Musica manoscritta");
		tipoType.put( "E" ,  "Cartografia a stampa");// 28-06-2010: "Documento cartografico");
		tipoType.put( "maps" ,  "Cartografia a stampa");
		tipoType.put( "e" ,  "Cartografia a stampa");// 28-06-2010: "Documento cartografico");
		tipoType.put( "G" ,  "Materiale video");// 28-06-2010:"Documento da proiettare o video");
		tipoType.put( "g" ,  "Materiale video");// 28-06-2010:"Documento da proiettare o video");
		tipoType.put( "i" ,  "Registrazione sonora non musicale");
		tipoType.put( "J" ,  "Registrazione sonora musicale"); // 28-06-2010
		tipoType.put( "j" ,  "Registrazione sonora musicale"); // 28-06-2010 
		tipoType.put( "K" ,  "Materiale grafico");// 28-06-2010:"Documento grafico");
		tipoType.put( "k" ,  "Materiale grafico");// 28-06-2010:"Documento grafico");
		tipoType.put( "L" ,  "Archivio elettronico");
		tipoType.put( "l" ,  "Archivio elettronico");
		tipoType.put( "M" ,   "Materiale multimediale");// 28-06-2010:"Documento multimediale");
		tipoType.put( "m" ,   "Materiale multimediale");// 28-06-2010:"Documento multimediale");
		tipoType.put( "R" ,  "Oggetto a tre dimensioni");// 28-06-2010: "Oggetto tridimensionale");
		tipoType.put( "r" ,  "Oggetto a tre dimensioni");// 28-06-2010:"Oggetto tridimensionale");
		tipoType.put( "text" ,  "Testo digitale");
		// 28-06-2010
		tipoType.put( "F" ,  "Fascicolo");
		tipoType.put( "f" ,  "Fascicolo");
	}
	protected 	MappingDefinition[] defs = null;
	
	public void init(){
		defs = new MappingDefinition[] {
		new MappingDefinition("200", new String[] { "a","e", "c" }, "Title"),
		// 28-06-2010 nonn in description ma in metaindice:titleArea
		//new MappingDefinition("200", ALL, "Description"),
		new MappingDefinition("200", ALL, "TitleArea"),
		// fine modifica
		//new MappingDefinition("210", ALL, "Description"),
		//new MappingDefinition("4xx:001+200", new String[] { "a","e","c" }, "Alternative"),
		new MappingDefinition("500", new String[] { "a","3" }, "Alternative"),
		new MappingDefinition("510", new String[] { "a","9" }, "Alternative"),
		new MappingDefinition("517", new String[] { "a","9" }, "Alternative"),
		new MappingDefinition("700", ALL, "Creator"),
		new MappingDefinition("701", ALL, "Creator"),
		new MappingDefinition("710", ALL, "Creator"),
		new MappingDefinition("711", ALL, "Creator"),
		new MappingDefinition("702", ALL, "Contributor"),
		// non metto gli editori in Contributor
		//new MappingDefinition("712", ALL, "Contributor"),
		
		// questi sono i rinvii 21-06-2010
		//new MappingDefinition("790", new String[] { "a","b","c","d","e","f"}, "Contributor"),
		//new MappingDefinition("791", new String[] { "a","b","c","d","e","f"}, "Contributor"),
		// lo facciamo dopo
		
		new MappingDefinition("101", A, "Language"),
		new MappingDefinition("210", new String[] { "a","e"}, "Publisher","Place"),
		new MappingDefinition("620", new String[] { "a","e"}, "Publisher","Place"),		
		/**
		 * 05-02-10: per la faccetta Luogo....
		 */
		new MappingDefinition("620", new String[] { "d"}, "Publisher","PlaceString"),				
		new MappingDefinition("210", new String[] { "a","e"}, "Publisher","PlaceString",false,true),
		// fine
		new MappingDefinition("210", new String[] { "c","g"}, "Publisher"),
		// la faccetta, solo se non c'era un 712
		new MappingDefinition("210", new String[] { "c","g"}, "Publisher","string",false,true),
		// fine
		new MappingDefinition("010", A, "Identifier","ISBN"),
		new MappingDefinition("011", A, "Identifier","ISSN"),
		new MappingDefinition("012", A, "Identifier","Impronta"),
		new MappingDefinition("013", A, "Identifier","ISMN"),														
		new MappingDefinition("606", new String[]{"a","x"}, "Subject"),
		// questo è la codifica dewey: non metterla in subject generale.
		// new MappingDefinition("676", new String[]{"a","1"}, "Subject"),
		new MappingDefinition("676", new String[]{"a"}, "Subject","DeweyNumber"),
		// non sta su c?, bug era 1
		new MappingDefinition("676", new String[]{"c"}, "Subject","Dewey"),
		
		new MappingDefinition("327", new String[]{"a"}, "Description","hidden"),
		new MappingDefinition("330", new String[]{"a"}, "Description","hidden"),
		
		new MappingDefinition("899", new String[] { "a","d", "3", "c", "1" }, "Location") 
		};
	}

	protected void makeGenereNode(OutItem desc, String data) throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		if(data!=null && !data.trim().equals("|"))
			makeNode(desc, "Subject", Genere.getGenereFromCodice(data),"genere");
	}
	
	HashMap<Character, String> types = null;
	HashMap<Character, String> levels = null;

	protected void makeMy(OutItem desc, Record record) throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		makeNode(desc, record, "200",
				new String[] { "a", "c", "e" }, "Title");
		makeNode(desc, record, "517", new String[] { "a" }, "Title");
		makeNode(desc, record, "500", new String[] { "a" }, "Title");
		makeNode(desc, record, "530", new String[] { "a" }, "Title");

		makeNode(desc, record, "200", new String[] { "f" },
				"Creator");
		makeNode(desc, record, "702", new String[] { "a", "b" },"Creator");
		makeNode(desc, record, "700", new String[] { "a", "b" },"Creator");

		makeNode(desc, record, "606", ALL, "Subject");
		makeNode(desc, record, "676", ALL, "Subject");
		makeNode(desc, record, "675", ALL, "Subject");
		makeNode(desc, record, "680", ALL, "Subject");
		makeNode(desc, record, "686", ALL, "Subject");

		makeNode(desc, record, "300", ALL, "Description");
		makeNode(desc, record, "330", A, "Description");

		makeNode(desc, record, "210", new String[] { "c" },
				"Publisher");
		makeNode(desc, record, "712", new String[] { "a" },
				"Publisher");

		makeNode(desc, record, "200", new String[] { "g" },
				"Contributor");
		makeNode(desc, record, "702", new String[] { "a" },
				"Contributor");
		makeNode(desc, record, "712", new String[] { "a" },
				"Contributor");

		makeNode(desc, record, "210", new String[] { "d" }, "Date");
		makeNode(desc, record, "210", new String[] { "h" }, "Date");

		makeNode(desc, record, "135", ALL, "Type");
		makeNode(desc, record, "230", ALL, "Type");

		makeNode(desc, record, "215", new String[] { "ALL" },
				"Format");
		makeNode(desc, record, "336", new String[] { "a" },
				"Format");
		makeNode(desc, record, "856", new String[] { "q" },
				"Format");

		makeNode(desc, record, "001", A, "Identifier");
		makeNode(desc, record, "010", A, "Identifier");
		makeNode(desc, record, "011", A, "Identifier");
		makeNode(desc, record, "012", A, "Identifier");
		makeNode(desc, record, "013", A, "Identifier");
		makeNode(desc, record, "020", A, "Identifier");
		makeNode(desc, record, "071", A, "Identifier");
		makeNode(desc, record, "856", new String[] { "g" },
				"Identifier");
		makeNode(desc, record, "856", new String[] { "u" },
				"Identifier");

		makeNode(desc, record, "324", ALL, "Source");

		makeNode(desc, record, "101", A, "Language");

		makeNode(desc, record, "300", ALL, "Relation");
		// makeNode(desc, record, "4xx", ALL, "Relation");
		// makeNode(desc, record, "5xx", ALL, "Relation");

		makeNode(desc, record, "102", A, "Coverage");
		makeNode(desc, record, "102", B, "Coverage");
		makeNode(desc, record, "606", A, "Coverage");
		makeNode(desc, record, "620", ALL, "Coverage");

		makeNode(desc, record, "856", new String[] { "u" },
				"Rights");

	}
	protected String getString(List<String> list, String sep){
		String out = "";
		for (Iterator<String> iterator = list.iterator(); iterator
			.hasNext();) {
			String dataField2 = (String) iterator.next();
			if(out.length()>0)
				out+=sep;
			out += dataField2;
		}
		return out;
	}
	protected boolean isLibroAntico(OutItem desc){
		return desc.getAbout().length()>10 && desc.getAbout().charAt(11)=='E';
	}
	protected boolean isValidDate(String data){
		if(data==null)
			return false;
		return (!data.trim().equals("0") && !data.equals("9999"));
	}
	protected String getLevelFromLeader(char c) {
		return tipoLivello.get(""+c);
		/*
		if (levels == null) {
			levels = new HashMap<Character, String>();
			levels.put('a', "Analitico");
			levels.put('m', "Monografia");
			levels.put('s', "Seriale");
			levels.put('c', "Collana");
		}
		return levels.get(c);
		*/
	}

	protected String getTypeFromLeader(char c) {
		return tipoType.get(""+c);
		/*
		if (types == null) {
			types = new HashMap<Character, String>();
			types.put('a', "Materiale a stampa");
			types.put('b', "Materiale manoscritto");
			types.put('c', "Spartiti musicali a stampa");
			types.put('d', "Spartiti musicali manoscritti");
			types.put('e', "Materiale cartografico a stampa");
			types.put('f', "Materiale cartografico manoscritto");
			types.put('g', "Video");
			types.put('i', "Audio registrazioni, esecuzioni non musicali");
			types.put('j', "Audio registrazioni, esecuzioni musicali");
			types.put('k', "Grafica bidimensionale (disegni, dipinti etc.)");
			types.put('l', "Computer media");
			types.put('m', "Multimedia");
			types.put('r', "Opere d'arte tridimensionali ");
		}
		return types.get(c);
		*/
	}

	public boolean isValidLocalizzazione(String value){
		return true;
	}
	
	public boolean makeLeader(OutItem desc, Leader leader) {
		char c = leader.toString().charAt(6);
		Field f = null;
		String str;
		if ((str = getTypeFromLeader(c)) != null) {
			f = addField(desc,"type", "tipo");
			if(f!=null)
				f.setTextValue(str);
			/*
			SimpleLiteral lit = DublinCoreParser.addNewSimpleLiterals(desc,
					"Type");
			XmlCursor cu = lit.newCursor();
			cu.setTextValue(str);			
			cu.setAttributeText(new QName("type"), "tipo");
			cu.dispose();
			*/
		}
		c = leader.toString().charAt(7);
		if ((str = getLevelFromLeader(c)) != null) {
			f = addField(desc,"type", "livello");
			if(f!=null)
				f.setTextValue(str);
			/*
			SimpleLiteral lit = DublinCoreParser.addNewSimpleLiterals(desc,
					"Type");
			XmlCursor cu = lit.newCursor();
			cu.setTextValue(str);
			cu.setAttributeText(new QName("type"), "livello");
			cu.dispose();
			*/
		}
		return true;
	}

	protected Field addField(OutItem desc, String name, String qualifier){
		return desc.addNewField(name, qualifier);
	}
	
	protected void makeId(OutItem desc, Record record, String fieldName,
			String[] subfieldCodes) {
		ArrayList<String> data = getValues(record, fieldName, subfieldCodes,-1,-1);
		if (data != null && data.size() > 0){
			desc.setAbout(data.get(0));
			//if(getDescSource()!=null && getDescSource().equals("edit16"))
			//	desc.setAbout("edit16:"+desc.getAbout());
			try {
				makeNode(desc, "Identifier",data.get(0), "bid");
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}
	
	protected String getBid(Record record, String fieldName,
			String[] subfieldCodes) {
		ArrayList<String> data = getValues(record, fieldName, subfieldCodes,-1,-1);
		if (data != null && data.size() > 0){
			return data.get(0);
		}
		return null;
	}
	
	public String getMapVersion(){
		return "1.0";
	}
	protected String getValueClosure(String fieldname, String code, String[] subfieldCodes,
			boolean hasNext){
		if(fieldname.equals("Publisher")||fieldname.equals("210")){			
			// bug: 01-06-2010: è g non h
			//if(code.equals("h"))
			if((code.equals("h")||code.equals("g")) && subfieldCodes!=null 
					&&	subfieldCodes.length>1 
					&& (code.equals(subfieldCodes[subfieldCodes.length-1])||!hasNext)
					) 					
			//if(code.equals("g") && Arrays.equals(subfieldCodes, ALL))
				return ")";	
		}
		return "";
	}
	protected String getValueSeparator(String fieldname){
		return getValueSeparator(fieldname,null,null, new String[]{});
	}
	protected String getValueSeparator(String fieldname, String code, String data, String[] subFieldsCodes) {
		MappingDefinition def = getDefinitionFromMarcField(fieldname);
		if(def!=null && def.getSeparator()!=null){
			String[] p = def.getSeparator().split("\\|");
			if(subFieldsCodes!=null && subFieldsCodes.length>0 && p.length==subFieldsCodes.length){
				int i = 0;
				for (; i < subFieldsCodes.length; i++) {
					if(subFieldsCodes[i].equals(code))
						break;
				}
				if(p.length > i)
				return (p[i]).equals("x")?"":p[i];
			}
			return def.getSeparator();
		}
			
		if (fieldname.equals("Title"))
			return ": ";
		else if (fieldname.equals("Description")||fieldname.equals("200")){
			if(code!=null){
				// titolo complemento
				if(code.equals("e"))
					return " : ";
				// prima resp.
				if(code.equals("f")){
					// tra le resp.
					if(data!=null && data.indexOf(" / ")!=-1
							&& data.lastIndexOf(" / ") > data.indexOf(" . "))
						return " ; ";
					else
						return " / ";	
				}
				// secondo titolo 
				if(code.equals("c"))
					return " . ";
			}
			// g?
			return " ; ";
		}
		// nuovo 23-04-2014
		else if (isPartOfField(fieldname)){
			if(code!=null){
				if(code.equals("b"))
					return "";
				if(code.equals("e"))
					return " : ";
				// prima resp.
				if(code.equals("f")){
					// tra le resp.
					if(data!=null && data.indexOf(" / ")!=-1
							&& data.lastIndexOf(" / ") > data.indexOf(" . "))
						return " | ";
					else
						return " / ";	
				}
				if(code.equals("g"))
					return " ; ";
				// secondo titolo 
				if(code.equals("c"))
					return " . ";
				if(code.equals("v"))
					return " ; ";
			}
			// g?
			return " ; ";
		}
		/**
		 * 24-11-2015: per collezioni
		 */
		else if(fieldname.equals("collezione")||fieldname.equals("410")||fieldname.equals("410")){
			if(code.equals("v"))
				return " ; ";	
			else
				return " ";
		}
		else if (fieldname.equals("Publisher")||fieldname.equals("210")){
			if(code!=null){
				// tra luoghi
				if(code.equals("a"))
					return " ; ";
				// nome editore
				if(code.equals("c"))
					return " : ";
				// prima della data
				if(code.equals("d"))
					return ", ";	
				//  va chiuso... dopo... e non aperto sempre
				if(code.equals("e") //&& Arrays.equals(subFieldsCodes, ALL)
						)
					return " (";	
				if(code.equals("e"))
					return " ";
				if(code.equals("g"))
					return " : ";	
				if(code.equals("h"))
					return ", ";	
			}
			return " / ";
		}
		else if (fieldname.equals("Formato")||fieldname.equals("215") && code.equals("e")) {
				return " + ";
		}
		else if (fieldname.equals("Marca")||fieldname.equals("921")){
			if(code.equals("c"))
				return " (";			
			if(code.equals("d"))
				return ") ";	
			return " ";
		}
		else if (fieldname.equals("Location")||fieldname.equals("899"))
			return " - ";
		else if (fieldname.startsWith("7")){
			if(code.equals("b"))
				return "";			
			if(code.equals("f"))
				return "";	
			return " ";
		}
		else			
			return getDefaultSeparator();
	}

	public String getDefaultSeparator(){
		return " ";
	}
	public boolean isPartOfField(String fieldname){
		return fieldname.equals("IsPartOf")||fieldname.equals("461")||fieldname.equals("462");
	}
	
	static final char c = 27; // bug: 01-06-2010 anche il carattere che segue deve essere tolto..
	static final char c3 = 136;	
	static final char c4 = 137;
	static final char c5 = 152; // marker 04-11-2014
	static final char c6 = 156; // marker 04-11-2014
	static final char c2 = 0xc2; // bug: 28-01-2014 anche il carattere che segue deve essere tolto..
	
	static final Pattern p = Pattern.compile("("+c+".)|("+c2+".)|("+c3+")|("+c4+")|("+c5+")|("+c6+")");
	
	protected String clear27(String value) { 
		
		if (value == null)
			return null;
		else{
			Matcher m = p.matcher(value);
			//String ret = m.replaceAll("");
			//if(!ret.equals(value))
			//	System.out.println(value+"=>"+ret);
			return m.replaceAll("");
		}
			//return value.replaceAll("" + c + ".", "").replaceAll("" + c2 + ".",
			//		"");
	}

	
	public ArrayList<String> getValues(Record record, String fieldName,
									   String[] subfieldCodes) {
		return getValues(record, fieldName, subfieldCodes,-1,-1,-1);
	}
	protected ArrayList<String> getValues(Record record, String fieldName,
			String[] subfieldCodes, int posInit, int posEnd) {
		return getValues(record, fieldName, subfieldCodes,posInit, posEnd,-1);
	}
	protected ArrayList<String> getValues(Record record, String fieldName,
			String[] subfieldCodes, int posInit, int posEnd, int pos) {
		@SuppressWarnings("unchecked")
		List<VariableField> dataFields = record.getVariableFields(fieldName);
		StringBuffer dataS = null;
		Arrays.sort(subfieldCodes);
		ArrayList<String> values = new ArrayList<String>();
		int i = 0;
		for (@SuppressWarnings("rawtypes")
		Iterator iterator = dataFields.iterator(); iterator.hasNext();) {
			Object object = iterator.next();
			if (object instanceof DataField) {
				dataS = null;
				DataField field = (DataField) object;
				dataS = makeValues(fieldName, subfieldCodes, dataS, values,
						field, posInit, posEnd);
				if (dataS != null && (pos==-1 || pos==i))
					values.add(dataS.toString());
				i++;
			} else if (object instanceof ControlField) {
				if(pos==-1 || pos==i)
					values.add(((ControlField) object).getData());
				i++;
			}

		}
		return values;
	}

	protected int getValuesCount(Record record, String fieldName) {
		@SuppressWarnings("unchecked")
		List<VariableField> dataFields = record.getVariableFields(fieldName);
		int i = 0;
		for (@SuppressWarnings("rawtypes")
		Iterator iterator = dataFields.iterator(); iterator.hasNext();) {
			Object object = iterator.next();
			if (object instanceof DataField) {
				i++;
			} else if (object instanceof ControlField) {
				i++;
			}

		}
		return i;
	}

	private StringBuffer makeValues(String fieldName, String[] subfieldCodes,
			StringBuffer dataS, ArrayList<String> values, DataField field) {
		return makeValues(fieldName, subfieldCodes, dataS, values, field,-1,-1);
	}
	
	@SuppressWarnings("rawtypes")
	private StringBuffer makeValues(String fieldName, String[] subfieldCodes,
			StringBuffer dataS, ArrayList<String> values, DataField field, int posInit, int posEnd) {
		List subfields = field.getSubfields();
		Iterator it = subfields.iterator();
		String id = null;		
		while (it.hasNext()) {
			Subfield subfield = (Subfield) it.next();
			String code = "" + subfield.getCode();
			String subFieldName = null;
			if(code.equals("1") && subfield.getData().length()>2){
				subFieldName = subfield.getData().substring(0,3);
				//System.out.println(fieldName+":"+subFieldName);
			}
			if(subfield.getData().startsWith("001"))
				id = subfield.getData().substring(3);
			/**
			 * MID della marca editoriale
			 */
			if(code.equals("a") && fieldName.equals("921")){
				id = subfield.getData();
			}
			/**
			 * titolo uniforme
			 */
			if(code.equals("3") && fieldName.equals("500")){
				id = subfield.getData();
			}
			/**
			 * cut nuovo 24-04-2015: da 700, 702 o 712 non si considera piu' il campo
			 */
			if((isPartOfField(fieldName)) 
					&& ("700".equals(subFieldName) ||
							"702".equals(subFieldName) ||"712".equals(subFieldName) ))
				return dataS;
			if (Arrays.binarySearch(subfieldCodes, "ALL") >= 0
					|| Arrays.binarySearch(subfieldCodes, code) >= 0) {
				String data = clear27(subfield.getData());
				if (dataS == null)
					dataS = new StringBuffer();
				if (dataS.length() > 0)
					dataS.append(getValueSeparator(fieldName,code,dataS.toString(),subfieldCodes));
				dataS.append(makeData(fieldName,code,data,posInit, posEnd));
				dataS.append(getValueClosure(fieldName, code, subfieldCodes,it.hasNext()));
				if(id!=null){
					dataS.append("[BID:"+id+"]");
					id=null;
				}
				if(isMultiValueField(fieldName)){
					values.add(dataS.toString());
					dataS = null;
				}
			}
		}
		return dataS;
	}
	protected boolean isMultiValueField(String name){
		return name!=null && (name.equalsIgnoreCase("language")||name.equalsIgnoreCase("101"));
	}
	
	protected String[] vidFields = new String[]{"700","701","702","710","711","712"};
	protected String[] getVidFields(){
		return vidFields; 
	}
	protected String makeData(String fieldName, String code, String data, int posInit, int posEnd) {
		String[] ff = getVidFields();
		if(posInit!=-1 && posInit>=0 && posInit<data.length() && data.length()>0)
			data = data.substring(posInit,(posEnd >0 && posEnd<=data.length())?posEnd:data.length()).trim();
		// bug POLODEBUG-605
		else if(posInit>data.length())
			data = null;
		if(Arrays.binarySearch(ff, fieldName)!=-1 && code.equals("3")){
			data = "[VID: "+data+"]";
		}
		if(Arrays.binarySearch(ff, fieldName)!=-1 && code.equals("4")){
			data = "[ROLE: "+data+"]";
		}		
		/**
		 * pipe | non vale niente
		 */
		if(data!=null && data.equals("|"))
			return "";
		return data;
	}
	public static final String MULTIVALUE_SEPARATOR = " | ";
	protected AnagraficaBiblioteche anagraficaBiblioteche = new AnagraficaBiblioteche();
	protected HashMap<String,Handler> handlers = new HashMap<String, Handler>();
  	@SuppressWarnings("rawtypes")
	protected void makeNode(OutItem desc, Record record, String fieldName,
			String[] subfieldCodes, String destinationNode, String qualifier, int posInit, int posEnd, String handler,
			List<MappingDefinition> subDefs, boolean facets, MappingDefinition definition)
			throws IllegalArgumentException, SecurityException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		List<String> data = null;
		/**
		 * con subdefs
		 */
		if (subDefs != null) {
			List<String> groups = new ArrayList<String>();
			for (MappingDefinition def : subDefs) {
				if (def.getLabelGroup() != null && !groups.contains(def.getLabelGroup()))
					groups.add(def.getLabelGroup());
			}
			int fieldCount = getValuesCount(record, fieldName);
			for (int i = 0; i < fieldCount; i++) {
				List<LabelValuePair> pairs = new ArrayList<LabelValuePair>();
				for (String group : groups) {
					makeGroup(subDefs, record, group, fieldName, destinationNode, pairs, i);
				}
				for (MappingDefinition def : subDefs) {
					if (def.getLabelGroup() != null || (isForFe() && def.isExcludeInFe()))
						continue;
					data = getValues(record, fieldName, def.getMarcSections(), def.getPosInit(), def.getPosEnd(), i);
					for (String string : data) {
						if (string.trim().length() > 0) {
							String key = destinationNode + "::" + def.getDestination();
							String v = normalizer.getMapValue(key, string);
							v = (v != null) ? v : string;
							if (v != null)
								v = v.trim();
							if(def.isCutZeros())
								v = cutZeros(v);
							boolean added = false;
							if(!isForFe() || !normalizer.isMapValue(key) || !v.equals(string)) {
								for (LabelValuePair labelValuePair : pairs) {
									if (labelValuePair.getLabel().equals(def.getDestination())) {
										labelValuePair.setValue(labelValuePair.getValue() + MULTIVALUE_SEPARATOR + v);
										added = true;
										break;
									}
								}
								if (!added) {
									LabelValuePair p = new LabelValuePair(def.getDestination(), v, def.getOrder());
									p.setVocabulary(def.getVocabulary());
									pairs.add(p);
								}
							}
						}
					}
				}
				if (pairs.size() > 0) {
					Collections.sort(pairs, new Comparator<LabelValuePair>() {
						public int compare(LabelValuePair o1, LabelValuePair o2) {
							return Double.compare(o1.getOrder(), o2.getOrder());
						}
					});
					Field f = addField(desc, destinationNode, qualifier);
					f.setLabelValuePairs(pairs);
					/**
					 * crea le faccette
					 */
					if (facets) {
						for (LabelValuePair labelValuePair : pairs) {
							String name = fieldName + "_" + labelValuePair.getLabel();
							String ps[] = labelValuePair.getValue().split("(" + java.util.regex.Pattern.quote(MULTIVALUE_SEPARATOR) + ")");
							for (int j = 0; j < ps.length; j++) {
								Field f2 = addField(desc, name, null);
								f2.setTextValue(ps[j]);
							}
						}
					}
				}
			}
		}
		/**
		 * senza subdefs
		 */
		else {
			if (handler != null) {
				Handler h = getHandler(handler);
				if (h != null)
					data = h.getValues(record);
			}
			else {
				/**
				 * filter se non coincide ind
				 */
				if (definition != null && definition.getInd() != null) {
					boolean found = false;
					for (Iterator iterator = record.getVariableFields(fieldName).iterator(); iterator.hasNext(); ) {
						DataField dataField = (DataField) iterator.next();
						if (("" + dataField.getIndicator1() + "" + dataField.getIndicator2()).equals(definition.getInd())) {
							found = true;
							break;
						}
					}
					if (!found)
						return;
				}

				/**
				 * filtro peruna condizione
				 */
				if (definition != null && definition.getCondMarcSection() != null && definition.getCondValue() != null) {
					data = getValues(record, fieldName, new String[]{definition.getCondMarcSection()});
					boolean found = false;
					for (Iterator iterator = data.iterator(); iterator.hasNext(); ) {
						String string = (String) iterator.next();
						String conditionValue = definition.getCondValue().trim().toLowerCase();
						String conditionValue2 = null;
						if(definition.getCondValue2()!=null)
							conditionValue2 = definition.getCondValue2().trim().toLowerCase();
						boolean equals = true;
						boolean equals2 = true;
						if(conditionValue2!=null){
							if(conditionValue2.startsWith("!")){
								equals2 = false;
								conditionValue2 = conditionValue2.substring(1);
							}
						}
						if(conditionValue.startsWith("!")) {
							equals = false;
							conditionValue = conditionValue.substring(1);
						}
						/**
						 * in OR
						 */
						if (equals && string.trim().toLowerCase().equals(conditionValue)
								|| (conditionValue2!=null && (equals2 && string.trim().toLowerCase().equals(conditionValue2)))
							) {
							found = true;
							break;
						}
						/**
						 * in AND NOT
						 */
						if (!equals && !string.trim().toLowerCase().equals(conditionValue)
								&& (conditionValue2==null || (!equals2 && !string.trim().toLowerCase().equals(conditionValue2)))
								) {
							found = false;
							break;
						}
					}
					if (!found) {
						/**
						 * POLDEBUG-580 condizione negativa
						 */
						if(data.size()==0
								&& (definition.getCondValue()==null|| definition.getCondValue().startsWith("!"))
								&& (definition.getCondValue2()==null|| definition.getCondValue2().startsWith("!"))
								)
							; //ok
						else
							return;
					}
				}
				data = getValues(record, fieldName, subfieldCodes, posInit, posEnd);
			}
		}
		if(data!=null) {
			if(fieldName.equals("977") && destinationNode.equalsIgnoreCase("localizzazione")
					&& data.size()==1){
				String[] values = data.get(0).split("\\|");
				List<String> vals = new ArrayList<>();
				Map<String, String> map = normalizer.getMap("biblioteche", false, true);
				for (int i = 0; i < values.length; i++) {
					String mappedValue = map.get(values[i].trim().toLowerCase());
					if(mappedValue!=null) {
						vals.add(normalizeLocation(mappedValue));
					}
				}
				data = vals;
			}
			for (Iterator iterator = data.iterator(); iterator.hasNext(); ) {
				String string = (String) iterator.next();
				// la prima stringa è il codice della biblioteca nei file di aggiornamento.
				if (destinationNode.equalsIgnoreCase("location") || destinationNode.equalsIgnoreCase("localizzazione")) {
					int pos = string.indexOf(" ");
					if (pos != -1) {
						String val = anagraficaBiblioteche.getName(string.substring(0, pos), true);
						if (val != null)
							string = val;
					}
					if (!isValidLocalizzazione(string))
						string = null;
					string = normalizeLocation(string);
				}
				makeNode(desc, destinationNode, string, qualifier);
			}
		}
	}

	protected String normalizeLocation(String location){
  		if(location==null)
  			return null;
  		if(location.equalsIgnoreCase("Archivio del Tesoro di San Gennaro - Biblioteca"))
  			return "Cappella del Tesoro di San Gennaro";
  		else
  			return location;
	}


	protected String cutZeros(String str){
  		if(str==null)
  			return str;
  		while(str.startsWith("0"))
			str = str.substring(1);
  		return str;
	}
  	
  	protected Handler getHandler(String handlerName){  		
		Handler h = handlers.get(handlerName);			
		if(h==null){
			try{
				h = (Handler)Class.forName(handlerName).newInstance();
				handlers.put(handlerName, h);
			}
			catch(Exception e){
				logger.error("",e);
			}
		}
		return h;
  	}
  	
	@SuppressWarnings("unchecked")
	protected void makeGroup(List<MappingDefinition> subDefs, Record record,
			String group,String fieldName, String destinationNode, List<LabelValuePair> pairs, int pos) {
		int j = 0;
		List<VariableField> dataFields = record.getVariableFields(fieldName);
		for (VariableField vField : dataFields) {
			if(vField instanceof DataField && (pos==-1||pos==j)){
				DataField dataField = (DataField)vField;
				List<LabelPairGroup> pairsGroups = new ArrayList<LabelPairGroup>();
				for (MappingDefinition def : subDefs) {
					if(def.getLabelGroup()==null
							|| !def.getLabelGroup().equals(group)
							|| (isForFe() && def.isExcludeInFe()))
						continue;
					if(def.getMarcSections()!=null && def.getMarcSections().length>0){
						List<Subfield> subfields = null;
						char code = def.getMarcSections()[0].charAt(0);
						if(code=='1'){
							SubfieldImpl subfieldImpl = new SubfieldImpl();
							subfieldImpl.setData(""+dataField.getIndicator1());
							ArrayList<Subfield> fields = new ArrayList<Subfield>();
							fields.add(subfieldImpl);
							subfields = fields;
						}	
						else
							subfields = dataField.getSubfields(code);
						int i = 0;
						for (Subfield subfield : subfields) {
							String data = null;
							data = makeData(fieldName, "", subfield.getData(), def.getPosInit(), def.getPosEnd());
							if(def.getHandler()!=null && data!=null){
								Handler h = getHandler(def.getHandler());								
								if(h.isValidRecord(record)){
									data = h.getValue(getBid(record, "001", ALL),
										data,dataField,i, fieldName);
								}
								else{
									data = null;
								}
							}
							if(data==null ||data.trim().length()==0){
								i++;
								continue;
							}
							String key = destinationNode+"::"+def.getDestination();
							String v = normalizer.getMapValue(key,data);
							v = (v!=null)?v:data;
							if(def.isCutZeros())
								v = cutZeros(v);
							if(isForFe() && normalizer.isMapValue(key) && data.equals(v))
								continue;
							while(pairsGroups.size()<i+1){
								LabelPairGroup group2 = new LabelPairGroup(def.getLabelGroup(), "");
								group2.setOrder(def.getOrder());
								pairsGroups.add(group2);
							}
							List<LabelValuePair> list = pairsGroups.get(i).getLabelValuePairs();
							/**
							 * lo stesso label si appende
							 */
							if(list.size()>0 && list.get(list.size()-1).getLabel().equals(def.getDestination())){
								LabelValuePair p = list.get(list.size()-1); 
								p.setValue(p.getValue()+" "+v.trim());
								//logger.debug(""+p.getLabel()+"=>"+p.getValue());
							}
							else{
								LabelValuePair p = new LabelValuePair(def.getDestination(), v.trim());
								p.setVocabulary(def.getVocabulary());
								list.add(p);
								//LabelValuePair p = list.get(list.size()-1);
								//logger.debug(""+p.getLabel()+"=>"+p.getValue());
							}
							i++;
						}
					}
				}
				pairs.addAll(pairsGroups);
			}
			j++;
		}		
	}

	protected void makeNode(OutItem desc, Record record, String fieldName,
			String[] subfieldCodes, String destinationNode) throws IllegalArgumentException, 
			SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		makeNode(desc, record, fieldName, subfieldCodes, destinationNode,null,-1,-1,null,null,false,null);
	}
	protected void makeNode(OutItem desc,String destinationNode, String data) throws IllegalArgumentException,
	SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		makeNode(desc, destinationNode, data,null);
	}
	
	public static boolean hasNode(OutItem desc,String destinationNode, String qualifier) throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		return desc.hasNode(destinationNode, qualifier);
	}
	/*
	public static Object[] getNodes(OutItem desc,String nodename, String qualifier) throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		if (nodename.equalsIgnoreCase("Location")) {
			return desc.getLocationArray();
		} else {
			return DublinCoreParser.getSimpleLiterals(desc,
					StringUtil.firstUpper( nodename));
		} 
	}
	*/
	public void makeNode(OutItem desc,String destinationNode, String data, String qualifier)
	throws IllegalArgumentException, SecurityException,
	IllegalAccessException, InvocationTargetException,
	NoSuchMethodException {	
		if(data==null || data.trim().length()==0)
			return;
		data = filterData(data,destinationNode,qualifier);
		Field f = addField(desc, destinationNode, qualifier);
		if(f!=null)
			f.setTextValue(data);
		//desc.addField(destinationNode,qualifier).setTextValue(data);
	}
	protected String filterData(String data, String destinationNode,
			String qualifier) {
		if(qualifier!=null && destinationNode.equals("Identifier") && new String(" ISBN, ISSN, ISMN").indexOf(qualifier)!=-1)
			return data.replaceAll("-", "");
		return data;
	}
	
	public MappingDefinition[] getDefinitions(){
		return defs;
	}
	/*
	private String getXmlName(String name) {
		if(name.length()<1)
			return name;
		String na = name.substring(0,1).toUpperCase()+name.substring(1);
		String qua = getQualifier(name);
		if(qua!=null)
			return na.substring(0,na.length()-qua.length());
		else
			return na;
	}
	

	private String getQualifier(String name) {
		String ends[] = new String[]{"String","Genere","Place","PlaceString","Hidden","Dewey",
					"DeweyNumber","Livello","Tipo"};
		for (int i = 0; i < ends.length; i++) {
			if(name.endsWith(ends[i]))
				return ends[i];
		}
		return null;
	}
	*/
	
	public boolean scarta(OutItem desc) {
		return desc.getAbout().indexOf("\\MSM\\")!=-1 || desc.getAbout().indexOf("\\MUS\\")!=-1;
	}
	public void makeId(OutItem desc, Record record) {
		makeId(desc, record, "001", ALL);		
	}	

	/**
	 * le definizioni generali
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws SecurityException 
	 * @throws IllegalArgumentException 
	 */
	public void makeDefinitions(OutItem desc,Record record) throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{ 
		MappingDefinition[] defs = getMappingDefinitions();
		for (int i = 0; i < defs.length; i++) {
			if(defs[i].isIfFirst()){
				if(hasNode(desc,defs[i].getDestination(), defs[i].getQualifier()))
					continue;
			}
			if(!(isForFe() && defs[i].isExcludeInFe()))
				makeNode(desc, record, defs[i].getMarcField(), defs[i]
					.getMarcSections(), defs[i].getDestination(), defs[i].getQualifier(), defs[i].getPosInit(),defs[i].getPosEnd(),
					defs[i].getHandler(),defs[i].getSubDefs(),defs[i].isFacets(), defs[i]);
		}
	}
	protected MappingDefinition[] getMappingDefinitions() {
		return defs;
	}
	@SuppressWarnings("rawtypes")
	public void makeSpecialTwo(OutItem desc,Record record) throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		if(isEnabledLibroAntico && isLibroAntico(desc)){
			Field des = desc.addNewField("publisherArea",null); // 28-06-2010 desc.addNewDescription();
			ArrayList<String> datass = getValues(record, "620", ALL,-1,-1);
			String out = getString(datass, " ");						
			List<String> s = getValues(record, "712", new String[]{"4"},-1,-1);
			boolean isOk = false;
			for (Iterator iterator = s.iterator(); iterator
					.hasNext();) {
				String string = (String) iterator.next();
				if(string.indexOf("650")!=-1||string.indexOf("750")!=-1)
					isOk= true;
			}
			if(isOk){
				datass = getValues(record, "712", ALL);
				out+= " - "+ StringUtil.filterAttrs( getString(datass, " ") );
			}
			List<Field> dates =  desc.getFieldArray("issued");
			for (int i = 0; i < dates.size(); i++) {
				out+=" - "+dates.get(i).getTextValue();
			}
			des.setTextValue(out);
		}
		else{
			// modifica 28-06-2010
			//makeNode(desc, record, "210",ALL,"Description");
			makeNode(desc, record, "210",ALL,"PublisherArea");
			// fine
		}
	}
	/**
	 * ot used in XMLProfile
	 */
	@SuppressWarnings("unchecked")
	public void makeSpecialOne(OutItem desc,Record record) throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		List<DataField> datas = record.getDataFields();
		//boolean isPartOf = record.getLeader().toString().charAt(9)=='W';
		HashMap<String, String> fieldsDone = new HashMap<String, String>();
		for (Iterator<DataField> iterator = datas.iterator(); iterator
				.hasNext();) {
			DataField dataField = (DataField) iterator.next();
			if(dataField.getTag().equals("200")){
				//if(dataField.getIndicator1()=='0')
				//	isPartOf = true;
			}
			/**
			 * Trattamento speciale per 4xx.
			 */
			if(dataField.getTag().startsWith("4") && dataField.getTag().length()==3){
				String id="",data="";
				List<Subfield> subs = dataField.getSubfields();
				for (Iterator<Subfield> iterator2 = subs.iterator(); iterator2
						.hasNext();) {
					Subfield subfield = (Subfield) iterator2.next();
					// responsabilità
					if((subfield.getCode()=='3' || subfield.getCode()=='1') 
							&& (isPartOfField(dataField.getTag())))
						break;
					if(subfield.getData().startsWith("001"))
						id = subfield.getData().substring(3);
					else if(
							(!isPartOfField(dataField.getTag()) &&
							(subfield.getCode()=='a' || subfield.getCode()=='e'|| subfield.getCode()=='c'))
						||
							((isPartOfField(dataField.getTag())) &&
									// a|e|f|g|v
							(subfield.getCode()=='a' ||subfield.getCode()=='b' || subfield.getCode()=='e'|| subfield.getCode()=='f'
							|| subfield.getCode()=='g' || subfield.getCode()=='v'))
							){
						if(data.length()>0)
							data +=	// era sempre ' ', 23-04-2015
									getValueSeparator(dataField.getTag(), String.valueOf(subfield.getCode()), data, ALL); 
						data +=  clear27( subfield.getData() );
					}
				}							
				// modifica 24-06-2010: tutti i 4.. vanno in isPartOf....non solo quelli con titoli non significativo
				//makeNode(desc, "Alternative", data.trim()+ " [BID: "+id+"]"); 
				if(/*isPartOf &&*/ record.getLeader().toString().charAt(7)=='m' &&  
						(isPartOfField(dataField.getTag())) ){
					makeNode(desc, "IsPartOf", data.trim()+ " [BID: "+id+"]");
				}
				if(/*isPartOf &&*/ record.getLeader().toString().charAt(7)=='a' &&  
						(dataField.getTag().equals("463")) ){
					makeNode(desc, "IsPartOf", data.trim()+ " [BID: "+id+"]");
				}
				//makeNode(desc, "Alternative", data.trim()+ " [BID: "+id+"]"); 
				// 24-06-2010: nuovo hasPart
				if(dataField.getTag().equals("464") ){
					makeNode(desc, "hasPart", data.trim()+ " [BID: "+id+"]");
				}
			}
			/**
			 * Subject genere
			 */
			else if(isLibroAntico(desc) && dataField.getTag().equals("140") && dataField.getSubfield('a')!=null){
				String data = clear27( dataField.getSubfield('a').getData() );
				if(data.length()>9){
					makeGenereNode(desc,data.substring(9,11));
				}
				if(data.length()>11){
					makeGenereNode(desc,data.substring(11,13));
				}
				if(data.length()>13){
					makeGenereNode(desc,data.substring(13,15));
				}
				if(data.length()>15){
					makeGenereNode(desc,data.substring(15,17));
				}
			}
			else if(!isLibroAntico(desc) && dataField.getTag().equals("105") && record.getLeader().toString().charAt(7)=='m'
				&& dataField.getSubfield('a')!=null){
				String data = clear27( dataField.getSubfield('a').getData() );
				if(data.length()>3){
					makeGenereNode(desc, data.substring(3,4));
				}
				if(data.length()>4){
					makeGenereNode(desc, data.substring(4,5));
				}
				if(data.length()>5){
					makeGenereNode(desc, data.substring(5,6));
				}
				if(data.length()>6){
					makeGenereNode(desc, data.substring(6,7));
				}							
			}
			else if(!isLibroAntico(desc) && dataField.getTag().equals("110") && record.getLeader().toString().charAt(7)=='s'
				&& dataField.getSubfield('a')!=null){
				String data = clear27( dataField.getSubfield('a').getData() );
				if(data.length()>2){
					makeGenereNode(desc, data.substring(2,3));
				}
			}
			/**
			 * Publisher o contributor?
			 * bug: 30-06-2010 : inserimento * n.
			 */
			else if(dataField.getTag().equals("712") ){
				Subfield sub = dataField.getSubfield('4');
				StringBuffer values = null;
				values = makeValues(dataField.getTag(),  ALL,values,
						new ArrayList<String>(), dataField);
				if(sub!=null && (sub.getData().equals("650") ||sub.getData().equals("750"))){
					makeNode(desc, "Publisher", clear27(values.toString()));
					//makeNode(desc, record, "712", ALL , "Publisher");
					// la faccetta
					makeNode(desc, "Publisher", clear27(values.toString()),"string"); 
					//makeNode(desc, record, "712", ALL , "Publisher","string");
				}
				else{
					makeNode(desc, "Contributor", clear27(values.toString()));
					//makeNode(desc, record, "712", ALL , "Contributor");
				}
			}
			/**
			 * rinvii varianti dei nomi: 21-06-2010
			 */
			else if(dataField.getTag().equals("790") || dataField.getTag().equals("791")){
				Subfield subZ =  dataField.getSubfield('z');
				StringBuffer values = null;
				values = makeValues(dataField.getTag(),  new String[]{"a","b","c","d","e","f"},values,
						new ArrayList<String>(), dataField);
				if(subZ!=null && values!=null ){
						String data = clear27(values.toString())+" --> "+ clear27(subZ.getData());
						makeNode(desc, "Contributor", data.trim(), "Alias");					
				} 
			}
			/**
			 * Identifier
			 */
			else if(dataField.getTag().equals("071")){
				String qua=null;
				if(dataField.getIndicator1()=='2')
					qua = "n. lastra";
				else if(dataField.getIndicator1()=='4')
					qua = "n. editoriale";
				else if(dataField.getIndicator1()=='?')
					qua = "ISAN";
				if(qua!=null)
					makeNode(desc, record, "071", new String[]{"a","c"}, "Identifier", qua,-1,-1,null,null,false,null);
			}
			else if(dataField.getTag().equals("899") && record.getLeader().toString().charAt(6)=='d'){
				makeNode(desc, record, "899", new String[]{"1","3","c"}, "Identifier","segnatura",-1,-1,null,null,false,null);
			}
			/**
			 * Format
			 */
			else if(dataField.getTag().equals("215") && record.getLeader().toString().charAt(6)!='a'
				&& dataField.getSubfield('a')!=null){
				makeNode(desc, "Format", dataField.getSubfield('a').getData());
			}
			/**
			 * data = issued
			 */
			else if(dataField.getTag().equals("100")){
				Subfield sub = dataField.getSubfield('a');
				if(sub!=null){
					String val = sub.getData().substring(9,13);
					if(isValidDate(val))
						makeNode(desc, "Issued", val);
					if(sub.getData().charAt(7)=='b' || sub.getData().charAt(7)=='g'
						|| sub.getData().charAt(7)=='f'
						){									
						val = sub.getData().substring(13,17);
						if(isValidDate(val))
							makeNode(desc, "Issued", val);
					}
				}
			}
			fieldsDone.put(dataField.getTag(),dataField.getTag());
		}

	}
	 
	public void normalize(OutItem output) {
		normalizer.normalize(output);		
	}

	public boolean isFinished() {
		return false;
	}

}
