/*
*
* Copyright 2008 AgileTec s.r.l. (http://www.agiletec.it) All rights reserved.
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
* Copyright 2008 AgileTec s.r.l. (http://www.agiletec.it) All rights reserved.
*
*/
package com.agiletec.plugins.jpuserreg.aps.externalFramework.actions.userreg.recover;

import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event.Level;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.user.IUserManager;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.apsadmin.system.BaseAction;
import com.agiletec.plugins.jpuserreg.aps.system.services.userreg.IUserRegManager;

/**
 * Struts Action for managing requests for password recover by username
 * @author G.Cocco
 * */
public class UserRecoverFromUsernameAction extends BaseAction implements IUserRecoverFromUsernameAction {
	
	 private IEventManager eventManager;
	    
    /**
	 * @param eventManager the eventManager to set
	 */
	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}
		
	@Override
	public String initRecover() {
		UserDetails userDetails = this.getCurrentUser();
		if (null != userDetails && !userDetails.getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			return "loggedUser";
		}
		return SUCCESS;
	}
	
	/**
	 * Password recover from username
	 * */
	@Override
	public String recoverFromUsername() {
		String target = SUCCESS;
		Event evento = new Event();
		String ipAddress = this.getRequest().getHeader("X-FORWARDED-FOR");
		if (ipAddress == null) {
			ipAddress = this.getRequest().getRemoteAddr();
		}
		evento.setIpAddress(ipAddress);
		evento.setEventType(PortGareEventsConstants.RECUPERO_PASSWORD);
		evento.setLevel(Event.Level.INFO);
		evento.setMessage("Recupero password via username (" + this.getUsername() + ")");
		try {
			UserDetails user = this.getUserManager().getUser(this.getUsername());
			if (user != null) {
				// si corregge immediatamente in caso di match case insensitive
				this.setUsername(user.getUsername());
				evento.setUsername(this.getUsername());
				
				boolean continua = true;
				if (user.isDisabled()) {
					continua = false;
					//this.addActionError(this.getText("Errors.userRecover.userDisabled"));
					evento.setLevel(Level.ERROR);
					evento.setDetailMessage("Utenza disabilitata");
					//target = FAILURE;
				} else if (!user.isAccountNotExpired()) {
					// NB: anche se l'utente risulta scaduto, consenti comunque il recupero password
					//     in questo modo si permette agli operatori di recuparare in autonomia l'accesso
					//continua = false;		 
					//this.addActionError(this.getText("Errors.userRecover.accountExpired"));
					evento.setLevel(Event.Level.WARNING);
					evento.setDetailMessage("Utenza scaduta");
					//target = FAILURE;
				}
				if (continua) {
					// 23/11/2016: SOLO SE non ci sono errori allora si invia il token
					this.getUserRegManager().reactivationByUserName(this.getUsername());
				}
			} else {
				//this.addActionError(this.getText("Errors.userRecover.userNotFound"));
				evento.setLevel(Event.Level.ERROR);
				evento.setDetailMessage("Username non trovato");
				//target = FAILURE;
			}
		} catch (Throwable t) {
			this.addActionError(this.getText("Errors.userRecover.genericError"));
			ApsSystemUtils.logThrowable(t, this, "recoverFromUsername");
			evento.setError(t);
			target = FAILURE;
		} finally {
			eventManager.insertEvent(evento);
		}
		
		return target;
	}
	
	public void setUsername(String username) {
		this._username = username;
	}
	public String getUsername() {
		return _username;
	}
	
	public void setUserRegManager(IUserRegManager userRegManager) {
		this._userRegManager = userRegManager;
	}
	protected IUserRegManager getUserRegManager() {
		return _userRegManager;
	}

	public void setUserManager(IUserManager userManager) {
		this._userManager = userManager;
	}
	protected IUserManager getUserManager() {
		return _userManager;
	}

	private String _username;
	private IUserRegManager _userRegManager;
	private IUserManager _userManager;
	
}