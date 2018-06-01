package com.gruppometa.unimarc.handlers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;

public class NascitaHandler implements Handler{

	protected int pos = 0;
	
	public NascitaHandler(){
		
	}
	public List<String> getValues(Record record) {
		@SuppressWarnings("rawtypes")
		List dataFields = record.getVariableFields(new String[]{"200","210","400","410","500","510","300"});
		List<String> values = new ArrayList<String>();
		for (@SuppressWarnings("rawtypes")
			Iterator iterator = dataFields.iterator(); iterator.hasNext();) {
			Object object = iterator.next();
			DataField field = (DataField) object;
			String val = null;
			if(field.getTag().equals("300")){
				Subfield sub = field.getSubfield('a');
				if(sub!=null && sub.getData().contains(" // ")){
					val = sub.getData();
					val = val.substring(0, val.indexOf(" // "));
				}
				
			}
			else{
				Subfield sub = field.getSubfield('f');
				if(sub!=null){
					val = sub.getData();			
				}
			}
			if(val!=null){
				val = val.trim()
						.replaceAll("^\\<", "")
						.replaceAll("\\>$", "")
						;
				int posSemi = val.indexOf(" ; ");
				if(posSemi!=-1){
					val = val.substring(posSemi+3);
				}
				String[] p = val.split("-");
				if(p.length==2 && !values.contains(p[pos]))
					values.add(p[pos]);
				else if(pos==0 && val.endsWith("-")){
					String v = val.substring(0,val.length()-1);
					if(!values.contains(v))
						values.add(v);
				}
				else if(pos==1 && val.startsWith("-")){
					String v = val.substring(1);
					if(!values.contains(v))
						values.add(v);
				}
			}
		}
		return filterBest(values);
	}

	protected List<String> filterBest(List<String> values){
		if(values==null || values.size()<2)
			return values;
		List<String> best = new ArrayList<String>();
		for(String v: values){
			if(v.length()==4){
				best.add(values.get(0));
				return best;
			}
		}
		best.add(values.get(0));
		return best;
	}

	public String getValue(String id, String param, DataField dataField, int i, String fieldName) {
		return null;
	}

	public boolean isValidRecord(Record record) {
		return false;
	}

}
