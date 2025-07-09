package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.adminaccess;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.authorization.IAuthorizationManager;
import com.agiletec.aps.system.services.i18n.II18nManager;
import com.agiletec.aps.system.services.lang.ILangManager;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.url.IURLManager;
import com.agiletec.aps.system.services.url.PageURL;
import com.agiletec.apsadmin.system.BaseAction;
import it.maggioli.eldasoft.mtoken.Mtoken;
import it.maggioli.eldasoft.plugins.ppcommon.aps.SpringAppContext;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event.Level;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.ValidationNotRequired;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.portoken.beans.TokenContent;
import it.maggioli.eldasoft.portoken.beans.UserInfoResponse;
import it.maggioli.eldasoft.portoken.client.PortokenClient;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class AdminAccessAction extends BaseAction implements ServletResponseAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -590160936911227418L;
//	private Map<String, Object> session;
//	private ServletContext servletContext;
	private transient HttpServletResponse response;
	private transient IPageManager pageManager;
	private transient IEventManager eventManager;
	private transient IURLManager urlManager;
	private transient IAppParamManager _appParamManager;
	@Validate(EParamValidation.DOMAIN_USER)
	private String domainMail;
	@Validate(EParamValidation.PASSWORD)
	private String domainPwd;
	@Validate(EParamValidation.MOTIVAZIONE)
	private String motivazione;	
	@ValidationNotRequired
	private String urlRedirect;	
	// Mtoken
	private File certificato;
	@Validate(EParamValidation.CONTENT_TYPE)
	private String certificatoContentType;
	@Validate(EParamValidation.FILE_NAME)
	private String certificatoFileName;
	private String certificatoText;
	//
	private Integer spidValidatorVisible; 
	
	/**
	 * In ambiente cluster (Redis) le istanze dei manager non possono essere serializzate/deserializzate 
	 */
	private void readObject(java.io.ObjectInputStream stream) throws ClassNotFoundException, IOException {
		stream.defaultReadObject();
		try {
			ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
			pageManager = (IPageManager) ctx.getBean(SystemConstants.PAGE_MANAGER);
			urlManager = (IURLManager) ctx.getBean(SystemConstants.URL_MANAGER);
			eventManager = (IEventManager) ctx.getBean(PortGareSystemConstants.EVENTI_MANAGER);
			_appParamManager = (IAppParamManager) ctx.getBean(CommonSystemConstants.APP_PARAM_MANAGER);
			//response = ???;
		} catch (Exception e) {
		}
	}
	
	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	public IPageManager getPageManager() {
		return pageManager;
	}

	public IEventManager getEventManager() {
		return eventManager;
	}

	public IURLManager getUrlManager() {
		return urlManager;
	}

	public void setUrlManager(IURLManager urlManager) {
		this.urlManager = urlManager;
	}

	public void setPageManager(IPageManager pageManager) {
		this.pageManager = pageManager;
	}

	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}
	
	public void setAppParamManager(IAppParamManager appParamManager) {
		this._appParamManager = appParamManager;
	}
	
	public String getDomainMail() {
		return domainMail;
	}

	public void setDomainMail(String domainMail) {
		this.domainMail = domainMail;
	}

	public String getDomainPwd() {
		return domainPwd;
	}

	public void setDomainPwd(String domainPwd) {
		this.domainPwd = domainPwd;
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
	
	public File getCertificato() {
		return certificato;
	}

	public void setCertificato(File certificato) {
		this.certificato = certificato;
	}

	public String getCertificatoContentType() {
		return certificatoContentType;
	}

	public void setCertificatoContentType(String certificatoContentType) {
		this.certificatoContentType = certificatoContentType;
	}

	public String getCertificatoFileName() {
		return certificatoFileName;
	}

	public void setCertificatoFileName(String certificatoFileName) {
		this.certificatoFileName = certificatoFileName;
	}

	public String getCertificatoText() {
		return certificatoText;
	}

	public void setCertificatoText(String certificatoText) {
		this.certificatoText = certificatoText;
	}
	
	public Integer getSpidValidatorVisible() {
		return spidValidatorVisible;
	}

	public void setSpidValidatorVisible(Integer spidValidatorVisible) {
		this.spidValidatorVisible = spidValidatorVisible;
	}

	/**
	 * ... 
	 */
	public String view() {
		String target = SUCCESS;
		
		this.spidValidatorVisible = (Integer)this._appParamManager.getConfigurationValue("sso.spid.validator");
		if(this.spidValidatorVisible == null) {
			this.spidValidatorVisible = new Integer(0);
		}

		return target;
	}

	/**
	 * login Portoken 
	 */
	@SuppressWarnings("unchecked")
	public String loginPortoken(){
		String target = SUCCESS;
		Event evento = new Event();
		Map<String,String> adminAttributes = (Map<String,String>)this.getRequest().getSession().getAttribute(SystemConstants.SESSION_ADMIN_ACCESS_USER);
		evento.setLevel(Level.INFO);
		evento.setEventType(PortGareEventsConstants.LOGIN_SSO);
		evento.setUsername(adminAttributes.get("username"));
		evento.setIpAddress(adminAttributes.get("ip"));
		evento.setSessionId(this.getRequest().getSession().getId());
		String domainMail = getDomainMail();
		String domainPwd = getDomainPwd();
		String motivazione = getMotivazione();
		PortokenClient client = new PortokenClient();
//		private static final String PORT_TOKEN_URL = "https://portoken.maggiolicloud.it/api/jsonws/mggl.token/generate-token/id-utente/-/tipologia-id/-/cod-sap-cliente/-/cod-sap-area/-/cod-sap-famiglia/-/cliente/-/ticket-id/-/stato/-/scadenza/-/aghoritm/-/domini/%22%22/software/portaleappalti/email/-";
		String url =  (String)this._appParamManager.getConfigurationValue("sso.portoken.url");
		UserInfoResponse response = client.getPortokenCredentials(url, domainMail, domainPwd);
		if(response.isEsito()){
			TokenContent tokenContent = response.getTokenContent();
			String utente = StringUtils.stripToNull(tokenContent.getUtente());
			String email = StringUtils.stripToNull(tokenContent.getEmail());
			this.getRequest().getSession().setAttribute("username", adminAttributes.get("username"));
			this.getRequest().getSession().setAttribute("password", adminAttributes.get("password"));
			evento.setMessage("Login mediante Portoken da parte dell'amministratore impersonato da " + utente + " (" + email + ") con motivazione di accesso o ticket: " + motivazione);
			this.getRequest().getSession().setAttribute(SystemConstants.ADMIN_LOGGED, true);
			this.urlRedirect = getPageURL("ppcommon_area_personale");
		} else {
			String codiceMessaggio = "Errors.sso."+response.getError();
			this.addActionError(this.getText(codiceMessaggio, new String[0]));
			evento.setMessage("Tentativo di login dell'amministratore non validato. (" + domainMail + ").");
			evento.setLevel(Level.ERROR);
			evento.setDetailMessage(this.getTextFromDefaultLocale(codiceMessaggio));
			ApsSystemUtils.getLogger().error(this.getTextFromDefaultLocale(codiceMessaggio));
			
			this.getRequest().getSession()
					.setAttribute("ACTION_OBJECT", this);
			this.urlRedirect = getPageURL("portalerror");
		}
		eventManager.insertEvent(evento);

		return target;
	}

	/**
	 * login Mtoken
	 */
	@SuppressWarnings("unchecked")
	public String loginMtoken() {
		String target = SUCCESS;
		
		Map<String,String> adminAttributes = (Map<String, String>)this.getRequest().getSession()
			.getAttribute(SystemConstants.SESSION_ADMIN_ACCESS_USER);
		String domainMail = getDomainMail();
		String domainPwd = getDomainPwd();
		String motivazione = getMotivazione();
		
		Event evento = new Event();
		evento.setLevel(Level.INFO);
		evento.setEventType(PortGareEventsConstants.LOGIN_SSO);
		evento.setUsername(adminAttributes.get("username"));
		evento.setIpAddress(adminAttributes.get("ip"));
		evento.setSessionId(this.getRequest().getSession().getId());
	
		try {
			// carica il certificato "ca-admin.maggioli.it.crt" da ...\WEB-INF\
			// [...\PortaleAppalti\WEB-INF\...]
			InputStream caFile =  this
					.getRequest()
					.getSession()
					.getServletContext()
					.getResourceAsStream(
							CommonSystemConstants.WEBINF_FOLDER
									+ "ca-admin.maggioli.it.crt");
		
			Mtoken client = new Mtoken(caFile);

			if(StringUtils.isNotEmpty(this.certificatoText)) {
				client.getMtokenCredentials(this.certificatoText);
			} else {
				client.getMtokenCredentials(this.certificato);
			}
			
			if(client.isEsito()) {
				String utente = StringUtils.stripToNull(client.getUtente());
				String email = StringUtils.stripToNull(client.getEmail());
				this.getRequest().getSession().setAttribute("username", adminAttributes.get("username"));
				this.getRequest().getSession().setAttribute("password", adminAttributes.get("password"));
				
				evento.setMessage(
						"Login mediante Mtoken da parte dell'amministratore impersonato da " + utente + " (" + email + ") " +
						"con motivazione di accesso o ticket: " + motivazione + " " +
						"con SHA1 del certificato " + client.getSha1() + "");
				
				this.getRequest().getSession().setAttribute(SystemConstants.ADMIN_LOGGED, true);
				this.urlRedirect = getPageURL("ppcommon_area_personale");
			} else {
				String codiceMessaggio = "Errors.sso." + client.getError();
					
				evento.setMessage("Tentativo di login dell'amministratore non validato. (" + domainMail + ").");
				evento.setLevel(Level.ERROR);
				evento.setDetailMessage(this.getTextFromDefaultLocale(codiceMessaggio));				

				this.addActionError(this.getText(codiceMessaggio, new String[0]));
				ApsSystemUtils.getLogger().error(this.getTextFromDefaultLocale(codiceMessaggio));
				
				this.getRequest().getSession().setAttribute("ACTION_OBJECT", this);
				this.urlRedirect = getPageURL("portalerror");
			}
		} catch (Throwable ex) {
			ApsSystemUtils.logThrowable(ex, this, "uploadCertificate");
			this.addActionError(this.getText("Errors.requestNotSent"));
			evento.setError(ex);
			target = INPUT;
		}
		
		this.eventManager.insertEvent(evento);

		return target;
	}
	
	/**
	 * ... 
	 */
	public String uploadCertificate() {
		String target = SUCCESS;
		
		// in fase di invio della richiesta si traccia anche l'evento
//		Event evento = new Event();
//		evento.setLevel(Level.INFO);
//		evento.setEventType(PortGareEventsConstants.LOGIN_SSO);
//		evento.setUsername(adminAttributes.get("username"));
//		evento.setIpAddress(adminAttributes.get("ip"));
//		evento.setSessionId(this.getRequest().getSession().getId());
//		evento.setMessage("Inserimento richiesta di assistenza da parte di "
//				+ this.ente
//				+ " con referente "
//				+ this.referente
//				+ " (mail " + this.email + ")");
//		evento.setDetailMessage(this.getTextFromDefaultLocale("Errors.assistenzaNotConfigured"));
		
		
//		// Verifico la validita' del file allegato
//		if (continua && this.getAllegato() != null) {
//			continua = this.checkFileSize(this.getAllegato(), this.getAllegatoFileName(), 0, this.appParamManager, evento);
//			continua = continua && this.checkFileName(this.getAllegatoFileName(), evento);
//			if (!continua) {
//				this.setTarget(INPUT);
//			}
//		}
		
		
		
//		this.eventManager.insertEvent(evento);
		
		return target;
	}

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
	private String getPageURL(String page) {
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

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}
	
}
