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
package test.com.agiletec.aps.services.mock;

import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.services.controller.control.RequestValidator;

/**
 * @version 1.0
 * @author M.Diana
 */
public class MockRequestValidator extends RequestValidator {
	
	protected String getResourcePath(RequestContext reqCtx) {
		String servletPath = (String) reqCtx.getExtraParam("testResourcePath");
		return servletPath;
	}
	
}
