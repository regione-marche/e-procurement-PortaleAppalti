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

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.user.IUserManager;
import com.agiletec.aps.util.ApsWebApplicationUtils;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.commons.lang.StringUtils;

/**
 * Interceptor per la gestione del cambio password. L'interceptor inserisce a db
 * nella tabella ppcommon_passwords ogni modifica subita dalle password utente.
 * In questo modo la funzionalità di cambio password può controllare che non si
 * stia inserendo una password già precedentemente utilizzata La coppia
 * username-passowrd dev'essere univoca
 *
 * @version 1.0
 * @author Marco Perazzetta
 */
public class ChangePasswordInterceptor extends AbstractInterceptor {

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		String result = invocation.invoke();
		//Loggo il cambio password se e' andato a buon fine
		if (result.equals(BaseAction.SUCCESS)) {
			String password = request.getParameter("password");
			String username = request.getParameter("username");
			if (StringUtils.isNotBlank(password) && StringUtils.isNotBlank(username) && !SystemConstants.ADMIN_USER_NAME.equals(username)) {
				try {
					IUserManager userManager = (IUserManager) ApsWebApplicationUtils.getBean(SystemConstants.USER_MANAGER, ServletActionContext.getRequest());
					userManager.logChangePassword(username, password);
				} catch (ApsSystemException t) {
					ApsSystemUtils.logThrowable(t, this, "logChangePassword");
				}
			}
		}
		return result;
	}
}
