package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.spid;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.aps.system.services.user.IUserManager;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.apsadmin.system.BaseAction;
import com.opensymphony.xwork2.ActionContext;
import it.cedaf.authservice.service.AuthDataV2;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.AccountSSO;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.BaseResponseAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.TrackerSessioniUtenti;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event.Level;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.spid.IAuthServiceSPIDManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.spid.WSAuthServiceSPIDWrapper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.regimpresa.WizardRegistrazioneImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.datiimpresa.DatiImpresaChecker;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;

import javax.servlet.ServletContext;
import java.util.Date;
import java.util.List;

/**
 * ...  
 */
public class SpidBusinessLoginResponseAction extends BaseResponseAction {
	/**
	 * UID 
	 */
	private static final long serialVersionUID = -508800390968455151L;

	private static final Logger log = ApsSystemUtils.getLogger();

	private static final String ERRORS_PARAMETER_CONFIGURATION = "Errors.sso.parameterConfiguration";

	private IBandiManager bandiManager;
	private IUserManager userManager;
	//	private String urlRedirect;
	protected DatiImpresaChecker datiImpresaChecker;

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	public void setUserManager(IUserManager userManager) {
		this.userManager = userManager;
	}

	public void setDatiImpresaChecker(DatiImpresaChecker datiImpresaChecker) {
		this.datiImpresaChecker = datiImpresaChecker;
	}

	/**
	 * inizializzazione dell'operazione di login al servizio SPID di Maggioli 
	 */
	public String prepareLogin() {
		String target = SUCCESS;
		target = prepareCallbackLogin(
				"do/SSO/SpidBusinessLoginResponse.action",
				this.idp,
				this.configManager,
				this.appParamManager,
				this.authServiceSPIDManager,
				this.wsAuthServiceSPID);
		if(this.urlLogin == null) {
			target = INPUT;
		}
		return target;
	}

	/**
	 * Creazione URL per accedere al servizio SPID di Maggioli, comprendente la backUrl di ritorno sul portale 
	 */
	private String prepareCallbackLogin(
			String callbackAction,
			String idProvider,
			ConfigInterface configManager,
			IAppParamManager appParamManager,
			IAuthServiceSPIDManager authServiceSPIDManager,
			WSAuthServiceSPIDWrapper wsAuthServiceSPID) {
		String target = SUCCESS;

		BaseAction action = (BaseAction)ActionContext.getContext().getActionInvocation().getAction();
		try {
			String url = (String) appParamManager
					.getConfigurationValue(AppParamManager.SPIDBUSINESS_WS_AUTHSERVICESPID_URL);
			String authSystem = SPID_AUTHSYSTEM_DEFAULT;
			String serviceProvider = (String) appParamManager
					.getConfigurationValue(AppParamManager.SPIDBUSINESS_SERVICEPROVIDER);
			Integer serviceIndex = (Integer) appParamManager
					.getConfigurationValue(AppParamManager.SPIDBUSINESS_SERVICEINDEX);
			String authLevel = (String) appParamManager
					.getConfigurationValue(AppParamManager.SPIDBUSINESS_AUTHLEVEL);
			String authPurpose = (String) appParamManager
					.getConfigurationValue(AppParamManager.SPIDBUSINESS_AUTHPURPOSE);
			//String idProvider = this.idp; 

			target = validateParameters(idProvider, target, action, url, serviceProvider, authLevel);

			if (SUCCESS.equals(target)) {
				// imposta dinamicamente l'endpoint del proxy prima di 
				// utilizzarne i servizi... 
				wsAuthServiceSPID.getProxyWSAuthService().setEndpoint(url);

				String backUrl = getBackUrl(callbackAction, configManager);

				// richiedi il token temporaneo al sevizio SPID... 
				// e salvalo in sessione per il login... 
				String authId = authServiceSPIDManager.getAuthId();

				action.getRequest().getSession().setAttribute(SESSION_ID_SSO_AUTHID, authId);

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
						+ "&authPurpose=" + authPurpose
						+ "&idp=" + idProvider;

				target = SUCCESS;
			}
		} catch (ApsException e) {
			action.addActionError(action.getText("Errors.sso.configuration", new String[] { "Maggioli SPID Business" }));
			action.getRequest().getSession().setAttribute(CommonSystemConstants.SESSION_OBJECT_ACTION, action);
			ApsSystemUtils.logThrowable(e, null, "prepareLogin");
			target = INPUT;
		}

		return target;
	}

	private static String validateParameters(String idProvider, String target, BaseAction action, String url,
											 String serviceProvider, String authLevel) {
		// valida i parametri... 
		if (StringUtils.isEmpty(url)) {
			action.addActionError(action
										  .getText(ERRORS_PARAMETER_CONFIGURATION,
												   new String[] { AppParamManager.SPID_WS_AUTHSERVICESPID_URL }));
			log.error(PREPARE_LOGIN
							  + action.getTextFromDefaultLocale(
					ERRORS_PARAMETER_CONFIGURATION,
					AppParamManager.SPID_WS_AUTHSERVICESPID_URL));
			action.getRequest().getSession().setAttribute(CommonSystemConstants.SESSION_OBJECT_ACTION, action);
			target = INPUT;
		}
		if (StringUtils.isEmpty(serviceProvider)) {
			action.addActionError(action
										  .getText(ERRORS_PARAMETER_CONFIGURATION,
												   new String[] { AppParamManager.SPID_SERVICEPROVIDER }));
			log.error(PREPARE_LOGIN
							  + action.getTextFromDefaultLocale(
					ERRORS_PARAMETER_CONFIGURATION,
					AppParamManager.SPID_SERVICEPROVIDER));
			action.getRequest().getSession().setAttribute(CommonSystemConstants.SESSION_OBJECT_ACTION, action);
			target = INPUT;
		}
		if (StringUtils.isEmpty(authLevel)) {
			action.addActionError(action
										  .getText(ERRORS_PARAMETER_CONFIGURATION,
												   new String[] { AppParamManager.SPID_AUTHLEVEL }));
			log.error(PREPARE_LOGIN
							  + action.getTextFromDefaultLocale(
					ERRORS_PARAMETER_CONFIGURATION,
					AppParamManager.SPID_AUTHLEVEL));
			action.getRequest().getSession().setAttribute(CommonSystemConstants.SESSION_OBJECT_ACTION, action);
			target = INPUT;
		}
		if (StringUtils.isEmpty(idProvider)) {
			action.addActionError(action
										  .getText(ERRORS_PARAMETER_CONFIGURATION,
												   new String[] { "IDP" }));
			log.error(PREPARE_LOGIN
							  + action.getTextFromDefaultLocale(
					ERRORS_PARAMETER_CONFIGURATION,
					"IDP"));
			action.getRequest().getSession().setAttribute(CommonSystemConstants.SESSION_OBJECT_ACTION, action);
			target = INPUT;
		}
		return target;
	}

	/**
	 * Per test manuali: http://localhost:8080/PortaleAppalti/do/SSO/SpidBusinessLoginResponse.action
	 * gestione della risposta di login 
	 */
	public String loginSpidBusiness() {
		String target = SUCCESS;

		BaseAction action = (BaseAction) ActionContext.getContext().getActionInvocation().getAction();

		try {
			String authId = (String) action.getRequest().getSession()
					.getAttribute(SESSION_ID_SSO_AUTHID);

			AuthDataV2 userInfo = authServiceSPIDManager.retrieveUserDataV2(authId);
//			AuthDataV2 userInfo = getTestUserInfo();

			//Formato: VAT-<partita_iva>
			String companyVat = valueWithoutPrefix(StringUtils.stripToNull(userInfo.getAziendaPIVA()));
			//Formato: TINIT-<codice_fiscale>
			String companyFiscalCode = valueWithoutPrefix(StringUtils.stripToNull(userInfo.getAziendaCodiceFiscale()));
			String companyName = StringUtils.stripToNull(userInfo.getAziendaDenominazione());
			//Possibile formato: TINIT-<codice_fiscale>
			String fiscalCode = valueWithoutPrefix(StringUtils.stripToNull(userInfo.getCodiceFiscale()));

			if (!StringUtils.isEmpty(fiscalCode)) {
				if (this.bandiManager.isImpresaRegistrata(companyFiscalCode, companyVat, true) ||
						(this.datiImpresaChecker.existsPartitaIVA(companyVat) &&
								this.datiImpresaChecker.existsCodFiscale(companyFiscalCode))) {
					List<UserDetails> utenze = this.userManager.searchUsersFromDelegateUser(companyFiscalCode);

					// Gestire piu' utenze 
					if (CollectionUtils.isNotEmpty(utenze)) {
						if (utenze.size() > 1) {
							errorWhileLoggingIn("Errors.sso.duplicateAccountBusiness");
							target = INPUT;
						} else
							succcesfullyLoggedIn(companyFiscalCode, companyName, userInfo, utenze.get(0));
					} else {
						errorWhileLoggingIn("Errors.sso.noAccountBusiness");
						target = INPUT;
					}
				} else if (StringUtils.isEmpty(companyFiscalCode)) {

					errorWhileLoggingIn("Errors.sso.companyWithoutFiscalCode");
					target = INPUT;
				} else {
					AccountSSO accountSpid = new AccountSSO();
					accountSpid.setNome(userInfo.getNome());
					accountSpid.setCognome(userInfo.getCognome());
					accountSpid.setLogin(companyFiscalCode);
					accountSpid.setRagioneSociale(companyName);
					accountSpid.setTipologiaLogin(PortGareSystemConstants.TIPOLOGIA_LOGIN_MAGGIOLI_AUTH_SSO_BUSINESS);
					this.getRequest().setAttribute(CommonSystemConstants.SESSION_OBJECT_ACCOUNT_SSO, accountSpid);

					newCompany(companyVat, companyFiscalCode, companyName);
//					target = "redirect"; 
				}
			} else {
				String erroreMessage = this.getText("Errors.sso.event.notPhisicalUserNotAllowed", new String[] { companyName, companyVat, companyFiscalCode });
				sendEvent(null, getIp(), erroreMessage);
				errorWhileLoggingIn("Errors.sso.notPhisicalUserNotAllowed");
				target = INPUT;
			}

		} catch (ApsException e) {
			errorWhileLoggingIn("Errors.sso.readingData");
			ApsSystemUtils.logThrowable(e, this, "login");
			target = INPUT;
		}

		return target;
	}

	/**
	 * Da utilizzare per i valore es:
	 * VAT-(partita_iva)
	 * TINIT-(codice_fiscale)
	 * 
	 * @param value
	 * @return
	 */
	private String valueWithoutPrefix(String value) {
		int indexOf = StringUtils.isEmpty(value) ? -1 : value.indexOf("-")+1;
		return indexOf != -1 ? value.substring(indexOf) : value;
	}

	private AuthDataV2 getTestUserInfo() {
		AuthDataV2 toReturn = new AuthDataV2();

		toReturn.setCognome("Rossi");
		toReturn.setNome("Mario");
		toReturn.setCodiceFiscale("RSSMRA89E12A794G");
		toReturn.setAziendaDenominazione("Maggioli S.p.A.");
		toReturn.setCellulare("03549632862");
		toReturn.setAziendaCodiceFiscale("TINIT-06188330150");
		toReturn.setAziendaPIVA("VAT-02066400405");
		toReturn.setAziendaSedelegale("Via del Carpino n. 8");
		toReturn.setAziendaSedelegaleCitta("Santarcangelo di Romagna");
		toReturn.setDomicilioCap("310030");
		toReturn.setDomicilioCitta("Spinea");
		toReturn.setResidenzaCitta("Treviso");

		return toReturn;
	}

	private void sendEvent(String username, String ip, String text) {
		Event evento = new Event();
		evento.setLevel(Level.INFO);
		evento.setEventType(PortGareEventsConstants.LOGIN_SSO);
		evento.setUsername(username);
		evento.setIpAddress(ip);
		evento.setSessionId(this.getRequest().getSession().getId());
		//evento.setMessage("Login mediante SPID da parte dell'amministratore impersonato da " + nome + " " + cognome  + " con motivazione di accesso o ticket: " + adminAttributes.get("motivazione")); 
		evento.setMessage(text);

		eventManager.insertEvent(evento);
	}

	private void errorWhileLoggingIn(String errorText) {
		this.addActionError(this.getText(errorText, new String[] { "Maggioli SPID-BUSINESS" }));
		this.getRequest().getSession().setAttribute(CommonSystemConstants.SESSION_OBJECT_ACTION, this);
	}

	private void succcesfullyLoggedIn(String companyFiscalCod, String companyName, AuthDataV2 userInfo, UserDetails userDetails) {
		AccountSSO accountSpid = new AccountSSO();
		accountSpid.setNome(userInfo.getNome());
		accountSpid.setCognome(userInfo.getCognome());
		accountSpid.setLogin(companyFiscalCod);
		accountSpid.setRagioneSociale(companyName);
		accountSpid.setTipologiaLogin(PortGareSystemConstants.TIPOLOGIA_LOGIN_MAGGIOLI_AUTH_SSO_BUSINESS);
		this.getRequest().setAttribute(CommonSystemConstants.SESSION_OBJECT_ACCOUNT_SSO, accountSpid);
		String ipAddress = getIp();

		accountSpid.setIpAddress(ipAddress);

		ServletContext ctx = this.getRequest().getSession().getServletContext();
		TrackerSessioniUtenti.getInstance(ctx)
				.putSessioneUtente(this.getRequest().getSession(),
								   ipAddress,
								   accountSpid.getLogin(),
								   DateFormatUtils.format(new Date(), "dd/MM/yyyy HH:mm:ss"));

		String message = this.getText("Succeded.sso.spid.business.login", new String[] { userInfo.getNome(), userInfo.getCognome(), userInfo.getAziendaDenominazione() });

		sendEvent(userDetails.getUsername(), ipAddress, message);

	}

	private String getIp() {
		String ipAddress = this.getRequest().getHeader("X-FORWARDED-FOR");
		if (StringUtils.isEmpty(ipAddress))
			ipAddress = StringUtils.isEmpty(ipAddress) ? this.getRequest().getRemoteAddr() : ipAddress;
		return ipAddress;
	}

	private void newCompany(String companyVat, String companyFiscalCode, String companyName) {
		WizardRegistrazioneImpresaHelper helper = new WizardRegistrazioneImpresaHelper();
		helper.getDatiPrincipaliImpresa().setPartitaIVA(companyVat);
		helper.getDatiPrincipaliImpresa().setRagioneSociale(companyName);
		helper.getDatiPrincipaliImpresa().setCodiceFiscale(companyFiscalCode);
		helper.setTipologiaRegistrazione(PortGareSystemConstants.TIPOLOGIA_LOGIN_MAGGIOLI_AUTH_SSO_BUSINESS);
		this.getRequest().getSession().setAttribute(PortGareSystemConstants.SESSION_ID_DETT_REGISTRAZIONE_IMPRESA, helper);
	}

	public void setUrlRedirect(String urlRedirect){
		this.urlRedirect = urlRedirect;
	}

	public String getUrlRedirect() {
		return getPageURL("ppgare_registr") + "?actionPath=/ExtStr2/do/FrontEnd/RegistrImpr/openPageDatiPrincImpresa.action&currentFrame=7";
	}

}