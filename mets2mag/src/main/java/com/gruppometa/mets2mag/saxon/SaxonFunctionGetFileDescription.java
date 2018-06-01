package com.gruppometa.mets2mag.saxon;

import com.gruppometa.mets2mag.FileDescriptionRepository;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.value.StringValue;
import net.sf.saxon.value.TextFragmentValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class SaxonFunctionGetFileDescription extends SaxonFunctionGetTypeFromLeader{

	protected FileDescriptionRepository fileDescriptionRepository;

	protected static final Logger logger = LoggerFactory.getLogger(SaxonFunctionGetFileDescription.class);

	public FileDescriptionRepository getFileDescriptionRepository() {
		return fileDescriptionRepository;
	}

	public void setFileDescriptionRepository(FileDescriptionRepository fileDescriptionRepository) {
		this.fileDescriptionRepository = fileDescriptionRepository;
	}

	public SaxonFunctionGetFileDescription(FileDescriptionRepository fileDescriptionRepository) {
		this.fileDescriptionRepository = fileDescriptionRepository;
	}

	public SequenceType[] getArgumentTypes() {
		return new SequenceType[] { SequenceType.SINGLE_STRING, SequenceType.SINGLE_STRING};
	}


	public String getValue(String baseDir,String path) {
		if(getFileDescriptionRepository()!=null)
			return getFileDescriptionRepository().getJsonInfo(baseDir, path);
		else
			return "";
	}

	@Override
	protected String getFunctionName() {
		return "getFileDescription";
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
