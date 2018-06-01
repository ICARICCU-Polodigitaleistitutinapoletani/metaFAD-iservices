package com.gruppometa.unimarc.output;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.gruppometa.unimarc.object.Field;
import com.gruppometa.unimarc.object.OutItem;
import com.gruppometa.unimarc.object.Output;
import com.gruppometa.unimarc.profile.XmlProfile;

public class TextOutputFormatter  extends BaseOutFormatter{
	
	public TextOutputFormatter(Output output) {
		this.output = output;
	}
	
	public void toXml(Appendable buf) throws IOException {
		for (Iterator<OutItem> iterator = output.getItems().iterator(); iterator.hasNext();) {
			OutItem type = (OutItem) iterator.next();
			toXml(buf,type);
		}	
	}

	@Override
	protected void toXml(Appendable buf, OutItem outitem) throws IOException {
		if(outitem==null)
			return;
		buf.append("\n");
		buf.append("\nid:" + outitem.getAbout());
		List<String> lines = new ArrayList<String>();
		for (Iterator<Field> iterator = outitem.getFields().iterator(); iterator.hasNext();) {
			Field field = (Field) iterator.next();
			lines.add(filterName(field.getName())+": " + field.getTextValueWithBidAndRole());
		}	
		lines.sort(new Comparator<String>(){

			public int compare(String o1, String o2) {
				if(o1.startsWith("field"))
					o1="z"+o1;
				if(o2.startsWith("field"))
					o2="z"+o2;
				return o1.compareTo(o2);				
			}});
		for (Iterator iterator = lines.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			buf.append("\n"+string);
		}
	}

	private String filterName(String name) {
		String fn = ((XmlProfile)profile).getMarcFieldFromDestination(name);
		return (fn!=null?("field_"+fn):name).toLowerCase();
	}

	@Override
	protected void writeInit(Appendable buf) throws IOException {
	}

	@Override
	protected void writeEnd(Appendable buf) throws IOException {
	}


}
