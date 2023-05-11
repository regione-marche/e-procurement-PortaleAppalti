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

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.common.entity.model.EntitySearchFilter;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import com.agiletec.plugins.jacms.aps.system.JacmsSystemConstants;
import com.agiletec.plugins.jacms.aps.system.services.content.showlet.IContentListBean;
import com.agiletec.plugins.jacms.aps.system.services.content.showlet.IContentListFilterBean;
import com.agiletec.plugins.jacms.aps.system.services.content.showlet.IContentListHelper;

/**
 * SottoTag di utilità per il tag ContentListTag.
 * Il SottoTag rappresenta un singolo filtro per la ricerca dei contenuti, epuò essere inserito in numero 
 * variabile all'interno del tag AdvancedContentListTag; il criterio di filtro ed ordinamento 
 * dei contenuti rispetterà l'ordine dei sottotag Filter inseriti.
 * @author E.Santoboni
 */
public class ContentListFilterTag extends TagSupport implements IContentListFilterBean {
	
	public ContentListFilterTag() {
		super();
		this.release();
	}
	
	@Override
	public int doEndTag() throws JspException {
		ServletRequest request =  this.pageContext.getRequest();
		RequestContext reqCtx = (RequestContext) request.getAttribute(RequestContext.REQCTX);
		try {
			if (!this.isRightKey()) {
				String message = "";
				for (int i=0; i<IContentListHelper.allowedMetadataFilterKeys.length; i++) {
					if (i!=0) message.concat(",");
					message.concat(IContentListHelper.allowedMetadataFilterKeys[i]);
				}
				throw new RuntimeException("Chiave '" + this.getKey() + "' NON Riconosciuta; " +
						"Deve essere una di quelle riconosciute - " + message);
			}
			IContentListHelper helper = (IContentListHelper) ApsWebApplicationUtils.getBean(JacmsSystemConstants.CONTENT_LIST_HELPER, this.pageContext);
			IContentListBean parent = (IContentListBean) findAncestorWithClass(this, IContentListBean.class);
			String contentType = parent.getContentType();
			EntitySearchFilter filter = helper.getFilter(contentType, this, reqCtx);
			if (null != filter) {
				parent.addFilter(filter);
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "doEndTag");
			throw new JspException("Errore tag", t);
		}
		return super.doEndTag();
	}
	
	private boolean isRightKey() {
		if (this.isAttributeFilter()) {
			return true;
		} else {
			for (int i = 0; i < IContentListHelper.allowedMetadataFilterKeys.length; i++) {
				if (IContentListHelper.allowedMetadataFilterKeys[i].equals(this.getKey())) return true;
			}
		}
		return false;
	}
	
	@Override
	public void release() {
		this._key = null;
		this._attributeFilter = false;
		this._value = null;
		this._order = null;
		this._start = null;
		this._end = null;
		this._likeOption = false;
	}
	
	@Override
	public String getKey() {
		return _key;
	}
	public void setKey(String key) {
		this._key = key;
	}
	@Override
	public boolean isAttributeFilter() {
		return _attributeFilter;
	}
	public void setAttributeFilter(boolean attributeFilter) {
		this._attributeFilter = attributeFilter;
	}
	@Override
	public String getValue() {
		return _value;
	}
	public void setValue(String value) {
		this._value = value;
	}
	@Override
	public String getStart() {
		return _start;
	}
	public void setStart(String start) {
		this._start = start;
	}
	@Override
	public String getEnd() {
		return _end;
	}
	public void setEnd(String end) {
		this._end = end;
	}
	@Override
	public String getOrder() {
		return _order;
	}
	public void setOrder(String order) {
		this._order = order;
	}
	@Override
	public boolean getLikeOption() {
		return this._likeOption;
	}
	public void setLikeOption(boolean likeOption) {
		this._likeOption = likeOption;
	}
	
	private String _key;
	private boolean _attributeFilter;
	private String _value;
	private String _start;
	private String _end;
	private String _order;
	private boolean _likeOption;
	
}
