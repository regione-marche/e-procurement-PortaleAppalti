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

import javax.servlet.http.HttpServletRequest;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;

/**
 * Action gestore delle operazioni di creazione nuovo contenuto.
 * @author E.Santoboni
 */
public class IntroNewContentAction extends AbstractContentAction {
	
	/**
	 * Punto di ingresso della redazione nuovo contenuto.
	 * Apre l'interfaccia per la scelta del tipo di contenuto 
	 * da gestire con gli altri campi standard di Descrizione e Stato
	 * @return Il risultato dell'azione.
	 */
	public String openNew() {
		HttpServletRequest request = this.getRequest();
		try {
			request.getSession().removeAttribute(ContentActionConstants.SESSION_PARAM_NAME_CURRENT_CONTENT);
			this.setContentStatus(Content.STATUS_DRAFT);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "openNew");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	/**
	 * Crea e mette in sessione un nuovo contenuto del tipo richiesto.
	 * @return Il risultato dell'azione.
	 */
	public String createNewVoid() {
		HttpServletRequest request = this.getRequest();
		try {
			Content prototype = this.getContentManager().createContentType(this.getContentTypeCode());
			prototype.setDescr(this.getContentDescription());
			prototype.setStatus(this.getContentStatus());
			request.getSession().setAttribute(ContentActionConstants.SESSION_PARAM_NAME_CURRENT_CONTENT, prototype);
			ApsSystemUtils.getLogger().debug("Creato e messo in sessione contenuto vuoto del tipo " + prototype.getTypeCode());
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "createNewVoid");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	/**
	 * Restituisce la descrizione del contenuto.
	 * @return La descrizione del contenuto.
	 */
	public String getContentDescription() {
		return _contentDescription;
	}
	
	/**
	 * Setta la descrizione del contenuto.
	 * @param contentDescription La descrizione del contenuto.
	 */
	public void setContentDescription(String contentDescription) {
		this._contentDescription = contentDescription;
	}
	
	/**
	 * Restituisce lo stato del contenuto.
	 * @return Lo stato del contenuto.
	 */
	public String getContentStatus() {
		return _contentStatus;
	}
	
	/**
	 * Setta lo stato del contenuto.
	 * @param contentStatus Lo stato del contenuto.
	 */
	public void setContentStatus(String contentStatus) {
		this._contentStatus = contentStatus;
	}
	
	/**
	 * Restituisce il tipo (codice) del contenuto.
	 * @return Il tipo (codice) del contenuto.
	 */
	public String getContentTypeCode() {
		return _contentTypeCode;
	}
	
	/**
	 * Setta il tipo (codice) del contenuto.
	 * @param contentTypeCode Il tipo (codice) del contenuto.
	 */
	public void setContentTypeCode(String contentTypeCode) {
		this._contentTypeCode = contentTypeCode;
	}
	
	private String _contentTypeCode;
	private String _contentDescription;
	private String _contentStatus;
	
}
