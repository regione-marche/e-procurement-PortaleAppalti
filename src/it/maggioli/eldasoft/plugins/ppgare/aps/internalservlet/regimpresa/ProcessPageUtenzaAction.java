package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.regimpresa;

import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.CustomConfigManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.services.user.IUserManager;

/**
 * Action di gestione delle operazioni nella pagina di inserimento delle
 * credenziali di accesso e del consenso alla privacy nel wizard di
 * registrazione di un'impresa
 * 
 * @author Stefano.Sabbadin
 * @since 1.2
 */
public class ProcessPageUtenzaAction extends AbstractProcessPageAction {

    /**
     * UID
     */
    private static final long serialVersionUID = -7527283479159496491L;

    // manager per la gestione della business logic

    private IUserManager _userManager;

    // credenziali di accesso
    private String username;
    private String usernameConfirm;
	private CustomConfigManager customConfigManager;
    // consenso alla privacy
    private Integer privacy;
    
    // consenso all'utilizzo piattaforma
    private Integer utilizzoPiattaforma;
    
    // soggetto richiedente
    private String soggettoRichiedente;
    
    /** Consenso alla privacy visualizzato. */
    private boolean visConsensoPrivacy;

    /**
     * @param manager
     *            the _userManager to set
     */
    public void setUserManager(IUserManager manager) {
    	_userManager = manager;
    }

	public void setCustomConfigManager(CustomConfigManager customConfigManager) {
		this.customConfigManager = customConfigManager;
	}
    
    /**
     * @return the username
     */
    public String getUsername() {
    	return username;
    }

    /**
     * @param username
     *            the username to set
     */
    public void setUsername(String username) {
    	this.username = username;
    }

    /**
     * @return the usernameConfirm
     */
    public String getUsernameConfirm() {
    	return usernameConfirm;
    }

    /**
     * @param usernameConfirm
     *            the usernameConfirm to set
     */
    public void setUsernameConfirm(String usernameConfirm) {
    	this.usernameConfirm = usernameConfirm;
    }

    /**
     * @return the privacy
     */
    public Integer getPrivacy() {
    	return privacy;
    }
    
    /**
     * @param privacy
     *            the privacy to set
     */
    public void setPrivacy(Integer privacy) {
    	this.privacy = privacy;
    }
    
    
	/**
	 * @return the utilizzoPiattaforma
	 */
	public Integer getUtilizzoPiattaforma() {
		return utilizzoPiattaforma;
	}

	/**
	 * @param utilizzoPiattaforma the utilizzoPiattaforma to set
	 */
	public void setUtilizzoPiattaforma(Integer utilizzoPiattaforma) {
		this.utilizzoPiattaforma = utilizzoPiattaforma;
	}

	/**
	 * @return the soggettoRichiedente
	 */
	public String getSoggettoRichiedente() {
		return soggettoRichiedente;
	}

	/**
	 * @param soggettoRichiedente the soggettoRichiedente to set
	 */
	public void setSoggettoRichiedente(String soggettoRichiedente) {
		this.soggettoRichiedente = soggettoRichiedente;
	}

	/**
	 * @return the visConsensoPrivacy
	 */
	public boolean isVisConsensoPrivacy() {
		return visConsensoPrivacy;
	}

	/**
	 * @param visConsensoPrivacy the visConsensoPrivacy to set
	 */
	public void setVisConsensoPrivacy(boolean visConsensoPrivacy) {
		this.visConsensoPrivacy = visConsensoPrivacy;
	}

	/**
     * Per sicurezza si valida che gia' in questa fase l'account indicato non
     * esista nel portale prima di inviarlo al backoffice, dove potrebbe
     * comunque risultare duplicato (caso limite, dovuto al fatto che un utente
     * si è registrato 2 volte sul portale in orari ravvicinati, per cui il
     * backoffice non ha ancora processato la prima richiesta e quindi non ha
     * trasferito l'account nel portale ed arriva gia' anche la seconda)
     */
    @Override
    public void validate() {
		super.validate();
		if (this.getFieldErrors().size() > 0) return;
		try {
		    if (!"".equals(this.getUsername())
			    && this.existsUser(this.getUsername())) {
			this.addFieldError("username", this
				.getText("Errors.duplicateUser"));
		    }
		} catch (Throwable t) {
		    throw new RuntimeException(
			    "Error validation of request for account registration"
				    + this.getUsername(), t);
		}
    }

    @Override
    public String back(){
    	
		String target = "back";
		WizardRegistrazioneImpresaHelper registrazioneHelper = (WizardRegistrazioneImpresaHelper) this.session
		.get(PortGareSystemConstants.SESSION_ID_DETT_REGISTRAZIONE_IMPRESA);
		
		try {
	    	if(!customConfigManager.isVisible("IMPRESA-DATIULT", "STEP")){
	    			
	    		if(registrazioneHelper.isLiberoProfessionista()){
	    			target = "backNoDatiUlterioriLiberoProf";
	    		} else {
   					target = "backNoDatiUlteriori";
   				}
   			} 
   		}catch (Exception e) {
   			// Configurazione sbagliata
   			ApsSystemUtils.logThrowable(e, this, "next",
   					"Errore durante la ricerca delle proprietà di visualizzazione dello step dati ulteriori");
   		}
		return target;
    }
    
    
    /**
     * check if user exist
     * 
     * @param username
     * @return true if exist a user with this username, false if user not exist.
     * @throws Throwable
     *             In error case.
     */
    protected boolean existsUser(String username) throws Throwable {
		boolean exists = (username != null && username.trim().length() >= 0 && this._userManager
			.getUser(username) != null);
		return exists;
    }

    public String next() {
		String target = SUCCESS;
	
		WizardRegistrazioneImpresaHelper registrazioneHelper = (WizardRegistrazioneImpresaHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_REGISTRAZIONE_IMPRESA);
	
		if (registrazioneHelper == null) {
		    // la sessione e' scaduta, occorre riconnettersi
		    this.addActionError(this.getText("Errors.sessionExpired"));
		    target = CommonSystemConstants.PORTAL_ERROR;
		} else {
		    // aggiorna i dati in sessione
		    registrazioneHelper.setUsername(this.username);
		    registrazioneHelper.setUsernameConfirm(this.usernameConfirm);
		    registrazioneHelper.setPrivacy(this.privacy);
		    registrazioneHelper.setUtilizzoPiattaforma(this.utilizzoPiattaforma);
		    registrazioneHelper.setSoggettoRichiedente(this.soggettoRichiedente);
		}
		return target;
    }

}
