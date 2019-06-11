package com.gruppometa.metasearch.data;

import com.gruppometa.metasearch.query.Operator;
import org.apache.solr.client.solrj.SolrQuery;

import com.gruppometa.metasearch.query.Query;
import org.apache.solr.client.solrj.util.ClientUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SolrQueryCreator {
	public SolrQuery createQuery(Query query, String profileName) throws FieldException {
		SolrQuery querySolr = new SolrQuery();
		querySolr.setRows(query.getRows());
		querySolr.setStart(query.getStart());
		querySolr.setFacetLimit(query.getFacetLimit());
		querySolr.setFacetMinCount(query.getFacetMinimum());
		querySolr.setQuery(query.getClause().toString());
		return querySolr;
	}
	public static List<String> getSolrTokens(String value){
		if(value==null)
			return null;
		List<String> list = new ArrayList<String>();
		Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(value);
		while (m.find())
			list.add(quoteString(m.group(1).replace("\"", "")));
		return list;
	}

	public SolrQuery createQuery4Facet(String facet, String prefix,String profileName) throws FieldException  {
		SolrQuery querySolr = new SolrQuery();
		querySolr.setRows(1);
		querySolr.setStart(0);
		querySolr.setFacetLimit(-1);
		StringBuffer buf = new StringBuffer();
		buf.append("*:*");
		querySolr.setQuery(buf.toString());
		return querySolr;
	}


	public static String quoteString(String value){
		if(value.endsWith("*"))
			return ClientUtils.escapeQueryChars(value.substring(0,value.length()-1))+"*";
		if(value.contains(" "))
			return "\""+ClientUtils.escapeQueryChars(value)+"\"";
		else
			return ClientUtils.escapeQueryChars(value);
	}
	protected String getConjunction(Operator innerOperator) {
		if(innerOperator.equals(Operator.OPERATOR_CONTAINS_ONE)
				||
				innerOperator.equals(Operator.OPERATOR_OR)	)
			return "OR";
		return "AND";
	}
}
