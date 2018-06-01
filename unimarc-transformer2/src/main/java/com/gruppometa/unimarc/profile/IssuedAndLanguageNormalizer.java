package com.gruppometa.unimarc.profile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gruppometa.unimarc.mapping.MappingDefinition;
import com.gruppometa.unimarc.maps.AnagraficaBiblioteche;
import com.gruppometa.unimarc.object.DefaultField;
import com.gruppometa.unimarc.object.Field;
import com.gruppometa.unimarc.object.OutItem;
import com.gruppometa.unimarc.object.Output;

public class IssuedAndLanguageNormalizer{
	
	protected static Log logger = LogFactory.getLog(IssuedAndLanguageNormalizer.class); 
	protected Map<String, String> languangeHash = null;
	protected Map<String, String> paeseHash = null;
	protected Map<String, Map<String,String>> maps = new HashMap<String, Map<String,String>>();
	protected Map<String, Map<String,String>> vocaburaliesLower = new HashMap<String, Map<String,String>>();
	protected Map<String, Map<String,String>> vocaburaliesNotLower = new HashMap<String, Map<String,String>>();
	protected Map<String, Map<String,String>> vocaburaliesLowerNotKey = new HashMap<String, Map<String,String>>();
	protected Map<String, Map<String,String>> vocaburaliesFromFile = new HashMap<String, Map<String,String>>();
	protected String languageFieldName = "language";
	protected String paeseFieldName = "paese";
	protected AnagraficaBiblioteche anagraficaBiblioteche = new AnagraficaBiblioteche();
	protected String fromFile = "frui";
	protected String fromFileName = "CategorieDiFruizione_NAP.txt";
	protected String pwd = null;
	
	public IssuedAndLanguageNormalizer(){
		initVocabularies();
		languangeHash = getMap("ling");//loadLanguageHash("/list.normalize.language.txt");
		paeseHash = getMap("paes");//loadLanguageHash("/list.normalize.paese.txt");			
	}
	protected Map<String, String> getMap(String vocabulary){
		return getMap(vocabulary, true);
	}

	public Map<String, String> getMap(String vocabulary, boolean makelower) {
		return getMap(vocabulary,makelower, true);
	}

	public Map<String, String> getMap(String vocabulary, boolean makelower, boolean keyLower){
		if(fromFile.equals(vocabulary)){
			HashMap<String, String> ret = loadFromFile(fromFileName, false);
			if(ret!=null)
				return ret;
		}
		if(makelower  && !keyLower && vocaburaliesLowerNotKey.get(vocabulary)!=null)
			return vocaburaliesLowerNotKey.get(vocabulary);
		else if(makelower && vocaburaliesLower.get(vocabulary)!=null)
				return vocaburaliesLower.get(vocabulary);
		else if(!makelower && vocaburaliesNotLower.get(vocabulary)!=null)
			return vocaburaliesNotLower.get(vocabulary);
		else{
			String key = vocabulary+"::"+makelower;
			if(vocaburaliesFromFile.get(key)!=null)
				return vocaburaliesFromFile.get(key);
			else {
				logger.warn("use extra vocabulary from txt file (" + vocabulary + ")");
				Map<String, String> ret = loadLanguageHash("/list.normalize." + vocabulary + ".txt", makelower);
				vocaburaliesFromFile.put(key, ret);
				return ret;
			}
		}
	}

	public boolean needReinit(){
		return (pwd!=null && new File(pwd,fromFileName).exists());
	}
	
	private HashMap<String, String> loadFromFile(String fromFileName2, boolean makelower) {
		HashMap<String, String> list = new HashMap<String, String> ();
		if(pwd==null || !new File(pwd,fromFileName2).exists()){
			logger.warn("Frui file not found ("+pwd+","+fromFileName2+").");
			return null;
		}			
		try{
			FileReader reader = new FileReader(new File(pwd,fromFileName2));
			BufferedReader buf = new BufferedReader(reader);
			String line = null;
		
			while((line=buf.readLine())!=null){
				String[] p = line.split("\\|");
				if(p.length>1){
					list.put(p[0].trim().toLowerCase(), makelower?p[1].trim().toLowerCase():p[1].trim());
				}
			}
			buf.close();
		} catch (Exception e) {
			logger.error(e);
			return null;
		}
		return list;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public void initMaps(MappingDefinition[] defs){
		for (MappingDefinition mappingDefinition : defs) {
			if(mappingDefinition.getVocabulary()!=null
					){
				if(!mappingDefinition.getVocabulary().equalsIgnoreCase("anagrafica-biblioteche"))
					maps.put(mappingDefinition.getDestination(),  getMap(mappingDefinition.getVocabulary()));
				else
					maps.put(mappingDefinition.getDestination(),anagraficaBiblioteche);
			}
			if(mappingDefinition.getSubDefs()!=null){
				for (MappingDefinition mappingDefinition2 : mappingDefinition.getSubDefs()) {
					if(mappingDefinition2.getVocabulary()!=null 

							){
						if(!mappingDefinition2.getVocabulary().equalsIgnoreCase("anagrafica-biblioteche"))
							maps.put(mappingDefinition.getDestination()+"::"+
									mappingDefinition2.getDestination(), getMap(mappingDefinition2.getVocabulary()));
						else
							maps.put(mappingDefinition.getDestination()+"::"+
									mappingDefinition2.getDestination(),anagraficaBiblioteche);
					}					
				}
			}
		}
	}
	
	protected static HashMap<String, String> loadLanguageHash(String source){
		return loadLanguageHash(source,true);
	}
	protected static HashMap<String, String> loadLanguageHash(String source, boolean makelower){
		HashMap<String, String> list = new HashMap<String, String> ();
		try {
		InputStream input =  IssuedAndLanguageNormalizer.class
				.getResourceAsStream(
				source
				//"/list.normalize.language.txt"
				); 
		if(input==null){
			logger.warn("Resource not found: "+source);
			return list;
		}
		//BufferedInputStream buf = new BufferedInputStream(input);
		InputStreamReader reader = new InputStreamReader(input);
		BufferedReader buf = new BufferedReader(reader);
		String line = null;
		
			while((line=buf.readLine())!=null){
				if(source.endsWith("parts2.txt")){
					String[] p = line.split("(\\t)");
					if(p.length==2){
						String ret = p[1];
						list.put(ret.toLowerCase(), makelower?ret.toLowerCase():ret);
					}
				}
				else if(source.endsWith("parts2e.txt")){
					String[] p = line.split("(=)",2);
					if(p.length==2){
						list.put(p[0].trim().toLowerCase(), makelower?p[1].trim().toLowerCase():p[1].trim());
					}
				}
				else{
					String[] p = line.split("( |\\t)");
					if(p.length>2){
						String ret = "";
						for (int i = 2; i < p.length-1; i++) {
							if(ret.length()>0)
								ret+=" ";
							ret += p[i];
						}
						list.put(p[1].toLowerCase(), makelower?ret.toLowerCase():ret);
					}
				}
			}
			buf.close();
		} catch (Exception e) {
			logger.error(e);
		}
		return list;
	}
	
	protected void initVocabularies() {
		try {
		String source = "/tabelle-codici.csv"; 
		InputStream input =  IssuedAndLanguageNormalizer.class
				.getResourceAsStream(source); 
		if(input==null){
			logger.warn("Resource not found: "+source);
			return;
		}
		InputStreamReader reader = new InputStreamReader(input);
		BufferedReader buf = new BufferedReader(reader);
		String line = null;
		
			while((line=buf.readLine())!=null){
				String[] p = line.split("\\t");
				if(p.length>2){
					String voc = p[0].trim().toLowerCase();
					Map<String, String> vocMap = vocaburaliesLower.get(voc);
					Map<String, String> vocMap2 = vocaburaliesNotLower.get(voc);
					Map<String, String> vocMap3 = vocaburaliesLowerNotKey.get(voc);
					if(vocMap==null){
						vocMap = new HashMap<String, String>();
						vocaburaliesLower.put(voc, vocMap);
					}
					if(vocMap2==null){
						vocMap2 = new HashMap<String, String>();
						vocaburaliesNotLower.put(voc, vocMap2);
					}
					if(vocMap3==null){
						vocMap3 = new HashMap<String, String>();
						vocaburaliesLowerNotKey.put(voc, vocMap3);
					}
					vocMap.put(p[2].trim().toLowerCase(), p[1].trim().toLowerCase());
					vocMap2.put(p[2].trim().toLowerCase(), p[1].trim());
					vocMap3.put(p[2].trim(), p[1].trim().trim().toLowerCase());
				}
			}
			buf.close();
		} catch (Exception e) {
			logger.error(e);
		}
	}
	protected static HashMap<String, String> loadLanguageHashTabbed(String source){
		HashMap<String, String> list = new HashMap<String, String> ();
		try {
		InputStream input =  IssuedAndLanguageNormalizer.class
				.getResourceAsStream(
				source
				//"/list.normalize.language.txt"
				); 
		if(input==null){
			logger.warn("Resource not found: "+source);
			return list;
		}
		InputStreamReader reader = new InputStreamReader(input);
		BufferedReader buf = new BufferedReader(reader);
		String line = null;
		
			while((line=buf.readLine())!=null){
				String[] p = line.split("\\t");
				if(p.length>1){
					list.put(p[0].trim(), p[1].trim());
				}
			}
			buf.close();
		} catch (Exception e) {
			logger.error(e);
		}
		return list;
	}

	/**
	 * @return the languageFieldName
	 */
	public String getLanguageFieldName() {
		return languageFieldName;
	}

	/**
	 * @param languageFieldName the languageFieldName to set
	 */
	public void setLanguageFieldName(String languageFieldName) {
		this.languageFieldName = languageFieldName;
	}

	public void normalize(Output rdf) {
		List<OutItem> descs = rdf.getItems();
		for (int i = 0; descs!=null && i < descs.size(); i++) {
			OutItem desc =  descs.get(i);
			normalize(desc);			
		}
	}

	public void normalize(OutItem desc) {
		try {
			String val = "";				
			List<Field> lits =  desc.getFieldArray("issued");
			for (int j = 0; lits!=null && j < lits.size(); j++) {
				String c =  lits.get(j).getTextValue();
				if(!val.contains(c)){
					if(val.length()>0)
						val+="-";
					val+=c;
				}
			}
			if(val.length()>0){
				Field lit = desc.addNewField("issued","string");
				lit.setTextValue(val);
			}
			makeHash(desc,paeseFieldName, paeseHash);
			
			for(String key: maps.keySet()){
				makeHash(desc, key, maps.get(key));
			}
			
			lits =  desc.getFieldArray(languageFieldName);				
			for (int j = 0; lits!=null && j < lits.size(); j++) {
				String text =  lits.get(j).getTextValue();
				String[] langues = getLanguages(text, languangeHash);
				if(langues!=null && langues.length>=1)
					 lits.get(j).setTextValue(langues[0]);					
				for (int k = 1;  langues!=null && k < langues.length; k++) {
					Field lit = desc.addNewField(languageFieldName,null);						
					lit.setTextValue(langues[k]);
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}


	public void makeHash(OutItem desc, String fieldName, Map<String, String> map) {
		List<Field> lits;
		lits =  desc.getFieldArray(fieldName);
		for (int j = 0; lits!=null && j < lits.size(); j++) {
			String text =  lits.get(j).getTextValue();
			if(map.get(text)!=null){
				lits.get(j).setTextValue(map.get(text));
			}						
			else if(map.get(text.toLowerCase())!=null){
				lits.get(j).setTextValue(map.get(text.toLowerCase()));
			}		
			if(text.contains(" | ")){
				String[] parts = text.split("( \\| )");
				String str = "";
				for (String nStr : parts) {
					String v = map.get(nStr);
					if(str.length()>0)
						str+=" | ";
					if(v!=null){
						str+=v;
					}
					else
						str+=nStr;
				}
				lits.get(j).setTextValue(str);
			}
		}
	}
	
	protected String[] getLanguages(String text, Map<String, String> languangeHash){
		if(text==null)
			return null;
		String[] vals = text.split(";|,|\\s");
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < vals.length; i++) {
			vals[i] = vals[i].toLowerCase().trim();
			if(vals[i].equals("e"))
				continue;
			if(vals[i].equals("at")||vals[i].equals("la"))
				vals[i] = "lat";
			if(vals[i].equals("it"))
				vals[i] = "ita";
			if(vals[i].equals("en"))
				vals[i] = "eng";
			if(vals[i].equals("es"))
				vals[i] = "spa";
			if(vals[i].equals("bs"))
				vals[i] = "abs";
			if(vals[i].equals("de"))
				vals[i] = "ger";
			if(vals[i].equals("multilingua"))
				vals[i] = "mis";
			if(vals[i].equals("fr")||vals[i].equals("fra"))
				vals[i] = "fre";
			if(languangeHash.get(vals[i])!=null)
				vals[i] = languangeHash.get(vals[i]);
			if(vals[i].length()>0)
				list.add(vals[i]);
		}
		
		return list.toArray(new String[list.size()]);
	}
	protected void normalize(OutItem desc,Field lit, String name, String qualifier, int i) {		
		if(name.equals("relation")){
			String text = lit.getTextValue();
			if(text.startsWith("'STA IN: '")){
				desc.getFields().remove(i);
				Field litt = new DefaultField("isPartOf",null);
				desc.getFields().add(0,litt);				
				litt.setTextValue(text.substring(text.indexOf(": '")+3));				
			}
		}
	}

	public boolean isMapValue(String key){
		return key!=null &&  maps.get(key)!=null;
	}
	public String getMapValue(String key, String string) {
		//System.out.println(key+"::"+string);
		if(key.endsWith("anagrafica-biblioteche")){			
			return anagraficaBiblioteche.getName(
					(string!=null&&string.toUpperCase().startsWith("IT-"))?
								string.substring(3).toUpperCase():
									string.toUpperCase()
							);
		}
		if(maps.get(key)!=null && string!=null){
			String ret = maps.get(key).get(string.toLowerCase());
			if(ret==null){
				/**
				 * il valore puo' contenere valori in " | "
				 * 
				 */
				if(string.contains(" | ")){
					String[] parts = string.split("( \\| )");
					String str = "";
					for (String nStr : parts) {
						String v = getMapValue(key, nStr);
						if(str.length()>0)
							str+=" | ";
						if(v!=null){
							str+=v;
						}
						else
							str+=nStr;
					}
					return str;
				}
				/**
				 * valore possono contenere | per "niente"
				 */
				string = string.replace('|', ' ');
				string =  string.trim();
				ret = maps.get(key).get(string.toLowerCase());
			}
			//System.out.println(key+"::"+string+" ="+ret);				
			return ret;
		}			
		return null;
	}
}
