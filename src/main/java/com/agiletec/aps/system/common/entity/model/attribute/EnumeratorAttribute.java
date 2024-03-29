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

import java.util.List;

import org.jdom.CDATA;
import org.jdom.Element;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import com.agiletec.aps.system.common.entity.model.attribute.util.EnumeratorAttributeItemsExtractor;
import com.agiletec.aps.system.exception.ApsSystemException;

/**
 * This class describes an "Enumerator" Attribute.
 * @author E.Santoboni
 */
public class EnumeratorAttribute extends MonoTextAttribute implements BeanFactoryAware {
	
	@Override
	public Object getAttributePrototype() {
		EnumeratorAttribute prototype = (EnumeratorAttribute) super.getAttributePrototype();
		prototype.setBeanFactory(this.getBeanFactory());
		prototype.setItems(this.getItems());
		prototype.setStaticItems(this.getStaticItems());
		prototype.setExtractorBeanName(this.getExtractorBeanName());
		prototype.setCustomSeparator(this.getCustomSeparator());
		return prototype;
	}
	
	@Override
	public void setAttributeConfig(Element attributeElement) throws ApsSystemException {
		super.setAttributeConfig(attributeElement);
		String separator = this.extractXmlAttribute(attributeElement, "separator", false);
		if (null == separator || separator.trim().length()==0) {
			separator = DEFAULT_ITEM_SEPARATOR;
		}
		this.setCustomSeparator(separator);
		String text = attributeElement.getText();
		this.setStaticItems(text);
		String extractorBeanName = this.extractXmlAttribute(attributeElement, "extractorBean", false);
		this.setExtractorBeanName(extractorBeanName);
		this.initItems();
	}
	
	protected void initItems() {
		if (null != this.getStaticItems()) {
			this.setItems(this.getStaticItems().split(this.getCustomSeparator()));
		}
		if (null == this.getExtractorBeanName()) return;
		EnumeratorAttributeItemsExtractor extractor = (EnumeratorAttributeItemsExtractor) this.getBeanFactory().getBean(this.getExtractorBeanName(), EnumeratorAttributeItemsExtractor.class);
		if (null != extractor) {
			List<String> items = extractor.getItems();
			if (items != null && items.size()>0) this.addExtractedItems(items);
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
		this.setConfig(configElement);
	}

	private void setConfig(Element configElement) {
		if (null != this.getStaticItems()) {
			CDATA cdata = new CDATA(this.getStaticItems());
			configElement.addContent(cdata);
		}
		if (null != this.getExtractorBeanName()) {
			configElement.setAttribute("extractorBean", this.getExtractorBeanName());
		}
		if (null != this.getCustomSeparator()) {
			configElement.setAttribute("separator", this.getCustomSeparator());
		}
	}

	private void addExtractedItems(List<String> items) {
		String[] values = null;
		if (null == this.getItems() || this.getItems().length==0) {
			values = new String[items.size()];
			for (int i=0; i<items.size(); i++) {
				String item = items.get(i);
				values[i] = item;
			}
		} else {
			values = new String[this.getItems().length + items.size()];
			for (int i=0; i<this.getItems().length; i++) {
				String item = this.getItems()[i];
				values[i] = item;
			}
			for (int i=0; i<items.size(); i++) {
				String item = items.get(i);
				values[i+this.getItems().length] = item;
			}
		}
		this.setItems(values);
	}
	
	public String[] getItems() {
		return _items;
	}
	public void setItems(String[] items) {
		this._items = items;
	}
	
	public String getStaticItems() {
		return _staticItems;
	}
	public void setStaticItems(String staticItems) {
		this._staticItems = staticItems;
	}
	
	protected String getExtractorBeanName() {
		return _extractorBeanName;
	}
	protected void setExtractorBeanName(String extractorBeanName) {
		this._extractorBeanName = extractorBeanName;
	}
	
	public String getCustomSeparator() {
		if (null == this._customSeparator) return DEFAULT_ITEM_SEPARATOR;
		return _customSeparator;
	}
	public void setCustomSeparator(String customSeparator) {
		this._customSeparator = customSeparator;
	}
	
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this._beanFactory = beanFactory;
	}
	protected BeanFactory getBeanFactory() {
		return this._beanFactory;
	}
	
	private String[] _items;
	private String _staticItems;
	private String _extractorBeanName;
	private String _customSeparator;
	
	private BeanFactory _beanFactory;
	
	private final String DEFAULT_ITEM_SEPARATOR = ",";
	
}