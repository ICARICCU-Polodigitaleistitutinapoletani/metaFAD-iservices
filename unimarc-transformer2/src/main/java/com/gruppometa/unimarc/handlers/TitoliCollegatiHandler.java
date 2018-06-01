package com.gruppometa.unimarc.handlers;

import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TitoliCollegatiHandler implements Handler{

	protected int pos = 0;

	public TitoliCollegatiHandler(){
		
	}
	public List<String> getValues(Record record) {
		@SuppressWarnings("rawtypes")
		List dataFields = record.getVariableFields();
		List<String> values = new ArrayList<String>();
		for (@SuppressWarnings("rawtypes")
			Iterator iterator = dataFields.iterator(); iterator.hasNext();) {
			Object object = iterator.next();
			if(!(object instanceof  DataField))
				continue;
			DataField field = (DataField) object;
			if(!field.getTag().startsWith("4"))
				continue;
			String val = null;
			List subfields = field.getSubfields();
			Iterator it = subfields.iterator();
			String id = null;
			String subFieldName = null;
			while (it.hasNext()) {
				Subfield subfield = (Subfield) it.next();
				String code = "" + subfield.getCode();
				if (subFieldName != null && subFieldName.equals("200")) {
					values.add(subfield.getData());
					subFieldName = null;
				}
				if (code.equals("1") && subfield.getData().length() > 2) {
					subFieldName = subfield.getData().substring(0, 3);
				}

			}
		}
		return values;
	}

	public String getValue(String id, String param, DataField dataField, int i, String fieldName) {
		return null;
	}

	public boolean isValidRecord(Record record) {
		return false;
	}

}
