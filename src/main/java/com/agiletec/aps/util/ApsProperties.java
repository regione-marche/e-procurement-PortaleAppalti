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
package com.agiletec.aps.util;

import java.io.IOException;
import java.util.Properties;

/**
 * Estensione di Properties con l'aggiunta di metodi di lettura/scrittura da stringa xml.
 * @author 
 */
public class ApsProperties extends Properties {

	/**
	 * Come java.util.Properties
	 */
	public ApsProperties() {
		super();
	}

	/**
	 * Come java.util.Properties.
	 * @param defaults Valori di default
	 */
	public ApsProperties(Properties defaults) {
		super(defaults);
	}
	
	/**
	 * Setta Properties estraendole dal testo xml inserito.
	 * @param propertyXml L'xml da cui estrarre le Properties
	 * @throws IOException
	 */
	public void loadFromXml(String propertyXml) throws IOException {
		ApsProperties prop = null;
		if(propertyXml != null) {
			ApsPropertiesDOM propDom = new ApsPropertiesDOM();
			prop = propDom.extractProperties(propertyXml);
			this.putAll(prop);
		}
	}
	
	/**
	 * Costruisce l'xml relativo alle properties.
	 * @return La stringa xml.
	 * @throws IOException
	 */
	public String toXml() throws IOException {
		String xml = null;
		ApsProperties prop = (ApsProperties) this.clone();
		if (null != prop && prop.size() > 0) {
			ApsPropertiesDOM propDom = new ApsPropertiesDOM();
			propDom.buildJDOM(prop);
			xml = propDom.getXMLDocument();
		}
		return xml;
	}
	
	/**
	 * restiruisce il valore della proprieta' "customized" associata alla label 
	 */
	public int getCustomized(String langCode) {
		int custom = 0; 
		try {
			Integer v = (Integer) this.get(langCode + ".custom");
			custom = (v != null ? v.intValue() : 0);
		} catch (Exception e) {
		}
		return custom;
	}
	
	/**
	 * imposta il valore della proprieta' "customized" associata alla label
	 */	
	public void setCustomized(String langCode, int value) {
		this.put(langCode + ".custom", value);
	}
	
}
