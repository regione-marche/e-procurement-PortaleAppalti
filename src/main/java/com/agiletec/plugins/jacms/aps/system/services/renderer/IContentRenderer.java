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
package com.agiletec.plugins.jacms.aps.system.services.renderer;

import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.common.renderer.IEntityRenderer;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;

/**
 * Interfaccia base per l'implementazione dei servizi 
 * di renderizzazione contenuti.
 * @author
 */
public interface IContentRenderer extends IEntityRenderer {
	
	/**
	 * Esegue il rendering del contenuto 
	 * secondo il modello specificato e nella lingua richiesta.
	 * @param content Il contenuto da renderizzare.
	 * @param modelId L'identificativo del modello di contenuto 
	 * tramite il quale effettuare la renderizzazione.
	 * @param langCode Il codice della lingua di renderizzazione.
	 * @param reqCtx Il contesto della richiesta.
	 * @return Il contenuto renderizzato.
	 */
	public String render(Content content, long modelId, String langCode, RequestContext reqCtx);
	
}
