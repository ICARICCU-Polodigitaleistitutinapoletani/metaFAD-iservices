package com.gruppometa.metasearch.query;

import java.util.List;

public class ComposedClause implements Clause{
	protected List<Clause> clauses;
	protected Operator operator;
	public List<Clause> getClauses() {
		return clauses;
	}
	public void setClauses(List<Clause> clauses) {
		this.clauses = clauses;
	}
	public Operator getOperator() {
		return operator;
	}
	public void setOperator(Operator operator) {
		this.operator = operator;
	}
	public Operator getInnerOperator() {
		return innerOperator;
	}
	public void setInnerOperator(Operator innerOperator) {
		this.innerOperator = innerOperator;
	}
	protected Operator innerOperator = Operator.OPERATOR_AND;
	
	@Override
	public String toString(){
		String str ="";
		for (Clause clause : clauses) {
			if(str.length()>0)
				str+=" "+innerOperator.getOperator()+" ";
			str+= clause.toString();
		}
		return str;
	}
}
