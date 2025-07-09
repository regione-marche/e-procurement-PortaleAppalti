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
package com.agiletec.aps.system.services.i18n;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.AbstractService;
import com.agiletec.aps.util.ApsProperties;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Servizio che fornisce stringhe "localizzate". Le stringhe sono specificate 
 * da una chiave di identificazione e dalla lingua di riferimento.
 * @author S.Didaci - E.Santoboni - S.Puddu
 */
public class I18nManager extends AbstractService implements II18nManager {

	public void init() throws Exception {
		this.loadLabels();
		ApsSystemUtils.getLogger().debug(this.getClass().getName() + 
				": initialized " + _labelGroups.size() + " labels");
	}

	/**
	 * Carica le label dalla configurazione riguardanti tutte le 
	 * lingue caricate nel sistema e le inserisce in un'HashMap
	 * @throws ApsSystemException in caso di errori di parsing
	 */
	private void loadLabels() throws ApsSystemException {
		try {
			_labelGroups = this.getI18nDAO().loadLabelGroups();
		} catch (Throwable t) {
			throw new ApsSystemException("Error loading labels", t);
		}
	}

	/**
	 * Restituisce una label in base alla chiave ed alla lingua specificata.
	 * @param key La chiave
	 * @param langCode Il codice della lingua.
	 * @return La label richiesta.
	 * @throws ApsSystemException
	 */
	public String getLabel(String key, String langCode) throws ApsSystemException {
		return getLabel(key, langCode, true);
	}
	/**
	 * Restituisce una label in base alla chiave ed alla lingua specificata.
	 * @param key La chiave
	 * @param langCode Il codice della lingua.
	 * @return La label richiesta.
	 * @throws ApsSystemException
	 */
	public String getLabel(String key, String langCode, boolean printError) throws ApsSystemException {
		String label = null;
		ApsProperties labelsProp = (ApsProperties) _labelGroups.get(key);
		if (labelsProp != null)
			label = labelsProp.getProperty(langCode);
		else if (printError)
			ApsSystemUtils.getLogger().warn("Reperita label in localstrings nulla per " + key + " (lang " + langCode + ")");

		return label;
	}


	/**
	 * Add a group of labels on db.
	 * @param key The key of the labels.
	 * @param labels The labels to add.
	 * @throws ApsSystemException In case of Exception.
	 */
	public void addLabelGroup(String key, ApsProperties labels) throws ApsSystemException {
		try {
			Enumeration<Object> langKeys = labels.keys();
			while (langKeys.hasMoreElements()) {
				String lang = (String) langKeys.nextElement();
				labels.setCustomized(lang, 1);
			}
			this.getI18nDAO().addLabelGroup(key, labels);
			this._labelGroups.put(key, labels);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "addLabelGroup");
			throw new ApsSystemException("Error while adding a group of labels", t);
		}
	}

	/**
	 * Delete a group of labels from db.
	 * @param key The key of the labels to delete.
	 * @throws ApsSystemException In case of Exception.
	 */
	public void deleteLabelGroup(String key) throws ApsSystemException {
		try {
			this.getI18nDAO().deleteLabelGroup(key);
			this._labelGroups.remove(key);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "deleteLabelGroup");
			throw new ApsSystemException("Error while deleting a label", t);
		}
	}

	/**
	 * Update a group of labels on db.
	 * @param key The key of the labels.
	 * @param labels The key of the labels to update.
	 * @throws ApsSystemException In case of Exception.
	 */
	public void updateLabelGroup(String key, ApsProperties labels) throws ApsSystemException {
		try {
			ApsProperties old = this._labelGroups.get(key);
			String[] langKeys = labels.keySet().toArray(new String[0]);
			// Nelle chiavi dentro "labels" (label.keys()) si trovano sia le lingue che le lingue con flag custom (es: it.custom).
			// Vanno escluse tali chiavi a cui non corrispondono valori di label tradotti in lingua, pertanto ciclo sull'array pulito
			// in quanto labels.setCustomized aggiunge chiavi "<lingua>.custom" direttamente dentro a label.keys()
			for (String lang: langKeys) {
				String oldlbl = old.getProperty(lang);
				String newlbl = labels.getProperty(lang);
				int custom = old.getCustomized(lang);
				if( !newlbl.equals(oldlbl) ) {
					custom = 1;
				}
				if(custom != 0) {
					labels.setCustomized(lang, 1);
				}
			}
			this.getI18nDAO().updateLabelGroup(key, labels);
			this._labelGroups.put(key, labels);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "updateLabel");
			throw new ApsSystemException("Error while updating a label", t);
		}
	}

	/**
	 * Restituisce la lista di chiavi di gruppi di labels 
	 * in base ai parametri segbalati.
	 * @param insertedText Il testo tramite il quale effettuare la ricerca.
	 * @param doSearchByKey Specifica se effettuare la ricerca sulle chiavi, 
	 * in base al testo inserito.
	 * @param doSearchByLang Specifica se effettuare la ricerca 
	 * sul testo di una lingua, in base al testo inserito.
	 * @param langCode Specifica la lingua della label sulla quale effettuare 
	 * la ricerca, in base al testo inserito.
	 * @return La lista di chiavi di gruppi di labels 
	 * in base ai parametri segbalati.
	 */
	public List<String> searchLabelsKey(
			String insertedText, 
			boolean doSearchByKey, 
			boolean doSearchByLang, 
			String langCode,
			boolean customized) 
	{
		List<String> keys = new ArrayList<String>();
		Pattern pattern = Pattern.compile(insertedText,Pattern.CASE_INSENSITIVE + Pattern.LITERAL);
		Matcher matcher = pattern.matcher("");
		List<String> allKeys = new ArrayList<String>(this.getLabelGroups().keySet());
		for (int i=0; i<allKeys.size(); i++) {
			String key = allKeys.get(i);
			ApsProperties properties = (ApsProperties) this.getLabelGroups().get(key);
			boolean addkey = false;
			if (!doSearchByKey && !doSearchByLang) {
				matcher = matcher.reset(key);
				if (matcher.find()) {
					addkey = true;
				} else {
					Enumeration<Object> langKeys = properties.keys();
					while (langKeys.hasMoreElements()) {
						String lang = (String) langKeys.nextElement();
						String target = properties.getProperty(lang);
						int custom = properties.getCustomized(lang);
						if (this.labelMatch(target, matcher)) {
							addkey = true;
							break;
						}
					}
				}
			} else if (doSearchByKey && !doSearchByLang) {
				matcher = matcher.reset(key);
				if (matcher.find()) {
					addkey = true;
				}
			} else if (!doSearchByKey && doSearchByLang) {
				String target = properties.getProperty(langCode);
				if(this.labelMatch(target, matcher)) {
					addkey = true;
				}
			}
			
			if(customized) {
				// verifica se esiste almeno una lingua custom per la label
				int n = 0;
				Enumeration<Object> langKeys = properties.keys();
				while (langKeys.hasMoreElements()) {
					String lang = (String) langKeys.nextElement();
					if(properties.getCustomized(lang) == 1) n++;
				}
				addkey = (n > 0);
			}
			
			if(addkey) {
				keys.add(key);
			}
		}
		return keys;
	}
	
	private boolean labelMatch(String target, Matcher matcher){
		boolean match = false;
		if(null != target) {
			matcher = matcher.reset(target);
			if(matcher.find()) {
				match =true;
			}
		}
		return match;
	}

	/**
	 * Return the group of labels.
	 * @return The group of labels.
	 */
	public Map<String, ApsProperties> getLabelGroups() {
		return this._labelGroups;
	}

	protected II18nDAO getI18nDAO() {
		return _i18nDAO;
	}
	public void setI18nDAO(II18nDAO i18nDao) {
		_i18nDAO = i18nDao;
	}

	/**
	 * Map delle label internazionalizzate.
	 */
	private Map<String, ApsProperties> _labelGroups;

	private II18nDAO _i18nDAO;

}
