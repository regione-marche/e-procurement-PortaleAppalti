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
import java.util.List;

import org.jdom.Element;

import com.agiletec.aps.system.common.entity.model.AttributeSearchInfo;
import com.agiletec.aps.system.services.lang.Lang;

/**
 * This class implements a text attribute that must be the same for all the languages in the system.
 * @author W.Ambu
 */
public class MonoTextAttribute extends AbstractTextAttribute {

	/**
	 * Return the text field to index.
	 * @return Returns The text field to index.
	 */
	@Override
	public String getIndexeableFieldValue(){ 
		return this.getText();
	}

	/**
	 * Return the text held by this attribute.
	 * @return Returns the text.
	 */
	@Override
	public String getText() {
		if (null != this._text) {
			return this._text;
		}
		return "";
	}

	@Override
	public String getTextForLang(String langCode) {
		return this.getText();
	}

	@Override
	public void setText(String text, String langCode) {
		this.setText(text);
	}

	@Override
	public boolean needToConvertSpecialCharacter() {
		return true;
	}

	/**
	 * Associate the given text to the attribute
	 * @param text The text to set.
	 */
	public void setText(String text) {
		this._text = text;
	}
	
	@Override
	public boolean isSearchableOptionSupported() {
		return true;
	}
	
	@Override
	public List<AttributeSearchInfo> getSearchInfos(List<Lang> systemLangs) {
		if (this.getText() != null) {
			List<AttributeSearchInfo> infos = new ArrayList<AttributeSearchInfo>();
			String text = this.getText();
			if (text != null && text.length() >= 255) {
				text = text.substring(0, 254);
			}
			AttributeSearchInfo info = new AttributeSearchInfo(text, null, null, null);
			infos.add(info);
			return infos;
		}
		return null;
	}

	/**
	 * @see com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface#getJDOMElement()
	 */
	@Override
	public Element getJDOMElement() {
		Element attributeElement = new Element("attribute");
		attributeElement.setAttribute("name", this.getName());
		attributeElement.setAttribute("attributetype", this.getType());
		if (null != this.getText() && this.getText().trim().length()>0) {
			Element monotextElement = new Element("monotext");
			monotextElement.setText(this.getText().trim());
			attributeElement.addContent(monotextElement);
		}
		return attributeElement;
	}

	private String _text;

}