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
package com.agiletec.aps.system.services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;

/**
 * Classe contenente alcuni metodi di utilita per i DAO.
 * @author 
 */
public abstract class AbstractDAO {

	/**
	 * Traccia un'eccezione e rilancia una eccezione runtime 
	 * con il messaggio specificato. Da usare nel catch delle eccezioni.
	 * @param t L'eccezione occorsa.
	 * @param message Il messaggio per la nuova ecceione da rilanciare
	 * @param methodName Il nome del metodo in cui si e verificata l'eccezione 
	 *                   (non indispensabile, può essere null)
	 */
	protected void processDaoException(Throwable t, String message, String methodName) {
		ApsSystemUtils.logThrowable(t, this, methodName, message);
		throw new RuntimeException(message, t);
	}

	/**
	 * Restituisce una connessione SQL relativa al datasource.
	 * @return La connessione richiesta.
	 * @throws ApsSystemException In caso di errore in apertura di connessione.
	 */
	protected Connection getConnection() throws ApsSystemException {
		Connection conn = null;
		try {
			conn = this.getDataSource().getConnection();
		} catch (SQLException e) {
			ApsSystemUtils.logThrowable(e, this, "getConnection");
			throw new ApsSystemException("Error getting connection to the datasource", e);
		}
		return conn;
	}

	/**
	 * Chiude in modo controllato un resultset, uno statement e la connessione, 
	 * senza rilanciare eccezioni. Da usare nel finally di gestione di
	 * una eccezione.
	 * @param res Il resultset da chiudere; può esser null
	 * @param stat Lo statement da chiudere; può esser null
	 * @param conn La connessione al db; può esser null
	 */
	protected void closeDaoResources(ResultSet res, Statement stat, Connection conn) {
		this.closeDaoResources(res, stat);
		this.closeConnection(conn);
	}

	/**
	 * Chiude in modo controllato un resultset e uno statement, 
	 * senza rilanciare eccezioni. Da usare nel finally di gestione di
	 * una eccezione.
	 * @param res Il resultset da chiudere; può esser null
	 * @param stat Lo statement da chiudere; può esser null
	 */
	protected void closeDaoResources(ResultSet res, Statement stat) {
		if (res != null) {
			try {
				res.close();
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "closeDaoResources", "Error while closing the resultset");
			}
		}
		if (stat != null) {
			try {
				stat.close();
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "closeDaoResources", "Error while closing the resultset");
			}
		}
	}

	/**
	 * Esegue un rollback, senza rilanciare eccezioni. 
	 * Da usare nel blocco catch di gestione di una eccezione. 
	 * @param conn La connessione al db.
	 */
	protected void executeRollback(Connection conn) {
		try {
			if (conn != null) conn.rollback();
		} catch (SQLException e) {
			ApsSystemUtils.logThrowable(e, this, "executeRollback");
		}
	}

	/**
	 * Chiude in modo controllato una connessione, 
	 * senza rilanciare eccezioni. Da usare nel finally di gestione di
	 * una eccezione.
	 * @param conn La connessione al db; può esser null
	 */
	protected void closeConnection(Connection conn) {
		try {
			if (conn != null) conn.close();
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "closeDaoStatement", "Error closing the connection");
		}
	}

	protected DataSource getDataSource() {
		return this._dataSource;
	}

	/**
	 * Setta il datasource relativo al db gestito dalla classe dao.
	 * @param dataSource Il datasorce da settare.
	 */
	public void setDataSource(DataSource dataSource) {
		this._dataSource = dataSource;
	}

	private DataSource _dataSource;

}
