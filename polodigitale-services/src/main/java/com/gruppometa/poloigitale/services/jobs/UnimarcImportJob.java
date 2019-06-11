package com.gruppometa.poloigitale.services.jobs;

import com.gruppometa.poloigitale.services.objects.Message;
import com.gruppometa.unimarc.MarcConvertor;
import com.gruppometa.unimarc.object.DefaultOutput;
import com.gruppometa.unimarc.object.Output;
import com.gruppometa.unimarc.output.DefaultLinkCreator;
import com.gruppometa.unimarc.output.JsonOutputFormatter;
import com.gruppometa.unimarc.output.OutputFormatter;
import com.gruppometa.unimarc.output.SolrOutputFormatter;
import com.gruppometa.unimarc.profile.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.FileInputStream;

@Component
@ConfigurationProperties(prefix="unimarcImportJob")
public class UnimarcImportJob {
	public String getSolrUrl() {
		return solrUrl;
	}
	public void setSolrUrl(String solrUrl) {
		this.solrUrl = solrUrl;
	}
	protected static final Logger logger = LoggerFactory.getLogger(UnimarcImportJob.class);
	protected MarcConvertor convertor = new MarcConvertor();
	protected String status;
	protected int bufferSize = 5000;

	public boolean isShowI4Vid() {
		return showI4Vid;
	}

	public void setShowI4Vid(boolean showI4Vid) {
		this.showI4Vid = showI4Vid;
	}

	protected boolean showI4Vid = true;

	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public String getSolrUrlMetaindiceAu() {
		return solrUrlMetaindiceAu;
	}

	public void setSolrUrlMetaindiceAu(String solrUrlMetaindiceAu) {
		this.solrUrlMetaindiceAu = solrUrlMetaindiceAu;
	}

	protected String message;
	protected String solrUrlMetaindice = "http://127.0.0.1:8983/solr/metaindice";
	protected String solrUrlMetaindiceAu = "http://127.0.0.1:8983/solr/metaindice_au";
	protected String solrUrl = "http://127.0.0.1:8983/solr/polodigitale_fe";

	public String getSolrUrlMetaindice() {
		return solrUrlMetaindice;
	}

	public void setSolrUrlMetaindice(String solrUrlMetaindice) {
		this.solrUrlMetaindice = solrUrlMetaindice;
	}

	public String getSolrUrlAu() {
		return solrUrlAu;
	}

	public void setSolrUrlAu(String solrUrlAu) {
		this.solrUrlAu = solrUrlAu;
	}

	protected String solrUrlAu = "http://127.0.0.1:8983/solr/polodigitale_au";
	protected int i;
	public Message getStatus(){
		Message messageObj = new Message();
		messageObj.setStatus(status); 
		messageObj.setMessage(message);
		messageObj.setConvertorStatus(convertor.getConvertorStatus());
		return messageObj;
	}
	public void stop(){
		convertor.stop();
	}
	XmlProfile pro = null;
	XmlProfile proCilento = null;
	XmlProfile proAu = null;
	XmlProfile proGeneral = null;
	
	@PostConstruct
	public void init(){
		pro = new NaXmlProfile("/naProfile.xml");
		pro.init();
		
		proCilento = new CilentoProfile("/naProfile.xml");
		proCilento.init();

		proGeneral = new GeneralProfile("/naProfile.xml");
		proGeneral.init();

		proAu = new AuProfile("/auProfile.xml");
		proAu.init();
	}
	@Async
	public void run(String filename,String directory, int rows, int offset, String profile, boolean clear, String id,
					String nature){
		try {

			long time = System.currentTimeMillis();
			status = "started";
			message = "";
			convertor.initStatus();
			convertor.setRows(rows);
			convertor.setOffset(offset);
			if(profile.equals("au"))
				convertor.setProfile(proAu);
			else if(profile.equals("cilento"))
				convertor.setProfile(proCilento);
			else if(profile.equals("general"))
				convertor.setProfile(proGeneral);
			else
				convertor.setProfile(pro);
			File inFile = new File(filename);
			FileInputStream fin = new FileInputStream(inFile);
			Output out = new DefaultOutput();
			convertor.getProfile().setFilename(inFile.getName());
			convertor.getProfile().notifyFullFilename(filename);
			OutputFormatter formatter = null;
			if(directory.equals("toSolr")){
				formatter = new SolrOutputFormatter(out);
				DefaultLinkCreator defaultLinkCreator = new DefaultLinkCreator();
				defaultLinkCreator.setShowI4Vid(isShowI4Vid());
				((SolrOutputFormatter)formatter).setLinkCreator(defaultLinkCreator);
				((SolrOutputFormatter)formatter).setSolrUrl(profile.equals("au")?getSolrUrlAu():getSolrUrl());
				/**
				 * ATTENZIONE: implicite: toSolr è FE!
				 */
				convertor.getProfile().setForFe(true);
			}
			else if(directory.equals("toSolrMetaindice")){
				formatter = new SolrOutputFormatter4Metaindice(out);
				((SolrOutputFormatter)formatter).setSolrUrl(getSolrUrlMetaindice());
				convertor.getProfile().setForFe(true);
			}
			else if(directory.equals("toSolrMetaindiceAu")){
				formatter = new SolrOutputFormatter4MetaindiceAu(out);
				((SolrOutputFormatter)formatter).setSolrUrl(getSolrUrlMetaindiceAu());
				convertor.getProfile().setForFe(true);
			}
			else{
				formatter = new JsonOutputFormatter(out);
				((JsonOutputFormatter )formatter).setDirectory(directory);
				convertor.getProfile().setForFe(false);
			}
			formatter.setBufferSize(bufferSize);
			Profile pro = convertor.getProfile();
			if(pro instanceof XmlProfile)
				((XmlProfile)pro).setFilterId(id);
			if(pro instanceof XmlProfile)
				((XmlProfile)pro).setFilterNature(nature);
			convertor.convert(fin, formatter);
			if(id==null && ((formatter instanceof  SolrOutputFormatter4Metaindice) ||
					(formatter instanceof  SolrOutputFormatter4MetaindiceAu)) && clear){
				((SolrOutputFormatter)formatter).clearOlderThan(time,"dominio_s:bibliografico");
			}
			else if(id==null && formatter instanceof  SolrOutputFormatter && clear){
				((SolrOutputFormatter)formatter).clearOlderThan(time,"");
			}
			status = "finished";
		} catch (Exception e) {
			logger.error("",e);
			message = e.getMessage();
			status = "error";
		}
	}
	
	@PreDestroy
	protected void release(){
		if(convertor!=null)
			convertor.shutDown();
	}
}
