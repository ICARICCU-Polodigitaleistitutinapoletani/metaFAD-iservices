package com.gruppometa.sbnmarc.mag;

import com.gruppometa.unimarc.profile.IssuedAndLanguageNormalizer;
import com.gruppometa.unimarc.profile.XmlProfile;
import net.sf.saxon.dom.DOMNodeWrapper;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.s9api.XdmNodeKind;
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

public class SaxonTextNormFunction extends ExtensionFunctionDefinition {

	protected String functionName;
	protected XmlProfile xmlProfile = null;

	protected IssuedAndLanguageNormalizer normalizer = new IssuedAndLanguageNormalizer();

	public SaxonTextNormFunction(){

	}

	public SaxonTextNormFunction(String functionName) {
		this.functionName = functionName;
		xmlProfile = createProfile();
		xmlProfile.init();
	}

	protected XmlProfile createProfile(){
		return new XmlProfile("/naProfile.xml"){
			protected String getValueSeparator(String fieldname, String code, String data, String[] subFieldsCodes) {
				if(fieldname.equals("922"))
					return ", ";
				if(fieldname.equals("210") && code.equals("e"))
					return " ; ";
				if(fieldname.equals("927") && code.equals("c"))
					return ": ";
				return super.getValueSeparator(fieldname,code,data,subFieldsCodes);
			}

		};
	}
	

	protected String getFunctionName(){
		return functionName;
	}


	@Override
	public SequenceType getResultType(SequenceType[] suppliedArgumentTypes) {
		return SequenceType.SINGLE_STRING;
	}

	public SequenceType[] getArgumentTypes() {
		return new SequenceType[] { SequenceType.NODE_SEQUENCE,SequenceType.SINGLE_STRING};
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
                Object arg1 = arguments[1].head();
				if(arg==null || arg1==null)
					return StringValue.makeStringValue("");
                String v0 = null;
				String codes = "";
				if (arg1 instanceof StringValue)
					codes = ((StringValue) arg1).getStringValue();
				if (arg instanceof DOMNodeWrapper) {
					v0 = "";
					DataField data = new DataFieldImpl();
					DOMNodeWrapper child = ((DOMNodeWrapper) arg).getFirstChild();
					data.setTag(getTag((DOMNodeWrapper) arg));
					do {
						if (child.getNodeKind() == Type.ELEMENT) {
							String codeString = child.getLocalPart().split("_")[0];
							/**
							 * tipo ac_210
							 */
							if(codeString.length()>1){
								DOMNodeWrapper child2 = ((DOMNodeWrapper) child).getFirstChild();
								do{
									if (child2.getNodeKind() == Type.ELEMENT) {
										String codeString2 = child2.getLocalPart().split("_")[0];
										v0 += " " + child2.getStringValueCS();
										Subfield sub = new SubfieldImpl();
										sub.setCode(codeString2.charAt(0));
										sub.setData(child2.getStringValueCS().toString());
										data.addSubfield(sub);
									}
								}
								while ((child2=child2.getNextSibling()) != null);
							}
							/**
							 * tipo a_210
							 */
							else {
								v0 += " " + child.getStringValueCS();
								Subfield sub = new SubfieldImpl();
								sub.setCode(codeString.charAt(0));
								sub.setData(child.getStringValueCS().toString());
								data.addSubfield(sub);
							}
						}
					}
					while ((child=child.getNextSibling()) != null);

					Record record = new RecordImpl();
					record.addVariableField(data);
					if(codes!=null && codes.contains("-") ){
						StringBuffer stringBuffer = new StringBuffer();
						String[] code = codes.split("\\-");
						for (int i = 0; i < code.length; i++) {
							String c = code[i];
							String map = null;
							if(code[i].contains("[")) {
								c = code[i].substring(code[i].indexOf("["));
								map = code[i].substring(code[i].indexOf("["),code[i].indexOf("]")-1);
							}
							List<String> values = xmlProfile.getValues(record, data.getTag(), c.split("\\|"));
							if (values != null && values.size() > 0) {
								String result = values.get(0);
								if(map!=null)
									result = normalizer.getMap(map, true, false).get(result);
								if (stringBuffer.length() > 0)
									stringBuffer.append(". ");
								stringBuffer.append(filter(result));
							}
						}
						return StringValue.makeStringValue(trim(stringBuffer.toString()));
					}
					else {
						List<String> values = xmlProfile.getValues(record, data.getTag(), codes != null ?
								codes.split("\\|") : null);
						if (values != null && values.size() > 0) {
							String result = values.get(0);
							return StringValue.makeStringValue(trim(""+filter(result)));
						}
					}
					return StringValue.makeStringValue("");

				}
				return null;
			}
		};
	}

	private String getTag(DOMNodeWrapper node) {
		if(node.getParent().getLocalPart().equals("DatiElementoAut") && node.getParent().getParent()!=null
				&& node.getParent().getParent().getParent()!=null){
			return (node.getParent().getParent().getParent().getAttributeValue("", "tipoLegame"));
		}
		return node.getLocalPart().substring(1);
	}

	private String trim(String result){
		if(result==null)
			return null;
		return result.trim();
	}
	private CharSequence filter(String result) {
		if(result==null)
			return null;
		return result.replaceAll("(\\<U\\+00)([a-z]|[0-9]){2}(\\>)","");
	}

}
