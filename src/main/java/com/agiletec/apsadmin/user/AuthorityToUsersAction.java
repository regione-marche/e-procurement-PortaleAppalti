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
package com.agiletec.apsadmin.user;

import java.util.Iterator;
import java.util.List;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.authorization.IApsAuthority;
import com.agiletec.aps.system.services.authorization.authorizator.IApsAuthorityManager;
import com.agiletec.aps.system.services.user.UserDetails;

/**
 * Classe action delegata alla gestione delle operazioni di associazione 
 * delle autorizzazioni agli utenti del sistema.
 * @version 1.0
 * @author E.Mezzano - E.Santoboni
 */
public class AuthorityToUsersAction extends UserFinderAction implements IAuthorityToUsersAction {
	
	@Override
	public String addUser() {
		IApsAuthority auth = this.getApsAuthority();
		try {
			if (SystemConstants.ADMIN_USER_NAME.equals(this.getUsername())) {
				//TODO DA INSERIRE MESSAGGIO CORRETTO
				this.addActionError(this.getText("MESSAGGIO_NON_SI_PUO_MODIFICARE_UTENTE_AMMINISTRATORE_DI_DEFAULT"));
				return INPUT;
			}
			UserDetails user = this.getUser();
			if (user!=null && !this.hasUserAuthority()) {
				IApsAuthorityManager authorizatorManager = this.getAuthorizatorManager();
				authorizatorManager.setUserAuthorization(this.getUsername(), auth);
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "addUser");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	@Override
	public String removeUser() {
		IApsAuthority auth = this.getApsAuthority();
		try {
			if (SystemConstants.ADMIN_USER_NAME.equals(this.getUsername())) {
				//TODO DA INSERIRE MESSAGGIO CORRETTO
				this.addActionError(this.getText("MESSAGGIO_NON_SI_PUO_MODIFICARE_UTENTE_AMMINISTRATORE_DI_DEFAULT"));
				return INPUT;
			}
			UserDetails user = this.getUser();
			if (user != null) {
				IApsAuthorityManager authorizatorManager = this.getAuthorizatorManager();
				authorizatorManager.removeUserAuthorization(this.getUsername(), auth);
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "removeUser");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	@Override
	public IApsAuthority getApsAuthority() {
		String authName = this.getAuthName();
		IApsAuthority authority = this.getAuthorizatorManager().getAuthority(authName);
		return authority;
	}
	
	/**
	 * Restituisce la lista degli utenti associati all'authority corrente.
	 * @return La lista degli utenti associati all'authority corrente.
	 */
	public List<UserDetails> getAuthorizedUsers() {
		IApsAuthority auth = this.getApsAuthority();
		try {
			return this.getAuthorizatorManager().getUsersByAuthority(auth);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getUserAuthorizated");
			throw new RuntimeException("Errore in ricerca utenti autorizzati", t);
		}
	}
	
	/**
	 * Recupera l'utente corrente. Se non esiste restituisce null.
	 * @return L'utente richiesto.
	 * @throws ApsSystemException In caso di errore.
	 */
	protected UserDetails getUser() throws ApsSystemException {
		String username = this.getUsername();
		UserDetails user = null;
		if (username!=null && username.trim().length()>=0) {
			user = this.getUserManager().getUser(username);
		}
		return user;
	}
	
	//TODO TROVARE IL MODO DI ELIMINARE QUESTO METODO
	protected boolean hasUserAuthority() throws ApsSystemException {
		String username = this.getUsername();
		List<UserDetails> users = this.getAuthorizatorManager().getUsersByAuthority(this.getApsAuthority());
		Iterator<UserDetails> usersIter = users.iterator();
		while (usersIter.hasNext()) {
			UserDetails currentUser = usersIter.next();
			if (currentUser.getUsername().equals(username)) {
				return true;
			}
		}
		return false;
	}
	
	public String getAuthName() {
		return _authName;
	}
	public void setAuthName(String authName) {
		this._authName = authName;
	}
	
	public String getUsername() {
		return _username;
	}
	public void setUsername(String username) {
		this._username = username;
	}
	
	protected IApsAuthorityManager getAuthorizatorManager() {
		return _authorizatorManager;
	}
	public void setAuthorizatorManager(IApsAuthorityManager authorizatorManager) {
		this._authorizatorManager = authorizatorManager;
	}
	
	private String _authName;
	private String _username;
	private IApsAuthorityManager _authorizatorManager;
	
}