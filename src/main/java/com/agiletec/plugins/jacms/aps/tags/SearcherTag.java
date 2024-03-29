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
import com.agiletec.plugins.jacms.aps.tags.util.SearcherTagHelper;

/**
 * Tag esecutore ricerca.
 * Estrae una lista di identificativi di contenuto come risultato di 
 * una ricerca nei contenuti publicati nel portale in base ad una parola chiave.
 * @author E.Santoboni
 */
public class SearcherTag extends TagSupport {
	
	@Override
	public int doStartTag() throws JspException {
		ServletRequest request =  this.pageContext.getRequest();
		RequestContext reqCtx = (RequestContext) request.getAttribute(RequestContext.REQCTX);
		try {
			String word = request.getParameter("search");
			SearcherTagHelper helper = new SearcherTagHelper();
			List<String> result = helper.executeSearch(word, reqCtx);
			this.pageContext.setAttribute(this.getListName(), result);
			request.setAttribute("search", word);
		} catch (Throwable e) {
			ApsSystemUtils.logThrowable(e, this, "doStartTag");
			throw new JspException("Errore inizializzazione tag", e);
		}
		return SKIP_BODY;
	}
	
	/**
	 * @return Returns the listName.
	 */
	public String getListName() {
		return _listName;
	}
	
	/**
	 * @param listName The listName to set.
	 */
	public void setListName(String listName) {
		this._listName = listName;
	}
	
	private String _listName;

}
