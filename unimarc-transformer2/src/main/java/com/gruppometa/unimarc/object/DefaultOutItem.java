package com.gruppometa.unimarc.object;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class DefaultOutItem implements OutItem {

	protected String about="";
	protected String descSource="";
	protected String descSourceLevel2="";
	protected String job="";
	
	protected List<Field> fields = new ArrayList<Field>();
	public String getAbout() {
		// TODO Auto-generated method stub
		return about;
	}

	public void setDescSource(String descSource) {
		this.descSource = descSource;		
	}

	public void setDescSourceLevel2(String descSourceLevel2) {
		this.descSourceLevel2 = descSourceLevel2;		
	}

	public void setJob(String job) {
		this.job = job;		
	}

	public void setAbout(String string) {
		this.about = string;
	}

	public Field addNewField(String string, String qualifier) {
		Field f = new DefaultField(string,  qualifier);
		int multiple = 0;
		for (Iterator<Field> iterator = fields.iterator(); iterator.hasNext();) {
			Field field = (Field) iterator.next();
			if(field.getName().equals(string) && ((field.getQualifier()!=null && field.getQualifier().equals(qualifier))
					|| (qualifier==null && field.getQualifier()==null))){
					multiple++;
					if(field.getMultiple()==0)
						((DefaultField)field).setMultiple(1);
			}			
		}
		if(multiple>0)
			((DefaultField)f).setMultiple(multiple+1);
		fields.add(f);
		return f;
	}

	/**
	 * @return the fields
	 */
	public List<Field> getFields() {
		return fields;
	}

	/**
	 * @param fields the fields to set
	 */
	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	public List<Field> getFieldArray(String string) {
		ArrayList<Field> fi = new ArrayList<Field>();
		for (Iterator<Field> iterator = fields.iterator(); iterator.hasNext();) {
			Field field = (Field) iterator.next();
			if(field.getName().equalsIgnoreCase(string))
				fi.add(field);
		}
		return fi;
	}

	public boolean hasNode(String destinationNode, String qualifier) {
		for (Iterator<Field> iterator = fields.iterator(); iterator.hasNext();) {
			Field field = (Field) iterator.next();
			if(field.getName().equals(destinationNode+qualifier))
				return true;
		}
		return false;
	}

	public Field addField(String string, String qualifier) {
		return addNewField(string, qualifier); 
	}
	
	

}
