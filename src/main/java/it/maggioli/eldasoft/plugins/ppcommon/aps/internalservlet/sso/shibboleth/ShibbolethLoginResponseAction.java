package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.shibboleth;

import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.url.IURLManager;
import com.agiletec.aps.system.services.url.PageURL;
import com.agiletec.apsadmin.system.BaseAction;
import com.opensymphony.xwork2.ActionContext;

import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.AccountSSO;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.HeaderParamsSSO;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.TrackerSessioniUtenti;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.struts2.interceptor.ServletResponseAware;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

public class ShibbolethLoginResponseAction extends BaseAction implements ServletResponseAware{
	/**
	 * UID
	 */
	private static final long serialVersionUID = 976187490508141028L;
	
	private static final String SESSION_OBJECT_SHIBBOLETH_HEDEAD_PARMAS = "shibbolethHeaderParams";
	
	private IAppParamManager appParamManager;
	private IURLManager urlManager;
	private IPageManager pageManager;
	
	private HttpServletResponse response;
	
	private boolean formLoginVisible;
	private String token;
	
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
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
	
	public boolean isFormLoginVisible() {
		return formLoginVisible;
	}

	public void setFormLoginVisible(boolean formLoginVisible) {
		this.formLoginVisible = formLoginVisible;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	
	/**
	 * recupera dall'header della risposta di Shibboleth i parametri del login 
	 */	
	private static HeaderParamsSSO getNormalizedShibbolethAccount(IAppParamManager appParamManager) {
		BaseAction action = (BaseAction)ActionContext.getContext().getActionInvocation().getAction();
		
		String attributoNome = (String) appParamManager.getConfigurationValue("auth.sso.shibboleth.mapping.nome");
		String attributoCognome = (String) appParamManager.getConfigurationValue("auth.sso.shibboleth.mapping.cognome");
		String attributoAzienda = (String) appParamManager.getConfigurationValue("auth.sso.shibboleth.mapping.azienda");
		String attributoLoginCodiceFiscale = (String) appParamManager.getConfigurationValue("auth.sso.shibboleth.mapping.login");
		String attributoPartitaIVA = (String) appParamManager.getConfigurationValue("auth.sso.shibboleth.mapping.partitaIVA");
		String attributoEmail = (String) appParamManager.getConfigurationValue("auth.sso.shibboleth.mapping.email");

		// ATTENZIONE! 
		// Shibboleth potrebbe restituire al posto di NULL delle stringhe "(null)" o "null" che vanno normalizzate
		String nome = ShibbolethLoginResponseAction.getHeaderValue(action, attributoNome);
		String cognome = ShibbolethLoginResponseAction.getHeaderValue(action, attributoCognome);
		String azienda = ShibbolethLoginResponseAction.getHeaderValue(action, attributoAzienda);
		String codiceFiscale = ShibbolethLoginResponseAction.getHeaderValue(action, attributoLoginCodiceFiscale);
		String partitaIVA = ShibbolethLoginResponseAction.getHeaderValue(action, attributoPartitaIVA);
		String email = ShibbolethLoginResponseAction.getHeaderValue(action, attributoEmail);

		// ATTENZIONE! 
		// SPID di Poste Italiane, per persone fisiche, passa "-" nei campi shib-companyName e shib-ivaCode per persone fisiche
		// per essere un nome azienda valido deve avere almeno 3 caratteri (scarto quindi "-" di Poste Italiane)
		azienda = (StringUtils.isNotEmpty(azienda) && azienda.length() <= 2 ? "" : azienda);
		
		// completa le info di login restituite dal sistema di autenticazione remoto
		HeaderParamsSSO header = HeaderParamsSSO.getFromSession();
		header = new HeaderParamsSSO(
				nome, 
				cognome, 
				azienda, 
				codiceFiscale, 
				partitaIVA, 
				email, 
				(header != null ? header.getCallback() : null)
		);
		header.putToSession();
		
		return header;
	}
	
	/**
	 * recupera un parametro di configurazione dall'header della request d risposta di shibbolet 
	 */
	private static String getHeaderValue(BaseAction action, String attributo) {
		String value = null;
		if(StringUtils.isNotEmpty(attributo)) {
			value = action.getRequest().getHeader(attributo);
			
			// in caso di valori vuoti/nulli Shibboleth potrebbe rispondere con stringhe "(null)", "null" al posto di null 
			// quindi valore va verificato e normalizzato
			if( "null".equalsIgnoreCase(value) || "(null)".equalsIgnoreCase(value) ) {
				LOG.warn("ShibbolethLoginResponseAction.getHeaderValue({}) = '{}' normalizzato come null", attributo, value);
				value = null;
			}
			
			value = StringUtils.stripToNull(value);
		} else {
			// traccia warning o errore ???
			// "parametro " + attributo + " vuoto o nullo" ???
			//LOG.warn("ShibbolethLoginResponseAction.getHeaderValue('{}') paremeter is empty", attributo);
		}
		return value;
	}

	/**
	 * ... 
	 */
	public static AccountSSO getSSOLogin(IAppParamManager appParamManager) {
		AccountSSO accountShibboleth = null;
		
		HeaderParamsSSO header = HeaderParamsSSO.getFromSession();
		String nome = header.getNome();
		String cognome = header.getCognome();
		String azienda = header.getAzienda();
		String codiceFiscale = header.getCodiceFiscale();
		String partitaIVA = header.getPartitaIVA();
		String email = header.getEmail();

		BaseAction action = (BaseAction)ActionContext.getContext().getActionInvocation().getAction();
		
		// calcolo di alcune variabili in seguito all'autenticazione SPID
		// integrata in Shibboleth e valida anche per soggetti giuridici
		// ATTENZIONE! SPID di Poste Italiane, per persone fisiche, passa "-" nei campi shib-companyName e shib-ivaCode per persone fisiche
		//
		//per essere un nome azienda valido deve avere almeno 3 caratteri (scarto quindi "-" di Poste Italiane)
		boolean isPersonaFisica = azienda != null && azienda.length() > 2 ? false : true;
		
		String login = codiceFiscale;
		if (!isPersonaFisica) {
			login = (partitaIVA != null ? partitaIVA : codiceFiscale);
		}
		
		if (((isPersonaFisica && (nome != null || cognome != null)) || (!isPersonaFisica && azienda != null))
				&& (login != null ))
		{	
			action.getRequest().getSession().removeAttribute("errMsg");
			
			accountShibboleth = new AccountSSO();
			accountShibboleth.setNome(isPersonaFisica ? nome : azienda);
			accountShibboleth.setCognome(isPersonaFisica ? cognome : null);
			accountShibboleth.setLogin(login);
			accountShibboleth.setEmail(email);
			accountShibboleth.setTipologiaLogin(PortGareSystemConstants.TIPOLOGIA_LOGIN_SHIBBOLETH_SSO);
			action.getRequest().setAttribute(CommonSystemConstants.SESSION_OBJECT_ACCOUNT_SSO, accountShibboleth);
			
			// 31/03/2017: si aggiunge l'utente autenticatosi nella lista contenuta nell'applicativo
			String ipAddress = action.getRequest().getHeader("X-FORWARDED-FOR");
			if (ipAddress == null) {
				ipAddress = action.getRequest().getRemoteAddr();
			}
			accountShibboleth.setIpAddress(ipAddress);

		} else {
			action.addActionError(action.getText(
					"Errors.sso.insufficientData",
					new String[] { "Shibboleth", nome, cognome, azienda, codiceFiscale, partitaIVA, email }));
			action.getRequest().getSession().setAttribute("ACTION_OBJECT", action);
		}
		
		return accountShibboleth;
	}
			
			
	/**
	 * login sso 
	 */
	public String login() {
		String target = SUCCESS;
		
		HeaderParamsSSO header = getNormalizedShibbolethAccount(appParamManager);
		String nome = header.getNome();
		String cognome = header.getCognome();
		String azienda = header.getAzienda();
		String codiceFiscale = header.getCodiceFiscale();
		String partitaIVA = header.getPartitaIVA();
		String email = header.getEmail();

		BaseAction action = (BaseAction)ActionContext.getContext().getActionInvocation().getAction();

		AccountSSO accountShibboleth = ShibbolethLoginResponseAction.getSSOLogin(this.appParamManager);

		// se e' presente il parametro "token" aggiungilo agli attributi della request per la successiva action...
		if(StringUtils.isNotEmpty(this.token)) {
			action.getRequest().setAttribute(CommonSystemConstants.SESSION_OBJECT_ACCOUNT_SSO_TOKEN, this.token);
		}
		
		if(accountShibboleth != null) {
			// calcolo di alcune variabili in seguito all'autenticazione SPID
			// integrata in Shibboleth e valida anche per soggetti giuridici
			// ATTENZIONE! SPID di Poste Italiane passa "-" nei campi shib-companyName e shib-ivaCode per persone fisiche
			//
			//per essere un nome azienda valido deve avere almeno 3 caratteri (scarto quindi "-" di Poste Italiane)
			boolean isPersonaFisica = azienda != null && azienda.length() > 2 ? false : true;
			
			String login = codiceFiscale;
			if (!isPersonaFisica) {
				login = (partitaIVA != null ? partitaIVA : codiceFiscale);
			}

			// 31/03/2017: si aggiunge l'utente autenticatosi nella lista contenuta nell'applicativo
			String ipAddress = this.getRequest().getHeader("X-FORWARDED-FOR");
			if (ipAddress == null) {
				ipAddress = this.getRequest().getRemoteAddr();
			}

			TrackerSessioniUtenti.getInstance(this.getRequest().getSession().getServletContext()).putSessioneUtente(
					this.getRequest().getSession(), 
					ipAddress, 
					login, 
					DateFormatUtils.format(new Date(), "dd/MM/yyyy HH:mm:ss"));
		} else {
			// NB: il messaggio di errore viene inserito da getSSOLogin() !!!
			//this.addActionError(this.getText(
			//		"Errors.sso.insufficientData",
			//		new String[] { "Shibboleth", nome, cognome, azienda, codiceFiscale, partitaIVA, email }));
			this.getRequest().getSession().setAttribute("ACTION_OBJECT", this);
			target = INPUT;
		}
		return target;
	}
	
	/**
	 * 
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
	
}
