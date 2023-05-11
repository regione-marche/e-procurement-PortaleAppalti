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
package com.agiletec.plugins.jacms.aps.tags;

import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.common.entity.model.EntitySearchFilter;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import com.agiletec.plugins.jacms.aps.system.JacmsSystemConstants;
import com.agiletec.plugins.jacms.aps.system.services.content.showlet.IContentListBean;
import com.agiletec.plugins.jacms.aps.system.services.content.showlet.IContentListHelper;

/**
 * Tag caricatore lista di identificativi contenuti.
 * Carica una lista di contenuti i base ai parametri immessi.
 * @author E.Santoboni
 */
public class ContentListTag extends TagSupport implements IContentListBean {
	
	@Override
	public int doStartTag() throws JspException {
		if (!this.isCacheable()) {
			return EVAL_BODY_INCLUDE;
		}
		ServletRequest request =  this.pageContext.getRequest();
		RequestContext reqCtx = (RequestContext) request.getAttribute(RequestContext.REQCTX);
		try {
			IContentListHelper helper = (IContentListHelper) ApsWebApplicationUtils.getBean(JacmsSystemConstants.CONTENT_LIST_HELPER, this.pageContext);
			List<String> contentsId = helper.searchInCache(this.getListName(), reqCtx);
			if (contentsId != null && !contentsId.isEmpty()) {
				this.pageContext.setAttribute(this.getListName(), contentsId);
				this.setListEvaluated(true);
				return SKIP_BODY;
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "doEndTag");
			throw new JspException("Errore tag", t);
		}
		return EVAL_BODY_INCLUDE;
	}
	
	@Override
	public int doEndTag() throws JspException {
		if (this.isListEvaluated()) {
			this.release();
			return super.doEndTag();
		}
		ServletRequest request =  this.pageContext.getRequest();
		RequestContext reqCtx = (RequestContext) request.getAttribute(RequestContext.REQCTX);
		try {
			IContentListHelper helper = (IContentListHelper) ApsWebApplicationUtils.getBean(JacmsSystemConstants.CONTENT_LIST_HELPER, this.pageContext);
			List<String> contents = helper.getContentsId(this, reqCtx);
			this.pageContext.setAttribute(_listName, contents);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "doEndTag");
			throw new JspException("Errore tag", t);
		}
		this.release();
		return super.doEndTag();
	}
	
	@Override
	public void release() {
		this._listName = null;
		this._contentType = null;
		this._category = null;
		this._filters = new EntitySearchFilter[0];
		this._listEvaluated = false;
		this._cacheable = true;
	}
	
	@Override
	public void addFilter(EntitySearchFilter filter) {
		int len = this._filters.length;
		EntitySearchFilter[] newFilters = new EntitySearchFilter[len + 1];
		for(int i=0; i < len; i++){
			newFilters[i] = this._filters[i];
		}
		newFilters[len] = filter;
		this._filters = newFilters;
	}
	
	@Override
	public EntitySearchFilter[] getFilters() {
		return this._filters;
	}
	
	/**
	 * Restituisce il nome con il quale viene inserita nel pageContext
	 * la lista degli identificativi trovati.
	 * @return Returns the listName.
	 */
	@Override
	public String getListName() {
		return _listName;
	}

	/**
	 * Setta il nome con il quale viene inserita nel pageContext
	 * la lista degli identificativi trovati.
	 * @param listName The listName to set.
	 */
	public void setListName(String listName) {
		this._listName = listName;
	}

	/**
	 * Restituisce il codice dei tipi di contenuto da cercare.
	 * @return Il codice dei tipi di contenuto da cercare.
	 */
	@Override
	public String getContentType() {
		return _contentType;
	}
	
	/**
	 * Setta il codice dei tipi di contenuto da cercare.
	 * @param contentType Il codice dei tipi di contenuto da cercare.
	 */
	@Override
	public void setContentType(String contentType) {
		this._contentType = contentType;
	}
	
	/**
	 * Restituisce la categoria dei contenuto da cercare.
	 * @return La categoria dei contenuto da cercare.
	 */
	@Override
	public String getCategory() {
		return _category;
	}
	
	/**
	 * Setta la categoria dei contenuto da cercare.
	 * @param category La categoria dei contenuto da cercare.
	 */
	@Override
	public void setCategory(String category) {
		this._category = category;
	}
	
	protected boolean isListEvaluated() {
		return _listEvaluated;
	}
	protected void setListEvaluated(boolean listEvaluated) {
		this._listEvaluated = listEvaluated;
	}
	
	@Override
	public boolean isCacheable() {
		return _cacheable;
	}
	public void setCacheable(boolean cacheable) {
		this._cacheable = cacheable;
	}
	
	private String _listName;
	private String _contentType;
	private String _category;
	private EntitySearchFilter[] _filters = new EntitySearchFilter[0];
	
	private boolean _cacheable = true;
	
	private boolean _listEvaluated;
	
}