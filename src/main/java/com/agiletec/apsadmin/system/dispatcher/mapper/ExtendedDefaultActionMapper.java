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
package com.agiletec.apsadmin.system.dispatcher.mapper;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.dispatcher.mapper.DefaultActionMapper;

/**
 * Estensione del Action Mapper a servizio della servlet delegata all'erogazione 
 * di funzionalità in "ExternalFramework".
 * @version 1.0
 * @author E.Santoboni
 */
public class ExtendedDefaultActionMapper extends DefaultActionMapper {
	
    /**
     * Gets the uri from the request
     * @param request The request
     * @return The uri
     */
    protected String getUri(HttpServletRequest request) {
        // handle http dispatcher includes.
        String uri = (String) request.getAttribute("javax.servlet.include.servlet_path");
        uri = (String) request.getAttribute("javax.servlet.include.request_uri");
        if (uri == null || "".equals(uri)) {
        	uri = (String) request.getAttribute("javax.servlet.include.servlet_path");
        } else {
        	return uri.substring((request.getContextPath()+"/ExtStr2").length());
        }
        if (uri != null && !"".equals(uri)) {
            return uri;
        }
        uri = request.getRequestURI();
        uri = uri.substring(request.getContextPath().length());
        return uri;
    }
    
}