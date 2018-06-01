package com.gruppometa.unimarc.profile;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gruppometa.unimarc.object.LegameElement;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

public class ParentCache extends RecordCache {
	private Cache cache;
	private static final String CACHENAME = "cacheParents";
	protected ObjectMapper mapper = new ObjectMapper();
	@Override
	protected void init(){
		if(cache==null){
			cache = cm.getCache(CACHENAME);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public void putParent(String id, String parent, String childTitle, String type){
		String key = prefix+"parent::"+parent;
		List<LegameElement> relations = null;
		if(cache.get(key)!=null)
			try {
				relations = (List<LegameElement>) mapper.readValue((String) cache.get(key).getObjectValue(), 
							new TypeReference<List<LegameElement>>(){});
			} catch (Exception e) {
				logger.error("",e);
			}
		else
			relations = new ArrayList<LegameElement>();
		relations.add(new LegameElement(parent, id, childTitle, type));
		try {
			cache.put(new Element(key, mapper.writerFor(new TypeReference<List<LegameElement>>(){}).
					writeValueAsString(relations)));
		} catch (Exception e) {
			logger.error("",e);
		}

	}
	
	public List<String> getAllParents(){
		@SuppressWarnings("unchecked")
		List<String> keys = cache.getKeys();
		List<String> rets = new ArrayList<String>();
		for (String string : keys) {
			if(string.startsWith(prefix+"parent::"))
				rets.add(string.substring(new String(prefix+"parent::").length()));
		}
		return rets;
	}

	@SuppressWarnings("unchecked")
	public List<LegameElement> getParentInfo(String id){
		String key = prefix+"parent::"+id; 
		if(cache.get(key)!=null){
			try{
				return  (List<LegameElement>) mapper.readValue((String) cache.get(key).getObjectValue(), 
					new TypeReference<List<LegameElement>>(){});
			} catch (Exception e) {
				logger.error("",e);
			}
		}
		return null;
	}
	
	public void finalize(){
	}
}
