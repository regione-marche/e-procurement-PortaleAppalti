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

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.common.entity.IEntityManager;
import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.common.entity.model.attribute.EnumeratorAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.ITextAttribute;
import com.agiletec.aps.system.common.searchengine.IndexableAttributeInterface;
import com.agiletec.apsadmin.system.BaseAction;

/**
 * Base action for Configure Entity Attributes.
 * @author E.Santoboni
 */
public class AbstractBaseEntityAttributeConfigAction extends BaseAction implements BeanFactoryAware {
	
	/**
	 * Fill form fields.
	 * @param attribute 
	 */
	protected void valueFormFields(AttributeInterface attribute) {
		this.setAttributeName(attribute.getName());
		this.setAttributeTypeCode(attribute.getType());
		this.setRequired(new Boolean(attribute.isRequired()));
		this.setSearcheable(new Boolean(attribute.isSearcheable()));
		String indexingType = attribute.getIndexingType();
		if (null != indexingType) {
			this.setIndexable(indexingType.equalsIgnoreCase(IndexableAttributeInterface.INDEXING_TYPE_TEXT));
		}
		if (attribute.isTextAttribute()) {
			ITextAttribute textAttribute = (ITextAttribute) attribute;
			if (textAttribute.getMaxLength() > -1) {
				this.setMaxLength(textAttribute.getMaxLength());
			}
			if (textAttribute.getMinLength() > -1) {
				this.setMinLength(textAttribute.getMinLength());
			}
			this.setRegexp(textAttribute.getRegexp());
		}
		if (attribute instanceof EnumeratorAttribute) {
			EnumeratorAttribute enumeratorAttribute = (EnumeratorAttribute) attribute;
			this.setEnumeratorStaticItems(enumeratorAttribute.getStaticItems());
			this.setEnumeratorStaticItemsSeparator(enumeratorAttribute.getCustomSeparator());
		}
	}
	
	/**
	 * Fill attribute fields.
	 * @param attribute The attribute to edit with the form values.
	 * @return A customized return code in the attribute needs a extra configuration, else null.
	 */
	protected String fillAttributeFields(AttributeInterface attribute) {
		attribute.setRequired(null != this.getRequired() && this.getRequired());
		attribute.setSearcheable(null != this.getSearcheable() && this.getSearcheable());
		String indexingType = IndexableAttributeInterface.INDEXING_TYPE_NONE;
		if (null != this.getIndexable()) {
			indexingType = IndexableAttributeInterface.INDEXING_TYPE_TEXT;
		}
		attribute.setIndexingType(indexingType);
		if (attribute.isTextAttribute()) {
			ITextAttribute textAttribute = (ITextAttribute) attribute;
			int maxLength = ((null != this.getMaxLength()) ? this.getMaxLength().intValue() : -1);
			textAttribute.setMaxLength(maxLength);
			int minLength = ((null != this.getMinLength()) ? this.getMinLength().intValue() : -1);
			textAttribute.setMinLength(minLength);
			textAttribute.setRegexp(this.getRegexp());
		}
		if (attribute instanceof EnumeratorAttribute) {
			EnumeratorAttribute enumeratorAttribute = (EnumeratorAttribute) attribute;
			enumeratorAttribute.setStaticItems(this.getEnumeratorStaticItems());
			if (null != this.getEnumeratorStaticItemsSeparator() && this.getEnumeratorStaticItemsSeparator().length()>0) {
				enumeratorAttribute.setCustomSeparator(this.getEnumeratorStaticItemsSeparator());
			}
		}
		return null;
	}
	
	/**
	 * Return the current entity type on edit.
	 * @return The current entity type.
	 */
	public IApsEntity getEntityType() {
		return (IApsEntity) this.getRequest().getSession().getAttribute(IEntityTypeConfigAction.ENTITY_TYPE_ON_EDIT_SESSION_PARAM);
	}
	
	/**
	 * Return the entity manager name that manages the current entity on edit.
	 * @return The entity manager name.
	 */
	public String getEntityManagerName() {
		return (String) this.getRequest().getSession().getAttribute(IEntityTypeConfigAction.ENTITY_TYPE_MANAGER_SESSION_PARAM);
	}
	
	/**
	 * Return the entity manager that manages the current entity on edit.
	 * @return The entity manager.
	 */
	protected IEntityManager getEntityManager() {
		String entityManagerName = this.getEntityManagerName();
		return (IEntityManager) this.getBeanFactory().getBean(entityManagerName);
	}
	
	/**
	 * Return the namespace prefix specific for the current entity manager.
	 * The prefix will extract by the object {@link EntityTypeNamespaceInfoBean} associated to the current entity manager.
	 * @return The namespace prefix specific for the current entity manager.
	 */
	public String getEntityTypeManagementNamespacePrefix() {
		try {
			EntityTypeNamespaceInfoBean infoBean = (EntityTypeNamespaceInfoBean) this.getBeanFactory().getBean(this.getEntityManagerName()+"NamespaceInfoBean");
			return infoBean.getNamespacePrefix();
		} catch (Throwable t) {
			//nothing to do
		}
		return "";
	}
	
	/**
	 * Indicates whether the current entity manager is a Search Engine user or not.
	 * @return True if the current entity manager is a Search Engine user, false otherwise.
	 */
	public boolean isEntityManagerSearchEngineUser() {
		return this.getEntityManager().isSearchEngineUser();
	}
	
	public boolean isIndexableOptionSupported(String attributeTypeCode) {
		try {
			AttributeInterface attribute = this.getAttributePrototype(attributeTypeCode);
			return (null == attribute.getIndexingType());
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "isIndexableOptionSupported");
		}
		return false;
	}
	
	public boolean isSearchableOptionSupported(String attributeTypeCode) {
		try {
			AttributeInterface attribute = this.getAttributePrototype(attributeTypeCode);
			return attribute.isSearchableOptionSupported();
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "isSearchableOptionSupported");
		}
		return false;
	}
	
	public AttributeInterface getAttributePrototype(String typeCode) {
		IEntityManager entityManager = this.getEntityManager();
		Map<String, AttributeInterface> attributeTypes = entityManager.getEntityAttributePrototypes();
		return attributeTypes.get(typeCode);
	}
	
	public int getStrutsAction() {
		return _strutsAction;
	}
	public void setStrutsAction(int strutsAction) {
		this._strutsAction = strutsAction;
	}
	
	public String getAttributeName() {
		return _attributeName;
	}
	public void setAttributeName(String attributeName) {
		this._attributeName = attributeName;
	}
	
	public String getAttributeTypeCode() {
		return _attributeTypeCode;
	}
	public void setAttributeTypeCode(String attributeTypeCode) {
		this._attributeTypeCode = attributeTypeCode;
	}
	
	public Boolean getRequired() {
		return _required;
	}
	public void setRequired(Boolean required) {
		this._required = required;
	}
	
	public Boolean getSearcheable() {
		return _searchable;
	}
	public void setSearcheable(Boolean searcheable) {
		this._searchable = searcheable;
	}
	
	public Boolean getIndexable() {
		return _indexable;
	}
	public void setIndexable(Boolean indexable) {
		this._indexable = indexable;
	}
	
	public Integer getMinLength() {
		return _minLength;
	}
	public void setMinLength(Integer minLength) {
		this._minLength = minLength;
	}
	
	public Integer getMaxLength() {
		return _maxLength;
	}
	public void setMaxLength(Integer maxLength) {
		this._maxLength = maxLength;
	}
	
	public String getRegexp() {
		return _regexp;
	}
	public void setRegexp(String regexp) {
		this._regexp = regexp;
	}
	
	public String getEnumeratorStaticItems() {
		return _enumeratorStaticItems;
	}
	public void setEnumeratorStaticItems(String enumeratorStaticItems) {
		this._enumeratorStaticItems = enumeratorStaticItems;
	}
	
	public String getEnumeratorStaticItemsSeparator() {
		return _enumeratorStaticItemsSeparator;
	}
	public void setEnumeratorStaticItemsSeparator(String enumeratorStaticItemsSeparator) {
		this._enumeratorStaticItemsSeparator = enumeratorStaticItemsSeparator;
	}
	
	protected BeanFactory getBeanFactory() {
		return _beanFactory;
	}
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this._beanFactory = beanFactory;
	}
	
	private int _strutsAction;
	
	private String _attributeName;
	private String _attributeTypeCode;
	private Boolean _required;
	private Boolean _searchable;
	private Boolean _indexable;
	
	private Integer _minLength;
	private Integer _maxLength;
	private String _regexp;
	
	private String _enumeratorStaticItems;
	private String _enumeratorStaticItemsSeparator;
	
	private BeanFactory _beanFactory;
	
}