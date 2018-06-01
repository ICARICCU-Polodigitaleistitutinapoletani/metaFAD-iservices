package com.gruppometa.poloigitale.services.jobs;

import com.gruppometa.unimarc.object.Output;
import org.apache.solr.common.SolrInputDocument;

/**
 * Created by ingo on 07/11/16.
 */
public class SolrOutputFormatter4MetaindiceAu extends  SolrOutputFormatter4Metaindice {
    public SolrOutputFormatter4MetaindiceAu(Output output) {
        super(output);
    }

    @Override
    protected SolrInputDocument filterDoc(SolrInputDocument doc){
        SolrInputDocument doc2 = new SolrInputDocument();
        doc2.setField("tiposcheda_s","SBN AUT");
        doc2.setField("tiposcheda_t","SBN AUT");

        doc2.setField("dominio_s","bibliografico");
        doc2.setField("dominio_t","bibliografico");

        copyAll(doc,doc2,"all_txt");
        doc2.setField("id", doc.getFieldValue("id"));
        doc2.setField("file", doc.getFieldValue("file"));
        doc2.setField("mapversion", doc.getFieldValue("mapversion"));

        String tipo = (String)doc.getFieldValue("tipo_di_scheda_s");
        if(tipo!=null && tipo.toLowerCase().contains("ente")) {
            doc2.setField("tipoentita_s", "ente");
            doc2.setField("tipoentita_t", "ente");
        }
        else{
            doc2.setField("tipoentita_s", "persona");
            doc2.setField("tipoentita_t", "persona");
        }

        //copy(doc,doc2,"tipo_di_scheda_s","tipoEntita");

        copy(doc,doc2,"nome_di_persona_t","denominazione_nome",true);
        copy(doc,doc2,"nome_t","denominazione_nome",true);
        copy(doc,doc2,"nome_di_gruppo_ente_t","denominazione_nome",true);
        copy(doc,doc2,"forme_varianti_txt","denominazione_nome",true);

        copy(doc,doc2,"nome_di_persona_t","denominazione_nome_sintetica",true);
        copy(doc,doc2,"nome_di_gruppo_ente__t","denominazione_nome_sintetica",true);

        copy(doc,doc2,"nascita_s","datada");
        copy(doc,doc2,"morte_s","dataa");
        copy(doc,doc2,"datazione_ss","datazione");

        // diventa multiple 11-04-2017
        copy(doc,doc2,"ruolo_s","ruolo", true);

        return doc2;
    }

}
