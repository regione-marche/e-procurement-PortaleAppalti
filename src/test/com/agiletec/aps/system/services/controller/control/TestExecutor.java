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
package test.com.agiletec.aps.system.services.controller.control;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import test.com.agiletec.aps.BaseTestCase;

import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.services.controller.ControllerManager;
import com.agiletec.aps.system.services.controller.control.Executor;

/**
 * @version 1.0
 * @author M.Diana
 */
public class TestExecutor extends BaseTestCase {

    public void testService() throws Throwable {
		RequestContext reqCtx = new RequestContext();
		Executor executor = new Executor(); 
		executor.afterPropertiesSet();
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		reqCtx.setRequest(request);
		reqCtx.setResponse(response);	
		int status = executor.service(reqCtx, ControllerManager.CONTINUE);
		assertEquals(status, ControllerManager.OUTPUT);
	}    
    	
}
