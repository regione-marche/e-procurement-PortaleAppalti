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
package com.agiletec.plugins.jacms.apsadmin.content.attribute.action.resource;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.agiletec.plugins.jacms.aps.system.services.resource.model.ResourceInterface;
import com.agiletec.plugins.jacms.apsadmin.content.ContentActionConstants;
import com.agiletec.plugins.jacms.apsadmin.resource.ResourceFinderAction;

/**
 * Classe action a servizio della gestione attributi risorsa, 
 * estensione della action gestrice delle operazioni di ricerca risorse.
 * @author E.Santoboni
 */
public class ExtendedResourceFinderAction extends ResourceFinderAction {
	
	@Override
	public List<String> getResources() throws Throwable {
		List<String> resourcesId = null;
		try {
			List<String> groupCodes = new ArrayList<String>();
			groupCodes.add(Group.FREE_GROUP_NAME);
			if (null != this.getContent().getMainGroup()) {
				groupCodes.add(this.getContent().getMainGroup());
			}
			resourcesId = this.getResourceManager().searchResourcesId(this.getResourceTypeCode(), 
					this.getText(), this.getCategoryCode(), groupCodes);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getResources");
			throw t;
		}
		return resourcesId;
	}
	
	public String getCurrentAttributeLang() {
		HttpSession session = this.getRequest().getSession();
		return (String) session.getAttribute(ResourceAttributeActionHelper.RESOURCE_LANG_CODE_SESSION_PARAM);
	}
	
	/**
	 * Restituisce il contenuto in sesione.
	 * @return Il contenuto in sesione.
	 */
	public Content getContent() {
		return (Content) this.getRequest().getSession().getAttribute(ContentActionConstants.SESSION_PARAM_NAME_CURRENT_CONTENT);
	}
	
	/**
	 * Aggiunge una risorsa ad un Attributo.
	 * @return SUCCESS se è andato a buon fine, FAILURE in caso contrario
	 */
	public String joinResource() {
		try {
			String resourceId = this.getResourceId();
			ResourceInterface resource = this.getResourceManager().loadResource(resourceId);
			ResourceAttributeActionHelper.joinResource(resource, this.getRequest());
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "joinResource");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	public boolean isOnEditContent() {
		return true;
	}
	
	public String getResourceId() {
		return _resourceId;
	}
	public void setResourceId(String resourceId) {
		this._resourceId = resourceId;
	}
	
	private String _resourceId;
	
}
