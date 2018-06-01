package com.gruppometa.unimarc.output;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.util.DateUtil;
import org.junit.Test;
import org.marc4j.MarcStreamReader;
import org.marc4j.MarcStreamWriter;
import org.marc4j.marc.Record;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gruppometa.unimarc.object.Field;
import com.gruppometa.unimarc.object.JsonOutField;
import com.gruppometa.unimarc.object.JsonOutItem;

public class TestUniMarc {

	//@Test
	public void test() {
		ObjectMapper mapper = new ObjectMapper();
		JsonOutItem item;
		try {
			item = mapper.readValue(new File("/home/ingo/temp/polo/ANA0004448.json"), JsonOutItem.class);
			for(JsonOutField f : item.getFields()){
				if(f.getName().equalsIgnoreCase("unimarc")){
					String value = f.getValues().get(0).getPlain();
					ByteArrayInputStream input = new ByteArrayInputStream(Base64.getDecoder().decode(value));
					MarcStreamReader reader = new MarcStreamReader(input);
					Record r = reader.next();
					System.out.println(r.toString());
					input.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test2() throws IOException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd'T'HH:mm:ss'Z'");
		Calendar calendar = Calendar.getInstance(
				TimeZone.getTimeZone("UTC")
		);
		long time = System.currentTimeMillis();//- TimeZone.getDefault().getRawOffset();
		//calendar.setTimeInMillis(time);
		//System.out.println(TimeZone.getDefault().getRawOffset());

		DateUtil.formatDate(new Date(time), calendar, System.out);
		System.out.println("\n"
				+TimeZone.getTimeZone("UTC").getRawOffset());
		String timeStamp = simpleDateFormat.format(new Date(System.currentTimeMillis()-TimeZone.getDefault().getRawOffset()));
		System.out.println(calendar.getTime()+" "+timeStamp);

	}

}
