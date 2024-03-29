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
package com.agiletec.plugins.jacms.apsadmin.portal.specialshowlet.listviewer;

import com.agiletec.apsadmin.portal.specialshowlet.ISimpleShowletConfigAction;

/**
 * Interfaccia base per le action gestori della configurazione della showlet erogatore avanzato lista contenuti.
 * @author E.Santoboni
 */
public interface IContentListViewerShowletAction extends ISimpleShowletConfigAction {
	
	/**
	 * Esegue l'operazione di configurazione del tipo di contenuto.
	 * Il tipo di contenuto scelto viene estratto dai parametri di richiesta corrente
	 * @return Il codice del risultato dell'azione.
	 */
	public String configContentType();
	
	/**
	 * Esegue la richiesta cambiamento del tipo di contenuto.
	 * L'operazione consiste nel reset dei parametri di configurazione della showlet e 
	 * redirect alla pagina di scelta del tipo di contenuto.
	 * @return Il codice del risultato dell'azione.
	 */
	public String changeContentType();
	
	/**
	 * Esegue l'operazione di aggiunta di un nuovo filtro 
	 * in base ai parametri di richiesta corrente.
	 * @return Il codice del risultato dell'azione.
	 */
	public String addFilter();
	
	/**
	 * Esegue l'operazione di rimozione di un filtro 
	 * in base ai parametri di richiesta corrente.
	 * @return Il codice del risultato dell'azione.
	 */
	public String removeFilter();
	
	/**
	 * Esegue l'operazione di spostamento di un filtro 
	 * in base ai parametri di richiesta corrente.
	 * @return Il codice del risultato dell'azione.
	 */
	public String moveFilter();
	
	public static final String MOVEMENT_UP_CODE = "UP";
	public static final String MOVEMENT_DOWN_CODE = "DOWN";
	
}