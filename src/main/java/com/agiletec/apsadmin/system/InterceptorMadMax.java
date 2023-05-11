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

import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.user.UserDetails;
import com.opensymphony.xwork2.ActionInvocation;

/**
 * Interceptor gestore della verifica delle autorizzazioni dell'utente corrente.
 * Verifica che l'utente corrente sia abilitato all'esecuzione dell'azione richiesta.
 * @version 1.0
 * @author E.Santoboni
 */
public class InterceptorMadMax extends BaseInterceptorMadMax {
	
	/**
	 * Restituisce il permesso specifico.
	 * @return Il permesso specifico.
	 */
	public String getRequiredPermission() {
		return _requiredPermission;
	}
	
	/**
	 * Setta il permesso specifico.
	 * @param requiredPermission Il permesso specifico.
	 */
	public void setRequiredPermission(String requiredPermission) {
		this._requiredPermission = requiredPermission;
	}
	
	public String getErrorResultName() {
		if (this._errorResultName == null) {
			return DEFAULT_ERROR_RESULT;
		}
		return this._errorResultName;
	}
	
	public void setErrorResultName(String errorResultName) {
		this._errorResultName = errorResultName;
	}
	
	@Override
	protected String invoke(ActionInvocation invocation) throws Exception {
		Logger log = ApsSystemUtils.getLogger();
		if (log.isInfoEnabled()) {
			HttpSession session = ServletActionContext.getRequest().getSession();
			UserDetails currentUser = (UserDetails) session.getAttribute(SystemConstants.SESSIONPARAM_CURRENT_USER);
			String message = "Invocata azione '" + invocation.getProxy().getActionName() + "' su namespace '" 
				+ invocation.getProxy().getNamespace() + "' da parte di utente '" + currentUser.getUsername() + "'";
			log.info(message);
		}
		return super.invoke(invocation);
	}
	
	private String _requiredPermission;
	
	private String _errorResultName;
	
	public static final String DEFAULT_ERROR_RESULT = "userNotAllowed";
	
}
