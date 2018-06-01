package com.gruppometa.metasearch.query;


public class Operator {
	public static final Operator OPERATOR_AND = new Operator("AND");
	public static final Operator OPERATOR_OR = new Operator("OR");
	public static final Operator OPERATOR_NOT = new Operator("NOT");
	public static final Operator OPERATOR_CONTAINS_ALL = new Operator("contains all");
	public static final Operator OPERATOR_CONTAINS_ONE = new Operator("contains one");
	public static final Operator OPERATOR_BETWEEN = new Operator("between");
	public static final Operator OPERATOR_EQUAL = new Operator("=");
	public static final Operator OPERATOR_GREATER_THAN = new Operator(">");
	public static final Operator OPERATOR_LOWER_THAN = new Operator("<");
	
	protected String operator = "";

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public Operator() {
	}
	
	public Operator(String operator) {
		this.operator = operator;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Operator){
			return operator.equals(((Operator)obj).getOperator());
		}
		return super.equals(obj);
	}

	
}
