package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.spid;

import it.cedaf.authservice.service.AuthData;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.AccountSSO;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.TrackerSessioniUtenti;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event.Level;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.spid.IAuthServiceSPIDManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.spid.WSAuthServiceSPIDWrapper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import java.util.Date;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.url.IURLManager;
import com.agiletec.aps.system.services.url.PageURL;
import com.agiletec.apsadmin.system.BaseAction;
import com.opensymphony.xwork2.ActionContext;

/**
 * ... 
 */
public class SpidLoginResponseAction extends BaseAction implements ServletResponseAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -508800390968455151L;

	private static final String SESSION_ID_SPID_AUTHID = "ACTION_SPID_AUTHID";

	private static final String SPID_AUTHSYSTEM_DEFAULT = "spid";
	private static final String SPID_AUTHLEVEL_URL = "https://www.spid.gov.it/Spid";
	// private static final String SPID_AUTHLEVEL_1 = "L1";
	// private static final String SPID_AUTHLEVEL_2 = "L2";
	// private static final String SPID_AUTHLEVEL_3 = "L3";
	// IDP standard da inserire nel parametro del Portale "spid.idp"
	// private static final String SPID_IDP_ARUBA =
	// "https://loginspid.aruba.it";
	// private static final String SPID_IDP_INTESA =
	// "https://spid.intesa.it/metadata/metadata.xml";
	// private static final String SPID_IDP_INFOCERT =
	// "https://identity.infocert.it/metadata/metadata.xml";
	// private static final String SPID_IDP_LEPIDA =
	// "https://id.lepida.it/idp/shibboleth";
	// private static final String SPID_IDP_NAMIRIAL =
	// "https://idp.namirialtsp.com/idp/metadata";
	// private static final String SPID_IDP_POSTEIT =
	// "http://posteid.poste.it/jod-fs/metadata/metadata.xml";
	// private static final String SPID_IDP_REGISTERIT =
	// "https://spid.register.it/login/metadata";
	// private static final String SPID_IDP_SIELTE =
	// "https://identity.sieltecloud.it/simplesaml/metadata.xml";
	// private static final String SPID_IDP_TITRUST =
	// "https://login.id.tim.it/spid-services/MetadataBrowser/idp";

	private IAppParamManager appParamManager;
	private IURLManager urlManager;
	private IPageManager pageManager;
	private IAuthServiceSPIDManager authServiceSPIDManager;
	private WSAuthServiceSPIDWrapper wsAuthServiceSPID;
	private IEventManager eventManager;
	
	private HttpServletResponse response;
	
	private String urlRedirect;
	private String urlLogin;
	private String idp;
	private String motivazione;

	
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
	 * inizializzazione dell'operazione di login al servizio SPID di Maggioli
	 */
	public String prepareLogin() {
		String target = SUCCESS;
		this.urlLogin = prepareCallbackLogin(
				"/do/SSO/SpidLoginResponse.action",
				this.idp,
				this.appParamManager,
				this.authServiceSPIDManager,
				this.wsAuthServiceSPID);
		if(this.urlLogin == null) {
			target = INPUT;
		}
		return target;
	}

	/**
	 * ... 
	 */
	public String prepareAdminLogin() {
		// controllo motivazione, se è presente la inserisco in sessione per tracciarla in seguito nell'evento di login,
		// in caso contrario scateno un errore
		if(motivazione == null || "".equals(motivazione)){
			this.addActionError(this.getText("Errors.sso.motivazioneMandatory",	new String[0]));
			this.getRequest().getSession().setAttribute("ACTION_OBJECT", this);
			return INPUT;
		} else {
			@SuppressWarnings("unchecked")
			Map<String,String> adminAttributes = (Map<String,String>)this.getRequest().getSession().getAttribute(SystemConstants.SESSION_ADMIN_ACCESS_USER);
			adminAttributes.put("motivazione", motivazione);
			String target = SUCCESS;
			this.urlLogin = prepareCallbackLogin(
					"/do/SSO/SpidLoginAdminResponse.action",
					this.idp,
					this.appParamManager,
					this.authServiceSPIDManager,
					this.wsAuthServiceSPID);
			if(this.urlLogin == null) {
				target = INPUT;
			}
			return target;
		}
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
					.getAttribute(SESSION_ID_SPID_AUTHID);

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
	public static String prepareCallbackLogin(
			String callbackAction,
			String idProvider,
			IAppParamManager appParamManager,
			IAuthServiceSPIDManager authServiceSPIDManager,
			WSAuthServiceSPIDWrapper wsAuthServiceSPID) 
	{
		String target = SUCCESS;
		String urlLogin = null; 

		BaseAction action = (BaseAction)ActionContext.getContext().getActionInvocation().getAction();
		try {
			String url = (String) appParamManager
					.getConfigurationValue(AppParamManager.SPID_WS_AUTHSERVICESPID_URL);
			String authSystem = SPID_AUTHSYSTEM_DEFAULT;
			String serviceProvider = (String) appParamManager
					.getConfigurationValue(AppParamManager.SPID_SERVICEPROVIDER);
			Integer serviceIndex = (Integer) appParamManager
			.getConfigurationValue(AppParamManager.SPID_SERVICEINDEX);
			String authLevel = (String) appParamManager
					.getConfigurationValue(AppParamManager.SPID_AUTHLEVEL);
			//String idProvider = this.idp;

			// valida i parametri...
			if (StringUtils.isEmpty(url)) {
				action.addActionError(action
						.getText("Errors.sso.parameterConfiguration",
								 new String[] { AppParamManager.SPID_WS_AUTHSERVICESPID_URL }));
				ApsSystemUtils.getLogger()
						.error("prepareLogin(): "
								+ action.getTextFromDefaultLocale(
										"Errors.sso.parameterConfiguration",
										AppParamManager.SPID_WS_AUTHSERVICESPID_URL));
				action.getRequest().getSession().setAttribute("ACTION_OBJECT", action);
				target = INPUT;
			}
			if (StringUtils.isEmpty(serviceProvider)) {
				action.addActionError(action
						.getText("Errors.sso.parameterConfiguration",
								 new String[] { AppParamManager.SPID_SERVICEPROVIDER }));
				ApsSystemUtils.getLogger()
						.error("prepareLogin(): "
								+ action.getTextFromDefaultLocale(
										"Errors.sso.parameterConfiguration",
										AppParamManager.SPID_SERVICEPROVIDER));
				action.getRequest().getSession().setAttribute("ACTION_OBJECT", action);
				target = INPUT;
			}
			if (StringUtils.isEmpty(authLevel)) {
				action.addActionError(action
						.getText("Errors.sso.parameterConfiguration",
								 new String[] { AppParamManager.SPID_AUTHLEVEL }));
				ApsSystemUtils.getLogger()
						.error("prepareLogin(): "
								+ action.getTextFromDefaultLocale(
										"Errors.sso.parameterConfiguration",
										AppParamManager.SPID_AUTHLEVEL));
				action.getRequest().getSession().setAttribute("ACTION_OBJECT", action);
				target = INPUT;
			}
			if (StringUtils.isEmpty(idProvider)) {
				action.addActionError(action
						.getText("Errors.sso.parameterConfiguration",
								 new String[] { "IDP" }));
				ApsSystemUtils.getLogger()
						.error("prepareLogin(): "
								+ action.getTextFromDefaultLocale(
										"Errors.sso.parameterConfiguration",
										"IDP"));
				action.getRequest().getSession().setAttribute("ACTION_OBJECT", action);
				target = INPUT;
			}

			if (SUCCESS.equals(target)) {
				// imposta dinamicamente l'endpoint del proxy prima di
				// utilizzarne i servizi...
				wsAuthServiceSPID.getProxyWSAuthService().setEndpoint(url);

				/**
				 * backurl url di ritorno dell'SP authSystem spid (default)
				 * authId token ottenuto via soap getAuthId (validità temporale
				 * limitata) serviceProvider alias configurato su
				 * AuthserviceSPID authLevel definisce L1 L2 L3 di SPID
				 * https://www.spid.gov.it/SpidL1 idp entityID ricavato dai
				 * metadata dell’IDP
				 */
				String backUrl = action.getRequest().getScheme() + "://"
						+ action.getRequest().getServerName() + ":"
						+ action.getRequest().getServerPort()
						+ action.getRequest().getContextPath()
						+ callbackAction;

				// richiedi il token temporaneo al sevizio SPID...
				// e salvalo in sessione per il login...
				String authId = authServiceSPIDManager.getAuthId();

				action.getRequest().getSession().setAttribute(SESSION_ID_SPID_AUTHID, authId);

				// invia la richiesta di login al servizio SPID...
				int i = url.indexOf("/services/");
				url = (i > 0 ? url.substring(0, i) : url);

				urlLogin = url + "/auth.jsp" 
						+ "?backUrl=" + backUrl
						+ "&authSystem=" + authSystem 
						+ "&authId=" + authId
						+ "&serviceProvider=" + serviceProvider 
						+ "&serviceIndex=" + serviceIndex
						+ "&authLevel=" + SPID_AUTHLEVEL_URL + authLevel 
						+ "&idp=" + idProvider;
				
				target = SUCCESS;
			}
		} catch (ApsException e) {
			action.addActionError(action.getText("Errors.sso.configuration", new String[] { "Maggioli SPID" }));
			action.getRequest().getSession().setAttribute("ACTION_OBJECT", action);
			ApsSystemUtils.logThrowable(e, null, "prepareLogin");
			target = INPUT;
		}

		return urlLogin;
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
		
		Integer configurazioneSSOProtocollo = (Integer) appParamManager
				.getConfigurationValue(PortGareSystemConstants.TIPO_PROTOCOLLO_SSO);
		
		if (PortGareSystemConstants.SSO_PROTOCOLLO_SPID_MAGGIOLI == configurazioneSSOProtocollo.intValue()) {
			try {
				// recupera dalla sessione il token temporaneo...
				String authId = (String) action.getRequest().getSession()
						.getAttribute(SESSION_ID_SPID_AUTHID);

				// recupera le info dell'utente dal servizio SPID tramite il
				// token...
				AuthData userInfo = authServiceSPIDManager
						.retrieveUserData(authId);
				if (userInfo != null) {
					// String attributoNome = (String)
					// appParamManager.getConfigurationValue("sso.mapping.nome");
					// String attributoCognome = (String)
					// appParamManager.getConfigurationValue("sso.mapping.cognome");
					// String attributoAzienda = (String)
					// appParamManager.getConfigurationValue("sso.mapping.azienda");
					// String attributoLoginCodiceFiscale = (String)
					// appParamManager.getConfigurationValue("sso.mapping.login");
					// String attributoPartitaIVA = (String)
					// appParamManager.getConfigurationValue("sso.mapping.partitaIVA");
					// String attributoEmail = (String)
					// appParamManager.getConfigurationValue("sso.mapping.email");

					// azienda o libero professionista ?
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
		} else {
			action.addActionError(action.getText("Errors.sso.IncoerentConfiguration"));
			action.getRequest().getSession().setAttribute("ACTION_OBJECT", action);
			ApsSystemUtils.logThrowable(null, null, "validateLogin");
			target = INPUT;
		}
		
		if(!SUCCESS.equals(target)) {
			accountSpid = null;
		}
			
		return accountSpid;
	}

	/**
	 * ... 
	 */
	public String getUrlErrori() {
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
		IPage pageDest = pageManager.getPage(page);
		reqCtx.setRequest(this.getRequest());
		reqCtx.setResponse(this.response);
		reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG, currentLang);
		reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE, pageDest);
		PageURL url = this.urlManager.createURL(reqCtx);
		return url.getURL();
	}

}
