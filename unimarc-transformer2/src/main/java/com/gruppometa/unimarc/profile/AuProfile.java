package com.gruppometa.unimarc.profile;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;

import org.marc4j.marc.DataField;
import org.marc4j.marc.Leader;
import org.marc4j.marc.Record;

import com.gruppometa.unimarc.object.Field;
import com.gruppometa.unimarc.object.OutItem;

public class AuProfile extends NaXmlProfile{

	protected String[] myVids = new String[]{"400","410","500","510"};
	
	@Override
	protected String[] getVidFields() {
		return myVids;
	}
	
	@Override
	public boolean makeLeader(OutItem desc, Leader leader) {
		super.makeLeader(desc, leader);
		if(leader.toString().length()>9 && leader.toString().charAt(9)!=' '){
			char c = leader.toString().charAt(9);
			Field f = addField(desc,"tipo_entita", "type");
			if(f!=null)
				f.setTextValue(""+c);
		}		
		if(leader.toString().length()>6 && leader.toString().charAt(6)!=' '){
			char c = leader.toString().charAt(6);
			Field f = addField(desc,"tipo_record", "type");
			if(f!=null)
				f.setTextValue(""+c);
		}
		return true;
	}
	
	
	@Override
	public void makeSpecialOne(OutItem desc, Record record) throws IllegalArgumentException, SecurityException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		super.makeSpecialOne(desc, record);
		String tipoScheda = "Persona fisica";
		String tipoEnte = null;
		if(record!=null){
			@SuppressWarnings("unchecked")
			List<DataField> datas = record.getDataFields();
			for (Iterator<DataField> iterator = datas.iterator(); iterator
					.hasNext();) {
				DataField dataField = (DataField) iterator.next();				
				if(dataField.getTag().equals("210")){
					tipoScheda = "Ente collettivo";
					char ind = dataField.getIndicator1();
					if(ind=='1')
						tipoEnte = "temporaneo";
					else
						tipoEnte = "permanente";
				}				
			}
		}
		makeNode(desc,"Tipo di scheda", tipoScheda);
		if(tipoEnte!=null)
			makeNode(desc,"Tipo di ente", tipoEnte);
	}



	@Override
	protected boolean isDataField(String dataField){
		return false; // 100 non è per la data
	}
	public AuProfile(String string) {
		super(string);
		isAuScheda = true;
	}

}
