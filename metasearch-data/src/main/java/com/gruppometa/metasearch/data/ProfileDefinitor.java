package com.gruppometa.metasearch.data;

/**
 * Created by ingo on 22/08/16.
 */
public interface ProfileDefinitor {
    FieldList getFields(String viewName, String profileName);
    FieldList getFields(String viewName, String prefix, String profileName);
}
