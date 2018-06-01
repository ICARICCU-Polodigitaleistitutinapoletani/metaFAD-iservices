package com.gruppometa.poloigitale.services.components;

import java.io.IOException;
import java.util.*;

import com.gruppometa.metasearch.data.*;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.gruppometa.unimarc.mapping.MappingDefinition;
import com.gruppometa.unimarc.object.DefaultOutput;
import com.gruppometa.unimarc.output.SolrOutputFormatter;
import com.gruppometa.unimarc.profile.XmlProfile;


@Component
@ConfigurationProperties(prefix = "mappedResponseCreator")
public class MappedResponseCreator extends SolrResponseCreator implements InitializingBean{
	protected HashMap<String,XmlProfile> profiles = new HashMap<String,XmlProfile>();

	public HashMap<String, String> getProfilesNames() {
		return profilesNames;
	}

	public void setProfilesNames(HashMap<String, String> profilesNames) {
		this.profilesNames = profilesNames;
	}

	protected HashMap<String, String> profilesNames;
	protected HashMap<String, String> whiteLists4Facets;

	public HashMap<String, String> getFacetsValueMap() {
		return facetsValueMap;
	}

	public void setFacetsValueMap(HashMap<String, String> facetsValueMap) {
		this.facetsValueMap = facetsValueMap;
	}

	protected HashMap<String, String> facetsValueMap;

	public HashMap<String, String> getLabel4CustomFacetMap() {
		return label4CustomFacetMap;
	}

	public void setLabel4CustomFacetMap(HashMap<String, String> label4CustomFacetMap) {
		this.label4CustomFacetMap = label4CustomFacetMap;
	}

	protected HashMap<String, String> label4CustomFacetMap;

	public HashMap<String, String> getWhiteLists4Facets() {
		return whiteLists4Facets;
	}

	public void setWhiteLists4Facets(HashMap<String, String> whiteLists4Facets) {
		this.whiteLists4Facets = whiteLists4Facets;
	}

	protected static final Logger logger = LoggerFactory.getLogger(MappedResponseCreator.class);
	protected HashMap<String,HashMap<String, MappingDefinition>> facetsMaps = new HashMap<String,HashMap<String,MappingDefinition>>();
	protected HashMap<String,HashMap<String, String>> facetsLabelMaps = new HashMap<String,HashMap<String,String>>();
	protected SolrOutputFormatter solrOutputFormatter = new SolrOutputFormatter(new DefaultOutput());
	protected HashMap<String, String> labelmap;
	public HashMap<String, String> getLabelmap() {
		return labelmap;
	}

	public HashMap<String, String> getFacetsLabelMapSecondLevel(String profileName) {
		return facetsLabelMaps.get(profileName);
	}

	public void setLabelmap(HashMap<String, String> labelmap) {
		this.labelmap = labelmap;
	}

	public MappedResponseCreator(){
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if(profilesNames!=null) {
			for (String p : profilesNames.keySet()) {
				XmlProfile profile = new XmlProfile(profilesNames.get(p));
				profile.init();
				try {
					solrOutputFormatter.notifyAdd(profile);
				} catch (IOException e) {
					logger.error("", e);
				}
				profile.getDefinitions();
				profiles.put(p, profile);
			}
		}
	}

	@Override
	public void addFields(SolrQuery solrQuery, String profileName,String viewName, boolean withHtml){
		List<MappingDefinition> defs = getDefinitions(viewName,profileName);
		for (MappingDefinition mappingDefinition : defs) {
			String n = getSolrName(mappingDefinition);
			solrQuery.addField(n);
			if(!viewName.equals("isbd") && withHtml){
				String nHtml = getSolrName4Html(mappingDefinition);
				solrQuery.addField(nHtml);
			}
		}
	}

	@Override
	public void addFacetFields(SolrQuery solrQuery,String profileName, String viewName, boolean secondLevel){
		if(getFacetsMap(profileName).size()>0){
			solrQuery.setFacet(true);
			for (String key : getFacetsMap(profileName).keySet()) {
				solrQuery.addFacetField(key);
			}
		}
		/**
		 * quelli del secondo livello non sono faccette secondo il issue POLOBE-315
		 */
		if(secondLevel) {
			if (getFacetsLabelMapSecondLevel(profileName).size() > 0) {
				solrQuery.setFacet(true);
				for (String key : getFacetsLabelMapSecondLevel(profileName).keySet()) {
					solrQuery.addFacetField(key);
				}
			}
		}
	}

	@Override
	public String getSolrNameTextFromFacet(String facetName){
		String end = facetName.lastIndexOf("_")!=-1?facetName.substring(facetName.lastIndexOf("_")):"";
		if(end.startsWith("_s"))
			return facetName.substring(0,facetName.length()-end.length())+(end.length()==2?"_t":"_txt");
		logger.warn("FacetName not found "+facetName);
		return facetName;
	}

	@Override
	public String getSolrNameForFacet(String destinationName, String profileName) throws FieldException{
		String ret = null;
		for (String key  : facetsMaps.get(profileName).keySet()) {
			if(facetsMaps.get(profileName).get(key).getDestination().equals(destinationName))
				return key;
		}
		MappingDefinition def = getMappginDefinition(destinationName,profileName);
		if(def!=null) {
			if(def.getType()!=null && def.getType().equals("text")){
				throw new FieldException("Field '"+destinationName+"' is text-field.");
			}
			ret = getSolrName(def);
		}
		return ret;
	}

	protected static boolean isInit = false;
	@Override
	public void init(){
		if(!isInit) {
			for(String p: profilesNames.keySet())
				getDefinitions("short",p);
			isInit = true;
		}
	}

	public List<MappingDefinition> getDefinitions(String viewName, String profileName){
		MappingDefinition[]  defs = profiles.get(profileName).getDefinitions();
		HashSet<String> fatti = new HashSet<String>();
		HashMap<String, String> facetsLabelMap = facetsLabelMaps.get(profileName);
		if(facetsLabelMap==null){
			facetsLabelMap = new HashMap<String, String>();
			facetsLabelMaps.put(profileName,facetsLabelMap);
		}
		HashMap<String, MappingDefinition> facetsMap = facetsMaps.get(profileName);
		if(facetsMap==null){
			facetsMap = new HashMap<String, MappingDefinition>();
			facetsMaps.put(profileName,facetsMap);
		}

		List<MappingDefinition> mappings = new ArrayList<MappingDefinition>();
		if(defs==null){
			logger.error("Mancanti le definizioni del profilo '"+profileName+"'.");
		}
		for (int i = 0; i < defs.length; i++) {
			if(fatti.contains(defs[i].getDestination()))
				continue;
			if(viewName.equals("short") && defs[i].getVistaShort()!=-1)
				mappings.add(defs[i]);
			if(viewName.equals("full") && defs[i].getVistaEtichette()>0)
				mappings.add(defs[i]);
			if(viewName.equals("search") &&
					(defs[i].isSearchField()
							|| (defs[i].getVocabulary()!=null && defs[i].getVocabulary().length()>0)
							|| defs[i].isFacets()))
				mappings.add(defs[i]);
			if(viewName.equals("unimarc") && defs[i].getDestination().toLowerCase().startsWith("unimarc"))
				mappings.add(defs[i]);
			if(viewName.equals("isbd") && defs[i].getVistaIsbd()>0)
				mappings.add(defs[i]);
			if(defs[i].isFacets()) {
				Map<String, String> map2 = new HashMap<String, String>();
				for (MappingDefinition mappingDefinition2 : defs[i].getSubDefs()) {
					String key = getSolrName(defs[i], mappingDefinition2);
					if (map2.containsKey(key)) {
						continue;
					}
					facetsLabelMap.put(key, defs[i].getDestination() + " - " + mappingDefinition2.getDestination());
				}
			}
			if(defs[i].getFacet()!=null){
				String facetName = null; 
				if(defs[i].getFacet().length()==0){
					facetName = solrOutputFormatter.getSolrFieldName(defs[i].getDestination(),"", defs[i]);					
				}
				else{
					facetName = solrOutputFormatter.makeSolrName( defs[i].getFacet())+"_facet_ss"; 
				}
				if(facetName!=null)
					facetsMap.put(facetName, defs[i]);
			}
			fatti.add(defs[i].getDestination());
		}
		if(viewName.equals("short")){
			mappings.sort(new Comparator<MappingDefinition>() {
				@Override
				public int compare(MappingDefinition o1, MappingDefinition o2) {
					return Double.compare(o1.getVistaShort(), o2.getVistaShort());
				}
			});
		}
		if(viewName.equals("isbd")){
			mappings.sort(new Comparator<MappingDefinition>() {
				@Override
				public int compare(MappingDefinition o1, MappingDefinition o2) {
					return Double.compare(o1.getVistaIsbd(), o2.getVistaIsbd());
				}
			});
		}
		if(viewName.equals("full")){
			mappings.sort(new Comparator<MappingDefinition>() {
				@Override
				public int compare(MappingDefinition o1, MappingDefinition o2) {
					return Double.compare(o1.getVistaEtichette(), o2.getVistaEtichette());
				}
			});
		}
		return mappings;
	}
	
	public String getSolrTxtName(MappingDefinition def){
		return solrOutputFormatter.getSolrTxtName(def.getDestination(),def);
	}
	public String getSolrName(MappingDefinition def) {
		return getSolrName(def,false);
	}

	public String getSolrName(MappingDefinition def, boolean yearable){
		String fieldName = solrOutputFormatter.getSolrFieldName(def.getDestination(),"", def);
		if(yearable && def!=null && def.getType()!=null && def.getType().equals("year")){
			//logger.debug("Find year for "+def.getDestination());
			int pos = 3;
			if(fieldName.endsWith("_s"))
				pos = 2;
			return fieldName.substring(0, fieldName.length()-pos)+
					(pos==2?"_i":"_is");
		}
		if(def.isSubfield() && def.getMarcField()!=null && def.getMarcField().length()>0)
			fieldName = def.getMarcField()+"_"+fieldName;
		return fieldName;
	}

	public String getSolrName4Html(MappingDefinition def){
		return solrOutputFormatter.getSolrFieldName(def.getDestination(),"t","txt","html_nx", def);
	}

	protected String getValue(SolrDocument doc, String fieldName){
		String ret = "";
		if(doc.getFieldValues(fieldName)==null)
			return ret;
		for (Object ob : doc.getFieldValues(fieldName)) {
			if(ret.length()>0)
				ret += ", ";
			ret += (String)ob;
		}
		return ret;
	}

	public List<Document> createDocs(QueryResponse resp, List<String> views, String profileName){
		List<Document> docs = new ArrayList<Document>();
		for(org.apache.solr.common.SolrDocument doc: resp.getResults()){
			Document doc2 = new Document();
			doc2.setId((String)doc.getFieldValue("id"));
			if(doc.getFieldValue("score")!=null)
				doc2.setScore((Float)doc.getFieldValue("score"));
			for (String view : views) {
				View viewPanel = new View();
				viewPanel.setViewType(view);
				doc2.getNodes().add(viewPanel);
				FieldGroup thePanel = viewPanel; 
				boolean hasGroups = !view.equals("isbd") && !view.equals("short");
				String lastGroup = "";
				List<MappingDefinition> defs =  getDefinitions(view, profileName);
				for (MappingDefinition mappingDefinition : defs) {
					String n = getSolrName(mappingDefinition);
					String nHtml = getSolrName4Html(mappingDefinition);
					boolean isHtml = false;
					if(nHtml!=null && doc.getFieldValues(nHtml)!=null){
						n = nHtml;
						isHtml = true;
					}
					if(n!=null && doc.getFieldValues(n)!=null){
						if(mappingDefinition.isDocAttribute()){
							String valBefore = doc2.getAttributes().get(mappingDefinition.getDestination());
							if(valBefore!=null && mappingDefinition.getSeparator()!=null)
								valBefore += mappingDefinition.getSeparator();
							else if(valBefore==null)
								valBefore = "";
							doc2.getAttributes().put(mappingDefinition.getDestination(),
									valBefore+getValue(doc,n));
						}
						else {
							Field f = new Field();
							f.setId(mappingDefinition.getDestination());
							String label = mappingDefinition.getLabel() != null ? mappingDefinition.getLabel() : mappingDefinition.getDestination();
							if(mappingDefinition.getFeLabel()!=null && mappingDefinition.getFeLabel().length()>0)
								label = mappingDefinition.getFeLabel();
							f.setLabel(filterLabel(label));
							f.setParentLabel(mappingDefinition.getParentLabel());
							f.setParentId(mappingDefinition.getParent());
							// per le copie e inventari......in FE
							f.setHideLabel(mappingDefinition.isHideLabel());
							if (isHtml)
								f.setMimetype("html");

							if(mappingDefinition.getJoin()!=null){
								String temp = "";
								for (Object ob : doc.getFieldValues(n)) {
									if(temp.length()>0)
										temp+= mappingDefinition.getJoin();
									temp+=ob;
								}
								f.getValues().add(temp);
							}
							else {
								for (Object ob : doc.getFieldValues(n)) {
									f.getValues().add(f.isHideLabel() ? filterBiblioteca(ob) : ob);
									if (f.isHideLabel())
										f.getValues().add("<div class='parent-archive-end'></div>");
								}
							}

							String theGroup = mappingDefinition.getGroup();
							if (mappingDefinition.getGroup2() != null) {
								theGroup = mappingDefinition.getGroup2();
							}
							if (hasGroups && theGroup != null
									&& !theGroup.equals("")
									&& !theGroup.equals(lastGroup)) {
								lastGroup = theGroup;
								FieldGroup group = new FieldGroup();
								group.setLabel(filterGroupLabel(lastGroup));
								viewPanel.getNodes().add(group);
								thePanel = group;
							}
							thePanel.getNodes().add(f);
						}
					}
				}
			}
			docs.add(doc2);
		}
		return docs;
	}

	private Object filterBiblioteca(Object ob) {
		if(!(ob instanceof String))
			return ob;
		else{
			String str = (String)ob;
			if(str.contains("<div class='label'>Biblioteca</div>")){
				str = str.replaceFirst("\n", "\n<div class='parent-archive'>Collocazioni e inventari</div>");
			}
			return str;
		}
	}

	private String filterGroupLabel(String lastGroup) {
		if(labelmap.get(solrOutputFormatter.makeSolrName(lastGroup))!=null)
			return labelmap.get(solrOutputFormatter.makeSolrName(lastGroup));
		else
			return lastGroup;
	}

	protected String filterLabel(String destination) {
		if(destination==null)
			return null;
		String s = " [sintetico]";
		String s2 = " [Faccetta]";
		if (destination.endsWith(s))
			return destination.substring(0, destination.length()-s.length());
		else if(destination.endsWith(s2))
			return destination.substring(0, destination.length()-s2.length());
		else
			return destination;
	}

	public String getFilteredValue(String profilename, String facet_id, String value){
		String key = profilename+"::"+facet_id+"::"+value;
		if(getFacetsValueMap()!=null && getFacetsValueMap().get(key)!=null)
			return getFacetsValueMap().get(key);
		else
			return value;
	}

	public String getOriginalOfFilteredValue(String profilename, String facet_id, String value){
		String key = profilename+"::"+facet_id+"::";
		if(getFacetsValueMap()==null)
			return value;
		for (String k: getFacetsValueMap().keySet()){
			if(getFacetsValueMap().get(k).equals(value) && k.startsWith(key))
				return k.substring(key.length());
		}
		return value;
	}

	@Override
	public List<FacetField> createFacets(QueryResponse resp, List<String> views, String profileName) {
		HashMap<String, String> facetsLabelMap = facetsLabelMaps.get(profileName);
		if(facetsLabelMap==null){
			facetsLabelMap = new HashMap<String, String>();
			facetsLabelMaps.put(profileName,facetsLabelMap);
		}
		HashMap<String, MappingDefinition> facetsMap = facetsMaps.get(profileName);
		if(facetsMap==null){
			facetsMap = new HashMap<String, MappingDefinition>();
			facetsMaps.put(profileName,facetsMap);
		}

		List<FacetField> facets = new ArrayList<FacetField>();
		if(resp.getFacetFields()==null)
			return facets;
		for (org.apache.solr.client.solrj.response.FacetField facetField : resp.getFacetFields()) {
			if(facetField.getValues().size()==0)
				continue;
			FacetField f = new FacetField();
			f.setId(facetField.getName());
			List<String> whiteList = null;
			if(whiteLists4Facets!=null && whiteLists4Facets.get(f.getId())!=null){
				whiteList = Arrays.asList(whiteLists4Facets.get(f.getId()).split("\\|"));
			}
			List<com.gruppometa.metasearch.data.Count> counts = new ArrayList<com.gruppometa.metasearch.data.Count>();
			for(Count c: facetField.getValues()){
				if(whiteList==null || whiteList.contains(c.getName().toLowerCase()))
					counts.add(new com.gruppometa.metasearch.data.Count(c.getCount(),
						getFilteredValue(profileName,facetField.getName(),c.getName())
					));
			}
			f.setValues(counts);
			MappingDefinition def = facetsMap.get(f.getId());
			if(def!=null){
				String label = def.getFacetLabel()!=null?def.getFacetLabel():def.getLabel();
				if(f.getId().endsWith("_facet_ss")) {
					if(def.getFacet()!=null && def.getFacet().endsWith(" facet") && label!=null)
						f.setLabel(label);
					else
						f.setLabel(def.getFacet());
				}
				else if(label!=null){
					f.setLabel(label);
				}
				else
					f.setLabel(def.getDestination());
			}
			else{
				if(facetsLabelMap.get(f.getId())!=null){
					f.setLabel(facetsLabelMap.get(f.getId()));
				}
				else {
					logger.warn("Facet def not found, probably no facet: " + f.getId());
					f.setLabel(f.getId());
				}
			}
			/**
			 * potrebbe essere filtrati tutti
			 */
			if(label4CustomFacetMap.get(profileName+"::"+f.getLabel())!=null){
				f.setLabel(label4CustomFacetMap.get(profileName+"::"+f.getLabel()));
			}
			if(f.getValues().size()>0)
				facets.add(f);
		}
		facets.sort(new MyComparator(facetsMap));
		//for(FacetField f: facets)
		//	logger.debug(""+f.getId()+" ->"+ (facetsMap.get(f.getId())!=null?facetsMap.get(f.getId()).getFacetOrder():"0"));
		return facets;
	}


	protected class MyComparator implements Comparator<FacetField>{
		protected HashMap<String, MappingDefinition> facetsMap;
		public MyComparator(HashMap<String, MappingDefinition> facetsMap) {
			this.facetsMap = facetsMap;
		}
		@Override
		public int compare(FacetField o1, FacetField o2) {	
			if(facetsMap.get(o1.getId())==null || facetsMap.get(o2.getId())==null)
				return 0;
			double v1 = facetsMap.get(o1.getId()).getFacetOrder();
			double v2 = facetsMap.get(o2.getId()).getFacetOrder();
			return v1<v2?-1:(v1==v2?0:1);
		}
		
	}
	

	public HashMap<String, MappingDefinition> getFacetsMap(String profileName) {
		return facetsMaps.get(profileName);
	}

	public MappingDefinition getMappginDefinition(String destiantion,String profileName){
		return profiles.get(profileName).getDefinition(destiantion);
	}
	public String getSolrName(MappingDefinition mappingDefinition, MappingDefinition mappingDefinition2) {
		return solrOutputFormatter.makeSolrName(mappingDefinition.getMarcField()+"_"+
				mappingDefinition2.getDestination())+	
				(mappingDefinition2.isMultiple()?"_ss":"_s");
	}
}
