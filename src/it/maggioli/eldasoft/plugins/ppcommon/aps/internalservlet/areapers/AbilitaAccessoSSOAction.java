package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.areapers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletResponseAware;

import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.AccountSSO;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.spid.SpidLoginResponseAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.spid.IAuthServiceSPIDManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.spid.WSAuthServiceSPIDWrapper;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.url.IURLManager;
import com.agiletec.aps.system.services.url.PageURL;
import com.agiletec.aps.system.services.user.IUserManager;
import com.agiletec.apsadmin.system.BaseAction;

/**
 * Azione che gestisce il join tra utenza standard e utenza SPID, CIE, etc.
 * 
 * @author ...
 */
public class AbilitaAccessoSSOAction extends BaseAction implements ServletResponseAware {
	//extends SpidLoginResponseAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 3196774442013131872L;

	private IAppParamManager appParamManager;
	private IPageManager pageManager;
	private IURLManager urlManager;
	private IAuthServiceSPIDManager authServiceSPIDManager;
	private WSAuthServiceSPIDWrapper wsAuthServiceSPID;
	private IEventManager eventManager;
	private IUserManager userManager;
	
//	private Map<String, Object> session;
	private HttpServletResponse response;
	
	private String urlRedirect;
	private String urlLogin;
	private String idp;
	
//	@Override
//	public void setSession(Map<String, Object> arg0) {
//		this.session = arg0;
//	}
		
	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		this.response = arg0;		
	}	
	
	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	public void setPageManager(IPageManager pageManager) {
		this.pageManager = pageManager;
	}
	
	public void setUrlManager(IURLManager urlManager) {
		this.urlManager = urlManager;
	}

	public void setAuthServiceSPIDManager(IAuthServiceSPIDManager authServiceSPIDManager) {
		this.authServiceSPIDManager = authServiceSPIDManager;
	}

	public void setWsAuthServiceSPID(WSAuthServiceSPIDWrapper wsAuthServiceSPID) {
		this.wsAuthServiceSPID = wsAuthServiceSPID;
	}

	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}

	public void setUserManager(IUserManager userManager) {
		this.userManager = userManager;
	}

	public String getUrlRedirect() {
		return urlRedirect;
	}

	public void setUrlRedirect(String urlRedirect) {
		this.urlRedirect = urlRedirect;
	}

	public String getUrlLogin() {
		return urlLogin;
	}

	public void setUrlLogin(String urlLogin) {
		this.urlLogin = urlLogin;
	}

	public String getIdp() {
		return idp;
	}

	public void setIdp(String idp) {
		this.idp = idp;
	}

	/**
	 * ... 
	 */
	public String open() {
		String target = SUCCESS;

//		if (null != this.getCurrentUser()
//			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
//		{ 
//			
//		} else {
//			this.addActionError(this.getText("Errors.sessionExpired"));
//			target = CommonSystemConstants.PORTAL_ERROR;
//		}
		
		return target;
	}
	
	/**
	 * prepara il login al servizio di autenticazione SPID 
	 */
	public String prepareLoginSpid() {
		String target = SUCCESS;
		
		this.urlRedirect = SpidLoginResponseAction.prepareCallbackLogin(				
				"/do/FrontEnd/AreaPers/spidLoginResponse.action",
				this.idp,
				this.appParamManager,
				this.authServiceSPIDManager,
				this.wsAuthServiceSPID);
		if(this.urlRedirect == null) {
			target = INPUT;
		}
		
		return target;
	}
	
	/**
	 * gestione della risposta di login di SPID
	 */
	public String loginSpid() {		
		String target = SUCCESS;

		try {
			AccountSSO accountSpid = SpidLoginResponseAction.validateLogin(
					this.appParamManager,
					this.authServiceSPIDManager,
					this.wsAuthServiceSPID);
			
			if(accountSpid == null) {
				target = INPUT;
			} else {
				// dati validi per associare l'utente a SPID !!!
				
				//this.getRequest().getSession().removeAttribute("errMsg");
				
				// aggiorna il delegate user dell'utente loggato 
				// con il CF restituito da SPID		
				this.userManager.setDelegateUser(this.getCurrentUser().getUsername(), accountSpid.getLogin());
				this.userManager.updateLastAccess(this.getCurrentUser());
				
		//			message = "Accesso del soggetto " + this.getCurrentUser().getUsername() + " " +
		//					  "abilitato con le credenziali SPID del soggetto " + accountSSO.getLogin() + ", " + 
		//					  accountSSO.getDenominazione();
		//			if(accountSSO.getEmail() != null) {
		//				message += " con email " + accountSSO.getEmail();
		//			}
		//			evento.setMessage(message); 
		//			evento.setUsername(user.getUsername());
				
		//			//session.put("username", user.getUsername());
		//			//session.put("password", Authenticator.PASSE_PARTOUT);
				
				this.urlRedirect = this.getPageURL("ppcommon_area_personale");
			}
		} catch (ApsSystemException e) {
			//this.addActionError(this.getText("Errors.sso.readingData", new String[] { "Maggioli SPID" }));
			//this.getRequest().getSession().setAttribute("ACTION_OBJECT", this);
			this.addActionError(this.getText("Errors.sessionExpired"));
			ApsSystemUtils.logThrowable(null, this, "loginConSpid");
			target = INPUT;
		}
		
		return target;
	}
	
	
	/**
	 * prepara il login al servizio di autenticazione CIE (Carta d'Identita' Elettronica)
	 */
	public String prepareLoginCIE() {
		String target = SUCCESS;
		
		return target;
	}

	/**
	 * gestione della risposta di login di CIE (Carta d'Identità Elettronica)
	 */
	public String loginCIE() {
		String target = SUCCESS;		
		//this.urlRedirect = getPageURL("ppcommon_area_personale");
		return target;
	}
	
	/**
	 * ... 
	 */
	private String getUrlErrori() {
		HttpServletRequest request = this.getRequest();
		RequestContext reqCtx = new RequestContext();
		reqCtx.setRequest(request);

		reqCtx.setResponse(response);
		Lang currentLang = this.getLangManager().getDefaultLang();
		reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG, currentLang);
		IPage pageDest = pageManager.getPage("portalerror");
		reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE, pageDest);
		request.setAttribute(RequestContext.REQCTX, reqCtx);

		PageURL url = this.urlManager.createURL(reqCtx);

		url.addParam(RequestContext.PAR_REDIRECT_FLAG, "1");
		return url.getURL();
	}
	
	/**
	 * Genera la url per il redirezionamento ad una specifica pagina.
	 * @param page codice pagina
	 * @return url per la redirect alla pagina
	 */
	private String getPageURL(String page) {
		RequestContext reqCtx = new RequestContext();
		Lang currentLang = this.getLangManager().getDefaultLang();
		IPage pageDest = this.pageManager.getPage(page);
		reqCtx.setRequest(this.getRequest());
		reqCtx.setResponse(this.response);
		reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG, currentLang);
		reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE, pageDest);
		PageURL url = this.urlManager.createURL(reqCtx);
		return url.getURL();
	}
	
}
