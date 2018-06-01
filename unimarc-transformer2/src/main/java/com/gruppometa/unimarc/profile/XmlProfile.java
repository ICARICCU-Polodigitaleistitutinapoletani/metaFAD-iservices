package com.gruppometa.unimarc.profile;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xpath.XPathAPI;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.gruppometa.unimarc.mapping.MappingDefinition;
import com.gruppometa.unimarc.object.Field;
import com.gruppometa.unimarc.object.OutItem;

public class XmlProfile extends SbnProfile{
	protected static Log logger = LogFactory.getLog(XmlProfile.class);
	protected String xmlFile = null;
	protected String filterId = null;
	protected String mapVersion = "1.0";
	
	@Override
	public String getMapVersion(){
		return mapVersion;
	}
	public String getFilterId() {
		return filterId;
	}

	public void setFilterId(String filterId) {
		this.filterId = filterId;
	}


	String className = "/defaultProfile.xml";

	public XmlProfile(){
		
	}
			
	public XmlProfile(String className) {
		this.className = className;
	}
	
	/* (non-Javadoc)
	 * @see com.gruppometa.unimarc.profile.SbnProfile#addField(com.gruppometa.unimarc.object.OutItem, java.lang.String, java.lang.String)
	 */
	@Override
	protected Field addField(OutItem desc, String name, String qualifier) {
		String key = name.toLowerCase()+(qualifier!=null?( ":"+qualifier.toLowerCase()):"");
		if(fieldmaps.get(key)!=null
				){
			if(!fieldmaps.get(key).equals("delete")){
				String[] names = fieldmaps.get(name.toLowerCase()+":"+qualifier.toLowerCase()).split(":"); 
				return super.addField(desc, names[0], (names.length>1? names[1]:null));
			}
			else
				return null;
		}
		return super.addField(desc, name, qualifier);
	}
	
	
	/* (non-Javadoc)
	 * @see com.gruppometa.unimarc.profile.SbnProfile#makeNode(com.gruppometa.unimarc.object.OutItem, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void makeNode(OutItem desc, String destinationNode, String data,
			String qualifier) throws IllegalArgumentException,
			SecurityException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		if(qualifier==null)
			qualifier = "";
		if(fieldmaps.get(destinationNode.toLowerCase()+":"+qualifier.toLowerCase())!=null){
			String[] names = fieldmaps.get(destinationNode.toLowerCase()+":"+qualifier.toLowerCase()).split(":");
			if(!names[0].equals("delete"))
				super.makeNode(desc, names[0], data,(names.length>1? names[1]:null));
		}
		else
			super.makeNode(desc, destinationNode, data, qualifier);
	}


	protected String sep =":";
	protected HashMap<String,String> fieldmaps = new HashMap<String, String>();
	protected HashMap<String,String> fieldsHash = new HashMap<String, String>();

	
	
	@Override
	public boolean isFinished() {
		return getFilterId()!=null && hasAdded;
	}
	/* (non-Javadoc)
	 * @see com.gruppometa.unimarc.profile.SbnProfile#init()
	 */
	@Override
	public void init() {
		normalizer.setLanguageFieldName("lingua");
		fieldsHash.clear();
		hasAdded = false;
		try {
			DocumentBuilder builder;
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			// factory.setXIncludeAware(true); // non supportato
			// factory.setNamespaceAware( true );
			builder = factory.newDocumentBuilder();

			Document doc = builder.parse(XmlProfile.class.getResourceAsStream(className));
			NodeList nodes =  XPathAPI.selectNodeList(doc,"/mappings/mapping");
			List<MappingDefinition> mappings = new ArrayList<MappingDefinition>();
			makeXmlDefNode(nodes,mappings);
			NodeList nodes2 =  XPathAPI.selectNodeList(doc,"/mappings/include");
			for (int i = 0; nodes2!=null && i < nodes2.getLength(); i++) {
				if(nodes2.item(i).getAttributes().getNamedItem("href")!=null){
					String xml = nodes2.item(i).getAttributes().getNamedItem("href").getNodeValue();
					Document doc2 = builder.parse(XmlProfile.class.getResourceAsStream(xml));
					NodeList nodes3 =  XPathAPI.selectNodeList(doc2,"/mappings/mapping");
					makeXmlDefNode(nodes3,mappings);
				}
			}

			defs = (MappingDefinition[])mappings.toArray(new MappingDefinition[mappings.size()]);			
			normalizer.initMaps(defs);
			
			nodes =  XPathAPI.selectNodeList(doc,"//version");
			for (int i = 0; nodes!=null && i < nodes.getLength(); i++) {
				Node node = nodes.item(i);
				mapVersion = node.getTextContent();
			}
			nodes =  XPathAPI.selectNodeList(doc,"//fieldmap");
			for (int i = 0; nodes!=null && i < nodes.getLength(); i++) {
				Node node = nodes.item(i);
				NamedNodeMap map = node.getAttributes();
				fieldmaps.put(map.getNamedItem("name").getNodeValue().toLowerCase(), map.getNamedItem("value").getNodeValue());
			}			
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	protected void makeXmlDefNode(NodeList nodes,List<MappingDefinition> mappings) throws TransformerException {
		for (int i = 0; nodes!=null && i < nodes.getLength(); i++) {
			MappingDefinition def = makeMapDefinition(nodes.item(i),-1);
			NodeList subnodes =  XPathAPI.selectNodeList(nodes.item(i),"mapping");
			List<MappingDefinition> subDefs = new ArrayList<MappingDefinition>();
			Map<String,MappingDefinition> subMap = new HashMap<String, MappingDefinition>();
			for (int j = 0; subnodes!=null && j < subnodes.getLength(); j++) {
				MappingDefinition defsub = makeMapDefinition(subnodes.item(j),j);
				if(def.isMultiple() || subMap.containsKey(defsub.getDestination())){
					defsub.setMultiple(true);
					if(subMap.containsKey(defsub.getDestination()))
						subMap.get(defsub.getDestination()).setMultiple(true);
				}
				subDefs.add(defsub);
				subMap.put(defsub.getDestination(), defsub);
			}
			if(subDefs.size()>0)
				def.setSubDefs(subDefs);
			mappings.add(def);
			fieldsHash.put(def.getDestination(), def.getMarcField());
		}
	}
	
	protected MappingDefinition makeMapDefinition(Node node, int pos){
		NamedNodeMap map = node.getAttributes();
		//new MappingDefinition("210", new String[] { "a","e"}, "Publisher","PlaceString",false,true),
		String marcField = "";
		if(map.getNamedItem("marcField")!=null)
			marcField = map.getNamedItem("marcField").getNodeValue();
		String marcSections[] = ALL;
		String group = null; 
		if(map.getNamedItem("group")!=null)
			group = map.getNamedItem("group").getNodeValue();
		if(map.getNamedItem("marcSection")!=null)
			marcSections = map.getNamedItem("marcSection").getNodeValue().split("\\|");
		String destination = map.getNamedItem("destination").getNodeValue();
		String qualifier = (map.getNamedItem("qualifier")!=null?map.getNamedItem("qualifier").getNodeValue():null);
		int posInit = map.getNamedItem("posInit")!=null?Integer.parseInt(map.getNamedItem("posInit").getNodeValue()):-1;
		int posEnd = map.getNamedItem("posEnd")!=null?Integer.parseInt(map.getNamedItem("posEnd").getNodeValue()):-1;
		double vistaEtichette = map.getNamedItem("vistaEtichette")!=null?Double.parseDouble(map.getNamedItem("vistaEtichette").getNodeValue()):-1;
		double vistaIsbd = map.getNamedItem("vistaIsbd")!=null?Double.parseDouble(map.getNamedItem("vistaIsbd").getNodeValue()):-1;
		boolean caseSensetive = false;
		boolean ifFirst = false;
		MappingDefinition def = new MappingDefinition(marcField, marcSections, destination, qualifier, caseSensetive, ifFirst);
        if(map.getNamedItem("ind")!=null)
            def.setInd(map.getNamedItem("ind").getNodeValue());
		if(map.getNamedItem("listType")!=null)
			def.setListType(map.getNamedItem("listType").getNodeValue());
		if(map.getNamedItem("condMarcSection")!=null)
			def.setCondMarcSection(map.getNamedItem("condMarcSection").getNodeValue());
		if(map.getNamedItem("condValue")!=null)
			def.setCondValue(map.getNamedItem("condValue").getNodeValue());
		if(map.getNamedItem("condValue2")!=null)
			def.setCondValue2(map.getNamedItem("condValue2").getNodeValue());
		if(map.getNamedItem("parent")!=null)
			def.setParent(map.getNamedItem("parent").getNodeValue());
		if(map.getNamedItem("parentLabel")!=null)
			def.setParentLabel(map.getNamedItem("parentLabel").getNodeValue());
		if(map.getNamedItem("type")!=null)
			def.setType(map.getNamedItem("type").getNodeValue());
		if(map.getNamedItem("excludeInFe")!=null &&  map.getNamedItem("excludeInFe").getNodeValue().equalsIgnoreCase("true"))
			def.setExcludeInFe(true);
		if(map.getNamedItem("cutZeros")!=null &&  map.getNamedItem("cutZeros").getNodeValue().equalsIgnoreCase("true"))
			def.setCutZeros(true);
		if(map.getNamedItem("docAttribute")!=null &&  map.getNamedItem("docAttribute").getNodeValue().equalsIgnoreCase("true"))
			def.setDocAttribute(true);
		if(map.getNamedItem("excludeFromSearchField")!=null &&  map.getNamedItem("excludeFromSearchField").getNodeValue().equalsIgnoreCase("true"))
			def.setExcludeFromSearchField(true);
		if(map.getNamedItem("hideLabel")!=null &&  map.getNamedItem("hideLabel").getNodeValue().equalsIgnoreCase("true"))
			def.setHideLabel(true);
		if(map.getNamedItem("sortField")!=null &&  map.getNamedItem("sortField").getNodeValue().equalsIgnoreCase("true"))
			def.setSortField(true);
		if(map.getNamedItem("sortFieldName")!=null)
			def.setSortFieldName(map.getNamedItem("sortFieldName").getNodeValue());
		if(map.getNamedItem("rangeEnd")!=null)
			def.setRangeEnd(map.getNamedItem("rangeEnd").getNodeValue());
		if(map.getNamedItem("label")!=null)
			def.setLabel(map.getNamedItem("label").getNodeValue());
		if(map.getNamedItem("feLabel")!=null)
			def.setFeLabel(map.getNamedItem("feLabel").getNodeValue());
		if(map.getNamedItem("facetLabel")!=null)
			def.setFacetLabel(map.getNamedItem("facetLabel").getNodeValue());
		if(map.getNamedItem("searchType")!=null)
			def.setSearchType(map.getNamedItem("searchType").getNodeValue());
		if(map.getNamedItem("copyTo")!=null)
			def.setCopyTo(map.getNamedItem("copyTo").getNodeValue());
		if(map.getNamedItem("join")!=null)
			def.setJoin(map.getNamedItem("join").getNodeValue());
		if(map.getNamedItem("solrFieldname")!=null)
			def.setSolrFieldname(map.getNamedItem("solrFieldname").getNodeValue());
		if(map.getNamedItem("vocabulary")!=null)
			def.setVocabulary(map.getNamedItem("vocabulary").getNodeValue());
		if(map.getNamedItem("group2")!=null)
			def.setGroup2(map.getNamedItem("group2").getNodeValue());
		if(map.getNamedItem("separator")!=null)
			def.setSeparator(map.getNamedItem("separator").getNodeValue());
		if(map.getNamedItem("facet")!=null)
			def.setFacet(map.getNamedItem("facet").getNodeValue());
		if(map.getNamedItem("facetOrder")!=null )
			def.setFacetOrder(Double.parseDouble(map.getNamedItem("facetOrder").getNodeValue()));
		if(map.getNamedItem("vistaShort")!=null )
			def.setVistaShort(Double.parseDouble(map.getNamedItem("vistaShort").getNodeValue()));
		if(map.getNamedItem("inverse")!=null)
			def.setInverse(map.getNamedItem("inverse").getNodeValue());
		if(map.getNamedItem("facets")!=null && map.getNamedItem("facets").getNodeValue().equalsIgnoreCase("true"))
			def.setFacets(true);
		if(map.getNamedItem("searchField")!=null && map.getNamedItem("searchField").getNodeValue().equalsIgnoreCase("true"))
			def.setSearchField(true);
		if(map.getNamedItem("fulltext")!=null && map.getNamedItem("fulltext").getNodeValue().equalsIgnoreCase("false"))
			def.setFulltext(false);
		if(map.getNamedItem("is4Fe")!=null && map.getNamedItem("is4Fe").getNodeValue().equalsIgnoreCase("true"))
			def.setIs4Fe(true);
		if(map.getNamedItem("multiple")!=null && map.getNamedItem("multiple").getNodeValue().equalsIgnoreCase("true"))
			def.setMultiple(true);
		if(map.getNamedItem("handler")!=null)
			def.setHandler(map.getNamedItem("handler").getNodeValue());
		if(map.getNamedItem("labelGroup")!=null)
			def.setLabelGroup(map.getNamedItem("labelGroup").getNodeValue());
		def.setPosInit(posInit);
		def.setPosEnd(posEnd);
		def.setVistaEtichette(vistaEtichette);		
		def.setOrder(pos);
		def.setVistaIsbd(vistaIsbd);
		def.setGroup(group);
		return def;
	}
	public String getMarcFieldFromDestination(String destination){
		if(fieldsHash!=null){
			String ret =  fieldsHash.get(destination);
			for(String key: fieldsHash.keySet()){
				if(!key.equals(destination) && fieldsHash.get(key).equals(ret))
					return ret+"_"+destination;
			}
			return ret;
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see com.gruppometa.unimarc.profile.SbnProfile#makeSpecialTwo(com.gruppometa.unimarc.object.OutItem, org.marc4j.marc.Record)
	 */
	@Override
	public void makeSpecialTwo(OutItem desc, Record record)
			throws IllegalArgumentException, SecurityException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		/**
		 *  Richiesta 10-12-2014:
		 *  In “libri antichi” creare una condizione per cui se il campo unimarc 200 
		 *  ha un valore solo numerico (1, 2, 3 …), comporre il campo “Titolo” della Teca 
		 *  unendo il valore del campo unimarc 200 solo numerico separato da <due punti – spazio> 
		 *  con il valore del campo unimarc 461 (es.:  1: Commedie in versi dell'abate 
		 *  Pietro Chiari bresciano poeta di S.A. serenissima il sig. duca di Modana. Tomo primo [-decimo ed ultimo])
		 */
		
	}

	protected boolean hasAdded;
	
	/* (non-Javadoc)
	 * @see com.gruppometa.unimarc.profile.SbnProfile#makeSpecialOne(com.gruppometa.unimarc.object.OutItem, org.marc4j.marc.Record)
	 */
	@Override
	public void makeSpecialOne(OutItem desc, Record record)
			throws IllegalArgumentException, SecurityException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		//logger.debug("ID: "+desc.getAbout());
		hasAdded = true;
		@SuppressWarnings("unchecked")
		List<DataField> datas = record.getDataFields();
		//boolean isPartOf = record.getLeader().toString().charAt(9)=='W';
		//HashMap<String, String> fieldsDone = new HashMap<String, String>();
		for (Iterator<DataField> iterator = datas.iterator(); iterator
				.hasNext();) {
			DataField dataField = (DataField) iterator.next();				
			if(dataField.getTag().equals("950")){
				@SuppressWarnings("unchecked")
				List<Subfield> subs = dataField.getSubfields('e');
				@SuppressWarnings("unchecked")
				List<Subfield> subsA = dataField.getSubfields('a');
				if(subs!=null && subsA!=null && subsA.size()>0 && isValidLocalizzazione(subsA.get(0).getData())){
					for (Subfield sub : subs) {
						String data = sub.getData();
						if(data.startsWith(" CR   ")|| data.length()>16){
							String val = data.substring(6,16);
							makeNode(desc, "Inventario", val);
							//logger.debug("Added inventario: '"+val+"'");
						}
					}
				}
				else if(subs!=null && subsA!=null && subsA.size()>0 && !isValidLocalizzazione(subsA.get(0).getData())){
					removeDataField(dataField);
				}
			}
			else if(isDataField(dataField.getTag())){
				Subfield sub = dataField.getSubfield('a');
				if(sub!=null){
					String val = sub.getData().substring(9,13);
					String range = "[";
					String range2 = "";
					boolean useRange = false;
					boolean useRange2 = true;
					if(isValidDate(val)){
						makeNode(desc, "Data", val);
						makeNode(desc, "Data_inizio", val);
						range +=val;
						range2 +=val;
					}
					if(sub.getData().charAt(7)=='b' || sub.getData().charAt(7)=='g'
						|| sub.getData().charAt(7)=='f'
						){									
						val = sub.getData().substring(13,17);
						if(isValidDate(val)){
							makeNode(desc, "Data", val);
							range+=" TO "+val+"]";
							range2+= " - "+ val;
							makeNode(desc, "Data_fine", val);
						}
						else{
							range2+= " - ";
							range+=" TO 9999]";
							makeNode(desc, "Data_fine", "9999");
						}
					}
					else{
						range2+= " - "+ val;
						range+=" TO "+val+"]";
						/**
						 * secondo valore
						 */
						val = sub.getData().substring(13,17);
						if(isValidDate(val)){
							makeNode(desc, "Data_inizio", val);
						}
						if(val.equals("9999")){
							makeNode(desc, "Data_fine", val);
						}

					}
					if(useRange)
						makeNode(desc, "Data_range", range);
					if(useRange2)
						makeNode(desc, "Data_range", range2);
				}
			}
		}
		removeDataFields(record);
		//super.makeSpecialOne(desc, record);
	}
	
	protected boolean isDataField(String dataField){
		return dataField!=null && dataField.equals("100");
	}
	protected void removeDataFields(Record record) {
	}
	protected void removeDataField(DataField dataField) {
	}

	@Override
	protected String getValueSeparator(String fieldname, String code, String data, String[] subFieldsCodes) {
		/**
		 * per BNCR
		 */
		if(fieldname.equals("215")){
			if(code.equals("c"))
				return " : ";
			if(code.equals("d"))
				return " ; ";			
		}
		if(fieldname.equals("012")){
			if(code.equals("9"))
				return " - ";						
		}
		return super.getValueSeparator(fieldname, code, data, subFieldsCodes);
	}

	@Override
	public boolean scarta(OutItem desc) {
		if(getFilterId()!=null && !desc.getAbout().equals(getFilterId()))
			return true;
		return false;
		//return desc.getAbout().indexOf("\\MSM\\")!=-1 || desc.getAbout().indexOf("\\MUS\\")!=-1;
	}

}
