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

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.agiletec.aps.system.ApsSystemUtils;

/**
 * Tag per la Content Negotiation.
 * Controlla se il Mime-Type richiesto è accettato dallo User Agent. In caso positivo lo dichiara, in caso negativo dichiara text/html.
 * Alla dichiarazione unisce il charset indicato.
 * @author William Ghelfi
 */
public class ContentNegotiationTag extends TagSupport {
	
	public int doStartTag() throws JspException {
		try {
			boolean isAcceptedMimeType = isAcceptedMimeType(this.getMimeType());
			if (!isAcceptedMimeType) {
				this.setMimeType(ContentNegotiationTag.DEFAULT_MIMETYPE);
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "doStartTag");
			throw new JspException("Error during tag initialization ", t);
		}
		return super.doStartTag();
	}
	
	/**
	 * Dichiara il corretto contentType frutto della Content Negotiation
	 * @see javax.servlet.jsp.tagext.Tag#doEndTag()
	 */
	public int doEndTag() throws JspException {
		ServletResponse response = this.pageContext.getResponse();
		StringBuffer contentType = new StringBuffer(this.getMimeType());
		contentType.append("; charset=");
		contentType.append(this.getCharset());
		response.setContentType(contentType.toString());
		return EVAL_PAGE;
	}
	
	public void release() {
		this._mimeType = null;
		this._charset = null;
	}

	public String getMimeType() {
		return _mimeType;
	}
	public void setMimeType(String mimeType) {
		this._mimeType = mimeType;
	}
	public String getCharset() {
		return _charset;
	}
	public void setCharset(String charset) {
		this._charset = charset;
	}
	
	private boolean isAcceptedMimeType(String mimeType) {
		boolean isAcceptedMimeType = false;
		HttpServletRequest request =  (HttpServletRequest) this.pageContext.getRequest();
		String header = request.getHeader("accept");
		if (null != header) {
			isAcceptedMimeType = (header.indexOf(mimeType) >= 0);
		}
		return isAcceptedMimeType;
	}
	
	private String _mimeType;
	private String _charset;
	
	/**
	 * Mime-Type di default, da usare se quello specificato non è accettato dallo User Agent.
	 */
	private static final String DEFAULT_MIMETYPE = "text/html";
	
}
