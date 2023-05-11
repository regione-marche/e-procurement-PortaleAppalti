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
package com.agiletec.apsadmin.system;

import com.agiletec.aps.system.common.tree.ITreeNode;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.user.UserDetails;

/**
 * Interfaccia base delle classi helper che gestiscono le operazioni su oggetti alberi.
 * @version 1.0
 * @author E.Santoboni
 */
public interface ITreeNodeBaseActionHelper {
	
	/**
	 * Costruisce il codice univoco di un nodo in base ai parametri specificato.
	 * Il metodo:
	 * 1) elimina i caratteri non compresi tra "a" e "z", tra "0" e "9".
	 * 2) taglia (se necessario) la stringa secondo la lunghezza massima immessa.
	 * 3) verifica se esistono entità con il codice ricavato (ed in tal caso appende il suffisso "_<numero>" fino a che non trova un codice univoco).
	 * @param title Il titolo del nuovo nodo.
	 * @param baseDefaultCode Un codice nodo di default.
	 * @param maxLength La lunghezza massima del codice.
	 * @return Il codice univoco univoco ricavato.
	 * @throws ApsSystemException In caso di errore.
	 */
	public String buildCode(String title, String baseDefaultCode, int maxLength) throws ApsSystemException;
	
	/**
	 * Restituisce il nodo root dell'albero abilitato all'utente specificato.
	 * @param user L'utente da cui ricavare il l'albero autorizzato.
	 * @return Il nodo root dell'albero autorizzato.
	 * @throws ApsSystemException In caso di errore.
	 */
	public ITreeNode getAllowedTreeRoot(UserDetails user) throws ApsSystemException;
	
}
