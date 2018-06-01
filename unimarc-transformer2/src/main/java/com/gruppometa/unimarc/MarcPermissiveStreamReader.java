package com.gruppometa.unimarc;
// $Id: MarcPermissiveStreamReader.java 17 2008-06-20 14:40:13Z wayne.graham $
/**
 * Copyright (C) 2004 Bas Peters
 *
 * This file is part of MARC4J
 *
 * MARC4J is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public 
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 *
 * MARC4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with MARC4J; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
 

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.marc4j.Constants;
import org.marc4j.MarcException;
import org.marc4j.MarcReader;
import org.marc4j.converter.CharConverter;
import org.marc4j.converter.impl.AnselToUnicode;
import org.marc4j.converter.impl.Iso5426ToUnicode;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Leader;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.VariableField;
import org.marc4j.marc.impl.Verifier;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;

/**
 * An iterator over a collection of MARC records in ISO 2709 format.
 * <p>
 * Example usage:
 * 
 * <pre>
 * InputStream input = new FileInputStream(&quot;file.mrc&quot;);
 * MarcReader reader = new MarcStreamReader(input);
 * while (reader.hasNext()) {
 *     Record record = reader.next();
 *     // Process record
 * }
 * </pre>
 * 
 * <p>
 * </p>
 * 
 * <p>
 * When no encoding is given as an constructor argument the parser tries to
 * resolve the encoding by looking at the character coding scheme (leader
 * position 9) in MARC21 records. For UNIMARC records this position is not
 * defined.
 * </p>
 * 
 * @author Bas Peters
 * @version $Revision: 1.10 $
 * 
 */
public class MarcPermissiveStreamReader implements MarcReader {

    private DataInputStream input = null;

    private Record record;

    private MarcFactory factory;

    private String encoding = "ISO8859_1";

    private boolean override = false;

    //private boolean hasNext = true;
   
    private boolean permissive = false;
    
    private CharConverter converterAnsel = null;

    private CharConverter converterUnimarc = null;
    
    private String conversionCheck1 = null;
    
    private String conversionCheck2 = null;

    private String conversionCheck3 = null;

    private static HashMap<String, String> langMap = null;
    
    private boolean cleaned = false;
    public static boolean showCleaned = false;
    protected Log log = LogFactory.getLog(MarcPermissiveStreamReader.class);
    /**
     * Constructs an instance with the specified input stream.
     */
    public MarcPermissiveStreamReader(InputStream input, boolean permissive) {
        this(input, null, permissive);
    }

    /**
     * Constructs an instance with the specified input stream.
     */
    public MarcPermissiveStreamReader(InputStream input, String encoding) {
        this(input, encoding, true);
    }

    /**
     * Constructs an instance with the specified input stream and character
     * encoding.
     */
    public MarcPermissiveStreamReader(InputStream input, String encoding, boolean permissive) {
        this.permissive = permissive;
        this.input = new DataInputStream(new BufferedInputStream(input));
        factory = MarcFactory.newInstance();
        if (encoding != null) {
            this.encoding = encoding;
            override = true;
        }
    }
    
    public static void setShowCleaned(boolean show)
    {
        showCleaned = show;
    }
    
    /**
     * Returns true if the iteration has more records, false otherwise.
     */
    public boolean hasNext() {
        try {
            if (input.available() == 0)
                return false;
        } catch (IOException e) {
            throw new MarcException(e.getMessage(), e);
        }
        return true;
    }

    /**
     * Returns the next record in the iteration.
     * 
     * @return Record - the record object
     */
    public Record next() 
    {
        record = factory.newRecord();

        try {
            cleaned = false;
            byte[] byteArray = new byte[24];
            //if(input.available()<24)
            //	throw new MarcException("Premature end of file encountered (2) input.available():"+ input.available());
            input.readFully(byteArray);

            if(isPermissive()){
            	// 	formato unimarc biblioteca alessandrina
            	if(byteArray.length>0 && byteArray[0]=='\n'){
            		byte c = (byte)input.read();
            		for (int i = 1; i < byteArray.length; i++) {
            			byteArray[i-1] = byteArray[i];
            		}
            		byteArray[byteArray.length-1] = c;
            	}
            	// 	fine 
            }
            
            int recordLength = parseRecordLength(byteArray);
            byte[] recordBuf = new byte[recordLength - 24];
            if (permissive) 
            {
                input.mark(recordLength * 2);
                input.readFully(recordBuf);
                if (recordBuf[recordBuf.length-1] != Constants.RT)
                {
                    cleaned = true;
                    recordBuf = rereadPermissively(input, recordBuf, recordLength);
                    recordLength = recordBuf.length + 24;
                }
            }
            else
            {
                input.readFully(recordBuf);
            }
            //String tmp = new String(recordBuf);
            parseRecord(record, byteArray, recordBuf, recordLength);
            if (showCleaned && cleaned) 
            {
                System.out.write(byteArray);
                System.out.write(recordBuf);
            }

            return(record);
        }
        catch (EOFException e) {
        	if(!permissive) // I.S.
        		throw new MarcException("Premature end of file encountered", e);
        	log.warn("Premature end of file encountered");
        	return null;
        } 
        catch (IOException e) {
            throw new MarcException("an error occured reading input", e);
        }   
    }
    
    private byte[] rereadPermissively(DataInputStream input, byte[] recordBuf, int recordLength) throws IOException
    {
        int loc = arrayContainsAt(recordBuf, Constants.RT);
        if (loc != -1)  // stated record length is too long
        {
            recordLength = loc + 24;
            input.reset();
            recordBuf = new byte[recordLength - 24];
            input.readFully(recordBuf);
        }
        else  // stated record length is too short read ahead
        {
            loc = recordLength - 24;
            int c = 0;
            do 
            {
                c = input.read();
                loc++;
            } while (loc < recordLength + 100 && c != Constants.RT && c != -1);
 
            if (c == Constants.RT)
            {
                recordLength = loc + 24;
                input.reset();
                recordBuf = new byte[recordLength - 24];
                input.readFully(recordBuf);
            }
            else if (c == -1)
            {
                recordLength = loc + 24;
                input.reset();
                recordBuf = new byte[recordLength - 24 + 1];
                input.readFully(recordBuf);
                recordBuf[recordBuf.length-1] = Constants.RT;  
            }
        }
        return(recordBuf);
    }
        
    @SuppressWarnings("unchecked")
	private void parseRecord(Record record, byte[] byteArray, byte[] recordBuf, int recordLength)
    {
        Leader ldr;
        ldr = factory.newLeader();
        ldr.setRecordLength(recordLength);
        int directoryLength=0;
        conversionCheck1 = "";
        conversionCheck2 = "";
        conversionCheck3 = "";
        
        try {                
            parseLeader(ldr, byteArray);
            directoryLength = ldr.getBaseAddressOfData() - (24 + 1);
        } 
        catch (IOException e) {
            throw new MarcException("error parsing leader with data: "
                    + new String(byteArray), e);
        } 
        catch (MarcException e) {
            if (permissive)
            {
                if (recordBuf[recordBuf.length-1] == Constants.RT && recordBuf[recordBuf.length-2] == Constants.FT)
                {
                    log.warn("Warning: Corrupt record encountered, attempting to read permissively");
                    // make an attempt to recover record.
                    int offset = 0;
                    while (offset < recordBuf.length)
                    {
                        if (recordBuf[offset] == Constants.FT)
                        {
                            break;
                        }
                        offset++;
                    }
                    if (offset % 12 == 1)
                    {
                        // move one byte from body to leader, make new leader, and try again
                        byte oldBody[] = recordBuf;
                        recordBuf = new byte[oldBody.length-1];
                        System.arraycopy(oldBody, 1, recordBuf, 0, oldBody.length-1);
                        directoryLength = offset-1;
                        ldr.setIndicatorCount(2);
                        ldr.setSubfieldCodeLength(2);
                        ldr.setImplDefined1((""+(char)byteArray[7]+" ").toCharArray());
                        ldr.setImplDefined2((""+(char)byteArray[18]+(char)byteArray[19]+(char)byteArray[20]).toCharArray());
                        ldr.setEntryMap("4500".toCharArray());
                        if (byteArray[10] == (byte)' ' || byteArray[10] == (byte)'a') // if its ' ' or 'a'
                        {
                            ldr.setCharCodingScheme((char)byteArray[10]);
                        }
                    }
                    else if (offset % 12 == 11) 
                    {
                        byte oldBody[] = recordBuf;
                        recordBuf = new byte[oldBody.length+1];
                        System.arraycopy(oldBody, 0, recordBuf, 1, oldBody.length);
                        recordBuf[0] = (byte)'0';
                        directoryLength = offset+1;
                        ldr.setIndicatorCount(2);
                        ldr.setSubfieldCodeLength(2);
                        ldr.setImplDefined1((""+(char)byteArray[7]+" ").toCharArray());
                        ldr.setImplDefined2((""+(char)byteArray[16]+(char)byteArray[17]+(char)byteArray[18]).toCharArray());
                        ldr.setEntryMap("4500".toCharArray());
                        if (byteArray[8] == (byte)' ' || byteArray[8] == (byte)'a') // if its ' ' or 'a'
                        {
                            ldr.setCharCodingScheme((char)byteArray[10]);
                        }
                        if (byteArray[10] == (byte)' ' || byteArray[10] == (byte)'a') // if its ' ' or 'a'
                        {
                            ldr.setCharCodingScheme((char)byteArray[10]);
                        }
                    }
                    else
                    {
                        throw new MarcException("error parsing leader with data: "
                                + new String(byteArray), e);
                    }
                }
            }
            else
            {
                throw new MarcException("error parsing leader with data: "
                        + new String(byteArray), e);
            }
        }

        // if MARC 21 then check encoding
        switch (ldr.getCharCodingScheme()) {
        case ' ':
            if (!override)
                encoding = "ISO-8859-1";
            break;
        case 'a':
            if (!override)
                encoding = "UTF8";
        }
        String utfCheck;
        if (permissive && encoding == "UTF8")
        {
            try
            {
                utfCheck = new String(recordBuf, "UTF-8");
                byte byteCheck[] = utfCheck.getBytes("UTF-8");
                if (recordBuf.length != byteCheck.length)
                {
                }
                for (int i = 0; i < recordBuf.length; i++)
                {
                    if (recordBuf[i] == 0x1B || byteCheck[i] != recordBuf[i])
                    {
                        encoding = "MARC8-Maybe";
                        break;
                    }
                    
                }
                if (utfCheck.contains("a$1!"))
                {
                    encoding = "MARC8-Broken";
                }
            }
            catch (UnsupportedEncodingException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (permissive && encoding != "UTF8")
        {
            try
            {
                utfCheck = new String(recordBuf, "UTF-8");
                byte byteCheck[] = utfCheck.getBytes("UTF-8");
                if (recordBuf.length == byteCheck.length)
                {
	                for (int i = 0; i < recordBuf.length; i++)
	                {
	                    if (recordBuf[i] < 0x00 || byteCheck[i] != recordBuf[i])
	                    {
	                        encoding = "UTF8-Maybe"; 
	                        break;
	                    }
	                }
                }
             }
            catch (UnsupportedEncodingException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        record.setLeader(ldr);
        
        if ((directoryLength % 12) != 0)
        {
            if (permissive && directoryLength % 12 == 11 && recordBuf[1] != (byte)'0') 
            {
                log.warn("Warning: Corrupt record encountered, attempting to read permissively");
                byte oldBody[] = recordBuf;
                recordBuf = new byte[oldBody.length+1];
                System.arraycopy(oldBody, 0, recordBuf, 1, oldBody.length);
                recordBuf[0] = (byte)'0';
                directoryLength = directoryLength+1;
            }
            else
            {
                throw new MarcException("invalid directory");
            }
        }
        DataInputStream inputrec = new DataInputStream(new ByteArrayInputStream(recordBuf));
        int size = directoryLength / 12;

        String[] tags = new String[size];
        int[] lengths = new int[size];

        byte[] tag = new byte[3];
        byte[] length = new byte[4];
        byte[] start = new byte[5];

        String tmp;

        try {
            for (int i = 0; i < size; i++) 
            {
                inputrec.readFully(tag);                
                tmp = new String(tag);
                tags[i] = tmp;
    
                inputrec.readFully(length);
                tmp = new String(length);
                lengths[i] = Integer.parseInt(tmp);
    
                inputrec.readFully(start);
            }
    
            if (inputrec.read() != Constants.FT)
            {
                throw new MarcException("expected field terminator at end of directory");
            }
            
            int numBadLengths = 0;
            
            for (int i = 0; i < size; i++) 
            {
                int fieldLength = getFieldLength(inputrec);
                if (fieldLength+1 != lengths[i] && permissive)
                {
                    if (numBadLengths < 3 && Math.abs(fieldLength - lengths[i]) < 10)
                    {
                        numBadLengths++;
                        lengths[i] = fieldLength+1;
                    }
                }
                if (Verifier.isControlField(tags[i]))
                {
                    byteArray = new byte[lengths[i] - 1];
                    inputrec.readFully(byteArray);
    
                    if (inputrec.read() != Constants.FT)
                    {
                        throw new MarcException("expected field terminator at end of field");
                    }
    
                    ControlField field = factory.newControlField();
                    field.setTag(tags[i]);
                    field.setData(getDataAsString(byteArray));
                    record.addVariableField(field);
    
                } 
                else 
                {
                	// I.S.
                    byteArray = new byte[ (!permissive|| (inputrec.available()>= lengths[i]))? lengths[i]:  inputrec.available()];
                    // I.S.
                    //try{
                    	inputrec.readFully(byteArray);
                    //}
                    /*catch(EOFException e){
                    	if(!permissive)
                    		throw e;
                    	else
                    		return;
                    }*/
    
                    try {
                        record.addVariableField(parseDataField(tags[i],
                                byteArray));
                    } catch (IOException e) {
                        throw new MarcException(
                                "error parsing data field for tag: " + tags[i]
                                        + " with data: "
                                        + new String(byteArray), e);
                    }
                }
            }
            
            // We've determined that although the record says it is UTF-8, it is not. 
            // Here we make an attempt to determine the actual encoding of the data in the record.
            if (permissive && conversionCheck1.length() > 1 && 
                    conversionCheck2.length() > 1 && conversionCheck3.length() > 1)
            {
                int partToUse = 0;
                if (conversionCheck2.length() < conversionCheck1.length()
                        && conversionCheck2.length() < conversionCheck3.length())
                {
                    partToUse = 1;
                }
                else if (conversionCheck1.length() > conversionCheck3.length())
                {
                    partToUse = 0;
                }
                else if (conversionCheck3.length() > conversionCheck1.length())
                {
                    partToUse = 2;
                }
                else if (conversionCheck2.equals(conversionCheck3) && !conversionCheck1.trim().contains(" "))
                {
                    partToUse = 2;
                }
                else if (numLetters(conversionCheck1) == 0)
                {
                    partToUse = 0;
                }
                else
                {
                    CharsetDetector detect = new CharsetDetector();
                    byte m8Bytes[] = null;
                    byte isoBytes[] = null;
                    byte uniBytes[] = null;
                    try
                    {
                        m8Bytes = conversionCheck1.getBytes("ISO-8859-1");
                        uniBytes = conversionCheck2.getBytes("ISO-8859-1");
                        isoBytes = conversionCheck3.getBytes("ISO-8859-1");
                    }
                    catch (UnsupportedEncodingException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    detect.setText(m8Bytes);
                    CharsetMatch match1 = langDetect(detect, record);
                    log.error(match1 != null ? match1.getName() + " " + match1.getConfidence() + " " + match1.getLanguage() : "No Match");
                    
                    detect.setText(uniBytes);
                    CharsetMatch match2 = langDetect(detect, record);
                    log.error(match2 != null ? match2.getName() + " " + match2.getConfidence() + " " + match2.getLanguage() : "No Match");
                    
                    detect.setText(isoBytes);
                    CharsetMatch match3 = langDetect(detect, record);
                    log.error(match3 != null ? match3.getName() + " " + match3.getConfidence() + " " + match3.getLanguage() : "No Match");
                    
                    if (match1 == null && match2 == null && match3 == null)
                    {
                        partToUse = 0;
                    }
                    else if (match1 != null && (match2 == null || match3 == null))
                    {
                        partToUse = 0;
                    }
                    else if (match1.getConfidence() >= match2.getConfidence() && 
                            match1.getConfidence() >= match3.getConfidence() )
                    {
                        partToUse = 0;
                    }
//                    else if (match2.getConfidence() > match1.getConfidence() && 
//                            match2.getConfidence() > match3.getConfidence() )
//                    {
//                        partToUse = 1;
//                    }
                    else if (match3.getConfidence() > match1.getConfidence() && 
                            match3.getConfidence() > match2.getConfidence() )
                    {
                        partToUse = 2;
                    }
                }
                List<VariableField> fields = record.getVariableFields();
                Iterator<VariableField> iter = fields.iterator();
                while (iter.hasNext())
                {
                    VariableField field = iter.next();
                    if (field instanceof DataField)
                    {
                        DataField df = (DataField)field;
                        List<Subfield> subf = df.getSubfields();
                        Iterator<Subfield> sfiter = subf.iterator();
                        while (sfiter.hasNext())
                        {
                            Subfield sf = sfiter.next();
                            if (sf.getData().contains("%%@%%"))
                            {
                                String parts[] = sf.getData().split("%%@%%", 3);
                                sf.setData(parts[partToUse]);
                            }
                        }
                    }
                }                      
            }

            if (inputrec.read() != Constants.RT)
            {
            	// I.S.
            	if(!this.isPermissive())
                	throw new MarcException("expected record terminator");
            	else
            		log.warn("expected record terminator");            	
            } 
            // bug: I.S. per newline
            //inputrec.read();
        }
        catch (IOException e)
        {
        	//e.printStackTrace();
            throw new MarcException("an error occured reading input", e);            
        }
    }

    private int arrayContainsAt(byte[] byteArray, int ft)
    {
        for (int i = 0; i < byteArray.length; i++)
        {
            if (byteArray[i] == (byte)ft)  return(i);
        }
        return(-1);
    }

    private CharsetMatch langDetect(CharsetDetector detect, Record record)
    {
        String lang = extractLang(record);
        if (lang != null && !lang.equals("eng"))
        {
            CharsetMatch matches[] = detect.detectAll();
            for (int i = 0; i < matches.length; i++)
            {
                if (langMap(matches[i].getLanguage()).equals(lang))
                {
                    return(matches[i]);
                }
            }
            return(matches.length > 0 ? matches[0] : null);
        }
        else
        {
            return(detect.detect());
        }
    }

    private Object langMap(String language)
    {
        if (langMap == null) 
        {
            langMap = new HashMap<String, String>();
            langMap.put("de", "ger");
            langMap.put("nl", "dut");
            langMap.put("fr", "fre");
            langMap.put("fi", "fin");
            langMap.put("sv", "swe");
            langMap.put("it", "ita");
            langMap.put("es", "spa");
            langMap.put("en", "eng");
            langMap.put("da", "dan");
            langMap.put("no", "nor");
            langMap.put("tr", "tur");
            langMap.put("hu", "hun");
            langMap.put("ro", "rum");
            langMap.put("cs", "cze");
            langMap.put("pt", "por");
            langMap.put("pl", "pol");
            langMap.put("ru", "rus");
            langMap.put("ar", "ara");
            langMap.put("el", "gre");
            langMap.put("he", "heb");
            langMap.put("pt", "por");
            langMap.put("pl", "pol");
        }
        if (langMap.containsKey(language))
        {
            return(langMap.get(language));
        }
        return null;
    }

    private String extractLang(Record record)
    {
        VariableField f = record.getVariableField("008");
        ControlField cf = (ControlField)f;
        if (cf != null)
        {
            String data = cf.getData();
            if (data.length() >= 38)
            {
                String lang = data.substring(35, 38);
                return(lang);
            }
        }
        return null;
    }

    private int numLetters(String conversionCheck)
    {
        int count = 0;
        for (int i = 0; i < conversionCheck.length(); i++)
        {
            if (Character.isLetter(conversionCheck.charAt(i)))   count++;
        }
        return(count);
    }

    private DataField parseDataField(String tag, byte[] field)  throws IOException 
    {
        if (permissive)
        {
            cleanupBadFieldSeperators(field);
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(field);
        char ind1 = (char) bais.read();
        char ind2 = (char) bais.read();

        DataField dataField = factory.newDataField();
        dataField.setTag(tag);
        dataField.setIndicator1(ind1);
        dataField.setIndicator2(ind2);

        int code;
        int size;
        int readByte;
        byte[] data;
        Subfield subfield;
        while (true) {
            readByte = bais.read();
            if (readByte < 0)
                break;
            switch (readByte) {
            case Constants.US:
                code = bais.read();
                if (code < 0){
                	if(permissive){ // I.S.
                		log.warn("unexpected end of data field");
                		break;
                	}	
                	else
                		throw new IOException("unexpected end of data field");
                }
                if (code == Constants.FT)
                    break;
                size = getSubfieldLength(bais);
                data = new byte[size];
                bais.read(data);
                subfield = factory.newSubfield();
                String dataAsString = getDataAsString(data);
                if (permissive && code == Constants.US)
                {
                    code = data[0];
                    dataAsString = dataAsString.substring(1);
                }
                subfield.setCode((char) code);
                subfield.setData(dataAsString);
                dataField.addSubfield(subfield);
                break;
            case Constants.FT:
                break;
            }
        }
        return dataField;
    }
    
    private void cleanupBadFieldSeperators(byte[] field)
    {
        boolean hasEsc = false;
        for (int i = 0 ; i < field.length-1; i++)
        {
            if (field[i] == 0x1b) hasEsc = true;
            if (hasEsc && field[i] == Constants.US && !((field[i+1] >= 'a' && field[i+1] <= 'z') || (field[i+1] >= '0' && field[i+1] <= '9')))
            {
                field[i] = 0x7C;
                cleaned = true;
            }
            if (hasEsc && field[i] == Constants.US && (field[i+1] == '0' ))
            {
                field[i] = 0x7C;
                field[i+1] = 0x21;
                cleaned = true;
            }
            if (field[i] == Constants.US && field[i+1] == Constants.US && field[i+2] == Constants.US )
            {
                field[i] = 0x7C;
                field[i+1] = 0x7C;
                cleaned = true;
            }
        }
    }

    private int getFieldLength(DataInputStream bais) throws IOException 
    {
        bais.mark(9999);
        int bytesRead = 0;
        while (true) {
            switch (bais.read()) {
             case Constants.FT:
                bais.reset();
                return bytesRead;
            case -1:
                bais.reset();
                if (permissive)
                    return (bytesRead);
                else
                    throw new IOException("Field not terminated");
            case Constants.US:
            default:
                bytesRead++;
            }
        }
    }

    private int getSubfieldLength(ByteArrayInputStream bais) throws IOException {
        bais.mark(9999);
        int bytesRead = 0; 
        while (true) {
            switch (bais.read()) {
            case Constants.FT:
                bais.reset();
                return bytesRead;
            case Constants.US:
                bais.reset();
                return bytesRead;
            case -1:
                bais.reset();
                if (permissive)
                    return (bytesRead);
                else
                    throw new IOException("subfield not terminated");
            default:
                bytesRead++;
            }
        }
    }

    private int parseRecordLength(byte[] leaderData) throws IOException {
        InputStreamReader isr = new InputStreamReader(new ByteArrayInputStream(
                leaderData));
        int length = -1;
        char[] tmp = new char[5];
        isr.read(tmp);
        /*
        // bug: formato unimarc biblioteca alessandrina
        if(tmp.length>0 && tmp[0]=='\n'){
        	char c = (char)isr.read();
        	for (int i = 1; i < tmp.length; i++) {
				tmp[i-1] = tmp[i];
			}
        	tmp[tmp.length-1] = c;
        }
        // fine bug
         
         */
        try {
            length = Integer.parseInt(new String(tmp));
            // bug : formato unimarc biblioteca alessandrina: c' un newline
        } catch (NumberFormatException e) {
            throw new MarcException("unable to parse record length "+tmp.toString(), e);
        }
        return(length);
    }
    
    private void parseLeader(Leader ldr, byte[] leaderData) throws IOException {
        InputStreamReader isr = new InputStreamReader(new ByteArrayInputStream(
                leaderData));
        char[] tmp = new char[5];
        isr.read(tmp);
        //  Skip over bytes for record length, If we get here, its already been computed.
        ldr.setRecordStatus((char) isr.read());
        ldr.setTypeOfRecord((char) isr.read());
        tmp = new char[2];
        isr.read(tmp);
        ldr.setImplDefined1(tmp);
        ldr.setCharCodingScheme((char) isr.read());
        char indicatorCount = (char) isr.read();
        char subfieldCodeLength = (char) isr.read();
        char baseAddr[] = new char[5];
        isr.read(baseAddr);
        tmp = new char[3];
        isr.read(tmp);
        ldr.setImplDefined2(tmp);
        tmp = new char[4];
        isr.read(tmp);
        ldr.setEntryMap(tmp);
        isr.close();
        try {
            ldr.setIndicatorCount(Integer.parseInt(String.valueOf(indicatorCount)));
        } catch (NumberFormatException e) {
            throw new MarcException("unable to parse indicator count", e);
        }
        try {
            ldr.setSubfieldCodeLength(Integer.parseInt(String
                    .valueOf(subfieldCodeLength)));
        } catch (NumberFormatException e) {
            throw new MarcException("unable to parse subfield code length", e);
        }
        try {
            ldr.setBaseAddressOfData(Integer.parseInt(new String(baseAddr)));
        } catch (NumberFormatException e) {
            throw new MarcException("unable to parse base address of data", e);
        }

    }

    private String getDataAsString(byte[] bytes) 
    {
        String dataElement = null;
        if (encoding.equals("UTF-8") || encoding.equals("UTF8"))
        {
            try {
                dataElement = new String(bytes, "UTF8");
            } 
            catch (UnsupportedEncodingException e) {
                throw new MarcException("unsupported encoding", e);
            }
        }
        else if (encoding.equals("UTF8-Maybe"))
        {
            try {
                dataElement = new String(bytes, "UTF8");
            } 
            catch (UnsupportedEncodingException e) {
                throw new MarcException("unsupported encoding", e);
            }
        }
        else if (encoding.equals("MARC-8") || encoding.equals("MARC8"))
        {
            if (converterAnsel == null) converterAnsel = new AnselToUnicode();
            dataElement = converterAnsel.convert(bytes);
        }
        else if (encoding.equals("MARC8-Maybe"))
        {
            if (converterAnsel == null) converterAnsel = new AnselToUnicode();
            if (converterUnimarc == null) converterUnimarc = new Iso5426ToUnicode();
            String dataElement1 = converterAnsel.convert(bytes);
            String dataElement2 = converterUnimarc.convert(bytes);
            String dataElement3 = null;
            try
            {
                dataElement3 = new String(bytes, "ISO-8859-1");
            }
            catch (UnsupportedEncodingException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (dataElement1.equals(dataElement2) && dataElement1.equals(dataElement3))
            {
                dataElement = dataElement1;
            }
            else 
            {
                conversionCheck1 = conversionCheck1 + " " + dataElement1;
                conversionCheck2 = conversionCheck2 + " " + dataElement2;
                conversionCheck3 = conversionCheck3 + " " + dataElement3;
                dataElement = dataElement1 + "%%@%%" + dataElement2 + "%%@%%" + dataElement3;                
            }            
        }
        else if (encoding.equals("MARC8-Broken"))
        {
            try
            {
                dataElement = new String(bytes, "ISO-8859-1");
            }
            catch (UnsupportedEncodingException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            dataElement = dataElement.replaceAll("&lt;", "<");
            dataElement = dataElement.replaceAll("&gt;", ">");
            dataElement = dataElement.replaceAll("&amp;", "&");
            dataElement = dataElement.replaceAll("&apos;", "'");
            dataElement = dataElement.replaceAll("&quot;", "\"");
            String rep1 = ""+(char)0x1b+"\\$1";
            String rep2 = ""+(char)0x1b+"\\(B";                    
            dataElement = dataElement.replaceAll("\\$1", rep1);
            dataElement = dataElement.replaceAll("\\(B", rep2);
            dataElement = converterAnsel.convert(dataElement);

        }
        else if (encoding.equals("ISO-8859-1") || encoding.equals("ISO8859_1"))
        {
            try {
                dataElement = new String(bytes, "ISO-8859-1");
            } 
            catch (UnsupportedEncodingException e) {
                throw new MarcException("unsupported encoding", e);
            }
        }
        dataElement = dataElement.replaceAll("&lt;", "<");
        dataElement = dataElement.replaceAll("&gt;", ">");
        dataElement = dataElement.replaceAll("&amp;", "&");
        dataElement = dataElement.replaceAll("&apos;", "'");
        dataElement = dataElement.replaceAll("&quot;", "\"");
        return dataElement;
    }

    public boolean isPermissive()
    {
        return permissive;
    }

    public void setPermissive(boolean permissive)
    {
        this.permissive = permissive;
    }

}