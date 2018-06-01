package com.gruppometa.sbnmarc;

import java.util.Map;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.value.StringValue;

import com.gruppometa.unimarc.profile.IssuedAndLanguageNormalizer;

public class SaxonFunction extends ExtensionFunctionDefinition {
	
	protected String functionName;
	protected String vocabulary;
	protected Map<String, String> map = null;
	protected IssuedAndLanguageNormalizer normalizer = new IssuedAndLanguageNormalizer();
	
	public SaxonFunction(String functionName,String vocabulary) {
		this.functionName = functionName;
		this.vocabulary = vocabulary;
	}
	
	
	public String getValue(String c) {
		if(map==null)
			map = normalizer.getMap(vocabulary,false);
		return map.get(c);
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
		 return new StructuredQName("polo", "http://polodigitale.it/saxon-extension", getFunctionName()); 
	 }

	@Override
	public ExtensionFunctionCall makeCallExpression() {
		 return new ExtensionFunctionCall() { 
			 @Override public Sequence call(XPathContext context, Sequence[] arguments) throws XPathException {
				 String v0 = ((StringValue)arguments[0].head()).getStringValue(); 
				 String result = getValue(v0); 
				 return StringValue.makeStringValue(result);
			 }
		};		 
	}

	
}
