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
package com.agiletec.aps.tags;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.Showlet;
import com.agiletec.aps.util.ApsProperties;
import com.agiletec.apsadmin.system.TokenInterceptor;

/**
 * Tag for showlet "InternalServlet".
 * @author M.Casari - E.Santoboni
 */
public class InternalServletTag extends TagSupport {
	
	/** 
	 * Classe interna che fa da wrapper alla response estendendo 
	 * la javax.servlet.http.HttpServletResponseWrapper 
	 * e fornendo un proprio canale di output. Viene utilizzada 
	 * per recuperare il contenuto della response
	 * dopo aver fatto un include sul RequestDispatcher.
	 */
	public class ResponseWrapper extends HttpServletResponseWrapper {
		
		public ResponseWrapper(HttpServletResponse response) {
			super(response);
			_output = new CharArrayWriter();
		}
		
		@Override
		public PrintWriter getWriter() {
			return new PrintWriter(_output);
		}
		
		@Override
		public void sendRedirect(String path) throws IOException {
			this._redirectPath = path;
		}
		
		@Override
		public void addCookie(Cookie cookie) {
			super.addCookie(cookie);
			this._cookieToAdd = cookie;
		}
		
		protected Cookie getCookieToAdd() {
			return _cookieToAdd;
		}
		
		public boolean isRedirected() {
			return (_redirectPath != null);
		}
		
		public String getRedirectPath() {
			return _redirectPath;
		}
		
		@Override
		public String toString() {
			return _output.toString();
		}
		
		private String _redirectPath;
		private CharArrayWriter _output;
		private Cookie _cookieToAdd;
		
	}
	
	/**
	 * Invoca le showlet configurate nella pagina.
	 * @throws JspException In caso di errori occorsi in questo metodo 
	 * o in una delle jsp incluse.
	 */
	public int doEndTag() throws JspException {
		int result = super.doEndTag();
		ServletRequest req =  this.pageContext.getRequest();
		RequestContext reqCtx = (RequestContext) req.getAttribute(RequestContext.REQCTX);
		try {
			IPage page = (IPage) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE);
			ResponseWrapper responseWrapper = new ResponseWrapper((HttpServletResponse)this.pageContext.getResponse());
			String output = this.buildShowletOutput(page, responseWrapper);
			if (responseWrapper.isRedirected()) {
				String redirect = responseWrapper.getRedirectPath();
				reqCtx.addExtraParam(SystemConstants.EXTRAPAR_EXTERNAL_REDIRECT, redirect);
				result = SKIP_PAGE;
			} else {
				this.pageContext.getOut().print(output);
			}
		} catch (Throwable t) {
			String msg = "Error in showlet preprocessing";
			ApsSystemUtils.logThrowable(t, this, "doEndTag", msg);
			throw new JspException(msg, t);
		}
		return result;
	}
	
	protected String buildShowletOutput(IPage page, ResponseWrapper responseWrapper) throws JspException {
		String output = null;
		ServletRequest req =  this.pageContext.getRequest();
		RequestContext reqCtx = (RequestContext) req.getAttribute(RequestContext.REQCTX);
		try {
			// Fix per la vulnerabilita CSRF o XSRF (Cross-site request forgery)
			TokenInterceptor.saveSessionToken(this.pageContext);
			
			Showlet showlet = (Showlet) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_SHOWLET);
			this.includeShowlet(reqCtx, responseWrapper, showlet);
			if (null != responseWrapper.getCookieToAdd()) {
				reqCtx.getResponse().addCookie(responseWrapper.getCookieToAdd());
			}
			output = responseWrapper.toString();
			responseWrapper.getWriter().close();
		} catch (Throwable t) {
			String msg = "Errore in preelaborazione showlets";
			throw new JspException(msg, t);
		}
		return output;
	}
	
	protected void includeShowlet(RequestContext reqCtx, ResponseWrapper responseWrapper, Showlet showlet) throws ServletException, IOException {
		HttpServletRequest request = reqCtx.getRequest();
		ApsProperties config = showlet.getConfig();
		if (showlet.getType().isLogic()) {
			config = showlet.getType().getConfig();
		}
		String actionPath = config.getProperty("actionPath");
		String requestActionPath = reqCtx.getRequest().getParameter("actionPath");
		String currentFrameActionPath = reqCtx.getRequest().getParameter("currentFrame");
		Integer currentFrame = (Integer) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_FRAME);
		if (requestActionPath != null && currentFrameActionPath != null && currentFrame.toString().equals(currentFrameActionPath)) {
			if (this.isAllowedRequestPath(requestActionPath)
					&& !this.isRecursivePath(requestActionPath, request)) {
				actionPath = requestActionPath;
			}
		}
		RequestDispatcher requestDispatcher = reqCtx.getRequest().getRequestDispatcher(actionPath);
		requestDispatcher.include(reqCtx.getRequest(), responseWrapper);
	}
	
	protected boolean isAllowedRequestPath(String requestActionPath) {
		String rapLowerCase = requestActionPath.toLowerCase();
		if (rapLowerCase.contains("web-inf") 
				|| rapLowerCase.contains("meta-inf") 
				|| rapLowerCase.contains("../") 
				|| rapLowerCase.contains("%2e%2e%2f") 
				|| rapLowerCase.endsWith(".txt") 
				|| rapLowerCase.contains("<") 
				|| rapLowerCase.endsWith("%3c") 
				|| rapLowerCase.endsWith("%00") 
				|| rapLowerCase.endsWith("'") 
				|| rapLowerCase.endsWith("\"")) {
			return false;
		}
		return true;
	}
	
	protected boolean isRecursivePath(String requestActionPath, HttpServletRequest request) {
		String contextPath = request.getContextPath();
		if (!requestActionPath.contains(contextPath)) {
			return false;
		}
		String prefix = contextPath + "/pages/";
		return (requestActionPath.contains(".wp") || requestActionPath.contains(".page") || requestActionPath.contains(prefix));
	}
	
}