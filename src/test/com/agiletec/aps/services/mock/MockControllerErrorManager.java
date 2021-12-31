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
import com.agiletec.aps.system.services.controller.ControllerManager;
import com.agiletec.aps.system.services.controller.control.AbstractControlService;

/**
 * @version 1.0
 * @author M.Diana
 */
public class MockControllerErrorManager extends AbstractControlService {
	
	public void afterPropertiesSet() throws Exception {
		// nothing to do
	}
	
	public int service(RequestContext req, int status) {
		if(status == ControllerManager.ERROR){
			req.addExtraParam(RequestContext.EXTRAPAR_REDIRECT_URL, "errorRedir");
			return ControllerManager.REDIRECT;
		}
		return status;
	}

}
