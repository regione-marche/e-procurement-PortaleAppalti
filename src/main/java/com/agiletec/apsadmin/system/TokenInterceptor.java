package com.agiletec.apsadmin.system;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.util.TokenHelper;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.opensymphony.xwork2.ActionChainResult;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.util.LocalizedTextUtil;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;


public class TokenInterceptor extends org.apache.struts2.interceptor.TokenInterceptor {		
	/**
	 * UID
	 */
	private static final long serialVersionUID = -7482540881852093031L;
	
	private static final Logger LOG = LoggerFactory.getLogger(TokenInterceptor.class);	
	private static final String STRUSTOKENNAME 	= "strutsTokenName"; 
	private static final String REDIRECT_TOKEN 	= "redirectToken"; 
	private static final String TOKENHREFPARAMS	= "tokenHrefParams";
	private static final int MAX_SESSION_TOKEN_LIST_SIZE = 20;
	
	
	/**
	 * doIntercept()
	 */
	@Override
	protected String doIntercept(ActionInvocation invocation) throws Exception {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Intercepting invocation to check for valid transaction token.", new String[0]);
		}

		HttpServletRequest req = ServletActionContext.getRequest();
		HttpSession session = req.getSession(true);	
		//ActionContext actCtx = ActionContext.getContext();

		synchronized (session) {
			if (!this.validToken()) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("TokenInterceptor.doIntercept(...)-> KO " + invocation.getAction().getClass().getName(), new String[0]);
				}
				return handleInvalidToken(invocation);
			}
			
			if (LOG.isDebugEnabled()) {
				LOG.debug("TokenInterceptor.doIntercept(...)-> OK " + invocation.getAction().getClass().getName(), new String[0]);
			}
			return handleValidToken(invocation);
		}
	}

	/**
	 * restituisce un id per il parametro "struts.token.name" 
	 */
	public static String getStrutsTokenName() {
		return "_csrf";  	// {CSRFToken, _csrf, ...}
 	}
	
	/**
	 * valida il token della request con il token di sessione 
	 */
	private boolean validToken() {
		String tokenName = getStrutsTokenName();
		String token = TokenHelper.getToken(tokenName);
		
		// NB:
		// in caso di "redirect" il token non e' presente tra i parametri 
		// della request/action
		Map session = ActionContext.getContext().getSession();
		if(StringUtils.isEmpty(token)) {
			token = (String)session.remove(REDIRECT_TOKEN);
		}

		if (token == null) {
			if (LOG.isDebugEnabled())
				LOG.debug("no token found for token name " + tokenName + " -> Invalid token ", new String[0]);
			return false;
		}

		List<String> list = getSessionTokenList(session);
		int sessionTokenIndex = sessionTokenListIndexOf(list, token);
		String sessionToken = (sessionTokenIndex >= 0 ? token : null); 

		// NB: 
		// in caso di action chaining, il token in sessione viene rimosso 
		// dopo la validazione della prima action del chaining...
		// in questo caso e' necessario ripristinare il token in sessione 
		// per consentire al chaining di eseguire correttamente tutte 
		// le action in cascata
		LinkedList<String> chainHistory = ActionChainResult.getChainHistory();
		boolean chainging = (chainHistory != null && chainHistory.size() > 0);

		if(chainging) {
			if(StringUtils.isNotEmpty(token)) {
				if(StringUtils.isEmpty(sessionToken)) {
					// il token in sessione e' gia' stato consumato dalla precedente action...
					// sincronizza il token della action in chain con il nuovo token di sessione...
					sessionToken = token;
					if (log.isDebugEnabled()) {
						log.debug("Action chain detected for token = " + sessionToken, new String[0]);
					}
			   }
			}
		} else {
			// consuma e rimuovi il token dalla sessione
			sessionTokenListRemove(list, sessionTokenIndex);
		}

		// valida il token corrente con il token in sessione...
//		if(StringUtils.isNotEmpty(sessionToken)) {
			if (!token.equals(sessionToken)) {
				LOG.warn(LocalizedTextUtil.findText(
							TokenHelper.class, 
							"struts.internal.invalid.token", 
 							ActionContext.getContext().getLocale(), 
 							"Form token {0} does not match the session token {1}.", 
 							new Object[] { token, sessionToken }), 
						 new String[0]);
				if (LOG.isDebugEnabled()) {
					LOG.debug("token " + token + " is invalid -> Request rejected.", new String[0]);
				}
				return false;
			}
//		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("token " + token + " validated.", new String[0]);
		}
				
		return true;
	}	
	
	/**
	 * inserisce un nuovo token di sessione nella lista dei token di sessione 
	 */
	public static String saveSessionToken(ServletRequest req) {
		RequestContext reqCtx = (RequestContext) req.getAttribute(RequestContext.REQCTX);
		if(reqCtx == null) return null;
		if(reqCtx.getRequest() == null) return null;
		HttpSession session = reqCtx.getRequest().getSession();
		if(session == null) return null;
		
		String token = null;
		synchronized (session) {
			// genera un nuovo token per la pagina/showlet corrente
			String tokenName = getStrutsTokenName();
			token = TokenHelper.generateGUID();
			String tokenHrefParams = getStrutsTokenName() + "=" + token;
//System.out.println("TokenInterceptor.saveSessionToken(...) token=" + token);
			
			List<String> list = getSessionTokenList(session);
			sessionTokenListAdd(list, token);
			
			ApsSystemUtils.getLogger().debug(
					"saveSessionToken() new token=" + token + ", list.size=" + list.size());
	
			// inserisci nella request/attributi di pagina
			// le variabili per la gestione del token da parte della jsp
			req.setAttribute(STRUSTOKENNAME, tokenName); 			// ${strutsTokenName}
			req.setAttribute(tokenName, token);						// ${requestScope[strutsTokenName]}
			req.setAttribute(TOKENHREFPARAMS, tokenHrefParams);		// ${tokenHrefParams}
			
			LOG.debug("saveSessionToken() new token=" + token + ", list.size=" + list.size() + " -> "); // + pageContext.getPage());
		}
		return token;
	}
	
	public static void saveSessionToken(PageContext pageContext) {
		ServletRequest req =  pageContext.getRequest();
		TokenInterceptor.saveSessionToken(req);
	}

	/**
	 * da utilizzare in caso una action effettui il redirect 
	 * (i.e. ...)
	 */
	@SuppressWarnings("unchecked")
	public static void redirectToken() {
		Object obj = ActionContext.getContext().getParameters().get(getStrutsTokenName());
		String token = (obj != null ? ((String[]) obj)[0] : null);
		Map session = ActionContext.getContext().getSession();
		List<String> list = (List<String>) session.get(getStrutsTokenName());
		sessionTokenListAdd(list, token);
		session.put(REDIRECT_TOKEN, token);
	}
	
	/**
	 * da utilizzare in caso si effettui il redirect senza action context 
	 */
	@SuppressWarnings("unchecked")
	public static void redirectNewToken(HttpSession session) {
		String token = TokenHelper.generateGUID();
		List<String> list = (List<String>) session.getAttribute(getStrutsTokenName());
		sessionTokenListAdd(list, token);
		session.setAttribute(REDIRECT_TOKEN, token);
	}

	/** 
	 * ********************************************************************************
	 * Gestione della lista  di token in sessione
	 * ********************************************************************************
	 * TokenInterceptor viene eseguito dopo InternalServletTag
	 * che genera un nuovo token di sessione per la nuova pagina (showlet)
	 * i token di sessione associati ad ogni pagina vengono memorizzati
	 * in una lista. Quindi il token di sessione da utilizzare per la
	 * validazione risulta essere il penultimo token inserito nella lista
	 */
	@SuppressWarnings("unchecked")
	private static List<String> getSessionTokenList(HttpSession session) {
		String tokenName = getStrutsTokenName();
		List<String> list = (List<String>) session.getAttribute(tokenName);
		if(list == null) {
			list = new ArrayList<String>();
			session.setAttribute(tokenName, list);
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	private static List<String> getSessionTokenList(Map session) {
		String tokenName = getStrutsTokenName();
		List<String> list = (List<String>) session.get(tokenName);
		if(list == null) {
			list = new ArrayList<String>();
			session.put(tokenName, list);
		}
		return list;
	}
	
	@SuppressWarnings("unused")
	private static void sessionTokenListAdd(List<String> list, String token) {
		list.add(token);
		sessionTokenListPurge(list);
	}
	
	@SuppressWarnings("unused")
	private static String sessionTokenListGet(List<String> list, int index) {
		return list.get(index);
	}
	
	@SuppressWarnings("unused")
	private static String sessionTokenListRemove(List<String> list, int index) {
		String sessionToken = null;
		if(index >= 0) {
			sessionToken = list.remove(index);
			sessionTokenListPurge(list);
		}
		return sessionToken;
	}
	
	@SuppressWarnings("unused")
	private static int sessionTokenListIndexOf(List<String> list, String token) {
		for(int i = 0; i < list.size(); i++) {
			if(list.get(i).equals(token)) {
				return i;
			}
 		}
		return -1;
	} 
		
	/**
	 * mantieni nella lista gli ultimi N elementi piu' recenti
	 */ 
	private static void sessionTokenListPurge(List<String> list) {
		for(int i = 1; i <= list.size() - MAX_SESSION_TOKEN_LIST_SIZE; i++) {
			list.remove(0);
		}
	}

}
