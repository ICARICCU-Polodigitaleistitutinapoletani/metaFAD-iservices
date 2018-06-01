package com.gruppometa.unimarc.output;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringEscapeUtils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.gruppometa.unimarc.mapping.MappingDefinition;
import com.gruppometa.unimarc.object.DefaultField;
import com.gruppometa.unimarc.object.Field;
import com.gruppometa.unimarc.object.JsonOutField;
import com.gruppometa.unimarc.object.JsonOutItem;
import com.gruppometa.unimarc.object.JsonValue;
import com.gruppometa.unimarc.object.LegameElement;
import com.gruppometa.unimarc.object.OutItem;
import com.gruppometa.unimarc.object.Output;
import com.gruppometa.unimarc.profile.ParentCache;
import com.gruppometa.unimarc.profile.Profile;

public class JsonOutputFormatter  extends BaseOutFormatter{
	public static final String CONTIENE_FIELD = "Comprende";
	public static final boolean CHECK_LEGAMI = false;
	protected String directory=null;
	protected ObjectMapper mapper = new ObjectMapper();
    protected BlockingQueue<List<OutItem>> issuesQueue = new ArrayBlockingQueue<List<OutItem>>(10);
    protected Consumer consumer = null;
    protected int counter=0;
    protected int dirSize= 10000;
    protected boolean oneThread = true;

	public String getDirectory() {
		return directory;
	}

	@Override
	public void notifyAdd(Profile profile) throws IOException{
		if(consumer==null && !oneThread){
			consumer = new Consumer("test1", issuesQueue);
			Thread thread = new Thread(consumer);
			thread.start();
		}
		counter++;
		this.profile = profile;
		/**
		 * flush to queue
		 */
		if(output.getItems().size()>=bufferSize){
			List<OutItem> items = new ArrayList<OutItem>();
			for (OutItem outItem : output.getItems()) {
				items.add(outItem);
			}
			if(oneThread)
				writeList(items);
			else {
				while(issuesQueue.remainingCapacity()==0) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						logger.error("",e);
					}
				}
				issuesQueue.add(items);
			}
			output.getItems().clear();
		}		
	}

	
	public void setDirectory(String directory) {
		this.directory = directory;
		if(!new File(directory).exists()){
			new File(directory).mkdirs();
		}
	}

	public JsonOutputFormatter(Output output) {
		this.output = output;
		this.bufferSize = 100;
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
		mapper.setSerializationInclusion(Include.NON_NULL);
		roleMap = //IssuedAndLanguageNormalizer.loadLanguageHashTabbed("/list.normalize.ruoli.txt");
				normalizer.getMap("leta", false);
	} 
	
	public void toXml(Appendable buf) throws IOException {
		for (Iterator<OutItem> iterator = output.getItems().iterator(); iterator.hasNext();) {
			OutItem item = (OutItem) iterator.next();			
			toXml(buf,item);
		}	
	}

	protected String getSubDir(){
		int i = (counter / dirSize);
		String str = ""+i;
		while(str.length()<6)
			str = "0"+str; 
		//logger.error("str="+str);
		return str;
	}
	@Override
	protected void toXml(Appendable buf, OutItem outitem) throws IOException {
		if(directory!=null){
			File dir = new File(directory,getSubDir());
			if(!dir.exists())
				dir.mkdirs();
			BufferedWriter buf2 = new BufferedWriter(new OutputStreamWriter(  
					new FileOutputStream(new File(dir,outitem.getAbout()+".json")),
					"UTF-8")
					);
			buf2.append(mapper.writeValueAsString(toXmlInner(outitem)));
			buf2.close();
		}
		else
			buf.append(mapper.writeValueAsString(toXmlInner(outitem)));
	}
	
	protected JsonOutItem toXmlInner(OutItem outitem) throws IOException {
		if(outitem==null)
			return null;
		JsonOutItem jitem = new JsonOutItem();
		jitem.setFilename(profile.getFilename());
		jitem.setId(outitem.getAbout());
		jitem.setMapVersion(profile.getMapVersion());
		jitem.setCreated(new Timestamp(System.currentTimeMillis()));
		HashMap<String, String> fatti = new HashMap<String, String>();
		List<Field> fields = new ArrayList<Field>();
		List<Field> fieldsIsbd = new ArrayList<Field>();
		
		/**
		 * et univok fields
		 */
		for (Iterator<Field> iterator = outitem.getFields().iterator(); iterator.hasNext();) {
			Field field = (Field) iterator.next();
			if(fatti.get(field.getName())==null){
				Field f  = new DefaultField(field.getName(), null);
				fields.add(f);
				fatti.put(field.getName(),field.getName());
			}
			
		}	
		
		/**
		 * get field definitions
		 */
		for (Field field : fields) {
			MappingDefinition def = profile.getDefinition(field.getName());
			if(def!=null){
				field.setOrderEtichette(def.getVistaEtichette());
				field.setOrderIsbd(def.getVistaIsbd());
				field.setGroup(def.getGroup());
				if(field.getOrderIsbd()>0){
					fieldsIsbd.add(field);
				}
			}
			else{
				//logger.warn("Definition not found for '"+field.getName()+"'.");
			}
		}
		
		/**
		 * sort fields
		 */
		fieldsIsbd.sort(new Comparator<Field>(){
			public int compare(Field o1, Field o2) {
				return o1.getOrderIsbd()<o2.getOrderIsbd()?-1:(o1.getOrderIsbd()>o2.getOrderIsbd()?1:0);				
			}});

		fields.sort(new Comparator<Field>(){
			public int compare(Field o1, Field o2) {
				return o1.getOrderEtichette()<o2.getOrderEtichette()?-1:(o1.getOrderEtichette()>o2.getOrderEtichette()?1:0);				
			}});
		
		/**
		 * map fields to output
		 */
		for (Field f : fields) {
			JsonOutField jfield = new JsonOutField();
			jfield.setName(filterName(f.getName()));
			jfield.setHidden(f.getOrderEtichette()<=0);
			jfield.setGroup(f.getGroup());
			if(!jfield.isHidden())
				jfield.setOrder(f.getOrderEtichette());
			jfield.setIsbd(!(f.getOrderIsbd()<=0));
			jfield.setValues(getValuesAsList(outitem, f.getName()));
			jitem.getFields().add(jfield);
		}
		
		/**
		 * construct isbd
		 */
		if(fieldsIsbd.size()>0){
			StringBuffer buf2 = new StringBuffer();
			int i = 0;
			boolean initNotes = false;
			boolean lastWithPoint = false;
			for (Field f : fieldsIsbd) {
				if(!initNotes && f.getName().toLowerCase().startsWith(("note"))){
					if(!lastWithPoint)
						buf2.append(".");
					buf2.append(" ((");
					initNotes= true;
				}
				else if(i>0 && f.getOrderIsbd()>100){
					if(!lastWithPoint)
						buf2.append(".");
					buf2.append(" - ");
				}
				if(f.getOrderIsbd()<=100)
					buf2.append("<b>");
				String str = getHtmlValues(outitem,f.getName());
				if(str.endsWith("."))
					lastWithPoint = true;
				else
					lastWithPoint = false;
				buf2.append(str);
				if(f.getOrderIsbd()<=100)
					buf2.append("</b>");
				if(f.getOrderIsbd()<100)
					buf2.append("<br/>");
				i++;
			}
			if(initNotes)
				buf2.append("))");
			jitem.setIsbd(buf2.toString());
		}
		
		/**
		 * write to buffer
		 */
		return jitem;
	}

	private String getHtmlValues(OutItem outitem, String name) {
		StringBuffer buf = new StringBuffer();
		int i = 0;
		for (Iterator<Field> iterator = outitem.getFields().iterator(); iterator.hasNext();) {
			Field field = (Field) iterator.next();
			if(field.getName().equals(name)){
				if(i>0)
					buf.append(" / ");
				buf.append(StringEscapeUtils.escapeHtml4(field.getTextValue()));
				i++;
			}
		}
		return buf.toString();
	}

	
	

	

	private String filterName(String name) {
		return name;
		//String fn = ((XmlProfile)profile).getMarcFieldFromDestination(name);
		//return (fn!=null?("field_"+fn):name).toLowerCase();
	}
	
	@Override
	protected void writeInit(Appendable buf) throws IOException {
	}

	@Override
	protected void writeEnd(Appendable buf) throws IOException {
		if(consumer!=null)
			consumer.end();
		rewriteItems();
	}
	
	protected void checkLegami(){
		ParentCache parentCache = profile.getParentCache();
		List<String> parents =  parentCache.getAllParents();
		for (String parent : parents) {
			if(getRecordCache().getFilename(parent)==null){
				List<LegameElement> legami = parentCache.getParentInfo(parent);
				for (LegameElement el : legami) {
					logger.error( "Not found\t"+el.getSourceId()+"\t"+el.getType()+"\t"+el.getTargetId());	
				}					
			}
		}
	}
	protected void rewriteItems(){
		if(CHECK_LEGAMI){
			checkLegami();
		}
		if(profile==null){
			logger.error("No profile set.");
			return;
		}
		ParentCache parentCache = profile.getParentCache();
		List<String> parents =  parentCache.getAllParents();
		for (String parent : parents) {
			if(getRecordCache().getFilename(parent)!=null){
				String filename = getRecordCache().getFilename(parent);
				try {
					File file = new File(filename);
					JsonOutItem item = mapper.readValue(file, JsonOutItem.class);
					List<LegameElement> legami = parentCache.getParentInfo(parent);
					boolean hasAdded = false;
					HashMap<String, JsonOutField> fields = new HashMap<String, JsonOutField>();
					for (LegameElement el : legami) {
						if(el.getType().equals("461")){
							if(!hasAdded){
								JsonOutField f = new JsonOutField();
								f.setName("hasParts");
								f.setValues(new ArrayList<JsonValue>());							
								JsonValue value = new JsonValue();
								value.setPlain("true");
								f.getValues().add(value);
								item.getFields().add(f);
								hasAdded = true;
							}
						}
						else if(fields.get(el.getType())==null){
							JsonOutField f = new JsonOutField();
							MappingDefinition defContiene = profile.getDefinitionFromMarcField(el.getType());
							defContiene = profile.getDefinition(defContiene.getInverse());
							f.setName(defContiene.getDestination());
							f.setGroup(defContiene.getGroup());
							f.setOrder(defContiene.getVistaEtichette());
							f.setValues(new ArrayList<JsonValue>());
							fields.put(el.getType(), f);
						}
					}
					for (LegameElement el : legami) {
						if(fields.get(el.getType())!=null){
							JsonValue v = new JsonValue();
							v.setHtml("<a href=\"${page}?BID="+el.getTargetId()+"\">"+
									StringEscapeUtils.escapeHtml4(el.getLabel())+
									"</a>"
									);
							v.setPlain(el.getLabel());
							v.setBid(el.getTargetId());
							fields.get(el.getType()).getValues().add(v);
						}
					}

					/**
					 * position
					 */
					for(String key: fields.keySet()){
						JsonOutField f = fields.get(key);
						int i = 0;
						for(JsonOutField f2: item.getFields()){
							if(f2.getOrder()>f.getOrder()){
								break;
							}
							i++;
						}
						item.getFields().add(i,f);
					}
					/**
					 * write
					 */
					mapper.writeValue(file, item);
					logger.info("Written "+filename);
				} catch (Exception e) {
					logger.error("Errore writing "+filename,e);
				}
			}
			else{
				logger.error("Not found parent: "+parent);
			}
		}
	}

	protected class Consumer implements Runnable{
		protected JsonFactory factory = new JsonFactory();
		protected BlockingQueue<List<OutItem>> listQueue;
		protected boolean stopped = false;
		protected boolean ended = false;
		protected String name = "noname";
		public Consumer(String name, BlockingQueue<List<OutItem>> listQueue){
			this.name = name;
			this.listQueue = listQueue;
		}

		public void end() {
			ended = true;
			while(!listQueue.isEmpty()){
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					logger.error("",e);
				}
			}
		}

		public void run() {
			while(!stopped){
		        List<OutItem> list = null;
				try {
					//logger.debug("consumeIssues["+name+"] poll");
					list = listQueue.poll(250, TimeUnit.MILLISECONDS);
				} catch (Exception e) {
					logger.error("",e);
				}
				if(list==null || list.size()==0){
					if(ended){
						//logger.debug("consumeIssues["+name+"] finished");
						return;
					}
					continue;
				}
				else{
					writeList(list);
				}
			}
		}
		public void stop(){
			stopped = true;
		}
	}


	protected void writeList(List<OutItem> list){
		for(OutItem item: list){
			if(outfile!=null){
				try {
					outfile.append(mapper.writeValueAsString(toXmlInner(item)));
				} catch (Exception e) {
					logger.error("",e);
				}
			}
			else{
				File dir = new File(directory,getSubDir());
				if(!dir.exists())
					dir.mkdir();
				File fileToWrite = new File(dir,item.getAbout()+".json");
				BufferedWriter buf2 = null;
				try {
					buf2 = new BufferedWriter(new OutputStreamWriter(
							new FileOutputStream(fileToWrite),
							"UTF-8")
					);
					buf2.append(mapper.writeValueAsString(toXmlInner(item)));
					getRecordCache().putFilename(item.getAbout(), fileToWrite.getAbsolutePath());
				} catch (Exception e) {
					logger.error("",e);
				}
				finally {
					if(buf2!=null){
						try {
							buf2.close();
						} catch (IOException e) {
							logger.error("",e);
						}
					}
				}
			}
		}

	}
}
