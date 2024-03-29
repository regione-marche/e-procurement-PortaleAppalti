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
package com.agiletec.plugins.jacms.apsadmin.portal.specialshowlet.listviewer;

import java.util.Properties;

import com.agiletec.aps.system.common.entity.model.EntitySearchFilter;

/**
 * Classe action per la configurazione del filtro su attributo tipo "Boolean".
 * @author E.Santoboni
 */
public class BooleanAttributeFilterAction extends BaseFilterAction {
	
	@Override
	protected Properties createFilterProperties() {
		Properties properties =  super.createFilterProperties();
		if (null != this.getBooleanValue() && this.getBooleanValue().trim().length()>0) {
			properties.put(EntitySearchFilter.VALUE_PARAM, this.getBooleanValue());
		}
		return properties;
	}
	
	@Override
	public int getFilterTypeId() {
		return BOOLEAN_ATTRIBUTE_FILTER_TYPE;
	}
	
	public String getBooleanValue() {
		return _booleanValue;
	}
	public void setBooleanValue(String value) {
		this._booleanValue = value;
	}
	
	private String _booleanValue;
	
}
