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

import java.io.File;

import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.plugins.jacms.aps.system.services.resource.parse.ResourceDOM;

/**
 * Classe astratta di base per l'implementazione 
 * di oggetti Risorsa composti da una singola istanza.
 * @author E.Santoboni
 */
public abstract class AbstractMonoInstanceResource extends AbstractResource {
	
	/**
     * Implementazione del metodo isMultiInstance() di AbstractResource.
     * Restituisce sempre false in quanto questa classe astratta è 
     * alla base di tutte le risorse SingleInstance.
     * @return false in quanto la risorsa è composta da una singola istanza. 
     */
	@Override
	public boolean isMultiInstance() {
    	return false;
    }
    
	@Override
	public void deleteResourceInstances() throws ApsSystemException {
		try {
			String docName = this.getInstance().getFileName();
		    File fileTemp = new File(this.getDiskFolder() + docName);
			fileTemp.delete();
		} catch (Throwable t) {
			throw new ApsSystemException("Errore in rimozione istanze", t);
		}
	}
    
	/**
     * Setta l'istanza alla risorsa.
     * @param instance L'istanza da settare alla risorsa.
     */
	@Override
	public void addInstance(ResourceInstance instance) {
    	this._instance = instance;
    }
    
    /**
     * Restituisce l'istanza della risorsa.
     * @return L'istanza della risorsa.
     */
    public ResourceInstance getInstance() {
    	return _instance ;
    }
    
    @Override
	public String getXML() {
        ResourceDOM resourceDom = this.getResourceDOM();
        resourceDom.addInstance(this.getInstance().getJDOMElement());
        return resourceDom.getXMLDocument();
    }
    
    /**
	 * Restituisce il nome corretto del file con cui 
	 * un'istanza di una risorsa viene salvata nel fileSystem. 
	 * @param masterFileName Il nome del file principale da cui ricavare l'istanza.
	 * @return Il nome corretto del file.
	 */
	public String getInstanceFileName(String masterFileName) {
    	return this.getRevisedInstanceFileName(masterFileName);
	}
    
	private ResourceInstance _instance;

}
