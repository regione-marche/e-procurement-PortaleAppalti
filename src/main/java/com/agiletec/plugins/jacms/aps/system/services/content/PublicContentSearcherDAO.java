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
package com.agiletec.plugins.jacms.aps.system.services.content;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.agiletec.aps.system.common.entity.model.EntitySearchFilter;
import com.agiletec.aps.system.services.group.Group;

/**
 * @author E.Santoboni
 */
public class PublicContentSearcherDAO extends AbstractContentSearcherDAO implements IPublicContentSearcherDAO {
	
	@Override
	public List<String> loadPublicContentsId(String contentType, String[] categories, EntitySearchFilter[] filters, Collection<String> userGroupCodes) {
		if (contentType != null && contentType.trim().length()>0) {
			EntitySearchFilter typeFilter = new EntitySearchFilter(IContentManager.ENTITY_TYPE_CODE_FILTER_KEY, false, contentType, false);
			filters = this.addFilter(filters, typeFilter);
		}
		return this.loadPublicContentsId(categories, filters, userGroupCodes);
	}
	
	@Override
	public List<String> loadPublicContentsId(String[] categories,
			EntitySearchFilter[] filters, Collection<String> userGroupCodes) {
		Set<String> groupCodes = new HashSet<String>();
		if (null != userGroupCodes) groupCodes.addAll(userGroupCodes);
		groupCodes.add(Group.FREE_GROUP_NAME);
		EntitySearchFilter onLineFilter = new EntitySearchFilter(IContentManager.CONTENT_ONLINE_FILTER_KEY, false);
		filters = this.addFilter(filters, onLineFilter);
		
		List<String> contentsId = new ArrayList<String>();
		Connection conn = null;
		PreparedStatement stat = null;
		ResultSet result = null;
		try {
			conn = this.getConnection();
			stat = this.buildStatement(filters, categories, groupCodes, false, conn);
			result = stat.executeQuery();
			this.flowResult(contentsId, filters, result);
		} catch (Throwable t) {
			processDaoException(t, "Errore in caricamento lista id contenuti", "loadContentsId");
		} finally {
			closeDaoResources(result, stat, conn);
		}
		return contentsId;
	}
	
	protected PreparedStatement buildStatement(EntitySearchFilter[] filters, String[] categories, 
			Collection<String> userGroupCodes, boolean selectAll, Connection conn) {
		Collection<String> groupsForSelect = this.getGroupsForSelect(userGroupCodes);
		String query = this.createQueryString(filters, categories, groupsForSelect, selectAll);
		//System.out.println("QUERY : " + query);
		PreparedStatement stat = null;
		try {
			stat = conn.prepareStatement(query);
			int index = 0;
			index = super.addAttributeFilterStatementBlock(filters, index, stat);
			index = this.addMetadataFieldFilterStatementBlock(filters, index, stat);
			if (groupsForSelect != null) {
				index = this.addGroupStatementBlock(groupsForSelect, index, stat);
			}
			if (categories != null) {
				for (int i=0; i<categories.length; i++) {
					stat.setString(++index, categories[i]);
				}
			}
		} catch (Throwable t) {
			processDaoException(t, "Errore in fase di creazione statement", "buildStatement");
		}
		return stat;
	}
	
	protected String createQueryString(EntitySearchFilter[] filters, String[] categories, 
			Collection<String> groupsForSelect, boolean selectAll) {
		StringBuffer query = this.createBaseQueryBlock(filters, selectAll);
		
		boolean hasAppendWhereClause = this.appendFullAttributeFilterQueryBlocks(filters, query, false);
		hasAppendWhereClause = this.appendMetadataFieldFilterQueryBlocks(filters, query, hasAppendWhereClause);
		
		if (groupsForSelect != null && !groupsForSelect.isEmpty()) {
			hasAppendWhereClause = this.verifyWhereClauseAppend(query, hasAppendWhereClause);
			this.addGroupsQueryBlock(query, groupsForSelect);
		}
		if (categories != null) {
			hasAppendWhereClause = this.verifyWhereClauseAppend(query, hasAppendWhereClause);
			for (int i=0; i<categories.length; i++) {
				if (i>0) query.append(" AND ");
				query.append(" contents.contentid IN (SELECT contentid FROM ")
					.append(this.getContentRelationsTableName()).append(" WHERE ")
					.append(this.getContentRelationsTableName()).append(".refcategory = ? ) ");
			}
		}
		
		boolean ordered = appendOrderQueryBlocks(filters, query, false);
		//System.out.println("********** " + query.toString());
		return query.toString();
	}
	
	@Override
	protected void addGroupsQueryBlock(StringBuffer query,
			Collection<String> userGroupCodes) {
		query.append(" ( ");
		int size = userGroupCodes.size();
		for (int i=0; i<size; i++) {
			if (i!=0) query.append("OR ");
			query.append("contents.maingroup = ? ");
		}
		query.append(" OR contents.contentid IN ( SELECT contentid FROM ")
				.append(this.getContentRelationsTableName()).append(" WHERE ");
		for (int i=0; i<size; i++) {
			if (i!=0) query.append("OR ");
			query.append(this.getContentRelationsTableName()).append(".refgroup = ? ");
		}
		query.append(") ");
		query.append(") ");
	}
	
	@Override
	protected int addGroupStatementBlock(Collection<String> groupCodes, int index, PreparedStatement stat) throws Throwable {
		List<String> groups = new ArrayList<String>(groupCodes);
		for (int i=0; i<groups.size(); i++) {
			String groupName = groups.get(i);
			stat.setString(++index, groupName);
		}
		for (int i=0; i<groups.size(); i++) {
			String groupName = groups.get(i);
			stat.setString(++index, groupName);
		}
		return index;
	}
	
	@Override
	public String getEntitySearchTableName() {
		return "contentsearch";
	}
	@Override
	public String getEntitySearchTableIdFieldName() {
		return "contentid";
	}
	public String getContentRelationsTableName() {
		return "contentrelations";
	}
	
}
