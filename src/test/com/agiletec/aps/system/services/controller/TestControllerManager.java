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
package test.com.agiletec.aps.system.services.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import test.com.agiletec.aps.BaseTestCase;
import test.com.agiletec.aps.services.mock.MockControllerContinue;
import test.com.agiletec.aps.services.mock.MockControllerError;
import test.com.agiletec.aps.services.mock.MockControllerErrorManager;
import test.com.agiletec.aps.services.mock.MockControllerOutput;
import test.com.agiletec.aps.services.mock.MockControllerRedirect;
import test.com.agiletec.aps.services.mock.MockRequestValidator;

import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.controller.ControllerManager;
import com.agiletec.aps.system.services.controller.control.ControlServiceInterface;
import com.agiletec.aps.system.services.lang.ILangManager;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.page.IPage;

/**
 * @author M.Diana - W.Ambu
 */
public class TestControllerManager extends BaseTestCase {
	
	public void testService_1() throws ApsSystemException {
		RequestContext reqCtx = this.getRequestContext();
		ControllerManager controller = (ControllerManager) this.getService(SystemConstants.CONTROLLER_MANAGER);
		MockHttpServletRequest request = (MockHttpServletRequest) reqCtx.getRequest();
		request.setServletPath("/it/homepage.wp");
		int status = controller.service(reqCtx);
		assertEquals(ControllerManager.OUTPUT, status);
		
		request.setParameter("username", "admin");
		request.setParameter("password", "admin");
		status = controller.service(reqCtx);
		assertEquals(ControllerManager.OUTPUT, status);
	}
	
	public void testService_2() throws ApsSystemException {
		RequestContext reqCtx = this.getRequestContext();
		ControllerManager controller = (ControllerManager) this.getService(SystemConstants.CONTROLLER_MANAGER);
		MockHttpServletRequest request = (MockHttpServletRequest) reqCtx.getRequest();
		request.setServletPath("/it/customers_page.wp");
		int status = controller.service(reqCtx);
		assertEquals(ControllerManager.REDIRECT, status);
		
		request.setParameter("username", "admin");
		request.setParameter("password", "admin");
		request.setServletPath("/it/customers_page.wp");
		status = controller.service(reqCtx);
		assertEquals(ControllerManager.OUTPUT, status);
	}
	
	public void testService_3() throws ApsSystemException {
		RequestContext reqCtx = this.getRequestContext();
		ControllerManager controller = (ControllerManager) this.getService(SystemConstants.CONTROLLER_MANAGER);
		MockHttpServletRequest request = (MockHttpServletRequest) reqCtx.getRequest();
		request.setServletPath("/it/administrators_page.wp");
		int status = controller.service(reqCtx);
		assertEquals(ControllerManager.REDIRECT, status);
		
		request.setParameter(RequestContext.PAR_REDIRECT_FLAG, "1");
		status = controller.service(reqCtx);
		assertEquals(ControllerManager.SYS_ERROR, status);
	}
	
	public void testServiceOutput() throws Throwable {
		RequestContext req = new RequestContext();		
		ControllerManager controller = new ControllerManager();
		List<ControlServiceInterface> controllerServices = new ArrayList<ControlServiceInterface>();
		controllerServices.add(new MockControllerContinue());
		controllerServices.add(new MockControllerOutput());
		controller.setControllerServices(controllerServices);
		controller.setBeanFactory(this.getApplicationContext());
		controller.init();
		int status = controller.service(req);
		assertEquals(status, ControllerManager.OUTPUT);
	}

	public void testServiceRedirect() throws Throwable {
		RequestContext req = new RequestContext();
		ControllerManager controller = new ControllerManager();
		List<ControlServiceInterface> controllerServices = new ArrayList<ControlServiceInterface>();
		controllerServices.add(new MockControllerContinue());
		controllerServices.add(new MockControllerRedirect());
		controllerServices.add(new MockControllerOutput());
		controller.setControllerServices(controllerServices);
		controller.setBeanFactory(this.getApplicationContext());
		controller.init();
		int status = controller.service(req);
		assertEquals(status, ControllerManager.REDIRECT);
		assertEquals(req.getExtraParam(RequestContext.EXTRAPAR_REDIRECT_URL), "urlRedir");
	}
	
	public void testServiceErrorManager() throws Throwable {
		RequestContext req = new RequestContext();
		ControllerManager controller = new ControllerManager();
		List<ControlServiceInterface> controllerServices = new ArrayList<ControlServiceInterface>();
		controllerServices.add(new MockControllerContinue());
		controllerServices.add(new MockControllerError());
		controllerServices.add(new MockControllerOutput());
		controllerServices.add(new MockControllerErrorManager());
		controller.setControllerServices(controllerServices);
		controller.setBeanFactory(this.getApplicationContext());
		controller.init();
		int status = controller.service(req);
		assertEquals(status, ControllerManager.ERROR);
	}
	
	public void testServiceRequestValidator() throws Throwable {
		RequestContext req = new RequestContext();
		req.setResponse(new MockHttpServletResponse());
		ControllerManager controller = new ControllerManager();
		List<ControlServiceInterface> controllerServices = new ArrayList<ControlServiceInterface>();
		
		MockRequestValidator reqValidator = new MockRequestValidator();
		reqValidator.setLangManager((ILangManager) this.getService(SystemConstants.LANGUAGE_MANAGER));
		reqValidator.setPageManager((IPageManager) this.getService(SystemConstants.PAGE_MANAGER));
		controllerServices.add(reqValidator);
		controller.setControllerServices(controllerServices);
		controller.setBeanFactory(this.getApplicationContext());
		controller.init();
		req.addExtraParam("testResourcePath","/it/homepage.wp");
		int status = controller.service(req);
		assertEquals(status, ControllerManager.CONTINUE);
		Lang lang = (Lang) req.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG);
		assertEquals(lang.getCode(), "it");
		IPage page = (IPage) req.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE);
		assertEquals(page.getCode(), "homepage");
		req.addExtraParam("testResourcePath","/en/nonesiste.wp");
		status = controller.service(req);
		assertEquals(status, ControllerManager.ERROR);
	}
	
}
