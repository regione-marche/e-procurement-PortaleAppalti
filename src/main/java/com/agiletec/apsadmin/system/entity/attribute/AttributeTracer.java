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
package com.agiletec.apsadmin.system.entity.attribute;

import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.services.lang.Lang;

/**
 * This class implements the 'tracer' for the jAPS Attributes. This class is
 * used, with the singles attributes, to trace the position inside 'composite'
 * attributes. This class is involved during the update and validation process
 * of the Attribute and, furthermore, it guarantees the correct construction of
 * the form in the content edit interface.
 * @author E.Santoboni
 */
public class AttributeTracer {
	
	public Object clone() {
		AttributeTracer clone = new AttributeTracer();
		clone.setMonoListElement(this._monoListElement);
		clone.setListElement(this._listElement);
		clone.setListLang(this._listLang);
		clone.setListIndex(this._listIndex);
		clone.setCompositeElement(this._compositeElement);
		clone.setParentAttribute(this._parentAttribute);
		clone.setLang(this._lang);
		return clone;
	}
	
	public AttributeTracer getMonoListElementTracer(int index) {
		AttributeTracer tracer = (AttributeTracer) this.clone();
		tracer.setMonoListElement(true);
		tracer.setListIndex(index);
		return tracer;
	}
	
	public AttributeTracer getListElementTracer(Lang lang, int index) {
		AttributeTracer tracer = (AttributeTracer) this.clone();
		tracer.setListElement(true);
		tracer.setListLang(lang);
		tracer.setListIndex(index);
		return tracer;
	}
	
	public AttributeTracer getCompositeTracer(AttributeInterface parentAttribute) {
		AttributeTracer tracer = (AttributeTracer) this.clone();
		tracer.setCompositeElement(true);
		tracer.setParentAttribute(parentAttribute);
		return tracer;
	}
	
	/**
	 * Return the name of the field related to the given attribute.
	 * The name is built considering the position of the attribute itself
	 * tracked by the tracer
	 * 
	 * @param attribute The attribute whose name must be returned.
	 * @return the name of the field associated to the attribute. 
	 */
	public String getFormFieldName(AttributeInterface attribute) {
		StringBuffer formFieldName = new StringBuffer();
		String langModule = "";
		if (null != this.getLang() && attribute.isMultilingual()) {
			langModule = this.getLang().getCode() + "_";
		}
		if (this.isMonoListElement()) {
			if (this.isCompositeElement()) {
				formFieldName.append(langModule).append(this.getParentAttribute().getName()).append("_")
					.append(attribute.getName()).append("_").append(this.getListIndex());
			} else {
				formFieldName.append(langModule).append(attribute.getName()).append("_").append(this.getListIndex());
			}
		} else if (this.isListElement()) {
			formFieldName.append(this.getListLang().getCode())
				.append("_").append(attribute.getName()).append("_").append(this.getListIndex());
		} else {
			formFieldName.append(langModule).append(attribute.getName());
		}
		return formFieldName.toString();
	}
	
	public Lang getLang() {
		return _lang;
	}
	public void setLang(Lang lang) {
		this._lang = lang;
	}
	
	public boolean isCompositeElement() {
		return _compositeElement;
	}
	public void setCompositeElement(boolean compositeElement) {
		this._compositeElement = compositeElement;
	}
	public AttributeInterface getParentAttribute() {
		return _parentAttribute;
	}
	public void setParentAttribute(AttributeInterface parentAttribute) {
		this._parentAttribute = parentAttribute;
	}
	
	/**
	 * Determine whether the attribute belongs to an element
	 * of a 'monolist' Attribute or not.
	 * 
	 * @return true if the attribute belongs to a 'Monolist' Attribute, false otherwise.
	 */
	public boolean isMonoListElement() {
		return _monoListElement;
	}
	
	/**
	 * Set the membership of the attribute to a 'Monolist' Attribute.
	 * 
	 * @param monoListElement true if the element belongs to a 'Monolist' Attribute,
	 * false otherwise. 
	 */
	public void setMonoListElement(boolean monoListElement) {
		this._monoListElement = monoListElement;
	}
	
	/**
	 * Determine whether the attribute belongs to a 'Multi-Language List' Attribute element
	 * 
	 * @return true if the element belongs to a 'Multi-Language List' Attribute, false otherwise.
	 */
	public boolean isListElement() {
		return _listElement;
	}
	
	/**
	 * Set the membership of the attribute to a 'Multi-Language List' Attribute element.
	 * 
	 * @param listElement set true if the attribute belongs to a 'Multi-Language List' Attribute element,
	 * false otherwise.
	 */
	public void setListElement(boolean listElement) {
		this._listElement = listElement;
	}
	
	/**
	 * Return the language of the list when the current attribute is
	 * an element of a 'Multi-language List' attribute.
	 * 
	 * @return The language of the list.
	 */
	public Lang getListLang() {
		return _listLang;
	}
	
	/**
	 * Set the language of the list when the current attribute is an element belonging to a 
	 * multi-language list attribute.
	 * @param lang The language of the list.
	 */
	public void setListLang(Lang lang) {
		this._listLang = lang;
	}
	
	/**
	 * Return the index of the position of the attribute within a multi-language
	 * list or a monolist.
	 * 
	 * @return The index of the position of the attribute inside a list 
	 */
	public int getListIndex() {
		return _listIndex;
	}
	
	/**
	 * Setta l'indice relativo alla posizione dell'attributo elemento 
	 * all'interno di una lista multilingua o di una monolista.
	 * 
	 * @param listIndex Set the index of the position of the attribute.
	 */
	public void setListIndex(int listIndex) {
		this._listIndex = listIndex;
	}
	
	private Lang _lang;
	private boolean _monoListElement = false;
	private boolean _listElement = false;
	private Lang _listLang = null;
	private int _listIndex = -1;
	
	private boolean _compositeElement = false;
	private AttributeInterface _parentAttribute = null;
	
}
