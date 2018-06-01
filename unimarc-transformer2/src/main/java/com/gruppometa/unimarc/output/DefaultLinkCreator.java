package com.gruppometa.unimarc.output;

import com.gruppometa.unimarc.object.Field;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by ingo on 14/03/17.
 * Gestisce "Titolo uniforme", "Legame al livello piu' elevato (set)", "Collezione" e tutti i VID.
 */
public class DefaultLinkCreator implements  LinkCreator{

    protected static Logger logger = LoggerFactory.getLogger(DefaultLinkCreator.class);


    public String createLinks(Field field){
        StringBuffer buf = new StringBuffer();
        boolean printValue = false;
        /**
         * bid, solo ricerca per titolo uniforme
         */
        String titoloUniforme = "Titolo uniforme";
        //String titoloPadre = "Legame parte analitica - padre";
        boolean isLink = false;
        if(field.getBid()!=null){
            if(field.getName().equals(titoloUniforme)){
                buf.append(" "+ getQueryPageInit(getQuery4Bid(field.getBid(),"titolo_uniforme_txt")));
                isLink = true;
            }
            //else if(field.getName().equals(titoloPadre)){
            //    buf.append(" "+ getQueryPageInit(getQuery4Bid(field.getBid(),"ispart_of_s")));
            //}
            else{
                String fieldName = null;
                if(field.getName().equals("Legame al livello piu' elevato (set)")) {
                    fieldName = "legame_al_livello_piu_elevato_set__html_nxtxt";
                    //fieldName = "ispart_of_s"; // questo campo manca.....
                }
                if(field.getName().equals("Collezione"))
                    fieldName = "collezione_html_nxtxt";
                if(fieldName!=null) {
                    buf.append(
                            getQueryPageInit(getQuery4Bid(field.getBid(), fieldName))
                            //getLinkPageInit("BID", field.getBid())
                    );
                    isLink = true;
                }
                else{
                    /**
                     * POLODEBUG-664
                     */
                    /*
                    buf.append(getLinkPageInit("BID", field.getBid(), false));
                    isLink = true;
                    */
                    printValue = true;
                }
            }
        }
        /**
         * VID
         */
        if(field.getVid()!=null && isValidRole(field.getRole())) {
            buf.append(
                    getQueryPageInit(getQuery4Vid(field.getVid()))
                    //getLinkPageInit("VID", field.getVid())
            );
            isLink = true;
        }
        /**
         * fine link
         */
        if (buf.length() > 0 || printValue) {
            String str = field.getTextValue();
            String ending = " | "+field.getBid();
            if(field.getName().equals(titoloUniforme) && str.endsWith(ending))
                str = str.substring(0,str.length()-ending.length());
            int pos = str.lastIndexOf(" ; ");
            if (pos != -1)
                buf.append(StringEscapeUtils.escapeHtml4(str.substring(0, pos)) +
                        (isLink?"</a>":"") + StringEscapeUtils.escapeHtml4(str.substring(pos)));
            else
                buf.append(StringEscapeUtils.escapeHtml4(str) +
                        (isLink?"</a>":"")
                );
        }
        /**
         * button link per i VID
         */
        if(field.getVid()!=null && isValidRole(field.getRole())) {
           buf.append(
                   getLinkPageInit("VID", field.getVid(), true, "page_detail", true)+getButtonEnd()
                   //getQueryPage(getQuery4Vid(field.getVid()))
           );
           buf.append(getLinkPageInit("VID", field.getVid(),false,"page", false )+getIEnd());
        }

        if(field.getVid()!=null && !isValidRole(field.getRole())) {
            buf.append(StringEscapeUtils.escapeHtml4(field.getTextValue()));
        }
        /**
         * button link per alcuni BID
         */
        if(field.getBid()!=null){
            boolean modal = false;
            String fieldName = null;
            if(field.getName().equals("Legame al livello piu' elevato (set)")) {
                fieldName = "legame_al_livello_piu_elevato_set__html_nxtxt";
                //fieldName = "ispart_of_s"; // questo campo manca.....
            }
            if(field.getName().equals("Collezione")) {
                fieldName = "collezione_html_nxtxt";
                // POLODEBUG-658
                modal = false;
            }
            if(fieldName!=null)
                buf.append(
                        getLinkPageInit("BID", field.getBid(), modal )+
                                (modal?getButtonEnd():getIEnd())
                        //getQueryPage(getQuery4Bid(field.getBid(),fieldName))
                );
            else {
                // POLODEBUG-664
                buf.append(
                        getLinkPageInit("BID", field.getBid(),
                                true, "page_detail", true )+
                                getButtonEnd()
                        //getQueryPage(getQuery4Bid(field.getBid(),fieldName))
                );
            }
        }
        return buf.toString();
    }

    protected boolean isValidRole(String role) {
        if(role==null)
            return true;
        // 320 e 390
        if(role.toLowerCase().contains("donatore")
           ||role.toLowerCase().contains("possessore")
           ||role.toLowerCase().contains("320")
           ||role.toLowerCase().contains("390"))
            return false;
        return true;
    }

    protected String getLinkPageInit(String field, String id) {
        return  getLinkPageInit(field, id, true);
    }

    protected String getLinkPageInit(String field, String id, boolean modal) {
        return getLinkPageInit(field, id, modal, null, false);
    }

    protected String getLinkPageInit(String field, String id, boolean modal, String link, boolean blank) {
        try {
            if(modal)
                return " <a target=\"_blank\" href=\"${"+(link!=null?link:"page")+"}?"
                        +field+"=" +  URLEncoder.encode(id, "UTF-8") + "\">";
            else
                return " <a "+ (blank?"target=\"_blank\"":"")+" class=\"js-openhere\" data-modal=\"${"+(link!=null?link:"page")+"}?"
                        +field+"=" +  URLEncoder.encode(id, "UTF-8") + "\">";
        } catch (UnsupportedEncodingException e) {
            logger.error("",e);
        }
        return "";
    }

    protected String getQueryPageInit(String query) {
        try {
            return "<a href=\"${page_search}?query=" +
                    URLEncoder.encode(query, "UTF-8")
                    + "\">";
        } catch (UnsupportedEncodingException e) {
            logger.error("",e);
        }
        return "";
    }

    protected String getButtonEnd(){
        return "${button}</a>";
    }

    protected String getIEnd(){
        return "(i)</a>";
    }

    protected String getQueryPage(String query){
        String init = getQueryPageInit(query);
        if(init.length()>0)
            return " "+getQueryPageInit(query)+getButtonEnd();
        else
            return "";
    }

    protected String template = "{\"query\":{\"clause\":{\"type\":\"SimpleClause\",\"operator\":" +
            "{\"operator\":\"AND\"},\"field\":\"Tutto\",\"innerOperator\":" +
            "{\"operator\":\"contains one\"},\"values\":[\"*\"]}," +
            "\"start\":0,\"rows\":10,\"facetLimit\":100,\"facetMinimum\":1," +
            "\"filters\":[" +
            "{\"type\":\"SimpleClause\",\"operator\":" +
            "{\"operator\":\"AND\"},\"field\":\"${FIELDNAME}\"," +
            "\"innerOperator\":{\"operator\":\"AND\"},\"values\":[\"${VID}\"]}" +
            //",{\"type\":\"SimpleClause\",\"operator\":{\"operator\":\"AND\"}," +
            //"\"field\":\"responsabilita_principale_html_nxtxt\"," +
            //"\"innerOperator\":{\"operator\":\"AND\"},\"values\":" +
            //"[\"\\\"[pippo]\\\"\"]}" +
            "],\"facets\":null,\"orderClauses\":null," +
            "\"fq\":null,\"fieldNamesAreNative\":false}}";

    /**
     * ricerca per le opere dell'autore
     * @param vid
     * @return query
     */
    protected String getQuery4Vid(String vid) {
        String fieldname = "responsabilita_principale_html_nxtxt";
        //fieldname = "ispart_of_s";
        return template
                .replace("${FIELDNAME}",fieldname)
                .replace("${VID}", vid);
    }

    /**
     * ricerca per fratelli
     * @param bid
     * @return query
     */
    protected String getQuery4Bid(String bid, String fieldname) {
        return template
                .replace("${FIELDNAME}", fieldname)
                .replace("${VID}", bid);
    }
}
