package com.gruppometa.sbnmarc.mag;

import net.sf.saxon.dom.DOMNodeWrapper;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.value.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CounterFunction extends ExtensionFunctionDefinition {
	protected static final Logger logger = LoggerFactory.getLogger(CounterFunction.class);

	protected String functionName;
	protected String vocabulary;
	protected Integer count = 0;

	public CounterFunction(String functionName) {
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
		return new SequenceType[] { SequenceType.STRING_SEQUENCE};
	}

	public boolean dependsOnFocus() {
		return true;
	}

	@Override 
	 public StructuredQName getFunctionQName() { 
		 return new StructuredQName("magextension", "http://magextension.it/saxon-extension", getFunctionName());
	 }

	@Override
	public ExtensionFunctionCall makeCallExpression() {
		return new ExtensionFunctionCall() {
			@Override
			public Sequence call(XPathContext context, Sequence[] arguments) throws XPathException {
				Object arg = arguments[0].head();
				String n = null;
				if(arg==null )
					return StringValue.makeStringValue("");
				if (arg instanceof StringValue)
					n = ((StringValue) arg).getStringValue();
				if(n.equals("0")) {
					count = 0;
					n = "";
				}
				else if(n.equals("1")) {
					++count;
					n = "";
				}
				else{
					n = ""+count;
				}
				//logger.error("Count: "+count);
				return StringValue.makeStringValue(n);
			}
		};
	}


}
