package com.gruppometa.unimarc.maps;

import java.util.HashMap;

public class Genere {
	public static HashMap<String, String> generi = new HashMap<String, String>();
	static {
		generi.put("A",  "BIBLIOGRAFIE");
		generi.put("AA", "OPERE RELIGIOSE");
		generi.put("AB", "CATECHISMO");
		generi.put("AC", "LETTERATURA DEVOZIONALE");
		generi.put("AD", "SERMONI");
		generi.put("AE", "LIBRI LITURGICI");
		generi.put("B",  "CATALOGHI");
		generi.put("BA", "OPERE SCIENTIFICHE");
		generi.put("BB", "DISSERTAZIONI");
		generi.put("C",  "INDICI");
		generi.put("CA", "USI E COSTUMI");
		generi.put("D",  "SOMMARI");
		generi.put("DA", "OPERE LEGALI");
		generi.put("DB", "OPERE POLITICHE");
		generi.put("E",  "DIZIONARI");
		generi.put("EA", "MATERIALE EFFIMERO");
		generi.put("F",  "ENCICLOPEDIE");
		generi.put("FA", "OPERE DI CONSULTAZIONE");
		generi.put("FB", "CATALOGHI DI BIBLIOTECA");
		generi.put("FC", "BIBLIOGRAFIE");
		generi.put("FD", "CALENDARI");
		generi.put("FE", "INDICI");
		generi.put("FF", "DIZIONARI");
		generi.put("FG", "ENCICLOPEDIE");
		generi.put("G",  "LISTE");
		generi.put("GA", "OPERE STORICHE");
		generi.put("H",  "ANNUARI");
		generi.put("HA", "TRATTATI POLEMICI");
		generi.put("I",  "STATISTICHE");
		generi.put("IA", "OPERE DISCORSIVE");
		generi.put("J",  "BIOGRAFIE");
		generi.put("JA", "OPERE DI CIRCOSTANZA");
		generi.put("K",  "BREVETTI");
		generi.put("KA", "LIBRI DI ISTRUZIONE");
		generi.put("KB", "MANUALI");
		generi.put("KC", "LIBRI DI TESTO");
		generi.put("L",  "NORME STANDARDIZZATE");
		generi.put("LA", "LISTE DI PREZZI");
		generi.put("M",  "TESI O DISSERTAZIONI");
		generi.put("MA", "OPERE DI SVAGO");
		generi.put("N",  "LEGGI E LEGISLAZIONE");
		generi.put("NA", "DIFFERENTI VERSIONI DI UN'OPERA");
		generi.put("O ", "TABELLE");
		generi.put("P",  "RENDICONTI TECNICI");
		generi.put("Q",  "RECENSIONI");
		generi.put("R",  "LETTERATURA PER RAGAZZI");
		generi.put("S",  "MOSTRE");
		generi.put("T",  "CARTOGRAFIA MANOSCRITTA");
		generi.put("W",  "TESTO LITURGICO");
		generi.put("X",  "RISORSA ELETTRONICA");
		generi.put("Y",  "MATERIALE CARTOGRAFICO");
		generi.put("YY", "MATERIALE CARTOGRAFICO");
		generi.put("Z",  "ATTI DI CONGRESSI / ALTRO");
		generi.put("ZZ", "ALTRO");

	}
	public static String getGenereFromCodice(String codice){
		if(codice==null)
			return null;
		String ret = generi.get(codice.toUpperCase());
		if(ret==null)
			return codice;
		else
			return ret;
	}
}
