package com.gruppometa.metasearch.data;

/**
 * Created by ingo on 22/08/16.
 */
public class Label {
    public Label(){

    }
    public Label(String id, String label) {
        super();
        this.id = id;
        this.label = label;
    }
    protected String id;
    protected String label;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }

}
