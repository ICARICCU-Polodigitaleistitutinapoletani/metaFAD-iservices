package com.gruppometa.sbnmarc;

import static org.junit.Assert.*;

import org.junit.Test;

import com.gruppometa.sbnmarc.objects.IccdException;


public class SbnMarcClientTest {

	@Test
	public void test() {
		SbnMarcClient client = new SbnMarcClient();
		String bib = "SP";
		String bid= "RML0028972"; // 
		//bid= "RMS0009664";
		bid="NAP0689822";
		bid="NAP0696046";
		bid ="NAP0724169";
		//client.setTestBib("IC"); // per avere dei dati
		// collaudo
		client.setPasswordPosseduti("polodigitale15");
		client.setUsernamePosseduti("iccerl");
		// produzione
		client.setPasswordPosseduti("pmdigi16");
		client.setUsernamePosseduti("pmdigi");
		client.setUrlPosseduti("http://sbnweb.bnnonline.it/sbn/api/1.0/docfisico/posseduto/");
		try {
			System.out.println(client.getResponseAsString(bib, bid));
		} catch (IccdException e) {
			e.printStackTrace();
		}
	}

}
