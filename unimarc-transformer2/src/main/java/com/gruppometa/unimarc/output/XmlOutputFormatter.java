package com.gruppometa.unimarc.output;

import java.io.IOException;
import java.util.Iterator;

import com.gruppometa.unimarc.object.Field;
import com.gruppometa.unimarc.object.OutItem;
import com.gruppometa.unimarc.object.Output;
import com.gruppometa.unimarc.util.StringUtil;

public class XmlOutputFormatter extends BaseOutFormatter implements OutputFormatter{
	protected boolean printAttrs = true;
	protected boolean noSpaceInNames = true;
	/**
	 * @return the printAttrs
	 */
	public boolean isPrintAttrs() {
		return printAttrs;
	}
	/**
	 * @param printAttrs the printAttrs to set
	 */
	public void setPrintAttrs(boolean printAttrs) {
		this.printAttrs = printAttrs;
	}
	public XmlOutputFormatter(Output output) {
		this.output = output;
	}
	public void toXml(Appendable buf) throws IOException {
		writeInit(buf);
		for (Iterator<OutItem> iterator = output.getItems().iterator(); iterator.hasNext();) {
			OutItem type = (OutItem) iterator.next();
			toXml(buf,type);
		}
		writeEnd(buf);
	}
	protected void writeInit(Appendable buf) throws IOException {
		buf.append("<?xml version='1.0' encoding='UTF-8'?>\n<add>");		
	}
	protected void writeEnd(Appendable buf) throws IOException {
		buf.append("\n</add>");
	}
	public void toXml(Appendable buf, OutItem item) throws IOException{
		buf.append("\n\t<doc>");
		buf.append("\n\t\t<field name=\"id\">"+StringUtil.xml(item.getAbout())+"</field>");
		for (Iterator<Field> iterator = item.getFields().iterator(); iterator.hasNext();) {
			Field type = (Field) iterator.next();
			buf.append("\n\t\t");
			toXml(buf,type);
		}
		buf.append("\n\t</doc>");
	}
	
	
	public void toXml(Appendable buf, Field field) throws IOException {
		buf.append("<field name=\""+StringUtil.xml(filterName(field.getName()))+"\"");
		if(printAttrs)
			buf.append(" multiple=\""+field.getMultiple()+"\""+
				((field.getBid()!=null && !field.getBid().equals("") )?(" bid=\""+StringUtil.xml(field.getBid())+"\""):"")+
				((field.getRole()!=null && !field.getRole().equals("") )?(" role=\""+StringUtil.xml(field.getRole())+"\""):"")+
				((field.getQualifier()!=null && !field.getQualifier().equals("") )?(" qualifier=\""+StringUtil.xml(field.getQualifier())+"\""):""));
		buf.append(">"+StringUtil.xml(field.getTextValue())+"</field>");		
	}
	private String filterName(String name) {
		if(name==null)
			return null;
		if(!noSpaceInNames)
			return name;
		else
			return name.replaceAll(" ", "_");
	}
	/**
	 * @return the noSpaceInNames
	 */
	public boolean isNoSpaceInNames() {
		return noSpaceInNames;
	}
	/**
	 * @param noSpaceInNames the noSpaceInNames to set
	 */
	public void setNoSpaceInNames(boolean noSpaceInNames) {
		this.noSpaceInNames = noSpaceInNames;
	}

}
