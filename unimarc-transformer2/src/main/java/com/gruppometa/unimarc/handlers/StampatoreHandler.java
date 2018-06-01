package com.gruppometa.unimarc.handlers;

import org.marc4j.marc.Subfield;

public class StampatoreHandler extends EditoreHandler {

	@Override
	protected boolean condition(Subfield sub) {
		return !sub.getData().equals("610")
				&& !sub.getData().equals("620");
	}

}
