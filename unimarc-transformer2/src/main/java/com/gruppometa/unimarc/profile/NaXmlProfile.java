package com.gruppometa.unimarc.profile;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.marc4j.MarcStreamWriter;
import org.marc4j.MarcXmlWriter;
import org.marc4j.converter.CharConverter;
import org.marc4j.converter.impl.AnselToUnicode;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Leader;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;

import com.gruppometa.unimarc.mapping.MappingDefinition;
import com.gruppometa.unimarc.object.Field;
import com.gruppometa.unimarc.object.OutItem;

public class NaXmlProfile extends XmlProfile{
	protected boolean haveToMakeFirst700 = true;
	protected boolean first700isSecondary = false;
	protected boolean isAuScheda = false;
	protected HashMap<String,String> ruoli_grafici = new HashMap<String,String>();
	public NaXmlProfile(String string) {
		super(string);
		initMap();
	}

	protected void initMap(){

		ruoli_grafici.put("360","");
		ruoli_grafici.put("570","");
		ruoli_grafici.put("018","");
		ruoli_grafici.put("040","");
		ruoli_grafici.put("050","");
		ruoli_grafici.put("070","");
		ruoli_grafici.put("072","");
		ruoli_grafici.put("330","");
		ruoli_grafici.put("170","");
		ruoli_grafici.put("580","");
		ruoli_grafici.put("190","");
		ruoli_grafici.put("723","");
		ruoli_grafici.put("220","");
		ruoli_grafici.put("240","");
		ruoli_grafici.put("257","");
		ruoli_grafici.put("700","");
		ruoli_grafici.put("340","");
		ruoli_grafici.put("273","");
		ruoli_grafici.put("290","");
		ruoli_grafici.put("280","");
		ruoli_grafici.put("651","");
		ruoli_grafici.put("901","");
		ruoli_grafici.put("310","");
		ruoli_grafici.put("320","");
		ruoli_grafici.put("650","");
		ruoli_grafici.put("380","");
		ruoli_grafici.put("400","");
		ruoli_grafici.put("720","");
		ruoli_grafici.put("450","");
		ruoli_grafici.put("600","");
		ruoli_grafici.put("410","");
		ruoli_grafici.put("245","");
		ruoli_grafici.put("440","");
		ruoli_grafici.put("350","");
		ruoli_grafici.put("530","");
		ruoli_grafici.put("900","");
		ruoli_grafici.put("490","");
		ruoli_grafici.put("500","");
		ruoli_grafici.put("510","");
		ruoli_grafici.put("430","");
		ruoli_grafici.put("420","");
		ruoli_grafici.put("390","");
		ruoli_grafici.put("630","");
		ruoli_grafici.put("680","");
		ruoli_grafici.put("610","");
		ruoli_grafici.put("620","");
		ruoli_grafici.put("750","");
		ruoli_grafici.put("760","");
	}

	@Override
	public boolean makeLeader(OutItem desc, Leader leader) {
		/**
		 * filter quelli not m,s,a POLODEBUG-202
		 */
		if(isForFe()) {
			char cLevel = leader.toString().charAt(7);
			if(cLevel!='m' && cLevel!='s' && cLevel!='a')
				return false;
		}
		boolean ret = super.makeLeader(desc, leader);
		haveToMakeFirst700 = true;
		if(leader.toString().length()>17){
			char c = leader.toString().charAt(18);
			Field f = addField(desc,"tipo_catalogazione", "type");
			if(f!=null)
				f.setTextValue(""+c);
		}
		return ret;
	}
	
	@Override
	public void init(){
		super.init();
		SbnProfile.tipoType = normalizer.getMap("geun");		
		SbnProfile.tipoLivello = normalizer.getMap("nabi");		
	}
	
	
	
	@Override
	public void notifyFullFilename(String filename) {
		//logger.info("reinit maps 1."+filename);
		if(filename==null || new File(filename).getParentFile()==null)
			return;
		//logger.info("reinit maps 2.");
		String directory = new File(filename).getParentFile().getAbsolutePath();
		normalizer.setPwd(directory);
		if(normalizer.needReinit()){
			logger.info("reinit maps.");
			init();
		}
	}

	@Override
	public void makeNode(OutItem desc,String destinationNode, String data, String qualifier)
			throws IllegalArgumentException, SecurityException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {	
				if(data==null || data.trim().length()==0)
					return;
				super.makeNode(desc, destinationNode, data, qualifier);
				if((destinationNode.equalsIgnoreCase("autore") || destinationNode.equalsIgnoreCase("autore secondario")) 
						&& (haveToMakeFirst700 || first700isSecondary)){
					if(first700isSecondary && destinationNode.equalsIgnoreCase("autore")){
						first700isSecondary = false;
						Field toRemove = null;
						for(Field f: desc.getFields()){
							if(f.getName().equals("Autore [sintetico]")){
								toRemove = f;
								break;
							}
						}
						if(toRemove!=null)
							desc.getFields().remove(toRemove);
					}
					if(destinationNode.equalsIgnoreCase("autore secondario"))
						first700isSecondary = true;
					super.makeNode(desc, "Autore [sintetico]", data, qualifier);
					haveToMakeFirst700 = false;
				}
	}

	@Override
	public boolean isValidLocalizzazione(String value){
		if(value==null)
			return false;
		if(value.equalsIgnoreCase("Istituto italiano per gli studi storici"))  
			return true;
		if(value.equalsIgnoreCase("Società napoletana di storia patria"))  
			return true;
		if(value.equalsIgnoreCase("Fondazione Biblioteca Benedetto Croce"))  
			return true;
		if(value.equalsIgnoreCase("Biblioteca del Pio Monte della Misericordia"))  
			return true;
		if(value.equalsIgnoreCase("Archivio del Tesoro di San Gennaro - Biblioteca"))
			return true;
		return false;
	}
	
	public static String getLocalizzazioneFromLongName(String value){
		if(value==null)
			return null;
		if(value.equalsIgnoreCase("Istituto italiano per gli studi storici"))  
			return "CR";
		if(value.equalsIgnoreCase("Società napoletana di storia patria"))  
			return "SP";
		if(value.equalsIgnoreCase("Fondazione Biblioteca Benedetto Croce"))  
			return "FC";
		if(value.equalsIgnoreCase("Biblioteca del Pio Monte della Misericordia"))  
			return "PM";
		if(value.equalsIgnoreCase("Archivio del Tesoro di San Gennaro - Biblioteca"))
			return "BK";
		return null;
	}
	protected List<DataField> removeFields = new ArrayList<DataField>();
	
	@Override
	protected void removeDataFields(Record record) {
		for (DataField f :  removeFields) {
			record.removeVariableField(f);
		}
		removeFields.clear();
	}

	@Override
	protected void removeDataField(DataField dataField) {
		removeFields.add(dataField);
	}

	@Override
	public void makeSpecialOne(OutItem desc, Record record) throws IllegalArgumentException ,SecurityException ,IllegalAccessException ,InvocationTargetException ,NoSuchMethodException {
		createUnimarc(desc, record);
		super.makeSpecialOne(desc, record);
		@SuppressWarnings("unchecked")
		List<DataField> datas = record.getDataFields();
		//boolean isPartOf = record.getLeader().toString().charAt(9)=='W';
		//HashMap<String, String> fieldsDone = new HashMap<String, String>();
		for (Iterator<DataField> iterator = datas.iterator(); iterator
				.hasNext();) {
			DataField dataField = (DataField) iterator.next();				
			if(dataField.getTag().equals("300")){
				@SuppressWarnings("unchecked")
				List<Subfield> subs = dataField.getSubfields('a');
				for (Subfield sub : subs) {
					String data = sub.getData();
					if(data.contains(" // ") && isAuScheda){
						int pos = data.indexOf(" // ");
						makeNode(desc, "Datazione", data.substring(0, pos));
						makeNode(desc, "Nota informativa", data.substring(pos+4));
					}
					else{
						// POLODEBUG-585, doppia nota, solo per au (31-08-2017)
						if(isAuScheda)
							makeNode(desc, "Nota informativa", data);
					}
				}
			}
			/**
			 * POLODEBUG-186
			 */
			if(dataField.getTag().startsWith("7") && isForFe()){
				// Ruolo
				List<Subfield> subs = dataField.getSubfields('4');
				for (Subfield sub : subs) {
					String data = sub.getData();
					while(data.startsWith("0"))
						data = data.substring(1);
					String ruolo = normalizer.getMapValue("Ruolo",data);
					if(ruolo!=null) {
						makeNode(desc, "Ruolo", ruolo);
						if(ruoli_grafici.containsKey(sub.getData()))
							makeNode(desc, "Ruolo ridotto grafica", ruolo);
					}
				}
			}
		}
	}
	
	protected void createUnimarc(OutItem desc, Record record){
		/**
		 * creazione campo unimarc
		 */
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			MarcStreamWriter writer = new MarcStreamWriter(out,"UTF-8");
			// bug: converte ...? 31-08-2016: non va convertito perchè deve rimanere UTF-8
			//writer.setConverter(new AnselToUnicode() );
			// vuol dire che va reindizzato per l'uso dell'unimarc
			writer.write(record);
			writer.close();
			Field f = desc.addField("unimarc", null);
			f.setBinaryValue(Base64.getEncoder().encodeToString(out.toByteArray()));			
			f.setTextValue(record.toString());
			//writer.write(record);			
		} catch (Exception e) {
			logger.error("Error in record "+desc.getAbout(),e);
		}	
	}
	@Override
	public void makeSpecialTwo(OutItem desc, Record record) throws IllegalArgumentException, SecurityException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		super.makeSpecialTwo(desc, record);
		/**
		 * gestione legami
		 */
		String partOf = null;
		for(Field f :desc.getFields()){			
			if(f.getBid()!=null){
				MappingDefinition def = getDefinition(f.getName());
				if(def!=null){
					if(def.getMarcField().equals("461") && isFieldValue(desc,"Livello bibliografico","Spoglio")){
						getParentCache().putParent(desc.getAbout(),	f.getBid(), "",	def.getMarcField());
						partOf = f.getBid();
					}
					if(def.getInverse()!=null){
						List<Field> titles = desc.getFieldArray("Titolo");
						if(titles.size()>0)
							getParentCache().putParent(desc.getAbout(), 
									f.getBid(), titles.get(0).getTextValue(),
									def.getMarcField());
					}
					/*
					if(isPartOfField(def.getMarcField())){
						List<Field> titles = desc.getFieldArray("Titolo");
						if(titles.size()>0)
							getParentCache().putParent(desc.getAbout(), 
									f.getBid(), titles.get(0).getTextValue(),
									def.getMarcField());
					}
					*/
				}
				else{
					logger.error("def not found "+f.getName());
				}
			}
		}
		if(partOf!=null)
			desc.addField("isPartOf", null).setTextValue(partOf);
	}

	protected boolean isFieldValue(OutItem desc, String fieldname, String value) {
		List<Field> values = desc.getFieldArray(fieldname);
		if(values!=null){
			for (Field field : values) {
				if(field.getTextValue().equalsIgnoreCase(value))
					return true;
			}
		}
		return false;
	}

	@Override
	public boolean isPartOfField(String fieldname) {
		return super.isPartOfField(fieldname) || fieldname.equals("463");
	}

	@Override
	public String getDefaultSeparator() {
		return " | ";
	}
	
	

}
