package com.gruppometa.poloigitale.services.controllers;

import com.gruppometa.metasearch.data.SolrSearchResponse;
import com.gruppometa.metasearch.query.*;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ingo on 06/12/16.
 */
@ConfigurationProperties(prefix="recordCollegatiController")
@RestController
public class RecordCollegatiController {
    public String getSolrUrl() {
        return solrUrl;
    }

    public String getQueryField2() {
        return queryField2;
    }

    public void setQueryField2(String queryField2) {
        this.queryField2 = queryField2;
    }

    protected String queryField = "responsabilita_principale_html_nxtxt";

    protected String queryField2 = null;

    public void setSolrUrl(String solrUrl) {
        this.solrUrl = solrUrl;
    }

    protected String solrUrl;

    public String getQueryField() {
        return queryField;
    }

    public void setQueryField(String queryField) {
        this.queryField = queryField;
    }

    protected static final Logger logger = LoggerFactory.getLogger(RecordCollegatiController.class);


    @RequestMapping("/opac/roles")
    public FilterList getRoles(
            @RequestParam(value="bid") String bid
    ){
        FilterList list = new FilterList();
        List<String> roles = new ArrayList<>();
        try {
            makeRoles(queryField, bid, roles);
        }
        catch(Exception e){
            logger.error("",e);
            return list;
        }

        /**
         * tutti i record collegati
         */

        Filter filterAll = new Filter();
        filterAll.setRole("");
        SimpleClause s1All = new SimpleClause();
        s1All.setField(queryField);
        s1All.setValues(Arrays.asList(bid));
        /**
         * cerca tutto
         */
        SimpleClause clauseAll = getAllClause();
        Query queryAll = new Query();
        queryAll.setFilters(new ArrayList<Clause>());
        queryAll.getFilters().add(s1All);
        queryAll.setClause(clauseAll);
        filterAll.setQuery(queryAll);
        list.getList().add(filterAll);

        makeFilters(roles, queryField, bid,list);

        if(getQueryField2()!=null){
            roles.clear();
            try {
                makeRoles(queryField2, bid, roles);
            }
            catch(Exception e){
                logger.error("",e);
                return list;
            }
            makeFilters(roles, queryField2, bid,list);
        }

        return list;
    }


    protected void makeFilters(List<String> roles, String queryField, String bid, FilterList list){

        for (String role: roles
                ) {
            /**
             *  i filtri
             */
            Filter filter = new Filter();
            filter.setRole(role);
            SimpleClause s1 = new SimpleClause();
            s1.setField(queryField);
            s1.setValues(Arrays.asList(bid));
            SimpleClause s2 = new SimpleClause();
            s2.setField(queryField);
            s2.setValues(Arrays.asList("\"["+role+"]\""));
            /**
             * cerca tutto
             */
            SimpleClause clause = getAllClause();
            Query query = new Query();
            query.setFilters(new ArrayList<Clause>());
            query.getFilters().add(s1);
            query.getFilters().add(s2);
            query.setClause(clause);
            filter.setQuery(query);
            list.getList().add(filter);
        }
    }
    protected SimpleClause getAllClause(){
        SimpleClause clauseAll = new SimpleClause();
        clauseAll.setField("Tutto");
        clauseAll.setInnerOperator(Operator.OPERATOR_CONTAINS_ONE);
        clauseAll.setValues(Arrays.asList("*"));
        return clauseAll;
    }


    protected void makeRoles(String queryField, String bid, List<String> roles) throws IOException, SolrServerException {
        HttpSolrClient httpSolrClient = new HttpSolrClient(getSolrUrl());
        httpSolrClient.setParser(new XMLResponseParser());
        SolrQuery solrQuery = new SolrQuery(queryField+":"+bid);
        solrQuery.addField(queryField);
        logger.debug(solrQuery.toString());
        QueryResponse rsp = httpSolrClient.query(solrQuery, SolrRequest.METHOD.POST);
        for (SolrDocument doc: rsp.getResults()
                ) {
            if(doc.getFieldValues(queryField)!=null){
                for (Object v: doc.getFieldValues(queryField)
                        ) {
                    String s = (String)v;
                    if(s.toLowerCase().contains(bid.toLowerCase()) && s.contains("[") && s.contains("]")) {
                        String candidate = s.substring(s.indexOf("[") + 1, s.indexOf("]"));
                        if(!roles.contains(candidate))
                            roles.add(candidate);
                    }
                }
            }
        }
        httpSolrClient.close();
    }

    public class FilterList{
        List<Filter> list = new ArrayList<>();
        String message;
        String status = "ok";

        public List<Filter> getList() {
            return list;
        }

        public void setList(List<Filter> list) {
            this.list = list;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public class Filter{
        protected String role;

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public Query getQuery() {
            return query;
        }

        public void setQuery(Query query) {
            this.query = query;
        }

        protected Query query;
    }
}
