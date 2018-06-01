package com.gruppometa.poloigitale.services.objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.gruppometa.unimarc.profile.XmlProfile;
import org.apache.commons.collections.iterators.ArrayListIterator;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Created by ingo on 19/09/16.
 */
public class LoadXmlFromJson {

    protected static boolean iccdSchedeActive = false;
    protected static boolean iccdSchedeAutActive = true;
    public static void main(String[] args){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String dir = (args.length>0?args[0]:"");
            if(dir.length()>0 && !dir.endsWith("/"))
                dir = dir+"/";
            String url = "http://52.48.173.171/mibac_museowebfad_dev/bin";
            // nuovo url
            url = "http://52.48.173.171/mibac_museowebfad_dev/rest/getJson/";
            // nuovo url 2
            url = "http://www.polodigitalenapoli.it/mibac_museowebfad_dev/rest/getJson/";
            JsonNode node = null;
            String header = "<?xml version='1.0' encoding='UTF-8'?>\n" +
                    "<mappings xmlns=\"http://gruppometa.it/metafad\">";

            if(iccdSchedeActive) {
                node = objectMapper.readTree(new URL(url + "/generateICCDModules/config/polo_na.json"));
                HashMap<String, OrderItem> map = new HashMap<String, OrderItem>();
                List<Set<String>> sets = new ArrayList<Set<String>>();
                XmlProfile profile4lookup = new XmlProfile("/profile-polodigitale-iccd-search-all-defaults.xml");
                profile4lookup.init();

                /**
                 * campi per la ricerca avanzata
                 */
                if (true && node instanceof ArrayNode) {
                    ArrayNode arr = (ArrayNode) node;
                    for (int i = 0; i < arr.size(); i++) {
                        String modName = arr.get(i).get("moduleName").asText();
                        if (modName.startsWith("Scheda")) {
                            Set<String> set = new TreeSet<String>();
                            sets.add(set);
                            int n = 0;
                            String scheda = modName.substring(7, 9).trim();
                            StringBuffer buf = new StringBuffer();
                            buf.append(header);
                            JsonNode fe_mapping = node.get(i).get("feMapping");
                            for (Iterator<String> it = fe_mapping.fieldNames(); it.hasNext(); ) {
                                String field = it.next();
                                boolean multiple = true;
                                ArrayNode arr2 = (ArrayNode) fe_mapping.get(field);
                                for (int j = 0; j < arr2.size(); j++) {
                                    if (arr2.get(j).asText().startsWith("sub"))
                                        multiple = false;
                                }
                                String searchType = null;
                                if (profile4lookup.getDefinition(field) != null)
                                    searchType = profile4lookup.getDefinition(field).getSearchType();
                                if (searchType == null)
                                    searchType = "text";
                                String item = "\n\t<mapping destination=\"" + field + "\" " +
                                        "searchType=\"" + searchType + "\" " +
                                        "searchField=\"true\" is4Fe='true' multiple=\"" + multiple + "\"/>";
                                //map.put(field, new OrderItem(item,++n));
                                set.add(item);
                                buf.append(item);
                                //System.out.println(""+field);
                            }
                            buf.append("\n</mappings>");
                            String fileName = dir + "profile-polodigitale-iccd-search-" + scheda.toLowerCase() + ".xml";
                            System.out.println("Write " + fileName + "."
                                    //" -> "+buf.toString()
                            );
                            FileUtils.write(new File(fileName), buf.toString());
                        }

                    }

                    if (sets.size() > 0) {
                        StringBuffer buf = new StringBuffer();
                        buf.append(header);
                        Set<String> set = sets.remove(0);
                        for (Set set2 : sets) {
                            set.retainAll(set2);
                        }
                        for (String s : set) {
                            buf.append(s);
                        }
                        buf.append("\n</mappings>");
                        String fileName = dir + "profile-polodigitale-iccd-search-all.xml";
                        System.out.println("Write " + fileName + "."
                                //" -> "+buf.toString()
                        );
                        FileUtils.write(new File(fileName), buf.toString());
                    }
                }
            }
            /**
             * campi per la scheda di dettaglio
             */
            HashMap<String,String> schede = new HashMap<>();
            if(iccdSchedeAutActive) {
                schede.put("f", "SchedaF400");
                schede.put("oa", "SchedaOA300");
                schede.put("d", "SchedaD300");
                schede.put("s", "SchedaS300");
            }
            if(iccdSchedeAutActive) {
                schede.put("au3", "AUT300");
                schede.put("au4", "AUT400");
            }
            for (String key: schede.keySet()) {
                node = objectMapper.readTree(new URL(url+ schede.get(key)));
                if(node instanceof ArrayNode) {
                    StringBuffer buf = new StringBuffer();
                    buf.append(header);
                    ArrayNode arr = (ArrayNode) node;
                    for (int i = 0; i < arr.size(); i++) {
                        String field = arr.get(i).get("name").asText();
                        String label = arr.get(i).get("label").asText();
                        boolean required = arr.get(i).get("required").asBoolean();
                        String minOccurs0 = arr.get(i).get("minOccurs").asText();
                        String maxOccurs0 = arr.get(i).get("maxOccurs").asText();
                        //System.out.println(""+field);
                        if(!required && minOccurs0.equalsIgnoreCase("0") ){
                            buf.append("\n\t<mapping destination=\""+field+"\" searchField=\"false\" vistaEtichette=\""+
                                    ((i+1)*100)+"\" is4Fe='true' label=\""+label+"\" group=\""+label+"\" multiple=\""+
                                    (maxOccurs0.equalsIgnoreCase("unbounded")?true:false)+"\"/>");
                        }
                        else if (minOccurs0.equalsIgnoreCase("1")  && maxOccurs0.equalsIgnoreCase("unbounded") ){
                            /**
                             * questi vengono salvati come monocampo
                             */
                            buf.append("\n\t<mapping destination=\""+field+"\" searchField=\"false\" vistaEtichette=\""+
                                    ((i+1)*100)+"\" is4Fe='true' label=\""+label+"\" group=\""+label+"\" multiple=\""+
                                    "false"//(maxOccurs0.equalsIgnoreCase("unbounded")?true:false)
                                    +"\"/>");
                        }
                        else{
                            ArrayNode children = (ArrayNode) arr.get(i).get("children");
                            if (children != null) {
                                for (int j = 0; j < children.size(); j++) {
                                    boolean multiple = false;
                                    String maxOccurs = children.get(j).get("maxOccurs").asText();
                                    String minOccurs = children.get(j).get("minOccurs").asText();
                                    if(minOccurs.equals("1") && maxOccurs.equals("1") && children.get(j).get("children")!=null){
                                        // Da prendere i figli non questo nodo come per PVC e NCT
                                        ArrayNode children2 = (ArrayNode) children.get(j).get("children");
                                        for (int k = 0; k < children2.size(); k++) {
                                            JsonNode node2 = children2.get(k);
                                            String maxOccurs2 = node2.get("maxOccurs").asText();
                                            if (maxOccurs.equalsIgnoreCase("unbounded"))
                                                multiple = true;
                                            String field2 = node2.get("name").asText();
                                            String label2 = node2.get("label").asText();
                                            //System.out.println(""+field+"_"+field2);
                                            buf.append("\n\t<mapping destination=\"" + field + "_" +
                                                    field2 + "\" searchField=\"false\" vistaEtichette=\"" +
                                                    ((i + 1) * 100 + j + 1) + "."+fill(k)+"\" is4Fe='true' label=\"" +
                                                    label2 + "\" group=\"" + label + "\" multiple=\"" + multiple + "\"/>");
                                        }
                                    }
                                    else {
                                        if (maxOccurs.equalsIgnoreCase("unbounded"))
                                            multiple = true;
                                        String field2 = children.get(j).get("name").asText();
                                        String label2 = children.get(j).get("label").asText();
                                        //System.out.println(""+field+"_"+field2);
                                        buf.append("\n\t<mapping destination=\"" + field + "_" +
                                                field2 + "\" searchField=\"false\" vistaEtichette=\"" +
                                                ((i + 1) * 100 + j + 1) + "\" is4Fe='true' label=\"" + label2
                                                + "\" group=\"" + label + "\" multiple=\"" + multiple + "\"/>");
                                    }
                                }
                            }
                        }
                    }
                    buf.append("\n</mappings>");
                   String fileName = dir+ "profile-polodigitale-iccd-fe-"+key+".xml";
                    System.out.println("Write "+fileName+"."
                            //+" -> "+buf.toString()
                    );
                    FileUtils.write(new File(fileName), buf.toString());
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String fill(int k) {
        if(k<10)
            return "00"+k;
        if(k<100)
            return "0"+k;
        return ""+k;
    }


}
