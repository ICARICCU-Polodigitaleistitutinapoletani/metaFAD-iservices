package com.gruppometa.sbnmarc.mag;

import com.gruppometa.unimarc.profile.IssuedAndLanguageNormalizer;
import net.sf.saxon.dom.DOMNodeWrapper;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.value.StringValue;

import java.util.Map;

public class SaxonFunction extends ExtensionFunctionDefinition {
	
	protected String functionName;
	protected String vocabulary;
	protected Map<String, String> map = null;
	protected IssuedAndLanguageNormalizer normalizer = new IssuedAndLanguageNormalizer();
	protected boolean keyLower = true;
	
	public SaxonFunction(String functionName, String vocabulary) {
		this.functionName = functionName;
		this.vocabulary = vocabulary;
	}

	public SaxonFunction(String functionName, String vocabulary, boolean keyLower) {
		this.functionName = functionName;
		this.vocabulary = vocabulary;
		this.keyLower = keyLower;
	}

	public String getValue(String c) {
		if(map==null)
			map = normalizer.getMap(vocabulary,true,keyLower);
		return map.get(keyLower? c.toLowerCase(): c);
	}
	
	protected String getFunctionName(){
		return functionName;
	}


	@Override
	public SequenceType getResultType(SequenceType[] suppliedArgumentTypes) {
		return SequenceType.SINGLE_STRING;
	}

	public SequenceType[] getArgumentTypes() {
		return new SequenceType[] { SequenceType.SINGLE_STRING};
	}
	

	@Override 
	 public StructuredQName getFunctionQName() { 
		 return new StructuredQName("magextension", "http://magextension.it/saxon-extension", getFunctionName());
	 }

	@Override
	public ExtensionFunctionCall makeCallExpression() {
		 return new ExtensionFunctionCall() { 
			 @Override public Sequence call(XPathContext context, Sequence[] arguments) throws XPathException {
				 if(arguments==null)
				 	return StringValue.makeStringValue("");
			 	 Object arg = arguments[0].head();
				 String v0 = null;
				 if(arg instanceof  StringValue)
				 	v0 = ((StringValue)arg).getStringValue();
				 String result = getValue(v0);
				 return StringValue.makeStringValue(result);
			 }
		};		 
	}


	public void setValue(String key, String value){
		if(map==null)
			map = normalizer.getMap(vocabulary,false);
		map.put(key,value);
	}
	
}
