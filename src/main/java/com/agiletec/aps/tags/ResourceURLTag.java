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
import javax.servlet.jsp.tagext.TagSupport;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.aps.util.ApsWebApplicationUtils;

/**
 * Tag visualizzatore URL delle Risorse.
 * Due attributi non obbligatori: root (di default prende il valore del 
 * parametro corrispondente a SystemConstants.PAR_RESOURCES_ROOT_URL) e folder (di default "").
 * @author E.Santoboni
 */
public class ResourceURLTag extends TagSupport {
	
	public int doEndTag() throws JspException {
		try {
			if (null == _root) {
				ConfigInterface configService = (ConfigInterface) ApsWebApplicationUtils.getBean(SystemConstants.BASE_CONFIG_MANAGER, this.pageContext);
				_root = configService.getParam(SystemConstants.PAR_RESOURCES_ROOT_URL);
			}
			if (null == _folder) {
				_folder = "";
			}
			pageContext.getOut().print(_root + _folder);
		} catch (Throwable e) {
			ApsSystemUtils.logThrowable(e, this, "doEndTag");
			throw new JspException("Error closing the tag", e);
		}
		return EVAL_PAGE;
	}
	
	/**
	 * Restituisce la root.
	 * @return La root.
	 */
	public String getRoot() {
		return _root;
	}

	/**
	 * Setta la root.
	 * @param root La root.
	 */
	public void setRoot(String root) {
		this._root = root;
	}
	
	/**
	 * Setta il folder.
	 * @return Il folder.
	 */
	public String getFolder() {
		return _folder;
	}

	/**
	 * Restituisce il folder.
	 * @param folder Il folder.
	 */
	public void setFolder(String folder) {
		this._folder = folder;
	}
	
	private String _root;
	private String _folder;

}
