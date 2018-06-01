package com.gruppometa.poloigitale.services.controllers;

import com.gruppometa.mets2mag.saxon.SaxonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

/**
 * Created by ingo on 02/03/17.
 */
@RestController
public class Mets2MagController {

    protected static Logger logger = LoggerFactory.getLogger(Mets2MagController.class);

    @RequestMapping(value="/mets/mag", produces={MediaType.APPLICATION_XML_VALUE},
        method = RequestMethod.POST)
    public String getMag(
            @RequestParam("mets") MultipartFile mets,
            @RequestParam(value = "stprog", required = false, defaultValue = "") String stprog,
            @RequestParam(value = "collection", required = false, defaultValue = "")  String collection,
            @RequestParam(value = "agency", required = false, defaultValue = "") String agency,
            @RequestParam(value = "access_rights", required = false, defaultValue = "") String access_rights,
            @RequestParam(value = "completeness", required = false, defaultValue = "") String completeness,
            HttpServletResponse response
            ){
        Transformer transformer = SaxonHelper.getInstance().getTransformer();
        try {
            StringWriter stringWriter = new StringWriter();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            InputStream inputStream = mets.getInputStream();
            Source streamSource = new StreamSource( inputStream );
            transformer.setParameter("stprog", stprog);
            transformer.setParameter("collection", collection);
            transformer.setParameter("agency", agency);
            transformer.setParameter("access_rights", access_rights);
            transformer.setParameter("completeness", completeness);
            transformer.transform(streamSource,
                    new StreamResult(stringWriter)
            );
            String ret = stringWriter.toString();
            inputStream.close();
            return ret;
        } catch (Exception e) {
            try {
                logger.error("",e);
                response.sendError(500, e.getMessage());
            } catch (IOException e1) {
                logger.error("",e1);
            }
        }
        return "";
    }
}
