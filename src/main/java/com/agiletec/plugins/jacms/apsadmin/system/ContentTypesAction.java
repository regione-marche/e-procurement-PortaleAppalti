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
package com.agiletec.plugins.jacms.apsadmin.system;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.apsadmin.system.entity.type.EntityTypesAction;
import com.agiletec.plugins.jacms.aps.system.services.searchengine.ICmsSearchEngineManager;

/**
 * @author E.Santoboni
 */
public class ContentTypesAction extends EntityTypesAction implements IContentReferencesReloadingAction {
	
	@Override
	public String reloadContentsIndexes() {
		try {
			this.getSearchEngineManager().startReloadContentsReferences();
			ApsSystemUtils.getLogger().info("Ricaricamento indicizzazione dei contenuti avviata");
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "reloadContentsIndexs");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	public int getSearcherManagerStatus() {
		return this.getSearchEngineManager().getStatus();
	}
	
	protected ICmsSearchEngineManager getSearchEngineManager() {
		return _searchEngineManager;
	}
	public void setSearchEngineManager(ICmsSearchEngineManager searchEngineManager) {
		this._searchEngineManager = searchEngineManager;
	}
	
	private ICmsSearchEngineManager _searchEngineManager;
	
}
