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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import org.apache.struts2.views.jsp.StrutsBodyTagSupport;
import org.springframework.web.context.WebApplicationContext;

import com.agiletec.aps.util.ApsWebApplicationUtils;
import com.agiletec.apsadmin.system.plugin.PluginSubMenuContainer;
import com.opensymphony.xwork2.util.ValueStack;

/**
 * Tag di utilità generatore dei menù dei plugins inseriti nel sistema.
 * Il tag si occupa della ricerca degli oggetti di tipo {@link PluginSubMenuContainer} 
 * per la erogazione nel menù di sinistra dell'area di amministrazione dei "subMenu" 
 * relativi a ciascun plugin.
 * @version 1.0
 * @author E.Santoboni
 */
public class PluginsSubMenuTag extends StrutsBodyTagSupport {
	
	public int doStartTag() throws JspException {
		HttpServletRequest request = (HttpServletRequest) this.pageContext.getRequest();
		WebApplicationContext wac = ApsWebApplicationUtils.getWebApplicationContext(request);
		List<PluginSubMenuContainer> containters = new ArrayList<PluginSubMenuContainer>();
		ValueStack stack = this.getStack();
		try {
			String[] beanNames =  wac.getBeanNamesForType(PluginSubMenuContainer.class);
			for (int i=0; i<beanNames.length; i++) {
				PluginSubMenuContainer container = (PluginSubMenuContainer) wac.getBean(beanNames[i]);
				containters.add(container);
			}
			if (containters.size()>0) {
				stack.getContext().put(this.getObjectName(), containters);
	            stack.setValue("#attr['" + this.getObjectName() + "']", containters, false);
	            return EVAL_BODY_INCLUDE;
			}
		} catch (Throwable t) {
			throw new JspException("Errore in creazione lista menu plugins", t);
		}
        return super.doStartTag();
    }
    
    protected String getObjectName() {
		return _objectName;
	}
	public void setObjectName(String objectName) {
		this._objectName = objectName;
	}
    
    private String _objectName;
	
}
