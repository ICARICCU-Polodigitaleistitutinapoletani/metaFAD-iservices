package com.gruppometa.sbnmarc.objects;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="records",namespace="http://polodigitale.it/iccd")
public class IccdRecordList {
	
	public static IccdRecordList getErrorList(String message){
		IccdRecordList iccdRecordList = new IccdRecordList();
		iccdRecordList.setMessage(message);
		iccdRecordList.setStatus("ko");
		return iccdRecordList;
	}
	protected String status = "Ok";
	protected String message = "";
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	protected List<IccdRecord> records = new ArrayList<IccdRecord>();

	@XmlElement(name="record",namespace="http://polodigitale.it/iccd")
	public List<IccdRecord> getRecords() {
		return records;
	}

	public void setRecords(List<IccdRecord> records) {
		this.records = records;
	}
}
