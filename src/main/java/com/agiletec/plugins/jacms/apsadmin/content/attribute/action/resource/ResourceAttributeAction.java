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
package com.agiletec.plugins.jacms.apsadmin.content.attribute.action.resource;

import javax.servlet.http.HttpSession;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.apsadmin.system.BaseAction;
import com.agiletec.apsadmin.util.ApsRequestParamsUtil;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.agiletec.plugins.jacms.apsadmin.content.ContentActionConstants;
import com.agiletec.plugins.jacms.apsadmin.content.helper.IContentActionHelper;

/**
 * Classe action base delegata alla gestione base delle operazione sugli attributi risorsa.
 * L'azione rappresenta l'entry point per tutte le richieste 
 * effettuate dall'interfaccia di redazione contenuto. 
 * @author E.Santoboni
 */
public class ResourceAttributeAction extends BaseAction implements IResourceAttributeAction {
	
	@Override
	public String chooseResource() {
		try {
			this.getContentActionHelper().updateEntity(this.getContent(), this.getRequest());
			ResourceAttributeActionHelper.initSessionParams(this, this.getRequest());
			String resourceTypeCode = (String) this.getRequest().getSession().getAttribute(ResourceAttributeActionHelper.RESOURCE_TYPE_CODE_SESSION_PARAM);
			this.setResourceTypeCode(resourceTypeCode);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "findResource");
			return FAILURE;
		}
		//POI FA IL FORWARD ALLA FINDER Resource
		return SUCCESS;
	}
	
	/**
	 * Rimuove da un Attributo la risorsa della lingua data.
	 * Necessita del parametro "joinResource<DEFAULT_SEPARATOR>IDRISORSA"
	 * dove <DEFAULT_SEPARATOR> è il separatore di default definito nella classe {@link ApsRequestParamsUtil} .
	 * @return SUCCESS se è andato a buon fine, FAILURE in caso contrario
	 */
	@Override
	public String removeResource() {
		try {
			this.getContentActionHelper().updateEntity(this.getContent(), this.getRequest());
			ResourceAttributeActionHelper.initSessionParams(this, this.getRequest());
			ResourceAttributeActionHelper.removeResource(this.getRequest());
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "removeResource");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	public String backToEntryContent() {
		HttpSession session = this.getRequest().getSession();
		String langCode = (String) session.getAttribute(ResourceAttributeActionHelper.RESOURCE_LANG_CODE_SESSION_PARAM);
		this.setResourceLangCode(langCode);
		ResourceAttributeActionHelper.removeSessionParams(this.getRequest().getSession());
		return SUCCESS;
	}
	
	/**
	 * Restituisce il contenuto in sesione.
	 * @return Il contenuto in sesione.
	 */
	public Content getContent() {
		return (Content) this.getRequest().getSession().getAttribute(ContentActionConstants.SESSION_PARAM_NAME_CURRENT_CONTENT);
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
	public String getResourceLangCode() {
		return _resourceLangCode;
	}
	public void setResourceLangCode(String resourceLangCode) {
		this._resourceLangCode = resourceLangCode;
	}
	
	@Override
	public String getResourceTypeCode() {
		return _resourceTypeCode;
	}
	public void setResourceTypeCode(String resourceTypeCode) {
		this._resourceTypeCode = resourceTypeCode;
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
	
	private String _attributeName;
	private String _parentAttributeName;
	private int _elementIndex = -1;
	private String _resourceLangCode;
	private String _resourceTypeCode;
	
	private IContentActionHelper _contentActionHelper;
	
}