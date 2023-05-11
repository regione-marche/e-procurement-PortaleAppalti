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

import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.Showlet;
import com.agiletec.aps.system.services.page.showlet.INavigatorParser;
import com.agiletec.aps.system.services.page.showlet.NavigatorTarget;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.aps.util.ApsWebApplicationUtils;

/**
 * Tag iterativo che genera una lista con un sottoinsieme di pagine, estratto
 * @author M.Diana - E.Santoboni
 */
public class NavigatorTag extends TagSupport {
	
	public int doStartTag() throws JspException {
		ServletRequest request =  this.pageContext.getRequest();
		this._reqCtx = (RequestContext) request.getAttribute(RequestContext.REQCTX);
		INavigatorParser navigatorParser = (INavigatorParser) ApsWebApplicationUtils.getBean(SystemConstants.NAVIGATOR_PARSER, this.pageContext); 
		IPage currPage = (IPage) _reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE);
		UserDetails currentUser = (UserDetails) this.pageContext.getSession().getAttribute(SystemConstants.SESSIONPARAM_CURRENT_USER);
		if (_spec == null) {
			Showlet currShowlet =  (Showlet) _reqCtx.getExtraParam((SystemConstants.EXTRAPAR_CURRENT_SHOWLET));
			String spec = currShowlet.getConfig().getProperty(SHOWLET_SPEC_PROPERTY);
			this._targets = navigatorParser.parseSpec(spec, currPage, currentUser);
		} else {
			this._targets = navigatorParser.parseSpec(_spec, currPage, currentUser);
		}
		if (_targets == null || _targets.isEmpty()) {
			return SKIP_BODY;
		} else {
			_index = 0;
			NavigatorTarget currentTarget = this.getCurrentTarget();
			this.pageContext.setAttribute(this.getVar(), currentTarget);
			return EVAL_BODY_INCLUDE;
		}
	}
	
	@Override
	public int doAfterBody() throws JspException {
		_index++;
		if (_index >= _targets.size()) {
			return SKIP_BODY;
		} else {
			NavigatorTarget currentTarget = this.getCurrentTarget();
			this.pageContext.setAttribute(this.getVar(), currentTarget);
			return EVAL_BODY_AGAIN;
		}
	}
	
	private NavigatorTarget getCurrentTarget() {
		NavigatorTarget item = (NavigatorTarget) _targets.get(_index);
		item.setRequestContext(this._reqCtx);
		return item;
	}
	
	@Override
	public int doEndTag() throws JspException {
		this.pageContext.removeAttribute(this.getVar());
		return super.doEndTag();
	}
	
	public void release() {
		_spec = null;
		_targets = null;
		_reqCtx = null;
		_var = null;
	}
	
	/**
	 * Restituisce l'attributo che specifica il sottoinsieme di pagine.
	 * @return La specifica.
	 */
	public String getSpec() {
		return _spec;
	}
	
	/**
	 * Imposta l'attributo che specifica il sottoinsieme di pagine.
	 * @param spec La specifica da impostare.
	 */
	public void setSpec(String spec) {
		this._spec = spec;
	}
	
	/**
	 * Restituisce il nome della variabile con cui viene inserito 
	 * il target corrente nel contesto della pagina.
	 * @return Il nome della variabile caratterizzante il target corrente.
	 */
	public String getVar() {
		return _var;
	}
	
	/**
	 * Setta il nome della variabile con cui viene inserito 
	 * il target corrente nel contesto della pagina.
	 * @param var Il nome della variabile caratterizzante il target corrente.
	 */
	public void setVar(String var) {
		this._var = var;
	}
	
	public static final String SHOWLET_SPEC_PROPERTY = "navSpec";
	private String _spec;
	private List _targets;
	private int _index;
	private String _var;
	private RequestContext _reqCtx;
	
}
