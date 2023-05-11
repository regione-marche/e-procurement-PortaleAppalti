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
import com.agiletec.aps.tags.util.HeadInfoContainer;

/**
 * Tag di utilità per la compilazione delle informazioni di testata
 * della pagina html. Verifica la presenza di informazioni del tipo specificato.
 * @author E.Santoboni
 */
public class CheckHeadInfoOutputterTag extends TagSupport {

	public int doStartTag() throws JspException {
		ServletRequest request =  this.pageContext.getRequest();
		RequestContext reqCtx = (RequestContext) request.getAttribute(RequestContext.REQCTX);
		HeadInfoContainer headInfo = (HeadInfoContainer) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_HEAD_INFO_CONTAINER);
		List<Object> infos = headInfo.getInfos(this.getType());
		if (infos == null || infos.size() == 0) {
			return SKIP_BODY;
		} else {
			return EVAL_BODY_INCLUDE;
		}
	}

	public void release() {
		this._type = null;
	}

	/**
	 * Restituisce il tipo di informazione oggetto della verifica.
	 * @return Il tipo di informazione.
	 */
	public String getType() {
		return _type;
	}

	/**
	 * Setta il tipo di informazione oggetto della verifica.
	 * @param type Il tipo di informazione.
	 */
	public void setType(String type) {
		this._type = type;
	}

	private String _type;

}