package com.gruppometa.poloigitale.services.controllers;

import com.gruppometa.metasearch.data.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by ingo on 03/07/17.
 */
@Controller
public class ViewController {

    @Autowired
    protected SolrSearchController solrSearchController;

    @RequestMapping(value="/view/search")
    public String getSearch(
            @RequestParam(name = "q") String q,
            @RequestParam(name="page", defaultValue = "1") int page,
            Model model
    ) {
       SearchResponse result = solrSearchController.getDocs(
               q,
               null,
               null,false, 10, 0,100,"opac");

        model.addAttribute("result", result);
        return "sintetica";
    }

    @RequestMapping(value="/view/resource")
    public String getDoc(
            @RequestParam(name = "id") String id,
            @RequestParam(name="page", defaultValue = "1") int page,
            Model model
    ) {
        SearchResponse result = solrSearchController.getDoc(
                id,
                "full",
                "opac");
        model.addAttribute("result", result);
        return "dettaglio";
    }
}
