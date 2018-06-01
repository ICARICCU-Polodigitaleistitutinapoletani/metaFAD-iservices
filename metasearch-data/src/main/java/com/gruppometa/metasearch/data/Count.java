package com.gruppometa.metasearch.data;

public class Count {
	protected long count;
	protected String name;
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Count(long count, String name) {
		super();
		this.count = count;
		this.name = name;
	}
	
}
