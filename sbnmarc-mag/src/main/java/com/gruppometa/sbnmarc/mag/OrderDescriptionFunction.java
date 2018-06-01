package com.gruppometa.sbnmarc.mag;

import com.gruppometa.unimarc.profile.IssuedAndLanguageNormalizer;
import com.gruppometa.unimarc.profile.XmlProfile;
import net.sf.saxon.dom.DOMNodeWrapper;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.type.Type;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.value.StringValue;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.impl.DataFieldImpl;
import org.marc4j.marc.impl.RecordImpl;
import org.marc4j.marc.impl.SubfieldImpl;

import java.util.List;
import java.util.Map;

public class OrderDescriptionFunction extends ExtensionFunctionDefinition {

	protected String functionName;
	protected String vocabulary;

	public OrderDescriptionFunction(String functionName) {
		this.functionName = functionName;
	}

	protected String getFunctionName(){
		return functionName;
	}

	@Override
	public SequenceType getResultType(SequenceType[] suppliedArgumentTypes) {
		return SequenceType.SINGLE_STRING;
	}

	public SequenceType[] getArgumentTypes() {
		return new SequenceType[] { SequenceType.NODE_SEQUENCE};
	}

	@Override 
	 public StructuredQName getFunctionQName() { 
		 return new StructuredQName("magextension", "http://magextension.it/saxon-extension", getFunctionName());
	 }

	public boolean dependsOnFocus() {
		return true;
	}

	@Override
	public ExtensionFunctionCall makeCallExpression() {
		return new ExtensionFunctionCall() {
			@Override
			public Sequence call(XPathContext context, Sequence[] arguments) throws XPathException {
				Object arg = arguments[0].head();
				if(arg==null )
					return StringValue.makeStringValue("");
				if (arg instanceof DOMNodeWrapper) {
					DOMNodeWrapper dom = (DOMNodeWrapper)arg;
					String tag = dom.getLocalPart();
					if(dom.getAttributeValue("","tipoLegame")!=null)
						tag = dom.getAttributeValue("","tipoLegame");
					if(dom.getAttributeValue("","tipoNota")!=null)
						tag = dom.getAttributeValue("","tipoNota");
					return StringValue.makeStringValue(getOrder(tag));

				}
				return null;
			}
		};
	}

	private String getOrder(String tag) {
		if(tag.startsWith("T"))
			tag = tag.substring(1);
		if(functionName.equals("getOrderGraficoDescription")){
			if (tag == null)
				return "0";
			if (tag.equals("330"))
				return "1";
			if (tag.equals("327"))
				return "2";
			if (tag.equals("300"))
				return "5";
			if (tag.equals("950"))
				return "6";
			if (tag.equals("316"))
				return "7";
			return "0";
		}

		else if(functionName.equals("getOrderTitoloUniforme")) { // anche per il libretto
			if (tag == null)
				return "0";
			if (tag.equals("500"))
				return "1";
			if (tag.equals("928"))
				return "2";
			if (tag.equals("929"))
				return "3";
			return "0";
		}
		else if(functionName.equals("getOrderAnticoDescription")){ // anche per il libretto
			if (tag == null)
				return "0";
			if (tag.equals("950"))
				return "1";
			if (tag.equals("316"))
				return "2";
			if (tag.equals("303"))
				return "3";
			if (tag.equals("921")) // marca
				return "4";
			if (tag.equals("300"))
				return "5";
			/**
			 * servono per il libretto
			 */
			if (tag.equals("922"))
				return "6";
			if (tag.equals("923"))
				return "7";
			if (tag.equals("922"))
				return "8";
			if (tag.equals("927"))
				return "9";
			return "0";
		}
		else if(functionName.equals("getOrderVideoDescription")){ // anche per il libretto
			if (tag == null)
				return "0";
			if (tag.equals("950"))
				return "1";
			if (tag.equals("316"))
				return "2";
			if (tag.equals("327"))
				return "3";
			if (tag.equals("323"))
				return "4";
			if (tag.equals("927"))
				return "5";
			if (tag.equals("300"))
				return "6";
			return "0";
		}
		else {
			if (tag == null)
				return "0";
			if (tag.equals("326"))
				return "1";
			if (tag.equals("300"))
				return "2";
			if (tag.equals("207"))
				return "3";
			if (tag.equals("950"))
				return "4";
			return "0";
		}
	}


}
