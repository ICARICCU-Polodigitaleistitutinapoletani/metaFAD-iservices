package com.gruppometa.metasearch.data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FieldList {
	protected List<Field> fields = new ArrayList<Field>();

	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	public void sort(){
		if(fields==null)
			return;
		fields.sort(new Comparator<Field>() {
			@Override
			public int compare(Field field, Field t1) {
				if(field.getLabel()!=null)
					return field.getLabel().compareTo(t1.getLabel());
				else
					return field.getId().compareTo(t1.getId());
			}
		});
	}
}
