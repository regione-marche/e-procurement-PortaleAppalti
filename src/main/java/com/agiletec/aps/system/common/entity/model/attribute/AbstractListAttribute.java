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

import java.util.Map;

import org.jdom.Element;

import com.agiletec.aps.system.common.searchengine.IndexableAttributeInterface;
import com.agiletec.aps.system.exception.ApsSystemException;

/**
 * Base abstract class for the complex attributes of type List and Monolist. 
 * @author E.Santoboni
 */
public abstract class AbstractListAttribute extends AbstractComplexAttribute 
		implements ListAttributeInterface {
	
	/**
	 * @see com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface#getAttributePrototype()
	 */
	@Override
	public Object getAttributePrototype() {
		ListAttributeInterface clone = (ListAttributeInterface) super.getAttributePrototype();
		clone.setNestedAttributeType(this.getNestedAttributeType());
		return clone;
	}
	
	@Override
	public void setComplexAttributeConfig(Element attributeElement, Map<String, AttributeInterface> attrTypes) throws ApsSystemException {
		String nestedTypeCode = this.extractXmlAttribute(attributeElement, "nestedtype", true);
		AttributeInterface nestedType = (AttributeInterface) attrTypes.get(nestedTypeCode);
		if (nestedType == null) {
			throw new ApsSystemException("Wrong list attribute element detected: " 
					+ nestedType + ", in attribute list " + this.getName());
		}
		nestedType = (AttributeInterface) nestedType.getAttributePrototype();
		nestedType.setAttributeConfig(attributeElement);
		if (!nestedType.isSimple()) {
			((AbstractComplexAttribute) nestedType).setComplexAttributeConfig(attributeElement, attrTypes);
		}
		this.setNestedAttributeType(nestedType);
	}
	
	@Override
	public Element getJDOMConfigElement() {
		Element configElement = super.getJDOMConfigElement();
		configElement.setAttribute("nestedtype", this.getNestedAttributeTypeCode());
		((AbstractAttribute) this.getNestedAttributeType()).addListElementTypeConfig(configElement);
		return configElement;
	}
	
	@Override
	protected String getTypeConfigElementName() {
		return "list";
	}
	
	/**
	 * Set up the attribute to utilize as prototype for the creation of the elements to add to
	 * the list of attributes.
	 * @param attribute The prototype attribute.
	 */
	@Override
	public void setNestedAttributeType(AttributeInterface attribute) {
		this._nestedType = attribute;
	}
	
	/**
	 * Return the attribute to utilize as prototype for the creation of the elements to add to
	 * the list of attributes.
	 * @return The prototype attribute.
	 */
	protected AttributeInterface getNestedAttributeType() {
		return this._nestedType;
	}
	
	/**
	 * Return the string which identifies the type of the attributes which will be 
	 * held in this class.
	 * @return The code of the Attribute Type.
	 */
	@Override
	public String getNestedAttributeTypeCode() {
		return this.getNestedAttributeType().getType();
	}
	
	/**
	 * This method overrides the method of the abstract class it derives from, because
	 * this kind of attribute can never be "indexable" and so it always returns the constant 
	 * 'INDEXING_TYPE_NONE', belonging to 'AttributeInterface', which declares it not searchable.
	 * Declaring a complex attribute indexable will result in its constituting elements to be considered
	 * as such.
	 * @see com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface#getIndexingType()
	 */
	@Override
	public String getIndexingType() {
		return IndexableAttributeInterface.INDEXING_TYPE_NONE;
	}
	
	/**
	 * This method overrides the method of the abstract class it derives from, because
	 * this kind of attribute can never be "searchable" and so it always return a 'false'
	 * value.
	 * @return Return always false.
	 * @see com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface#isSearcheable()
	 */
	@Override
	public boolean isSearcheable() {
		return false;
	}
	
	private AttributeInterface _nestedType;
	
}
