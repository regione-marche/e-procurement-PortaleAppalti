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
package com.agiletec.plugins.jacms.apsadmin.content;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.apsadmin.system.BaseAction;
import com.agiletec.plugins.jacms.aps.system.services.content.IContentManager;
import com.agiletec.plugins.jacms.aps.system.services.searchengine.ICmsSearchEngineManager;
import com.agiletec.plugins.jacms.aps.system.services.searchengine.LastReloadInfo;

/**
 * Classi Action delegata alla esecuzione delle operazioni 
 * di amministrazione dei contenuti.
 * @author E.Santoboni
 */
public class ContentAdminAction extends BaseAction implements IContentAdminAction {
	
	@Override
	public String reloadContentsIndex() {
		try {
			this.getSearchEngineManager().startReloadContentsReferences();
			ApsSystemUtils.getLogger().info("Ricaricamento indicizzazione dei contenuti avviata");
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "reloadContentsIndex");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	@Override
	public String reloadContentsReference() {
		try {
			String typeCode = null;
			this.getContentManager().reloadEntitiesReferences(typeCode);
			ApsSystemUtils.getLogger().info("Ricaricamento referenze contenuti avviato");
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "reloadContentsReference");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	public int getContentManagerStatus() {
		return this.getContentManager().getStatus();
	}
	
	public int getSearcherManagerStatus() {
		return this.getSearchEngineManager().getStatus();
	}
	
	/**
	 * @deprecated From jAPS 2.0 version 2.0.9. Use getContentManagerStatus() method
	 */
	public int getContentManagerState() {
		return this.getContentManagerStatus();
	}
	
	/**
	 * @deprecated From jAPS 2.0 version 2.0.9. Use getSearcherManagerStatus() method
	 */
	public int getSearcherManagerState() {
		return this.getSearcherManagerStatus();
	}
	
	public LastReloadInfo getLastReloadInfo() {
		return this.getSearchEngineManager().getLastReloadInfo();
	}
	
	protected IContentManager getContentManager() {
		return _contentManager;
	}
	public void setContentManager(IContentManager contentManager) {
		this._contentManager = contentManager;
	}
	
	protected ICmsSearchEngineManager getSearchEngineManager() {
		return _searchEngineManager;
	}
	public void setSearchEngineManager(ICmsSearchEngineManager searchEngineManager) {
		this._searchEngineManager = searchEngineManager;
	}
	
	private IContentManager _contentManager;
	private ICmsSearchEngineManager _searchEngineManager;
	
}