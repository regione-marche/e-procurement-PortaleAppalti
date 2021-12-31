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

import javax.servlet.http.HttpServletResponse;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.aps.system.services.controller.ControllerManager;

/**
 * 
 * Implementazione del sottoservizio di controllo che 
 * gestisce gli errori
 * @author 
 */
public class ErrorManager extends AbstractControlService {
	
	public void afterPropertiesSet() throws Exception {
		_errorPageCode = configManager.getParam("errorPageCode");
		_notFoundPageCode = configManager.getParam("notFoundPageCode");
	}
	
	public int service(RequestContext reqCtx, int status) {
		int retStatus = ControllerManager.INVALID_STATUS;
		if(status != ControllerManager.ERROR
				&& status != ControllerManager.SYS_ERROR
				&& status != ControllerManager.INVALID_STATUS){
			return status;
		}
		_log.debug("Intervention of the error service");
		try {
			String redirDestPage = getDestPageCode(reqCtx);
			retStatus = redirect(redirDestPage, reqCtx);
		} catch (Throwable e) {
			ApsSystemUtils.logThrowable(e, this, "service", "Error detected while processing the request");
			retStatus = ControllerManager.SYS_ERROR;
			reqCtx.setHTTPError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
		}
		return retStatus;
	}

	private String getDestPageCode(RequestContext reqCtx){
		String pageCode = null;
		if("user".equals(reqCtx.getExtraParam("errorType"))
				&& "invalidRequest".equals(reqCtx.getExtraParam("errorCode"))){
			pageCode = _notFoundPageCode;
		} else {
			pageCode = _errorPageCode;
		}
		return pageCode;
	}
	
	protected ConfigInterface getConfigManager() {
		return configManager;
	}
	public void setConfigManager(ConfigInterface configService) {
		this.configManager = configService;
	}
	
	private  String _notFoundPageCode ;
	private  String _errorPageCode ;
	
	private ConfigInterface configManager;
	
	
}
