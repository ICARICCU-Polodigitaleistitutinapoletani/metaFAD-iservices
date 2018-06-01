package com.gruppometa.sbnmarc.mag;

import org.junit.Test;
import org.w3c.dom.Document;

/**
 * Created by ingo on 02/03/17.
 */
public class TestUniMarc {

    @Test
    public void test(){

        MagTransformer magTransformer = new MagTransformer();

        /**
         * unimarc da OPAC SBN
         */
        UnimarcClient unimarcClient = new UnimarcClient();
        String mag = null;

        try {
           mag = unimarcClient.getOpacSbn2X(magTransformer,
                   //"MUS0001212" // MUS0001212,MUS0035133
                   //"MUS0002804"
                   //"MUS0001212"
                   //"MSM0000069"
                   //"MSM0000424"
                   //"MSM0000722"
                   //"MUS0056776"
                   //"NAP0668034"
                   "NAP0138823"
                   , true,false
           );
        } catch (Exception e) {
            e.printStackTrace();
        }

        //System.out.println(mag);
        //if(true)
         //   return;

        /**
         * SBNMARC dal polo
         */
        SbnMarcClient sbnMarcClient = new SbnMarcClient();
        sbnMarcClient.setUsername("pmdigi");
        sbnMarcClient.setUrl("http://sbnweb.bnnonline.it/SbnMarcWeb/SbnMarcTest");

        String mag2 = null;
        try {
            //sbnMarcClient.getResponseAsString(biblioteca,bid,type,version,false);
            mag2 = sbnMarcClient.getSbnMarc2Mag(magTransformer,
                    "NAP0138823"
                    //"NAP0668034"
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(mag2);

    }
}
