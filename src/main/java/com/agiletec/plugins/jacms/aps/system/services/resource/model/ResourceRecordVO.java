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
package com.agiletec.plugins.jacms.aps.system.services.resource.model;

/**
 * Oggetto di utilità per le risorse. Ha la funzione fare da tramite 
 * (tra dati estratti da db e parser) nel caricamento di una risorsa completa.
 * @author E.Santoboni
 */
public class ResourceRecordVO {
	
	/**
	 * Restituisce l'id della risorsa.
	 * @return L'id della risorsa.
	 */
	public String getId() {
		return _id;
	}
	
	/**
	 * Setta l'id della risorsa.
	 * @param id L'id della risorsa.
	 */
	public void setId(String id) {
		this._id = id;
	}
	
	/**
	 * Restituisce il tipo della risorsa.
	 * @return Il tipo della risorsa.
	 */
	public String getResourceType() {
		return _resourceType;
	}

	/**
	 * Setta il tipo della risorsa.
	 * @param resourceType Il tipo della risorsa.
	 */
	public void setResourceType(String resourceType) {
		this._resourceType = resourceType;
	}
	
	/**
	 * Restituisce la descrizione della risorsa.
	 * @return La descrizione della risorsa.
	 */
	public String getDescr() {
		return _descr;
	}
	
	/**
	 * Setta la descrizione della risorsa.
	 * @param descr La descrizione della risorsa.
	 */
	public void setDescr(String descr) {
		this._descr = descr;
	}
	
	/**
     * Restituisce la stringa identificante 
     * il gruppo principale di cui la risorsa è membro.
     * @return Il gruppo principale di cui la risorsa è membro.
     */
    public String getMainGroup() {
		return _mainGroup;
	}
	
    /**
	 * Setta la stringa identificante 
     * il gruppo principale di cui il contenuto è membro.
	 * @param mainGroup Il gruppo principale di cui il contenuto è membro.
	 */
    public void setMainGroup(String mainGroup) {
		this._mainGroup = mainGroup;
	}
	
	/**
	 * Restituisce l'xml completo della risorsa.
	 * @return L'xml completo della risorsa.
	 */
	public String getXml() {
		return _xml;
	}
	
	/**
	 * Setta l'xml completo della risorsa.
	 * @param xml L'xml completo della risorsa.
	 */
	public void setXml(String xml) {
		this._xml = xml;
	}
	
	private String _id;
	private String _resourceType;
	private String _descr;
	private String _mainGroup;
	private String _xml;
	
}
