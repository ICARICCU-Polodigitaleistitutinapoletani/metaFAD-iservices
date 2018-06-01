package com.gruppometa.unimarc.object;

public class ConvertorStatus {
	protected String message;
	protected String status;
	protected int count;
	protected int scartati;
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getStatus() {
		return status;
	}
	public ConvertorStatus(String message, String status, int count, int scartati) {
		super();
		this.message = message;
		this.status = status;
		this.count = count;
		this.scartati = scartati;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getScartati() {
		return scartati;
	}
	public void setScartati(int scartati) {
		this.scartati = scartati;
	}
}	
