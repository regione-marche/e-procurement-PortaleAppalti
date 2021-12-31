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
package com.agiletec.plugins.jacms.aps.system.services.searchengine;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;


import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.plugins.jacms.aps.system.services.content.IContentManager;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;

/**
 * Thread Class delegate to load content index, in use on SearchEngine.
 * @author E.Santoboni
 */
public class IndexLoaderThread extends Thread {
	
	public IndexLoaderThread(SearchEngineManager searchEngineManager, 
			IContentManager contentManager, IIndexerDAO indexerDao) {
		this._contentManager = contentManager;
		this._searchEngineManager = searchEngineManager;
		this._indexerDao = indexerDao;
	}
	
	@Override
	public void run() {
		LastReloadInfo reloadInfo = new LastReloadInfo();
		try {
			this.loadNewIndex();
			reloadInfo.setResult(LastReloadInfo.ID_SUCCESS_RESULT);
		} catch (Throwable t) {
			reloadInfo.setResult(LastReloadInfo.ID_FAILURE_RESULT);
			ApsSystemUtils.logThrowable(t, this, "run");
		} finally {
			reloadInfo.setDate(new Date());
			this._searchEngineManager.notifyEndingIndexLoading(reloadInfo, this._indexerDao);
			this._searchEngineManager.sellOfQueueEvents();
		}
	}
	
	private void loadNewIndex() throws Throwable {
		Logger log = ApsSystemUtils.getLogger();
		try {
			List<String> contentsId = this._contentManager.searchId(null);
			for (int i=0; i<contentsId.size(); i++) {
				String id = contentsId.get(i);
				Content currentContent = this._contentManager.loadContent(id, true);
				if (currentContent != null) {
					this._indexerDao.add(currentContent);
					if (log.isInfoEnabled()) {
						log.info("Indicizzato contenuto " + currentContent.getId());
					}
				}
			}
			log.info("Indicizzazione effettuata");
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "reloadIndex");
			throw t;
		}
	}
	
	private SearchEngineManager _searchEngineManager;
	private IContentManager _contentManager;
	private IIndexerDAO _indexerDao;
	
}