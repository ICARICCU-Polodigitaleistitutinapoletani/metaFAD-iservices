package com.gruppometa.poloigitale.services.components;

import com.gruppometa.metasearch.data.FieldException;
import com.gruppometa.metasearch.query.*;
import com.gruppometa.unimarc.output.SolrOutputFormatter;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.gruppometa.metasearch.data.SolrQueryCreator;
import com.gruppometa.unimarc.mapping.MappingDefinition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@ConfigurationProperties(prefix = "mappedSolrQueryCreator")
public class MappedSolrQueryCreator extends SolrQueryCreator{

	protected static final Logger logger = LoggerFactory.getLogger(MappedSolrQueryCreator.class);

	public HashMap<String, String> getQueryAdds() {
		return queryAdds;
	}

	public void setQueryAdds(HashMap<String, String> queryAdds) {
		this.queryAdds = queryAdds;
	}

	protected HashMap<String, String> queryAdds;

	protected HashMap<String, String> filterQueries;

	public HashMap<String, String> getFilterQueries() {
		return filterQueries;
	}

	public void setFilterQueries(HashMap<String, String> filterQueries) {
		this.filterQueries = filterQueries;
	}

	@Autowired
	protected MappedResponseCreator mappedResponseCreator;// = new MappedResponseCreator();

	@Override
	public SolrQuery createQuery4Id(String id, String profileName){
		SolrQuery solrQuery = super.createQuery4Id(id, profileName);
		if(queryAdds!=null && queryAdds.get(profileName)!=null) {
			solrQuery.setQuery(solrQuery.getQuery()+ " AND "+queryAdds.get(profileName));
		}
		return solrQuery;
	}

	@Override
	public SolrQuery createQuery(Query query, String profileName) throws FieldException  {
		if(query.isFieldNamesAreNative())
			return super.createQuery(query, profileName);
		else{
			SolrQuery solrQuery = super.createQuery(query, profileName);
			StringBuffer buf = new StringBuffer();
			makeClause(buf, query.getClause(), profileName);
			if(buf.toString().length()==0){
				throw new FieldException("No query found.");
			}
			if(filterQueries!=null && filterQueries.get(profileName)!=null){
				solrQuery.setQuery("("+buf.toString()+") AND "+filterQueries.get(profileName));
			}
			else if(queryAdds!=null && queryAdds.get(profileName)!=null){
				solrQuery.setQuery("("+buf.toString()+") AND "+queryAdds.get(profileName));
			}
			else
				solrQuery.setQuery(buf.toString());
			List<String> filters = new ArrayList<String>();
			makefilter(filters,query.getFilters(), profileName );
			makeOrder(query, solrQuery, profileName);
			solrQuery.setFilterQueries(filters.toArray(new String[filters.size()]));
			return solrQuery;
		}
	}

	protected void makeOrder(Query query, SolrQuery solrQuery, String profileName) {
		if(query.getOrderClauses()==null)
			return;
		for (OrderClause clause: query.getOrderClauses()) {
			MappingDefinition def = mappedResponseCreator.getMappginDefinition(clause.getFieldname(),profileName);
			if(def!=null) {
				String fieldname = mappedResponseCreator.getSolrName(def);
				if(def.getSortFieldName()!=null)
					fieldname = def.getSortFieldName();
				else
					fieldname =  SolrOutputFormatter.getSortFieldName(fieldname);
				solrQuery.addSort(fieldname, clause.getDirection().equals("asc") ? SolrQuery.ORDER.asc
						: SolrQuery.ORDER.desc);
			}
			else
				logger.error("Field definition not found for '"+clause.getFieldname()+"'.");
		}
	}

	@Override
	public SolrQuery createQuery4Facet(String facet,String prefix, String profileName) throws FieldException  {
		String solrFacet = mappedResponseCreator.getSolrNameForFacet(facet, profileName);
		if(solrFacet==null)
			solrFacet = facet;
		SolrQuery querySolr = new SolrQuery();
		querySolr.addField("id");
		querySolr.setRows(0);
		querySolr.setFacet(true);
		querySolr.addFacetField(solrFacet);
		querySolr.setStart(0);
		querySolr.setFacetMinCount(1);
		querySolr.setFacetLimit(-1);
		String query = "*:*";
		if(prefix!=null && prefix.trim().length()>0){
			String f = mappedResponseCreator.getSolrNameTextFromFacet(solrFacet!=null?solrFacet:facet);
			query =  f+":"+ ClientUtils.escapeQueryChars(prefix)+"*";
		}
		if(queryAdds!=null && queryAdds.get(profileName)!=null){
			querySolr.setQuery("("+query+") AND "+queryAdds.get(profileName));
		}
		else
			querySolr.setQuery(query);
		return querySolr;
	}

	protected void makefilter(List<String> filters,List<Clause> clauses, String profileName) {
		if(clauses!=null) {
			for (Clause clause : clauses) {
				if (clause instanceof SimpleClause) {
					SimpleClause c = (SimpleClause) clause;
					MappingDefinition def = mappedResponseCreator.getMappginDefinition(c.getField(), profileName);
					String fieldName = null;
					if (def == null) {
						fieldName = c.getField();
						logger.warn("Not found def for " + c.getField());
					} else
						fieldName = mappedResponseCreator.getSolrName(def);
					for (String v : c.getValues()) {
						StringBuffer buf = new StringBuffer();
						buf.append(fieldName);
						buf.append(":");
						buf.append("\"" + ClientUtils.escapeQueryChars(
								mappedResponseCreator.getOriginalOfFilteredValue(profileName,c.getField(),v)
								) + "\"");
						filters.add(buf.toString());
					}
				}
			}
		}
	}
	protected void makeClause(StringBuffer buf,Clause clause, String profileName) throws FieldException {
		if(clause  instanceof SimpleClause) {
			SimpleClause c = (SimpleClause)clause;
			if(mappedResponseCreator==null)
				logger.error("is null");
			MappingDefinition def = mappedResponseCreator.getMappginDefinition(c.getField(), profileName);
			if(def==null){
				throw new FieldException("Campo '"+c.getField()+ "' non trovato.");
			}
			String fieldName = mappedResponseCreator.getSolrName(def);
			boolean isContains = false;
			boolean isVocabulary = false;
			if(def.getVocabulary()!=null){
				isVocabulary = true;
			}
			if(c.getInnerOperator()!=null && c.getInnerOperator().getOperator().startsWith("contains")) {
				fieldName = mappedResponseCreator.getSolrTxtName(def);
				isContains = true;
			}
			if(fieldName==null){
				throw new FieldException("Cannot create solrField for "+clause);
			}
			String operator = "", postOperator = "";
			if(c.getOperator()!=null && c.getOperator().equals(Operator.OPERATOR_NOT)) {
				operator = "NOT(";
				postOperator = ")";
			}
			int i = 0;
			buf.append(operator);
			if(c.getInnerOperator().equals(Operator.OPERATOR_BETWEEN)){
				if(c.getValues().size()==2) {
					if(def.getRangeEnd()==null) {
						buf.append(createRangeQueryPart(fieldName,null,c.getValues(), def.getType()));
					}
					else{
						fieldName = mappedResponseCreator.getSolrName(def,true);
						MappingDefinition rangeEnd = mappedResponseCreator.getMappginDefinition(def.getRangeEnd(), profileName);
						String fieldName2 = mappedResponseCreator.getSolrName(rangeEnd,true);
						buf.append(createRangeQueryPart(fieldName,fieldName2,c.getValues(), def.getType()));
					}
				}
				else{
					logger.error("In correct query between size: "+ c.getValues().size());
				}
			}
			else {
				for (String v : c.getValues()) {
					if (i > 0)
						buf.append(" " + getConjunction(c.getInnerOperator()) + " ");
					if (isContains) {
						List<String> tokens = SolrQueryCreator.getSolrTokens(v);
						buf.append("(");
						int j = 0;
						String op = " OR ";
						if (c.getInnerOperator().equals(Operator.OPERATOR_CONTAINS_ALL))
							op = " AND ";
						for (String token : tokens
								) {
							if (j > 0)
								buf.append(" " + op + " ");
							buf.append(fieldName);
							buf.append(":");
							buf.append(token);
							j++;
						}
						buf.append(")");
					} else {
						buf.append(fieldName);
						buf.append(":");
						buf.append("\"" + ClientUtils.escapeQueryChars(v) + "\"");
					}
					i++;
				}
			}
			buf.append(postOperator);
		}
		else{
			ComposedClause c = (ComposedClause)clause ;
			int i =0;
			buf.append("(");
			for (Clause c1 : c.getClauses()) {
				if(i>0)
					buf.append(" "+c.getInnerOperator().getOperator()+" ");
				makeClause(buf, c1, profileName);
				i++;
			}
			buf.append(")");
		}
	}

	public List<String> getCenturies(String value, boolean next){
		String[] centuries = new String[]{"I","II","III","IV","V","VI","VII","VIII","IX","X",
				"XI","XII","XIII","XIV","XV","XVI","XVII","XVIII","XIX","XX","XXI"};
		List<String> rets = new ArrayList<>();
		boolean add = !next;
		for (int i = 0; i < centuries.length; i++) {
			if(add)
				rets.add(centuries[i]);
			if(centuries[i].equalsIgnoreCase(value)) {
				if(next)
					rets.add(centuries[i]);
				add = next;
			}
		}
		return rets;
	}

	protected void makeCenturyPart(String fieldName, StringBuffer buf, String value, boolean next){
		List<String> vals = getCenturies(value, next);
		int pos = 0;
		for(String val: vals) {
			if(pos>0)
				buf.append(" OR ");
			pos++;
			buf.append(fieldName);
			buf.append(":");
			buf.append(val);
		}

	}

	protected String createRangeQueryPart(String fieldName,String rangeEnd,List<String> values, String type){
		String ret = "";
		if(rangeEnd==null)
			ret = fieldName+":[" + ClientUtils.escapeQueryChars(values.get(0)) + " TO " + ClientUtils.escapeQueryChars(values.get(1)) + "]";
		else{
			boolean isCentury = type!=null && type.equals("century");
			StringBuffer buf = new StringBuffer();
			if(values.get(0).trim().length()>0) {
				if (isCentury) {
					buf.append("(");
					makeCenturyPart(fieldName, buf, values.get(0), true);
					buf.append(")");
				} else {
					buf.append(fieldName);
					buf.append(":");
					buf.append("[" + ClientUtils.escapeQueryChars(values.get(0)) + " TO *] ");
				}
			}
			if(values.get(0).trim().length()>0 && values.get(1).trim().length()>0)
				buf.append(" AND (");

			if(values.get(1).trim().length()>0) {
				String fieldName2 = rangeEnd;
				if (isCentury) {
					buf.append("(");
					makeCenturyPart(fieldName2, buf, values.get(1), false);
					buf.append(")");
				} else {
					buf.append(fieldName2);
					buf.append(":");
					buf.append("[* TO " + ClientUtils.escapeQueryChars(values.get(1)) + "] ");
				}
				buf.append(" OR (-" + fieldName2 + ":* AND ");
				if (isCentury) {
					buf.append("(");
					makeCenturyPart(fieldName, buf, values.get(1), false);
					buf.append("))");
				} else
					buf.append(fieldName + ":[* TO " +
							ClientUtils.escapeQueryChars(values.get(1)) + "])");
			}
			if(values.get(0).trim().length()>0 && values.get(1).trim().length()>0)
				buf.append(")");
			ret = buf.toString();
		}
		return ret;
	}
}
