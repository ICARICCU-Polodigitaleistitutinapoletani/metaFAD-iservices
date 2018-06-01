package com.gruppometa.unimarc.handlers;

import org.marc4j.marc.Subfield;

public class Editore2Handler extends EditoreHandler {

	@Override
	protected boolean condition(Subfield sub) {
		return !sub.getData().equals("650");
	}

}
