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
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.aps.system.services.lang.ILangManager;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.util.ApsWebApplicationUtils;

/**
 * Tag per la visualizzazione di informazioni generali sul sistema e il contesto.
 * key: informazione desiderata
 * Lista delle chiavi:
 * - currentLang: la lingua corrente.
 * - langs : la lista di tutte le lingue del sistema.
 * - systemParam : il valore di un parametro di sistema; 
 * in tal caso bisogna valorizzare l'attributo "paramName".
 * @author Wiz of Id <wiz@apritisoftware.it>
 */
public class InfoTag extends TagSupport {

	public int doStartTag() throws JspException {
		ServletRequest request =  this.pageContext.getRequest();
		try {
			if ("defaultLang".equals(_key)) {
				ILangManager langManager = (ILangManager) ApsWebApplicationUtils.getBean(SystemConstants.LANGUAGE_MANAGER, this.pageContext);
				_info = langManager.getDefaultLang().getCode();
			} else if ("currentLang".equals(_key)) {
				RequestContext reqCtx = (RequestContext) request.getAttribute(RequestContext.REQCTX);
				Lang currentLang = (Lang) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG);
				_info = currentLang.getCode();
			} else if ("langs".equals(_key)) {
				ILangManager langManager = (ILangManager) ApsWebApplicationUtils.getBean(SystemConstants.LANGUAGE_MANAGER, this.pageContext);
				_info = langManager.getLangs();
			} else if ("systemParam".equals(_key)) {
				ConfigInterface confManager = (ConfigInterface) ApsWebApplicationUtils.getBean(SystemConstants.BASE_CONFIG_MANAGER, this.pageContext);
				_info = confManager.getParam(this.getParamName());
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "doStartTag");
			throw new JspException("Error during tag initialization", t);
		}
		return EVAL_BODY_INCLUDE;
	}

	/**
	 * Completa il popolamento della label e la rende 
	 * disponibile su una variabile o in output.
	 * @see javax.servlet.jsp.tagext.Tag#doEndTag()
	 */
	public int doEndTag() throws JspException {
		if (_varName != null) {
			this.pageContext.setAttribute(_varName, _info);
		} else {
			try {
				this.pageContext.getOut().print(_info);
			} catch (IOException e) {
			}
		}
		return EVAL_PAGE;
	}

	public void release() {
		_key = null;
		_varName = null;
		_info = null;
		_paramName = null;
	}

	/**
	 * Restituisce l'attributo chiave.
	 * @return la chiave relativa all'informazione richiesta.
	 */
	public String getKey() {
		return _key;
	}

	/**
	 * Imposta l'attributo chiave.
	 * @param key la chiave relativa all'informazione richiesta.
	 */
	public void setKey(String key) {
		this._key = key;
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
	 * @return Il nome della variabile di output.
	 */
	public String getVar() {
		return _varName;
	}

	/**
	 * Restituisce il nome del parametro di sistema richiesto.
	 * Il nome del parametro viene richiesto esclusivamente nel caso che la chiave immessa sia "systemParam".
	 * @return Il nome del parametro di sistema richiesto.
	 */
	public String getParamName() {
		return _paramName;
	}

	/**
	 * Setta il nome del parametro di sistema richiesto.
	 * Il nome del parametro viene richiesto esclusivamente nel caso che la chiave immessa sia "systemParam".
	 * @param paramName Il nome del parametro di sistema richiesto.
	 */
	public void setParamName(String paramName) {
		this._paramName = paramName;
	}

	private String _key;
	private String _varName;
	private String _paramName;
	private Object _info;

}
