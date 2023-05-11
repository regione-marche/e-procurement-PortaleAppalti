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

import org.jdom.CDATA;
import org.jdom.Element;

import com.agiletec.aps.system.common.searchengine.IndexableAttributeInterface;
import com.agiletec.aps.system.exception.ApsSystemException;

/**
 * This abstract class is the base for the 'Text' Attributes.
 * @author E.Santoboni
 */
public abstract class AbstractTextAttribute extends AbstractAttribute implements IndexableAttributeInterface, ITextAttribute {
	
	/**
	 * @see com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface#isTextAttribute()
	 */
	@Override
	public boolean isTextAttribute() {
		return true;
	}
	
	@Override
	public void setAttributeConfig(Element attributeElement) throws ApsSystemException {
		super.setAttributeConfig(attributeElement);
		String maxLength = this.extractXmlAttribute(attributeElement, "maxlength", false);
		if (null != maxLength) {
			this.setMaxLength(Integer.parseInt(maxLength));
		}
		String minLength = this.extractXmlAttribute(attributeElement, "minlength", false);
		if (null != minLength) {
			this.setMinLength(Integer.parseInt(minLength));
		}
		Element regexpElement = attributeElement.getChild("regexp");
		if (null != regexpElement) {
			String regexp = regexpElement.getText();
			if (null != regexp && regexp.trim().length() > 0) {
				this.setRegexp(regexp);
			}
		}
	}
	
	@Override
	public Element getJDOMConfigElement() {
		Element configElement = super.getJDOMConfigElement();
		this.setConfig(configElement);
		return configElement;
	}
	
	@Override
	protected void addListElementTypeConfig(Element configElement) {
		if (null != this.getIndexingType() && !this.getIndexingType().equals(IndexableAttributeInterface.INDEXING_TYPE_NONE)) {
			configElement.setAttribute("indexingtype", this.getIndexingType());
		}
		this.setConfig(configElement);
	}
	
	private void setConfig(Element configElement) {
		if (this.getMinLength() > -1) {
			configElement.setAttribute("minlength", String.valueOf(this.getMinLength()));
		}
		if (this.getMaxLength() > -1) {
			configElement.setAttribute("maxlength", String.valueOf(this.getMaxLength()));
		}
		if (null != this.getRegexp() && this.getRegexp().trim().length() > 0) {
			Element regexpElem = new Element("regexp");
			CDATA cdata = new CDATA(this.getRegexp());
			regexpElem.addContent(cdata);
			configElement.addContent(regexpElem);
		}
	}
	
	@Override
	public Object getAttributePrototype() {
		ITextAttribute prototype = (ITextAttribute) super.getAttributePrototype();
		prototype.setMaxLength(this.getMaxLength());
		prototype.setMinLength(this.getMinLength());
		prototype.setRegexp(this.getRegexp());
		return prototype;
	}
	
	@Override
	public int getMaxLength() {
		return _maxLength;
	}
	@Override
	public void setMaxLength(int maxLength) {
		this._maxLength = maxLength;
	}
	
	@Override
	public int getMinLength() {
		return _minLength;
	}
	@Override
	public void setMinLength(int minLength) {
		this._minLength = minLength;
	}
	
	@Override
	public String getRegexp() {
		return _regexp;
	}
	@Override
	public void setRegexp(String regexp) {
		this._regexp = regexp;
	}
	
	private int _maxLength = -1;
	private int _minLength = -1;
	
	private String _regexp;
	
}
