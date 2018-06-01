package com.gruppometa.poloigitale.services.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger; 
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.gruppometa.poloigitale.services.objects.KardexResponse;
import com.gruppometa.sbnmarc.BasicAuthorizationInterceptor;

import it.sbnweb.kardex.FascicoloType;
import it.sbnweb.kardex.KardexType;
import it.sbnweb.kardex.SbnwebType;

@RestController
@RequestMapping("/kardex")
@ConfigurationProperties(prefix="kardexService")
public class KardexController {

	protected static Logger logger = LoggerFactory.getLogger(KardexController.class);
	protected String password;
	protected String username;
	
	protected RestTemplate restTemplate;
	
	protected String baseUrl;
	
	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {		
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@PostConstruct
	public void makeRestTemplate(){
		restTemplate = new RestTemplate();
		List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
		interceptors.add(new BasicAuthorizationInterceptor(getUsername(), getPassword()));
		//interceptors.add(new LoggingRequestInterceptor());
		restTemplate.setInterceptors(interceptors);
		/**
		 * serve per le liste degli inventari
		 */
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
		JacksonXmlModule module = new JacksonXmlModule();
		module.setDefaultUseWrapper(false);
		XmlMapper xmlMapper = new XmlMapper(module);
		messageConverters.add(new MappingJackson2XmlHttpMessageConverter(xmlMapper));
		restTemplate.setMessageConverters(messageConverters);
		
	}
	@RequestMapping("/search")
	public KardexResponse getKardex(
			@RequestParam(value="bid") String bid,
			@RequestParam(value="biblioteca") String biblioteca,
			@RequestParam(value="inventario",required=false) String inventario,
			@RequestParam(value="collocazione",required=false) String collocazione,
			@RequestParam(value="checkOnly",required=false,defaultValue="false") boolean checkOnly
			) {
		KardexResponse response = new KardexResponse();
		SbnwebType type = null;
		try {
			String url =  getBaseUrl()+"{biblioteca}/{bid}";
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("bid", bid);
			params.put("biblioteca", biblioteca);
			// BIB+++0000inventario
			if(inventario!=null){
				url+="/inventario?id={id}";
				params.put("id", inventario);
				response.setInventario(inventario);
			}
			// con tutti i spazi come nell'UNIMARC
			else if(collocazione!=null){
				url+="/collocazione?id={id}";
				params.put("id", collocazione);
				response.setCollocazione(collocazione);
			}
			type =  restTemplate.getForObject(url,	SbnwebType.class,params);
			/**
			 * handle result
			 */
			if(type!=null){
				if(type.getDocumento()!=null){			
					response.setIsbd(type.getDocumento().getIsbd());
					response.setBid(type.getDocumento().getBid());
				}
				if(type.getKardex()!=null){
					response.setContainsKardex(true);
					if(!checkOnly)
						response.setKardexType(orderFascicolo(type.getKardex()));
					response.setMessage("Ok");
				}
			}
		} catch (Exception e) {
			response.setMessage(e.getMessage());
		}
		response.stopTime();
		return response;
	}

	private KardexType orderFascicolo(KardexType kardex) {
		if(kardex!=null && kardex.getInventario()!=null && kardex.getInventario().size()>0){
			List<FascicoloType> fascs = kardex.getInventario().get(0).getFascicolo();
			Collections.sort(fascs, new Comparator<FascicoloType>() {

				public int compare(FascicoloType o1, FascicoloType o2) {
					int ret = o1.getAnnata().compareTo(o2.getAnnata());
					if(ret!=0)
						return ret;
					ret = o1.getDataPubblicazione().compare(o2.getDataPubblicazione());
					if(ret!=0)
						return ret;
					try{
						return Integer.compare( Integer.parseInt( o1.getNumerazione()) , Integer.parseInt(o2.getNumerazione()));
					}
					catch(Exception e){
						logger.error("",e);
						return 0;
					}
				}
			});
		}
		return kardex;
	}

	
	private static class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {

	    final static Logger logger = LoggerFactory.getLogger(LoggingRequestInterceptor.class);

	    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

	        traceRequest(request, body);
	        ClientHttpResponse response = execution.execute(request, body);
	        traceResponse(response);
	        return response;
	    }

	    private void traceRequest(HttpRequest request, byte[] body) throws IOException {
	        logger.debug("===========================request begin================================================");

	        logger.debug("URI : " + request.getURI());
	        logger.debug("Method : " + request.getMethod());
	        logger.debug("Request Body : " + new String(body, "UTF-8"));
	        logger.debug("==========================request end================================================");
	    }

	    private void traceResponse(ClientHttpResponse response) throws IOException {
	        StringBuilder inputStringBuilder = new StringBuilder();
	        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody(), "UTF-8"));
	        String line = bufferedReader.readLine();
	        while (line != null) {
	            inputStringBuilder.append(line);
	            inputStringBuilder.append('\n');
	            line = bufferedReader.readLine();
	        }
	        logger.debug("============================response begin==========================================");
	        logger.debug("status code: " + response.getStatusCode());
	        logger.debug("status text: " + response.getStatusText());
	        logger.debug("Response Body : " + inputStringBuilder.toString());
	        logger.debug("=======================response end=================================================");
	    }

	}
}
