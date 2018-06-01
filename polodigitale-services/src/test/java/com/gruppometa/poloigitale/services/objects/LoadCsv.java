package com.gruppometa.poloigitale.services.objects;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import java.io.File;
import java.util.List;

/**
 * Created by ingo on 07/11/16.
 */
public class LoadCsv {
    public static void main(String[] args ){
        try {
            File file = new File("/home/ingo/profiloArchivi_Solr_UNITA_2016.10.28.csv");
            List<String> lines = FileUtils.readLines(file);
            String header = "<?xml version='1.0' encoding='UTF-8'?>\n" +
                    "<mappings xmlns=\"http://gruppometa.it/metafad\">";
            StringBuffer buf = new StringBuffer();
            buf.append(header);
            int shortVistaCount = 1;
            for (String line : lines) {
                String[] values = line.split("(\\t)");
                if(values.length>2 && values[1].charAt(2)==':'){
                    boolean multiple = true;
                    //System.out.println(values[1]);
                    String label = values[2];
                    String subLabel = " ("+ values[1].substring(0,2).toUpperCase()+ ")";
                    if(label.trim().length()==0)
                        label = values[3];
                    String facet = "";
                    String shortVista = "";
                    if(values.length>5 && values[5].length()>0){
                        facet = " facet=\"\"";
                    }
                    if(values.length>6 && values[6].length()>0){
                        shortVista = " vistaShort=\""+(shortVistaCount++)+"\"";
                    }
                    String item = "\n\t<mapping " +
                            "destination=\""+StringEscapeUtils.escapeXml11(values[1])+"\" " +
                            facet+
                            shortVista+
                            " label=\""+StringEscapeUtils.escapeXml11(label+subLabel)+"\""+
                            " searchField=\"true\" is4Fe='true' multiple=\""+multiple+"\"/>";
                    buf.append(item);
                }
            }
            buf.append("\n</mappings>");
            String dir = (args.length > 0 ? args[0] : "");
            if(dir.length()>0 && !dir.endsWith("/"))
                dir = dir+"/";
            String fileName = dir + "profile-polodigitale-archivio-search-ud.xml";
            System.out.println("Write " + fileName + "."
                    //" -> "+buf.toString()
            );
            FileUtils.write(new File(fileName), buf.toString());
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
