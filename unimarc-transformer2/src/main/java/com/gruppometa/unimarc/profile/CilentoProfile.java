package com.gruppometa.unimarc.profile;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.marc4j.marc.Record;

import com.gruppometa.unimarc.object.OutItem;

public class CilentoProfile extends NaXmlProfile{

	public CilentoProfile(String string) {
		super(string);
	}

	@Override
	public boolean isValidLocalizzazione(String value) {
		return true;
	}

	@Override
	protected void makeId(OutItem desc, Record record, String fieldName, String[] subfieldCodes) {		
		super.makeId(desc, record, fieldName, subfieldCodes);
		desc.setAbout( getLocation()+"_"+desc.getAbout());
	}

	protected String getLocation() {
		Pattern pattern = Pattern.compile("(.+)\\_(.+)\\_(.*)\\_(.+)");
		Matcher m = pattern.matcher(getFilename());
		if(m.find()){
			return m.group(3);
		}
		return getFilename();
	}

}
