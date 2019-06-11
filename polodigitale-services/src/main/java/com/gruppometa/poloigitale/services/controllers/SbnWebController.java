package com.gruppometa.poloigitale.services.controllers;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gruppometa.poloigitale.services.components.UnimarcClient;
import com.gruppometa.sbnmarc.SbnMarcClient;
import com.gruppometa.sbnmarc.mag.MagTransformer;
import com.gruppometa.sbnmarc.objects.IccdException;
import com.gruppometa.sbnmarc.objects.IccdRecordList;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

@RestController
@ConfigurationProperties(prefix="sbnWebController")
public class SbnWebController {
	
	protected static Logger logger = LoggerFactory.getLogger(SbnWebController.class);
	
	@Autowired
	protected UnimarcClient unimarcClient;
	
	protected SbnMarcClient client = new SbnMarcClient();

	protected MagTransformer magTransfomrer =  new MagTransformer();
	protected String urlPosseduti;
	protected String usernamePosseduti;
	protected String passwordPosseduti;
	
	public String getUrlPosseduti() {
		return urlPosseduti;
	}

	public void setUrlPosseduti(String urlPosseduti) {
		this.urlPosseduti = urlPosseduti;
	}

	public String getUsernamePosseduti() {
		return usernamePosseduti;
	}

	public void setUsernamePosseduti(String usernamePosseduti) {
		this.usernamePosseduti = usernamePosseduti;
	}

	public String getPasswordPosseduti() {
		return passwordPosseduti;
	}

	public void setPasswordPosseduti(String passwordPosseduti) {
		this.passwordPosseduti = passwordPosseduti;
	}

	protected String url;
	protected String username;
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	protected int timeout = 5000;

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	@PostConstruct
	public void postInit() {
		client.setUsername(username);
		client.setUrl(url);
		client.setPasswordPosseduti(passwordPosseduti);
		client.setUsernamePosseduti(usernamePosseduti);
		client.setUrlPosseduti(urlPosseduti);
		client.setTimeout(timeout);
	}
	
	@RequestMapping("/sbnmarc/iccd")
	public IccdRecordList getResult(
			@RequestParam(value="biblioteca",required=false) String biblioteca,
			@RequestParam(value="bid") String bid,
			@RequestParam(value="type",required=false) String type,
			@RequestParam(value="version",required=false) String version
			) {
			try	{
				return client.getResponseAsList(biblioteca,bid,type, version);
			} catch (Exception e) {
				logger.error("",e);
				return IccdRecordList.getErrorList(e.getMessage());
			}	
		}
	
	/**
	 * trasformazione unimarc in sbnweb/sbnmarc
	 * @param bid numero bid
	 * @return stringa
	 */
	@RequestMapping(value="/unimarc/sbnmarc",
			produces={MediaType.APPLICATION_XML_VALUE})
	public String getSbnWeb(
			@RequestParam(value="bid") String bid) {
		String ret = null;
		try {
			ret = unimarcClient.getResponseFromSolr(bid);
		} catch (Exception e) {
			return "<error>"+ StringEscapeUtils.escapeXml11(e.getMessage())+"</error>";
		}
		if(ret!=null)
			return ret;
		else{			
			return "<notFound/>";
		}
	}

	@RequestMapping(value="/unimarc/marcxml",
			produces={MediaType.APPLICATION_XML_VALUE})
	public String getMarcXml(
			@RequestParam(value="bid") String bid) {
		String ret = null;
		try {
			ret = unimarcClient.getResponseFromSolr(bid,"marc");
		} catch (Exception e) {
			return "<error>"+ StringEscapeUtils.escapeXml11(e.getMessage())+"</error>";
		}
		if(ret!=null)
			return ret;
		else{
			return "<notFound/>";
		}
	}

	/**
	 * trasformazione unimarc in sbnweb/sbnmarc
	 * @param bid numero bid
	 * @return stringa
	 */
	@RequestMapping(value="/unimarc/mag",
			produces={MediaType.APPLICATION_XML_VALUE})
	public String getSbnWeb2Mag(
			@RequestParam(value="bid") String bid) {
		String ret = null;
		try {
			ret = unimarcClient.getResponseFromSolr(bid);
		} catch (Exception e) {
			return "<error>"+ StringEscapeUtils.escapeXml11(e.getMessage())+"</error>";
		}
		if(ret!=null) {
			try {
				return magTransfomrer.transform(ret);
			} catch (Exception e) {
				logger.error("",e);
				return "<error>"+e.getMessage()+"</error>";
			}
		}
		else{
			return "<notFound/>";
		}
	}

	@RequestMapping(value="/opacsbn/mag",
			produces={MediaType.APPLICATION_XML_VALUE})
	public String getOpacSbn2Mag(
			@RequestParam(value="bid") String bid) {
		return getOpacSbn2X(bid, true, false);
	}

	@RequestMapping(value="/opacsbn/sbnmarc",
			produces={MediaType.APPLICATION_XML_VALUE})
	public String getOpacSbn2Sbnmarc(
			@RequestParam(value="bid") String bid) {
		return getOpacSbn2X(bid, false, false);
	}

	@RequestMapping(value="/opacsbn/marcxml",
			produces={MediaType.APPLICATION_XML_VALUE})
	public String getOpacSbn2Marc(
			@RequestParam(value="bid") String bid) {
		return getOpacSbn2X(bid, false, true);
	}

	public String getOpacSbn2X(
			@RequestParam(value="bid") String bid, boolean toMag, boolean toMarcXml) {
		String ret = null;
		try {
			String bid2 = bid.startsWith("IT\\ICCU\\")?bid.substring(8):bid;
			bid2 = bid2.replaceAll("\\\\","");
			bid2 = URLEncoder.encode(bid2,"UTF-8");
			InputStream bidInputStream = null;
			URL url = new URL("http://opac.sbn.it/opacsbn/opaclib?db=solr_iccu&select_db=solr_iccu&" +
					"nentries=1&from=1&searchForm=opac/iccu/error.jsp&resultForward=opac/iccu/scarico_uni.jsp" +
					"&do_cmd=search_show_cmd&format=unimarc&rpnlabel=BID%3D"+bid2+"&" +
					"rpnquery=%40attrset+bib-1++%40attr+1%3D1032+%40attr+4%3D2+%22"+bid2+"%22" +
					"&totalResult=1&fname=none");

			HttpURLConnection huc = (HttpURLConnection) url.openConnection();
			HttpURLConnection.setFollowRedirects(false);
			huc.setConnectTimeout(15 * 1000);
			huc.setRequestMethod("GET");
			huc.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.1.2) Gecko/20090729 Firefox/3.5.2 (.NET CLR 3.5.30729)");
			huc.connect();
			bidInputStream = huc.getInputStream();
			ret = unimarcClient.getResponse(bidInputStream, (toMarcXml?"marc":null) );
		} catch (Exception e) {
			return "<error>"+ StringEscapeUtils.escapeXml11(e.getMessage())+"</error>";
		}
		if(ret!=null && ret.trim().length()>0) {
			if(!toMag)
				return ret;
			try {
				return magTransfomrer.transform(ret);
			} catch (Exception e) {
				logger.error("",e);
				return "<error>"+e.getMessage()+"</error>";
			}
		}
		else{
			return "<notFound/>";
		}
	}

	/**
	 * trasformazione unimarc in sbnweb/sbnmarc
	 * @param bid numero bid
	 * @return stringa
	 */
	@RequestMapping(value="/unimarc/iccd",
			produces={MediaType.APPLICATION_XML_VALUE})
	public String getIccdFromUnimarc(
			@RequestParam(value="bid") String bid) {
		String ret = null;
		try {
			ret = unimarcClient.getResponseFromSolr(bid);
		} catch (Exception e) {
			return "<error>"+ StringEscapeUtils.escapeXml11(e.getMessage())+"</error>";
		}
		if(ret!=null)
			return ret;
		else{
			return "<notFound/>";
		}
	}

	@RequestMapping(value="/sbnmarc/search",
			produces={MediaType.APPLICATION_XML_VALUE})
	public String getSbnMarcWeb(
			@RequestParam(value="biblioteca",required=false) String biblioteca,
			@RequestParam(value="bid") String bid,
			@RequestParam(value="type",defaultValue="F") String type,
			@RequestParam(value="version",defaultValue="4") String version
			){
		String ret;
		try {
			ret = client.getResponseAsString(biblioteca,bid,type,version,false);
		} catch (IccdException e) {
			return "<error>"+e.getMessage()+"</error>";
		}
		if(ret!=null)
			return ret;
		else{			
			return "<notFound/>";
		}
	}
	@RequestMapping(value="/sbnmarc/mag",
			produces={MediaType.APPLICATION_XML_VALUE})
	public String getSbnMarcWebMag(
			@RequestParam(value="biblioteca",required=false) String biblioteca,
			@RequestParam(value="bid") String bid,
			@RequestParam(value="type",defaultValue="F") String type,
			@RequestParam(value="version",defaultValue="4") String version
	){
		String ret;
		try {
			ret = client.getResponseAsString(biblioteca,bid,type,version,false);
		} catch (IccdException e) {
			return "<error>"+e.getMessage()+"</error>";
		}
		if(ret!=null) {
			try {
				ret = ret.replace("<SBNMarc ","<SBNMarc xmlns=\"http://www.iccu.sbn.it/opencms/opencms/documenti/2016/SBNMarcv202.xsd\" ");
				ret =  magTransfomrer.transform(ret);
				if(!ret.trim().equals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"))
					return ret;
				else
					return "<notFound/>";
			} catch (Exception e) {
				logger.error("",e);
				return "<error>"+e.getMessage()+"</error>";
			}
		}
		else{
			return "<notFound/>";
		}
	}

}
