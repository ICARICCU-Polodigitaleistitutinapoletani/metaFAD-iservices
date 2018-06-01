package com.gruppometa.poloigitale.services.controllers;

import com.gruppometa.metasearch.query.Operator;
import com.gruppometa.metasearch.query.SimpleClause;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

/**
 * Created by ingo on 20/04/17.
 */
@ConfigurationProperties(prefix="recordCollegatiArchivioController")
@RestController
public class RecordCollegatiArchivioController extends RecordCollegatiController{

    @RequestMapping("/archivio-ud/roles")
    public FilterList getRoles(
            @RequestParam(value="id") String id
    ){
        return super.getRoles(id);
    }

    @Override
    protected SimpleClause getAllClause(){
        SimpleClause clauseAll = new SimpleClause();
        clauseAll.setField("type_nxs");
        clauseAll.setInnerOperator(Operator.OPERATOR_CONTAINS_ONE);
        clauseAll.setValues(Arrays.asList("archiveUN"));
        return clauseAll;
    }
}
