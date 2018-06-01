package com.gruppometa.poloigitale.services.objects;

import it.sbnweb.kardex.KardexType;

public class KardexResponse {
	
	protected boolean containsKardex = false;
	protected String isbd;
	protected String bid;
	protected String inventario;
	protected String collocazione;
	
	public String getInventario() {
		return inventario;
	}

	public void setInventario(String inventario) {
		this.inventario = inventario;
	}

	public String getCollocazione() {
		return collocazione;
	}

	public void setCollocazione(String collocazione) {
		this.collocazione = collocazione;
	}

	public String getBid() {
		return bid;
	}

	public void setBid(String bid) {
		this.bid = bid;
	}

	public String getIsbd() {
		return isbd;
	}

	public void setIsbd(String isbd) {
		this.isbd = isbd;
	}

	public boolean isContainsKardex() {
		return containsKardex;
	}

	public void setContainsKardex(boolean containsKardex) {
		this.containsKardex = containsKardex;
	}

	protected String message ;
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	protected long time = System.currentTimeMillis();
	
	protected KardexType kardexType;
	
	public KardexType getKardexType() {
		return kardexType;
	}

	public void stopTime(){
		time = System.currentTimeMillis()- time;
	}
	public void setKardexType(KardexType kardexType) {
		this.kardexType = kardexType;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
	
}
