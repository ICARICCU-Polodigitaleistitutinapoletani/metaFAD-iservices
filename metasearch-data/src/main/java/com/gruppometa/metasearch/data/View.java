package com.gruppometa.metasearch.data;

public class View extends FieldGroup{
	protected String viewType;
	public String getViewType() {
		return viewType;
	}

	public void setViewType(String viewType) {
		this.viewType = viewType;
	}

	protected String mimetype;
	
	public String getMimetype() {
		return mimetype;
	}

	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}

	@Override
	public String getType() {
		return "view";
	}

}
