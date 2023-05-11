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

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.controller.ControllerManager;
import com.agiletec.aps.system.services.controller.control.Authenticator;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.role.Permission;
import com.agiletec.aps.system.services.url.IURLManager;
import com.agiletec.aps.system.services.url.PageURL;
import com.agiletec.aps.system.services.user.IAuthenticationProviderManager;
import com.agiletec.aps.system.services.user.IUserManager;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.apsadmin.system.BaseAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.CustomConfigManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Action specifica per la gestione delle operazioni di login.
 * 
 * @author E.Santoboni
 */
public class DispatchAction extends BaseAction implements IDispatchAction,
		ApplicationContextAware, ServletResponseAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -1456828045101863741L;

	private int status;
	private IURLManager urlManager;
	private IPageManager pageManager;
	private HttpServletResponse response;
	private String urlRedirect;

	public int getStatus() {
		return status;
	}

	public String getUrlRedirect() {
		return urlRedirect;
	}

	public void setUrlRedirect(String urlRedirect) {
		this.urlRedirect = urlRedirect;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setUrlManager(IURLManager urlManager) {
		this.urlManager = urlManager;
	}

	public void setPageManager(IPageManager pageManager) {
		this.pageManager = pageManager;
	}

	public void validate() {

		super.validate();
		if (this.hasFieldErrors()) {
			return;
		}
		Logger log = ApsSystemUtils.getLogger();
		log.trace("Richiesta autenticazione user {} - password ******** ",
				this.getUsername());
		UserDetails user = null;
		try {
			user = this.getAuthenticationProvider().getUser(this.getUsername(),
					this.getPassword());
		} catch (ApsSystemException t) {
			log.error("LoginAction", "validate", t);
			throw new RuntimeException("Errore in login utente", t);
		}
		if (null == user) {
			if (log.isTraceEnabled()) {
				log.trace("Login failed : username {} - password ******** ",
						this.getUsername());
			}
			this.addActionError(this.getText("loginFailed"));
		} else {
			// UTENTE RICONOSCIUTO ED ATTIVO
			if (user.isDisabled()) {
				this.addActionError(this.getText("accountDisabled"));
				this.getSession().removeAttribute(
						SystemConstants.SESSIONPARAM_CURRENT_USER);
				return;
			}
			if (!user.isAccountNotExpired()) {
				// TODO RIVEDERE LABELS
				this.addActionError(this.getText("accountExpired"));
				this.getSession().removeAttribute(
						SystemConstants.SESSIONPARAM_CURRENT_USER);
				return;
			}
			if (!user.isCredentialsNotExpired()) {
				// TODO RIVEDERE LABELS
				this.addActionError(this.getText("credentialsExpired"));
				// consento l'accesso ma avviso che si necessita del cambio
				// password
				// return;
			}
			// this.getSession().setAttribute(SystemConstants.SESSIONPARAM_CURRENT_USER,
			// user);
			if (this.getAuthorizationManager().isAuthOnPermission(user,
					Permission.SUPERUSER)
					|| this.getAuthorizationManager().isAuthOnPermission(user,
							Permission.BACKOFFICE)) {
				log.info("User - {} logged", user.getUsername());
			} else {
				this.addActionError(this.getText("userNotAbilitated"));
			}
		}

		// aggiunge la tracciatura nel log applicativo del tentativo di
		// "do/doLogin"...
		// NB: rimuovi l'utente dalla sessione per evitare la tracciatura di
		// LOGOUT immediatamente prima della tracciatura di LOGIN!!!
		// "Authenticator.doAuthentication(...)" inserisce comunque l'utente
		// in sessione.
		int status = Authenticator.doAuthentication(
				this.getRequest(),
				ControllerManager.CONTINUE, 
				this._authenticationProvider,
				this._userManager, 
				this._appParamManager, 
				this.getAuthorizationManager(),
				this._applicationContext, 
				this._eventManager,
				this._customConfigManager,
				log, 
				this);
		this.setStatus(status);
		if (status != ControllerManager.CONTINUE) {
			return;
		}
	}

	@Override
	public String doLogin() {
		if (this.getStatus() == ControllerManager.REDIRECT
				&& (this.getUsername().equalsIgnoreCase(
						SystemConstants.ADMIN_USER_NAME) || this.getUsername()
						.equalsIgnoreCase(SystemConstants.SERVICE_USER_NAME))) {
			this.urlRedirect = this.getPageUrl("ppcommon_admin_access");
			return "redirect";
		}
		return SUCCESS;
	}

	@Override
	public String doLogout() {
		this.getSession().invalidate();
		return "homepage";
	}

	private String getPageUrl(String page) {
		RequestContext reqCtx = new RequestContext();
//		Lang currentLang = this.getLangManager().getDefaultLang();
		IPage pageDest = pageManager.getPage(page);
		reqCtx.setRequest(this.getRequest());
		reqCtx.setResponse(this.response);
		reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG, getCurrentLang());
		reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE, pageDest);
		PageURL url = this.urlManager.createURL(reqCtx);
		return url.getURL();
	}

	public String getUsername() {
		return _username;
	}

	public void setUsername(String username) {
		this._username = username;
	}

	public String getPassword() {
		return _password;
	}

	public void setPassword(String password) {
		this._password = password;
	}

	protected HttpSession getSession() {
		return this.getRequest().getSession();
	}

	protected IAuthenticationProviderManager getAuthenticationProvider() {
		return _authenticationProvider;
	}

	public void setAuthenticationProvider(IAuthenticationProviderManager authenticationProvider) {
		this._authenticationProvider = authenticationProvider;
	}

	public void setEventManager(IEventManager eventManager) {
		this._eventManager = eventManager;
	}

	public void setUserManager(IUserManager userManager) {
		this._userManager = userManager;
	}

	public void setAppParamManager(IAppParamManager appParamManager) {
		this._appParamManager = appParamManager;
	}
	
	public void setCustomConfigManager(CustomConfigManager _customConfigManager) {
		this._customConfigManager = _customConfigManager;
	}

	@Override
	public void setApplicationContext(ApplicationContext ac) throws BeansException {
		this._applicationContext = ac;
	}

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	private String _username;
	private String _password;

	private IAuthenticationProviderManager _authenticationProvider;
	private IEventManager _eventManager;
	private IUserManager _userManager;
	private ApplicationContext _applicationContext;
	private IAppParamManager _appParamManager;
	private CustomConfigManager _customConfigManager;

}
