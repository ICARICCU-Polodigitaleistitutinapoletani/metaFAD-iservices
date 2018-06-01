package com.gruppometa.unimarc.handlers;

import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DedicaHandler implements Handler{

	public List<String> getValues(Record record) {
		@SuppressWarnings("rawtypes")
		List dataFields = record.getVariableFields("950");
		StringBuffer dataS;
		ArrayList<String> values = new ArrayList<String>();
		for (@SuppressWarnings("rawtypes")
		Iterator iterator = dataFields.iterator(); iterator.hasNext();) {
			Object object = iterator.next();
			if (object instanceof DataField) {
				DataField field = (DataField) object;
				List<Subfield> subsE = field.getSubfields('e');
				List<Subfield> subsI = field.getSubfields('l');
				int count = 0;
				if(subsE!=null) {
					for (Subfield sub : subsE) {
						dataS = new StringBuffer();
						String data = sub.getData();
						if (data.startsWith(" CR   ") || data.length() > 16) {
							String inventario = data.substring(6, 16);
							dataS.append(inventario + ": ");
						}
						sub = subsI != null && subsI.size() > count ? subsI.get(count) : null;
						if (sub != null)
							dataS.append(sub.getData());
						else
							dataS = new StringBuffer();
						if (dataS.length() > 0)
							values.add(dataS.toString());
						count++;
					}
				}
			} 
		}
		return values;
	}

	public String getValue(String id, String param, DataField dataField, int i, String fieldName) {
		return null;
	}

	public boolean isValidRecord(Record record) {
		return true;
	}

}
