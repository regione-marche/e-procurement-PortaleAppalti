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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.BeanComparator;

import com.agiletec.aps.system.services.authorization.IAuthorizationManager;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.group.IGroupManager;
import com.agiletec.aps.system.services.lang.ILangManager;
import com.agiletec.aps.system.services.user.UserDetails;

/**
 * Base Action Helper for all helper class of the system.
 * @version 1.0
 * @author E.Santoboni
 */
public abstract class BaseActionHelper {
	
	/**
	 * Returns the list of authorized groups to a user.
	 * @param user The user.
	 * @return The list of authorized groups to a user.
	 */
	protected List<Group> getAllowedGroups(UserDetails user) {
		List<Group> groups = new ArrayList<Group>();
		List<Group> groupsOfUser = this.getAuthorizationManager().getGroupsOfUser(user);
		boolean isAdministrator = false;
		Iterator<Group> iter = groupsOfUser.iterator();
    	while (iter.hasNext()) {
    		Group group = iter.next();
    		if (group.getName().equals(Group.ADMINS_GROUP_NAME)) {
    			isAdministrator = true;
    			break;
    		}
    	}
		if (isAdministrator) {
			Collection<Group> collGroup = this.getGroupManager().getGroups();
			groups.addAll(collGroup);
		} else {
			groups.addAll(groupsOfUser);
		}
		BeanComparator comparator = new BeanComparator("descr");
		Collections.sort(groups, comparator);
    	return groups;
    }
	
	/**
	 * Metodo di servizio utilizzato per uniformare i codici.
	 * Elimina i caratteri non compresi tra "a" e "z" e tra "0" e "9" dalla stringa immessa.
	 * @param code La stringa da analizzare.
	 * @return La stringa corretta.
	 */
	public static String purgeString(String code) {
		code = code.trim().toLowerCase();
		code = code.replaceAll("[^ _a-z0-9]", "");
		code = code.trim().replace(' ', '_');
		return code;
    }
	
	protected IGroupManager getGroupManager() {
		return _groupManager;
	}
	public void setGroupManager(IGroupManager groupManager) {
		this._groupManager = groupManager;
	}
	
	protected ILangManager getLangManager() {
		return _langManager;
	}
	public void setLangManager(ILangManager langManager) {
		this._langManager = langManager;
	}
	
	@Deprecated
	protected IAuthorizationManager getAuthorizatorManager() {
		return _authorizationManager;
	}
	protected IAuthorizationManager getAuthorizationManager() {
		return _authorizationManager;
	}
	public void setAuthorizationManager(IAuthorizationManager authorizationManager) {
		this._authorizationManager = authorizationManager;
	}
	
	private ILangManager _langManager;
	private IGroupManager _groupManager;
	private IAuthorizationManager _authorizationManager;
	
}
