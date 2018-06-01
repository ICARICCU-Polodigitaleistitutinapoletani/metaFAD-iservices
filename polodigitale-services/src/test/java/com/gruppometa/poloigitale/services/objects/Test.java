package com.gruppometa.poloigitale.services.objects;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gruppometa.metasearch.query.Clause;
import com.gruppometa.metasearch.query.Operator;
import com.gruppometa.metasearch.query.Query;
import com.gruppometa.metasearch.query.SimpleClause;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by ingo on 14/03/17.
 */
public class Test {
    @org.junit.Test
    public void test(){
        String queryField = "responsabilita_principale_html_nxtxt";
        String role="pippo";
        String bid = "BID";
        SimpleClause s1 = new SimpleClause();
        s1.setField(queryField);
        s1.setValues(Arrays.asList(bid));
        SimpleClause s2 = new SimpleClause();
        s2.setField(queryField);
        s2.setValues(Arrays.asList("\"["+role+"]\""));
        /**
         * cerca tutto
         */
        SimpleClause clause = new SimpleClause();
        clause.setField("Tutto");
        clause.setInnerOperator(Operator.OPERATOR_CONTAINS_ONE);
        clause.setValues(Arrays.asList("*"));
        Query query = new Query();
        query.setFilters(new ArrayList<Clause>());
        query.getFilters().add(s1);
        query.getFilters().add(s2);
        query.setClause(clause);
        ObjectMapper objectMapper = new ObjectMapper();
        StringWriter stringWriter = new StringWriter();

        try {
            objectMapper.writeValue(stringWriter, query);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(stringWriter.toString());
    }
}
