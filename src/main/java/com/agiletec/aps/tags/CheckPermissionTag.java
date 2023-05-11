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
package com.agiletec.aps.tags;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.authorization.IAuthorizationManager;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.role.Permission;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.aps.util.ApsWebApplicationUtils;

/**
 * Tag di utilità per i menù a scomparsa.
 * Visualizza il corpo del tag se l'utente corrente possiede 
 * il permesso specificato o appartiene al gruppo specificato come attributo del tag.
 * @version 1.0
 * @author E.Santoboni
 */
public class CheckPermissionTag extends TagSupport {
	
	public int doStartTag() throws JspException {
		HttpSession session = this.pageContext.getSession();
		try {
			boolean isAuthorized = false;
			UserDetails currentUser = (UserDetails) session.getAttribute(SystemConstants.SESSIONPARAM_CURRENT_USER);
			IAuthorizationManager authManager = (IAuthorizationManager) ApsWebApplicationUtils.getBean(SystemConstants.AUTHORIZATION_SERVICE, this.pageContext);
			boolean isGroupSetted = (this.getGroupName() != null && this.getGroupName().length()>0);
			boolean isPermissionSetted = (this.getPermission() != null && this.getPermission().length()>0);
			
			boolean isAuthGr = !isGroupSetted || authManager.isAuthOnGroup(currentUser, this.getGroupName()) || authManager.isAuthOnGroup(currentUser, Group.ADMINS_GROUP_NAME);
			boolean isAuthPerm = !isPermissionSetted || authManager.isAuthOnPermission(currentUser, this._permission) || authManager.isAuthOnPermission(currentUser, Permission.SUPERUSER);
			
			isAuthorized = isAuthGr && isAuthPerm;
			if (null != this.getVar()) {
				this.pageContext.setAttribute(this.getVar(), new Boolean(isAuthorized));
			}
			if (isAuthorized) {
				return EVAL_BODY_INCLUDE;
			} else {
				return SKIP_BODY;
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "doStartTag");
			throw new JspException("Error during tag initialization ", t);
		}
	}
	
	/**
	 * Restituisce il permesso richiesto.
	 * @return Il permesso richiesto.
	 */
	public String getPermission() {
		return _permission;
	}
	
	/**
	 * Setta il permesso richiesto.
	 * @param permission Il permesso richiesto.
	 */
	public void setPermission(String permission) {
		this._permission = permission;
	}
	
	/**
	 * Restituisce il nome del gruppo richiesto.
	 * @return Il nome del gruppo richiesto.
	 */
	public String getGroupName() {
		return _groupName;
	}
	
	/**
	 * Setta il nome del gruppo richiesto.
	 * @param groupName Il nome del gruppo richiesto.
	 */
	public void setGroupName(String groupName) {
		this._groupName = groupName;
	}
			
	/**
	 * Setta il nome del parametro tramite il quale settare nella request 
	 * il buleano rappresentativo del risultato del controllo di autorizzazione.
	 * @param resultParamName Il nome del parametro.
	 */
	public void setVar(String var) {
		this._var = var;
	}

	/**
	 * Restituisce il nome del parametro tramite il quale settare nella request 
	 * il buleano rappresentativo del risultato del controllo di autorizzazione.
	 * @return Il nome del parametro.
	 */
	public String getVar() {
		return _var;
	}

	private String _permission;
	private String _groupName;
	private String _var;
	
}
