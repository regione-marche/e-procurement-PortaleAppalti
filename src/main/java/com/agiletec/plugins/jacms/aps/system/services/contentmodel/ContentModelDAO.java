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
package com.agiletec.plugins.jacms.aps.system.services.contentmodel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.agiletec.aps.system.services.AbstractDAO;

/**
 * Data Access Object per gli oggetti modello contenuto (ContentModel).
 * @author M.Diana - C.Siddi - C.Sirigu
 */
public class ContentModelDAO extends AbstractDAO implements IContentModelDAO {
	
	@Override
	public Map<Long, ContentModel> loadContentModels(){
		Connection conn = null;
		Statement stat = null;
		ResultSet res = null;
		Map<Long, ContentModel> models = new HashMap<Long, ContentModel>();
		String query = ALL_CONTENTMODEL;
		try {
			conn = this.getConnection();
			stat = conn.createStatement();
			res = stat.executeQuery(query);
			while(res.next()){
				ContentModel contentModel = loadContentModel(res);
				//Hash map che contiene come chiave l'id e come valore l'oggetto stesso
				Long wrapLongId = new Long(contentModel.getId());
				models.put(wrapLongId, contentModel);
			}
		} catch (Throwable e) {
			processDaoException(e, "Errore in caricamento modelli pagina", "loadContentModels");
		} finally{
			closeDaoResources(res, stat, conn);
		}
		return models;
	}
    
	@Override
	public void addContentModel(ContentModel model) {
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(ADD_CONTENT_MODEL);
			stat.setLong(1, model.getId());
			stat.setString(2, model.getContentType());
			stat.setString(3, model.getDescription());
			stat.setString(4, model.getContentShape());
			stat.setString(5, model.getStylesheet());
			stat.executeUpdate();
			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			processDaoException(t, "Errore in aggiunta modello di contenuto " + model.getId(), "addContentModel");
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}
	
	@Override
	public void deleteContentModel(ContentModel model) {
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(DELETE_CONTENT_MODEL);
			stat.setLong(1, model.getId());
			stat.executeUpdate();
			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			processDaoException(t, "Errore in cancellazione modello di contenuto " + model.getId(), "deleteContentModel");
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}
	
	@Override
	public void updateContentModel(ContentModel model) {
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(UPDATE_CONTENT_MODEL);
			stat.setString(1, model.getContentType());
			stat.setString(2, model.getDescription());
			stat.setString(3, model.getContentShape());
			stat.setString(4, model.getStylesheet());
			stat.setLong(5, model.getId());
			stat.executeUpdate();
			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			processDaoException(t, "Errore in modifica modello di contenuto " + model.getId(), "updateContentModel");
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}
	
	/**
	 * Costruisce e restituisce un modello di contenuto leggendo una riga
	 * di recordset.
	 * @param res Il resultset da leggere
	 * @return Il modello di contenuto generato
	 * @throws SQLException
	 */
	private ContentModel loadContentModel(ResultSet res) throws SQLException {
	    ContentModel contentModel = new ContentModel();
	    contentModel.setId(res.getLong(1));
	    contentModel.setContentType(res.getString(2));
	    contentModel.setDescription(res.getString(3));	
	    contentModel.setContentShape(res.getString(4));
	    contentModel.setStylesheet(res.getString(5));
	    return contentModel;
	}
	
	private final String ALL_CONTENTMODEL =
		"SELECT modelid, contenttype, descr, model, stylesheet FROM contentmodels";
	
	private final String ADD_CONTENT_MODEL =
		"INSERT INTO contentmodels (modelid, contenttype, descr, model, stylesheet ) VALUES ( ? , ? , ? , ? , ? )";
	
	private static final String DELETE_CONTENT_MODEL = 
		"DELETE FROM contentmodels WHERE modelid = ? ";
	
	private final String UPDATE_CONTENT_MODEL =
		"UPDATE contentmodels SET contenttype = ? , descr = ? , model = ? , stylesheet = ? WHERE modelid = ? ";	
	
}