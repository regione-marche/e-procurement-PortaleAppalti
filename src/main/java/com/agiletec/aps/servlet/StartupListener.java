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
package com.agiletec.aps.servlet;

import it.maggioli.eldasoft.plugins.ppcommon.aps.system.TrackerSessioniUtenti;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

/**
 * Init the system when the web application is started
 * @version 1.0
 * @author 
 */
public class StartupListener extends org.springframework.web.context.ContextLoaderListener {
	
	/** 
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent event) {
		ServletContext svCtx = event.getServletContext();
		String msg = this.getClass().getName()+ ": INIT " + svCtx.getServletContextName();
		System.out.println(msg);
		super.contextInitialized(event);
		// inizializzazione del tracker delle sessioni nel contesto
		TrackerSessioniUtenti tracker = new TrackerSessioniUtenti();
		svCtx.setAttribute(TrackerSessioniUtenti.UTENTI_CONNESSI, tracker);
		//msg = this.getClass().getName() + ": INIT DONE "+ svCtx.getServletContextName();
		//System.out.println(msg);
	}
	
}
