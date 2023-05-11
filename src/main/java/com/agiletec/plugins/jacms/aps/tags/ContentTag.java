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
import com.agiletec.aps.util.ApsWebApplicationUtils;
import com.agiletec.plugins.jacms.aps.system.JacmsSystemConstants;
import com.agiletec.plugins.jacms.aps.system.services.content.showlet.IContentViewerHelper;

/**
 * Restituisce nella pagina il contenuto richiesto tramite l'id.
 * @author 
 */
public class ContentTag extends TagSupport {
	
	public ContentTag() {
		super();
		this.release();
	}
	
    @Override
	public int doStartTag() throws JspException {
		ServletRequest request =  this.pageContext.getRequest();
		RequestContext reqCtx = (RequestContext) request.getAttribute(RequestContext.REQCTX);
		try {
			IContentViewerHelper helper = (IContentViewerHelper) ApsWebApplicationUtils.getBean(JacmsSystemConstants.CONTENT_VIEWER_HELPER, this.pageContext);
			String renderedContent = helper.getRenderedContent(this.getContentId(), this.getModelId(), reqCtx);
			this.pageContext.getOut().print(renderedContent);
		} catch (Throwable e) {
			ApsSystemUtils.logThrowable(e, this, "doStartTag");
			throw new JspException("Errore inizializzazione tag", e);
		}
		return EVAL_PAGE;
	}
	
    @Override
	public void release() {
    	this.setContentId(null);
    	this.setModelId(null);
	}

	public String getContentId() {
        return _contentId;
    }
    
	public String getModelId() {
        return _modelId;
    }
    
	public void setContentId(String id) {
        _contentId = id;
    }
    
    public void setModelId(String id) {
        _modelId = id;
    }
    
	private String _contentId;
	private String _modelId;

}
