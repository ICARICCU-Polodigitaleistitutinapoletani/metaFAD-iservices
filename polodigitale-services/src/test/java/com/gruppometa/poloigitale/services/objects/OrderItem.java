package com.gruppometa.poloigitale.services.objects;

/**
 * Created by ingo on 20/09/16.
 */
public class OrderItem{
    protected String field;

    public OrderItem(String field, int order) {
        this.field = field;
        this.order = order;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    protected int order;
}