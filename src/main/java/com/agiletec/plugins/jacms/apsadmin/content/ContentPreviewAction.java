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
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.page.Showlet;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.agiletec.plugins.jacms.aps.system.services.contentpagemapper.IContentPageMapperManager;
import org.apache.struts2.interceptor.ServletResponseAware;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Classe action delegate alla gestione della funzione di preview contenuti.
 * @author E.Santoboni
 */
public class ContentPreviewAction extends AbstractContentAction implements IContentPreviewAction, ServletResponseAware {
	
	@Override
	public String preview() {
		Content content = this.getContent();
		this.getContentActionHelper().updateEntity(content, this.getRequest());
		return SUCCESS;
	}
	
	@Override
	public String executePreview() {
		try {
			String pageDestCode = this.getCheckPageDestinationCode();
			if (null == pageDestCode) return INPUT;
			this.prepareForwardParams(pageDestCode);
			this.getRequest().setCharacterEncoding("UTF-8");
		} catch (Throwable t) {
			String message = "Errore";
			ApsSystemUtils.logThrowable(t, this, "executePreview", message);
			throw new RuntimeException(message, t);
		}
		return SUCCESS;
	}
	
	protected String getCheckPageDestinationCode() {
		IPageManager pageManager = this.getPageManager();
		String pageDestCode = this.getPreviewPageCode();
		if (null == pageDestCode || pageDestCode.trim().length() == 0) {
			pageDestCode = this.searchPublishingPage();
			if (null == pageDestCode) {
				pageDestCode = this.getContent().getViewPage();
				if (null == pageDestCode || null == pageManager.getPage(pageDestCode)) {
					String[] args = {pageDestCode};
					this.addFieldError("previewPageCode", this.getText("Message.Content.previewPageNotValid", args));
					return null;
				}
			}
		}
		if (null == pageManager.getPage(pageDestCode)) {
			String[] args = {pageDestCode};
			this.addFieldError("previewPageCode", this.getText("Message.Content.pageNotFound", args));
			return null;
		}
		return pageDestCode;
	}
	
	private void prepareForwardParams(String pageDestCode) {
		HttpServletRequest request = this.getRequest();
		RequestContext reqCtx = new RequestContext();
		reqCtx.setRequest(request);
		reqCtx.setResponse(this.getServletResponse());
//		Lang currentLang = this.getLangManager().getDefaultLang();
		reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG, getCurrentLang());
		IPageManager pageManager = this.getPageManager();
		IPage pageDest = pageManager.getPage(pageDestCode);
		reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE, pageDest);
		request.setAttribute(RequestContext.REQCTX, reqCtx);
	}
	
	private String searchPublishingPage() {
		Content content = this.getContent();
		if (content.getId() == null) return null;
		String pageCode = this.getContentPageMapperManager().getPageCode(content.getId());
		if (pageCode == null || this.getPageManager().getPage(pageCode) == null) return null;
		IPage pageDest = this.getPageManager().getPage(pageCode);
		if (!this.getAuthorizationManager().isAuthOnGroup(this.getCurrentUser(), pageDest.getGroup())) {
			return null;
		}
		for (int i=0; i<pageDest.getShowlets().length; i++) {
			Showlet showlet = pageDest.getShowlets()[i];
			if (null != showlet && content.getId().equals(showlet.getPublishedContent())) {
				return pageCode;
			}
		}
		return null;
	}
	
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this._response = response;
	}
	public HttpServletResponse getServletResponse() {
		return _response;
	}
	
	public String getPreviewPageCode() {
		return _previewPageCode;
	}
	public void setPreviewPageCode(String previewPageCode) {
		this._previewPageCode = previewPageCode;
	}
	
	protected IPageManager getPageManager() {
		return _pageManager;
	}
	public void setPageManager(IPageManager pageManager) {
		this._pageManager = pageManager;
	}
	
	protected IContentPageMapperManager getContentPageMapperManager() {
		return _contentPageMapperManager;
	}
	public void setContentPageMapperManager(IContentPageMapperManager contentPageMapperManager) {
		this._contentPageMapperManager = contentPageMapperManager;
	}

	private HttpServletResponse _response;
	
	private String _previewPageCode;
	
	private IPageManager _pageManager;
	private IContentPageMapperManager _contentPageMapperManager;
	
}
