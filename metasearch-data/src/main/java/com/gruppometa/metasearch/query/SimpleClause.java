package com.gruppometa.metasearch.query;

import java.util.List;

public class SimpleClause implements Clause{
	protected Operator operator = Operator.OPERATOR_AND;
	public Operator getOperator() {
		return operator;
	}
	public void setOperator(Operator operator) {
		this.operator = operator;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public Operator getInnerOperator() {
		return innerOperator;
	}
	public void setInnerOperator(Operator innerOperator) {
		this.innerOperator = innerOperator;
	}
	public List<String> getValues() {
		return values;
	}
	public void setValues(List<String> values) {
		this.values = values;
	}
	protected String field;
	protected Operator innerOperator = Operator.OPERATOR_AND;
	protected List<String> values;
	protected double boost = 1;

	public double getBoost() {
		return boost;
	}

	public void setBoost(double boost) {
		this.boost = boost;
	}

	@Override
	public String toString(){
		if(values!=null && values.size()>0)
			return field+":"+values.get(0)+"";
		return "";
	}
}
