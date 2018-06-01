package com.gruppometa.unimarc.profile;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.marc4j.marc.Leader;
import org.marc4j.marc.Record;

import com.gruppometa.unimarc.mapping.MappingDefinition;
import com.gruppometa.unimarc.object.OutItem;
import com.gruppometa.unimarc.object.Output;

public interface Profile {
	public static final String[] ALL = new String[] { "ALL" };
	public static final String[] ALL_LETTERS =new String[] { "a","b","c","d","e","f"};
	public static final String[] A = new String[] { "a" };
	public static final String[] B = new String[] { "b" };
	boolean scarta(OutItem desc);
	String getMapVersion();
	void makeId(OutItem desc, Record record);
	boolean makeLeader(OutItem desc, Leader leader);
	public void makeSpecialOne(OutItem desc,Record record) throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;
	public void makeSpecialTwo(OutItem desc,Record record) throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;
	public void makeDefinitions(OutItem desc,Record record) throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;
	void normalize(OutItem output);
	void setFilename(String filename);
	MappingDefinition[] getDefinitions();
	MappingDefinition getDefinition(String destination);
	MappingDefinition getDefinitionFromMarcField(String marcField);
	void init();
	String getFilename();
	ParentCache getParentCache();
	void setParentCache(ParentCache cache);
	void notifyFullFilename(String filename);
	boolean isFinished();
	void setForFe(boolean forFe);
	
}
