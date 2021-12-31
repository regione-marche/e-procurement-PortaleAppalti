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

import test.com.agiletec.aps.BaseTestCase;

import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.controller.ControllerManager;
import com.agiletec.aps.system.services.controller.control.Authenticator;
import com.agiletec.aps.system.services.controller.control.RequestAuthorizator;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.user.UserDetails;

/**
 * @version 1.0
 * @author M.Diana
 */
public class TestRequestAuthorizator extends BaseTestCase {
	
	protected void setUp() throws Exception {
        super.setUp();
        this.init();
    }
	
	public void testService_1() throws ApsSystemException {
		RequestContext reqCtx = this.getRequestContext();
		MockHttpServletRequest request = (MockHttpServletRequest) reqCtx.getRequest();
		request.setParameter("username", "admin");
		request.setParameter("password", "admin");
		IPage root = this._pageManager.getRoot();
		reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE, root);
		int status = _authenticator.service(reqCtx, ControllerManager.CONTINUE);
		status = _authorizator.service(reqCtx, status);
		assertEquals(status, ControllerManager.CONTINUE);
		UserDetails currentUser = (UserDetails) request.getSession().getAttribute(SystemConstants.SESSIONPARAM_CURRENT_USER);
		assertEquals("admin", currentUser.getUsername());
	}
	
	public void testService_2() throws ApsSystemException {
		RequestContext reqCtx = this.getRequestContext();
		MockHttpServletRequest request = (MockHttpServletRequest) reqCtx.getRequest();
		request.setParameter("username", "wrongUsername");
		request.setParameter("password", "wrongPassword");
		IPage root = this._pageManager.getRoot();
		reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE, root);
		int status = _authenticator.service(reqCtx, ControllerManager.CONTINUE);
		status = _authorizator.service(reqCtx, status);
		assertEquals(status, ControllerManager.CONTINUE);
		UserDetails currentUser = (UserDetails) request.getSession().getAttribute(SystemConstants.SESSIONPARAM_CURRENT_USER);
		assertEquals(SystemConstants.GUEST_USER_NAME, currentUser.getUsername());
	}
	
	public void testServiceFailure() throws ApsSystemException {
		RequestContext reqCtx = this.getRequestContext();
		MockHttpServletRequest request = (MockHttpServletRequest) reqCtx.getRequest();
		request.setParameter("username", "wrongUsername");
		request.setParameter("password", "wrongPassword");
		IPage root = this._pageManager.getPage("customers_page");
		reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE, root);
		int status = _authenticator.service(reqCtx, ControllerManager.CONTINUE);
		status = _authorizator.service(reqCtx, status);
		assertEquals(status, ControllerManager.REDIRECT);
	}
	
	private void init() throws Exception {
        try {
        	this._authenticator = (Authenticator) this.getApplicationContext().getBean("AuthenticatorControlService");
        	this._authorizator = (RequestAuthorizator) this.getApplicationContext().getBean("RequestAuthorizatorControlService");
        	this._pageManager = (IPageManager) this.getService(SystemConstants.PAGE_MANAGER);
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }
	
	private Authenticator _authenticator;
	
	private RequestAuthorizator _authorizator;
	
	private IPageManager _pageManager;
    
}