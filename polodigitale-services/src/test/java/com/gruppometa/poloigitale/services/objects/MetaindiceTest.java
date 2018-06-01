package com.gruppometa.poloigitale.services.objects;

import com.gruppometa.poloigitale.services.jobs.SolrOutputFormatter4Metaindice;
import com.gruppometa.unimarc.MarcConvertor;
import com.gruppometa.unimarc.object.DefaultOutput;
import com.gruppometa.unimarc.object.Output;
import com.gruppometa.unimarc.output.DefaultLinkCreator;
import com.gruppometa.unimarc.output.OutputFormatter;
import com.gruppometa.unimarc.output.SolrOutputFormatter;
import com.gruppometa.unimarc.profile.NaXmlProfile;
import com.gruppometa.unimarc.profile.Profile;
import com.gruppometa.unimarc.profile.XmlProfile;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

public class MetaindiceTest {
    //Test
    public void test(){
        XmlProfile pro = new NaXmlProfile("/naProfile.xml");
        pro.init();
        MarcConvertor convertor = new MarcConvertor();
        convertor.setProfile(pro);
        Output out = new DefaultOutput();
        String filename = "/home/ingo/Scaricati/IE001_NAP_BN_00024527.mrc";
        File inFile = new File(filename);
        convertor.getProfile().setFilename(inFile.getName());
        convertor.getProfile().notifyFullFilename(filename);
        OutputFormatter formatter = null;
        formatter = new SolrOutputFormatter4Metaindice(out);
        ((SolrOutputFormatter)formatter).setSolrUrl("http://localhost:8983/solr/metaindice_dev");
        //formatter = new SolrOutputFormatter(out);
        //((SolrOutputFormatter)formatter).setLinkCreator(new DefaultLinkCreator());
        //((SolrOutputFormatter)formatter).setSolrUrl("http://localhost:8983/solr/polodigitale_fe_dev");
        convertor.getProfile().setForFe(true);
        try {
            FileInputStream fin = new FileInputStream(inFile);
            String id = "NAP0737971";
            ((XmlProfile)pro).setFilterId(id);
            convertor.convert(fin, formatter);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
