package com.gruppometa.poloigitale.services.controllers;

import com.gruppometa.metasearch.data.*;
import org.apache.commons.collections.ArrayStack;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.SystemProfileValueSource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ingo on 26/08/16.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("desktop")
public class TestFields {

    @Autowired
    protected SolrSearchController solrSearchController;

    @Autowired
    protected SolrResponseCreator solrResponseCreator;
    @Test
    public void dummy(){

    }
    //@Test
    public void test() throws IOException {
        URL url = getClass().getResource("/lista-campi.txt");
        solrResponseCreator.init();
        FieldList fieldList = solrSearchController.getFields("","sbn");
        List<String> list = FileUtils.readLines(new File(url.getFile()));
        List<String> listId = new ArrayList<String>();
        List<String> facets = new ArrayList<String>();
        for (Field f: fieldList.getFields()){
            listId.add(f.getId());
            if(f.isFacet())
                facets.add(f.getId());
            if(!list.contains(f.getId()) && f.getId().indexOf("_")!=3)
                System.out.println("Missing mapping: "+f.getId());
        }
        for (String s : list){
            if(!listId.contains(s))
                System.out.println("Missing field: "+s);
        }
        List<Count> counts = new ArrayList<Count>();
        for (String id: facets) {
            SearchResponse response = solrSearchController.getFacets(id,"",100,0,"opac");
            long c = -1;
            if(response.getResponse()==null
                    || response.getResponse().getFacetsFields()==null
            )
                    c = -1;
            else
                c = response.getResponse().getFacetsFields().size()>0?
                    response.getResponse().getFacetsFields().get(0).getValues().size():0;
            Count count = new Count(c,id);
            counts.add(count);
        }
        for (Count c: counts){
            System.out.println(c.getName()+"\t -> \t"+c.getCount());
        }
    }

}
