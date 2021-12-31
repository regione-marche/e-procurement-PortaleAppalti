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
package com.agiletec.plugins.jacms.aps.system.services.cache;

import java.util.List;

import org.slf4j.Logger;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.services.AbstractService;
import com.agiletec.aps.system.services.cache.ICacheManager;
import com.agiletec.aps.system.services.lang.ILangManager;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.plugins.jacms.aps.system.JacmsSystemConstants;
import com.agiletec.plugins.jacms.aps.system.services.content.ContentManager;
import com.agiletec.plugins.jacms.aps.system.services.content.event.PublicContentChangedEvent;
import com.agiletec.plugins.jacms.aps.system.services.content.event.PublicContentChangedObserver;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.agiletec.plugins.jacms.aps.system.services.contentmodel.ContentModel;
import com.agiletec.plugins.jacms.aps.system.services.contentmodel.IContentModelManager;
import com.agiletec.plugins.jacms.aps.system.services.contentmodel.event.ContentModelChangedEvent;
import com.agiletec.plugins.jacms.aps.system.services.contentmodel.event.ContentModelChangedObserver;

/**
 * @author E.Santoboni
 */
public class CmsCacheWrapperManager extends AbstractService 
		implements PublicContentChangedObserver, ContentModelChangedObserver {
	
	@Override
	public void init() throws Exception {
		ApsSystemUtils.getLogger().debug(this.getClass().getName() + ": initialized");
	}
	
	@Override
	public void updateFromPublicContentChanged(PublicContentChangedEvent event) {
		Content content = event.getContent();
		String authInfokey = ContentManager.getContentAuthInfoCacheKey(content.getId());
		this.getCacheManager().flushEntry(authInfokey);
		this.getCacheManager().flushGroup(JacmsSystemConstants.CONTENTS_ID_CACHE_GROUP_PREFIX + content.getTypeCode());
		List<Lang> langs = this.getLangManager().getLangs();
		List<ContentModel> models = this.getContentModelManager().getModelsForContentType(content.getTypeCode());
		for (int i = 0; i < langs.size(); i++) {
			Lang lang = langs.get(i);
			for (int j = 0; j < models.size(); j++) {
				ContentModel contentModel = models.get(j);
				String key = ContentManager.getRenderedContentCacheKey(content.getId(), contentModel.getId(), lang.getCode());
				this.getCacheManager().flushEntry(key);
			}
		}
		Logger log = ApsSystemUtils.getLogger();
		if (log.isTraceEnabled()) {
			log.info("Notificato modifica contenuto pubblico " + content.getId());
		}
	}
	
	@Override
	public void updateFromContentModelChanged(ContentModelChangedEvent event) {
		ContentModel model = event.getContentModel();
		String cacheGroupKey = JacmsSystemConstants.CACHE_GROUP_MODEL_PREFIX + model.getId();
		this.getCacheManager().flushGroup(cacheGroupKey);
	}
	
	protected ICacheManager getCacheManager() {
		return _cacheManager;
	}
	public void setCacheManager(ICacheManager cacheManager) {
		this._cacheManager = cacheManager;
	}
	
	protected ILangManager getLangManager() {
		return _langManager;
	}
	public void setLangManager(ILangManager langManager) {
		this._langManager = langManager;
	}
	
	protected IContentModelManager getContentModelManager() {
		return _contentModelManager;
	}
	public void setContentModelManager(IContentModelManager contentModelManager) {
		this._contentModelManager = contentModelManager;
	}
	
	private ICacheManager _cacheManager;
	private ILangManager _langManager;
	private IContentModelManager _contentModelManager;
	
}