package com.gruppometa.unimarc.mapping;

import java.util.List;

public class MappingDefinition {
	protected String facet=null;
	protected String parent=null;

	public String getJoin() {
		return join;
	}

	public void setJoin(String join) {
		this.join = join;
	}

	protected String parentLabel=null;
	protected String feLabel;
	protected String join;

	public String getFeLabel() {
		return feLabel;
	}

	public void setFeLabel(String feLabel) {
		this.feLabel = feLabel;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getParentLabel() {
		return parentLabel;
	}

	public void setParentLabel(String parentLabel) {
		this.parentLabel = parentLabel;
	}

	protected boolean is4Fe=false;

	public boolean isDocAttribute() {
		return docAttribute;
	}

	public void setDocAttribute(boolean docAttribute) {
		this.docAttribute = docAttribute;
	}

	protected boolean docAttribute;

	public boolean isSortField() {
		return sortField;
	}

	public void setSortField(boolean sortField) {
		this.sortField = sortField;
	}

	protected boolean sortField= false;
	protected String sortFieldName;

	public String getSortFieldName() {
		return sortFieldName;
	}

	public void setSortFieldName(String sortFieldName) {
		this.sortFieldName = sortFieldName;
	}

	public boolean isExcludeFromSearchField() {
		return excludeFromSearchField;
	}

	public void setExcludeFromSearchField(boolean excludeFromSearchField) {
		this.excludeFromSearchField = excludeFromSearchField;
	}

	protected boolean excludeFromSearchField=false;

	public boolean isCutZeros() {
		return cutZeros;
	}

	public void setCutZeros(boolean cutZeros) {
		this.cutZeros = cutZeros;
	}

	protected boolean cutZeros = false;

	public boolean isSubfield() {
		return subfield;
	}

	public void setSubfield(boolean subfield) {
		this.subfield = subfield;
	}

	protected boolean subfield;

	public boolean isExcludeInFe() {
		return excludeInFe;
	}

	public void setExcludeInFe(boolean excludeInFe) {
		this.excludeInFe = excludeInFe;
	}

	protected boolean excludeInFe = false;
	protected String searchType;

	public String getSearchType() {
		return searchType;
	}

	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	public boolean isIs4Fe() {
		return is4Fe;
	}
	public void setIs4Fe(boolean is4Fe) {
		this.is4Fe = is4Fe;
	}
	protected String copyTo=null;
	public String getCopyTo() {
		return copyTo;
	}
	public void setCopyTo(String copyTo) {
		this.copyTo = copyTo;
	}
	protected boolean facets=false;
	protected boolean hideLabel=false;
	protected String type;

	public boolean isHideLabel() {
		return hideLabel;
	}

	public void setHideLabel(boolean hideLabel) {
		this.hideLabel = hideLabel;
	}

	protected String solrFieldname;
	protected boolean fulltext = true;
	public boolean isFulltext() {
		return fulltext;
	}
	public void setFulltext(boolean fulltext) {
		this.fulltext = fulltext;
	}
	public String getSolrFieldname() {
		return solrFieldname;
	}
	public void setSolrFieldname(String solrFieldname) {
		this.solrFieldname = solrFieldname;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFacet() {
		return facet;
	}
	public void setFacet(String facet) {
		this.facet = facet;
	}
	protected boolean multiple=false;
	public boolean isMultiple() {
		return multiple;
	}
	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}
	public boolean isFacets() {
		return facets;
	}
	public void setFacets(boolean facets) {
		this.facets = facets;
	}
	protected String inverse;
	public String getInverse() {
		return inverse;
	}
	public void setInverse(String inverse) {
		this.inverse = inverse;
	}
	protected String separator;
	protected String labelGroup;
	protected String listType;

	public String getListType() {
		return listType;
	}

	public void setListType(String listType) {
		this.listType = listType;
	}

	public String getLabelGroup() {
		return labelGroup;
	}
	public String getSeparator() {
		return separator;
	}
	public void setSeparator(String separator) {
		this.separator = separator;
	}
	public void setLabelGroup(String labelGroup) {
		this.labelGroup = labelGroup;
	}
	protected List<MappingDefinition> subDefs;
	public List<MappingDefinition> getSubDefs() {
		return subDefs;
	}
	public void setSubDefs(List<MappingDefinition> subDefs) {
		this.subDefs = subDefs;
	}
	protected String marcField;
	protected String[] marcSections;
	protected String destination;
	protected String qualifier;
	protected String sourceType;
	protected String vocabulary;
	protected String group;
	protected String label;
	protected String rangeEnd;
	protected String condMarcSection;
    protected String ind;

	public String getFacetLabel() {
		return facetLabel;
	}

	public void setFacetLabel(String facetLabel) {
		this.facetLabel = facetLabel;
	}

	protected String facetLabel;

    public String getInd() {
        return ind;
    }

    public void setInd(String ind) {
        this.ind = ind;
    }

    public String getCondMarcSection() {
		return condMarcSection;
	}

	public void setCondMarcSection(String condMarcSection) {
		this.condMarcSection = condMarcSection;
	}

	protected String condValue;

	public String getCondValue2() {
		return condValue2;
	}

	public void setCondValue2(String condValue2) {
		this.condValue2 = condValue2;
	}

	protected String condValue2;

	public String getCondValue() {
		return condValue;
	}

	public void setCondValue(String condValue) {
		this.condValue = condValue;
	}

	public void setCaseSensetive(boolean caseSensetive) {
		this.caseSensetive = caseSensetive;
	}

	public String getRangeEnd() {
		return rangeEnd;
	}

	public void setRangeEnd(String rangeEnd) {
		this.rangeEnd = rangeEnd;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	protected boolean searchField;

	public boolean isSearchField() {
		return searchField;
	}

	public void setSearchField(boolean searchField) {
		this.searchField = searchField;
	}

	protected String group2;
	public String getGroup2() {
		return group2;
	}
	public void setGroup2(String group2) {
		this.group2 = group2;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	protected String handler;
	public String getHandler() {
		return handler;
	}
	public void setHandler(String handler) {
		this.handler = handler;
	}
	protected double vistaEtichette = -1;
	protected double vistaIsbd = -1;
	protected double vistaShort = -1;
	protected double order = -1;
	protected double facetOrder = -1;
	
	public double getFacetOrder() {
		return facetOrder;
	}
	public void setFacetOrder(double facetOrder) {
		this.facetOrder = facetOrder;
	}
	public double getVistaShort() {
		return vistaShort;
	}
	public void setVistaShort(double vistaShort) {
		this.vistaShort = vistaShort;
	}
	public double getOrder() {
		return order;
	}
	public void setOrder(double order) {
		this.order = order;
	}
	public String getVocabulary() {
		return vocabulary;
	}
	public double getVistaEtichette() {
		return vistaEtichette;
	}
	public void setVistaEtichette(double vistaEtichette) {
		this.vistaEtichette = vistaEtichette;
	}
	public double getVistaIsbd() {
		return vistaIsbd;
	}
	public void setVistaIsbd(double vistaIsbd) {
		this.vistaIsbd = vistaIsbd;
	}
	public void setVocabulary(String vocabulary) {
		this.vocabulary = vocabulary;
	}
	protected int posInit=-1;
	protected int posEnd=-1;
	public int getPosInit() {
		return posInit;
	}
	public void setPosInit(int posInit) {
		this.posInit = posInit;
	}
	public int getPosEnd() {
		return posEnd;
	}
	public void setPosEnd(int posEnd) {
		this.posEnd = posEnd;
	}
	protected boolean caseSensetive = true;
	protected boolean ifFirst = false;
	protected boolean unique = false;	
	protected boolean onlyFirst = false;	
	protected String constantValue = null;
	/**
	 * @return the ifFirst
	 */
	public boolean isIfFirst() {
		return ifFirst;
	}
	/**
	 * @param ifFirst the ifFirst to set
	 */
	public void setIfFirst(boolean ifFirst) {
		this.ifFirst = ifFirst;
	}
	public String getSourceType() {
		return sourceType;
	}
	public boolean isCaseSensetive(){
		return caseSensetive;
	}
	public void setCaseSentive(boolean caseSensetive){
		this.caseSensetive = caseSensetive;
	}
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}
	public String getQualifier() {
		return qualifier;
	}
	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}
	public String getMarcField() {
		return marcField;
	}
	public MappingDefinition(String marcField, String sourceType,
			String destination, String qualifier) {
		super();
		this.marcField = marcField;
		this.sourceType = sourceType;
		this.destination = destination;
		this.qualifier = qualifier;
	}
	public MappingDefinition(String marcField, String sourceType,
			String destination, String qualifier, boolean caseSensetive) {
		super();
		this.marcField = marcField;
		this.sourceType = sourceType;
		this.destination = destination;
		this.qualifier = qualifier;
		this.caseSensetive = caseSensetive;
	}
	public MappingDefinition(String marcField, String sourceType,
			String destination, String qualifier, boolean caseSensetive,boolean ifFirst) {
		super();
		this.marcField = marcField;
		this.sourceType = sourceType;
		this.destination = destination;
		this.qualifier = qualifier;
		this.caseSensetive = caseSensetive;
		this.ifFirst = ifFirst;
	}
	public MappingDefinition(String marcField) {
		super();
		this.marcField = marcField;
		this.destination = marcField;
	}
	public MappingDefinition(String marcField, String[] marcSections,
			String destination, String qualifier) {
		super();
		this.marcField = marcField;
		this.marcSections = marcSections;
		this.destination = destination;
		this.qualifier = qualifier;
	}
	public MappingDefinition(String marcField, String[] marcSections,
			String destination, String qualifier,boolean caseSensetive,boolean ifFirst) {
		super();
		this.marcField = marcField;
		this.marcSections = marcSections;
		this.destination = destination;
		this.qualifier = qualifier;
		this.caseSensetive = caseSensetive;
		this.ifFirst = ifFirst;
	}
	public void setMarcField(String marcField) {
		this.marcField = marcField;
	}
	public String[] getMarcSections() {
		return marcSections;
	}
	public void setMarcSections(String[] marcSections) {
		this.marcSections = marcSections;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public MappingDefinition(String marcField, String[] marcSections,
			String destination) {
		super();
		this.marcField = marcField;
		this.marcSections = marcSections;
		this.destination = destination;
	}
	public boolean isUnique() {
		return unique;
	}
	/**
	 * @param unique the unique to set
	 */
	public void setUnique(boolean unique) {
		this.unique = unique;
	}
	public void setOnlyFirst(boolean b) {
		this.onlyFirst = b;
	}
	public boolean isOnlyFirst(){
		return onlyFirst;
	}
	/**
	 * @return the constantValue
	 */
	public String getConstantValue() {
		return constantValue;
	}
	/**
	 * @param constantValue the constantValue to set
	 */
	public void setConstantValue(String constantValue) {
		this.constantValue = constantValue;
	}
	
}
