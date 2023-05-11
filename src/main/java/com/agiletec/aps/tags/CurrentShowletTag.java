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
import com.agiletec.aps.system.services.lang.ILangManager;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.Showlet;
import com.agiletec.aps.system.services.showlettype.ShowletType;
import com.agiletec.aps.util.ApsWebApplicationUtils;

/**
 * Restituisce informazioni riguardo la showlet nel quale il tag è inserito.
 * Sono disponibili i seguenti valori di parametri per l'attributo "param".
 * - "code" per la restituzione del codice del tipo di showlet associata (niente nel caso di nessuna showlet associata).
 * - "title" per la restituzione del nome del tipo di showlet associata (niente nel caso di nessuna showlet associata).
 * - "config" per la restituzione di un valore di configurazione. In tal caso è necessario valorizzare anche l'attributo "configParam".
 * Nel caso si desiderino informazioni riguardo ad una showlet inserita in un frame diverso da quello corrente, 
 * è necessario specificare il frame nell'attributo "frame".
 * @author E.Santoboni - E.Mezzano
 */
public class CurrentShowletTag extends TagSupport {
	
	public int doStartTag() throws JspException {
		try {
			Showlet showlet = this.extractShowlet();
			if (null == showlet) return super.doStartTag();
			String value = null;
			if ("code".equals(this.getParam())) {
				value = showlet.getType().getCode();
			} else if ("title".equals(this.getParam())) {
				value = this.extractTitle(showlet);
			} else if ("config".equals(this.getParam())) {
				value = showlet.getConfig().getProperty(this.getConfigParam());
			}
			if (null != value) {
				String var = this.getVar();
				if (null == var || "".equals(var)) {
					this.pageContext.getOut().print(value);
				} else {
					this.pageContext.setAttribute(this.getVar(), value);
				}
			}
		} catch (Throwable t) {
			String msg = "Error detected during showlet preprocessing";
			ApsSystemUtils.logThrowable(t, this, "doEndTag", msg);
			throw new JspException(msg, t);
		}
		return super.doStartTag();
	}
	
	private String extractTitle(Showlet showlet) {
		ServletRequest request = this.pageContext.getRequest();
		RequestContext reqCtx = (RequestContext) request.getAttribute(RequestContext.REQCTX);
		Lang currentLang = (Lang) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG);
		
		ShowletType type = showlet.getType();
		String value = type.getTitles().getProperty(currentLang.getCode());
		if (null == value || value.trim().length() == 0) {
			ILangManager langManager = 
				(ILangManager) ApsWebApplicationUtils.getBean(SystemConstants.LANGUAGE_MANAGER, this.pageContext);
			Lang defaultLang = langManager.getDefaultLang();
			value = type.getTitles().getProperty(defaultLang.getCode());
		}
		return value;
	}
	
	private Showlet extractShowlet() {
		ServletRequest req =  this.pageContext.getRequest();
		RequestContext reqCtx = (RequestContext) req.getAttribute(RequestContext.REQCTX);
		Showlet showlet = null;
		if (this.getFrame() < 0) {
			showlet = (Showlet) reqCtx.getExtraParam((SystemConstants.EXTRAPAR_CURRENT_SHOWLET));
		} else {
			IPage currentPage = (IPage) reqCtx.getExtraParam((SystemConstants.EXTRAPAR_CURRENT_PAGE));
			Showlet[] showlets = currentPage.getShowlets();
			if (showlets.length > this.getFrame()) {
				showlet = showlets[this.getFrame()];
			}
		}
		return showlet;
	}
	
	@Override
	public void release() {
		super.release();
		this._param = null;
		this._configParam = null;
		this._var = null;
		this._frame = -1;
	}
	
	public String getParam() {
		return _param;
	}
	public void setParam(String param) {
		this._param = param;
	}
	
	public String getConfigParam() {
		return _configParam;
	}
	public void setConfigParam(String configParam) {
		this._configParam = configParam;
	}
	
	public String getVar() {
		return _var;
	}
	public void setVar(String var) {
		this._var = var;
	}
	
	public int getFrame() {
		return _frame;
	}
	public void setFrame(int frame) {
		this._frame = frame;
	}
	
	private String _param;
	private String _configParam;
	private String _var;
	
	private int _frame = -1;
	
}