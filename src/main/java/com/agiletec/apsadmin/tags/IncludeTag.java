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
package com.agiletec.apsadmin.tags;

import javax.servlet.jsp.JspException;

/**
 * Rewriting of the Original IncludeTag from Struts 2 (version 2.0.11.1) for extension 
 * of attribute "value" (to allow to use EL).
 * @version 1.0
 * @author E.Santoboni
 */
public class IncludeTag extends org.apache.struts2.views.jsp.IncludeTag {
	
	@Override
	public int doStartTag() throws JspException {
		this.getActualValue(this.value);
		return super.doStartTag();
	}
	
	private void getActualValue(String value) {
		if (value.startsWith("%{") && value.endsWith("}")) {
			value = value.substring(2, value.length() - 1);
			this.value = (String) getStack().findValue(value, String.class);;
        }
	}
	
}
