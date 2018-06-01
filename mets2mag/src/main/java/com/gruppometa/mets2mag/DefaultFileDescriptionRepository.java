package com.gruppometa.mets2mag;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ingo on 07/03/17.
 */
public class DefaultFileDescriptionRepository implements  FileDescriptionRepository{


    protected static Logger logger = LoggerFactory.getLogger(DefaultFileDescriptionRepository.class);
    public List<Integer> getDimensionOfImage(String path) {
        List<Integer> dim = new ArrayList<Integer>();
        //dim.add(10);
        //dim.add(10);
        return dim;
    }

    public String getJsonInfo(String baseDir,String path) {
        if(path==null)
            return "";
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String,String> map = new HashMap<String, String>();
        File resource = new File(baseDir,path);
        if(!resource.exists()) {
            logger.error("file not found "+resource.getAbsolutePath());
            return "";
        }
        try {
            BufferedImage img = ImageIO.read(resource);
            map.put("width", "" + img.getWidth());
            map.put("height", "" + img.getHeight());
            map.put("filesize",""+resource.length());
            map.put("md5", getMD5Checksum(resource));
        }
        catch(Exception e){
            logger.error("",e);
        }
        StringWriter stringWriter = new StringWriter();
        try {
            objectMapper.writeValue(stringWriter,map);
        } catch (IOException e) {
            logger.error("",e);
        }
        return stringWriter.toString();
    }

    public static byte[] createChecksum(File file) throws Exception {
        InputStream fis =  new FileInputStream(file);

        byte[] buffer = new byte[1024];
        MessageDigest complete = MessageDigest.getInstance("MD5");
        int numRead;

        do {
            numRead = fis.read(buffer);
            if (numRead > 0) {
                complete.update(buffer, 0, numRead);
            }
        } while (numRead != -1);

        fis.close();
        return complete.digest();
    }

    // see this How-to for a faster way to convert
    // a byte array to a HEX string
    public static String getMD5Checksum(File file) throws Exception {
        byte[] b = createChecksum(file);
        String result = "";

        for (int i=0; i < b.length; i++) {
            result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
        }
        return result;
    }
}
