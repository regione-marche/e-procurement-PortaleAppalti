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
package com.agiletec.plugins.jacms.aps.system.services.resource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.AbstractDAO;
import com.agiletec.aps.system.services.category.Category;
import com.agiletec.plugins.jacms.aps.system.services.resource.model.ResourceInterface;
import com.agiletec.plugins.jacms.aps.system.services.resource.model.ResourceRecordVO;

/**
 * Data Access Object per gli oggetti risorsa.
 * @author E.Santoboni - W.Ambu
 */
public class ResourceDAO extends AbstractDAO implements IResourceDAO {
	
	/**
	 * Carica una risorsa nel db. 
	 * @param resource La risorsa da caricare nel db.
	 */
	@Override
	public void addResource(ResourceInterface resource) {
		Connection conn = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			this.addResourceRecord(resource, conn);
			this.addCategoryRelationsRecord(resource, conn);
			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			processDaoException(t, "Errore in aggiunta risorsa", "addResource");
		} finally {
			closeConnection(conn);
		}
	}
    
    protected void addResourceRecord(ResourceInterface resource, Connection conn) throws ApsSystemException {
    	PreparedStatement stat = null;
        try {
        	stat = conn.prepareStatement(ADD_RESOURCE);
			stat.setString(1, resource.getId());
			stat.setString(2, resource.getType());
			stat.setString(3, resource.getDescr());
			stat.setString(4, resource.getMainGroup());
			stat.setString(5, resource.getXML());
			stat.executeUpdate();
        } catch (Throwable t) {
            processDaoException(t, "Errore in aggiunta record risorsa", "addResourceRecord");
        } finally {
            closeDaoResources(null, stat);
        }
    }
	
	/**
	 * Aggiorna una risorsa nel database.
	 * @param resource La risorsa da aggiornare nel db.
	 */
    @Override
	public void updateResource(ResourceInterface resource){
		Connection conn = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			this.deleteResourceRelationsRecord(resource.getId(), conn);
			this.updateResourceRecord(resource, conn);
			this.addCategoryRelationsRecord(resource, conn);
			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			processDaoException(t, "Errore in aggiornamento risorsa", "updateResource");
		} finally {
			closeConnection(conn);
		}
	}
    
    protected void updateResourceRecord(ResourceInterface resource, Connection conn) throws ApsSystemException {
    	PreparedStatement stat = null;
        try {
        	stat = conn.prepareStatement(UPDATE_RESOURCE);
			stat.setString(1, resource.getType());
			stat.setString(2, resource.getDescr());
			stat.setString(3, resource.getMainGroup());
			stat.setString(4, resource.getXML());
			stat.setString(5, resource.getId());
			stat.executeUpdate();
        } catch (Throwable t) {
            processDaoException(t, "Errore in aggiornamento record risorsa", "updateResourceRecord");
        } finally {
            closeDaoResources(null, stat);
        }
    }
	
	/**
	 * Cancella una risorsa dal db.
	 * @param id L'identificativo della risorsa da cancellare.
	 */
    @Override
	public void deleteResource(String id) {
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			this.deleteResourceRelationsRecord(id, conn);
			this.deleteContentsReference(id, conn);
			stat = conn.prepareStatement(DELETE_RESOURCE);
			stat.setString(1, id);
			stat.executeUpdate();
			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			processDaoException(t, "Errore in cancellazione risorsa", "deleteResource");
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}
	
	/**
	 * Carica una lista di identificativi di risorse 
	 * in base al tipo, ad una parola chiave e dalla categoria della risorsa. 
	 * @param type Tipo di risorsa da cercare.
	 * @param text Testo immesso per il raffronto con la descrizione della risorsa. null o 
	 * stringa vuota nel caso non si voglia ricercare le risorse per parola chiave. 
	 * @param categoryCode Il codice della categoria delle risorse. null o 
	 * stringa vuota nel caso non si voglia ricercare le risorse per categoria.
	 * @param groupCodes I codici dei gruppi utenti consentiti tramite il quale 
	 * filtrare le risorse. Nel caso che la collezione di codici sia nulla o vuota, 
	 * non verrà eseguito la selezione per gruppi.
	 * @return La lista di identificativi di risorse.
	 */
    @Override
	public List<String> searchResourcesId(String type, String text, String categoryCode, 
			Collection<String> groupCodes) {
		Connection conn = null;
		List<String> resources = new ArrayList<String>();
		PreparedStatement stat = null;
		ResultSet res = null;
		try {
			conn = this.getConnection();
			stat = this.buildStatementForLoadResourcesId(type, categoryCode, groupCodes, conn);
			res = stat.executeQuery();
			while (res.next()) {
				String descr = res.getString("descr");
				if (this.checkText(text, descr)) {
					resources.add(res.getString("resid"));
				}
			}
		} catch (Throwable t) {
			processDaoException(t, "Errore in caricamento id risorse", "searchResourcesId");
		} finally {
			closeDaoResources(res, stat, conn);
		}
		return resources;
	}
	
	private PreparedStatement buildStatementForLoadResourcesId(String type, String categoryCode, 
			Collection<String> groupCodes, Connection conn) throws ApsSystemException {
		PreparedStatement stat = null;
		String query = this.createQueryForLoadResourcesId(type, categoryCode, groupCodes);
		try {
			stat = conn.prepareStatement(query);
			int index = 0;
			if (null != type && type.trim().length() > 0) {
				stat.setString(++index, type);
			}
			if (null != categoryCode && categoryCode.trim().length() > 0) {
				stat.setString(++index, categoryCode);
			}
			if (groupCodes != null && groupCodes.size()>0) {
				Iterator<String> groupIter = groupCodes.iterator();
				while (groupIter.hasNext()) {
					String groupName = (String) groupIter.next();
					stat.setString(++index, groupName);
				}
			}
		} catch (Throwable t) {
			processDaoException(t, "Errore in fase di creazione statement", "buildStatementForLoadResources");
		}
		return stat;
	}
	
	private String createQueryForLoadResourcesId(String type, String categoryCode, Collection<String> groupCodes) {
		StringBuffer query = new StringBuffer(LOAD_RESOURCES_ID_START_BLOCK);
		if (null != categoryCode && categoryCode.trim().length() > 0) {
			query.append(LOAD_RESOURCES_CATEGORY_BLOCK);
		}
		boolean check = false;
		if (null != type && type.trim().length() > 0) {
			query.append(APPEND_STRING_WHERE).append(LOAD_RESOURCES_TYPE_SEARCH_BLOCK);
			check = true;
		}
		
		if (null != categoryCode && categoryCode.trim().length() > 0) {
			if (!check) {
				query.append(APPEND_STRING_WHERE);
				check = true;
			} else {
				query.append(APPEND_STRING_AND);
			}
			query.append(LOAD_RESOURCES_CATEGORY_SEARCH_BLOCK);
		}
		if (groupCodes != null && groupCodes.size()>0) {
			if (!check) {
				query.append(APPEND_STRING_WHERE);
				check = true;
			} else {
				query.append(APPEND_STRING_AND);
			}
			query.append(APPEND_STRING_GROUP_SEARCH_START);
			int size = groupCodes.size();
			for (int i=0; i<size; i++) {
				if (i!=0) query.append(APPEND_STRING_OR);
				query.append(APPEND_STRING_GROUP_SEARCH);
			}
			query.append(APPEND_STRING_GROUP_SEARCH_END);
		}
		query.append(LOAD_RESOURCES_ORDER_STRING_BLOCK);
		return query.toString();
	}
	
	/**
	 * Metodo di servizio. 
	 * Serve a controllare se il testo immesso testo (insertedText) 
	 * è contenuto dentro un'altro (descr), ed in caso di esito 
	 * affermativo restituisce true.
	 */
	private boolean checkText(String insertedText, String descr) {
		boolean validate = false;
		if (insertedText == null || insertedText.trim().length() == 0 || 
				descr.toLowerCase().indexOf(insertedText.trim().toLowerCase()) != -1) {
			validate = true;
		}
		return validate;
	}
	
	/**
	 * Carica un record di risorse in funzione dell'idRisorsa. Questo record è 
	 * necessario per l'estrazione della risorse in oggetto tipo AbstractResource 
	 * da parte del ResourceManager. 
	 * @param id L'identificativo della risorsa.
	 * @return Il record della risorsa.
	 */
	@Override
	public ResourceRecordVO loadResourceVo(String id) {
		Connection conn = null;
		ResourceRecordVO resourceVo = null;
		PreparedStatement stat = null;
		ResultSet res = null;
		try {
			conn = this.getConnection();
			stat = conn.prepareStatement(LOAD_RESOURCE_VO);
			stat.setString(1, id);
			res = stat.executeQuery();
			if (res.next()) {
				resourceVo = new ResourceRecordVO();
				resourceVo.setId(id);
				resourceVo.setResourceType(res.getString(1));
				resourceVo.setDescr(res.getString(2));
				resourceVo.setMainGroup(res.getString(3));
				resourceVo.setXml(res.getString(4));
			}
		} catch (Throwable t) {
			processDaoException(t, "Errore in caricamento risorsa", "loadResourceVo");
		} finally {
			closeDaoResources(res, stat, conn);
		}
		return resourceVo;
	}
	
	/**
	 * Cancella i riferimenti della risorsa con i contenuti.
	 * @param id L'identificativo della risorsa.
	 * @param conn La connessione al db.
	 * @throws ApsSystemException in caso di errore nell'accesso al db.
	 */
	private void deleteContentsReference(String id, Connection conn) throws ApsSystemException {
		PreparedStatement stat = null;
		try {
			stat = conn.prepareStatement(DELETE_CONTENTS_REFERENCE);
			stat.setString(1, id);
			stat.executeUpdate();
		} catch (Throwable t) {
			processDaoException(t, "Errore in cancellazione riferimenti contenuti", "deleteContentsReference");
		} finally {
			closeDaoResources(null, stat);
		}
	}
	
	/**
	 * Metodo di servizio.
	 * Aggiunge un record nella tabella resourcerelations 
	 * per ogni categoria della risorsa.
	 * @param resource La risorsa del quale referenziare le categorie.
	 * @param conn La connessione con il db.
	 * @throws ApsSystemException
	 */
	private void addCategoryRelationsRecord(ResourceInterface resource, Connection conn) 
			throws ApsSystemException {
		if (resource.getCategories().size()>0) {
			PreparedStatement stat = null;
			try {
				Set<String> codes = this.getCategoryCodes(resource);
				stat = conn.prepareStatement(ADD_RESOURCE_REL_RECORD);
				Iterator<String> codeIter = codes.iterator();
				while (codeIter.hasNext()) {
					String code = (String) codeIter.next();
					stat.setString(1, resource.getId());
					stat.setString(2, code);
					stat.addBatch();
					stat.clearParameters();
				}
				stat.executeBatch();
			} catch (Throwable t) {
				processDaoException(t, "Errore in aggiunta record tabella resourcerelations", "addCategoryRelationsRecord");
			} finally {
				closeDaoResources(null, stat);
			}
		}
	}
	
	/**
	 * Restituisce la lista di codici di categorie associate ad una risorsa.
	 * La risorsa viene sempre referenziata con la categoria "root" della 
	 * tipologia relativa (che corrisponde al codice della tipologia). 
	 * @param resource La risorsa da inserire o da modificare.
	 * @return Il set di codici di categorie.
	 */
	private Set<String> getCategoryCodes(ResourceInterface resource) {
		Set<String> codes = new HashSet<String>();
		Iterator<Category> categoryIter = resource.getCategories().iterator();
		while (categoryIter.hasNext()) {
			Category category = (Category) categoryIter.next();
			this.addCategoryCode(resource, category, codes);
		}
		return codes;
	}
	
	private void addCategoryCode(ResourceInterface resource, Category category, Set<String> codes) {
		if (category.getCode().equals(category.getParent().getCode())) return;
		codes.add(category.getCode());
		Category parentCategory = (Category) category.getParent();
		if (null != parentCategory) {
			this.addCategoryCode(resource, parentCategory, codes);
		}
	}
	
	/**
	 * Metodo di servizio.
	 * Cancella i record della tabella resourcerelations 
	 * di una risorsa.
	 * @param id L'identificativo della risorsa.
	 * @param conn La connessione al db.
	 * @throws ApsSystemException
	 */
	private void deleteResourceRelationsRecord(String id, Connection conn) throws ApsSystemException{
		PreparedStatement stat = null;
		try {
			stat = conn.prepareStatement(DELETE_RESOURCE_REL_RECORD);
			stat.setString(1, id);
			stat.executeUpdate();
		} catch (Throwable t) {
			processDaoException(t, "Errore in cancellazione record tabella resourcerelations", "deleteResourceRelationsRecord");
		} finally {
			closeDaoResources(null, stat);
		}
	}
	
	private final String LOAD_RESOURCES_ID_START_BLOCK =
		"SELECT resources.resid, resources.descr FROM resources ";
	private final String LOAD_RESOURCES_CATEGORY_BLOCK = 
		"INNER JOIN resourcerelations ON resources.resid = resourcerelations.resid ";
	private final String APPEND_STRING_WHERE = "WHERE ";
	private final String LOAD_RESOURCES_TYPE_SEARCH_BLOCK = "resources.restype = ? ";
	private final String APPEND_STRING_AND = "AND ";
	private final String LOAD_RESOURCES_CATEGORY_SEARCH_BLOCK = 
		"resourcerelations.refcategory = ? ";
	private final String APPEND_STRING_GROUP_SEARCH_START = "( ";
	private final String APPEND_STRING_GROUP_SEARCH = "maingroup = ? ";
	private final String APPEND_STRING_OR = "OR ";
	private final String APPEND_STRING_GROUP_SEARCH_END = ") ";
	private final String LOAD_RESOURCES_ORDER_STRING_BLOCK = 
		"ORDER BY resources.descr ";
	
	private final String LOAD_RESOURCE_VO =
		"SELECT restype, descr, maingroup, xml FROM resources WHERE resid = ? ";
	
	private final String ADD_RESOURCE =
		"INSERT INTO resources (resid, restype, descr, maingroup, xml) VALUES ( ? , ? , ? , ? , ? )";
	
	private final String UPDATE_RESOURCE =
		"UPDATE resources SET restype = ? , descr = ? , maingroup = ? , xml = ? WHERE resid = ? ";
	
	private final String DELETE_CONTENTS_REFERENCE =
		"DELETE FROM contentrelations WHERE refresource = ? ";
	
	private final String DELETE_RESOURCE =
		"DELETE FROM resources WHERE resid = ? ";
	
	private final String ADD_RESOURCE_REL_RECORD =
		"INSERT INTO resourcerelations (resid, refcategory) VALUES ( ? , ? )";
	
	private final String DELETE_RESOURCE_REL_RECORD =
		"DELETE FROM resourcerelations WHERE resid = ? ";
	
}