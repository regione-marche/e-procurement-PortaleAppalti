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
package com.agiletec.plugins.jacms.apsadmin.content.attribute.action.link;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.agiletec.plugins.jacms.aps.system.services.content.model.ContentRecordVO;
import com.agiletec.plugins.jacms.aps.system.services.content.model.SymbolicLink;
import com.agiletec.plugins.jacms.apsadmin.content.ContentFinderAction;
import com.agiletec.plugins.jacms.apsadmin.content.attribute.action.link.helper.ILinkAttributeActionHelper;

/**
 * Classe action delegata alla gestione dei link a contenuto nelle 
 * operazioni sugli attributi tipo Link.
 * @author E.Santoboni
 */
public class ContentLinkAction extends ContentFinderAction {
	
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
	
	public String joinContentLink() {
		ContentRecordVO contentVo = this.getContentVo(this.getContentId());
		if (null == contentVo || !contentVo.isOnLine()) {
			ApsSystemUtils.getLogger().error("Contenuto '" + this.getContentId() + "' INESISTENTE O NON PUBBLICO");
			return FAILURE;
		}
		if (this.isContentOnPageType()) {
			//Fa il forward alla scelta pagina di destinazione
			return "configContentOnPageLink";
		} else {
			String[] destinations = {null, this.getContentId(), null};
			HttpSession session = this.getRequest().getSession();
			String langCode = (String) session.getAttribute(ILinkAttributeActionHelper.LINK_LANG_CODE_SESSION_PARAM);
			this.setLangCode(langCode);
			this.getLinkAttributeHelper().joinLink(destinations, SymbolicLink.CONTENT_TYPE, this.getRequest());
			return SUCCESS;
		}
	}
	
	/**
	 * Sovrascrittura del metodo della {@link ContentFinderAction}.
	 * Il metodo fà in modo di ricercare i contenuti che hanno, come gruppo proprietario o gruppo abilitato alla visualizzazione, 
	 * o il gruppo "free" o il gruppo proprietario del contenuto in redazione.
	 */
	@Override
	protected List<String> getContentGroupCodes() {
		List<String> allowedGroups = new ArrayList<String>();
		allowedGroups.add(Group.FREE_GROUP_NAME);
		Content currentContent = this.getContent();
		allowedGroups.add(currentContent.getMainGroup());
		return allowedGroups;
	}
	
	public SymbolicLink getSymbolicLink() {
		return (SymbolicLink) this.getRequest().getSession().getAttribute(ILinkAttributeActionHelper.SYMBOLIC_LINK_SESSION_PARAM);
	}
	
	public String getContentId() {
		return _contentId;
	}
	public void setContentId(String contentId) {
		this._contentId = contentId;
	}
	
	public boolean isContentOnPageType() {
		return _contentOnPageType;
	}
	public void setContentOnPageType(boolean contentOnPageType) {
		this._contentOnPageType = contentOnPageType;
	}
	
	public String getLangCode() {
		return _langCode;
	}
	public void setLangCode(String langCode) {
		this._langCode = langCode;
	}
	
	/**
	 * Restituisce la classe helper della gestione degli attributi di tipo Link.
	 * @return La classe helper degli attributi di tipo Link.
	 */
	protected ILinkAttributeActionHelper getLinkAttributeHelper() {
		return _linkAttributeHelper;
	}
	/**
	 * Setta la classe helper della gestione degli attributi di tipo Link.
	 * @param linkAttributeHelper La classe helper degli attributi di tipo Link.
	 */
	public void setLinkAttributeHelper(ILinkAttributeActionHelper linkAttributeHelper) {
		this._linkAttributeHelper = linkAttributeHelper;
	}
	
	private String _contentId;
	private boolean _contentOnPageType;
	private String _langCode;
	
	private ILinkAttributeActionHelper _linkAttributeHelper;
	
}