package com.gruppometa.unimarc.profile;

public class GeneralProfile extends NaXmlProfile {
	public GeneralProfile(String string) {
		super(string);
	}

	@Override
	public boolean isValidLocalizzazione(String value) {
		return true;
	}

}
