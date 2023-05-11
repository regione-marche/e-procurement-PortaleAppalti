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
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;

import java.util.Iterator;
import java.util.List;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.common.entity.ApsEntityManager;
import com.agiletec.aps.system.common.entity.model.EntitySearchFilter;
import com.agiletec.aps.system.services.user.IUserManager;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.apsadmin.system.BaseAction;
import com.agiletec.plugins.jpuserprofile.aps.system.services.profile.IUserProfileManager;
import com.agiletec.plugins.jpuserreg.aps.system.services.userreg.IUserRegManager;

/**
 * Struts Action for managing requests for password recover by email
 *
 * @author G.Cocco
 *
 */
public class UserRecoverFromEmailAction extends BaseAction implements IUserRecoverFromEmailAction {

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
	 * Password recover from email
	 *
	 */
	@Override
	public String recoverFromEmail() {
		String target = SUCCESS;
		Event evento = new Event();
		String ipAddress = this.getRequest().getHeader("X-FORWARDED-FOR");
		if (ipAddress == null) {
			ipAddress = this.getRequest().getRemoteAddr();
		}
		evento.setIpAddress(ipAddress);
		evento.setEventType(PortGareEventsConstants.RECUPERO_PASSWORD);
		evento.setLevel(Event.Level.INFO);
		evento.setMessage("Recupero password via mail (" + this.getEmail() + ")");
		try {
			// per evitare di modificare AbstractEntitySearcherDAO,
			// non potendo utilizzare una modalita' case insensitive,
			// si cerca su email in lower e upper case !!!
			EntitySearchFilter[] filters = {new EntitySearchFilter(this.getUserRegManager().getUserRegConfig().getProfileEMailAttr(), true, this.getEmail().toLowerCase(), false)};
			List<String> usernames = ((ApsEntityManager) this.getUserProfileManager()).searchId(filters);
			Iterator usernamesIter = usernames.iterator();
			
			if ( !usernamesIter.hasNext() ) {
				// ...se non viene trovata una mail lower case,
				// si cerca in uppercase...
				EntitySearchFilter[] f = {new EntitySearchFilter(this.getUserRegManager().getUserRegConfig().getProfileEMailAttr(), true, this.getEmail().toUpperCase(), false)};
				usernames = ((ApsEntityManager) this.getUserProfileManager()).searchId(f);
				usernamesIter = usernames.iterator();
			}
			
			String username = null;
			if (usernamesIter.hasNext()) {
				username = (String) usernamesIter.next();
				evento.setUsername(username);
				UserDetails user = this.getUserManager().getUser(username);
				boolean continua = true;
				if (user.isDisabled()) {
					continua = false;
					//this.addActionError(this.getText("Errors.userRecover.userDisabled"));
					evento.setLevel(Event.Level.ERROR);
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
					this.getUserRegManager().reactivationByUserName(username);
				}
			} else {
				//this.addActionError(this.getText("Errors.userRecover.mail.notFound"));
				evento.setLevel(Event.Level.ERROR);
				evento.setDetailMessage("Email non trovata");
				//target = FAILURE;
			}
		} catch (Throwable t) {
			this.addActionError(this.getText("Errors.userRecover.genericError"));
			ApsSystemUtils.logThrowable(t, this, "recoverFromEmail");
			evento.setError(t);
			target = FAILURE;
		} finally {
			eventManager.insertEvent(evento);
		}
		return target;
	}

	public void setEmail(String email) {
		this._email = email;
	}

	public String getEmail() {
		return _email;
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

	public void setUserProfileManager(IUserProfileManager userProfileManager) {
		this._userProfileManager = userProfileManager;
	}

	protected IUserProfileManager getUserProfileManager() {
		return _userProfileManager;
	}

	private String _email;
	private IUserRegManager _userRegManager;
	private IUserManager _userManager;
	private IUserProfileManager _userProfileManager;

}
