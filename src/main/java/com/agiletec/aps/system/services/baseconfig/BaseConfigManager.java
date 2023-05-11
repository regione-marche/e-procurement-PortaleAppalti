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

import java.io.StringReader;
import java.util.Map;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.AbstractService;
import com.agiletec.aps.util.ForJLogger;
import com.agiletec.aps.util.MapSupportRule;

/**
 * Servizio di configurazione. Carica da db e rende disponibile
 * la configurazione. La configurazione è costituita da voci (items), individuate
 * da un nome, e da parametri, anch'essi individuati da un nome. I parametri sono
 * stringhe semplici, le voci possono essere testi XML complessi.
 * In particolare, una delle voci contiene la configurazione dei parametri
 * in forma di testo XML.
 * L'insieme dei parametri comprende anche le proprietà di inizializzazione,
 * passate alla factory del contesto di sistema; i valori di queste possono
 * essere sovrascritti dai valori di eventuali parametri omonimi.
 * @author 
 */
public class BaseConfigManager extends AbstractService implements ConfigInterface {
	
	public void init() throws Exception {
		this.loadConfigItems();
		this._params = this.parseParams(_params);
		ApsSystemUtils.getLogger().debug(this.getClass().getName() + ": initialized # "
				+ _configItems.size() + " configuration items and "
				+ _params.size() + " parameters");
	}
	
	/**
	 * Restituisce il valore di una voce della tabella di sistema.
	 * Il valore può essere un XML complesso.
	 * @param name Il nome della voce di configurazione.
	 * @return Il valore della voce di configurazione.
	 */
	public String getConfigItem(String name) {
		return (String) _configItems.get(name);
	}
	
	/**
	 * Aggiorna un'item di configurazione nella mappa 
	 * della configurazione degli item e nel db.
	 * @param itemName Il nome dell'item da aggiornare.
	 * @param config La nuova configurazione.
	 * @throws ApsSystemException
	 */
	public void updateConfigItem(String itemName, String config) throws ApsSystemException {
		String oldParamValue = this.getConfigItem(itemName);
		this._configItems.put(itemName, config);
		String version = (String) _params.get(SystemConstants.INIT_PROP_CONFIG_VERSION);
		try {
			this.getConfigDAO().updateConfigItem(itemName, config, version);
			this.refresh();
		} catch (Throwable t) {
			this._configItems.put(itemName, oldParamValue);
			ApsSystemUtils.logThrowable(t, this, "updateConfigItem");
			throw new ApsSystemException("Error while updating item", t);
		}
	}
	
	/**
	 * Restituisce il valore di un parametro di configurazione.
	 * I parametri sono desunti dalla voce "params" della tabella di sistema.
	 * @param name Il nome del parametro di configurazione.
	 * @return Il valore del parametro di configurazione.
	 */
	public String getParam(String name) {
		return (String) _params.get(name);
	}		
	
	/**
	 * Carica le voci di configurazione da db e le memorizza su un Map.
	 * @throws ApsSystemException in caso di errori di lettura da db
	 */
	private void loadConfigItems() throws ApsSystemException {
		String version = (String) _params.get(SystemConstants.INIT_PROP_CONFIG_VERSION);
		try {
			_configItems = this.getConfigDAO().loadVersionItems(version);
		} catch (Throwable t) {
			throw new ApsSystemException("Error while loading items", t);
		}
	}
	
	/**
	 * Esegue il parsing della voce di configurazione "params" per
	 * estrarre i parametri. I parametri sono caricati sul Map passato
	 * come argomento. I parametri corrispondono a tag del tipo:<br>
	 * &lt;Param name="nome_parametro"&gt;valore_parametro&lt;/Param&gt;<br> 
	 * qualunque sia la loro posizione relativa nel testo XML.<br>
	 * ATTENZIONE: non viene controllata l'univocità del nome, in caso
	 * di doppioni il precedente valore perso.
	 * @throws ApsSystemException In caso di errori IO e Sax
	 */
	private Map<String, String> parseParams(Map<String, String> params) throws ApsSystemException{
		String xml = this.getConfigItem("params");
		Digester dig = new Digester();
		Logger log = ApsSystemUtils.getLogger();
		Logger digLog = LoggerFactory.getLogger(log.getName() + ".digester");
		//digLog.setLevel(Level.SEVERE);
		dig.setLogger(new ForJLogger(digLog));
		Rule rule = new MapSupportRule("name");
		dig.addRule("*/Param", rule);
		dig.push(params);
		try {
			dig.parse(new StringReader(xml));
		} catch (Exception e) {
			ApsSystemUtils.logThrowable(e, this, "parseParams");
			throw new ApsSystemException(
					"Error detected while parsing the \"params\" item in the \"sysconfig\" table:" +
					" verify the \"sysconfig\" table", e);
		}
		return params;
	}
	
	/**
     * Restutuisce il dao in uso al manager.
     * @return Il dao in uso al manager.
     */
    protected IConfigItemDAO getConfigDAO() {
		return _configDao;
	}
	
	/**
     * Setta il dao in uso al manager.
     * @param configDao Il dao in uso al manager.
     */
	public void setConfigDAO(IConfigItemDAO configDao) {
		this._configDao = configDao;
	}
	
	/**
	 * Setta la mappa dei parametri di inizializzazione del sistema.
	 * @param systemParams La mappa dei parametri di inizializzazione.
	 */
	public void setSystemParams(Map<String, String> systemParams) {
		this._params = systemParams;
	}
	
	/**
	 * Map contenente tutte le voci di configurazione di una versione.
	 */
	private Map<String, String> _configItems;
	/**
	 * Map contenente tutti i parametri di configurazione di una versione.
	 */
	private Map<String, String> _params;
	
	private IConfigItemDAO _configDao;
	
}
