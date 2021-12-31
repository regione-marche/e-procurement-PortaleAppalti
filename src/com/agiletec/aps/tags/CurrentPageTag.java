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
import com.agiletec.aps.system.services.lang.ILangManager;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.util.ApsProperties;
import com.agiletec.aps.util.ApsWebApplicationUtils;

/**
 * Tag visualizzatore di valori del bean pagina corrente.
 * E' possibile richiedere il titolo nella lingua corrente (param "title") o il codice (param "code").
 * Se il parametro "param" non è specificato, restituisce il titolo nella lingua corrente.
 * @author E.Santoboni
 */
public class CurrentPageTag extends TagSupport {

	@Override
	public int doStartTag() throws JspException {
		ServletRequest request = this.pageContext.getRequest();
		RequestContext reqCtx = (RequestContext) request.getAttribute(RequestContext.REQCTX);
		Lang currentLang = (Lang) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG);
		try {
			IPage page = (IPage) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE);
			ApsProperties titles = page.getTitles();
			if (this.getParam() == null || this.getParam().equals("title")) {
				if ((_langCode == null) || (_langCode.equals(""))
						|| (currentLang.getCode().equalsIgnoreCase(_langCode))) {
					// restituisci il titolo nella lingua corrente
					_value = titles.getProperty(currentLang.getCode());
				} else {
					_value = titles.getProperty(_langCode);
				}
				if (_value == null || _value.trim().equals("")) {
					ILangManager langManager = 
						(ILangManager) ApsWebApplicationUtils.getBean(SystemConstants.LANGUAGE_MANAGER, this.pageContext);
					_value = titles.getProperty(langManager.getDefaultLang().getCode());
				}
			} else if (this.getParam().equals("code")) {
				_value = page.getCode();
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "doStartTag");
			throw new JspException("Error during tag initialization ", t);
		}
		return EVAL_BODY_INCLUDE;
	}

	/**
	 * Rende disponibile il titolo della pagina corrente 
	 * su una variabile o lo stampa in output.
	 * @see javax.servlet.jsp.tagext.Tag#doEndTag()
	 */
	@Override
	public int doEndTag() throws JspException {
		if (_varName != null) {
			this.pageContext.setAttribute(_varName, _value);
		} else {
			try {
				this.pageContext.getOut().print(_value);
			} catch (IOException e) {
				ApsSystemUtils.logThrowable(e, this, "doEndTag");
				throw new JspException("Error closing tag ", e);
			}
		}
		return EVAL_PAGE;
	}

	@Override
	public void release() {
		this._langCode = null;
		this._varName = null;
		this._value = null;
	}

	/**
	 * Restituisce il codice della lingua nel quale 
	 * si richiede il titolo della pagina.
	 * @return Il codice lingua.
	 */
	public String getLangCode() {
		return _langCode;
	}

	/**
	 * Imposta il codice della lingua nel quale 
	 * si richiede il titolo della pagina.
	 * @param langCode Il codice lingua.
	 */
	public void setLangCode(String langCode) {
		this._langCode = langCode;
	}

	/**
	 * Imposta il nome della variabile mediante il quale 
	 * inserire il valore del parametro di pagina richiesto nel pageContext.
	 * @param var Il nome della variabile.
	 */
	public void setVar(String var) {
		this._varName = var;
	}

	/**
	 * Restituisce il nome della variabile mediante il quale 
	 * inserire il valore del parametro di pagina richiesto nel pageContext.
	 * @return Il nome della variabile.
	 */
	protected String getVar() {
		return _varName;
	}

	protected String getParam() {
		return _param;
	}
	public void setParam(String param) {
		this._param = param;
	}

	private String _varName;
	private String _langCode;
	private String _value;

	private String _param;

}
