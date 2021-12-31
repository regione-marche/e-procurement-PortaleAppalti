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

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.services.controller.ControllerManager;

/**
 * Implementazione del sottoservizio di controllo che 
 * genera l'output destinato al client. Questa implementazione 
 * invoca la jsp "/WEB-INF/aps/jsp/system/main.jsp"
 * @author M.Diana
 */
public class Executor implements ControlServiceInterface {
	
	public void afterPropertiesSet() throws Exception {
		ApsSystemUtils.getLogger().debug(this.getClass().getName() + ": initialized");
	}
	
	public int service(RequestContext reqCtx, int status) {
		int retStatus = ControllerManager.INVALID_STATUS;
		if (status == ControllerManager.ERROR) {
			return status;
		}
		Logger log = ApsSystemUtils.getLogger();
		try {
			HttpServletResponse resp = reqCtx.getResponse();
			HttpServletRequest req = reqCtx.getRequest();
			String jspPath = "/WEB-INF/aps/jsp/system/main.jsp";
			req.setCharacterEncoding("UTF-8");
			RequestDispatcher dispatcher = req.getRequestDispatcher(jspPath);
			dispatcher.forward(req, resp);
			log.trace("Eseguito forward verso " + jspPath);
			retStatus = ControllerManager.OUTPUT;
		} catch (Throwable e) {
			ApsSystemUtils.logThrowable(e, this, "service", "Error while forwarding to main.jsp");
			retStatus = ControllerManager.ERROR;
		}
		return retStatus;
	}

}
