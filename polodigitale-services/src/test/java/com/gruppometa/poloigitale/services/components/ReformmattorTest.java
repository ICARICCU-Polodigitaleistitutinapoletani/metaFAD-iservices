package com.gruppometa.poloigitale.services.components;

import com.gruppometa.metasearch.data.Document;
import com.gruppometa.metasearch.data.Field;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.junit.Test;


import java.nio.file.Files;
import java.nio.file.Paths;

public class ReformmattorTest {
    @Test
    public void test(){
        try {
            String encoding = "UTF-8";
            String path = "/opt/Progetti/polodigitale-services/src/test/resources/test-copie.xml";
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            String str = new String(encoded, encoding);
            Reformattor reformattor = new Reformattor();
            Document document = new Document();
            Field field = new Field();
            field.setId("Livello bibliografico");
            field.getValues().add("monografia");
            document.getNodes().add(field);
            System.out.println(reformattor.reformat(str,1, document, Lists.newArrayList("CR")));

        }
        catch(Exception e){
            e.printStackTrace();
        }

    }
}
