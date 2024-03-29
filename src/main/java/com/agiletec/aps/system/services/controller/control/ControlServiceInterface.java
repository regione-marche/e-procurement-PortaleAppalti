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
package com.agiletec.aps.system.services.controller.control;

import org.springframework.beans.factory.InitializingBean;

import com.agiletec.aps.system.RequestContext;

/**
 * Interfaccia per i sottoservizi di controllo del controller (Controller).
 * I sottoservizi contengono le fasi operative per esaudire la richiesta del
 * client, e possono eseguire operazioni di autorizzazione, di verifica,
 * di preparazione, di generazione dell'output, di gestione e segnalazione
 * errori. Per ulteriori informazioni vedere la documentazione del controller.
 * I sottoservizi possono fornire informazioni ad altri sottoservizi o
 * alle classi di livello superiore inserendole nel contesto di richiesta
 * (RequestContext). Le classi implementanti devono esporre i soli 
 * metodi previsti nell'interfaccia.
 * @author 
 */
public interface ControlServiceInterface extends InitializingBean {
	
	/**
	 * Esegue le operazioni specifiche del sottoservizio.
	 * @param reqCtx Il contesto di richiesta
	 * @param status Lo stato di uscita del sottoservizio precedente
	 * @return Lo stato di uscita, una delle costanti di Controller
	 */
	public int service(RequestContext reqCtx, int status);

}
