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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.i18n.II18nManager;
import com.agiletec.aps.system.services.lang.ILangManager;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.util.ApsWebApplicationUtils;

/**
 * Tag per l'internazionalizzazione delle label.
 * @author S.Didaci
 */
public class I18nTag extends TagSupport {

	public int doStartTag() throws JspException {
		RequestContext reqCtx = (RequestContext) this.pageContext.getRequest().getAttribute(RequestContext.REQCTX);
		Lang currentLang = null;;
		if (reqCtx != null) {
			currentLang = (Lang) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG);
		} else {
			ILangManager langManager = (ILangManager) ApsWebApplicationUtils.getBean(SystemConstants.LANGUAGE_MANAGER, this.pageContext);
			currentLang = langManager.getDefaultLang();
		}
		try {
			this.extractLabel(currentLang);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "doStartTag");
			throw new JspException("Error during tag initialization", t);
		}
		return EVAL_BODY_INCLUDE;
	}

	private void extractLabel(Lang currentLang) throws ApsSystemException {
		II18nManager i18nManager = (II18nManager) ApsWebApplicationUtils.getBean(SystemConstants.I18N_MANAGER, this.pageContext);
		try {
			if (_lang == null 
					|| _lang.equals("") 
					|| currentLang.getCode().equalsIgnoreCase(_lang)) {
				_label = i18nManager.getLabel(_key, currentLang.getCode());
				if (_label == null) {
					ILangManager langManager = 
						(ILangManager) ApsWebApplicationUtils.getBean(SystemConstants.LANGUAGE_MANAGER, this.pageContext);
					Lang defaultLang = langManager.getDefaultLang();
					_label = i18nManager.getLabel(_key, defaultLang.getCode());
				}
			} else {
				_label = i18nManager.getLabel(_key, _lang);
			}
			if (_label == null) {
				_label = _key;
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "extractLabel");
			throw new ApsSystemException("Error getting label", t);
		}
	}

	/**
	 * Completa il popolamento della label e la rende disponibile su una variabile
	 * o in output.
	 * @see javax.servlet.jsp.tagext.Tag#doEndTag()
	 */
	public int doEndTag() throws JspException {
		if (_varName != null) {
			this.pageContext.setAttribute(_varName, _label);
		} else {
			try {
				this.pageContext.getOut().print(_label);
			} catch (IOException e) {
				throw new JspException("Error closing the tag ", e);
			}
		}
		return EVAL_PAGE;
	}

	public void release() {
		this._key = null;
		this._lang = null;
		this._varName = null;
		this._label = null;
	}

	/**
	 * Restituisce l'attributo codice lingua.
	 * @return Il codice lingua.
	 */
	public String getLang() {
		return _lang;
	}

	/**
	 * Imposta l'attributo codice lingua.
	 * @param lang Il codice lingua.
	 */
	public void setLang(String lang) {
		this._lang = lang;
	}

	/**
	 * Imposta l'attributo che definisce il nome della variabile di output.
	 * @param var Il nome della variabile.
	 */
	public void setVar(String var) {
		this._varName = var;
	}

	/**
	 * Restituisce l'attributo che definisce il nome della variabile di output.
	 * @return Il nome della variabile.
	 */
	public String getVar() {
		return _varName;
	}

	/**
	 * Restituisce l'attributo chiave mediante il quale estrarre la label desiderata.
	 * @return La chiave mediante il quale estrarre la label desiderata.
	 */
	public String getKey() {
		return _key;
	}

	/**
	 * Imposta l'attributo chiave mediante il quale estrarre la label desiderata.
	 * @param _key La chiave mediante il quale estrarre la label desiderata.
	 */
	public void setKey(String _key) {
		this._key = _key;
	}

	private String _varName;

	private String _key;

	private String _lang;

	private String _label;

}