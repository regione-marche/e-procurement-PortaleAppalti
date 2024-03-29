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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom.Element;

import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.common.searchengine.IndexableAttributeInterface;
import com.agiletec.aps.system.exception.ApsSystemException;

/**
 * This class describes the Entity of a Composed Attribute.
 * This attribute is build by one or more elementary attributes of different types.
 * These elementary attributes can support multiple languages or not (and are defined
 * multi or mono-language).
 * The Composite Attribute is only utilized in conjunction with the 'Monolist' attribute. Please
 * note that the composite attribute cannot be used as an element of the "List" attribute since
 * the items in the List can support multiple languages.
 * @author E.Santoboni
 */
public class CompositeAttribute extends AbstractComplexAttribute {
	
	/**
	 * Attribute initialization.
	 */
	public CompositeAttribute(){
		_attributeList = new ArrayList<AttributeInterface>();
		_attributeMap = new HashMap<String, AttributeInterface>();
	}
	
	/**
	 * Return the attribute prototype, that is an empty attribute.
	 */
	public Object getAttributePrototype() {
		CompositeAttribute clone = (CompositeAttribute) super.getAttributePrototype();
		Iterator<AttributeInterface> iter = this.getAttributes().iterator();
		while (iter.hasNext()) {
			AttributeInterface attribute = iter.next();
			attribute.setParentEntity(this.getParentEntity());
			clone.addAttribute((AttributeInterface) attribute.getAttributePrototype());
		}
		return clone;
	}
	

	public void setRenderingLang(String langCode) {
		super.setRenderingLang(langCode);
		Iterator<AttributeInterface> iter = this.getAttributes().iterator();
		while (iter.hasNext()) {
			AttributeInterface attribute = iter.next();
			attribute.setRenderingLang(langCode);
		}
	}
	
	@Override
	public void setDefaultLangCode(String langCode) {
		super.setDefaultLangCode(langCode);
		Iterator<AttributeInterface> iter = this.getAttributes().iterator();
		while (iter.hasNext()) {
			AttributeInterface attribute = iter.next();
			attribute.setDefaultLangCode(langCode);
		}
	}
	
	@Override
	public Element getJDOMElement() {
		Element attributeElement = new Element("composite");
		attributeElement.setAttribute("name", this.getName());
		attributeElement.setAttribute("attributetype", this.getType());
		Iterator<AttributeInterface> iter = this.getAttributes().iterator();
		while (iter.hasNext()) {
			AttributeInterface attribute = iter.next();
			attributeElement.addContent(attribute.getJDOMElement());
		}
		return attributeElement;
	}
	
	@Override
	public Element getJDOMConfigElement() {
		Element configElement = super.getJDOMConfigElement();
		List<AttributeInterface> attributes = this.getAttributes();
		for (int i=0; i<attributes.size(); i++) {
			AttributeInterface attributeElement = attributes.get(i);
			Element subConfigElement = attributeElement.getJDOMConfigElement();
			configElement.addContent(subConfigElement);
		}
		return configElement;
	}
	
	@Override
	protected void addListElementTypeConfig(Element configElement) {
		List<AttributeInterface> attributes = this.getAttributes();
		for (int i=0; i<attributes.size(); i++) {
			AttributeInterface attributeElement = attributes.get(i);
			Element subConfigElement = attributeElement.getJDOMConfigElement();
			configElement.addContent(subConfigElement);
		}
	}
	
	/**
	 * Return the attribute with the given name.
	 * @param name The name of the requested attribute
	 * @return The requested attribute.
	 */
	public AttributeInterface getAttribute(String name) {
		AttributeInterface attribute = (AttributeInterface) this.getAttributeMap().get(name);
		return attribute;
	}
	
	/**
	 * Add an attribute to the current Composite Attribute.
	 * @param attribute The attribute to add.
	 */
	private void addAttribute(AttributeInterface attribute) {
		this.getAttributes().add(attribute);
		this.getAttributeMap().put(attribute.getName(), attribute);
	}
	
	public List<AttributeInterface> getAttributes() {
		return this._attributeList;
	}
	
	/**
	 * Return the map of the elementary attributes.
	 * @return The requested map
	 */
	public Map<String, AttributeInterface> getAttributeMap() {
		return this._attributeMap;
	}
	
	public void setComplexAttributeConfig(Element attributeElement, Map<String, AttributeInterface> attrTypes) throws ApsSystemException {
		List<Element> attributeElements = attributeElement.getChildren();
		for (int j=0; j<attributeElements.size(); j++) {
			Element currentAttrJdomElem = attributeElements.get(j);
			String typeCode = this.extractXmlAttribute(currentAttrJdomElem, "attributetype", true);
			AttributeInterface compositeAttrElem = (AttributeInterface) attrTypes.get(typeCode);
			if (compositeAttrElem == null) {
				throw new ApsSystemException("The type " + typeCode +
						" of the attribute element found in the tag <"+attributeElement.getName()+"> of the composite attribute is not a valid one");
			}
			compositeAttrElem = (AttributeInterface) compositeAttrElem.getAttributePrototype();
			compositeAttrElem.setAttributeConfig(currentAttrJdomElem);
			compositeAttrElem.setSearcheable(false);
			compositeAttrElem.setDefaultLangCode(this.getDefaultLangCode());
			this.addAttribute(compositeAttrElem);
		}
	}
	
	/**
	 * Since this kind of attribute can never be indexable this method, which overrides
	 * the one of the abstract class, always returns the constant "INDEXING_TYPE_NONE" 
	 * (defined in AttributeInterface) which explicitly declares it not indexable.
	 * Declaring indexable a complex attribute will make the contained element indexable.
	 * @see com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface#getIndexingType()
	 */
	public String getIndexingType(){
		return IndexableAttributeInterface.INDEXING_TYPE_NONE;
	}
	
	public Object getRenderingAttributes() {
		return this.getAttributeMap();
	}
	
	public void setParentEntity(IApsEntity parentEntity) {
		super.setParentEntity(parentEntity);
		for (int i=0; i<this.getAttributes().size(); i++) {
			AttributeInterface attributeElement = this.getAttributes().get(i);
			attributeElement.setParentEntity(parentEntity);
		}
	}
	
	private List<AttributeInterface> _attributeList;
	private Map<String, AttributeInterface> _attributeMap;
	
}
