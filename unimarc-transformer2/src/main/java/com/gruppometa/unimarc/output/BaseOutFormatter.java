package com.gruppometa.unimarc.output;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gruppometa.unimarc.logging.UserLogger;
import com.gruppometa.unimarc.object.Field;
import com.gruppometa.unimarc.object.JsonValue;
import com.gruppometa.unimarc.object.LabelPairGroup;
import com.gruppometa.unimarc.object.LabelValuePair;
import com.gruppometa.unimarc.object.OutItem;
import com.gruppometa.unimarc.object.Output;
import com.gruppometa.unimarc.profile.IssuedAndLanguageNormalizer;
import com.gruppometa.unimarc.profile.Profile;
import com.gruppometa.unimarc.profile.RecordCache;

public abstract class BaseOutFormatter implements OutputFormatter{
	protected static Log logger = LogFactory.getLog(BaseOutFormatter.class);
	protected IssuedAndLanguageNormalizer normalizer = new IssuedAndLanguageNormalizer();
	protected Map<String, String> roleMap = null;

	protected RecordCache recordCache;
	public RecordCache getRecordCache() {
		return recordCache;
	}

	public void setRecordCache(RecordCache recordCache) {
		this.recordCache = recordCache;
	}

	protected Output output;

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	protected int bufferSize = 1000;
	protected Profile profile;
	/* (non-Javadoc)
	 * @see com.gruppometa.unimarc.output.OutputFormatter#notifyAdd()
	 */
	public void notifyAdd(Profile profile) throws IOException{
		this.profile = profile;
		if(output.getItems().size()==bufferSize){
			for (Iterator<OutItem> iterator = output.getItems().iterator(); iterator.hasNext();) {
				OutItem type = (OutItem) iterator.next();
				toXml(getOutfile(),type);
			}
			output.getItems().clear();
		}		
	}

	/* (non-Javadoc)
	 * @see com.gruppometa.unimarc.output.OutputFormatter#notifyInit()
	 */
	public void notifyInit() throws IOException {
		writeInit(getOutfile());		
	}

	protected abstract void toXml(Appendable buf, OutItem outitem) throws IOException;
	protected abstract void writeInit(Appendable buf) throws IOException;
	
	protected abstract void writeEnd(Appendable buf) throws IOException;
	
	/* (non-Javadoc)
	 * @see com.gruppometa.unimarc.output.OutputFormatter#notifyEnd()
	 */
	public void notifyEnd() throws IOException{
		for (Iterator<OutItem> iterator = output.getItems().iterator(); iterator.hasNext();) {
			OutItem type = (OutItem) iterator.next();
			toXml(getOutfile(),type);
		}
		output.getItems().clear();
		writeEnd(getOutfile());
	}

	/**
	 * @return the output
	 */
	public Output getOutput() {
		return output;
	}

	/**
	 * @param output the output to set
	 */
	public void setOutput(Output output) {
		this.output = output;
	}

	protected Appendable outfile;

	/**
	 * @return the outfile
	 */
	public Appendable getOutfile() {
		return outfile;
	}

	/**
	 * @param outfile the outfile to set
	 */
	public void setOutfile(Appendable outfile) {
		this.outfile = outfile;
	}
	
	protected String getHtmlValue(List<LabelValuePair> labelValuePairs) {
		StringBuffer buf = new StringBuffer();
		for (LabelValuePair labelValuePair : labelValuePairs) {
			if(buf.length()>0)
				buf.append("\n");
			if(labelValuePair instanceof LabelPairGroup){
				buf.append("<div class='group' title='"+StringEscapeUtils.escapeHtml4(labelValuePair.getLabel())+"'>");
				buf.append(getHtmlValue(((LabelPairGroup)labelValuePair).getLabelValuePairs()));
				buf.append("</div>");
			}
			else{
				buf.append("<div><div class='label'>");
				buf.append(StringEscapeUtils.escapeHtml4(labelValuePair.getLabel()));
				buf.append("</div><div class='value'>"+ StringEscapeUtils.escapeHtml4(labelValuePair.getValue())+
					"</div>"
					+ "</div>");
			}
		}
		return buf.toString();
	}
	
	protected String lastId=null;
	
	protected String checkValue(String value, OutItem outItem){
		if(value!=null && (lastId==null || !lastId.equals(outItem.getAbout())) && value.contains("\u00C3"))
			UserLogger.logger.warn("Wrong encoding in record '"+outItem.getAbout()+"'");
		lastId = outItem.getAbout();
		return value;
	}

	protected String cutZeros(String role){
		if(role==null)
			return null;
		while(role.startsWith("0"))
			role = role.substring(1);
		return role;
	}
	protected List<JsonValue> getValuesAsList(OutItem outitem, String name) {
		return getValuesAsList(outitem, name, false);
	}
	protected LinkCreator linkCreator = null;

	public LinkCreator getLinkCreator() {
		return linkCreator;
	}

	public void setLinkCreator(LinkCreator linkCreator) {
		this.linkCreator = linkCreator;
	}

	protected List<JsonValue> getValuesAsList(OutItem outitem, String name, boolean checkDouble) {
		List<JsonValue> list = new ArrayList<JsonValue>();
		int i = 0;
		for (Iterator<Field> iterator = outitem.getFields().iterator(); iterator.hasNext();) {
			Field field = (Field) iterator.next();
			if(field.getName().equals(name)){
				JsonValue v = new JsonValue();
				if(field.getLabelValuePairs()!=null){
					v.setPlain(getPlainValue(field.getLabelValuePairs()));
					v.setHtml(getHtmlValue(field.getLabelValuePairs()));
				}
				else{
					v.setPlain(checkValue(field.getTextValue(),outitem));
					v.setBinary(field.getBinaryValue());
					StringBuffer buf = new StringBuffer();
					if(field.getRole()!=null){
						String r = cutZeros(field.getRole()); 
						if(roleMap.get(r)!=null) 
							buf.append("["+roleMap.get(r)+"] ");
						else
							logger.warn("Ruolo '"+r+"' not found.");
						v.setRole(roleMap.get(field.getRole()));
					}
					if(getLinkCreator()!=null){
						buf.append(getLinkCreator().createLinks(field));
					}
					if(field.getVid()!=null){
						v.setVid(field.getVid());
						if(getLinkCreator()==null) {
							buf.append("<a href=\"${page}?VID=" + field.getVid() + "\">");
						}
					}
					if(field.getBid()!=null){
						v.setBid(field.getBid());
						if(getLinkCreator()==null) {
							buf.append("<a href=\"${page}?BID=" + field.getBid() + "\">");
						}
					}
					if(getLinkCreator()==null) {
						if (buf.length() > 0) {
							String str = field.getTextValue();
							int pos = str.lastIndexOf(" ; ");
							if (pos != -1)
								buf.append(StringEscapeUtils.escapeHtml4(str.substring(0, pos)) + "</a>" + StringEscapeUtils.escapeHtml4(str.substring(pos)));
							else
								buf.append(StringEscapeUtils.escapeHtml4(str) + "</a>");
						}
					}
					if(buf.length()>0 &&
							// escluso "Campi personalizzati"
							(field.getGroup()==null || field.getGroup().toLowerCase().contains("campi personalizzati"))
							)
						v.setHtml(buf.toString());
				}
				if(checkDouble){
					boolean found = false;
					for (JsonValue jsonValue : list) {
						if(jsonValue.getPlain().equals(v.getPlain())){
							found = true;
							break;
						}							
					}
					if(!found)
						list.add(v);
				}
				else
					list.add(v);
				i++;
			}
		}
		return list;
	}
	
	protected String getPlainValue(List<LabelValuePair> labelValuePairs) {
		StringBuffer buf = new StringBuffer();
		for (LabelValuePair labelValuePair : labelValuePairs) {
			if(filterPlainLabel(labelValuePair))
				continue;
			if(buf.length()>0)
				buf.append(" | ");
			if(labelValuePair instanceof LabelPairGroup){
				buf.append(getPlainValue(((LabelPairGroup)labelValuePair).getLabelValuePairs()));
			}
			else{				
				buf.append(labelValuePair.getValue());
			}
		}
		return buf.toString();
	}

	protected boolean filterPlainLabel(LabelValuePair labelValuePair) {
		if(labelValuePair!=null && labelValuePair.getLabel()!=null && labelValuePair.getLabel().equalsIgnoreCase("kardex"))
			return true;
		else
			return false;
	}

	

}
