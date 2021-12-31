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
package com.agiletec.aps.tags;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.url.IURLManager;
import com.agiletec.aps.system.services.url.PageURL;
import com.agiletec.aps.util.ApsWebApplicationUtils;

/**
 * Genera l'URL corrispondente al richiamo di una funzionalità di una servlet interna.
 * @author M.Casari
 */
public class ActionURLTag extends TagSupport {
	
	public int doStartTag() throws JspException {
		return SKIP_BODY;
	}
	
	public int doEndTag() throws JspException {	
		IURLManager urlManager = (IURLManager) ApsWebApplicationUtils.getBean(SystemConstants.URL_MANAGER, this.pageContext);
		ServletRequest request =  this.pageContext.getRequest();
		RequestContext reqCtx = (RequestContext) request.getAttribute(RequestContext.REQCTX);
		PageURL url = urlManager.createURL(reqCtx);
		if (_pageCode == null) {
			IPage currPage = (IPage) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE);
			url.setPage(currPage);
		} else {
			url.setPageCode(_pageCode);
		}
		
		StringBuffer urlString = new StringBuffer(url.getURL());
		if (urlString!= null && _path != null) {	
			if (urlString.indexOf("?") != -1) {
				if (urlString.indexOf("&") == -1 
						&& urlString.indexOf("&amp;") == -1 
						&& urlString.indexOf("&#38;") == -1) {
					urlString.append("&amp;");
				}
			} else {
				urlString.append("?");
			}
			urlString.append("actionPath=").append(this._path);
			Integer currentFrame = (Integer) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_FRAME);
			urlString.append("&amp;currentFrame=").append(currentFrame.intValue());
		}
		try {
			this.pageContext.getOut().print(urlString);
		} catch (IOException e) {
			ApsSystemUtils.logThrowable(e, this, "doEndTag");
			throw new JspException("Error closing tag ", e);
		}
		return EVAL_PAGE;
	}
	
	/**
	 * Restituisce il path relativo dell'"azione" o della pagina da richiamare.
	 * @return Il path relativo dell'"azione" o della pagina da richiamare.
	 */
	public String getPath() {
		return _path;
	}

	/**
	 * Setta il path relativo dell'"azione" o della pagina da richiamare.
	 * @param path Il path relativo dell'"azione" o della pagina da richiamare.
	 */
	public void setPath(String path) {
		this._path = path;
	}
	
	/**
	 * Restituisce l'attributo pagina.
	 * @return Il codice pagina
	 */
	public String getPage() {
		return _pageCode;
	}
	
	/**
	 * Imposta l'attributo pagina
	 * @param page Il codice pagina
	 */
	public void setPage(String page) {
		this._pageCode = page;
	}
	
	private String _path;
	private String _pageCode;

}
