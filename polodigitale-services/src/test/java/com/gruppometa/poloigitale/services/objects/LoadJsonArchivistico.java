package com.gruppometa.poloigitale.services.objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by ingo on 03/11/16.
 */
public class LoadJsonArchivistico {
    public static void main(String[] args) {
        write(args,"ComplessoArchivistico","ca");
        //write(args,"UnitaDocumentaria","ud");
        //write(args,"UnitaArchivistica","ua");
        //write(args,"ProduttoreConservatore","au");
    }
    public static void write(String[] args,String tipo, String filenamesnippet){

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String url = "http://52.48.173.171/mibac_museowebfad_dev/bin";
            // nuovo url
            url = "http://52.48.173.171/mibac_museowebfad_dev/rest/getJson";
            url = url+"/"+tipo+"?module=archive";
            System.out.println("URL: "+url );
            JsonNode node = objectMapper.readTree(new URL(url ));
            String header = "<?xml version='1.0' encoding='UTF-8'?>\n" +
                    "<mappings xmlns=\"http://gruppometa.it/metafad\">";
            HashMap<String, OrderItem> map = new HashMap<String, OrderItem>();
            StringBuffer buf = new StringBuffer();
            buf.append(header);
            String searchType = "text";
            boolean multiple = false;
            HashSet<String> parents = new HashSet<>();
            ArrayNode tabs = (ArrayNode) node.get("tabs");
            /*
            for (int i = 0; i < tabs.size(); i++) {
                JsonNode tab = tabs.get(i);
                String tabId = tab.get("id").asText();
                ArrayNode fields = (ArrayNode) tab.get("fields");
                for (int j = 0; j < fields.size(); j++) {
                    JsonNode field = fields.get(j);
                    String parentId = getFieldId(tabId, field);
                    String typeField = field.get("type").asText();
                    if (typeField.equals("Fieldset")) {
                        ArrayNode children = (ArrayNode) field.get("children");
                        for (int k = 0; k < children.size(); k++) {
                            JsonNode child = children.get(k);
                            String childId = getFieldId(tabId, child);
                            parents.add(childId);
                        }
                    } else
                        parents.add(parentId);
                }
            }
            */
            for (int i = 0; i < tabs.size(); i++) {
                JsonNode tab = tabs.get(i);
                String tabLabel = tab.get("label").asText();
                String tabId = tab.get("id").asText();
                ArrayNode fields = (ArrayNode) tab.get("fields");
                for (int j = 0; j < fields.size(); j++) {
                    JsonNode field = fields.get(j);
                    String typeField = field.get("type").asText();
                    if (typeField.equals("Fieldset")) {
                        String parentId = getFieldId(tabId, field);
                        String parentLabel = getLabel(field);
                        ArrayNode children = (ArrayNode) field.get("children");
                        for (int k = 0; k < children.size(); k++) {
                            JsonNode child = children.get(k);
                            buf.append(makeNode(tabId, tabLabel, child, child.get("type").asText(), searchType, parentId, parentLabel));
                        }
                    } else {
                        buf.append(makeNode(tabId, tabLabel, field, typeField, searchType,null, null));
                    }
                }
            }


            buf.append("\n</mappings>");
            String dir = (args.length > 0 ? args[0] : "");
            if(dir.length()>0 && !dir.endsWith("/"))
                dir = dir+"/";
            String fileName = dir + "profile-polodigitale-archivio-fe-"+filenamesnippet+".xml";
            System.out.println("Write " + fileName + "."
                    //" -> "+buf.toString()
            );
            FileUtils.write(new File(fileName), buf.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    static int count=1;
    protected static String makeNode(String tabId, String tabLabel, JsonNode field, String typeField, String
                                    searchType, String parentId, String parentLabel){
        boolean multiple = typeField.equals("Repeater");
        System.out.println("Field:"+ field.get("id").asText());
        String item = "\n\t<mapping destination=\""+
                StringEscapeUtils.escapeXml11(getFieldId(tabId,field))+"\" " +
                " group=\"" + StringEscapeUtils.escapeXml11(tabLabel)+ "\""+
                " vistaEtichette=\""+(count++)+"\""+
                " solrFieldname=\""+StringEscapeUtils.escapeXml11(tabId+"_"+field.get("id").asText())+
                        (multiple?"_html_nxtxt":"_html_nxt")+"\""+
                " label=\""+StringEscapeUtils.escapeXml11(getLabel(field))+"\""+
                (parentId!=null?(" parent=\""+StringEscapeUtils.escapeXml11(parentId)+"\""):"")+
                (parentLabel!=null?(" parentLabel=\""+StringEscapeUtils.escapeXml11(parentLabel)+"\""):"")+
                " searchField=\"false\" is4Fe='true' multiple=\""+multiple+"\"/>";
        return item;
    }

    protected static String getLabel(JsonNode field) {
        if(field.get("label")!=null)
            return field.get("label").asText();
        if(field.get("attributes")!=null){
            if(field.get("attributes").get("label")!=null)
                return field.get("attributes").get("label").asText();
        }
        return field.get("id").asText();
    }

    protected static String getFieldId(String tabId, JsonNode field){
        return tabId+"_"+field.get("id").asText();
    }
}