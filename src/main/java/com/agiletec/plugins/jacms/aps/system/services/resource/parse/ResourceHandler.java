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
package com.agiletec.plugins.jacms.aps.system.services.resource.parse;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.agiletec.aps.system.services.category.Category;
import com.agiletec.aps.system.services.category.ICategoryManager;
import com.agiletec.plugins.jacms.aps.system.services.resource.model.ResourceInstance;
import com.agiletec.plugins.jacms.aps.system.services.resource.model.ResourceInterface;

/**
 * Classe "handler" di supporto all'interpretazione 
 * dell'XML che rappresenta una risorsa.
 * @author W.Ambu - E.Santoboni
 */
public class ResourceHandler extends DefaultHandler {
	
	/**
	 * Inizializzazione dell'handler.
	 * @param resource La risorsa prototipo da valorizzare.
	 * @param categoryManager Il manager delle categorie.
	 */
	public ResourceHandler(ResourceInterface resource, ICategoryManager categoryManager) {
		super();
		this._currentResource = resource;
		this._categoryManager = categoryManager;
	}
	
	@Override
	public void characters(char[] buf, int offset, int length) throws SAXException {
		String s = new String(buf, offset, length);
		if (_textBuffer == null) {
			_textBuffer = new StringBuffer(s);
		} else {
			_textBuffer.append(s);
		}
	}
	
	/**
	 * Sovrascrive il metodo omonimo dell'interfaccia ContentHandler.
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException{
		_textBuffer = null;
		if (qName.equals("resource")){
			defineResource(attributes, "resource");
		} else if (qName.equals("instance")){
			defineInstance(attributes, "instance");
		} else if (qName.equals("groups")) {
			startGroups(attributes, qName);
		} else if (qName.equals("category")) {
			startCategory(attributes, "category");
		}
	}
	
	/**
	 * Sovrascrive il metodo omonimo dell'interfaccia ContentHandler.
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (null != _textBuffer) {
			String text = _textBuffer.toString();
			if (qName.equals("descr")){
				this.getCurrentResource().setDescr(text);
			} else if (qName.equals("groups")) {
				this.endGroups();
			} else if (qName.equals("category")) {
				this.endCategory();
			} else if(qName.equals("lang")){
				_currentInstance.setLangCode(text);
			} else if(qName.equals("size")){
				_currentInstance.setSize(Integer.parseInt(text.trim()));
			} else if(qName.equals("filename")){
				_currentInstance.setFileName(text);
			} else if(qName.equals("mimetype")){	
				_currentInstance.setMimeType(text);
			} else if(qName.equals("weight")){
				_currentInstance.setFileLength(text);
			}
			_textBuffer = null;
		}
		if(qName.equals("instance")){
			this.getCurrentResource().addInstance(_currentInstance);
			_currentInstance = null;
		}
	}
	
	private void defineInstance(Attributes attributes, String tagName) throws SAXException {
		_currentInstance = new ResourceInstance();
	}
	
	private void defineResource(Attributes attributes, String tagName) throws SAXException{
		String id = extractXmlAttribute(attributes, "id", tagName, true);
		this.getCurrentResource().setId(id);
		String typecode = extractXmlAttribute(attributes, "typecode", tagName, true);
		this.getCurrentResource().setType(typecode);
	}
	
	/**
	 * Recupera in modo controllato un attributo di un tag xml dall'insieme
	 * degli attributi.
	 * @param attrs Attributi del tag xml
	 * @param attributeName Nome dell'attributo richiesto
	 * @param tagName Nome del tag xml
	 * @param required Se true, l'attributo è considerato obbligatorio.
	 * @return Il valore dell'attributo richiesto.
	 * @throws SAXException Nel caso l'attributo sia dichiarato obbligatorio e
	 * risulti assente.
	 */
	protected String extractXmlAttribute(Attributes attrs, String attributeName,
			String tagName, boolean required) throws SAXException {
		int index = attrs.getIndex(attributeName);
		String value = attrs.getValue(index);		
		if(required && value == null) {
			throw new SAXException("Attributo '" + attributeName +"' assente in tag <" + tagName + ">");
		}
		return value;
	}
	
	private void startGroups(Attributes attributes, String qName) throws SAXException {
		String mainGroup = extractXmlAttribute(attributes, "mainGroup", qName, true);
		this.getCurrentResource().setMainGroup(mainGroup);
	}
	
	private void endGroups() {
		return; // nulla da fare
	}
	
	private void startCategory(Attributes attributes, String tagName) throws SAXException {
		String categoryCode = extractXmlAttribute(attributes, "id", tagName, true);
		Category category = _categoryManager.getCategory(categoryCode);
		if (null != category) {
			this.getCurrentResource().addCategory(category);
		}
	}
	
	private void endCategory() {
		return; // niente da fare
	}
	
	protected ResourceInterface getCurrentResource() {
		return _currentResource;
	}
	
	public StringBuffer getTextBuffer() {
		return _textBuffer;
	}
	public void setTextBuffer(StringBuffer buffer) {
		this._textBuffer = buffer;
	}
	
	private ResourceInterface _currentResource;
	private ResourceInstance _currentInstance;
	private ICategoryManager _categoryManager;
	private StringBuffer _textBuffer;
	
}
