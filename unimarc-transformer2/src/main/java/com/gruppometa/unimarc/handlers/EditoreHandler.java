package com.gruppometa.unimarc.handlers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;

public class EditoreHandler implements Handler{

	public List<String> getValues(Record record) {
		@SuppressWarnings("rawtypes")
		List dataFields = record.getVariableFields("710");
		StringBuffer dataS = null;
		ArrayList<String> values = new ArrayList<String>();
		for (@SuppressWarnings("rawtypes")
		Iterator iterator = dataFields.iterator(); iterator.hasNext();) {
			Object object = iterator.next();
			if (object instanceof DataField) {
				dataS = new StringBuffer();
				DataField field = (DataField) object;
				Subfield sub = field.getSubfield('4');
				if(sub!=null){
					if(condition(sub))
						continue;
				}
				else
					continue;
				sub = field.getSubfield('a');
				if(sub!=null)
					dataS.append(sub.getData());
				sub = field.getSubfield('b');
				if(sub!=null)
					dataS.append(sub.getData());
				sub = field.getSubfield('f');
				if(sub!=null)
					dataS.append(sub.getData());
				if (dataS.length()>0)
					values.add(dataS.toString());
			} 
		}
		return values;
	}

	protected boolean condition(Subfield sub){
		return !(sub.getData().equals("650")
				||	sub.getData().equals("610")
				||	sub.getData().equals("750")
				||	sub.getData().equals("760")
				);
	}
	
	public String getValue(String id, String param, DataField dataField, int i, String fieldName) {
		return null;
	}

	public boolean isValidRecord(Record record) {
		return true;
	}

}
