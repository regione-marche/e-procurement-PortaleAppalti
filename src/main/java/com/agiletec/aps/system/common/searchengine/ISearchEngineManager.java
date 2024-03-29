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
package com.agiletec.aps.system.common.searchengine;

import java.util.Collection;
import java.util.List;

import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.exception.ApsSystemException;

/**
 * Interfaccia base per i servizi detentori delle operazioni di indicizzazione 
 * di oggetti ricercabili tramite motore di ricerca.
 * @version 1.0
 * @author
 */
public interface ISearchEngineManager {
	
	/**
     * Aggiorna le indicizzazioni relative ad un contenuto.
     * @param content Il contenuto di cui aggiornare le indicizzazioni.
     * @throws ApsSystemException
     */
    public void updateIndexedEntity(IApsEntity entity) throws ApsSystemException;
    
    /**
     * Cancella una entità in base all'identificativo.
     * @param entityId L'identificativo dell'entità.
     * @throws ApsSystemException
     */
    public void deleteIndexedEntity(String entityId) throws ApsSystemException;
    
    /**
     * Aggiunge un documento relativo ad una entità 
     * nel db del motore di ricerca.
     * @param entity Il contenuto da cui estrarre il documento.
     * @throws ApsSystemException
     */
    public void addEntityToIndex(IApsEntity entity) throws ApsSystemException;
    
    /**
     * Ricerca una lista di identificativi di entità in base 
     * alla chiave lingua corrente ed alla parola immessa.
     * @param langCode Il codice della lingua corrente.
     * @param word La parola in base al quale fare la ricerca. Nel caso venissero 
     * inserite stringhe di ricerca del tipo "Venice Amsterdam" viene considerato come 
     * se fosse "Venice OR Amsterdam".
     * @param allowedGroups I gruppi autorizzati alla visualizzazione.
     * @throws ApsSystemException
     */
	public List<String> searchEntityId(String langCode, 
			String word, Collection<String> allowedGroups) throws ApsSystemException;
	
    
}