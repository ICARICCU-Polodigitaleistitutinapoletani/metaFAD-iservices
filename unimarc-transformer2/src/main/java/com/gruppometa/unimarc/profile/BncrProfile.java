package com.gruppometa.unimarc.profile;

import com.gruppometa.unimarc.object.Field;
import com.gruppometa.unimarc.object.OutItem;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;

public class BncrProfile extends XmlProfile{
    @Override
    public void makeSpecialTwo(OutItem desc, Record record) throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        super.makeSpecialOne(desc, record);
        List<DataField> datas = record.getDataFields();
        //boolean isPartOf = record.getLeader().toString().charAt(9)=='W';
        //HashMap<String, String> fieldsDone = new HashMap<String, String>();
        boolean makeTitolo = false;
        for (Iterator<DataField> iterator = datas.iterator(); iterator
                .hasNext(); ) {
            DataField dataField = (DataField) iterator.next();
            if (dataField.getTag().equals("200")) {
                if(dataField.getIndicator1()=='0') {
                    makeTitolo = true;
                }
            }
            if((dataField.getTag().equals("461")||dataField.getTag().equals("462")) && makeTitolo) {
                String value = "[";
                if(dataField.getSubfield('a')!=null)
                    value += clear27( dataField.getSubfield('a').getData() );
                if(dataField.getSubfield('e')!=null)
                    value += " : "+clear27( dataField.getSubfield('e').getData() );
                if(dataField.getSubfield('v')!=null)
                    value += "]  "+clear27( dataField.getSubfield('v').getData() );
                else
                    value += "]";
                List<Field> titoli = desc.getFieldArray("Titolo");
                if(titoli!=null && titoli.size()>0)
                    titoli.get(0).setTextValue(value);
                else{
                    addField(desc, "Titolo","").setTextValue(value);
                }
                makeTitolo = false;
            }
        }
    }
}
