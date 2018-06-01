package com.gruppometa.poloigitale.services.objects;

import com.gruppometa.unimarc.object.ConvertorStatus;

public class Message {
	protected String message;
	protected String status;
	protected ConvertorStatus convertorStatus;
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public ConvertorStatus getConvertorStatus() {
		return convertorStatus;
	}

	public void setConvertorStatus(ConvertorStatus convertorStatus) {
		this.convertorStatus = convertorStatus;
	}


	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
