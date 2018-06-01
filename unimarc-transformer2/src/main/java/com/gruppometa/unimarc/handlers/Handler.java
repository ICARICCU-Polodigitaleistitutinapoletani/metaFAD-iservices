package com.gruppometa.unimarc.handlers;

import java.util.List;

import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;

public interface Handler {
	List<String> getValues(Record record);
	String getValue(String id, String param, DataField dataField,int i, String fieldName);
	public boolean isValidRecord(Record record);
}
