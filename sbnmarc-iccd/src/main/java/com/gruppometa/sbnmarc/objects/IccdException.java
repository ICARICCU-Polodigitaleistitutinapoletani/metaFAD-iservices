package com.gruppometa.sbnmarc.objects;

public class IccdException extends Exception{
	protected String message;
	public IccdException(String message) {
		this.message = message;		
	}

	
	
	@Override
	public String getMessage() {
		return message;
	}



	/**
	 * 
	 */
	private static final long serialVersionUID = 2017604226183115379L;

}
