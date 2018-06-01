package com.gruppometa.unimarc.profile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;


public class RecordCache {
	protected static final Logger logger = LoggerFactory.getLogger(RecordCache.class);
	
	protected static CacheManager cm = null;
	private Cache cache;
	private static final String CACHENAME = "cacheRecords";
	protected String prefix;
	public RecordCache(){
		this(""+System.currentTimeMillis());
	}
	public RecordCache(String prefix){
		this.prefix = prefix;
		initCm();
		init();
	}
	
	protected void initCm(){
		if(cm==null){
			synchronized (CACHENAME) {
				if(cm==null)
					cm = CacheManager.getInstance();				
			}
		}
	}
	
	protected void init(){
		if(cache==null)
			cache = cm.getCache(CACHENAME);
	}

	public void putFilename(String id, String filename){
		cache.put(new Element(prefix+"file::"+id,filename));
	}

	
	public String getFilename(String id) {
		if(cache==null)
			init();
		if(cache.get(prefix+"file::"+id)!=null)
			return cache.get(prefix+"file::"+id).getObjectValue().toString();
		else
			return null;			
	}
	

	public void finalize(){
	}
	
	public void shutDown(){
		if(cm!=null)
			cm.shutdown();
		cm = null;
	}
}
