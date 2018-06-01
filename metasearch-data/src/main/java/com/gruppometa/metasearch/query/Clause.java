package com.gruppometa.metasearch.query;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
	    use = JsonTypeInfo.Id.NAME,
	    include = JsonTypeInfo.As.PROPERTY,
	    property = "type")
	@JsonSubTypes({
	    @Type(value = ComposedClause.class, name = "CompostedClause"),
	    @Type(value = SimpleClause.class, name = "SimpleClause")
	}
	    )
public interface Clause {
}
