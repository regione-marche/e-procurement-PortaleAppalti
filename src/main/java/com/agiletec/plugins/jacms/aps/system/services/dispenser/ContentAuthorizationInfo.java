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
package com.agiletec.plugins.jacms.aps.system.services.dispenser;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import com.agiletec.aps.system.services.group.Group;

/**
 * Rappresentazione delle informazioni di autorizzazione di un contenuto. 
 * L'oggetto valorizzato viene inserito nella cache in base all'identificativo 
 * alfanumerico prodotto dall'identificativo del contenuto. 
 * @author E.Santoboni
 */
public class ContentAuthorizationInfo implements Serializable {
	
	private static final long serialVersionUID = -5241592759371755368L;
	
	/**
	 * Setta l'array dei codici dei gruppi 
	 * autorizzati alla visualizzazione del contenuto.
	 * @param allowedGroups  L'array dei codici dei gruppi autorizzati.
	 */
	protected void setAllowedGroups(String[] allowedGroups) {
		this._allowedGroups = allowedGroups;
	}
	
	/**
	 * Verifica i permessi dell'utente in accesso al contenuto.
	 * Restituisce true se l'utente specificato è abilitato 
	 * ad accedere al contenuto, false in caso contrario.
	 * @param user L'utente di cui verificarne l'abilitazione.
	 * @return true se l'utente specificato è abilitato ad accedere 
	 * al contenuto, false in caso contrario.
	 */
	public boolean isUserAllowed(List<Group> userGroups) {
		boolean check = this.checkGroup(userGroups, Group.ADMINS_GROUP_NAME);
		if (check) return true;
    	for (int i=0; i<_allowedGroups.length; i++) {
			String allowedGroup = _allowedGroups[i];
			if (Group.FREE_GROUP_NAME.equals(allowedGroup) || 
					this.checkGroup(userGroups, allowedGroup)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean checkGroup(List<Group> groups, String groupName) {
		Iterator<Group> iter = groups.iterator();
    	while (iter.hasNext()) {
    		Group group = iter.next();
    		if (group.getName().equals(groupName)) {
    			return true;
    		}
    	}
		return false;
	}
	
	/**
	 * Aggiunge un identificativo di risorsa protetta nella lista 
	 * di risorse protette referenziato dal contenuto.
	 * @param resourceId L'identificativo della risorsa protetta 
	 * da aggiungere nella lista.
	 */
	protected void addProtectedResourceId(String resourceId) {
		int len = _protectedResourcesId.length;
		String[] newArray = new String[len + 1];
		for(int i=0; i < len; i++){
			newArray[i] = _protectedResourcesId[i];
		}
		newArray[len] = resourceId;
		_protectedResourcesId = newArray;
	}
	
	/**
	 * Verifica che una risorsa protetta sia referenziata nel contenuto gestito.
	 * @param resourceId L'identificativo della risorsa del quale 
	 * verificare se referenziato.
	 * @return True se la risorsa è referenziata nel contenuto, 
	 * false in caso contrario.
	 */
	public boolean isProtectedResourceReference(String resourceId) {
		for (int i=0; i<_protectedResourcesId.length; i++) {
			if (_protectedResourcesId[i].equals(resourceId)) return true;
		}
		return false;
	}
	
	private String[] _allowedGroups = new String[0];
	private String[] _protectedResourcesId = new String[0];
	
}
