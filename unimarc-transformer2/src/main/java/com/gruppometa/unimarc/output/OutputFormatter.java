package com.gruppometa.unimarc.output;

import java.io.IOException;

import com.gruppometa.unimarc.object.Output;
import com.gruppometa.unimarc.profile.Profile;
import com.gruppometa.unimarc.profile.RecordCache;

public interface OutputFormatter {

	void toXml(Appendable buf) throws IOException;

	void setOutfile(Appendable outfile);

	Output getOutput();

	void notifyAdd(Profile profile) throws IOException;
	
	void notifyInit()throws IOException;
	void notifyEnd()throws IOException;
	void setBufferSize(int bufferSize);
	void setRecordCache(RecordCache cache);
}
