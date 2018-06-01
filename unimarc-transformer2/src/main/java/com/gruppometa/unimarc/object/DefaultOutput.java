package com.gruppometa.unimarc.object;

import java.util.ArrayList;
import java.util.List;


public class DefaultOutput implements Output{
	protected List<OutItem> items = new ArrayList<OutItem>();
	public void addItem(OutItem item){
		items.add(item);		
	}
	/**
	 * @return the items
	 */
	public List<OutItem> getItems() {
		return items;
	}
	/**
	 * @param items the items to set
	 */
	public void setItems(List<OutItem> items) {
		this.items = items;
	}
	
}
