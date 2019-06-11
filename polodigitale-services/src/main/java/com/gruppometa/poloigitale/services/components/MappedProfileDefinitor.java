package com.gruppometa.poloigitale.services.components;

import com.gruppometa.metasearch.data.Field;
import com.gruppometa.metasearch.data.FieldList;
import com.gruppometa.metasearch.data.ProfileDefinitor;
import com.gruppometa.metasearch.data.Label;
import com.gruppometa.poloigitale.services.controllers.SolrSearchController;
import com.gruppometa.unimarc.mapping.MappingDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by ingo on 22/08/16.
 */

@Component
@ConfigurationProperties(prefix="mappedProfileDefinitor")
public class MappedProfileDefinitor implements  ProfileDefinitor {

    @Autowired
    protected MappedResponseCreator responseCreator;

    protected List<Label> labels;

    public List<Label> getLabels() {
        return labels;
    }

    public void setLabels(List<Label> labels) {
        this.labels = labels;
    }

    @Override
    public FieldList getFields(String viewName, String profileName) {
        return getFields(viewName,null, profileName);

    }

    @Override
    public FieldList getFields(String viewName, String prefix, String profileName) {
        FieldList list = new FieldList();
        List<MappingDefinition> defs = responseCreator.getDefinitions(viewName, profileName);
        Map<String,String> map = new HashMap<String, String>();
        for (MappingDefinition mappingDefinition : defs) {
            if(mappingDefinition.isExcludeFromSearchField() || map.containsKey(mappingDefinition.getDestination()))
                continue;
            String id = mappingDefinition.getDestination();//responseCreator.getSolrName(mappingDefinition);
            /**
             * senza facets
             */
            if(!mappingDefinition.isFacets()) {
                Field f = new Field();
                f.setId(id);
                if(mappingDefinition.getLabel()!=null)
                    f.setLabel(mappingDefinition.getLabel());
                else
                    f.setLabel(getLabel(id, mappingDefinition.getDestination()));
                f.setMultiple(mappingDefinition.isMultiple());
                f.setFulltext(mappingDefinition.isFulltext());
                f.setSearchType(mappingDefinition.getSearchType());
                if (mappingDefinition.getType() != null)
                    f.setDatatype(mappingDefinition.getType());
                if(mappingDefinition.getType()!=null && mappingDefinition.getType().equals("text"))
                    f.setFacet(false);
                else
                    f.setFacet(true);
                if(mappingDefinition.getListType()!=null)
                    f.setListType(mappingDefinition.getListType());
                else if(mappingDefinition.getVocabulary()!=null){
                    f.setListType("closed");
                }
                f.setRange(mappingDefinition.getRangeEnd()!=null);
                f.setStartsWith(mappingDefinition.isStartsWith());
                list.getFields().add(f);
            }
            if(mappingDefinition.isFacets()){
                Map<String,String> map2 = new HashMap<String, String>();
                for (MappingDefinition mappingDefinition2 : mappingDefinition.getSubDefs()) {
                    if(mappingDefinition2.isExcludeFromSearchField()
                            ||( !mappingDefinition2.isSearchField()
                                && mappingDefinition2.getVocabulary()==null))
                        continue;
                    String key = responseCreator.getSolrName( mappingDefinition,mappingDefinition2);
                    if(map2.containsKey(key)){
                        continue;
                    }
                    Field f1 = new Field();
                    f1.setId(key);
                    f1.setLabel(mappingDefinition.getDestination()+" - " +mappingDefinition2.getDestination());
                    f1.setMultiple(mappingDefinition2.isMultiple());
                    if(mappingDefinition2.getListType()!=null)
                        f1.setListType(mappingDefinition2.getListType());
                    else
                        f1.setListType("closed");
                    f1.setSearchType(f1.getSearchType());
                    f1.setFacet(true);
                    f1.setFulltext(false);
                    list.getFields().add(f1);
                    map2.put(key, key);
                }
            }
            map.put(mappingDefinition.getDestination(), id);
        }
        /**
         * aggiungo tutte le faccette
         */
        boolean addFacets = false;
        if(addFacets) {
            Map<String, MappingDefinition> facets = responseCreator.getFacetsMap(profileName);
            for (String key : facets.keySet()) {
                if (map.containsKey(key))
                    continue;
                MappingDefinition mappingDefinition = facets.get(key);
                Field f = new Field();
                f.setId(key);
                if (mappingDefinition.getType() != null)
                    f.setDatatype(mappingDefinition.getType());
                f.setLabel(mappingDefinition.getDestination());
                f.setMultiple(mappingDefinition.isMultiple());
                f.setFacet(true);
                f.setFulltext(false);
                list.getFields().add(f);
                map.put(key, key);
            }
        }
        /**
         * filtro per prefisso
         */
        if(prefix!=null) {
            for (int i = list.getFields().size()-1; i >= 0; i--) {
                if(!list.getFields().get(i).getLabel().toLowerCase().contains(prefix.toLowerCase()))
                    list.getFields().remove(i);
            }
        }
        list.sort();
        return list;
    }

    private String getLabel(String id, String destination) {
        if(labels!=null) {
            for (Label label : labels) {
                if (label.getId().equals(id))
                    return label.getLabel();
            }
        }
        return destination;
    }
}
