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
package com.agiletec.aps.system.services.user;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.SpringAppContext;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.TrackerSessioniUtenti;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionEvent;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * Rappresentazione di un'utente.
 *
 * @author M.Diana - E.Santoboni
 */
public class User extends AbstractUser implements HttpSessionBindingListener, HttpSessionActivationListener, Serializable {
	
	/**
	 * Indica se l'utente e' definito localmente all'interno del db "serv" di jAPS.
	 *
	 * @return true se e' un'utente locale di jAPS, false se e' un'utente definito
	 * in altra base dati.
	 */
	public boolean isJapsUser() {
		return true;
	}

	/**
	 * Crea una copia dell'oggetto user e lo restituisce.
	 *
	 * @return Oggetto di tipo User clonato.
	 */
	public Object clone() {
		User cl = new User();
		cl.setUsername(this.getUsername());
		cl.setPassword("");

		cl.setAuthorities(this.getAuthorities());
		return cl;
	}
	
	/**
	 * @return the _delegateUser
	 */
	public String getDelegateUser() {
		return _delegateUser;
	}

	/**
	 * @param _delegateUser the _delegateUser to set
	 */
	public void setDelegateUser(String _delegateUser) {
		this._delegateUser = _delegateUser;
	}

	public Date getCreationDate() {
		return _creationDate;
	}

	protected void setCreationDate(Date creationDate) {
		this._creationDate = creationDate;
	}

	public Date getLastAccess() {
		return _lastAccess;
	}

	public void setLastAccess(Date lastAccess) {
		this._lastAccess = lastAccess;
	}

	public Date getLastPasswordChange() {
		return _lastPasswordChange;
	}

	public void setLastPasswordChange(Date lastPasswordChange) {
		this._lastPasswordChange = lastPasswordChange;
	}

	@Override
	public boolean isDisabled() {
		return _disabled;
	}

	public void setDisabled(boolean disabled) {
		this._disabled = disabled;
	}

	public boolean isCheckCredentials() {
		return _checkCredentials;
	}

	public void setCheckCredentials(boolean checkCredentials) {
		this._checkCredentials = checkCredentials;
	}

	public int getMaxMonthsSinceLastAccess() {
		return _maxMonthsSinceLastAccess;
	}

	public void setMaxMonthsSinceLastAccess(int maxMonthsSinceLastAccess) {
		this._maxMonthsSinceLastAccess = maxMonthsSinceLastAccess;
	}

	public int getMaxMonthsSinceLastPasswordChange() {
		return _maxMonthsSinceLastPasswordChange;
	}

	public void setMaxMonthsSinceLastPasswordChange(int maxMonthsSinceLastPasswordChange) {
		this._maxMonthsSinceLastPasswordChange = maxMonthsSinceLastPasswordChange;
	}

	public String getCrc() {
		return _crc;
	}

	public void setCrc(String crc) {
		this._crc = crc;
	}

	@Override
	public Integer getAcceptanceVersion() {
		return _acceptanceVersion;
	}

	@Override
	public void setAcceptanceVersion(Integer acceptanceVersion) {
		this._acceptanceVersion = (acceptanceVersion != 0 ? acceptanceVersion : null);
	}

	@Override
	public String getIpAddress() {
		return _ipAddress;
	}

	@Override
	public void setIpAddress(String ipAddress) {
		this._ipAddress = ipAddress;
	}

	@Override
	public String getLogoutTime() {
		return this._logoutTime;
	}

	@Override
	public void setLogoutTime(String logout) {
		this._logoutTime = logout;
	}
	
	@Override
	public String getSessionId() {
		return this._sessionId;
	}

	@Override
	public void setSessionId(String id) {
		this._sessionId = id;
	}
	

	@Override
	public boolean isAccountNotExpired() {
		
		if (!this.isCheckCredentials() || SystemConstants.ADMIN_USER_NAME.equals(this.getUsername()) || SystemConstants.ENTE_ADMIN_USER_NAME.equals(this.getUsername()) || SystemConstants.SERVICE_USER_NAME.equals(this.getUsername())) {
			return true;
		}
		int maxDelay = this.getMaxMonthsSinceLastAccess();
		if (maxDelay > 0) {
			Date dateForCheck = (this.getLastAccess() != null ? this.getLastAccess() : this.getCreationDate());
			if (null != dateForCheck) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dateForCheck);
				cal.add(Calendar.MONTH, maxDelay);
				Date expirationDate = cal.getTime();
				return expirationDate.after(new Date());
			}
		}
		return super.isAccountNotExpired();
	}

	@Override
	public boolean isCredentialsNotExpired() {
		
		if (!this.isCheckCredentials() || SystemConstants.ADMIN_USER_NAME.equals(this.getUsername())) {
			// solo l'amministratore "admin" non ha necessita' di cambiare la password, tutti gli altri utenti si
			return true;
		}
		int maxDelay = this.getMaxMonthsSinceLastPasswordChange();
		if (maxDelay > 0) {
			Date dateForCheck = (this.getLastPasswordChange() != null ? this.getLastPasswordChange() : this.getCreationDate());
			if (null != dateForCheck) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dateForCheck);
				cal.add(Calendar.MONTH, maxDelay);
				Date expirationDate = cal.getTime();
				return expirationDate.after(new Date());
			}
		}
		return super.isCredentialsNotExpired();
	}

	@Override
	public boolean isLoginAlive() {
		boolean alive = false;
		try {
			ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
			IAuthenticationProviderManager authenticationProvider = (IAuthenticationProviderManager) ctx
					.getBean("AuthenticationProviderManager");
		  
			UserDetails u = authenticationProvider.getUser(this);
			if(u != null)  {
				String sessionid = (StringUtils.isNotEmpty(this._sessionId) ? this._sessionId : "");
				alive = StringUtils.isEmpty(u.getLogoutTime()) && sessionid.equals(u.getSessionId()); 
			}
		} catch (Exception e) {
			// traccia qualcosa ...
		}	
		return alive;
	}
	
	@Override
	public void valueBound(HttpSessionBindingEvent hsbe) {
	}

	@Override
	public void valueUnbound(HttpSessionBindingEvent hsbe) {
		if (!this.getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			boolean logoutDone = true;
			
			// utente autenticatosi direttamente e non mediante single sign on
			Event evento = new Event();
			evento.setUsername(this.getUsername());
			evento.setSessionId(hsbe.getSession().getId());
			evento.setLevel(Event.Level.INFO);
			evento.setEventType(PortGareEventsConstants.LOGOUT);
			evento.setIpAddress(this.getIpAddress());
			evento.setMessage("Disconnessione utente");

			ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
			IEventManager eventManager = (IEventManager) ctx.getBean("EventManager");
			
			try {
				if (!isAdmin(getUsername())) {
					IAuthenticationProviderManager manager = (IAuthenticationProviderManager) ctx.getBean("AuthenticationProviderManager");
					logoutDone = manager.logLogout(this.getUsername(), this.getIpAddress(), hsbe.getSession().getId());
				}
				// registra l'evento di LOGOUT solo se l'utente era loggato
				// dallo stesso pc da cui viene fatta la chiamata !!!
				if(logoutDone) {
					eventManager.insertEvent(evento);
				}
			} catch (Throwable e) {
				evento.setError(e);
				ApsSystemUtils.logThrowable(e, this, "service", "Error, could not fulfill the request");
			}

			//31/03/2017: si rimuove l'utente disconnesso dalla lista contenuta nell'applicativo
			// nel caso di disconnessione di un utente di single sign on la seguente istruzione viene eseguita sia
			// qui sia nella rimozione dell'oggetto AccountSSO, la seconda delle due non rimuovera' nulla pertanto
			// e' ininfluente
			if(logoutDone) { 
			    ServletContext context = hsbe.getSession().getServletContext();
			    TrackerSessioniUtenti trackerSessioni = TrackerSessioniUtenti.getInstance(context);
				trackerSessioni.removeSessioneUtente(hsbe.getSession().getId());
			}
		}
	}

	private boolean isAdmin(String username) {
		return username.equals(SystemConstants.ADMIN_USER_NAME) || username.equals(SystemConstants.SERVICE_USER_NAME);
	}

	@Override
	public void sessionDidActivate(HttpSessionEvent event) {
		ServletContext context = event.getSession().getServletContext();
		TrackerSessioniUtenti trackerSessioni = TrackerSessioniUtenti.getInstance(context);
		String[] datiUtente = trackerSessioni.getDatiSessioniUtentiConnessi().get(event.getSession().getId());
		if (datiUtente == null && !SystemConstants.GUEST_USER_NAME.equals(this.getUsername())) {
			// vuol dire che non ho caricato i dati dell'utente SSO perche' questo se esiste ha priorita' massima
			trackerSessioni.putSessioneUtente(
					event.getSession(), 
					this.getIpAddress(), 
					this.getUsername(), 
					DateFormatUtils.format(new Date(event.getSession().getCreationTime()), "dd/MM/yyyy HH:mm:ss"));
		}
	}

	@Override
	public void sessionWillPassivate(HttpSessionEvent event) {
		ServletContext context = event.getSession().getServletContext();
		TrackerSessioniUtenti trackerSessioni = TrackerSessioniUtenti.getInstance(context);
		trackerSessioni.removeSessioneUtente(event.getSession().getId());
	}
	
	private String _delegateUser;
	private Date _creationDate;			// CAMBIARE IN REGISTRATION DATE
	private Date _lastAccess;
	private Date _lastPasswordChange;

	private boolean _disabled;

	private boolean _checkCredentials;

	private int _maxMonthsSinceLastAccess = -1;
	private int _maxMonthsSinceLastPasswordChange = -1;

	private String _ipAddress;
	
	private String _crc;
	
	private Integer _acceptanceVersion;

	private String _logoutTime;	
	private String _sessionId;
	
}
