package com.gruppometa.poloigitale.services.objects;

import com.gruppometa.poloigitale.services.components.MappedSolrQueryCreator;
import com.gruppometa.unimarc.object.OutItem;
import com.gruppometa.unimarc.object.Output;
import com.gruppometa.unimarc.output.SolrOutputFormatter;
import org.junit.Test;

import java.util.List;

/**
 * Created by ingo on 05/08/16.
 */
public class TestSolr{

        @Test
        public void test() {
            List<String> tokens = MappedSolrQueryCreator.getSolrTokens("hello rest \"rer ell\"");
            for (String s: tokens) {
                System.out.println(s);
            }
            System.out.println(new SolrOutputFormatter(new Output() {
                @Override
                public List<OutItem> getItems() {
                    return null;
                }

                @Override
                public void addItem(OutItem desc) {

                }
            }).makeSolrName(new String("Bene (cosa)")));
        }
}