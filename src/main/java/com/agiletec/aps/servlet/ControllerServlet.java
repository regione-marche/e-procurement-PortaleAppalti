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

import java.io.IOException;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.controller.ControllerManager;
import com.agiletec.aps.util.ApsWebApplicationUtils;

/**
 * Servlet di controllo, punto di ingresso per le richieste di pagine del
 * portale. Predispone il contesto di richiesta, invoca il controller e ne
 * gestisce lo stato di uscita.
 *
 * @version 1.0
 * @author
 */
public class ControllerServlet extends HttpServlet {

	protected void service(HttpServletRequest request, HttpServletResponse response)
					throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		RequestContext reqCtx = new RequestContext();
		Logger log = ApsSystemUtils.getLogger();
		if (log.isTraceEnabled()) {
			log.trace("Richiesta:" + request.getServletPath());
		}
		request.setAttribute(RequestContext.REQCTX, reqCtx);
		reqCtx.setRequest(request);
		reqCtx.setResponse(response);
		ControllerManager controller
						= (ControllerManager) ApsWebApplicationUtils.getBean(SystemConstants.CONTROLLER_MANAGER, request);
		int status = controller.service(reqCtx);
		if (status == ControllerManager.REDIRECT) {
			if (log.isTraceEnabled()) {
				log.trace("Redirezione");
			}
			this.redirect(reqCtx, response);
		} else if (status == ControllerManager.OUTPUT) {
			if (log.isTraceEnabled()) {
				log.trace("Output");
			}
		} else if (status == ControllerManager.ERROR) {
			this.outputError(reqCtx, response);
			if (log.isTraceEnabled()) {
				log.trace("Errore");
			}
		} else {
			log.error("Errore: stato uscita controller = "
							+ ControllerManager.getStatusDescription(status)
							+ " per richiesta: ");
			log.error(request.getServletPath());
			throw new ServletException("Servizio non disponibile");
		}
		return;
	}

	private void redirect(RequestContext reqCtx, HttpServletResponse response)
					throws ServletException {
		try {
			String url = (String) reqCtx.getExtraParam(RequestContext.EXTRAPAR_REDIRECT_URL);
			response.sendRedirect(url);
		} catch (Exception e) {
			throw new ServletException("Servizio non disponibile");
		}
	}

	private void outputError(RequestContext reqCtx, HttpServletResponse response)
					throws ServletException {
		try {
			if (!response.isCommitted()) {
				Integer httpErrorCode = (Integer) reqCtx.getExtraParam("errorCode");
				if (httpErrorCode == null) {
					httpErrorCode = new Integer(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
				}
				response.sendError(httpErrorCode.intValue());
			}
		} catch (IOException e) {
			ApsSystemUtils.logThrowable(e, this, "outputError");
			throw new ServletException("Servizio non disponibile");
		} catch (ClassCastException ex) {
			ApsSystemUtils.logThrowable(ex, this, "outputError");
			throw new ServletException("Servizio non disponibile");
		}
	}
}
