package com.gruppometa.unimarc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.gruppometa.unimarc.output.*;
import com.gruppometa.unimarc.profile.*;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.marc4j.MarcException;
import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;
import org.marc4j.marc.Record;

import com.gruppometa.unimarc.gui.MarcConvertorGui;
import com.gruppometa.unimarc.logging.UserLogger;
import com.gruppometa.unimarc.object.ConvertorStatus;
import com.gruppometa.unimarc.object.DefaultOutItem;
import com.gruppometa.unimarc.object.DefaultOutput;
import com.gruppometa.unimarc.object.Output;

public class MarcConvertor  {
	protected static Log logger = LogFactory.getLog(MarcConvertor.class);
	
	private int rows;
	private int offset;
	protected String descSource="";
	protected String descSourceLevel2="";
	protected String job="";
	protected Profile profile = null;
	protected String encoding = null;//"ISO8859_1";
	protected int count = 0;
	protected int scartati = 0;
	protected String status = "idle";
	protected SimpleDateFormat dateFormat = new SimpleDateFormat();
	protected boolean stopped = false;
	protected String message = "";
	protected RecordCache cache;
	protected boolean isMrk = false;
	
	public ConvertorStatus getConvertorStatus(){
		return new ConvertorStatus(message, status, count, scartati);
	}

	public void initStatus(){
		status = "idle";
		message = "";
		count = 0;
		scartati = 0;
	}

	public void stop(){
		stopped = true;
		status = "stopped";
	}
	
	/**
	 * @return the rows
	 */
	public int getRows() {
		return rows;
	}

	/**
	 * @return the profile
	 */
	public Profile getProfile() {
		return profile;
	}

	/**
	 * @param profile the profile to set
	 */
	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	/**
	 * @param rows the rows to set
	 */
	public void setRows(int rows) {
		this.rows = rows;
	}

	/**
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * @param offset the offset to set
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}

	
	/**
	 * @return the descSourceLevel2
	 */
	public String getDescSourceLevel2() {
		return descSourceLevel2;
	}

	/**
	 * @param descSourceLevel2 the descSourceLevel2 to set
	 */
	public void setDescSourceLevel2(String descSourceLevel2) {
		this.descSourceLevel2 = descSourceLevel2;
	}

	/**
	 * @return the job
	 */
	public String getJob() {
		return job;
	}

	/**
	 * @param job the job to set
	 */
	public void setJob(String job) {
		this.job = job;
	}

	/**
	 * @return the descSource
	 */
	public String getDescSource() {
		return descSource;
	}

	/**
	 * @param descSource the descSource to set
	 */
	public void setDescSource(String descSource) {
		this.descSource = descSource;
	}
	
	public Output convert(InputStream inputstream)
	throws IllegalArgumentException, SecurityException,
	IllegalAccessException, InvocationTargetException,
	NoSuchMethodException, IOException {
		return convert(inputstream, null);
	}
	public Output convert(InputStream inputstream, OutputFormatter outFormatter)
			throws IllegalArgumentException, SecurityException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, IOException {
		cache = new RecordCache();
		outFormatter.setRecordCache(cache);
		getProfile().setParentCache(new ParentCache());
		
		status = "running";
		stopped = false;
		long now = System.currentTimeMillis();
		MarcReader reader =  isMrk? (new MarcStreamReader(inputstream )):
			new MarcPermissiveStreamReader(inputstream,
					encoding, 
					true);
		Output output = (outFormatter!=null? outFormatter.getOutput() :new DefaultOutput());
		count = 0;
		scartati = 0;
		if(outFormatter!=null)
			outFormatter.notifyInit();		
		
		while (reader.hasNext() && (rows==0 || (offset+rows)>count) && !stopped && !profile.isFinished()) {
			Record record = null;
			try {
				record = reader.next();
				if (record != null)
					count++;
				if(offset!=0 && offset>=count)
					continue;
			} catch (MarcException ex) {
				logger.error(ex);
			}
			if (record != null) {
				//boolean isNew = false;
				//logger.debug("Add new Item count="+count);
				DefaultOutItem desc = new DefaultOutItem();
				output.addItem(desc);
				
				profile.makeId(desc, record); //makeId(desc, record, "001", ALL);
								
				/**
				 * scarta alcuni record come MSM e MUS
				 */
				if (profile.scarta(desc) || !profile.makeLeader(desc, record.getLeader())){
					record = null;
					output.getItems().remove(desc);
					//logger.debug("Record "+ count+ " scartato.");
					scartati++;
					continue;
				}
				
				
				desc.setDescSource(getDescSource());
				if(getDescSourceLevel2()!=null)
					desc.setDescSourceLevel2(getDescSourceLevel2()); 
				desc.setJob(getJob());

				boolean isMy = false;
				if (isMy) {
					//makeMy(desc, record);
				} else {

					profile.makeSpecialOne(desc, record);
					profile.makeDefinitions(desc, record);
					profile.makeSpecialTwo(desc, record);
					profile.normalize(desc);
				}
				// SimpleLiteral source = desc.addNewSource();
				// source.newCursor().setTextValue("Unimarc");
				// log.debug("Record n."+ count+ " added.");
				if(outFormatter!=null)
					outFormatter.notifyAdd(profile);
			} else
				logger.info("Record n." + count + " is null.");
		}
		logger.info((count-offset) + " records added. Time: "+(System.currentTimeMillis()-now));
		message = (count-offset) + " record aggiunti. Tempo: "+
				(System.currentTimeMillis()-now)+"ms.";
		UserLogger.logger.info(message);
		message = dateFormat.format(new Date())+ " "+message;
		status = "finished";
		setRecordAdded(count-offset);
		setRecordSkipped(scartati);
		//System.gc();
		inputstream.close();
		if(outFormatter!=null)
			outFormatter.notifyEnd();
		return output;
	}

	private void setRecordSkipped(int scartati) {
	}

	private void setRecordAdded(int i) {
	}

	
	public static void main(String[] args){
		if(args.length==0){
			new MarcConvertorGui();
			return;
		}			
		CommandLineParser parser = new PosixParser();
		Options options = new Options();
		options.addOption( "o", "out", true, "Formato di output" );
		options.addOption( "of", "outfile", true, "File di output" );
		options.addOption( "solr", "solrurl", true, "Url di solr" );
		options.addOption( "h", "help", false, "informazione di aiuto" );
		options.addOption( "p", "profile", true, "profilo" );
		options.addOption( "i", "offset", true, "offset" );
		options.addOption( "fe", "frontend", true, "frontend" );
		options.addOption( "r", "rows", true, "rows" );
		options.addOption( "l", "link", true, "linkcreator" );
		options.addOption( "d", "directory", true, "directory per la creazione dei file di output" );
		options.addOption( "id", "id", true, "id" );
		options.addOption( "e", "encoding", true, "encoding default ISO8859_1, serve UTF8" );
		options.addOption( "mysqltruncate","mysqltruncate", false, "truncate delle tabelle" );
		options.addOption( "mysqlindexes", "mysqlindexes", false, "crea indici" );
		options.addOption( "mysqldrop","mysqldrop", false, "drop delle tabelle" );
		options.addOption( "xmlNoAttr","xmlNoAttr", false, "non stampere gli attributi." );
		options.addOption( "xmlSpaceInNames","xmlSpaceInNames", false, "spazi nei nomi dei campi." );
		
		MarcConvertor convertor = new MarcConvertor();
		//convertor.setOffset(0);
		//convertor.setRows(30);
		String filename = "";//"/home/ingo/Documents/meta/unimarc/IE001_RML_01_00003640.mrc";
		//String outfilename = "/home/ingo/Documents/meta/unimarc/IE001_RML_01_00003640.xml";
		Output out;
		try {
			CommandLine line = parser.parse( options, args );
			if(line.getArgs().length==0 || line.hasOption("help")){
				HelpFormatter formatterH = new HelpFormatter();			
				formatterH.printHelp( "UnimarcTransformer [opzioni] file", options );
				return;
			}			
			filename = line.getArgs()[0];
			
			if(line.hasOption("profile") && line.getOptionValue("profile").equals("xml")){
				UserLogger.logger.info("Utilizzo del profilo XML (defaultProfile.xml)");
				XmlProfile pro = new XmlProfile();
				if(line.hasOption("id"))
					pro.setFilterId(line.getOptionValue("id"));
				convertor.setProfile(pro);
			}
			else if(line.hasOption("profile") && line.getOptionValue("profile").equals("xmlbncr")){
				UserLogger.logger.info("Utilizzo del profilo XML BNCR (defaultProfile.xml)");
				BncrProfile pro = new BncrProfile();
				if(line.hasOption("id"))
					pro.setFilterId(line.getOptionValue("id"));
				convertor.setProfile(pro);
			}
			else if(line.hasOption("profile") && line.getOptionValue("profile").equals("xmlna")){
				UserLogger.logger.info("Utilizzo del profilo XML (naProfile.xml)");
				XmlProfile pro = new NaXmlProfile("/naProfile.xml");
				if(line.hasOption("id"))
					pro.setFilterId(line.getOptionValue("id"));
				convertor.setProfile(pro);
			}
			else if(line.hasOption("profile") && line.getOptionValue("profile").equals("xmlau")){
				UserLogger.logger.info("Utilizzo del profilo XML (auProfile.xml)");
				XmlProfile pro = new AuProfile("/auProfile.xml");
				if(line.hasOption("id"))
					pro.setFilterId(line.getOptionValue("id"));
				convertor.setProfile(pro);
			}
			else if(line.hasOption("profile") && line.getOptionValue("profile").equals("xmlcilento")){
				UserLogger.logger.info("Utilizzo del profilo XML (naProfile.xml) versione cilento");
				XmlProfile pro = new CilentoProfile("/naProfile.xml");
				if(line.hasOption("id"))
					pro.setFilterId(line.getOptionValue("id"));
				convertor.setProfile(pro);
			}
			else{
				UserLogger.logger.info("Utilizzo del profilo SBN");
				convertor.setProfile(new SbnProfile());				
			}
			convertor.getProfile().init();

			if(line.hasOption("frontend") && line.getOptionValue("frontend").equals("true")){
				convertor.getProfile().setForFe(true);
			}
			if(line.hasOption("frontend") && line.getOptionValue("frontend").equals("false")){
				convertor.getProfile().setForFe(false);
			}
			if(line.hasOption("offset")){				
				convertor.setOffset(Integer.parseInt(line.getOptionValue("offset")));
				UserLogger.logger.info("Offset = "+convertor.getOffset());
			} 
			if(line.hasOption("rows")){				
				convertor.setRows(Integer.parseInt(line.getOptionValue("rows")));
				UserLogger.logger.info("Rows = "+convertor.getRows());
			} 
			if(line.hasOption("encoding")){
				convertor.setEncoding(line.getOptionValue("encoding"));
				UserLogger.logger.info("Encoding = "+convertor.getEncoding());
			}
			
			
			out = new DefaultOutput(); //;convertor.convert(fin);
			Appendable outfile = null;
			if(line.hasOption("outfile")){
				outfile = new BufferedWriter(new OutputStreamWriter(  
						new FileOutputStream(line.getOptionValue("outfile")),
						"UTF-8")
						);
				UserLogger.logger.info("Si scrive il file '"+line.getOptionValue("outfile")+"'.");
			}
			else if(line.hasOption("solrurl")){
				// niente of
			}
			else if(line.hasOption("directory")){
				// niente of
			}
			else{
				UserLogger.logger.info("Si scrive sul standard output.");
				outfile = System.out;
			}
			OutputFormatter formatter;
			if(line.hasOption("out") && line.getOptionValue("out").equals("mysql")){
				formatter = new MysqlOutputFormatter(out);			
				if(line.hasOption("mysqltruncate") )
					((MysqlOutputFormatter)formatter).setTruncateTables(true);
				if(line.hasOption("mysqlindexes") )
					((MysqlOutputFormatter)formatter).setCreateIndexes(true);
				if(line.hasOption("mysqldrop") )
					((MysqlOutputFormatter)formatter).setDropTables(true);
				UserLogger.logger.info("Si scrive l'output in formato MySQL.");
			}
			else if(line.hasOption("out") && line.getOptionValue("out").equals("plain")){
				formatter = new TextOutputFormatter(out);
			}
			else if(line.hasOption("out") && line.getOptionValue("out").equals("json")){
				formatter = new JsonOutputFormatter(out);
				if(line.hasOption("l"))
					((JsonOutputFormatter)formatter).setLinkCreator(new DefaultLinkCreator());
				if(line.hasOption("directory") )
					((JsonOutputFormatter)formatter).setDirectory(line.getOptionValue("directory"));
			}
			else if(line.hasOption("out") && line.getOptionValue("out").equals("solr")){
				formatter = new SolrOutputFormatter(out);
				((SolrOutputFormatter)formatter).setLinkCreator(new DefaultLinkCreator());
				if(line.hasOption("solrurl") )
					((SolrOutputFormatter)formatter).setSolrUrl(line.getOptionValue("solrurl"));
			}
			else{
				formatter = new XmlOutputFormatter(out);				
				if(line.hasOption("xmlSpaceInNames") )
					((XmlOutputFormatter)formatter).setNoSpaceInNames(false);
				if(line.hasOption("xmlNoAttr") )
					((XmlOutputFormatter)formatter).setPrintAttrs(false);
				UserLogger.logger.info("Si scrive l'output in formato XML.");
			}
			//formatter.toXml(outfile);
			formatter.setOutfile(outfile);
			// formatter.toXml(outfile);
			
			
			if(filename.contains("*")){
				String[] parts = filename.split("\\*",2);
				File[] files  = new File(parts[0]).listFiles();
				for (int i = 0; i < files.length; i++) {
					if(files[i].getName().endsWith(parts[1])){
						FileInputStream fin = makeFile(convertor, files[i].getAbsolutePath());			
						convertor.convert(fin, formatter);						
					}
				}
			}
			else{
				FileInputStream fin = makeFile(convertor, filename);
				if(filename.endsWith(".mrk"))
					convertor.isMrk = true;
				else
					convertor.isMrk = false;
				convertor.convert(fin, formatter);
			}
			
			
			if(outfile!=null && outfile instanceof BufferedWriter)
				((BufferedWriter)outfile).close();					
		} catch (Exception e) {
			UserLogger.logger.error(e);
			e.printStackTrace();
		}
		convertor.shutDown();
	}
	
	public void shutDown(){
		if(cache!=null)
			cache.shutDown();
	}

	public static FileInputStream makeFile(MarcConvertor convertor, String filename) throws FileNotFoundException {
		File inFile = new File(filename);
		FileInputStream fin = new FileInputStream(inFile);
		UserLogger.logger.info("Si legge il file '"+filename+"'.");
		convertor.getProfile().setFilename(inFile.getName());
		convertor.getProfile().notifyFullFilename(filename);
		return fin;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
}

