package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.areapers;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.url.IURLManager;
import com.agiletec.aps.system.services.url.PageURL;
import com.agiletec.aps.system.services.user.IUserManager;
import com.agiletec.apsadmin.system.BaseAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.AccountSSO;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.BaseResponseAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.cie.CieLoginResponseAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.cns.CnsLoginResponseAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.crs.CrsLoginResponseAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.federa.FederaLoginResponseAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.gel.GelLoginResponseAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.myid.MyIdLoginResponseAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.shibboleth.ShibbolethLoginResponseAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.spid.SpidLoginResponseAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.TrackerSessioniUtenti;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.spid.IAuthServiceSPIDManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.spid.WSAuthServiceSPIDWrapper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;
import org.w3c.dom.Document;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

/**
 * Azione che gestisce il join tra utenza standard e utenza SPID, CIE, etc.
 * 
 * @author ...
 */
public class AbilitaAccessoSSOAction extends BaseAction implements ServletResponseAware, SessionAware {
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
	private IUserManager userManager;
	private ConfigInterface configManager;
	private List<String> autenticazioni;
	private HttpServletResponse response;
	@Validate(EParamValidation.URL)
	private String urlLoginCohesion;
	@Validate(EParamValidation.URL)
	private String urlRedirect;
	@Validate(EParamValidation.URL)
	private String urlLogin;
	@Validate(EParamValidation.URL)
	private String idp;
	@Validate(EParamValidation.CODICE_FISCALE)
	private String cfAssociato;
	/** contenitore dei dati di sessione. */
	private Map<String, Object> session;
	
	@Override
	public void setSession(Map<String, Object> arg0) {
		this.session = arg0;
	}
		
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
	
	public void setConfigManager(ConfigInterface configManager) {
		this.configManager = configManager;
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

	public void setUserManager(IUserManager userManager) {
		this.userManager = userManager;
	}

	public List<String> getAutenticazioni() {
		return autenticazioni;
	}

	public void setAutenticazioni(List<String> autenticazioni) {
		this.autenticazioni = autenticazioni;
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

	public String getCfAssociato() {
		return cfAssociato;
	}

	public void setCfAssociato(String cfAssociato) {
		this.cfAssociato = cfAssociato;
	}

	public String getUrlLoginCohesion() {
		return urlLoginCohesion;
	}

	public void setUrlLoginCohesion(String urlLoginCohesion) {
		this.urlLoginCohesion = urlLoginCohesion;
	}
	
	/**
	 * ... 
	 */
	public String open() {
		String target = SUCCESS;
		try{
			
			if(null != this.getCurrentUser() && !StringUtils.isEmpty(this.getCurrentUser().getDelegateUser())){
				target = "redirect";
				this.cfAssociato = this.getCurrentUser().getDelegateUser();
			} else {
				this.autenticazioni = appParamManager.loadEnabledAuthentications();
				this.urlLoginCohesion = (String) appParamManager.getConfigurationValue("auth.sso.cohesion.login.url") + "Join" ;
			}
		}catch (Exception e) {
			this.addActionError(this.getText("Errors.auth.generic", new String[] { "Maggioli Auth" }));
			this.getRequest().getSession().setAttribute("ACTION_OBJECT", this);
			ApsSystemUtils.logThrowable(e, null, "AbilitaAccessoSSO");
			target = INPUT;
		}
		return target;
	}
	
	/**
	 * prepara il login al servizio di autenticazione SPID 
	 */
	public String prepareLoginSpid() {
		String target = SUCCESS;
		
		this.urlRedirect = SpidLoginResponseAction.prepareCallbackLogin(
				"/do/FrontEnd/AreaPers/ssoMaggioliLoginResponse.action",
				this.idp,
				this.configManager,
				this.appParamManager,
				this.authServiceSPIDManager,
				this.wsAuthServiceSPID);
		
		if(this.urlRedirect == null) {
			this.addActionError(this.getText("Errors.unexpected"));
			this.getRequest().getSession().setAttribute("ACTION_OBJECT", this);
			target = INPUT;
		}
		return target;
	}
	
	public String prepareLoginSpidBusiness() {
		String target = SUCCESS;
		
		this.urlRedirect = SpidLoginResponseAction.prepareCallbackLogin(
				"/do/FrontEnd/AreaPers/ssoMaggioliLoginResponse.action",
				this.idp,
                this.configManager,
				this.appParamManager,
				this.authServiceSPIDManager,
				this.wsAuthServiceSPID);
		
		if(this.urlRedirect == null) {
			this.addActionError(this.getText("Errors.unexpected"));
			this.getRequest().getSession().setAttribute("ACTION_OBJECT", this);
			target = INPUT;
		}
		return target;
	}
	
	
	
	/**
	 * prepara il login al servizio di autenticazione CIE 
	 */
	public String prepareLoginCie() {
		String target = SUCCESS;
		
		this.urlRedirect = CieLoginResponseAction.prepareCallbackLogin(
				"/do/FrontEnd/AreaPers/ssoMaggioliLoginResponse.action",
                this.configManager,
				this.appParamManager,
				this.authServiceSPIDManager,
				this.wsAuthServiceSPID);
		
		if(this.urlRedirect == null) {
			this.addActionError(this.getText("Errors.unexpected"));
			this.getRequest().getSession().setAttribute("ACTION_OBJECT", this);
			target = INPUT;
		}
		return target;
	}
	
	/**
	 * prepara il login al servizio di autenticazione CNS 
	 */
	public String prepareLoginCns() {
		String target = SUCCESS;
		
		this.urlRedirect = CnsLoginResponseAction.prepareCallbackLogin(
				"/do/FrontEnd/AreaPers/ssoMaggioliLoginResponse.action",
                this.configManager,
				this.appParamManager,
				this.authServiceSPIDManager,
				this.wsAuthServiceSPID);
		
		if(this.urlRedirect == null) {
			this.addActionError(this.getText("Errors.unexpected"));
			this.getRequest().getSession().setAttribute("ACTION_OBJECT", this);
			target = INPUT;
		}
		return target;
	}
	
	/**
	 * prepara il login al servizio di autenticazione CRS 
	 */
	public String prepareLoginCrs() {
		String target = SUCCESS;
		
		this.urlRedirect = CrsLoginResponseAction.prepareCallbackLogin(
				"/do/FrontEnd/AreaPers/ssoMaggioliLoginResponse.action",
                this.configManager,
				this.appParamManager,
				this.authServiceSPIDManager,
				this.wsAuthServiceSPID);
		
		if(this.urlRedirect == null) {
			this.addActionError(this.getText("Errors.unexpected"));
			this.getRequest().getSession().setAttribute("ACTION_OBJECT", this);
			target = INPUT;
		}
		return target;
	}
	
	
	/**
	 * prepara il login al servizio di autenticazione GEL 
	 */
	public String prepareLoginGel() {
		String target = SUCCESS;
		
		this.urlRedirect = GelLoginResponseAction.prepareCallbackLogin(
				"/do/FrontEnd/AreaPers/ssoMaggioliLoginResponse.action",
                this.configManager,
				this.appParamManager,
				this.authServiceSPIDManager,
				this.wsAuthServiceSPID);
		
		if(this.urlRedirect == null) {
			this.addActionError(this.getText("Errors.unexpected"));
			this.getRequest().getSession().setAttribute("ACTION_OBJECT", this);
			target = INPUT;
		}
		return target;
	}
	
	/**
	 * prepara il login al servizio di autenticazione My Id 
	 */
	public String prepareLoginMyId() {
		String target = SUCCESS;
		
		this.urlRedirect = MyIdLoginResponseAction.prepareCallbackLogin(
				"/do/FrontEnd/AreaPers/ssoMaggioliLoginResponse.action",
                this.configManager,
				this.appParamManager,
				this.authServiceSPIDManager,
				this.wsAuthServiceSPID);
		
		if(this.urlRedirect == null) {
			this.addActionError(this.getText("Errors.unexpected"));
			this.getRequest().getSession().setAttribute("ACTION_OBJECT", this);
			target = INPUT;
		}
		return target;
	}
	
	
	/**
	 * prepara il login al servizio di autenticazione Federa 
	 */
	public String prepareLoginFedera() {
		String target = SUCCESS;
		
		this.urlRedirect = FederaLoginResponseAction.prepareCallbackLogin(
				"/do/FrontEnd/AreaPers/ssoMaggioliLoginResponse.action",
                this.configManager,
				this.appParamManager,
				this.authServiceSPIDManager,
				this.wsAuthServiceSPID);
		
		if(this.urlRedirect == null) {
			this.addActionError(this.getText("Errors.unexpected"));
			this.getRequest().getSession().setAttribute("ACTION_OBJECT", this);
			target = INPUT;
		}
		return target;
	}
	
	
	
	
	/**
	 * prepara il login al servizio di autenticazione  
	 */
	public String prepareLoginShibboleth() {
		String target = SUCCESS;
		
		this.urlRedirect = null;
		
		Integer ssoShibbolethAttivo = (Integer)appParamManager.getConfigurationValue(PortGareSystemConstants.TIPO_PROTOCOLLO_SSO_SHIBBOLETH);
		
		if(ssoShibbolethAttivo == 1) {
			StringBuilder sb = new StringBuilder(); 
			sb.append(this.configManager.getParam(SystemConstants.PAR_APPL_BASE_URL))
			  .append("do/FrontEnd/AreaPers/shibboletLoginResponse.action");
			
			this.urlRedirect = sb.toString();
		}	
			
		if(this.urlRedirect == null) {
			this.addActionError(this.getText("Errors.unexpected"));			
			this.getRequest().getSession().setAttribute("ACTION_OBJECT", this);			
			target = INPUT;
		}
		
		return target;
	}
	
	
	/**
	 * gestione della risposta di login
	 */
	public String login() {
		String target = SUCCESS;

		try {
			AccountSSO account = BaseResponseAction.validateLogin(
					this.appParamManager,
					this.authServiceSPIDManager,
					this.wsAuthServiceSPID);
			
			if(account == null) {
				target = INPUT;
			} else {
				this.userManager.setDelegateUser(this.getCurrentUser().getUsername(), account.getLogin());
				this.getCurrentUser().setDelegateUser(account.getLogin());
				this.userManager.updateLastAccess(this.getCurrentUser());
				putAccountInSession(account);
				this.urlRedirect = this.getPageURL("ppcommon_area_personale");
			}
		} catch (ApsSystemException e) {
			this.addActionError(this.getText("Errors.unexpected"));
			ApsSystemUtils.logThrowable(null, this, "login");
			target = INPUT;
		}
		
		return target;
	}
	
	public String loginShibboleth() {		
		String target = SUCCESS;

		try {
			AccountSSO accountShib = ShibbolethLoginResponseAction.validateLogin(this.appParamManager);
			if(accountShib == null) {
				target = INPUT;
			} else {
				// aggiorna il delegate user dell'utente loggato 
				this.userManager.setDelegateUser(this.getCurrentUser().getUsername(), accountShib.getLogin());
				this.getCurrentUser().setDelegateUser(accountShib.getLogin());
				this.userManager.updateLastAccess(this.getCurrentUser());
				putAccountInSession(accountShib);
				this.urlRedirect = this.getPageURL("ppcommon_area_personale");
			}
		} catch (ApsSystemException e) {
			this.addActionError(this.getText("Errors.unexpected"));
			ApsSystemUtils.logThrowable(null, this, "login");
			target = INPUT;
		}
		
		return target;
	}	
	

	private void putAccountInSession(AccountSSO account) throws ApsSystemException{
		try {
			this.session.put(CommonSystemConstants.SESSION_OBJECT_ACCOUNT_SSO, account);
			TrackerSessioniUtenti tracker = TrackerSessioniUtenti.getInstance(this.getRequest().getSession().getServletContext());
			String sessionId = this.getRequest().getSession().getId();
			String[] info = (String[]) tracker.getDatiSessioniUtentiConnessi().get(sessionId);
			tracker.putSessioneUtente(this.getRequest().getSession(), info[0], account.getLogin(), info[2]);
		} catch (Exception ex) {
			throw new ApsSystemException("Errore nell'aggiornamento della sessione utenti in seguito a login SSO. Utente: "+account.getLogin(), ex);
		}
	}
	
	
	public String loginCohesion() {
		String target = SUCCESS;
		String cohesionEncryptionKey = (String) appParamManager
				.getConfigurationValue("auth.sso.cohesion.encryption.key");
		String token = StringEscapeUtils.unescapeXml(this.getRequest()
				.getParameter("token"));

		if (token != null) {
			String cohesionResponse = new String(base64Decode(token));
			if (!"".equals(cohesionEncryptionKey)) {
				try {
					cohesionResponse = new String(cipher3DES(false,
							base64Decode(token),
							cohesionEncryptionKey.getBytes()));
					Document cohesionResponseXML = getXmlDocFromString(cohesionResponse);
					cohesionResponseXML.getDocumentElement().normalize();

					String attributoLoginCodiceFiscale = (String) appParamManager
							.getConfigurationValue("auth.sso.cohesion.mapping.login");
					
					String login = getCohesionElement(cohesionResponseXML,
							attributoLoginCodiceFiscale);
					this.userManager.setDelegateUser(this.getCurrentUser().getUsername(), login);
					this.getCurrentUser().setDelegateUser(login);
					this.userManager.updateLastAccess(this.getCurrentUser());
					String attributoNome = (String) appParamManager
					.getConfigurationValue("auth.sso.cohesion.mapping.nome");
					String attributoCognome = (String) appParamManager
							.getConfigurationValue("auth.sso.cohesion.mapping.cognome");
					String attributoEmail = (String) appParamManager
							.getConfigurationValue("auth.sso.cohesion.mapping.email");
					String attributoTipoAutenticazione = (String) appParamManager
							.getConfigurationValue("auth.sso.cohesion.mapping.tipoAutenticazione");
					String nome = getCohesionElement(cohesionResponseXML,
							attributoNome);
					String cognome = getCohesionElement(cohesionResponseXML,
							attributoCognome);
					String email = getCohesionElement(cohesionResponseXML,
							attributoEmail);
					String tipoAutenticazione = getCohesionElement(
							cohesionResponseXML, attributoTipoAutenticazione);
					AccountSSO accountCohesion = new AccountSSO();
					accountCohesion.setLogin(login);
					accountCohesion.setNome(nome);
					accountCohesion.setCognome(cognome);
					accountCohesion.setEmail(email);
					accountCohesion
							.setTipoAutenticazione(tipoAutenticazione);
					accountCohesion.setTipologiaLogin(PortGareSystemConstants.TIPOLOGIA_LOGIN_COHESION_SSO);
					putAccountInSession(accountCohesion);
					
					this.urlRedirect = this.getPageURL("ppcommon_area_personale");

				} catch (Exception ex) {
					this.addActionError(this.getText("Errors.unexpected"));
					ApsSystemUtils.logThrowable(null, this, "login");
					target = INPUT;
				}
			}
		}
		return target;
	}
	
	private Document getXmlDocFromString(String xml) {

		Document xmlDoc = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			dbf.setNamespaceAware(true);
			xmlDoc = dbf.newDocumentBuilder().parse(
					new ByteArrayInputStream(xml.getBytes()));
		} catch (Exception ex) {
			ApsSystemUtils.logThrowable(ex, this, "getXmlDocFromString");
		}
		return xmlDoc;
	}

	private byte[] cipher3DES(boolean encrypt, byte[] message, byte[] key)
			throws Exception {
		byte[] cipher = new byte[0];
		try {
			if (key.length != 24) {
				throw new Exception("key size must be 24 bytes");
			}
			int cipherMode = Cipher.DECRYPT_MODE;
			if (encrypt) {
				cipherMode = Cipher.ENCRYPT_MODE;
			}
			Cipher sendCipher = Cipher.getInstance("DESede/ECB/NoPadding");
			SecretKey myKey = new SecretKeySpec(key, "DESede");
			sendCipher.init(cipherMode, myKey);
			cipher = sendCipher.doFinal(message);
		} catch (Exception ex) {
			ApsSystemUtils.logThrowable(ex, this, "cipher3DES");
		}
		return cipher;
	}
	
	
	private byte[] base64Decode(String data) {
		byte[] result = new byte[0];
		try {
			result = DatatypeConverter.parseBase64Binary(data);
		} catch (Exception ex) {
			ApsSystemUtils.logThrowable(ex, this, "base64Decode");
		}
		return result;
	}
	
	
	/**
	 * gestione dell'unlink account SSO
	 */
	public String unlink() {
		String target = SUCCESS;

		try {
			this.userManager.setDelegateUser(this.getCurrentUser().getUsername(), "");
			this.getCurrentUser().setDelegateUser("");
			this.urlRedirect = this.getPageURL("ppcommon_area_personale");
		} catch (ApsSystemException e) {
			this.addActionError(this.getText("Errors.unexpected"));
			ApsSystemUtils.logThrowable(null, this, "unlink");
			target = INPUT;
		}
		
		return target;
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
	private String getPageURL(String page) {
		RequestContext reqCtx = new RequestContext();
//		Lang currentLang = this.getLangManager().getDefaultLang();
		IPage pageDest = this.pageManager.getPage(page);
		reqCtx.setRequest(this.getRequest());
		reqCtx.setResponse(this.response);
		reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG, getCurrentLang());
		reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE, pageDest);
		PageURL url = this.urlManager.createURL(reqCtx);
		return url.getURL();
	}
	
	private String getCohesionElement(Document doc, String name) {
		String value = null;
		try {
			value = StringUtils.stripToNull(doc.getElementsByTagName(name)
					.item(0).getTextContent());
		} catch (Exception e) {
			value = null;
		}
		return value;
	}
	
}
