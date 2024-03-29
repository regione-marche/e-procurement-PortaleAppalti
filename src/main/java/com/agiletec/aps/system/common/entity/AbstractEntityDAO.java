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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.agiletec.aps.system.common.entity.model.ApsEntityRecord;
import com.agiletec.aps.system.common.entity.model.AttributeSearchInfo;
import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.common.util.EntityAttributeIterator;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.AbstractDAO;
import com.agiletec.aps.system.services.lang.ILangManager;

/**
 * Abstract DAO class used for the management of the ApsEntities.
 * @author E.Santoboni
 */
public abstract class AbstractEntityDAO extends AbstractDAO implements IEntityDAO {
	
	@Override
	public void addEntity(IApsEntity entity) {
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(this.getAddEntityRecordQuery());
			this.buildAddEntityStatement(entity, stat);
			stat.executeUpdate();
			this.addEntitySearchRecord(entity.getId(), entity, conn);
			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			processDaoException(t, "Error adding entity", "addEntity");
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}
	
	protected abstract String getAddEntityRecordQuery();
	
	protected abstract void buildAddEntityStatement(IApsEntity entity, PreparedStatement stat) throws Throwable;
	
	@Override
	public void deleteEntity(String entityId) {
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			this.deleteEntitySearchRecord(entityId, conn);
			stat = conn.prepareStatement(this.getDeleteEntityRecordQuery());
			stat.setString(1, entityId);
			stat.executeUpdate();
			conn.commit();
		} catch (Throwable t) {
			processDaoException(t, "Errore deleting the entity by id", "deleteEntity");
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}
	
	protected abstract String getDeleteEntityRecordQuery();
	
	@Override
	public void updateEntity(IApsEntity entity) {
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			this.deleteEntitySearchRecord(entity.getId(), conn);
			stat = conn.prepareStatement(this.getUpdateEntityRecordQuery());
			this.buildUpdateEntityStatement(entity, stat);
			stat.executeUpdate();
			this.addEntitySearchRecord(entity.getId(), entity, conn);
			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			processDaoException(t, "Errore updating entity", "updateEntity");
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}
	
	protected abstract String getUpdateEntityRecordQuery();
	
	protected abstract void buildUpdateEntityStatement(IApsEntity entity, PreparedStatement stat) throws Throwable;
	
	@Override
	public ApsEntityRecord loadEntityRecord(String id) {
		Connection conn = null;
		PreparedStatement stat = null;
		ResultSet res = null;
		ApsEntityRecord entityRecord = null;
		try {
			conn = this.getConnection();
			stat = conn.prepareStatement(this.getLoadEntityRecordQuery());
			stat.setString(1, id);
			res = stat.executeQuery();
			if (res.next()) {
				entityRecord = this.createEntityRecord(res);
			}
		} catch (Throwable t) {
			processDaoException(t, "Error loading entity record", "loadEntityRecord");
		} finally {
			closeDaoResources(res, stat, conn);
		}
		return entityRecord;
	}
	
	public abstract String getLoadEntityRecordQuery();
	
	public abstract ApsEntityRecord createEntityRecord(ResultSet res) throws Throwable;
	
	@Override
	public void reloadEntitySearchRecords(String id, IApsEntity entity) {
		Connection conn = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			this.deleteEntitySearchRecord(id, conn);
			this.addEntitySearchRecord(id, entity, conn);
			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			processDaoException(t, "Error detected while reloading references", "reloadEntityReferences");
		} finally {
			this.closeConnection(conn);
		}
	}
	
	/**
	 * 'Utility' method.
	 * Add a record in the 'contentSearch' table for every indexed attribute.
	 */
	protected void addEntitySearchRecord(String id, IApsEntity entity, Connection conn) throws ApsSystemException {
		PreparedStatement stat = null;
		try {
			stat = conn.prepareStatement(this.getAddingSearchRecordQuery());
			this.addEntitySearchRecord(id, entity, stat);
		} catch (Throwable t) {
			processDaoException(t, "Error while adding a new record", "addEntitySearchRecord");
		} finally {
			closeDaoResources(null, stat);
		}
	}
	
	protected void addEntitySearchRecord(String id, IApsEntity entity, PreparedStatement stat) throws Throwable {
		EntityAttributeIterator attributeIter = new EntityAttributeIterator(entity);
		while (attributeIter.hasNext()) {
			AttributeInterface currAttribute = (AttributeInterface) attributeIter.next();
			List<AttributeSearchInfo> infos = currAttribute.getSearchInfos(this.getLangManager().getLangs());
			if (currAttribute.isSearcheable() && null != infos) {
				for (int i=0; i<infos.size(); i++) {
					AttributeSearchInfo searchInfo = infos.get(i);
					stat.setString(1, id);
					stat.setString(2, currAttribute.getName());
					stat.setString(3, searchInfo.getString());
					if (searchInfo.getDate() != null) {
						stat.setDate(4, new java.sql.Date(searchInfo.getDate().getTime()));
					} else {
						stat.setDate(4, null);
					}
					stat.setBigDecimal(5, searchInfo.getBigDecimal());
					stat.setString(6, searchInfo.getLangCode());
					stat.addBatch();
					stat.clearParameters();
				}
			}
		}
		stat.executeBatch();
	}
	
	/**
	 * 'Utility' method.
	 * Delete the records in the support table (the table used to perform search the entities)
	 */
	protected void deleteEntitySearchRecord(String id, Connection conn) throws ApsSystemException{
		PreparedStatement stat = null;
		try {
			stat = conn.prepareStatement(this.getRemovingSearchRecordQuery());
			stat.setString(1, id);
			stat.executeUpdate();
		} catch (Throwable t) {
			processDaoException(t, "Error detected while deleting a record from the support table",
					"deleteEntitySearchRecord");
		} finally {
			closeDaoResources(null, stat);
		}
	}
	
	/**
	 * @deprecated deprecated from jAPS 2.0 version 2.0.9
	 */
	@Override
	public List<String> getAllEntityId() {
		Connection conn = null;
		Statement stat = null;
		ResultSet res = null;
		List<String> entitiesId = new ArrayList<String>();
		try {
			conn = this.getConnection();
			stat = conn.createStatement();
			res = stat.executeQuery(this.getExtractingAllEntityIdQuery());
			while (res.next()) {
				entitiesId.add(res.getString(1));
			}
		} catch (Throwable t) {
			processDaoException(t, "Error retrieving the list of entity IDs", "getAllEntityId");
		} finally {
			closeDaoResources(res, stat, conn);
		}
		return entitiesId;
	}
	
	/**
	 * Return the specific query to add a new record of informations in the
	 * support database.
	 * The query must respect the following positions of the elements:<br />
	 * Position 1: entity ID<br />
	 * Position 2: attribute name<br />
	 * Position 3: searchable string<br />
	 * Position 4: searchable data<br />
	 * Position 5: searchable number<br />
	 * Position 6: Language code
	 * @return the query to add a look up record for the entity search. 
	 */
	public abstract String getAddingSearchRecordQuery();
	
	/**
	 * Return the query to delete the record associated to an entity. The returned query will only need
	 * the declaration of the ID of the entity to delete.
	 * @return  The query to delete the look up record of a single entity.
	 */
	public abstract String getRemovingSearchRecordQuery();
	
	/**
	 * Return the query that extracts the list of entity IDs.
	 * @return The query that extracts the list of entity IDs.
	 * @deprecated As of jAPS 2.0 version 2.0.9
	 */
	public abstract String getExtractingAllEntityIdQuery();
	
	protected ILangManager getLangManager() {
		return _langManager;
	}
	public void setLangManager(ILangManager langManager) {
		this._langManager = langManager;
	}
	
	private ILangManager _langManager;
	
}
