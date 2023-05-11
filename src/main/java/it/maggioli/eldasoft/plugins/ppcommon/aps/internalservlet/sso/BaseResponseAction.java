package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso;


import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.url.IURLManager;
import com.agiletec.aps.system.services.url.PageURL;
import com.agiletec.apsadmin.system.BaseAction;
import com.opensymphony.xwork2.ActionContext;
import it.cedaf.authservice.service.AuthData;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.TrackerSessioniUtenti;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event.Level;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.spid.IAuthServiceSPIDManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.spid.WSAuthServiceSPIDWrapper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.struts2.interceptor.ServletResponseAware;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

/**
 * ... 
 */
public class BaseResponseAction extends BaseAction implements ServletResponseAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -508800390968455151L;
	protected static final String SESSION_ID_SSO_AUTHID = "ACTION_SSO_AUTHID";
	protected static final String SPID_AUTHLEVEL_URL = "https://www.spid.gov.it/Spid";
	protected static final String SPID_AUTHSYSTEM_DEFAULT = "spid";
	protected static final String CIE_AUTHSYSTEM_DEFAULT = "cieid";
	protected static final String CNS_AUTHSYSTEM_DEFAULT = "ssl";
	protected static final String CRS_AUTHSYSTEM_DEFAULT = "idpc";
	protected static final String MYID_AUTHSYSTEM_DEFAULT = "federa";
	protected static final String GEL_AUTHSYSTEM_DEFAULT = "gel";
	protected static final String FEDERA_AUTHSYSTEM_DEFAULT = "federa";

	protected static final String PREPARE_LOGIN = "prepareLogin(): ";

	protected ConfigInterface configManager;
	protected IAppParamManager appParamManager;
	protected IURLManager urlManager;
	protected IPageManager pageManager;
	protected IAuthServiceSPIDManager authServiceSPIDManager;
	protected WSAuthServiceSPIDWrapper wsAuthServiceSPID;
	protected IEventManager eventManager;
	protected HttpServletResponse response;
	protected String urlRedirect;
	protected String urlLogin;
	protected String idp;
	protected String motivazione;

	
    public void setConfigManager(ConfigInterface configManager) {
      this.configManager = configManager;
    }
    
    public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	public void setUrlManager(IURLManager urlManager) {
		this.urlManager = urlManager;
	}

	public void setPageManager(IPageManager pageManager) {
		this.pageManager = pageManager;
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
	
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
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

	public String getMotivazione() {
		return motivazione;
	}

	public void setMotivazione(String motivazione) {
		this.motivazione = motivazione;
	}

	public String getUrlRedirect() {
		return urlRedirect;
	}
	
	/**
	 * gestione della risposta di login
	 */
	public String login() {
		String target = SUCCESS;

		AccountSSO accountSpid = validateLogin(
				this.appParamManager, 
				this.authServiceSPIDManager, 
				this.wsAuthServiceSPID); 
		
		if(accountSpid == null) {
			target = INPUT;
		} else {
			// dati validi...
			this.getRequest().getSession().removeAttribute("errMsg");

			this.getRequest().setAttribute(
							CommonSystemConstants.SESSION_OBJECT_ACCOUNT_SSO,
							accountSpid);

			// 31/03/2017: si aggiunge l'utente autenticatosi nella
			// lista contenuta nell'applicativo
			String ipAddress = this.getRequest().getHeader("X-FORWARDED-FOR");
			if (ipAddress == null) {
				ipAddress = this.getRequest().getRemoteAddr();
			}
			accountSpid.setIpAddress(ipAddress);
			
			ServletContext ctx = this.getRequest().getSession().getServletContext();
			TrackerSessioniUtenti.getInstance(ctx)
					.putSessioneUtente(this.getRequest().getSession(),
									   ipAddress,
									   accountSpid.getLogin(),
									   DateFormatUtils.format(new Date(), "dd/MM/yyyy HH:mm:ss"));			
		} 
		
		return target;
	}	

	/**
	 * ... 
	 */
	public String adminLogin() {
		String target = SUCCESS;

		try {
			Map<String,String> adminAttributes = (Map<String, String>)this.getRequest().getSession()
					.getAttribute(SystemConstants.SESSION_ADMIN_ACCESS_USER);
			
			// recupera dalla sessione il token temporaneo...
			String authId = (String) this.getRequest().getSession()
					.getAttribute(SESSION_ID_SSO_AUTHID);

			// recupera le info dell'utente dal servizio SPID tramite il token...
			AuthData userInfo = this.authServiceSPIDManager
					.retrieveUserData(authId);
			
			if (userInfo != null) {
				String nome = StringUtils.stripToNull(userInfo.getNome());
				String cognome = StringUtils.stripToNull(userInfo.getCognome());
				
				this.getRequest().getSession().setAttribute("username", adminAttributes.get("username"));
				this.getRequest().getSession().setAttribute("password", adminAttributes.get("password"));
				
				Event evento = new Event();
				evento.setLevel(Level.INFO);
				evento.setEventType(PortGareEventsConstants.LOGIN_SSO);
				evento.setUsername(adminAttributes.get("username"));
				evento.setIpAddress(adminAttributes.get("ip"));
				evento.setSessionId(this.getRequest().getSession().getId());
				evento.setMessage("Login mediante SPID da parte dell'amministratore impersonato da " + nome + " " + cognome  + " con motivazione di accesso o ticket: " + adminAttributes.get("motivazione"));
				eventManager.insertEvent(evento);
				
				this.getRequest().getSession().setAttribute(SystemConstants.ADMIN_LOGGED, true);
				
				this.urlRedirect = getPageURL("ppcommon_area_personale");
				
			} else {
				this.addActionError(this.getText("Errors.sso.readingData", new String[] { "Maggioli SPID" }));
				this.getRequest().getSession().setAttribute("ACTION_OBJECT", this);
				ApsSystemUtils.logThrowable(null, this, "login");
				target = INPUT;
			}
		} catch (ApsException e) {
			this.addActionError(this.getText("Errors.sso.readingData", new String[] { "Maggioli SPID" }));
			this.getRequest().getSession().setAttribute("ACTION_OBJECT", this);
			ApsSystemUtils.logThrowable(null, this, "login");
			target = INPUT;
		}
		return target;
	}

	/**
	 * ... 
	 */
	public static AccountSSO validateLogin(
			IAppParamManager appParamManager,
			IAuthServiceSPIDManager authServiceSPIDManager,
			WSAuthServiceSPIDWrapper wsAuthServiceSPID) 
	{
		AccountSSO accountSpid = null;
		
		String target = SUCCESS;

		BaseAction action = (BaseAction)ActionContext.getContext().getActionInvocation().getAction();
		try {
			// recupera dalla sessione il token temporaneo...
			String authId = (String) action.getRequest().getSession()
					.getAttribute(SESSION_ID_SSO_AUTHID);

			// recupera le info dell'utente dal servizio SPID tramite il
			// token...
			AuthData userInfo = authServiceSPIDManager
					.retrieveUserData(authId);
			if (userInfo != null) {
				String nome = StringUtils.stripToNull(userInfo.getNome());
				String cognome = StringUtils.stripToNull(userInfo.getCognome());
				String azienda = StringUtils.stripToNull(userInfo.getAziendaDenominazione());
				String codiceFiscale = StringUtils.stripToNull(userInfo.getCodiceFiscale());
				String partitaIVA = StringUtils.stripToNull(userInfo.getAziendaPIVA());
				String email = (StringUtils.isNotEmpty(userInfo.getPec()) 
						? StringUtils.stripToNull(userInfo.getPec()) 
						: StringUtils.stripToNull(userInfo.getMailAddress()));

				// per essere un nome azienda valido deve avere almeno 3 caratteri
				boolean isPersonaFisica = (azienda != null && azienda.length() > 2 ? false : true);

				// calcolo di alcune variabili in seguito all'autenticazione SPID
				// e valida anche per soggetti giuridici
				String login = codiceFiscale;
				if (!isPersonaFisica) {
					login = (partitaIVA != null ? partitaIVA : codiceFiscale);
				}

				if (((isPersonaFisica && nome != null && cognome != null) || (!isPersonaFisica && azienda != null))
					&& login != null && login.length() >= 11) {
					
					// AccountSSO ready !!!
					accountSpid = new AccountSSO();
					accountSpid.setNome(isPersonaFisica ? nome : azienda);
					accountSpid.setCognome(isPersonaFisica ? cognome : null);
					accountSpid.setLogin(login);
//						accountSpid.setEmail(email);
					accountSpid.setTipologiaLogin(PortGareSystemConstants.TIPOLOGIA_LOGIN_MAGGIOLI_AUTH_SSO);
			
				} else {
					action.addActionError(action.getText(
							"Errors.sso.insufficientData", new String[] {
									"Maggioli SPID", nome, cognome,
									azienda, codiceFiscale, partitaIVA,
									email }));
					action.getRequest().getSession().setAttribute("ACTION_OBJECT", action);
					target = INPUT;
				}

			} else {
				action.addActionError(action.getText("Errors.sso.readingData", new String[] { "Maggioli SPID" }));
				action.getRequest().getSession().setAttribute("ACTION_OBJECT", action);
				ApsSystemUtils.logThrowable(null, null, "validateLogin");
				target = INPUT;
			}
		} catch (ApsException e) {
			action.addActionError(action.getText("Errors.sso.readingData", new String[] { "Maggioli SPID" }));
			action.getRequest().getSession().setAttribute("ACTION_OBJECT", action);
			ApsSystemUtils.logThrowable(e, null, "validateLogin");
			target = INPUT;
		}
		
		if(!SUCCESS.equals(target)) {
			accountSpid = null;
		}
			
		return accountSpid;
	}

	/**
	 * backurl url di ritorno dell'SP authSystem spid (default)
	 * authId token ottenuto via soap getAuthId (validit� temporale
	 * limitata) serviceProvider alias configurato su
	 * AuthserviceSPID authLevel definisce L1 L2 L3 di SPID
	 * https://www.spid.gov.it/SpidL1 idp entityID ricavato dai
	 * metadata dell�IDP
	 */
    protected static String getBackUrl(String callbackAction, ConfigInterface configManager) {
      String backUrl = configManager.getParam(SystemConstants.PAR_APPL_BASE_URL) + callbackAction;
      return backUrl;
    }	
	
	/**
	 * ... 
	 */
	public String getUrlErrori() {
		HttpServletRequest request = this.getRequest();
		RequestContext reqCtx = new RequestContext();
		reqCtx.setRequest(request);

		reqCtx.setResponse(response);
//		Lang currentLang = this.getLangManager().getDefaultLang();
		reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG, getCurrentLang());
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
	protected String getPageURL(String page) {
		RequestContext reqCtx = new RequestContext();
//		Lang currentLang = this.getLangManager().getDefaultLang();
		IPage pageDest = pageManager.getPage(page);
		reqCtx.setRequest(this.getRequest());
		reqCtx.setResponse(this.response);
		reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG, getCurrentLang());
		reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE, pageDest);
		PageURL url = this.urlManager.createURL(reqCtx);
		return url.getURL();
	}

}
