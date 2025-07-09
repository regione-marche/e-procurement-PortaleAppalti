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
package com.agiletec.plugins.jacms.aps.system.services.resource.model.util;

import java.io.IOException;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.plugins.jacms.aps.system.services.resource.model.ImageResourceDimension;
import com.agiletec.plugins.jacms.aps.system.services.resource.parse.ImageDimensionDOM;

import it.maggioli.eldasoft.plugins.ppcommon.aps.SpringAppContext;

/**
 * Classe delegata al caricamento 
 * delle dimensioni per il redimensionamento delle immagini.
 * @author E.Santoboni
 */
public class ImageDimensionReader implements IImageDimensionReader {
	
	/**
	 * In ambiente cluster (Redis) le istanze dei manager non possono essere serializzate/deserializzate 
	 */	
	private void readObject(java.io.ObjectInputStream stream) throws ClassNotFoundException, IOException {
		stream.defaultReadObject();
		try {
			ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
			_configManager = (ConfigInterface) ctx.getBean(SystemConstants.BASE_CONFIG_MANAGER);
		} catch (Exception e) {
		}
	}

	/**
	 * Inizializzazione della classe.
	 * Effettua il caricamento delle dimensioni di resize delle immagini.
	 * @throws Exception In caso di errore.
	 */
	public void init() throws Exception {
		try {
    		String xml = this.getConfigManager().getConfigItem("imageDimensions");
    		if (xml == null) {
    			throw new ApsSystemException("Item configurazione assente: imageDimensions");
    		}
    		ImageDimensionDOM dimensionDom = new ImageDimensionDOM(xml);
    		this._imageDimensions = dimensionDom.getDimensions();
    	} catch (Throwable t) {
    		ApsSystemUtils.logThrowable(t, this, "init");
    		throw new ApsSystemException("Errore caricamento dimensioni immagini di resize", t);
    	}
	}
	
	/**
     * Restituisce la mappa delle dimensioni di resize delle immagini, 
     * indicizzate in base all'id della dimensione.
     * @return La mappa delle dimensioni di resize delle immagini.
     */
	@Override
	public Map<Integer, ImageResourceDimension> getImageDimensions() {
    	return _imageDimensions;
    }
    
    protected ConfigInterface getConfigManager() {
		return _configManager;
	}
	public void setConfigManager(ConfigInterface configService) {
		this._configManager = configService;
	}
	
	/**
     * Mappa delle dimensioni di resize delle immagini, 
     * indicizzate in base all'id della dimensione.
     */
    private Map<Integer, ImageResourceDimension> _imageDimensions;
    
    private transient ConfigInterface _configManager;
	
}
