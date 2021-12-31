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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * Sotto tag di URLTag, per l'aggiunta di parametri di query string.
 * E' previsto l'attributo "name", mentre il valore del paramero deve essere
 * indicato nel corpo del tag.
 * @author 
 */
public class URLParTag extends BodyTagSupport {
	
	public int doEndTag() throws JspException {
		BodyContent body = this.getBodyContent();
		String value = body.getString();
		URLTag parentTag = null;
		try {
			parentTag = (URLTag) this.getParent();
		} catch (RuntimeException e) {
			throw new JspException("Nesting error of the parameter tag detected in a URL tag", e);
		}
		parentTag.addParam(this.getName(), value);
		return super.doEndTag();
	}
	
	/**
	 * Restituisce l'attributo "name".
	 * @return L'attributo name
	 */
	public String getName() {
		return _name;
	}
	
	/**
	 * Imposta l'attributo "name".
	 * @param name L'attributo name
	 */
	public void setName(String name) {
		this._name = name;
	}
	
	private String _name;
	
}
