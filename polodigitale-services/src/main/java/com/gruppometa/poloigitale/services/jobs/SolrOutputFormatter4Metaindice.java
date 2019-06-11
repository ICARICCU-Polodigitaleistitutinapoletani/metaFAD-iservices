package com.gruppometa.poloigitale.services.jobs;

import com.gruppometa.unimarc.object.Output;
import com.gruppometa.unimarc.output.SolrOutputFormatter;
import org.apache.solr.common.SolrInputDocument;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by ingo on 02/11/16.
 */
public class SolrOutputFormatter4Metaindice extends SolrOutputFormatter{

    public SolrOutputFormatter4Metaindice(Output output) {
        super(output);
    }

    @Override
    protected SolrInputDocument filterDoc(SolrInputDocument doc){

        SolrInputDocument doc2 = new SolrInputDocument();

        doc2.setField("dominio_s","bibliografico");
        doc2.setField("dominio_t","bibliografico");

        copyAll(doc,doc2,"cosa_txt");
        doc2.setField("id", doc.getFieldValue("id"));
        doc2.setField("file", doc.getFieldValue("file"));
        doc2.setField("mapversion", doc.getFieldValue("mapversion"));

        String tipo_documento = (String)doc.getFieldValue("tipo_documento_s");
        /**
         * POLODEBUG-479
         */
        //doc2.setField("tipospecificodoc_s",doc.getFieldValue("livello_bibliografico_s"));
        //doc2.setField("tipospecificodoc_t",doc.getFieldValue("livello_bibliografico_s"));
        if(tipo_documento!=null) {
            String val = tipo_documento;
            if (tipo_documento.toLowerCase().contains("grafico")) {
                val = "grafica";
                if (doc.getFieldValues("116_specifica_del_materiale_ss") != null) {
                    for (Iterator<Object> it = doc.getFieldValues("116_specifica_del_materiale_ss").iterator(); it.hasNext(); ) {
                        String v = (String) it.next();
                        if (v.contains("foto")) {
                            val = "fotografia";
                        } else if (v.contains("disegno")) {
                            val = "disegno";
                        } else if (v.contains("stampa")) {
                            val = "stampa";
                        }
                    }
                }
            } else if (tipo_documento.toLowerCase().contains("a manoscritta"))
                val = "manoscritto";
            //else if(tipo_documento.toLowerCase().contains("cartografi"))
            //    val = "cartografia";
            doc2.addField("tipospecificodoc_s", val);
            doc2.addField("tipospecificodoc_t", val);
        }
        else{
            String val = "testo a stampa";
            doc2.addField("tipospecificodoc_s", val);
            doc2.addField("tipospecificodoc_t", val);
        }

        /**
         * POLODEBUG-216
         */
        boolean useSubTypes = false;
        if(tipo_documento!=null){
            String val = "libri";
            if(tipo_documento.toLowerCase().contains("grafico")) {
                val = "grafica";
                if(useSubTypes && doc.getFieldValues("116_specifica_del_materiale_ss")!=null) {
                    for (Iterator<Object> it = doc.getFieldValues("116_specifica_del_materiale_ss").iterator(); it.hasNext(); ) {
                        String v = (String) it.next();
                        if (v.contains("foto")) {
                            val = "fotografie";
                        } else if (v.contains("disegno")) {
                            val = "disegni";
                        } else if (v.contains("stampa")) {
                            val = "stampe";
                        }
                    }
                }
            }
            else if(tipo_documento.toLowerCase().contains("a manoscritta"))
                val = "manoscritti";
            //else if(tipo_documento.toLowerCase().contains("cartografi"))
            //    val = "cartografia";
            doc2.addField("area_digitale_ss",val);
            doc2.addField("area_digitale_txt",val);
        }

        copy(doc,doc2,"localizzazione_ss","localizzazione");
        copy(doc,doc2,"titoli_tutti_txt","denominazione_titolo");
        //
        // copyToStartsWithField(doc,doc2,"titoli_tutti_txt","denominazione_titolo");
        copy(doc,doc2,"titolo_proprio_txt","titolo_proprio");
        copy(doc,doc2,"collezione_sintetica__ss","collezione");
        copy(doc,doc2,"legame_al_livello_piu_elevato_set__ss","fa_parte_di");
        copy(doc,doc2,"responsabilita_tutte_txt","responsabilita");
        copy(doc,doc2,"responsabilita_tutte_txt","responsabilita_accettate");
        deleteValues(doc, doc2, "responsabilita_accettate_ss","nome_di_persona_forma_non_accettata_ss");
        deleteValues(doc, doc2, "responsabilita_accettate_txt","nome_di_persona_forma_non_accettata_ss");
        deleteValues(doc, doc2, "responsabilita_accettate_ss","nome_di_gruppo_forma_non_accettata_ss");
        deleteValues(doc, doc2, "responsabilita_accettate_txt","nome_di_gruppo_forma_non_accettata_ss");
        copy(doc,doc2,"autore_txt","autore");
        copy(doc,doc2,"ruolo_ss","ruolo");
        copy(doc,doc2,"tipo_seriale_s","tipo_seriale");
        copy(doc,doc2,"data_inizio_ss","estremo_remoto");
        copy(doc,doc2,"data_fine_ss","estremo_recente");
        copy(doc,doc2,"data_range_ss","cronologia_range");
        copy(doc,doc2,"soggetto_ss","soggetto");
        copy(doc,doc2,"lingua_ss","lingua");
        copy(doc,doc2,"responsabilita_tutte_txt","chi");
        copy(doc,doc2,"responsabilita_materiale_txt","chi");
        copy(doc,doc2,"data_da_ss","quando");
        copy(doc,doc2,"data_range_ss","quando");
        copy(doc,doc2,"luogo_ss","dove");
        copySortField(doc,doc2,"titoli_tutti_txt","title_t");
        copy(doc,doc2,"pubblicazione_s","description");
        copySortField(doc,doc2,"responsabilita_tutte_txt", "autore_ordinamento_s");
        copySortField(doc,doc2,"titoli_tutti_txt", "titolo_ordinamento_s");
        return doc2;
    }

    protected void deleteValues(SolrInputDocument doc, SolrInputDocument doc2, String destFieldName, String fieldname) {
        if(doc.getFieldValues(fieldname)==null)
            return;
        for (Object v: doc.getFieldValues(fieldname)){
            v = getMappedValue(fieldname,v);
            if(doc2.getFieldValue(destFieldName)!=null && doc2.getFieldValues(destFieldName).contains(v))
                doc2.getFieldValues(destFieldName).remove(v);
        }
    }

    protected void copySortField(SolrInputDocument doc, SolrInputDocument doc2, String fieldname, String sortField){
        if(doc.getFieldValues(fieldname)==null)
            return;
        for (Object v: doc.getFieldValues(fieldname)){
            v = getMappedValue(fieldname,v);
            if(doc2.getFieldValue(sortField)!=null)
                v = doc2.getFieldValue(sortField)+  " " + v;
            doc2.setField(sortField,v);
        }
    }

    protected void copy(SolrInputDocument doc, SolrInputDocument doc2, String fieldname, String fieldname2){
        copy(doc, doc2, fieldname, fieldname2, false);
    }

    protected void copy(SolrInputDocument doc, SolrInputDocument doc2, String fieldname, String fieldname2, boolean forceMultiple){
        if(doc.getFieldValues(fieldname)==null)
            return;
        for (Object v: doc.getFieldValues(fieldname)){
            String suf = "_s";
            String suf2 = "_t";
            if(forceMultiple || fieldname.endsWith("_ss")||fieldname.endsWith("_txt")){
                suf = "_ss";
                suf2 = "_txt";
            }
            v = getMappedValue(fieldname2,v);
            doc2.addField(fieldname2+suf,v);
            doc2.addField(fieldname2+suf2,v);
        }
    }

    protected void copyToStartsWithField(SolrInputDocument doc, SolrInputDocument doc2, String fieldname, String fieldname2){
        if(doc.getFieldValues(fieldname)==null)
            return;
        for (Object v: doc.getFieldValues(fieldname)){
            String suf = "_low";
            if(fieldname.endsWith("_ss")||fieldname.endsWith("_txt")){
                suf = "_lows";
            }
            v = getMappedValue(fieldname2,v);
            doc2.addField(fieldname2+suf,v);
        }
    }

    protected Object getMappedValue(String fieldname, Object v) {
        if(v==null)
            return null;
        if(fieldname.equals("localizzazione")){
            if(v.equals("Biblioteca del Pio Monte della Misericordia"))
                return "Pio Monte della Misericordia";
            if(v.equals("Società napoletana di storia patria"))
                return "Società Napoletana di Storia Patria";
            if(v.equals("Archivio del Tesoro di San Gennaro - Biblioteca"))
                return "Cappella del Tesoro di San Gennaro";
        }
        return v;
    }

    protected void copyAll(SolrInputDocument doc, SolrInputDocument docs2, String destination){
        HashSet<String> checkSet = new HashSet<>();
        for (String name: doc.getFieldNames()){
            if(!name.contains("unimarc") && !name.contains("_html_") && !name.equals("mapversion")
                    && !name.equals("timestamp") && !name.equals("file")) {
                for (Object v : doc.getFieldValues(name)) {
                    String str = ""+v;
                    if(checkSet.contains(str))
                        continue;
                    docs2.addField(destination,str);
                    checkSet.add(str);
                }
            }
        }
    }
}
