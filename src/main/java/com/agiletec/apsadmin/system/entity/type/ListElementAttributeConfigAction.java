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
package com.agiletec.apsadmin.system.entity.type;

import com.agiletec.aps.system.ApsSystemUtils;

import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;

/**
 * @author E.Santoboni
 */
public class ListElementAttributeConfigAction extends AbstractBaseEntityAttributeConfigAction implements IListElementAttributeConfigAction {
	
	@Override
	public String configureListElement() {
		this.valueFormFields(this.getAttributeElement());
		return SUCCESS;
	}
	
	@Override
	public String saveListElement() {
		try {
			AttributeInterface attribute = this.getAttributeElement();
			this.fillAttributeFields(attribute);
			this.getRequest().getSession().removeAttribute(IListElementAttributeConfigAction.LIST_ELEMENT_ON_EDIT_SESSION_PARAM);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "saveListElement");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	public AttributeInterface getAttributeElement() {
		return (AttributeInterface) this.getRequest().getSession().getAttribute(LIST_ELEMENT_ON_EDIT_SESSION_PARAM);
	}
	
}
