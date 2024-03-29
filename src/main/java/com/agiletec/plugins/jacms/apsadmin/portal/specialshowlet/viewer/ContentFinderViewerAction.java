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
package com.agiletec.plugins.jacms.apsadmin.portal.specialshowlet.viewer;

import java.util.ArrayList;
import java.util.List;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.showlettype.IShowletTypeManager;
import com.agiletec.plugins.jacms.apsadmin.content.ContentFinderAction;

/**
 * Classe Action che cerca i contenuti per 
 * la configurazione delle showlet di tipo "Pubblica contenuto singolo".
 * @author E.Santoboni
 */
public class ContentFinderViewerAction extends ContentFinderAction implements IContentFinderViewerAction {
	
	@Override
	public List<String> getContents() {
		List<String> result = null;
		try {
			List<String> allowedGroups = this.getContentGroupCodes();
			result = this.getContentManager().loadPublicContentsId(null, this.createFilters(), allowedGroups);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getContents");
			throw new RuntimeException("Errore in ricerca contenuti", t);
		}
		return result;
	}
	
	@Override
	public String joinContent() {
		return SUCCESS;
	}
	
	@Override
	protected List<String> getContentGroupCodes() {
		List<String> allowedGroups = new ArrayList<String>();
		allowedGroups.add(Group.FREE_GROUP_NAME);
		IPage currentPage = this.getCurrentPage();
		allowedGroups.add(currentPage.getGroup());
    	return allowedGroups;
	}
	
	public IPage getCurrentPage() {
		return this.getPageManager().getPage(this.getPageCode());
	}
	
	public String getPageCode() {
		return _pageCode;
	}
	public void setPageCode(String pageCode) {
		this._pageCode = pageCode;
	}
	
	public int getFrame() {
		return _frame;
	}
	public void setFrame(int frame) {
		this._frame = frame;
	}
	
	public String getShowletTypeCode() {
		return _showletTypeCode;
	}
	public void setShowletTypeCode(String showletTypeCode) {
		this._showletTypeCode = showletTypeCode;
	}
	
	protected IPageManager getPageManager() {
		return _pageManager;
	}
	public void setPageManager(IPageManager pageManager) {
		this._pageManager = pageManager;
	}
	
	protected IShowletTypeManager getShowletTypeManager() {
		return _showletTypeManager;
	}
	public void setShowletTypeManager(IShowletTypeManager showletTypeManager) {
		this._showletTypeManager = showletTypeManager;
	}
	
	/**
	 * Restituisce il percorso a briciole di pane, con i titoli 
	 * nella lingua corrente, tra la root e la pagina specificato.
	 * @param pageCode Il codice pagina tramite il quale costruire il percorso.
	 * @param separator La stringa separatore tra le pagine.
	 * @return Il percorso a briciole di pane.
	 * FIXME METODO IDENTICO A QUELLO DI BASE PER LE ACTION DI GESTIONE PAGINE
	 */
	public String getBreadCrumbs(String pageCode, String separator) {
		IPage page = this.getPageManager().getPage(pageCode);
		if (null == page) return "NULL PAGE";
		Lang lang = this.getCurrentLang();
		String breadCrumbs = page.getTitle(lang.getCode());
		IPage parent = page.getParent();
		return this.getSubBreadCrumbs(page, parent, separator, breadCrumbs, lang);
	}
	
	private String getSubBreadCrumbs(IPage page, IPage parent, String separator, String breadCrumbs, Lang lang) {
		if (parent != null && !parent.getCode().equals(page.getCode())) {
			breadCrumbs = parent.getTitle(lang.getCode()) + separator + breadCrumbs;
			IPage currentParent = parent.getParent();
			return this.getSubBreadCrumbs(parent, currentParent, separator, breadCrumbs, lang);
		}
		return breadCrumbs;
	}
	
	public String getContentId() {
		return _contentId;
	}
	public void setContentId(String contentId) {
		this._contentId = contentId;
	}
	
	public String getModelId() {
		return _modelId;
	}
	public void setModelId(String modelId) {
		this._modelId = modelId;
	}
	
	private String _pageCode;
	private int _frame = -1;
	private String _showletTypeCode;
	
	private IPageManager _pageManager;
	private IShowletTypeManager _showletTypeManager;
	
	private String _contentId;
	private String _modelId;
	
}