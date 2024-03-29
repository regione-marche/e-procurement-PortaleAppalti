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
package com.agiletec.apsadmin.system.entity.attribute.manager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.common.entity.model.attribute.DateAttribute;
import com.agiletec.apsadmin.system.entity.attribute.AttributeTracer;
import com.agiletec.apsadmin.util.CheckFormatUtil;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Manager class for the 'date' attribute.
 * @author E.Santoboni
 */
public class DateAttributeManager extends AbstractMonoLangAttributeManager {
	
	@Override
	protected Object getValue(AttributeInterface attribute) {
		return ((DateAttribute) attribute).getDate();
	}
	
	@Override
	protected void setValue(AttributeInterface attribute, String value) {
		DateAttribute dateAttribute = (DateAttribute) attribute;
		Date data = null;
		if (value != null) value = value.trim();
		if (CheckFormatUtil.isValidDate(value)) {
			try {
				SimpleDateFormat dataF = new SimpleDateFormat("dd/MM/yyyy");
				data = dataF.parse(value);
				dateAttribute.setFailedDateString(null);
			} catch (ParseException ex) {
				throw new RuntimeException("Error while parsing the date submitted - " + value + " -");
			}
		} else {
			dateAttribute.setFailedDateString(value);
		}
		dateAttribute.setDate(data);
	}
	
	@Override
	protected int getState(AttributeInterface attribute, AttributeTracer tracer) {
		int state = super.getState(attribute, tracer);
		boolean valuedString = ((DateAttribute) attribute).getFailedDateString() != null;
		if (state == VALUED_ATTRIBUTE_STATE || valuedString) return this.VALUED_ATTRIBUTE_STATE;
		return this.EMPTY_ATTRIBUTE_STATE;
	}
	
	@Override
	protected void checkSingleAttribute(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer, IApsEntity entity) {
		super.checkSingleAttribute(action, attribute, tracer, entity);
		this.checkDate(action, attribute, tracer);
	}
	
	@Override
	protected void checkListElement(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer, IApsEntity entity) {
		super.checkListElement(action, attribute, tracer, entity);
		this.checkDate(action, attribute, tracer);
	}
	
	@Override
	protected void checkMonoListCompositeElement(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer, IApsEntity entity) {
		super.checkMonoListCompositeElement(action, attribute, tracer, entity);
		this.checkDate(action, attribute, tracer);
	}
	
	@Override
	protected void checkMonoListElement(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer, IApsEntity entity) {
		super.checkMonoListElement(action, attribute, tracer, entity);
		this.checkDate(action, attribute, tracer);
	}
	
	private void checkDate(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer) {
		if (this.getState(attribute, tracer) == VALUED_ATTRIBUTE_STATE && !this.hasRightValue(attribute)) {
			this.addFieldError(action, attribute, tracer, "DateAttribute.fieldError.invalidDate", null);
		}
	}
	
	/**
	 * Check for the coherency of the data the attribute was valued with
	 * @param attribute The attribute to check
	 * @return true if the attribute was valued with proper data, false otherwise.
	 */
	private boolean hasRightValue(AttributeInterface attribute) {
		if (this.getValue(attribute) != null) return true;
		String insertedDateString = ((DateAttribute) attribute).getFailedDateString();
		return CheckFormatUtil.isValidDate(insertedDateString);
	}
	
	@Override
	protected String getInvalidAttributeMessage() {
		return "DateAttribute.fieldError.invalidDate";
	}
	
}
