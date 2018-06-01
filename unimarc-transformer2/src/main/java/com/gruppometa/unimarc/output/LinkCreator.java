package com.gruppometa.unimarc.output;

import com.gruppometa.unimarc.object.Field;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by ingo on 14/03/17.
 */
public interface LinkCreator {


    public String createLinks(Field field);

}
