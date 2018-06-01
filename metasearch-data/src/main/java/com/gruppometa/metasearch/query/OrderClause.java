package com.gruppometa.metasearch.query;

/**
 * Created by ingo on 01/12/16.
 */
public class OrderClause {
    protected String fieldname;
    protected String direction="asc";

    public String getFieldname() {
        return fieldname;
    }

    public void setFieldname(String fieldname) {
        this.fieldname = fieldname;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
