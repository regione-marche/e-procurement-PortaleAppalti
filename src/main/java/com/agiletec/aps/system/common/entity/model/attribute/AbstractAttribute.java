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
package com.agiletec.aps.system.common.entity.model.attribute;

import java.io.Serializable;

import org.jdom.Element;

import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.common.entity.parse.attribute.AttributeHandlerInterface;
import com.agiletec.aps.system.common.searchengine.IndexableAttributeInterface;
import com.agiletec.aps.system.exception.ApsSystemException;

/**
 * This abstract class must be used when implementing Entity Attributes.
 * @author W.Ambu - E.Santoboni
 */
public abstract class AbstractAttribute implements AttributeInterface, Serializable {
	
	/**
	 * @see com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface#isMultilingual()
	 */
	@Override
	public boolean isMultilingual() {
		return false;
	}
	
	/**
	 * @see com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface#isTextAttribute()
	 */
	@Override
	public boolean isTextAttribute() {
		return false;
	}
	
	/**
	 * @see com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface#getName()
	 */
	@Override
	public String getName() {
		return _name;
	}
	
	/**
	 * @see com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		this._name = name;
	}
	
	/**
	 * The returned type corresponds to the attribute code as found in the declaration
	 * of the attribute type.
	 * @see com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface#getType()
	 */
	@Override
	public String getType() {
		return _type;
	}
	
	/**
	 * @see com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface#setType(java.lang.String)
	 */
	@Override
	public void setType(String typeName) {
		this._type = typeName;
	}
	
	/**
	 * @see com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface#setDefaultLangCode(java.lang.String)
	 */
	@Override
	public void setDefaultLangCode(String langCode){
		this._defaultLangCode = langCode;
	}
	
	/**
	 * Return the code of the default language.
	 * @return The code of the default language.
	 */
	public String getDefaultLangCode(){
		return _defaultLangCode;
	}
	
	/**
	 * Set up the language to use in the rendering process.
	 * @param langCode The code of the rendering language.
	 */
	@Override
	public void setRenderingLang(String langCode){
		_renderingLangCode = langCode;
	}
	
	/**
	 * Return the code of the language used in the rendering process.
	 * @return The code of the language used for rendering.
	 */
	public String getRenderingLang(){
		return _renderingLangCode;
	}
	
	/**
	 * @return True if the attribute is searchable, false otherwise.
	 */
	@Override
	public boolean isSearcheable() {
		return _searcheable;
	}
	
	/**
	 * Toggle the searchable condition of the attribute.
	 * @param searchable True if the attribute is searchable, false otherwise.
	 */
	@Override
	public void setSearcheable(boolean searchable){
		this._searcheable = searchable;
	}
	
	/**
	 * @see com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface#getAttributePrototype()
	 */
	@Override
	public Object getAttributePrototype() {
		AttributeInterface clone = null;
		try {
			Class attributeClass = Class.forName(this.getClass().getName());
			clone = (AttributeInterface) attributeClass.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Error detected while cloning the attribute '" 
					+ this.getName() + "' type '" + this.getType() + "'");
		}
		clone.setName(this.getName());
		clone.setType(this.getType());
		clone.setRequired(this.isRequired());
		clone.setSearcheable(this.isSearcheable());
		clone.setDefaultLangCode(this.getDefaultLangCode());
		clone.setIndexingType(this.getIndexingType());
		clone.setParentEntity(this.getParentEntity());
		AttributeHandlerInterface handler = (AttributeHandlerInterface) this.getHandler().getAttributeHandlerPrototype();
		clone.setHandler(handler);
		if (this.getDisablingCodes() != null) {
			String[] disablingCodes = new String[this.getDisablingCodes().length];
			for (int i=0; i<this.getDisablingCodes().length; i++) {
				disablingCodes[i] = this.getDisablingCodes()[i];
			}
			clone.setDisablingCodes(disablingCodes);
		}
		return clone;
	}
	
	@Override
	public void setAttributeConfig(Element attributeElement) throws ApsSystemException {
		String name = this.extractXmlAttribute(attributeElement, "name", true);
		this.setName(name);
		String searcheable = this.extractXmlAttribute(attributeElement, "searcheable", false);
		this.setSearcheable(null != searcheable && searcheable.equalsIgnoreCase("true"));
		String required = this.extractXmlAttribute(attributeElement, "required", false);
		this.setRequired(null != required && required.equalsIgnoreCase("true"));
		String indexingType = this.extractXmlAttribute(attributeElement, "indexingtype", false);
		if (null != indexingType) {
			this.setIndexingType(indexingType);
		} else {
			this.setIndexingType(IndexableAttributeInterface.INDEXING_TYPE_NONE);
		}
		
		String disablingCodesStr = this.extractXmlAttribute(attributeElement, "disablingCodes", false);
		if (disablingCodesStr != null) {
			String[] disablingCodes = disablingCodesStr.split(",");
			this.setDisablingCodes(disablingCodes);
		}
	}
	
	@Override
	public Element getJDOMConfigElement() {
		Element configElement = new Element(this.getTypeConfigElementName());
		configElement.setAttribute("name", this.getName());
		configElement.setAttribute("attributetype", this.getType());
		if (this.isSearcheable()) {
			configElement.setAttribute("searcheable", "true");
		}
		if (this.isRequired()) {
			configElement.setAttribute("required", "true");
		}
		if (null != this.getIndexingType() && !this.getIndexingType().equals(IndexableAttributeInterface.INDEXING_TYPE_NONE)) {
			configElement.setAttribute("indexingtype", this.getIndexingType());
		}
		if (null != this.getDisablingCodes()) {
			StringBuffer disablingCodes = new StringBuffer();
			for (int i = 0; i < this.getDisablingCodes().length; i++) {
				if (i > 0) disablingCodes.append(",");
				disablingCodes.append(this.getDisablingCodes()[i]);
			}
			configElement.setAttribute("disablingCodes", disablingCodes.toString());
		}
		return configElement;
	}
	
	protected String getTypeConfigElementName() {
		return "attribute";
	}

	/**
	 * Get the attribute matching the given criteria from a XML string.
	 * @param currElement The element where to extract the value of the attribute from 
	 * @param attributeName Name of the requested attribute.
	 * @param required Select a mandatory condition of the attribute to search for.
	 * @return The value of the requested attribute.
	 * @throws ApsSystemException when a attribute declared mandatory is not present in the given
	 * XML element.
	 */
	protected String extractXmlAttribute(Element currElement, String attributeName,
			boolean required) throws ApsSystemException {
		String value = currElement.getAttributeValue(attributeName);
		if (required && value == null) {
			throw new ApsSystemException("Attribute '" + attributeName +"' not found in the tag <" + currElement.getName() + ">");
		}
		return value;
	}
	
	protected void addListElementTypeConfig(Element configElement) {
		//nothing to do
	}
	
	/**
	 * @see com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface#getIndexingType()
	 */
	@Override
	public String getIndexingType(){
		return _indexingType;
	}
	
	/**
	 * @see com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface#setIndexingType(java.lang.String)
	 */
	@Override
	public void setIndexingType(String indexingType){
		this._indexingType = indexingType;
	}
	
	@Override
	public boolean isSimple() {
		return true;
	}
	
	@Override
	public boolean isRequired() {
		return _required;
	}

	@Override
	public void setRequired(boolean required) {
		this._required = required;
	}
	
	@Override
	public IApsEntity getParentEntity() {
		return _parentEntity;
	}
	@Override
	public void setParentEntity(IApsEntity parentEntity) {
		this._parentEntity = parentEntity;
	}
	
	@Override
	public AttributeHandlerInterface getHandler() {
		return _handler;
	}
	@Override
	public void setHandler(AttributeHandlerInterface handler) {
		this._handler = handler;
	}
	
	@Override
	public void disable(String disablingCode) {
		if (_disablingCodes != null && disablingCode != null) {
			for (int i=0; i<_disablingCodes.length; i++) {
				if (disablingCode.equals(_disablingCodes[i])) {
					this._active = false;
					return;
				}
			}
		}
	}
	@Override
	public boolean isActive() {
		return _active;
	}
	@Override
	public void setDisablingCodes(String[] disablingCodes) {
		this._disablingCodes = disablingCodes;
	}
	private String[] getDisablingCodes() {
		return this._disablingCodes;
	}
	
	private String _name;
	private String _type;
	private boolean _required;
	private String _defaultLangCode;
	private String _renderingLangCode;
	private boolean _searcheable;
	private String _indexingType;
	private IApsEntity _parentEntity;
	
	private AttributeHandlerInterface _handler;
	
	private String[] _disablingCodes;
	private boolean _active = true;
	
}