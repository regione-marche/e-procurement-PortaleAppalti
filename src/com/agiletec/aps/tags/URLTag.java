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

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.url.IURLManager;
import com.agiletec.aps.system.services.url.PageURL;
import com.agiletec.aps.util.ApsWebApplicationUtils;

/**
 * Genera l'URL corrispondente ad una pagina del portale. L'URL è emesso in output
 * o reso disponibile in una variabile. L'URL è generato sulla base degli attributi
 * di pagina e lingua specificati, o, in default, dai corrispondenti valori correnti.
 * Per l'inserimento di parametri nella query string è previsto il sotto-tag URLParTag.
 * @author 
 */
public class URLTag extends TagSupport {
	
	/**
	 * Predispone un oggetto di tipo PageURL. Questo potrà essere popolato
	 * anche da eventuali sotto-tag.
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	 */
	public int doStartTag() throws JspException {
		ServletRequest request =  this.pageContext.getRequest();
		RequestContext reqCtx = (RequestContext) request.getAttribute(RequestContext.REQCTX);
		try {
			IURLManager urlManager = 
				(IURLManager) ApsWebApplicationUtils.getBean(SystemConstants.URL_MANAGER, this.pageContext);
			_pageUrl = urlManager.createURL(reqCtx);
			if (_pageCode != null) {
				_pageUrl.setPageCode(_pageCode);
			}
			if (_langCode != null) {
				_pageUrl.setLangCode(_langCode);
			}
			if (_paramRepeat) {
				_pageUrl.setParamRepeat();
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "doStartTag");
			throw new JspException("Error during tag initialization", t);
		}
		return EVAL_BODY_INCLUDE;
	}
	
	/**
	 * Completa il popolamento dell'URL e lo rende disponibile su una variabile
	 * o in output.
	 * @see javax.servlet.jsp.tagext.Tag#doEndTag()
	 */
	public int doEndTag() throws JspException {
		String url = _pageUrl.getURL();
		if (_varName != null) {
			this.pageContext.setAttribute(_varName, url);
		} else {
			try {
				this.pageContext.getOut().print(url);
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "doEndTag");
				throw new JspException("Error closing tag", t);
			}
		}
		return EVAL_PAGE;
	}
	
	/**
	 * Aggiunge un parametro di query string all'URL. Questo metodo è invocato
	 * da eventuali sotto-tag.
	 * @param name Nome del parametro.
	 * @param value Valore del parametro.
	 */
	public void addParam(String name, String value){
		_pageUrl.addParam(name, value);
	}
		
	/**
	 * Restituisce l'attributo lingua.
	 * @return Il codice lingua
	 */
	public String getLang() {
		return _langCode;
	}

	/**
	 * Imposta l'attributo lingua
	 * @param lang Il codice lingua
	 */
	public void setLang(String lang) {
		this._langCode = lang;
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
	
	/**
	 * Restituisce l'attributo che definisce il nome della variabile di output.
	 * @return Il nome della variabile
	 */
	public String getVar() {
		return _varName;
	}
	
	/**
	 * Imposta l'attributo che definisce il nome della variabile di output.
	 * @param var Il nome della variabile
	 */
	public void setVar(String var) {
		this._varName = var;
	}
	
	/**
	 * True se si deve ripetere la querystring relativa ai 
	 * parametri della request precedente, false in caso contrario.
	 * @return Returns the parRepeat.
	 */
	public boolean isParamRepeat() {
		return _paramRepeat;
	}
	
	/**
	 * Setta se ripetere la querystring relativa ai parametri della request precedente.
	 * @param paramRepeat The parRepeat to set.
	 */
	public void setParamRepeat(boolean paramRepeat) {
		this._paramRepeat = paramRepeat;
	}
	
	public void release() {
		_langCode = null;
		_pageCode = null;
		_varName = null;
		_paramRepeat = false;
		_pageUrl = null;
	}
	
	private String _langCode;
	private String _pageCode;
	private String _varName;
	private boolean _paramRepeat;
	private PageURL _pageUrl;

}
