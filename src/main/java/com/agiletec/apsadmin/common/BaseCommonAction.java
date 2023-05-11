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
package com.agiletec.apsadmin.common;

import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;

import org.apache.commons.lang.StringUtils;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.user.IUserManager;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.apsadmin.system.BaseAction;

/**
 * Action specifica per la gestione della password utente corrente.
 *
 * @version 1.0
 * @author E.Santoboni
 */
public class BaseCommonAction extends BaseAction implements IBaseCommonAction {

	private IEventManager eventManager;	
	private IUserManager _userManager;

	private String _oldPassword;
	private String _password;
	private String _passwordConfirm;
	private Boolean _administrator;
	

	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}

	protected IUserManager getUserManager() {
		return _userManager;
	}

	public void setUserManager(IUserManager userManager) {
		this._userManager = userManager;
	}

	public String getUsername() {
		return this.getCurrentUser().getUsername();
	}

	public String getOldPassword() {
		return _oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this._oldPassword = oldPassword;
	}

	public String getPassword() {
		return _password;
	}

	public void setPassword(String password) {
		this._password = password;
	}

	public String getPasswordConfirm() {
		return _passwordConfirm;
	}

	public void setPasswordConfirm(String passwordConfirm) {
		this._passwordConfirm = passwordConfirm;
	}

	public Boolean isAdministrator() {
		this._administrator = false;
		UserDetails currentUser = this.getCurrentUser();
		for(int i = 0; i < currentUser.getAuthorities().length; i++) {
			if(currentUser.getAuthorities()[i] instanceof Group) {
				_administrator = Group.ADMINS_GROUP_NAME.equals(((Group) currentUser.getAuthorities()[i]).getName());
				break;
			}
		}		
		return _administrator;
	}

	/**
	 * validazione della password
	 */
	@Override
	public void validate() {
		super.validate();
		if (this.getFieldErrors().isEmpty()) {
			try {
				UserDetails currentUser = this.getCurrentUser();
				
				String error = this.getUserManager().validatePassword(currentUser, this.getPassword());
				if(StringUtils.isNotEmpty(error)) {
					this.addFieldError("username", this.getText(error));
				}
				
				if (!currentUser.isJapsUser()) {
					//TODO Label da revisionare
					this.addFieldError("username", this.getText("MESSAGGIO_UTENTE_NON_LOCALE DI JAPS"));
				} else if (null == this.getUserManager().getUser(currentUser.getUsername(), this.getOldPassword())) {
					//TODO Label da revisionare
					this.addFieldError("oldPassword", this.getText("Message.wrongOldPassword"));
				} else if (StringUtils.getLevenshteinDistance(this.getPassword().trim(), currentUser.getUsername().trim()) < 3) {
					this.addFieldError("password", this.getText("passwordMatchUsername"));
				} else if (!this.getUserManager().isPasswordNew(currentUser.getUsername(), this.getPassword())) {
					this.addFieldError("password", this.getText("passwordUserInThePast"));
				}
			} catch (ApsSystemException e) {
				throw new RuntimeException("Errore in estrazione utente", e);
			}
		}
	}

	/**
	 * visualizza la pagina del cambio password 
	 */
	@Override
	public String editPassword() {
		this._administrator = this.isAdministrator();
		return SUCCESS;
	}

	/**
	 * modifica la password 
	 */
	@Override
	public String changePassword() {
		String target = SUCCESS;
		Event evento = new Event();
		evento.setUsername(this.getUsername());
		evento.setSessionId(this.getRequest().getSession().getId());
		evento.setIpAddress(this.getCurrentUser().getIpAddress());
		evento.setEventType(PortGareEventsConstants.CAMBIO_PASSWORD);
		evento.setLevel(Event.Level.INFO);
		evento.setMessage("Modifica password");
		try {
			this.getUserManager().changePassword(this.getUsername(), this.getPassword());
		} catch (Exception t) {
			evento.setError(t);
			ApsSystemUtils.logThrowable(t, this, "changePassword");
			target = INPUT;
		} catch (Throwable t) {
			evento.setError(t);
			ApsSystemUtils.logThrowable(t, this, "changePassword");
			target = FAILURE;
		} finally{
			eventManager.insertEvent(evento);
		}
		return target;
	}

}
