package com.gruppometa.poloigitale.services.controllers;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedRequestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.gruppometa.metasearch.query.*;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gruppometa.metasearch.data.DefaultSearchRequest;
import com.gruppometa.poloigitale.services.Application;
import com.gruppometa.poloigitale.services.components.MappedResponseCreator;
import com.gruppometa.poloigitale.services.components.MappedProfileDefinitor;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class ControllerTests {
 
	@Rule
    public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("build/generated-snippets");

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;
    
    private MockMvc mockMvc;

    private RestDocumentationResultHandler document;
    
    @Before
    public void setUp() {
        this.document = document("{method-name}", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()));
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .apply(documentationConfiguration(this.restDocumentation))
                .alwaysDo(this.document)
                .build();
    }
/*
    @Test
    public void getSbnmarc() throws Exception {
    	
    	this.document.snippets(
    			 requestParameters( 
    					 	parameterWithName("biblioteca"). description("L'id della biblioteca per la parte di inventari e possessori (come SP,FC)"),
    		                parameterWithName("bid").description("Il bid del documento da cercare"))
    			);
        this.mockMvc.perform(
        		RestDocumentationRequestBuilders.get("/sbnmarc/search?biblioteca=SP&bid=ANA0000403")
                .accept(MediaType.APPLICATION_XML)
        ).andExpect(status().isOk());
    }

    @Test
    public void getIccd() throws Exception {
    	
    	this.document.snippets(
    			 requestParameters( 
    					 	parameterWithName("biblioteca").description("L'id della biblioteca per la parte di inventari e possessori (come SP,FC)"),
    		                parameterWithName("bid").description("Il bid del documento da cercare"),
    			 			parameterWithName("type").description("Il tipo della scheda F, S, D o AUT o null (senza)"),
    			 			parameterWithName("version").description("La versione della scheda, per ora implementato solo per il tipo AUT o null, default=3"))
    			);
        this.mockMvc.perform(
        		RestDocumentationRequestBuilders.get("/sbnmarc/iccd?biblioteca=SP&bid=NAP0668034&type=F&version=4")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }
    
    @Test
    public void getIccd2() throws Exception {
    	
    	this.document.snippets(
    			 requestParameters( 
    		                parameterWithName("bid").description("Il bid/vid del documento da cercare"),
    			 			parameterWithName("type").description("Il tipo della scheda F, S, D o AUT o null (senza)"),
    			 			parameterWithName("version").description("La versione della scheda, per ora implementato solo per il tipo AUT o null, default=3"))
    			);
        this.mockMvc.perform(
        		RestDocumentationRequestBuilders.get("/sbnmarc/iccd?bid=SBLV224544&type=AUT&version=4")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    public void getOpacresource() throws Exception {

        this.mockMvc.perform(
        		RestDocumentationRequestBuilders.get("/opac/resource?id=ANA0014158")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
		.andDo(document("get-opacresource",
				requestParameters(
						parameterWithName("id").description("Bid della risorsa")
				),
				responseFields(
						fieldWithPath("error").description("eventuale errore").type("String"),
						fieldWithPath("response").description("la risposta"),
						fieldWithPath("response.docs[]").description("lista dei documenti"),
						fieldWithPath("response.docs[].id").description("id del documento").type("String"),
						fieldWithPath("response.docs[].score").description("score del documento"),
						fieldWithPath("response.docs[].nodes[]").description("nodi del documento del tipo view, group e field"),
						fieldWithPath("response.docs[].nodes[].id").description("id del nodo"),
						fieldWithPath("response.docs[].nodes[].type").description("tipo del nodo"),
						fieldWithPath("response.docs[].nodes[].label").description("label del nodo"),
						//fieldWithPath("response.docs[].nodes[].values").description("lista dei valori del nodo").optional(),
						fieldWithPath("response.docs[].nodes[].mimetype").description("mimetype del nodo"),
						fieldWithPath("queryTime").description("tempo di risposta solr")
				)
				));
    }

*/
    FieldDescriptor[] fieldDescriptors = new FieldDescriptor[]{
			fieldWithPath("error").description("eventuale errore").type("String"),
			fieldWithPath("response").description("la risposta"),
			fieldWithPath("response.docs[]").description("lista dei documenti"),
			fieldWithPath("response.docs[].id").description("id del documento").type("String"),
			fieldWithPath("response.docs[].score").description("score del documento"),
			fieldWithPath("response.docs[].nodes[]").description("nodi del documento del tipo view, group e field"),
			fieldWithPath("response.docs[].nodes[].id").description("id del nodo").type("String"),
			fieldWithPath("response.docs[].nodes[].type").description("tipo del nodo"),
			fieldWithPath("response.docs[].nodes[].label").description("label del nodo"),
			//fieldWithPath("response.docs[].nodes[].values").description("lista dei valori del nodo").optional(),
			fieldWithPath("response.docs[].nodes[].mimetype").description("mimetype del nodo").type("String"),
			fieldWithPath("response.facetsFields").description("Le facette"),
			fieldWithPath("response.facetsFields[].id").description("campo solr da utilizzare per il filtro"),
			fieldWithPath("response.facetsFields[].values").description("i valori della faccetta"),
			fieldWithPath("response.facetsFields[].values[].name").description("il testo della faccetta"),
			fieldWithPath("response.facetsFields[].values[].count").description("il numero di occorrenze della faccetta"),
			fieldWithPath("response.numFound").description("Numero record trovati"),
			fieldWithPath("response.start").description("Offset della lista"),
			fieldWithPath("queryTime").description("tempo di risposta solr")

	};
/*
    @Test
    public void getOpacsearch() throws Exception {
    	
    	this.document.snippets(
    			 requestParameters( 
    		                parameterWithName("q").description("query solr da fare, p.e. \"roma\""),
    		                parameterWithName("fq")
    		                	.description("query per filtrare con le faccette, p.e. livello_bibliografico_s:monografia")
    		                	
    		                ),
    			 
    			 responseFields(fieldDescriptors
    					 )
    			);
        this.mockMvc.perform(
        		RestDocumentationRequestBuilders.get("/opac/search?q=roma&fq=livello_bibliografico_s:monografia")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
	public void test(){

	}

	@Test
	public void getFields() throws Exception {
		this.mockMvc.perform(
				RestDocumentationRequestBuilders.get("/iccd/fields?prefix=tito")
						.accept(MediaType.APPLICATION_JSON)
		).andExpect(status().isOk())
				.andDo(document("get-fields",
						requestParameters(
								parameterWithName("prefix").optional().description("Filtro per il nome del campo")),
						relaxedResponseFields(
								fieldWithPath("fields[]").description("I campi"),
								fieldWithPath("fields[].id").description("Identificativo"),
								fieldWithPath("fields[].label").description("Label"),
								fieldWithPath("fields[].datatype").description("Tipo di data"),
								fieldWithPath("fields[].listType").description("Tipo di lista, valori: \"closed\" o altro"),
								fieldWithPath("fields[].searchType").description("Tipo per la visualizzazione nella ricerca avanzata," +
										" valori: \"text\", \"list\" o \"listText\"")
						)
				));
	}

	@Test
	public void getFacet() throws Exception {
		this.mockMvc.perform(
				RestDocumentationRequestBuilders.get("/opac/facet?id=tipo_documento_s&prefix=son")
						.accept(MediaType.APPLICATION_JSON)
		).andExpect(status().isOk())
				.andDo(document("get-facet",
						requestParameters(
								parameterWithName("id").description("Nome della facetta"),
								parameterWithName("prefix").optional().description("Prefisso per il filtro dei valori della facetta (caseinsensitive)")
						),
						relaxedResponseFields(
								fieldWithPath("error").description("eventuale errore").type("String"),
								fieldWithPath("response").description("la risposta"),
								fieldWithPath("response.facetsFields").description("Le facette"),
								fieldWithPath("response.facetsFields[].id").description("campo solr da utilizzare per il filtro").optional().type(JsonFieldType.NUMBER),
								fieldWithPath("response.facetsFields[].label").description("Label della faccetta").optional().type(JsonFieldType.STRING),
								fieldWithPath("response.facetsFields[].values").description("i valori della faccetta").optional().type(JsonFieldType.ARRAY),
								fieldWithPath("response.facetsFields[].values[].name").description("il testo della faccetta").optional().type(JsonFieldType.STRING),
								fieldWithPath("response.facetsFields[].values[].count").description("il numero di occorrenze della faccetta").optional().type(JsonFieldType.NUMBER),
								fieldWithPath("response.numFound").description("Numero record trovati"),
								fieldWithPath("response.start").description("Offset della lista"),
								fieldWithPath("queryTime").description("tempo di risposta solr"))
						))
		;
	}
*/
	@Test
	public void postOpacsearch() throws Exception {

		DefaultSearchRequest request = new DefaultSearchRequest();
		Query q = new Query();
		q.setFacetLimit(5);
		q.setFacetMinimum(1);
		q.setRows(3);
		OrderClause order = new OrderClause();
		//order.setFieldname("istituzione");
		order.setFieldname("Titolo ordinamento");
		q.setOrderClauses(new ArrayList<OrderClause>());
		//q.getOrderClauses().add(order);
		request.setQuery(q);

		String index = "metaindice";
		boolean boostTest =  true;
		if(boostTest) {
			SimpleClause clause = new SimpleClause();
			//clause.setField("Codice identificativo");
			clause.setField("Tutto");
			clause.setInnerOperator(Operator.OPERATOR_CONTAINS_ONE);
			List<String> values = new ArrayList<String>();
			//values.add("MOD1367650");
			//values.add("opera tacendo"); // quello è proprio una serie mongrafica
			values.add("giornale critico della filosofia italiana ");
			//values.add("Croce e Gentile");
			clause.setValues(values);
			q.setClause(clause);
		}
		boolean startsWithTest = false;
		if(startsWithTest) {
			index = "opac";
			SimpleClause clause2 = new SimpleClause();
			//clause.setField("Codice identificativo");
			//clause2.setField("denominazione/titolo");
			clause2.setField("Titoli tutti");
			clause2.setInnerOperator(Operator.OPERATOR_STARTS_WITH);
			List<String> values2 = new ArrayList<String>();
			//values.add("MOD1367650");
			//values.add("opera tacendo"); // quello è proprio una serie mongrafica
			//values2.add("vent'anni ");
			values2.add("L'");
			clause2.setValues(values2);
			q.setClause(clause2);
		}


		SimpleClause filter = new SimpleClause();
		filter.setField("tipo_documento_s");
		filter.setInnerOperator(Operator.OPERATOR_CONTAINS_ALL);
		List<Clause> filters = new ArrayList<Clause>();
		filters.add(filter);
		filter.setValues(Arrays.asList("testo a stampa"));
		// DISABILITA: request.getQuery().setFilters(filters);

		//ObjectMapper mapper = new ObjectMapper();
		String content = objectMapper.writeValueAsString(request);
		this.mockMvc.perform(
				RestDocumentationRequestBuilders.post("/"+index+"/search")
						.content(content)
						.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
						.accept(MediaType.APPLICATION_JSON)
		).andExpect(status().isOk())
		.andDo(document("post-opacsearch",
				relaxedRequestFields(
						fieldWithPath("query").description("Oggetto query"),
						fieldWithPath("query.orderClauses").description("criteri per l'ordinamento"),
						fieldWithPath("query.clause").description("filtro per la ricerca"),
						fieldWithPath("query.clause.type").description("Tipo di clause 'SimpleClause' o 'ComposedClause'"),
						fieldWithPath("query.clause.operator").description("Operatore esterno"),
						fieldWithPath("query.clause.field").description("Nome del campo"),
						fieldWithPath("query.clause.innerOperator").description("Operatore interno"),
						fieldWithPath("query.clause.values").description("Valori")
				)
				,relaxedResponseFields(fieldDescriptors)));
	}
/*
	@Test
	public void postOpacsearch2() throws Exception {

		DefaultSearchRequest request = new DefaultSearchRequest();
		Query q = new Query();
		q.setFacetLimit(5);
		q.setFacetMinimum(1);
		q.setRows(3);
		request.setQuery(q);

		ComposedClause composedClause = new ComposedClause();

		SimpleClause clause2 = new SimpleClause();
		clause2.setField("tipo_documento_s");
		clause2.setInnerOperator(Operator.OPERATOR_CONTAINS_ALL);
		clause2.setValues(Arrays.asList("registrazione sonora musicale"));

		SimpleClause clause = new SimpleClause();
		clause.setField("Autore");
		clause.setInnerOperator(Operator.OPERATOR_CONTAINS_ONE);
		List<String> values = new ArrayList<String>();
		values.add("Digby");
		clause.setValues(values);
		List<Clause> clauses = new ArrayList<Clause>();
		composedClause.setClauses(clauses);
		composedClause.getClauses().add(clause);
		composedClause.getClauses().add(clause2);
		q.setClause(composedClause);

		String content = objectMapper.writeValueAsString(request);
		this.mockMvc.perform(
				RestDocumentationRequestBuilders.post("/opac/search")
						.content(content)
						.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
						.accept(MediaType.APPLICATION_JSON)
		).andExpect(status().isOk())
				.andDo(document("post-opacsearch2",
						relaxedRequestFields(
								fieldWithPath("query").description("Oggetto query"),
								fieldWithPath("query.clause").description("filtro per la ricerca"),
								fieldWithPath("query.clause.type").description("Tipo di clause 'SimpleClause' o 'ComposedClause'"),
								fieldWithPath("query.clause.operator").description("Operatore esterno"),
								//fieldWithPath("query.clause.field").description("Nome del campo"),
								fieldWithPath("query.clause.innerOperator").description("Operatore interno"),
								fieldWithPath("query.clause.clauses").description("lista dei clauses")
								//fieldWithPath("query.clause.values").description("Valori")
						)
				//		,relaxedResponseFields(fieldDescriptors)
				));
	}

    @Test
    public void getJob() throws Exception {
    	
    	this.document.snippets(
    			 requestParameters( 
    		                parameterWithName("filename").description("Percorso assoluto del file Unimarc"),
    			 			parameterWithName("directory").description("La cartella di putput"),
    			 			parameterWithName("profile").description("profilo au o na")
    			 			)
    			);
        this.mockMvc.perform(
        		RestDocumentationRequestBuilders.get("/jobs/import/start?profile=naxml&filename=/opt/dataIn/___IE001_NAP_BN_00019690.mrc&directory=/opt/dataOut/output-20160223")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    public void getJobstatus() throws Exception {
		this.document.snippets(
				responseFields(
				fieldWithPath("message").description("Messaggio di errore"),
				fieldWithPath("status").description("Lo status del job: 'started', 'finished', 'error'"),
				fieldWithPath("convertorStatus").description("stato interno"),
				fieldWithPath("convertorStatus.message" ).description("messaggio aggiuntivo"),
				fieldWithPath("convertorStatus.status" ).description("stato interno p.e. 'idle', 'running', 'stopped'"),
				fieldWithPath("convertorStatus.count" ).description("Numero record elaborati"),
				fieldWithPath("convertorStatus.scartati" ).description("Numero record scartati")
				)
		);
        this.mockMvc.perform(
        		RestDocumentationRequestBuilders.get("/jobs/import/status")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }
*/
/*
    @Test
	public void testMets2Mag() throws Exception{

		this.document.snippets(
			requestParts(
					partWithName("mets").description("il file METS in upload")
			),
			requestParameters(
					parameterWithName("stprog").description("Campo aggiuntivo facoltativo dei metadati"),
					parameterWithName("collection").description("Campo aggiuntivo facoltativo dei metadati"),
					parameterWithName("agency").description("Campo aggiuntivo facoltativo dei metadati"),
					parameterWithName("access_rights").description("Campo aggiuntivo facoltativo dei metadati"),
					parameterWithName("completeness").description("Campo aggiuntivo facoltativo dei metadati")
			)
		);
		byte[] encoded = Files.readAllBytes(Paths.get("/opt/Progetti/mets2mag/src/test/resources/IBNN_BNVA001366035.xml"));
		this.mockMvc.perform(
				RestDocumentationRequestBuilders.fileUpload("/mets/mag").file("mets", encoded)
						.param("stprog","stprog")
						.param("collection","collection")
						.param("agency","agency")
						.param("access_rights","access_rights")
						.param("completeness","completeness")
						.accept(MediaType.APPLICATION_XML)
		).andExpect(status().isOk());
	}
	*/
}
