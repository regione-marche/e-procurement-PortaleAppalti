/*
*
* Copyright 2005 AgileTec s.r.l. (http://www.agiletec.it) All rights reserved.
*
* This file is part of jAPS software.
* jAPS is a free software; 
* you can redistribute it and/or modify it
* under the terms of the GNU General Public License (GPL) as published by the Free Software Foundation; version 2.
* 
* See the file License for the specific language governing permissions   
* and limitations under the License
* 
* 
* 
* Copyright 2005 AgileTec s.r.l. (http://www.agiletec.it) All rights reserved.
*
*/
package com.agiletec.aps.system.common.entity;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.agiletec.aps.system.common.entity.model.ApsEntityRecord;
import com.agiletec.aps.system.common.entity.model.EntitySearchFilter;
import com.agiletec.aps.system.services.AbstractDAO;

/**
 * Abstract class extended by those DAO that perform searches on entities.
 * @author E.Santoboni
 */
public abstract class AbstractEntitySearcherDAO extends AbstractDAO implements IEntitySearcherDAO {
	
	@Override
	public List<ApsEntityRecord> searchRecords(EntitySearchFilter[] filters) {
		Connection conn = null;
		List<ApsEntityRecord> records = new ArrayList<ApsEntityRecord>();
		PreparedStatement stat = null;
		ResultSet result = null;
		try {
			conn = this.getConnection();
			stat = this.buildStatement(filters, true, conn);
			result = stat.executeQuery();
			this.flowRecordsResult(records, filters, result);
		} catch (Throwable t) {
			processDaoException(t, "Error while loading records list", "searchRecord");
		} finally {
			closeDaoResources(result, stat, conn);
		}
		return records;
	}
	
	protected void flowRecordsResult(List<ApsEntityRecord> records, EntitySearchFilter[] filters, ResultSet result) throws Throwable {
		List<EntitySearchFilter> likeFieldFilters = this.getLikeFieldFilters(filters);
		while (result.next()) {
			ApsEntityRecord record = this.createRecord(result);
			if (!records.contains(record)) {//TODO DA ANALIZZARE
				if (likeFieldFilters.isEmpty()) {
					records.add(record);
				} else {
					boolean verify = this.verifyLikeFieldFilters(result, likeFieldFilters);
					if (verify) records.add(record);
				}
			}
		}
	}
	
	protected abstract ApsEntityRecord createRecord(ResultSet result) throws Throwable;
	
	@Override
	public List<String> searchId(String typeCode, EntitySearchFilter[] filters) {
		if (typeCode != null && typeCode.trim().length()>0) {
			EntitySearchFilter filter = new EntitySearchFilter(IEntityManager.ENTITY_TYPE_CODE_FILTER_KEY, false, typeCode, false);
			EntitySearchFilter[] newFilters = this.addFilter(filters, filter);
			return this.searchId(newFilters);
		}
		return this.searchId(filters);
	}
	
	@Override
	public List<String> searchId(EntitySearchFilter[] filters) {
		Connection conn = null;
		List<String> idList = new ArrayList<String>();
		PreparedStatement stat = null;
		ResultSet result = null;
		try {
			conn = this.getConnection();
			stat = this.buildStatement(filters, false, conn);
			result = stat.executeQuery();
			this.flowResult(idList, filters, result);
		} catch (Throwable t) {
			processDaoException(t, "Error while loading the list of IDs", "searchId");
		} finally {
			closeDaoResources(result, stat, conn);
		}
		return idList;
	}
	
	protected EntitySearchFilter[] addFilter(EntitySearchFilter[] filters, EntitySearchFilter filterToAdd){
		int len = 0;
		if (filters != null) {
			len = filters.length;
		}
		EntitySearchFilter[] newFilters = new EntitySearchFilter[len + 1];
		for(int i=0; i < len; i++){
			newFilters[i] = filters[i];
		}
		newFilters[len] = filterToAdd;
		return newFilters;
	}
	
	protected void flowResult(List<String> contentsId, EntitySearchFilter[] filters, ResultSet result) throws SQLException {
		List<EntitySearchFilter> likeFieldFilters = this.getLikeFieldFilters(filters);
		while (result.next()) {
			String id = result.getString(this.getEntityMasterTableIdFieldName());
			if (contentsId.contains(id)) continue;//TODO DA ANALIZARE
			if (likeFieldFilters.isEmpty()) {
				contentsId.add(id);
			} else {
				boolean verify = this.verifyLikeFieldFilters(result, likeFieldFilters);
				if (verify) contentsId.add(id);
			}
		}
	}
	
	protected boolean verifyLikeFieldFilters(ResultSet result,
			List<EntitySearchFilter> likeFieldFilters) throws SQLException {
		boolean verify = true;
		for (int i=0; i<likeFieldFilters.size(); i++) {
			EntitySearchFilter likeFilter = likeFieldFilters.get(i);
			String fieldName = this.getTableFieldName(likeFilter.getKey());
			String value = result.getString(fieldName);
			verify = this.checkText((String)likeFilter.getValue(), value);
			if (!verify) break;
		}
		return verify;
	}
	
	protected List<EntitySearchFilter> getLikeFieldFilters(EntitySearchFilter[] filters) {
		List<EntitySearchFilter> likeFieldFilters = new ArrayList<EntitySearchFilter>();
		if (filters == null) return likeFieldFilters;
		for (int i=0; i<filters.length; i++) {
			EntitySearchFilter filter = filters[i];
			if (filter.getKey() != null && !filter.isAttributeFilter() && filter.isLikeOption()) {
				likeFieldFilters.add(filter);
			}
		}
		return likeFieldFilters;
	}
	
	/**
	 * This utility method checks if the given Text matches or is contained inside
	 * another one.
	 * @param insertedText The text to look for
	 * @param text The text to search in
	 * @return True if an occurrence of 'insertedText' is found in 'text'.
	 */
	private boolean checkText(String insertedText, String text) {
		if (insertedText.trim().length() == 0 || 
				text.toLowerCase().indexOf(insertedText.trim().toLowerCase()) != -1) {
			return true;
		}
		return false;
	}
	
	private PreparedStatement buildStatement(EntitySearchFilter[] filters, boolean selectAll, Connection conn) {
		String query = this.createQueryString(filters, selectAll);
		PreparedStatement stat = null;
		try {
			stat = conn.prepareStatement(query);
			int index = 0;
			index = this.addAttributeFilterStatementBlock(filters, index, stat);
			index = this.addMetadataFieldFilterStatementBlock(filters, index, stat);
		} catch (Throwable t) {
			processDaoException(t, "Error while creating the statement", "buildStatement");
		}
		return stat;
	}
	
	/**
	 * Add to the statement the filters on the entity metadata.
	 * @param filters the filters to add to the statement.
	 * @param index The current index of the statement.
	 * @param stat The statement.
	 * @return The current statement index, eventually incremented because of the filters number.
	 * L'indice corrente dello statement eventualmente incrementato per il set dei filtri. // FIXME
	 * @throws Throwable In case of error.
	 */
	protected int addMetadataFieldFilterStatementBlock(EntitySearchFilter[] filters, int index, PreparedStatement stat) throws Throwable {
		if (filters == null) return index;
		for (int i=0; i<filters.length; i++) {
			EntitySearchFilter filter = filters[i];
			if (filter.getKey() != null && !filter.isAttributeFilter() && !filter.isLikeOption()) {
				index = this.addObjectSearchStatementBlock(filter, index, stat);
			}
		}
		return index;
	}
	
	/**
	 * Add the attribute filters to the statement.
	 * @param filters The filters on the entity filters to insert in the statement.
	 * @param index The last index used to associate the elements to the statement.
	 * @param stat The statement where the filters are applied.
	 * @return The last used index.
	 * @throws SQLException In case of error.
	 */
	protected int addAttributeFilterStatementBlock(EntitySearchFilter[] filters,
			int index, PreparedStatement stat) throws SQLException {
		if (filters == null) return index;
		for (int i=0; i<filters.length; i++) {
			EntitySearchFilter filter = filters[i];
			if (filter.getKey() != null && filter.isAttributeFilter()) {
				stat.setString(++index, filter.getKey());
				index = this.addObjectSearchStatementBlock(filter, index, stat);
			}
		}
		return index;
	}
	
	/**
	 * Add to the statement a filter on a attribute.
	 * @param filter The filter on the attribute to apply in the statement.
	 * @param index The last index used to associate the elements to the statement.
	 * @param stat The statement where the filters are applied.
	 * @return The last used index.
	 * @throws SQLException In case of error.
	 */
	private int addObjectSearchStatementBlock(EntitySearchFilter filter, int index, PreparedStatement stat) throws SQLException {
		if (filter.getValue() != null) {
			this.addObjectSearchStatementBlock(stat, ++index, filter.getValue(), filter.isLikeOption());
		} else {
			if (null != filter.getStart()) {
				this.addObjectSearchStatementBlock(stat, ++index, filter.getStart(), false);
			}
			if (null != filter.getEnd()) {
				this.addObjectSearchStatementBlock(stat, ++index, filter.getEnd(), false);
			}
		}
		if (filter.isAttributeFilter() && null != filter.getLangCode()) {
			stat.setString(++index, filter.getLangCode());
		}
		return index;
	}
	
	private void addObjectSearchStatementBlock(PreparedStatement stat, int index,
			Object object, boolean isLikeOption) throws SQLException {
		if (object instanceof String) {
			if (isLikeOption) {
				stat.setString(index, "%"+((String) object)+"%");
			} else {
				stat.setString(index, (String) object);
			}
		} else if (object instanceof Date) {
			stat.setDate(index, new java.sql.Date(((Date) object).getTime()));
		} else if (object instanceof BigDecimal) {
			stat.setBigDecimal(index, (BigDecimal) object);
		} else if (object instanceof Boolean) {
			stat.setString(index, ((Boolean) object).toString());
		}
	}
	
	private String createQueryString(EntitySearchFilter[] filters, boolean selectAll) {
		StringBuffer query = this.createBaseQueryBlock(filters, selectAll);
		boolean hasAppendWhereClause = this.appendFullAttributeFilterQueryBlocks(filters, query, false);
		this.appendMetadataFieldFilterQueryBlocks(filters, query, hasAppendWhereClause);
		
		boolean ordered = appendOrderQueryBlocks(filters, query, false);
		return query.toString();
	}
	
	/**
	 * Create the 'base block' of the query with the eventual references to the support table.
	 * @param filters The filters defined.
	 * @param selectAll When true, this will insert all the fields in the master table in the select 
	 * of the master query.
	 * When true we select all the available fields; when false only the field addressed by the filter
	 * is selected.
	 * @return The base block of the query.
	 */
	protected StringBuffer createBaseQueryBlock(EntitySearchFilter[] filters, boolean selectAll) {
		StringBuffer query = this.createMasterSelectQueryBlock(filters, selectAll);
		this.appendJoinSerchTableQueryBlock(filters, query);
		return query;
	}
	
	private StringBuffer createMasterSelectQueryBlock(EntitySearchFilter[] filters, boolean selectAll) {
		String masterTableName = this.getEntityMasterTableName();
		StringBuffer query = new StringBuffer("SELECT ").append(masterTableName).append(".");
		if (selectAll) {
			query.append("* ");
		} else {
			query.append(this.getEntityMasterTableIdFieldName());
			if (filters != null) {
				for (int i=0; i<filters.length; i++) {
					if (!filters[i].isAttributeFilter() && filters[i].isLikeOption()) {
						query.append(", ").append(masterTableName).append(".").append(this.getTableFieldName(filters[i].getKey()));
					}
				}
			}
		}
		query.append(" FROM ").append(masterTableName).append(" ");
		return query;
	}
	
	private void appendJoinSerchTableQueryBlock(EntitySearchFilter[] filters, StringBuffer query) {
		if (filters == null) return;
		String masterTableName = this.getEntityMasterTableName();
		String masterTableIdFieldName = this.getEntityMasterTableIdFieldName();
		String searchTableName = this.getEntitySearchTableName();
		String searchTableIdFieldName = this.getEntitySearchTableIdFieldName();
		for (int i=0; i<filters.length; i++) {
			if (null != filters[i].getKey() && filters[i].isAttributeFilter()) {
				query.append("INNER JOIN ");
				query.append(searchTableName).append(" ").append(searchTableName).append(i).append(" ON ")
					.append(masterTableName).append(".").append(masterTableIdFieldName).append(" = ")
					.append(searchTableName).append(i).append(".").append(searchTableIdFieldName).append(" ");
			}
		}
	}
	
	protected boolean appendFullAttributeFilterQueryBlocks(EntitySearchFilter[] filters, StringBuffer query, boolean hasAppendWhereClause) {
		if (filters != null) {
			for (int i=0; i<filters.length; i++) {
				EntitySearchFilter filter = filters[i];
				String searchTableNameAlias = this.getEntitySearchTableName()+i;
				if (filter.getKey() != null && filter.isAttributeFilter()) {
					hasAppendWhereClause = this.verifyWhereClauseAppend(query, hasAppendWhereClause);
					query.append(searchTableNameAlias).append(".attrname = ? ");
					if (filter.getValue() != null) {
						Object object = filter.getValue();
						String operator = null;
						if (filter.isLikeOption()) {
							operator = "LIKE ? ";
						} else {
							operator = "= ? ";
						}
						hasAppendWhereClause = this.addAttributeObjectSearchQueryBlock(searchTableNameAlias, query, 
								object, operator, hasAppendWhereClause, filter.getLangCode());
						hasAppendWhereClause = this.addAttributeLangQueryBlock(searchTableNameAlias, query, 
								filter, hasAppendWhereClause);
					} else {
						//creazione blocco selezione su tabella ricerca
						if (null != filter.getStart()) {
							hasAppendWhereClause = this.addAttributeObjectSearchQueryBlock(searchTableNameAlias, query, 
									filter.getStart(), ">= ? ", hasAppendWhereClause, filter.getLangCode());
						}
						if (null != filter.getEnd()) {
							hasAppendWhereClause = this.addAttributeObjectSearchQueryBlock(searchTableNameAlias, query, 
									filter.getEnd(), "<= ? ", hasAppendWhereClause, filter.getLangCode());
						}
						hasAppendWhereClause = this.addAttributeLangQueryBlock(searchTableNameAlias, query, filter, hasAppendWhereClause);
						if (null == filter.getStart() && null == filter.getEnd()) {
							hasAppendWhereClause = this.verifyWhereClauseAppend(query, hasAppendWhereClause);
							query.append(" (").append(searchTableNameAlias).append(".datevalue IS NOT NULL OR ").append(searchTableNameAlias).append(".textvalue IS NOT NULL OR ").append(searchTableNameAlias).append(".numvalue IS NOT NULL) ");
						}
					}
				}
			}
		}
		return hasAppendWhereClause;
	}
	
	protected boolean addAttributeLangQueryBlock(String searchTableNameAlias, StringBuffer query, 
			EntitySearchFilter filter, boolean hasAppendWhereClause) {
		if (null != filter.getLangCode()) {
			hasAppendWhereClause = this.verifyWhereClauseAppend(query, hasAppendWhereClause);
			query.append(searchTableNameAlias).append(".langcode = ? ");
		}
		return hasAppendWhereClause;
	}
	
	protected boolean appendMetadataFieldFilterQueryBlocks(EntitySearchFilter[] filters, StringBuffer query, boolean hasAppendWhereClause) {
		if (filters == null) return hasAppendWhereClause;
		for (int i=0; i<filters.length; i++) {
			EntitySearchFilter filter = filters[i];
			if (filter.getKey() != null && !filter.isAttributeFilter() && !filter.isLikeOption()) {
				hasAppendWhereClause = this.addMetadataFieldFilterQueryBlock(filter, query, hasAppendWhereClause);
			}
		}
		return hasAppendWhereClause;
	}
	
	private boolean addMetadataFieldFilterQueryBlock(EntitySearchFilter filter, StringBuffer query, boolean hasAppendWhereClause) {
		hasAppendWhereClause = this.verifyWhereClauseAppend(query, hasAppendWhereClause);
		String tableFieldName = this.getTableFieldName(filter.getKey());
		query.append(this.getEntityMasterTableName()).append(".").append(tableFieldName).append(" ");
		if (filter.getValue() != null) {
			if (filter.isLikeOption()) {
				query.append("LIKE ? ");
			} else {
				query.append("= ? ");
			}
		} else {
			if (null != filter.getStart()) {
				query.append(">= ? ");
				if (null != filter.getEnd()) {
					query.append("AND ").append(this.getEntityMasterTableName()).append(".").append(tableFieldName).append(" ");
					query.append("<= ? ");
				}
			} else if (null != filter.getEnd()) {
				query.append("<= ? ");
			} else {
				if (filter.isNullOption()) {
					query.append(" IS NULL ");
				} else {
					query.append(" IS NOT NULL ");
				}
			}
		}
		return hasAppendWhereClause;
	}
	
	public abstract String getTableFieldName(String metadataFieldKey);
	
	protected boolean appendOrderQueryBlocks(EntitySearchFilter[] filters, StringBuffer query, boolean ordered) {
		if (filters == null) return ordered;
		for (int i=0; i<filters.length; i++) {
			EntitySearchFilter filter = filters[i];
			if (null != filter.getKey() && null != filter.getOrder()) {
				if (!ordered) {
					query.append("ORDER BY ");
					ordered = true;
				} else {
					query.append(", ");
				}
				if (filter.isAttributeFilter()) {
					String tableName = this.getEntitySearchTableName() + i;
					this.addAttributeOrderQueryBlock(tableName, query, filter, filter.getOrder());
				} else {
					String fieldName = this.getTableFieldName(filter.getKey());
					query.append(this.getEntityMasterTableName()).append(".").append(fieldName).append(" ").append(filter.getOrder());
				}
			}
		}
		return ordered;
	}
	
	protected boolean addAttributeObjectSearchQueryBlock(String searchTableNameAlias, 
			StringBuffer query, Object object, String operator, boolean hasAppendWhereClause, String langCode) {
		hasAppendWhereClause = this.verifyWhereClauseAppend(query, hasAppendWhereClause);
		query.append(searchTableNameAlias);
		if (object instanceof String) {
			query.append(".textvalue ");
		} else if (object instanceof Date) {
			query.append(".datevalue ");
		} else if (object instanceof BigDecimal) {
			query.append(".numvalue ");
		} else if (object instanceof Boolean) {
			query.append(".textvalue ");
		}
		query.append(operator);
		return hasAppendWhereClause;
	}
	
	protected boolean verifyWhereClauseAppend(StringBuffer query, boolean hasAppendWhereClause) {
		if (hasAppendWhereClause) {
			query.append("AND ");
		} else {
			query.append("WHERE ");
			hasAppendWhereClause = true;
		}
		return hasAppendWhereClause;
	}
	
	private void addAttributeOrderQueryBlock(String searchTableNameAlias, StringBuffer query, EntitySearchFilter filter, String order) {
		if (order == null) order = "";
		Object object = filter.getValue();
		if (object == null) object = filter.getStart();
		if (object == null) object = filter.getEnd();
		if (null == object) {
			query.append(searchTableNameAlias).append(".textvalue ").append(order).append(", ")
				.append(searchTableNameAlias).append(".datevalue ").append(order).append(", ")
				.append(searchTableNameAlias).append(".numvalue ").append(order);
			return;
		}
		if (object instanceof String) {
			query.append(searchTableNameAlias).append(".textvalue ");
		} else if (object instanceof Date) {
			query.append(searchTableNameAlias).append(".datevalue ");
		} else if (object instanceof BigDecimal) {
			query.append(searchTableNameAlias).append(".numvalue ");
		} else if (object instanceof Boolean) {
			query.append(searchTableNameAlias).append(".textvalue ");
		}
		query.append(order);
	}
	
	/**
	 * Return the name of the entities master table.
	 * @return The name of the master table.
	 */
	public abstract String getEntityMasterTableName();
	
	/**
	 * Return the name of the "entity ID" field in the master entity table.
	 * @return The name of the "entity ID" field.
	 */
	public abstract String getEntityMasterTableIdFieldName();
	
	/**
	 * Return the name of the "Entity Type code" in the master entity table.
	 * @return The name of the "Entity Type code".
	 */
	public abstract String getEntityMasterTableIdTypeFieldName();
	
	/**
	 * Return the name of the support table used to perform search on entities.
	 * @return The name of the support table.
	 */
	public abstract String getEntitySearchTableName();
	
	/**
	 * Return the name of the "Entity ID" in the support table used to perform search on entities.
	 * @return The name of "Entity ID" field.
	 */
	public abstract String getEntitySearchTableIdFieldName();
	
}
