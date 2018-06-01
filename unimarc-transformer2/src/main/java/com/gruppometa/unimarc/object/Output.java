package com.gruppometa.unimarc.object;


import java.util.List;


public interface Output {
	
	public List<OutItem> getItems();

	public void addItem(OutItem desc);
}
