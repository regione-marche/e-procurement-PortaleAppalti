package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.shibboleth;

import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.AccountSSO;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.TrackerSessioniUtenti;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.url.IURLManager;
import com.agiletec.aps.system.services.url.PageURL;
import com.agiletec.apsadmin.system.BaseAction;

public class ShibbolethLoginResponseAction extends BaseAction implements ServletResponseAware{
	/**
	 * UID
	 */
	private static final long serialVersionUID = 976187490508141028L;
	
	private IAppParamManager appParamManager;
	private IURLManager urlManager;
	private IPageManager pageManager;
	
	private HttpServletResponse response;
	
	private boolean formLoginVisible;
	
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

	/**
	 * login sso 
	 */
	public String login() {
		String target = SUCCESS;
		
		Integer configurazioneSSOProtocollo = (Integer)appParamManager.getConfigurationValue(PortGareSystemConstants.TIPO_PROTOCOLLO_SSO);
		if(PortGareSystemConstants.SSO_PROTOCOLLO_SHIBBOLETH == configurazioneSSOProtocollo.intValue()) {
			
			String attributoNome = (String) this.appParamManager.getConfigurationValue("sso.mapping.nome");
			String attributoCognome = (String) this.appParamManager.getConfigurationValue("sso.mapping.cognome");
			String attributoAzienda = (String) this.appParamManager.getConfigurationValue("sso.mapping.azienda");
			String attributoLoginCodiceFiscale = (String) this.appParamManager.getConfigurationValue("sso.mapping.login");
			String attributoPartitaIVA = (String) this.appParamManager.getConfigurationValue("sso.mapping.partitaIVA");
			String attributoEmail = (String) this.appParamManager.getConfigurationValue("sso.mapping.email");
			
			String nome    = this.getHeaderValue(attributoNome);
			String cognome = this.getHeaderValue(attributoCognome);
			String azienda = this.getHeaderValue(attributoAzienda);
			String codiceFiscale = this.getHeaderValue(attributoLoginCodiceFiscale);
			String partitaIVA = this.getHeaderValue(attributoPartitaIVA);
			String email   = this.getHeaderValue(attributoEmail);
			
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
			
			if (((isPersonaFisica && nome != null && cognome != null) || (!isPersonaFisica && azienda != null))
				&& (login != null && login.length() >= 11)) 
			{	
				this.getRequest().getSession().removeAttribute("errMsg");
				
				AccountSSO accountShibboleth = new AccountSSO();
				accountShibboleth.setNome(isPersonaFisica ? nome : azienda);
				accountShibboleth.setCognome(isPersonaFisica ? cognome : null);
				accountShibboleth.setLogin(login);
				accountShibboleth.setEmail(email);
		
				this.getRequest().setAttribute(CommonSystemConstants.SESSION_OBJECT_ACCOUNT_SSO, accountShibboleth);

				// 31/03/2017: si aggiunge l'utente autenticatosi nella lista contenuta nell'applicativo
				String ipAddress = this.getRequest().getHeader("X-FORWARDED-FOR");
				if (ipAddress == null) {
					ipAddress = this.getRequest().getRemoteAddr();
				}
				accountShibboleth.setIpAddress(ipAddress);
				TrackerSessioniUtenti.getInstance(this.getRequest().getSession().getServletContext()).putSessioneUtente(this.getRequest().getSession(), ipAddress, login, DateFormatUtils.format(new Date(), "dd/MM/yyyy HH:mm:ss"));

			} else {
				this.addActionError(this.getText(
						"Errors.sso.insufficientData",
						new String[] { "Shibboleth", nome, cognome, azienda, codiceFiscale, partitaIVA, email }));
				this.getRequest().getSession().setAttribute("ACTION_OBJECT", this);
				target = INPUT;
			}
		} else {
			this.addActionError(this.getText("Errors.sso.IncoerentConfiguration"));
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
	 *  
	 */
	private String getHeaderValue(String attributo) {
		String value = null;
		if(StringUtils.isNotEmpty(attributo)) {
			value = StringUtils.stripToNull(this.getRequest().getHeader(attributo));
		} else {
			// traccia warning o errore ???
			// "parametro " + attributo + " vuoto o nullo" ???
		}
		return value;
	}
	
}
