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
package com.agiletec.aps.system.services.baseconfig;

import java.io.BufferedReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;


import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.AbstractDAO;

/**
 * Data Access Object delegate 
 * alla gestione delle le voci di configurazione. 
 * Opera sulla tabella "system".
 * @author 
 */
public class ConfigItemDAO extends AbstractDAO implements IConfigItemDAO {
	
	/**
	 * Carica e restituisce un Map con tutte le voci di
	 * configurazione di una versione di configurazione.
	 * @param version La versione di configurazione.
	 * @return Il Map con le voci di configurazione
	 */
	public Map<String, String> loadVersionItems(String version) {
		Connection conn = null;
		PreparedStatement stat = null;
		ResultSet res = null;
		Map<String, String> itemMap = new HashMap<String, String>();
		try {
			conn = this.getConnection();
			stat = conn.prepareStatement(VERSION_ITEMS);
			stat.setString(1, version);
			res = stat.executeQuery();
			while(res.next()){
				String name = res.getString(1);
				String value = res.getString(2);
				itemMap.put(name, value);
			}
		} catch (Throwable e) {
			processDaoException(e, "Error while loading configuration item - version " 
			+ version, "loadVersionItems");
		} finally {
			closeDaoResources(res, stat, conn);
		}
		return itemMap;
	}
	
	/**
	 * Carica e restituisce una voce di configurazione
	 * di una versione di configurazione. Questo metodo NON deve essere utilizzato
	 * normalmente, ma solo nelle fasi di inizializzazione del sistema, quando
	 * il SysContext non è ancora disponibile.
	 * @param version La versione di configurazione
	 * @param itemName Il nome della voce di configurazione.
	 * @return La voce di configurazione richiesta
	 */
	public String loadVersionItem(String version, String itemName) {
		Connection conn = null;
		PreparedStatement stat = null;
		ResultSet res = null;
		String value = null;
		try {
			conn = this.getConnection();
			stat = conn.prepareStatement(VERSION_ITEM);
			stat.setString(1, version);
			stat.setString(2, itemName);
			res = stat.executeQuery();
			res.next();
			value = null; //res.getString(1);
			if (res.getObject(1) instanceof String) {
				value = res.getString(1);
			} else if (res.getObject(1) instanceof oracle.sql.CLOB) {
				// lettura in caso di CLOB Oracle (che non consente i varchar senza dimensione)
				StringBuffer str = new StringBuffer();
				String tempString = null;
				try {
					BufferedReader bufferRead = new BufferedReader(
							((oracle.sql.CLOB) res.getObject(1)).getCharacterStream());
					while ((tempString = bufferRead.readLine()) != null) {
						str.append(tempString);
					}
				} catch (Throwable t) {
					String msg = "IO error detected while reading configuration item - version: " + 
					 version + ", item: " + itemName;
					ApsSystemUtils.logThrowable(t, this, "loadVersionItem", msg);
					throw new ApsSystemException(msg, t);
				}
				value = str.toString();
			}

		} catch (Throwable t) {
			Logger log = ApsSystemUtils.getLogger();
			log.error("Error while loading the configuration item - version: " + 
					 version + ", item: " + itemName);
			log.error(this.getClass().getName(), "loadVersioneItem", t);
		} finally {
			closeDaoResources(res, stat, conn);
		}
		return value;
	}
	
	/**
	 * Aggiorna un'item di configurazione nel db.
	 * @param itemName Il nome dell'item da aggiornare.
	 * @param config La nuova configurazione.
	 * @param version La versione da aggiornare.
	 */
	public void updateConfigItem(String itemName, String config, String version) {
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(UPDATE_ITEM);
			stat.setString(1, config);
			stat.setString(2, itemName);
			stat.setString(3, version);
			stat.executeUpdate();
			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			processDaoException(t, "Error while updating oconfiguration item",
					"updateConfigItem");
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}
	
	private final String VERSION_ITEMS =
		"SELECT item, config FROM sysconfig WHERE version = ?";
	
	private final String VERSION_ITEM =
		"SELECT config FROM sysconfig WHERE version = ? AND item = ?";
	
	private static final String UPDATE_ITEM = 
		"UPDATE sysconfig SET config = ? WHERE item = ? AND version = ?";
	
}
