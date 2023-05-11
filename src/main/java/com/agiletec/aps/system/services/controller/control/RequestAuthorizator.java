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
package com.agiletec.aps.system.services.controller.control;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.authorization.IAuthorizationManager;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.aps.system.services.controller.ControllerManager;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.user.UserDetails;

/**
 * Sottoservizio delegato al controllo dell'autorizzazione dell'utente corrente.
 * Esegue la verifica dell'autorizzazione all'accesso alla pagina richiesta da 
 * parte dell'utente corrente. Nel caso di richiesta non valida, il controllo 
 * imposta il redirect alla pagina di login.
 * @author M.Diana
 */
public class RequestAuthorizator extends AbstractControlService {

	public void afterPropertiesSet() throws Exception {
		this._loginPageCode = this.getConfigManager().getParam("loginPageCode");
		this._log.debug(this.getClass().getName() + ": initialized");
	}

	/**
	 * Verifica che l'utente in sessione sia abilitato all'accesso alla pagina richiesta.
	 * Se è autorizzato il metodo termina con CONTINUE, altrimenti 
	 * con REDIRECT impostando prima i parametri di redirezione alla pagina di login.
	 * @param reqCtx Il contesto di richiesta
	 * @param status Lo stato di uscita del servizio precedente
	 * @return Lo stato di uscita
	 */
	public int service(RequestContext reqCtx, int status) {
		if (_log.isTraceEnabled()) {
			_log.trace("Invoked: " + this.getClass().getName());
		}
		int retStatus = ControllerManager.INVALID_STATUS;
		if (status == ControllerManager.ERROR) {
			return status;
		}
		try {
			HttpServletRequest req = reqCtx.getRequest();
			HttpSession session = req.getSession();
			IPage currentPage = (IPage) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE);
			UserDetails currentUser = (UserDetails) session.getAttribute(SystemConstants.SESSIONPARAM_CURRENT_USER);
			String pageGroup = currentPage.getGroup();
			boolean authorized = this.checkUserPermission(currentUser, pageGroup);
			if (authorized) {
				retStatus = ControllerManager.CONTINUE;
			} else {
				retStatus = this.redirect(this.getLoginPageCode(), reqCtx);
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "service", "Error while processing the request");
			retStatus = ControllerManager.ERROR;
		}
		return retStatus;
	}

	/**
	 * Verifica se l'utente corrente possiede l'autorizzazione per l'accesso alla pagina richiesta.
	 * @param user L'utente da controllare.
	 * @param pageGroup Il nome del permesso specifico per l'accesso alla pagina.
	 * @return true se l'utente è autorizzato ad accedere alla pagina richiesta.
	 */
	protected boolean checkUserPermission(UserDetails user, String pageGroup) {
		if (Group.FREE_GROUP_NAME.equals(pageGroup)) return true;
		IAuthorizationManager authManager = this.getAuthManager();
		boolean isAuthorized = authManager.isAuthOnGroup(user, pageGroup) || authManager.isAuthOnGroup(user, Group.ADMINS_GROUP_NAME);
		return isAuthorized;
	}

	protected String getLoginPageCode() {
		return this._loginPageCode;
	}

	protected ConfigInterface getConfigManager() {
		return _configManager;
	}
	public void setConfigManager(ConfigInterface configService) {
		this._configManager = configService;
	}

	protected IAuthorizationManager getAuthManager() {
		return _authManager;
	}
	public void setAuthManager(IAuthorizationManager authManager) {
		this._authManager = authManager;
	}

	private String _loginPageCode;
	private IAuthorizationManager _authManager;
	private ConfigInterface _configManager;

}