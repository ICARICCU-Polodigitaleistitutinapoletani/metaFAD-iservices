package com.gruppometa.unimarc.handlers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gruppometa.unimarc.profile.NaXmlProfile;

public class KardexHandler implements Handler{
	
	protected static final Logger logger = LoggerFactory.getLogger(KardexHandler.class); 
	
	public List<String> getValues(Record record) {
		return null;
	}
	
	public boolean isValidRecord(Record record){
		int pos = 7;
		String leader = record.getLeader().toString(); 
		if(leader.length()>pos && leader.charAt(pos)=='s')
			return true;
		else
			return false;
	}

	public String getValue(String id, String param, DataField dataField, int i, String fieldName) {
		String biblioteca = "";
		String data = dataField.getSubfield('a').getData();
		biblioteca = NaXmlProfile.getLocalizzazioneFromLongName(data);
		if(biblioteca==null)
			return null;
		String inventario = //biblioteca+"   "+
				fillSpace( fill(param,9), 12);
		String url = null;
		try {
			url = "${kardexService}?bid="+URLEncoder.encode(id,"UTF-8")+"&biblioteca="
						+URLEncoder.encode(biblioteca,"UTF-8")+
						"&inventario="+ URLEncoder.encode(inventario,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("",e);
		}
		return url;
	}

	protected String fill(String param, int i) {
		if(param==null)
			return null;
		while(param.length()<i)
			param = param.substring(0,3)+ "0"+param.substring(3);
		return param;
	}
	protected String fillSpace(String param, int i) {
		if(param==null)
			return null;
		while(param.length()<i)
			param = " "+param;
		return param;
	}

}
