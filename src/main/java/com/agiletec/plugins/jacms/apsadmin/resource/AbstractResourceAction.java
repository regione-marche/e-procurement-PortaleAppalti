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
package com.agiletec.plugins.jacms.apsadmin.resource;

import com.agiletec.aps.system.services.category.Category;
import com.agiletec.aps.system.services.category.ICategoryManager;
import com.agiletec.apsadmin.system.BaseAction;
import com.agiletec.plugins.jacms.aps.system.services.resource.IResourceManager;
import com.agiletec.plugins.jacms.aps.system.services.resource.model.ResourceInterface;
import com.agiletec.plugins.jacms.apsadmin.resource.helper.IResourceActionHelper;

/**
 * Classe astratta base per le Action di gestione risorse.
 * @version 1.0
 * @author E.Santoboni
 */
public abstract class AbstractResourceAction extends BaseAction {
	
	/**
	 * Restutuisce la root delle categorie.
	 * @return La root delle categorie.
	 */
	public Category getCategoryRoot() {
		return this.getCategoryManager().getRoot();
	}
	
	/**
	 * Restituisce una risorsa in base all'identificativo.
	 * @param resourceId L'identificativo della risorsa da caricare.
	 * @return La risorsa cercata.
	 * @throws Throwable In caso di errore.
	 */
	public ResourceInterface loadResource(String resourceId) throws Throwable {
		return this.getResourceManager().loadResource(resourceId);
	}
	
	public String getResourceTypeCode() {
		return _resourceTypeCode;
	}
	public void setResourceTypeCode(String resourceTypeCode) {
		this._resourceTypeCode = resourceTypeCode;
	}
	
	protected ICategoryManager getCategoryManager() {
		return _categoryManager;
	}
	public void setCategoryManager(ICategoryManager categoryManager) {
		this._categoryManager = categoryManager;
	}
	
	protected IResourceManager getResourceManager() {
		return _resourceManager;
	}
	public void setResourceManager(IResourceManager resourceManager) {
		this._resourceManager = resourceManager;
	}
	
	protected IResourceActionHelper getResourceActionHelper() {
		return _resourceActionHelper;
	}
	public void setResourceActionHelper(IResourceActionHelper resourceActionHelper) {
		this._resourceActionHelper = resourceActionHelper;
	}
	
	private String _resourceTypeCode;
	private IResourceManager _resourceManager;
	private ICategoryManager _categoryManager;
	private IResourceActionHelper _resourceActionHelper;
	
}
