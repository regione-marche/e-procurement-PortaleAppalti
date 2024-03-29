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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanComparator;
import org.springframework.beans.factory.BeanFactoryAware;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.common.entity.IEntityManager;
import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.common.entity.model.attribute.AbstractListAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.common.entity.model.attribute.CompositeAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.EnumeratorAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.ITextAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.ListAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.MonoListAttribute;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.apsadmin.system.ApsAdminSystemConstants;

/**
 * @author E.Santoboni
 */
public class EntityAttributeConfigAction extends AbstractBaseEntityAttributeConfigAction implements IEntityAttributeConfigAction, BeanFactoryAware {
	
	@Override
	public void validate() {
		super.validate();
		IApsEntity entityType = this.getEntityType();
		if (this.getStrutsAction() == ApsAdminSystemConstants.ADD && null != entityType.getAttribute(this.getAttributeName())) {
			String[] args = {this.getAttributeName()};
			this.addFieldError("attributeName", this.getText("field.error.attribute.name.already.exists", args));
		}
		AttributeInterface attributePrototype = this.getAttributePrototype(this.getAttributeTypeCode());
		if (null == attributePrototype) {
			String[] args = {this.getAttributeTypeCode()};
			this.addFieldError("attributeTypeCode", this.getText("field.error.attribute.type.invalid", args));
		} else {
			if ((attributePrototype instanceof EnumeratorAttribute) 
					&& (this.getEnumeratorStaticItems() == null || this.getEnumeratorStaticItems().trim().length() == 0)) {
				String[] args = {this.getAttributeTypeCode()};
				this.addFieldError("enumeratorStaticItems", this.getText("field.error.attribute.enumerator.items.missing",args));
			}
			if ((attributePrototype instanceof AbstractListAttribute) 
					&& (null == this.getListNestedType()|| null == this.getAttributePrototype(this.getAttributeTypeCode()))) {
				String[] args = {this.getAttributeTypeCode()};
				this.addFieldError("listNestedType", this.getText("field.error.attribute.list.handled.missing",args));
			}
		}
	}
	
	@Override
	public String addAttribute() {
		this.setStrutsAction(ApsAdminSystemConstants.ADD);
		this.getRequest().getSession().removeAttribute(ICompositeAttributeConfigAction.COMPOSITE_ATTRIBUTE_ON_EDIT_SESSION_PARAM);
		this.getRequest().getSession().removeAttribute(IListElementAttributeConfigAction.LIST_ELEMENT_ON_EDIT_SESSION_PARAM);
		return SUCCESS;
	}
	
	@Override
	public String editAttribute() {
		this.setStrutsAction(ApsAdminSystemConstants.EDIT);
		this.getRequest().getSession().removeAttribute(ICompositeAttributeConfigAction.COMPOSITE_ATTRIBUTE_ON_EDIT_SESSION_PARAM);
		this.getRequest().getSession().removeAttribute(IListElementAttributeConfigAction.LIST_ELEMENT_ON_EDIT_SESSION_PARAM);
		try {
			AttributeInterface attribute = (AttributeInterface) this.getEntityType().getAttribute(this.getAttributeName());
			this.valueFormFields(attribute);
			if (attribute instanceof AbstractListAttribute) {
				AbstractListAttribute listAttribute = (AbstractListAttribute) attribute;
				this.setListNestedType(listAttribute.getNestedAttributeTypeCode());
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "editAttribute");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	@Override
	public String saveAttribute() {
		AttributeInterface attribute = null;
		try {
			if (this.getStrutsAction() == ApsAdminSystemConstants.EDIT) {
				attribute = (AttributeInterface) this.getEntityType().getAttribute(this.getAttributeName());
			} else {
				attribute = this.getAttributePrototype(this.getAttributeTypeCode());
				attribute.setName(this.getAttributeName());
				this.getEntityType().addAttribute(attribute);
			}
			String resultCode = this.fillAttributeFields(attribute);
			if (null != resultCode) return resultCode;
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "saveAttribute");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	/**
	 * Fill attribute fields.
	 * @param attribute The attribute to edit with the form values.
	 * @return A custom return code in the attribute neads a extra configuration, else null.
	 */
	@Override
	protected String fillAttributeFields(AttributeInterface attribute) {
		super.fillAttributeFields(attribute);
		AttributeInterface nestedType = null;
		if (attribute instanceof AbstractListAttribute) {
			AbstractListAttribute listAttribute = (AbstractListAttribute) attribute;
			if (this.getStrutsAction() == ApsAdminSystemConstants.EDIT && 
					listAttribute.getNestedAttributeTypeCode().equals(this.getListNestedType())) {
				if (listAttribute instanceof ListAttribute) {
					Lang defaultLang = this.getLangManager().getDefaultLang();
					nestedType = ((ListAttribute) listAttribute).addAttribute(defaultLang.getCode());//Composite Element
					((ListAttribute) listAttribute).getAttributeList(defaultLang.getCode()).clear();
				} else {
					nestedType = ((MonoListAttribute) listAttribute).addAttribute();//Composite Element
					((MonoListAttribute) listAttribute).getAttributes().clear();
				}
			} else {
				nestedType = this.getAttributePrototype(this.getListNestedType());
				nestedType.setName(this.getAttributeName());
			}
			listAttribute.setNestedAttributeType(nestedType);
			nestedType.setName(attribute.getName());
		}
		if ((attribute instanceof CompositeAttribute) || (nestedType != null && nestedType instanceof CompositeAttribute)) {
			CompositeAttribute composite = ((attribute instanceof CompositeAttribute) ? (CompositeAttribute) attribute : (CompositeAttribute) nestedType);
			this.getRequest().getSession().setAttribute(ICompositeAttributeConfigAction.COMPOSITE_ATTRIBUTE_ON_EDIT_SESSION_PARAM, composite);
			return "configureCompositeAttribute";
		}
		if (nestedType != null && nestedType instanceof ITextAttribute) {
			this.getRequest().getSession().setAttribute(IListElementAttributeConfigAction.LIST_ELEMENT_ON_EDIT_SESSION_PARAM, nestedType);
			return "configureListElementAttribute";
		}
		return null;
	}
	
	public List<AttributeInterface> getAllowedNestedTypes(AttributeInterface listType) {
		List<AttributeInterface> attributes = new ArrayList<AttributeInterface>();
		try {
			IEntityManager entityManager = this.getEntityManager();
			Map<String, AttributeInterface> attributeTypes = entityManager.getEntityAttributePrototypes();
			Iterator<AttributeInterface> attributeIter = attributeTypes.values().iterator();
			while (attributeIter.hasNext()) {
				AttributeInterface attribute = attributeIter.next();
				boolean simple = attribute.isSimple();
				boolean multiLanguage = attribute.isMultilingual();
				if ((listType instanceof ListAttribute && simple && !multiLanguage) 
						|| (listType instanceof MonoListAttribute && !(attribute instanceof AbstractListAttribute))) {
					attributes.add(attribute);
				}
			}
			Collections.sort(attributes, new BeanComparator("type"));
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getAllowedNestedTypes");
			throw new RuntimeException("Error while extracting Allowed Nested Types", t);
		}
		return attributes;
	}
	
	public String getListNestedType() {
		return _listNestedType;
	}
	public void setListNestedType(String listNestedType) {
		this._listNestedType = listNestedType;
	}
	
	private String _listNestedType;
	
}