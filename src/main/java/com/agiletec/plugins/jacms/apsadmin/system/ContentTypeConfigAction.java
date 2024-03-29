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

import java.util.ArrayList;
import java.util.List;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.apsadmin.system.entity.type.EntityTypeConfigAction;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.agiletec.plugins.jacms.aps.system.services.contentmodel.ContentModel;
import com.agiletec.plugins.jacms.aps.system.services.contentmodel.IContentModelManager;

/**
 * @author E.Santoboni
 */
public class ContentTypeConfigAction extends EntityTypeConfigAction {
	
	@Override
	protected IApsEntity updateEntityOnSession() {
		Content contentType = (Content) super.updateEntityOnSession();
		contentType.setViewPage(this.getViewPageCode());
		if (null != this.getListModelId()) {
			contentType.setListModel(this.getListModelId().toString());
		}
		if (null != this.getDefaultModelId()) {
			contentType.setDefaultModel(this.getDefaultModelId().toString());
		}
		return contentType;
	}
	
	/**
	 * Return a plain list of the free pages in the portal.
	 * @return the list of the free pages of the portal.
	 */
	public List<IPage> getFreePages() {
		IPage root = this.getPageManager().getRoot();
		List<IPage> pages = new ArrayList<IPage>();
		this.addPages(root, pages);
		return pages;
	}
	
	private void addPages(IPage page, List<IPage> pages) {
		if (page.getGroup().equals(Group.FREE_GROUP_NAME)) {
			pages.add(page);
		}
		IPage[] children = page.getChildren();
		for (int i=0; i<children.length; i++) {
			this.addPages(children[i], pages);
		}
	}
	
	/**
	 * Return the list of contentmodel given the content type code.
	 * @param typeCode The content type code.
	 * @return The Content Models found
	 */
	public List<ContentModel> getContentModels(String typeCode) {
		if (null == typeCode) return new ArrayList<ContentModel>();
		List<ContentModel> models = null;
		try {
			models = this.getContentModelManager().getModelsForContentType(typeCode);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getModels");
			throw new RuntimeException("Error on extracting models by type " + typeCode, t);
		}
		return models;
	}
	
	public String getViewPageCode() {
		return _viewPageCode;
	}
	public void setViewPageCode(String viewPageCode) {
		this._viewPageCode = viewPageCode;
	}
	
	public Integer getListModelId() {
		return _listModelId;
	}
	public void setListModelId(Integer listModelId) {
		this._listModelId = listModelId;
	}
	
	public Integer getDefaultModelId() {
		return _defaultModelId;
	}
	public void setDefaultModelId(Integer defaultModelId) {
		this._defaultModelId = defaultModelId;
	}
	
	protected IPageManager getPageManager() {
		return _pageManager;
	}
	public void setPageManager(IPageManager pageManager) {
		this._pageManager = pageManager;
	}
	
	protected IContentModelManager getContentModelManager() {
		return _contentModelManager;
	}
	public void setContentModelManager(IContentModelManager contentModelManager) {
		this._contentModelManager = contentModelManager;
	}
	
	private String _viewPageCode;
	private Integer _listModelId;
	private Integer _defaultModelId;
	
	private IPageManager _pageManager;
	private IContentModelManager _contentModelManager;
	
}