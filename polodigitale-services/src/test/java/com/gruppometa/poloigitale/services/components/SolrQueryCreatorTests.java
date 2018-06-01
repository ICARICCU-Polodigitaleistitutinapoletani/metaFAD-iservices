package com.gruppometa.poloigitale.services.components;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ingo on 22/03/17.
 */
public class SolrQueryCreatorTests {
    @Test
    public void test(){
        MappedSolrQueryCreator mappedSolrQueryCreator = new MappedSolrQueryCreator();
        List<String> values = new ArrayList<>();
        values.add("IX");
        values.add("X");
        System.out.print(
            mappedSolrQueryCreator.createRangeQueryPart("secolo","secolo_end",values,"cen__tury")
        );
    }
}
