package com.gruppometa.unimarc.profile;

import static org.junit.Assert.*;

import org.junit.Test;

import com.gruppometa.unimarc.object.DefaultOutItem;

public class RecordCacheTest {

	@Test
	public void testPut() {
		//new RecordCache().put("test","Bib","inv");
		CilentoProfile pro =  new CilentoProfile("test_wewew_wewew");
		pro.setFilename("sdsd_saaaaa_asasaa.pdf");
		System.out.println(""+pro.getLocation());
	}

	@Test
	public void testGet() {
		//System.out.println( new RecordCache().getLocations("test").get(0) );
	}

}
