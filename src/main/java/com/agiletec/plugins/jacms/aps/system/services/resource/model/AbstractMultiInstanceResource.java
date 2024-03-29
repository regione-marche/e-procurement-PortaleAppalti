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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.plugins.jacms.aps.system.services.resource.parse.ResourceDOM;

/**
 * Classe astratta di base per l'implementazione 
 * di oggetti Risorsa Multi-Istanza.
 * @author E.Santoboni
 */
public abstract class AbstractMultiInstanceResource extends AbstractResource {
	
	/**
	 * Inizializza la mappa delle istanze della risorsa.
	 */
	public AbstractMultiInstanceResource() {
        super();
        this._instances = new HashMap<String, ResourceInstance>();
    }
    
	@Override
	public void deleteResourceInstances() throws ApsSystemException {
		try {
			Collection<ResourceInstance> instances = this.getInstances().values();
			Iterator<ResourceInstance> instancesIter = instances.iterator();
			while (instancesIter.hasNext()) {
				ResourceInstance currentInstance = instancesIter.next();
				String fileName = currentInstance.getFileName();
				File fileTemp = new File(this.getDiskFolder() + fileName);
				fileTemp.delete();
			}
		} catch (Throwable t) {
			throw new ApsSystemException("Errore in rimozione istanze", t);
		}
	}
    
    /**
     * Implementazione del metodo isMultiInstance() di AbstractResource.
     * Restituisce sempre true in quanto questa classe astratta è 
     * alla base di tutte le risorse MultiInstance.
     * @return true in quanto la risorsa è MultiInstance. 
     */
	@Override
	public boolean isMultiInstance() {
    	return true;
    }
    
    /**
     * Restituisce l'xml completo della risorsa.
     * @return L'xml completo della risorsa.
     */
	@Override
	public String getXML() {
    	ResourceDOM resourceDom = this.getResourceDOM();
    	List<ResourceInstance> instances = new ArrayList<ResourceInstance>(this.getInstances().values());
    	for (int i=0; i<instances.size(); i++) {
    		ResourceInstance currentInstance = instances.get(i);
    		resourceDom.addInstance(currentInstance.getJDOMElement());
    	}
    	return resourceDom.getXMLDocument();
    }
    
    /**
	 * Restituisce il nome corretto del file con cui 
	 * un'istanza di una risorsa viene salvata nel fileSystem. 
	 * @param masterFileName Il nome del file principale da cui ricavare l'istanza.
	 * @param size Il size dell'istanza della risorsa multiInstanza.
	 * @param langCode Il codice lingua dell'istanza della risorsa multiInstanza.
	 * @return Il nome corretto del file.
	 */
	public abstract String getInstanceFileName(String masterFileName, int size, String langCode);
	
	/**
	 * Restituisce un'istanza della risorsa.
	 * @param size Il size dell'istanza della risorsa multiInstanza.
	 * @param langCode Il codice lingua dell'istanza della risorsa multiInstanza.
	 * @return L'istanza cercata.
	 */
	public abstract ResourceInstance getInstance(int size, String langCode);
	
    /**
     * Restituisce la mappa delle istanze della risorsa, 
     * indicizzate in base al size o alla lingua dell'istanza.
     * @return La mappa delle istanze della risorsa.
     */
    public Map<String, ResourceInstance> getInstances() {
    	return this._instances;
    }
    
    private Map<String, ResourceInstance> _instances;
	
}
