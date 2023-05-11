package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.regimpresa;

import com.agiletec.apsadmin.system.BaseAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.AccountSSO;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;

/**
 * Action di gestione dell'apertura della pagina di inserimento credenziali
 * accesso e consenso alla privacy nel wizard di registrazione impresa
 * 
 * @author Stefano.Sabbadin
 * @since 1.2
 */
public class OpenPageUtenzaAction extends BaseAction implements SessionAware {
    /**
     * UID
     */
    private static final long serialVersionUID = -7527283479159496491L;

    private Map<String, Object> session;
    private IAppParamManager appParamManager;

    // credenziali di accesso
	@Validate(EParamValidation.USERNAME)
    private String username;
	@Validate(EParamValidation.USERNAME)
    private String usernameConfirm;

    // consenso alla privacy
    private Integer privacy;
    
    // consenso extra customizzabile
    private Integer consensoExtra;
    
    // consenso all'utilizzo piattaforma
    private Integer utilizzoPiattaforma;
    
    // soggetto richiedente
	@Validate(EParamValidation.GENERIC)
    private String soggettoRichiedente;
    
    private Boolean ssoPresente;
    private Integer ssoProtocollo;

    @Override
    public void setSession(Map<String, Object> session) {
    	this.session = session;
    }

	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}
	
	public String getUsername() {
    	return username;
    }

    public void setUsername(String username) {
    	this.username = username;
    }

    public String getUsernameConfirm() {
    	return usernameConfirm;
    }

    public void setUsernameConfirm(String usernameConfirm) {
    	this.usernameConfirm = usernameConfirm;
    }

    public Integer getPrivacy() {
    	return privacy;
    }

    public void setPrivacy(Integer privacy) {
    	this.privacy = privacy;
    }

	public Integer getConsensoExtra() {
		return consensoExtra;
	}

	public void setConsensoExtra(Integer consensoExtra) {
		this.consensoExtra = consensoExtra;
	}

	public Integer getUtilizzoPiattaforma() {
		return utilizzoPiattaforma;
	}

	public void setUtilizzoPiattaforma(Integer utilizzoPiattaforma) {
		this.utilizzoPiattaforma = utilizzoPiattaforma;
	}

	public String getSoggettoRichiedente() {
		return soggettoRichiedente;
	}

	public void setSoggettoRichiedente(String soggettoRichiedente) {
		this.soggettoRichiedente = soggettoRichiedente;
	}	

	public Boolean getSsoPresente() {
		return ssoPresente;
	}

	public void setSsoPresente(Boolean ssoPresente) {
		this.ssoPresente = ssoPresente;
	}
	
	public Integer getSsoProtocollo() {
		return ssoProtocollo;
	}

	public void setSsoProtocollo(Integer ssoProtocollo) {
		this.ssoProtocollo = ssoProtocollo;
	}

	/**
	 * ... 
	 */
	public String openPage() {
		String target = SUCCESS;
		
		WizardRegistrazioneImpresaHelper registrazioneHelper = (WizardRegistrazioneImpresaHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_REGISTRAZIONE_IMPRESA);
	
		if (registrazioneHelper == null) {
		    // la sessione e' scaduta, occorre riconnettersi
		    this.addActionError(this.getText("Errors.sessionExpired"));
		    target = CommonSystemConstants.PORTAL_ERROR;
		} else {
		    // la sessione non e' scaduta, per cui proseguo regolarmente
	
		    // aggiorna i dati nel bean a partire da quelli presenti in sessione
		    this.username = registrazioneHelper.getUsername();
		    this.usernameConfirm = registrazioneHelper.getUsernameConfirm();
		    this.privacy = registrazioneHelper.getPrivacy();
		    this.consensoExtra = registrazioneHelper.getConsensoExtra();
		    this.utilizzoPiattaforma = registrazioneHelper.getUtilizzoPiattaforma();
		    this.soggettoRichiedente = registrazioneHelper.getSoggettoRichiedente();

			handleSSOLogin(registrazioneHelper);

			this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
		    				 PortGareSystemConstants.WIZARD_REGIMPRESA_PAGINA_ACCOUNT_PRIVACY);
		}
		return target;
    }

	private void handleSSOLogin(WizardRegistrazioneImpresaHelper registrazioneHelper) {
		AccountSSO sso = (AccountSSO) this.session.get(CommonSystemConstants.SESSION_OBJECT_ACCOUNT_SSO);
		this.ssoPresente = (sso != null || PortGareSystemConstants.TIPOLOGIA_LOGIN_MAGGIOLI_AUTH_SSO_BUSINESS.equals(registrazioneHelper.getTipologiaRegistrazione()));
	}
	
	/**
	 * ... 
	 */
    public String openPageAfterError() {
		String target = SUCCESS;
		WizardRegistrazioneImpresaHelper registrazioneHelper = (WizardRegistrazioneImpresaHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_REGISTRAZIONE_IMPRESA);
	
		if (registrazioneHelper == null) {
		    // la sessione e' scaduta, occorre riconnettersi
		    this.addActionError(this.getText("Errors.sessionExpired"));
		    target = CommonSystemConstants.PORTAL_ERROR;
		} else {
		    // la sessione non e' scaduta, per cui proseguo regolarmente
	
		    this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
		    				 PortGareSystemConstants.WIZARD_REGIMPRESA_PAGINA_ACCOUNT_PRIVACY);
		}
		return target;
    }

}
