package com.gruppometa.unimarc.output;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrInputDocument;

import com.fasterxml.jackson.core.JsonFactory;
import com.gruppometa.unimarc.mapping.MappingDefinition;
import com.gruppometa.unimarc.object.DefaultOutItem;
import com.gruppometa.unimarc.object.Field;
import com.gruppometa.unimarc.object.JsonOutField;
import com.gruppometa.unimarc.object.JsonOutItem;
import com.gruppometa.unimarc.object.JsonValue;
import com.gruppometa.unimarc.object.LegameElement;
import com.gruppometa.unimarc.object.OutItem;
import com.gruppometa.unimarc.object.Output;
import com.gruppometa.unimarc.profile.ParentCache;
import com.gruppometa.unimarc.profile.Profile;
import com.gruppometa.unimarc.profile.XmlProfile;
import org.apache.solr.common.util.DateUtil;

public class SolrOutputFormatter  extends BaseOutFormatter{
	protected String solrUrl;
	protected List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
	protected ConcurrentUpdateSolrClient server = null;
    protected BlockingQueue<List<OutItem>> issuesQueue = new ArrayBlockingQueue<List<OutItem>>(1000);
    protected Consumer consumer = null;
	protected boolean filter4Fe = true;
    protected int counter=0;
    protected boolean usethread = false;
    public static final String SUFFIX_FOR_SORTFIELD = "_sort_s";

	
	public SolrOutputFormatter(Output output) {	
		this.output = output;
		this.bufferSize = 5000;
		roleMap = //IssuedAndLanguageNormalizer.loadLanguageHashTabbed("/list.normalize.ruoli.txt");
				normalizer.getMap("leta", false);
	}
	
	@Override
	public void notifyAdd(Profile profile) throws IOException{
		if(usethread){
			if(consumer==null){
				consumer = new Consumer("test1", issuesQueue);
				Thread thread = new Thread(consumer);
				thread.start();
			}
			counter++;
			this.profile = profile;
			/**
			 * flush to queue
			 */
			if(output.getItems().size()==bufferSize){
				List<OutItem> items = new ArrayList<OutItem>();
				for (OutItem outItem : output.getItems()) {
					items.add(outItem);
				}
				issuesQueue.add(items);
				output.getItems().clear();
			}
		}
		else{
			counter++;
			this.profile = profile;
			if(output.getItems().size()==bufferSize){
				for (OutItem outItem : output.getItems()) {
					toXml(null,outItem);
				}
				output.getItems().clear();
			}

		}
	}
	
	public void toXml(Appendable buf) throws IOException {
		for (Iterator<OutItem> iterator = output.getItems().iterator(); iterator.hasNext();) {
			OutItem type = (OutItem) iterator.next();
			toXml(buf,type);
		}	
	}

	Pattern p = Pattern.compile("(\\d+)");

	public static String getSortFieldName(String fieldname){
		String ret = fieldname;
		if(fieldname.indexOf("_")!=-1){
			ret = fieldname.substring(0, fieldname.lastIndexOf("_")-1)+SUFFIX_FOR_SORTFIELD;
		}
		return ret;
	}

	protected static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd'T'HH:mm:ss'Z'");

	public void clearOlderThan(long time, String filter){
		String add = "";
		if(filter!=null && filter.length()>0)
			add = " AND "+filter;
		String timeStamp = null;
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		//timeStamp = simpleDateFormat.format(calendar.getTime());
		try {
			StringWriter stringWriter = new StringWriter();
			DateUtil.formatDate(new Date(time), calendar, stringWriter);
			timeStamp = stringWriter.toString();
			String query = "timestamp:[* TO "+timeStamp+"]"+add;
			UpdateResponse resp = server.deleteByQuery(query);
			if(resp.getStatus()== 500){
				logger.error("Delete failed.");
			}
			else {
				logger.debug("Deleted by query:" + query);
				server.commit();
			}
		} catch (Exception e) {
			logger.error("",e);
		}
	}

	@Override
	protected void toXml(Appendable buf, OutItem outitem) throws IOException {
		if(outitem==null)
			return;
		SolrInputDocument doc = new SolrInputDocument();
		doc.setField("id", outitem.getAbout());
		doc.setField("mapversion",((XmlProfile)profile).getMapVersion());
		doc.setField("file",((XmlProfile)profile).getFilename());
		doc.setField("timestamp", new  Date());
		for (Iterator<Field> iterator = outitem.getFields().iterator(); iterator.hasNext();) {
			Field field = (Field) iterator.next();
			MappingDefinition def = ((XmlProfile)profile).getDefinition(field.getName());
			/**
			 * name + _html_nxs o _html_nxss - html non indicizzato in text
			 * name + _facet_ss - solo faccetta
			 * name + _s o _ss - stringhe normali
			 * name + _t o _txt (multiple) - per la ricerca avanzata da definire 
			 * TODO e le date? "year" _i o _is
			 */
			String fieldName = getSolrName(field.getName(),def);
			String fieldNameTxt = getSolrTxtName(field.getName(),def);
			String fieldName_int = null;
			String copyTo = null;
			String fieldNameSortField = null;
			String copyTo2 = null;
			if(fieldName==null)
				continue;
			boolean createHtml = true;
			if(def!=null && def.isSortField()){
				if(def.getSortFieldName()!=null)
					fieldNameSortField = def.getSortFieldName();
				else
					fieldNameSortField = getSortFieldName(fieldName);
			}
			String searchValue = "campi personalizzati";
			if(def!=null &&	((def.getGroup()!=null && def.getGroup().toLowerCase().contains(searchValue))
			    ||(def.getGroup2()!=null && def.getGroup2().toLowerCase().contains(searchValue)))){
				createHtml = false;
			}
			if(def!=null && def.getCopyTo()!=null){
				MappingDefinition defCopyTo = ((XmlProfile)profile).getDefinition(def.getCopyTo());
				copyTo = getSolrTxtName(def.getCopyTo(),defCopyTo);
				if(defCopyTo.getCopyTo()!=null){
					String fieldToCopy2 = defCopyTo.getCopyTo();
					defCopyTo = ((XmlProfile)profile).getDefinition(fieldToCopy2);
					if(defCopyTo!=null) {
						copyTo2 = getSolrTxtName(defCopyTo.getCopyTo(), defCopyTo);
					}
					else{
						logger.warn("Cannot find field '"+fieldToCopy2+"'.");
					}
				}
			}
			if(def!=null && def.getType()!=null && def.getType().equals("year")){
				//logger.debug("Find year for "+def.getDestination());
				int pos = 3;
				if(fieldName.endsWith("_s"))
					pos = 2;
				fieldName_int = fieldName.substring(0, fieldName.length()-pos)+
						(pos==2?"_i":"_is");
			}
			String fieldNameHtml = getSolrFieldName(field.getName(),"t","txt","html_nx",def);
			String fieldNameFacet = getFacetName(def);
			List<JsonValue> values = getValuesAsList(outitem, field.getName());
			for (JsonValue jsonValue : values) {
				/**
				 * salto doppioni
				 */
				if(doc.getFieldValues(fieldName)!=null && 
						doc.getFieldValues(fieldName).contains(jsonValue.getPlain()))
					continue;
				/**
				 * se c'è HTML da salvare
				 */
				if(jsonValue.getHtml()!=null && createHtml){
					doc.addField(fieldNameHtml,jsonValue.getHtml());
				}
				/**
				 * plain text come stringa
				 */
				doc.addField(fieldName,jsonValue.getPlain());
				/**
				 * sort field
				 */
				if(fieldNameSortField!=null){
					String v = (String)doc.getFieldValue(fieldNameSortField);
					doc.setField(fieldNameSortField, (v!=null?v:"")+" "+ jsonValue.getPlain());
				}
				/**
				 * copyTo feature
				 */
				if(copyTo!=null){
					doc.addField(copyTo,jsonValue.getPlain());
				}
				if(copyTo2!=null){
					doc.addField(copyTo2,jsonValue.getPlain());
				}
				/**
				 * campo per ricerca fulltext
				 */
				if(fieldNameTxt!=null && !fieldName.equals(fieldNameTxt))
					doc.addField(fieldNameTxt,jsonValue.getPlain());
				/**
				 * numeri (year)
				 */
				if(fieldName_int!=null){
					String v = jsonValue.getPlain();
					Matcher m = p.matcher(v);
					if (m.find()) {
						if(doc.getFieldValues(fieldName_int)==null || 
								!doc.getFieldValues(fieldName_int).contains(m.group(1)))
							doc.addField(fieldName_int,m.group(1));
					}
					else{
						//logger.debug("no int for value '"+v+"'");
					}
				}
				/**
				 * facetta
				 */
				if(fieldNameFacet!=null){
					doc.addField(fieldNameFacet,jsonValue.getPlain());
				}
				/**
				 * binario (per unimarc)
				 */
				if(jsonValue.getBinary()!=null){
					doc.addField(makeSolrName(field.getName())+"_binary", jsonValue.getBinary());
				}
			}
		}
		doc = filterDoc(doc);
		docs.add(doc);
		if(docs.size()==bufferSize){
			commitDocs(true);
		}
	}

	protected SolrInputDocument filterDoc(SolrInputDocument doc){
		return doc;
    }

	public String getSolrName(String field,MappingDefinition def){
		if(def!=null && def.getDestination().equals("Unimarc"))
			return getSolrTxtName(field,def);
		return getSolrFieldName(field,"",def);
	}
	public String getSolrTxtName(String field,MappingDefinition def){
		return getSolrFieldName(field,"t","txt","",def);
	}

	public String getFacetName(MappingDefinition def){
		String fieldNameFacet = null;
		/**
		 * serve un campo aggiuntivo
		 */
		if(def!=null && def.getFacet()!=null && def.getFacet().length()>0 ){
			fieldNameFacet = makeSolrName( def.getFacet() )+"_facet_ss";
		}
		/**
		  * 	non serve un campo aggiuntivo per le facette
		  */
		if(def!=null && def.getFacet()!=null && def.getFacet().length()==0 ){
			fieldNameFacet = null;
		}
		return fieldNameFacet;	
	} 
		
	public String makeSolrName(String name){
		if(name==null)
			return null;
		return name
				.replace(" ", "_")
				.replace("'", "_")
				.replace("-", "_")
				.replace("]", "_")
				.replace("[", "_")
				.replace("(", "_")
				.replace(")", "_")
				.replace(":", "_")
				.replace("/", "_")
				.replace(",", "_")
				.replace("__", "_")
				.toLowerCase();		
	}
	
	public String getSolrFieldName(String name,String suffix1, MappingDefinition def) {
		return getSolrFieldName(name, "s","ss",suffix1, def);
	}
	protected Set<String> notFounds = new HashSet<String>();

	public String getSolrFieldName(String name,String suffix, String suffixx,
		String suffix1, MappingDefinition def) {
		if(name==null)
			return null;
		if(def!=null && def.getSolrFieldname()!=null){
			return def.getSolrFieldname();
		}
		if(filter4Fe && def!=null){
			if(!def.isIs4Fe() && def.getVistaIsbd()==-1 && def.getVistaEtichette()==-1 && def.getFacet()==null && def.getVistaShort()==-1)
				return null;
		}
		String suffixFinal = "_"+suffix1+suffix;
		if(def==null){
			if(notFounds.contains(name)) {
				logger.warn("Field def not found for '" + name + "'.");
				notFounds.add(name);
			}
		}
		if(def!=null && def.isMultiple())
			suffixFinal="_"+suffix1+suffixx;
		return makeSolrName(name)+suffixFinal;
		//String fn = ((XmlProfile)profile).getMarcFieldFromDestination(name);
		//return (fn!=null?("field_"+fn):name).toLowerCase();
	}

	public String getSolrUrl() {
		return solrUrl;
	}

	public void setSolrUrl(String solrUrl) {
		this.solrUrl = solrUrl;
	}

	@Override
	protected void writeInit(Appendable buf) throws IOException {
		int iCores = Runtime.getRuntime().availableProcessors();
		server =  new ConcurrentUpdateSolrClient(solrUrl,2056,iCores/2);
	}

	protected void commitDocs(boolean commit){
		if(docs.size()>0){
			try {
				long now = System.currentTimeMillis();
				server.add(docs);
				if(commit)
					server.commit();
				logger.debug("Commit time "+(System.currentTimeMillis()-now)+"ms. Counter="+counter);
			} catch (Exception e) {
				logger.error("",e);
			}
			docs.clear();
		}		
		else if(commit){
			try {
				server.commit();
			} catch (Exception e) {
				logger.error("",e);
			}
		}
	}
	
	@Override
	protected void writeEnd(Appendable buf) throws IOException {
		commitDocs(true);
		rewriteItems();		
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
					for(OutItem item: list){	
						try {
							toXml(null,item);
						} catch (IOException e) {
							logger.error("",e);
						}
					}
				}				
			}
		}
		public void stop(){
			stopped = true;
		}
	}

	protected void rewriteItems(){
		ParentCache parentCache = profile.getParentCache();
		List<String> parents =  parentCache.getAllParents();
		logger.debug("Found "+parents.size()+" parents to write.");
		for (String parent : parents) {
			List<LegameElement> legami = parentCache.getParentInfo(parent);
			SolrQuery query = new SolrQuery();
			query.setQuery("id:\""+ ClientUtils.escapeQueryChars(parent)+"\"");
			SolrInputDocument docIn = null;
			 try {
				QueryResponse resp = server.query(query);
				if(resp.getResults().size()==0){
					logger.error("Document not found '"+parent+"'");
					continue;
				}					
				docIn = ClientUtils.toSolrInputDocument(resp.getResults().get(0));
			} catch (Exception e) {
				logger.error(""+parent,e);
				continue;
			}
			boolean hasAdded = false;
			JsonOutItem item = new JsonOutItem();
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
			for (Iterator<JsonOutField> iterator = item.getFields().iterator(); iterator.hasNext();) {
				JsonOutField field = (JsonOutField) iterator.next();
				MappingDefinition def = ((XmlProfile)profile).getDefinition(field.getName());
				String fieldName = getSolrName(field.getName(),def);
				String fieldNameTxt = getSolrTxtName(field.getName(),def);
				String fieldNameHtml = getSolrFieldName(field.getName(),"html_nx",def);
				for (JsonValue jsonValue : field.getValues()) {
					if(jsonValue.getHtml()!=null && !contains(docIn, fieldNameHtml, jsonValue.getHtml()))
						docIn.addField(fieldNameHtml,jsonValue.getHtml());
					if(!contains(docIn, fieldName, jsonValue.getPlain()))
						docIn.addField(fieldName,jsonValue.getPlain());
					if(fieldNameTxt!=null && !contains(docIn, fieldNameTxt, jsonValue.getPlain()))
						docIn.addField(fieldNameTxt,jsonValue.getPlain());				
				}				
			}
			try {
				docIn.setField("timestamp", new  Date());
				server.add(docIn);
			} catch (Exception e) {
				logger.error("",e);
			}
		}
		try {
			server.commit();
		} catch (Exception e) {
			logger.error("",e);
		}
		
	}
	
	protected boolean contains(SolrInputDocument doc, String fieldName, String value){
		if(doc.getFieldValues(fieldName)==null)
			return false;
		for (Iterator<Object> iterator = doc.getFieldValues(fieldName).iterator(); iterator.hasNext();) {
			Object v = (Object) iterator.next();
			if (v  instanceof String) {
				String new_name = (String)  v;
				if(new_name.equals(value))
					return true;
			}
		}
		return false;
	}
}
