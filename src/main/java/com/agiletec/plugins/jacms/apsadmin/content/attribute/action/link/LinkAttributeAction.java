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

import javax.servlet.http.HttpSession;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.apsadmin.system.BaseAction;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.agiletec.plugins.jacms.aps.system.services.content.model.SymbolicLink;
import com.agiletec.plugins.jacms.apsadmin.content.ContentActionConstants;
import com.agiletec.plugins.jacms.apsadmin.content.attribute.action.link.helper.ILinkAttributeActionHelper;
import com.agiletec.plugins.jacms.apsadmin.content.helper.IContentActionHelper;

/**
 * Classe action delegata alla gestione delle operazioni base sugli attributi Link.
 * Le azioni gestite rappresentano ciascuna un entry point dall'interfaccia di redazione contenuto. 
 * @author E.Santoboni
 */
public class LinkAttributeAction extends BaseAction implements ILinkAttributeAction {
	
	@Override
	public String chooseLink() {
		try {
			this.getContentActionHelper().updateEntity(this.getContent(), this.getRequest());
			this.getLinkAttributeHelper().initSessionParams(this, this.getRequest());
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "chooseLink");
			return FAILURE;
		}
		//FA IL FORWARD ALLA choose link type
		return SUCCESS;
	}
	
	@Override
	public String chooseLinkType() {
		// TODO Auto-generated method stub
		return SUCCESS;
	}
	
	public String configLink() {
		String result = FAILURE;
		int destType = this.getLinkType();
        switch (destType) {
        case (SymbolicLink.CONTENT_TYPE):
        	result = "configContentLink";
            break;
        case (SymbolicLink.CONTENT_ON_PAGE_TYPE):
        	ApsSystemUtils.logThrowable(new RuntimeException("Non è possibile selezionare direttamente " +
        			"il link di contenuto su pagina"), this, "configLink");
            break;
        case SymbolicLink.PAGE_TYPE:
        	result = "configPageLink";
            break;
        case SymbolicLink.URL_TYPE:
        	result = "configUrlLink";
            break;
        default:
        	ApsSystemUtils.logThrowable(new RuntimeException("Link non riconosciuto : " +
        			"type " + destType), this, "configLink");
            break;
        }
		return result;
	}
	
	@Override
	public String removeLink() {
		try {
			this.getContentActionHelper().updateEntity(this.getContent(), this.getRequest());
			this.getLinkAttributeHelper().initSessionParams(this, this.getRequest());
			this.getLinkAttributeHelper().removeLink(this.getRequest());
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "removeLink");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	public String backToEntryContent() {
		HttpSession session = this.getRequest().getSession();
		String langCode = (String) session.getAttribute(ILinkAttributeActionHelper.LINK_LANG_CODE_SESSION_PARAM);
		this.setLangCode(langCode);
		this.getLinkAttributeHelper().removeSessionParams(session);
		return SUCCESS;
	}
	
	/**
	 * Restituisce il contenuto in sesione.
	 * @return Il contenuto in sesione.
	 */
	public Content getContent() {
		return (Content) this.getRequest().getSession().getAttribute(ContentActionConstants.SESSION_PARAM_NAME_CURRENT_CONTENT);
	}
	
	public SymbolicLink getSymbolicLink() {
		return (SymbolicLink) this.getRequest().getSession().getAttribute(ILinkAttributeActionHelper.SYMBOLIC_LINK_SESSION_PARAM);
	}
	
	public int[] getLinkDestinations() {
		return SymbolicLink.getDestinationTypes();
	}
	
	public IPage getPage(String pageCode) {
		return this.getPageManager().getPage(pageCode);
	}
	
	public int getLinkType() {
		return _linkType;
	}
	public void setLinkType(int linkType) {
		this._linkType = linkType;
	}
	
	protected IPageManager getPageManager() {
		return _pageManager;
	}
	public void setPageManager(IPageManager pageManager) {
		this._pageManager = pageManager;
	}
	
	@Override
	public String getAttributeName() {
		return _attributeName;
	}
	public void setAttributeName(String attributeName) {
		this._attributeName = attributeName;
	}
	
	@Override
	public String getParentAttributeName() {
		return _parentAttributeName;
	}
	public void setParentAttributeName(String parentAttributeName) {
		this._parentAttributeName = parentAttributeName;
	}
	
	@Override
	public int getElementIndex() {
		return _elementIndex;
	}
	public void setElementIndex(int elementIndex) {
		this._elementIndex = elementIndex;
	}
	
	@Override
	public String getLangCode() {
		return _langCode;
	}
	public void setLangCode(String langCode) {
		this._langCode = langCode;
	}
	
	/**
	 * Restituisce la classe helper della gestione contenuti.
	 * @return La classe helper della gestione contenuti.
	 */
	protected IContentActionHelper getContentActionHelper() {
		return _contentActionHelper;
	}
	
	/**
	 * Setta la classe helper della gestione contenuti.
	 * @param contentActionHelper La classe helper della gestione contenuti.
	 */
	public void setContentActionHelper(IContentActionHelper contentActionHelper) {
		this._contentActionHelper = contentActionHelper;
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
	
	private String _attributeName;
	private String _parentAttributeName;
	private int _elementIndex = -1;
	private String _langCode;
	
	private int _linkType;
	private IPageManager _pageManager;
	
	private IContentActionHelper _contentActionHelper;
	private ILinkAttributeActionHelper _linkAttributeHelper;
	
}