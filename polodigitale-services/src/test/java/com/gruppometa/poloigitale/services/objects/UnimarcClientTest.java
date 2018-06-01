package com.gruppometa.poloigitale.services.objects;

import static org.junit.Assert.*;

import org.junit.Test;

import com.gruppometa.poloigitale.services.components.UnimarcClient;

public class UnimarcClientTest {

	@Test
	public void test() {
		String filename ="/home/ingo/Progetti/TextData/poloigitale/IE001_NAP_BN_00019690.mrc";
		System.out.println(new UnimarcClient().getResponse(filename));
	}

}
