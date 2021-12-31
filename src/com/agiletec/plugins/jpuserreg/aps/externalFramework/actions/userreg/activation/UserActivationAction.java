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
package com.agiletec.plugins.jpuserreg.aps.externalFramework.actions.userreg.activation;

import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.plugins.jpuserreg.aps.JpUserRegSystemConstants;
import com.agiletec.plugins.jpuserreg.aps.externalFramework.actions.userreg.common.UserRegBaseAction;
import com.agiletec.plugins.jpuserreg.aps.system.services.userreg.IUserRegManager;

/**
 * Struts Action to manage account activation
 * @author S.Puddu
 * @author E.Mezzano
 * @author G.Cocco
 * */
public class UserActivationAction extends UserRegBaseAction implements IUserActivationAction, SessionAware {

	private IEventManager eventManager;

	/**
	 * @param eventManager the eventManager to set
	 */
	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}

	@Override
	public String initActivation() {
		try {
			Boolean alreadyActivated = 
				(Boolean) this.getRequest().getSession().getAttribute(JpUserRegSystemConstants.USER_REG_ALREADY_EXECUTED_ACTIVATION);
			if (null != alreadyActivated && alreadyActivated) {
				this.sendHomeRedirect();
				return null;
			}
			String username = this.extractUsername();
			if (username==null || username.length()==0 ) {
				return "activationError";
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "activationAccountInit");
			return FAILURE;
		}
		return SUCCESS;
	}

	@Override
	public String activate() {
		String target = SUCCESS;
		Event evento = new Event();
		String ipAddress = this.getRequest().getHeader("X-FORWARDED-FOR");
		if (ipAddress == null) {
			ipAddress = this.getRequest().getRemoteAddr();
		}
		
		evento.setIpAddress(ipAddress);
		evento.setEventType(PortGareEventsConstants.PROCESSA_TOKEN);
		evento.setLevel(Event.Level.INFO);
		evento.setMessage("Utilizzo token attivazione " + this.getToken());
		try {
			String username = this.extractUsername();
			if (username==null || username.length()==0) {
				target = "activationError";
				evento.setLevel(Event.Level.ERROR);
				evento.setDetailMessage("Token non valido, impossibile estrarre lo username");
			} else {
				evento.setUsername(username);
				this.getUserRegManager().activateUser(username, this.getPassword(), this.getToken());
				this.getRequest().getSession().setAttribute(JpUserRegSystemConstants.USER_REG_ALREADY_EXECUTED_ACTIVATION, true);
			}
		} catch (Exception e) {
			ApsSystemUtils.logThrowable(e, this, "activationAccount");
			target = INPUT;
			evento.setError(e);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "activationAccount");
			target = FAILURE;
			evento.setError(t);
		} finally {
			eventManager.insertEvent(evento);
		}
		return target;
	}

	
	@Override
	public String initReactivation() {
		try {
			Boolean alreadyActivated = (Boolean) this.getRequest().getSession().getAttribute(JpUserRegSystemConstants.USER_REG_ALREADY_EXECUTED_REACTIVATION);
			if (null != alreadyActivated && alreadyActivated) {
				this.sendHomeRedirect();
				return null;
			}
			String username = this.extractUsername();
			if (username==null || username.length()==0 ) {
				return "activationError";
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "activationAccountInit");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	@Override
	public String reactivate() {
		String target = SUCCESS;
		Event evento = new Event();
		String ipAddress = this.getRequest().getHeader("X-FORWARDED-FOR");
		if (ipAddress == null) {
			ipAddress = this.getRequest().getRemoteAddr();
		}
		evento.setIpAddress(ipAddress);
		evento.setEventType(PortGareEventsConstants.PROCESSA_TOKEN);
		evento.setLevel(Event.Level.INFO);
		evento.setMessage("Utilizzo token riattivazione " + this.getToken());
		try {
			String username = this.extractUsername();
			if (username==null || username.length()==0) {
				target = "activationError";
				evento.setLevel(Event.Level.ERROR);
				evento.setDetailMessage("Token non valido, impossibile estrarre lo username");
			} else {
				evento.setUsername(username);
				this.getUserRegManager().reactivateUser(username, this.getPassword(), this.getToken());
				this.getRequest().getSession().setAttribute(JpUserRegSystemConstants.USER_REG_ALREADY_EXECUTED_REACTIVATION, true);
			}
		} catch (Exception e) {
			ApsSystemUtils.logThrowable(e, this, "reactivationAccount");
			target = INPUT;
			evento.setError(e);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "reactivationAccount");
			target = FAILURE;
			evento.setError(t);
		} finally {
			eventManager.insertEvent(evento);
		}
		return target;
	}

	private String extractUsername() {
		String token = this.getToken();
		String username = null;
		if (null!=token && token.length()>0) {
			username = this.getUserRegManager().getUsernameFromToken(token);
		}
		this.setUsername(username);
		return username;
	}

	public void setPassword(String password) {
		this._password = password;
	}
	
	public String getPassword() {
		return _password;
	}

	public void setPasswordConfirm(String passwordConfirm) {
		this._passwordConfirm = passwordConfirm;
	}
	
	public String getPasswordConfirm() {
		return _passwordConfirm;
	}

	public void setToken(String token) {
		this._token = token;
	}
	
	public String getToken() {
		return _token;
	}

	public void setUsername(String username) {
		this._username = username;
	}
	
	public String getUsername() {
		return this._username;
	}

	public void setUserRegManager(IUserRegManager userRegManager) {
		this._userRegManager = userRegManager;
	}
	
	protected IUserRegManager getUserRegManager() {
		return _userRegManager;
	}
	
	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
		
	}
	
	private String _password;
	private String _passwordConfirm;
	private String _token;
	private String _username;	
	private IUserRegManager _userRegManager;
	private Map<String, Object> session;

}