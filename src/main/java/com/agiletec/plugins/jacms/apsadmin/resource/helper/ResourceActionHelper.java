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
package com.agiletec.plugins.jacms.apsadmin.resource.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.category.ICategoryManager;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import com.agiletec.apsadmin.system.BaseActionHelper;
import com.agiletec.plugins.jacms.aps.system.services.resource.IResourceManager;
import com.agiletec.plugins.jacms.aps.system.services.resource.ResourceUtilizer;
import com.agiletec.plugins.jacms.aps.system.services.resource.model.ResourceInterface;

/**
 * Classe helper della gestione risorse.
 * @version 1.0
 * @author E.Santoboni
 */
public class ResourceActionHelper extends BaseActionHelper implements IResourceActionHelper {
	
	public Map getReferencingObjects(ResourceInterface resource, HttpServletRequest request) throws ApsSystemException {
		Map<String, List> references = new HashMap<String, List>();
    	try {
    		String[] defNames = ApsWebApplicationUtils.getWebApplicationContext(request).getBeanNamesForType(ResourceUtilizer.class);
			for (int i=0; i<defNames.length; i++) {
				Object service = null;
				try {
					service = ApsWebApplicationUtils.getWebApplicationContext(request).getBean(defNames[i]);
				} catch (Throwable t) {
					ApsSystemUtils.logThrowable(t, this, "getReferencingObjects");
					service = null;
				}
				if (service != null) {
					ResourceUtilizer resourceUtilizer = (ResourceUtilizer) service;
					List utilizers = resourceUtilizer.getResourceUtilizers(resource.getId());
					if (utilizers != null && !utilizers.isEmpty()) {
						//TODO STUDIARE CODIFICA CHIAVE
						references.put(resourceUtilizer.getName()+"Utilizers", utilizers);
					}
				}
			}
    	} catch (Throwable t) {
    		throw new ApsSystemException("Errore in getReferencingObjects", t);
    	}
    	return references;
	}
	
	@Override
	public List<Group> getAllowedGroups(UserDetails currentUser) {
		return super.getAllowedGroups(currentUser);
    }
	
	@Override
	public List<String> searchResources(String resourceType, String insertedText,
			String categoryCode, UserDetails currentUser) throws Throwable {
		List<String> allowedGroups = new ArrayList<String>();
		List<Group> userGroups = this.getAuthorizationManager().getGroupsOfUser(currentUser);
		for (int i=0; i<userGroups.size(); i++) {
			Group group = userGroups.get(i);
			if (group.getName().equals(Group.ADMINS_GROUP_NAME)) {
				allowedGroups.clear();
				break;
			} else {
				allowedGroups.add(group.getName());
			}
		}
		List<String> resourcesId = this.getResourceManager().searchResourcesId(resourceType, 
				insertedText, categoryCode, allowedGroups);
		return resourcesId;
	}
	
    protected IResourceManager getResourceManager() {
		return _resourceManager;
	}
	public void setResourceManager(IResourceManager resourceManager) {
		this._resourceManager = resourceManager;
	}
	
	protected ICategoryManager getCategoryManager() {
		return _categoryManager;
	}
	public void setCategoryManager(ICategoryManager categoryManager) {
		this._categoryManager = categoryManager;
	}
	
	private IResourceManager _resourceManager;
	private ICategoryManager _categoryManager;
	
}
