package com.gruppometa.mets2mag.saxon;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.value.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

public class SaxonFunctionGetPropertyFromJson extends SaxonFunctionGetTypeFromLeader{


	protected String functionName;
	public SaxonFunctionGetPropertyFromJson(String functionName){
		this.functionName = functionName;
	}
	
	protected static final Logger logger = LoggerFactory.getLogger(SaxonFunctionGetPropertyFromJson.class);

	public String getValue(String json, String fieldname) {
		if(json==null || json.length()==0)
			return "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode node = mapper.readTree(json);
			return node.get(fieldname)!=null?node.get(fieldname).asText():"";
		} catch (IOException e) {
			logger.error("",e);
		}
		return "";
	}

	public SequenceType[] getArgumentTypes() {
		return new SequenceType[] { SequenceType.SINGLE_STRING,SequenceType.SINGLE_STRING};
	}

	@Override
	protected String getFunctionName() {
		return functionName;
	}

	@Override
	public ExtensionFunctionCall makeCallExpression() {
		return new ExtensionFunctionCall() {
			@Override public Sequence call(XPathContext context, Sequence[] arguments) throws XPathException {
				String v0 = ((StringValue)arguments[0].head()).getStringValue();
				String v1 = ((StringValue)arguments[1].head()).getStringValue();
				String result = getValue(v0,v1);
				return StringValue.makeStringValue(result);
			}
		};
	}
}
