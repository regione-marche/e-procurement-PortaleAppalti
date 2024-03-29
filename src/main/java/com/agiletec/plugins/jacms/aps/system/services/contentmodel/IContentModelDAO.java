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

import java.util.Map;

/**
 * Interfaccia base per i Data Access Object 
 * degli oggetti modello contenuto (ContentModel).
 * @author M.Diana
 */
public interface IContentModelDAO {
	
	/**
	 * Carica e restituisce la mappa dei modelli di contenuto.
	 * @return La mappa dei modelli di contenuto.
	 */
	public Map<Long, ContentModel> loadContentModels();
	
	/**
	 * Aggiunge un modello di contenuto nel db.
	 * @param model Il modello di contenuto da aggiungere.
	 */
	public void addContentModel(ContentModel model);
	
	/**
	 * Rimuove un modello di contenuto dal db.
	 * @param group Il modello di contenuto da rimuovere.
	 */
	public void deleteContentModel(ContentModel model);
	
	/**
	 * Aggiorna un modello di contenuto nel db.
	 * @param group Il modello di contenuto da aggiornare.
	 */
	public void updateContentModel(ContentModel model);
	
}
