package com.gruppometa.unimarc.output;

import java.io.IOException;
import java.util.Iterator;

import com.gruppometa.unimarc.logging.UserLogger;
import com.gruppometa.unimarc.object.Field;
import com.gruppometa.unimarc.object.OutItem;
import com.gruppometa.unimarc.object.Output;
import com.gruppometa.unimarc.util.StringUtil;

public class MysqlOutputFormatter extends BaseOutFormatter implements OutputFormatter{
	
	protected boolean createIndexes = true;
	protected boolean truncateTables = false;
	protected boolean dropTables = false;
	protected static String DOC_TABLE = "mw_opac_tbl";
	protected static String DOC_TABLE_FULL = "mw_opacfull_tbl";
	public MysqlOutputFormatter(Output output) {
		this.output = output;
	}
	/**
	 * @return the truncateTables
	 */
	public boolean isTruncateTables() {
		return truncateTables;
	}
	/**
	 * @param truncateTables the truncateTables to set
	 */
	public void setTruncateTables(boolean truncateTables) {
		this.truncateTables = truncateTables;
	}
	public void toXml(Appendable buf) throws IOException {
		writeInit(buf);
		for (Iterator<OutItem> iterator = output.getItems().iterator(); iterator.hasNext();) {
			OutItem type = (OutItem) iterator.next();
			toXml(buf,type);
		}
		writeEnd(buf);
	}
	protected void writeInit(Appendable buf) throws IOException {
		buf.append("-- encoding UTF-8");
		if(dropTables){
			buf.append("\n DROP TABLE "+DOC_TABLE+";");
			buf.append("\n DROP TABLE "+DOC_TABLE_FULL+";");
		}
		buf.append("\n CREATE TABLE IF NOT EXISTS "+DOC_TABLE+" (" +
			"\n\tid varchar(155)," +
			"\n\tfieldname varchar(155)," +
			"\n\tpos int," +
			"\n\tmultiple int," +
			"\n\tbid varchar(255)," +
			"\n\trole varchar(255)," +
			"\n\tqualifier varchar(255)," +
			"\n\tfieldvalue text," +
			"\n\tprimary key(id,fieldname, pos));");
		buf.append("\n CREATE TABLE IF NOT EXISTS "+DOC_TABLE_FULL+" (" +
			"\n\tid varchar(255) primary key," +
			"\n\tfieldvalue text);");
		if(truncateTables){
			if(createIndexes){
				buf.append("\n DROP INDEX idx_"+DOC_TABLE_FULL+"_fieldvalue;");
				buf.append("\n DROP INDEX idx_"+DOC_TABLE+"_fieldname;");
				buf.append("\n DROP INDEX idx_"+DOC_TABLE+"_fieldvalue;");
			}
			buf.append("\n TRUNCATE TABLE "+DOC_TABLE+";");
			buf.append("\n TRUNCATE TABLE "+DOC_TABLE_FULL+";");
		}
		
	}
	protected void writeEnd(Appendable buf) throws IOException {
		if(createIndexes){
			buf.append("\n CREATE FULLTEXT INDEX idx_"+DOC_TABLE_FULL+"_fieldvalue on "+DOC_TABLE_FULL+"(fieldvalue);");
			buf.append("\n CREATE INDEX idx_"+DOC_TABLE+"_fieldname on "+DOC_TABLE+"(fieldname);");
			buf.append("\n CREATE FULLTEXT INDEX idx_"+DOC_TABLE+"_fieldvalue  on "+DOC_TABLE+"(fieldvalue);");
		}
		buf.append("\n-- fine file");				
	}
	
	/**
	 * @return the dropTables
	 */
	public boolean isDropTables() {
		return dropTables;
	}
	/**
	 * @param dropTables the dropTables to set
	 */
	public void setDropTables(boolean dropTables) {
		this.dropTables = dropTables;
	}
	/**
	 * @return the createIndexes
	 */
	public boolean isCreateIndexes() {
		return createIndexes;
	}
	/**
	 * @param createIndexes the createIndexes to set
	 */
	public void setCreateIndexes(boolean createIndexes) {
		this.createIndexes = createIndexes;
	}
	public void toXml(Appendable buf, OutItem item) throws IOException{
		String id = item.getAbout();
		if(item.getFields().size()==0){
			UserLogger.logger.warn("Doc senza campi: id = "+ id);
			return;
		}
		if(!truncateTables)
			buf.append("\n DELETE from "+DOC_TABLE+" WHERE id = '"+StringUtil.sqlencode(id)+"';");
		int i = 0;		
		buf.append("\n INSERT INTO "+DOC_TABLE+" (id,fieldname,pos,multiple,qualifier,bid,role,fieldvalue) VALUES ");	
		for (Iterator<Field> iterator = item.getFields().iterator(); iterator.hasNext();) {
			Field type = (Field) iterator.next();
			if(i>0)
				buf.append(",");
			toXml(buf,id,type,i++);
		}
		buf.append(";");
		if(!truncateTables)
			buf.append("\n DELETE from "+DOC_TABLE_FULL+" WHERE id = '"+StringUtil.sqlencode(id)+"';");
		buf.append("\n INSERT INTO "+DOC_TABLE_FULL+" (id,fieldvalue) VALUES ("+
				"'"+StringUtil.sqlencode(id)+"','");
		for (Iterator<Field> iterator = item.getFields().iterator(); iterator.hasNext();) {
			Field type = (Field) iterator.next();
			//buf.append("");
			toXml(buf,type);
		}
		buf.append("');");
	}
	
	public void toXml(Appendable buf,  Field field) throws IOException {
		buf.append("\n\t"+StringUtil.sqlencode(field.getTextValue()));	
	}
	
	public void toXml(Appendable buf, String id, Field field,int pos) throws IOException {
		//buf.append("\n INSERT INTO doc (id,fieldname,pos,qualifier,bid,role,fieldvalue) VALUES (");
		buf.append("\n\t(");
		buf.append("'"+StringUtil.sqlencode(id)+"'," +
				"'"+StringUtil.sqlencode(field.getName())+"'" +
				(","+pos)+
				(","+field.getMultiple())+
				((field.getQualifier()!=null && !field.getQualifier().equals("") )?(",'"+StringUtil.sqlencode(field.getQualifier())+"'"):",null")+
				((field.getBid()!=null && !field.getBid().equals("") )?(",'"+StringUtil.sqlencode(field.getBid())+"'"):",null")+
				((field.getRole()!=null && !field.getRole().equals("") )?(",'"+StringUtil.sqlencode(field.getRole())+"'"):",null")+
				",'"+StringUtil.sqlencode(field.getTextValue())+"'");	
		buf.append(")");
	}

}
