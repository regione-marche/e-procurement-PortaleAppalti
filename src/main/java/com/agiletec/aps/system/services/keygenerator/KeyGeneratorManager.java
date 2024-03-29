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
package com.agiletec.aps.system.services.keygenerator;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.AbstractService;

/**
 * Servizio gestore di sequenze univoche.
 * @author S.Didaci - E.Santoboni
 */
public class KeyGeneratorManager extends AbstractService implements IKeyGeneratorManager {

	public void init() throws Exception {
		this.loadUniqueKey();
		ApsSystemUtils.getLogger().debug(this.getClass().getName() + 
				": last loaded key " + _uniqueKeyCurrentValue );
	}

	/**
	 * Estrae la chiave presente nel db.
	 * Il metodo viene chiamato solo in fase di inizializzazione.
	 * @throws ApsSystemException
	 */
	private void loadUniqueKey() throws ApsSystemException {
		try {
			_uniqueKeyCurrentValue = this.getKeyGeneratorDAO().getUniqueKey();
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "loadUniqueKey","Error retrieving the unique key");
			throw new ApsSystemException("Error retrieving the unique key", t);
		}
	}

	/**
	 * Restituisce la chiave univoca corrente.
	 * @return La chiave univoca corrente.
	 * @throws ApsSystemException In caso di errore 
	 * nell'aggiornamento della chiave corrente.
	 */
	public int getUniqueKeyCurrentValue() throws ApsSystemException {
		++_uniqueKeyCurrentValue;
		int key = _uniqueKeyCurrentValue;
		this.updateKey();
		return key;
	}

	private void updateKey() throws ApsSystemException {
		try {
			this.getKeyGeneratorDAO().updateKey(_uniqueKeyCurrentValue);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "loadUniqueKey");
			throw new ApsSystemException("Error updating the unique key", t);
		}
	}

	protected IKeyGeneratorDAO getKeyGeneratorDAO() {
		return _keyGeneratorDao;
	}

	public void setKeyGeneratorDAO(IKeyGeneratorDAO generatorDAO) {
		this._keyGeneratorDao = generatorDAO;
	}

	private int _uniqueKeyCurrentValue;

	private IKeyGeneratorDAO _keyGeneratorDao;

}
