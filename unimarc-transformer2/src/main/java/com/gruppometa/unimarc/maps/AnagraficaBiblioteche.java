package com.gruppometa.unimarc.maps;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class AnagraficaBiblioteche implements Map<String, String>{
	protected static Properties bibs = null;
	public static String BIB_SEPERATOR = " ; ";
	public static String BIB_SEPERATOR_REGEX = "\\s;\\s";
	public static String BIB_SEGNATURA_LABEL = "Segnatura: ";
	public static String BIB_INVENTORY_LABEL = "Inventario: ";
	
	protected static Log logger = LogFactory.getLog(AnagraficaBiblioteche.class);
	public AnagraficaBiblioteche(){
		if(bibs==null){
			bibs = new Properties();
			try {
				bibs.load(this.getClass().getResourceAsStream("/anagrafica-biblioteche.properties"));
			} catch (IOException e) {
				logger.error(e);
			}
		}
	}
	public String getName(String key){
		return getName(key, false);
	}
	public String getName(String key, boolean keyLastPos){
		if(bibs!=null){
			String ret = bibs.getProperty(
					(key!=null&&key.toUpperCase().startsWith("IT-"))?key.substring(3).toUpperCase():key.toUpperCase()
							); 
			if(ret==null){
				return null;
			}
			String[] parts = ret.split(" \\- ");
			if(parts.length!=3 && key.length()<2)
				return ret;
		    return (!keyLastPos?(key + " - "):"")+ parts[0]+ " - " + parts[1] + " - " + key.substring(0,2)+
		    	(keyLastPos?(" - " +key ):"");
		}
		else
			return key;
	}
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}
	public boolean containsKey(Object key) {
		// TODO Auto-generated method stub
		return false;
	}
	public boolean containsValue(Object value) {
		// TODO Auto-generated method stub
		return false;
	}
	public String get(Object key) {
		return getName((String)key,true);
	}
	public String put(String key, String value) {
		// TODO Auto-generated method stub
		return null;
	}
	public String remove(Object key) {
		// TODO Auto-generated method stub
		return null;
	}
	public void putAll(Map<? extends String, ? extends String> m) {
		// TODO Auto-generated method stub
		
	}
	public void clear() {
	}
	public Set<String> keySet() {
		// TODO Auto-generated method stub
		return null;
	}
	public Collection<String> values() {
		// TODO Auto-generated method stub
		return null;
	}
	public Set<java.util.Map.Entry<String, String>> entrySet() {
		// TODO Auto-generated method stub
		return null;
	}
}
