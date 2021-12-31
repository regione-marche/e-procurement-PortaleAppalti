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
import com.agiletec.aps.system.services.controller.control.RequestValidator;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.lang.ILangManager;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;

/**
 * @version 1.0
 * @author M.Casari
 */
public class TestRequestValidator extends BaseTestCase {
	
	protected void setUp() throws Exception {
        super.setUp();
        this.init();
    }
	
    public void testService() throws ApsSystemException {
		RequestContext reqCtx = new RequestContext();
		MockHttpServletRequest request = new MockHttpServletRequest();
		reqCtx.setRequest(request);
		request.setServletPath("/it/homepage.wp");
		int status = _requestValidator.service(reqCtx, ControllerManager.CONTINUE);
		assertEquals(status, ControllerManager.CONTINUE);
		assertNotNull(reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG));
		assertNotNull(reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE));
		Lang lang = (Lang) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG);
		IPage page = (IPage) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE);
		assertEquals(lang.getCode(), "it");
		assertEquals(page.getCode(), "homepage");
	}    
	
	public void testServiceFailure() throws ApsSystemException {
		RequestContext reqCtx = new RequestContext();
		MockHttpServletRequest request = new MockHttpServletRequest();
		reqCtx.setRequest(request);
		request.setRequestURI("/it/notexists.wp");
		int status = _requestValidator.service(reqCtx, ControllerManager.CONTINUE);
		assertEquals(status, ControllerManager.ERROR);
	}
	
	private void init() throws Exception {
		_requestValidator = new RequestValidator();
        try {
        	IPageManager pageManager = (IPageManager) this.getService(SystemConstants.PAGE_MANAGER);
        	ILangManager langManager = (ILangManager) this.getService(SystemConstants.LANGUAGE_MANAGER);
        	
        	_requestValidator.setLangManager(langManager);
        	_requestValidator.setPageManager(pageManager);
        	_requestValidator.afterPropertiesSet();
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }
	
	private RequestValidator _requestValidator;
    	
}
