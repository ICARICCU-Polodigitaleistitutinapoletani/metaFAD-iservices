package com.gruppometa.unimarc.object;

import java.util.ArrayList;
import java.util.List;

public class LabelPairGroup extends LabelValuePair{
	protected List<LabelValuePair> labelValuePairs = new ArrayList<LabelValuePair>();
	
	public List<LabelValuePair> getLabelValuePairs() {
		return labelValuePairs;
	}

	public void setLabelValuePairs(List<LabelValuePair> labelValuePairs) {
		this.labelValuePairs = labelValuePairs;
	}


	public LabelPairGroup(String label, String value) {
		super(label, value);
	}
	
}
