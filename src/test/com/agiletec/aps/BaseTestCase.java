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
package test.com.agiletec.aps;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;

import test.com.agiletec.ConfigTestUtils;

import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.IManager;
import com.agiletec.aps.system.services.lang.ILangManager;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.notify.NotifyManager;
import com.agiletec.aps.system.services.user.IAuthenticationProviderManager;
import com.agiletec.aps.system.services.user.IUserManager;
import com.agiletec.aps.system.services.user.UserDetails;

/**
 * @version 1.0
 * @author W.Ambu
 */
public class BaseTestCase extends TestCase {
	
	protected void setUp() throws Exception {
		try {
			super.setUp();
			ServletContext srvCtx = new MockServletContext("", new FileSystemResourceLoader());
			ApplicationContext applicationContext = this.getConfigUtils().createApplicationContext(srvCtx);
			this.setApplicationContext(applicationContext);
			RequestContext reqCtx = this.createRequestContext(applicationContext, srvCtx);
			this.setRequestContext(reqCtx);
			this.setUserOnSession("guest");
		} catch (Exception e) {
			throw e;
		}
	}
	
	protected RequestContext createRequestContext(ApplicationContext applicationContext, ServletContext srvCtx) {
    	RequestContext reqCtx = new RequestContext();
    	srvCtx.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, applicationContext);
    	MockHttpServletRequest request = new MockHttpServletRequest();
    	MockHttpServletResponse response = new MockHttpServletResponse();
		MockHttpSession session = new MockHttpSession(srvCtx);
		request.setSession(session);
		reqCtx.setRequest(request);
		reqCtx.setResponse(response);
		
		//TODO SETTARE PERAMETRI BASE DEL CONTESTO DELLA RICHIESTA.
		ILangManager langManager = (ILangManager) this.getService(SystemConstants.LANGUAGE_MANAGER);
		Lang defaultLang = langManager.getDefaultLang();
		reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG, defaultLang);
		
    	return reqCtx;
    }
	
	protected void tearDown() throws Exception {
		this.waitNotifyingThread();
		super.tearDown();
		this.getConfigUtils().closeDataSources(this.getApplicationContext());
	}
    
	protected void waitNotifyingThread() throws InterruptedException {
		Thread[] threads = new Thread[20];
	    Thread.enumerate(threads);
	    for (int i=0; i<threads.length; i++) {
	    	Thread currentThread = threads[i];
	    	if (currentThread != null && 
	    			currentThread.getName().startsWith(NotifyManager.NOTIFYING_THREAD_NAME)) {
	    		currentThread.join();
	    	}
	    }
	}
	
	protected void setUserOnSession(String username) throws Exception {
		IAuthenticationProviderManager provider = (IAuthenticationProviderManager) this.getService("AuthenticationProviderManager");
		IUserManager userManager = (IUserManager) this.getService(SystemConstants.USER_MANAGER);
		UserDetails currentUser = null;
		if (username.equals(SystemConstants.GUEST_USER_NAME)) {
			currentUser = userManager.getGuestUser();
		} else {
			currentUser = provider.getUser(username, username);//nel database di test, username e password sono uguali
		}
		HttpSession session = _reqCtx.getRequest().getSession();
		session.setAttribute(SystemConstants.SESSIONPARAM_CURRENT_USER, currentUser);
    }
    
	protected RequestContext getRequestContext() {
        return this._reqCtx;
    }
    
	protected void setRequestContext(RequestContext reqCtx) {
        this._reqCtx = reqCtx;
    }
    
	protected IManager getService(String name) {
		return (IManager) this.getApplicationContext().getBean(name);
	}
	
	protected ApplicationContext getApplicationContext() {
    	return this._applicationContext;
    }
    
	protected void setApplicationContext(ApplicationContext applicationContext) {
    	this._applicationContext = applicationContext;
    }
	
	protected ConfigTestUtils getConfigUtils() {
		return new ConfigTestUtils();
	}
    
    private RequestContext _reqCtx;
    
    private ApplicationContext _applicationContext;
    
}