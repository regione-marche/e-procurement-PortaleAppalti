package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.areapers;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.aps.system.services.user.DelegateUser;
import com.agiletec.aps.system.services.user.IUserManager;
import com.agiletec.apsadmin.system.BaseAction;
import com.agiletec.plugins.jpmail.aps.services.mail.IMailManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.AccountSSO;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.ImpresaAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.struts2.interceptor.validation.SkipValidation;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Gestione dei profili dei soggetti impresa SSO 
 * 
 */
public class DelegatiImpresaSSOAction extends BaseAction implements SessionAware {
	/**
	 * UID 
	 */
	private static final long serialVersionUID = -4697124808563885246L;
	
	private static final int NOTIFY_ADD 	= 1;
	private static final int NOTIFY_UPDATE	= 2;
	private static final int NOTIFY_REMOVE 	= 3;
	
	private static final String ID_SESSIONE_DELEGATO_IMPRESA 	= "delegatoImpresaSSO";
	
	private IUserManager userManager;
	private IEventManager eventManager;
	private IMailManager mailManager;
	private ConfigInterface configManager;
		
	private Map<String, Object> session;
	
	private List<DelegateUser> delegatiImpresa;
	private List<DelegateUser> sessioni;
	private Boolean questionConfirmDelete;
	private Long idDelete;
	
	private Long id;
	@Validate(EParamValidation.CODICE_FISCALE)
	private String idUtente;
	@Validate(EParamValidation.COGNOME)
	private String cognomeNome;
	@Validate(EParamValidation.GENERIC)
	private String profilo;
	@Validate(EParamValidation.EMAIL)
	private String email;
	
	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}

	public void setUserManager(IUserManager userManager) {
		this.userManager = userManager;
	}

	public void setMailManager(IMailManager mailManager) {
		this.mailManager = mailManager;
	}

	public void setConfigManager(ConfigInterface configManager) {
		this.configManager = configManager;
	}

	public List<DelegateUser> getDelegatiImpresa() {
		return delegatiImpresa;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIdUtente() {
		return idUtente;
	}

	public void setIdUtente(String idUtente) {
		this.idUtente = idUtente;
	}

	public String getCognomeNome() {
		return cognomeNome;
	}

	public void setCognomeNome(String cognomeNome) {
		this.cognomeNome = cognomeNome;
	}

	public String getProfilo() {
		return profilo;
	}

	public void setProfilo(String profilo) {
		this.profilo = profilo;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public DelegateUser.Accesso[] getAccesso() {
		return DelegateUser.Accesso.values();
	}

	public List<DelegateUser> getSessioni() {
		return sessioni;
	}
	
	public Boolean getQuestionConfirmDelete() {
		return questionConfirmDelete;
	}

	public void setQuestionConfirmDelete(Boolean questionConfirmDelete) {
		this.questionConfirmDelete = questionConfirmDelete;
	}

	public Long getIdDelete() {
		return idDelete;
	}

	public void setIdDelete(Long idDelete) {
		this.idDelete = idDelete;
	}

	/**
	 * apre la pagina dell'area personale per un account SSO 
	 */
	@SkipValidation
	public String view() {
		String target = SUCCESS;
		
		if( !isOwner() ) {
			this.addActionError(this.getText("Errors.function.notEnabled"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			try {
				// carica la lista dei soggetti impresa
				delegatiImpresa = userManager.getProfiliSSO(this.getCurrentUser().getUsername(), null);
				
				// recupera i dati del soggetto impresa dalla sessione...
				setInfoSoggettoImpresa( (DelegateUser)getRequest().getSession().getAttribute(ID_SESSIONE_DELEGATO_IMPRESA) );
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "view");
				this.addActionError(this.getText("Errors.unexpected"));
				target = CommonSystemConstants.PORTAL_ERROR;
			}
		}
		
		return target;
	}
	
	/**
	 * prepara gli input per l'inserimento di un nuovo soggetto impresa
	 */
	@SkipValidation
	public String clearSoggetto() {
		String target = SUCCESS;
		questionConfirmDelete = false;
		setInfoSoggettoImpresa(null);
		getRequest().getSession().removeAttribute(ID_SESSIONE_DELEGATO_IMPRESA);
		return target;
	}	

	/**
	 * aggiungi un soggetto impresa come delegato per operare per l'impresa 
	 */
	@SkipValidation
	public String modifySoggetto() {
		String target = "modify";
		
		if( !isOwner() ) {
			this.addActionError(this.getText("Errors.function.notEnabled"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			try {
				// carica la lista dei soggetti impresa
				delegatiImpresa = userManager.getProfiliSSO(this.getCurrentUser().getUsername(), null);

				// carica il delegate selezionato
				//loadDelegato(idUtente);
				if(id != null && id.longValue() >= 0)
					setInfoSoggettoImpresa(id.intValue());
				
				getRequest().getSession().setAttribute(ID_SESSIONE_DELEGATO_IMPRESA, buildDelegateUser(null)); 
				
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "modify");
				this.addActionError(this.getText("Errors.unexpected"));
				target = CommonSystemConstants.PORTAL_ERROR;
			}
		}
		
		return target;
	}
	
	/**
	 * aggiungi un soggetto impresa come delegato per operare per l'impresa 
	 */
	public String addSoggetto() {
		String target = "clear";
		
		if( !isOwner() ) {
			this.addActionError(this.getText("Errors.function.notEnabled"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			try {
				if(userManager.getProfiloSSO(this.getCurrentUser().getUsername(), idUtente) != null) {
					this.addActionError(this.getText("Errors.delegateSSO.duplicatedCodiceFiscale"));
					target = INPUT;
				} else {
					// crea un nuovo soggetto impresa
					DelegateUser soggetto = buildDelegateUser(null);
					
					// aggiungi il nuovo profilo
					userManager.addProfiloSSO(soggetto);
					
					// invia una mail di notifica sulla variazione del profilo 
					sendNotification(soggetto, NOTIFY_ADD);
				}
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "add");
				this.addActionError(this.getText("Errors.unexpected"));
				target = CommonSystemConstants.PORTAL_ERROR;
			}
		}
		
		return target;
	}

	/**
	 * modifica un soggetto impresa come delegato per operare per l'impresa 
	 */
	public String saveSoggetto() {
		String target = "clear";
		
		if( !isOwner() ) {
			this.addActionError(this.getText("Errors.function.notEnabled"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			try {
//				String oldIdUtente = null;
//				if(id != null && id.intValue() >= 0) {
//					DelegateUser s = (DelegateUser)getRequest().getSession().getAttribute(ID_SESSIONE_DELEGATO_IMPRESA);
//					oldIdUtente = (s != null ? s.getDelegate() : null);
//				}
				DelegateUser soggetto = userManager.getProfiloSSO(this.getCurrentUser().getUsername(), idUtente);						
				if(soggetto != null) {
					soggetto = buildDelegateUser(soggetto);
					
					userManager.updateProfiloSSO(soggetto);
					
					// invia una mail di notifica sulla variazione del profilo 
					sendNotification(soggetto, NOTIFY_UPDATE);
				}
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "save");
				this.addActionError(this.getText("Errors.unexpected"));
				target = CommonSystemConstants.PORTAL_ERROR;
			}
		}
		
		return target;
	}

	/**
	 * Chiede la conferma per l'eliminazione di un soggetto impresa
	 */
	@SkipValidation
	public String confirmDeleteSoggetto() {
		String target = SUCCESS;
		try {
			questionConfirmDelete = true;
			idDelete = id;
			DelegateUser soggetto = (DelegateUser)getRequest().getSession().getAttribute(ID_SESSIONE_DELEGATO_IMPRESA);
			getRequest().getSession().setAttribute(ID_SESSIONE_DELEGATO_IMPRESA, buildDelegateUser(soggetto));
			delegatiImpresa = userManager.getProfiliSSO(this.getCurrentUser().getUsername(), null);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "confirmDeleteSoggetto");
		}
		return target;
	}

	/**
	 * aggiungi un soggetto impresa come delegato per operare per l'impresa 
	 */
	@SkipValidation
	public String deleteSoggetto() {
		String target = SUCCESS;
		
		if( !isOwner() ) {
			this.addActionError(this.getText("Errors.function.notEnabled"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else { 
			try {
				DelegateUser soggetto = (DelegateUser)getRequest().getSession().getAttribute(ID_SESSIONE_DELEGATO_IMPRESA);
				idUtente = soggetto.getDelegate();
				soggetto = userManager.getProfiloSSO(this.getCurrentUser().getUsername(), idUtente);
				userManager.removeProfiloSSO(this.getCurrentUser().getUsername(), idUtente);
			
				// invia una mail di notifica sulla variazione del profilo 
				sendNotification(soggetto, NOTIFY_REMOVE);
				
				getRequest().getSession().removeAttribute(ID_SESSIONE_DELEGATO_IMPRESA);
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "remove");
				this.addActionError(this.getText("Errors.unexpected"));
				target = CommonSystemConstants.PORTAL_ERROR;
			}
		}
		
		return target;
	}

	/**
	 * Annulla l'eliminazione di un soggetto impresa
	 */
	@SkipValidation
	public String cancelDeleteSoggetto() {
		getRequest().getSession().removeAttribute(ID_SESSIONE_DELEGATO_IMPRESA);
		return SUCCESS;
	}

	@Override
    public void validate() {
		super.validate();
		if (this.getFieldErrors().size() > 0) {
			return;
		}

		if(StringUtils.isEmpty(idUtente)) {
			this.addFieldError("idUtente", this.getText("Errors.requiredstring", new String[] { this.getTextFromDB("idUtente") }));
		}
		if(StringUtils.isEmpty(cognomeNome)) {
			this.addFieldError("cognomeNome", this.getText("Errors.requiredstring", new String[] { this.getTextFromDB("cognomeNome") }));
		}
		if(StringUtils.isEmpty(profilo)) {
			this.addFieldError("profilo", this.getText("Errors.requiredstring", new String[] { this.getTextFromDB("profilo") }));
		}
//		if(StringUtils.isEmpty(email)) {
//			this.addFieldError("email", this.getText("Errors.requiredstring", new String[] { this.getTextFromDB("email") }));
//		}
    }
	
	/**
	 * apre la pagina dell'area personale con i LOCK attivi dei soggetti impresa 
	 */
	@SkipValidation
	public String viewSessions() {
		String target = SUCCESS;
		
		if( !isOwner() ) {
			this.addActionError(this.getText("Errors.function.notEnabled"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			try {
				// carica la lista dei lock
				sessioni = getListaSessioni();
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "viewSessions");
				this.addActionError(this.getText("Errors.unexpected"));
				target = CommonSystemConstants.PORTAL_ERROR;
			}
		}
		
		return target;
	}
	
	/**
	 * chiede la conferma per l'eliminazione di un soggetto impresa
	 */
	@SkipValidation
	public String confirmUnlock() {
		String target = SUCCESS;
		try {
			questionConfirmDelete = true;
			idDelete = id;
			DelegateUser soggetto = (DelegateUser)getRequest().getSession().getAttribute(ID_SESSIONE_DELEGATO_IMPRESA);
			getRequest().getSession().setAttribute(ID_SESSIONE_DELEGATO_IMPRESA, buildDelegateUser(soggetto));
			sessioni = getListaSessioni();
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "confirmUnlock");
		}
		return target;
	}

	/**
	 * apre la pagina dell'area personale con i LOCK attivi dei soggetti impresa 
	 */
	@SkipValidation
	public String unlock() {
		String target = SUCCESS;
		
		if( !isOwner() ) {
			this.addActionError(this.getText("Errors.function.notEnabled"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			DelegateUser soggetto = (DelegateUser)getRequest().getSession().getAttribute(ID_SESSIONE_DELEGATO_IMPRESA);
			try {
				idUtente = soggetto.getDelegate();
				userManager.unlockProfiloSSOAccess(getCurrentUser(), idUtente);
				
				// aggiorna i dati del profilo sso dopo aver rilasciato il lock del soggetto impresa
				DelegateUser u = userManager.getProfiloSSO(getCurrentUser().getUsername(), idUtente);
				soggetto.copyFrom(u);
				
				getRequest().getSession().removeAttribute(ID_SESSIONE_DELEGATO_IMPRESA);
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "unlock");
				this.addActionError(this.getText("Errors.unexpected"));
				target = CommonSystemConstants.PORTAL_ERROR;
			}
		}
		
		return target;
	}	

	/**
	 * Annulla l'eliminazione di un soggetto impresa
	 */
	@SkipValidation
	public String cancelUnlock() {
		getRequest().getSession().removeAttribute(ID_SESSIONE_DELEGATO_IMPRESA);
		return SUCCESS;
	}
	
	/**
	 * restituisce se l'utente corrente e' soggetto OWNER o meno  
	 */
	private boolean isOwner() {
		boolean isOwner = false;
		if(AccountSSO.isAccessiDistinti() && getCurrentUser() != null) {
			AccountSSO accountSSO = AccountSSO.getFromSession();
			isOwner = (accountSSO != null && accountSSO.getProfilo() != null 
					   ? accountSSO.getProfilo().isOwner() 
					   : false);
		}
		return isOwner;
	}
	
	/**
	 * ... 
	 */
	private int getIdSoggetto(DelegateUser soggetto) {
		int id = -1;
		if(delegatiImpresa != null)   
			for(int i = 0; i < delegatiImpresa.size(); i++) 
				if(delegatiImpresa.get(i).getUsername().equalsIgnoreCase(idUtente)) {
					id = i;
					break;
				}
		return id;
	}
	
	/**
	 * imposta i dati della pagina dato un soggetto impresa
	 */
	private void setInfoSoggettoImpresa(DelegateUser soggetto) {
		this.id = null;
		this.idUtente = (soggetto != null ? soggetto.getDelegate() : null);
		this.cognomeNome = (soggetto != null ? soggetto.getDescription() : null);
		this.profilo = (soggetto != null ? soggetto.getRolename().name() : null);
		this.email = (soggetto != null ? soggetto.getEmail() : null);
	}
	
	private void setInfoSoggettoImpresa(int id) {
		DelegateUser soggetto = (id >= 0 ? delegatiImpresa.get(id) : null);
		setInfoSoggettoImpresa(soggetto);
		this.id = (id >= 0 ? new Long(id) : null);
	}

//	/**
//	 * carica il profilo del soggetto impresa selezionato
//	 */
//	private DelegateUser _loadDelegato(String idUtente) throws ApsSystemException {
//		DelegateUser soggetto = userManager.getProfiloSSO(this.getCurrentUser().getUsername(), idUtente);
//		setInfoSoggettoImpresa(soggetto);
//		return soggetto;
//	}
	
	/**
	 * crea un profilo per un soggetto impresa  
	 */
	private DelegateUser buildDelegateUser(DelegateUser delegate) {
		if(delegate == null) {
			// crea un soggetto impresa nuovo
			delegate = new DelegateUser();
		}
		delegate.setOwner(false);
		delegate.setUsername(this.getCurrentUser().getUsername());
		delegate.setDelegate(idUtente);
		delegate.setDescription(cognomeNome);
		delegate.setRolename(DelegateUser.valueOfDefault(profilo, DelegateUser.Accesso.READONLY));
		delegate.setEmail((StringUtils.isNotEmpty(email) ? email : null));
		return delegate;
	}

	/**
	 * restituisce l'elenco dei lock di funzione in corso
	 */
	private List<DelegateUser> getListaSessioni() throws ApsSystemException {
		List<DelegateUser> lista = userManager.loadProfiliSSOAccesses(this.getCurrentUser().getUsername());
		return lista.stream()
				.filter(d -> !"LOGIN".equalsIgnoreCase(d.getFlusso()))
				.collect(Collectors.toList()); 
	}

	/**
	 * invia una mail di notifica di variazione del profilo al soggetto impresa autorizzato
	 */
	private void sendNotification(DelegateUser delegate, int type) {
		try {
			AccountSSO soggettoSSO = (AccountSSO)session.get(CommonSystemConstants.SESSION_OBJECT_ACCOUNT_SSO);
			
			String baseUrl = configManager.getParam(SystemConstants.PAR_APPL_BASE_URL);

			// recupera la ragione sociale dell'operatore economico 
			WizardDatiImpresaHelper impresa = ImpresaAction.getLatestDatiImpresa(
					getCurrentUser().getUsername()
					, null
					, getRequest()
			);	
			String ragioneSociale = (impresa != null && impresa.getDatiPrincipaliImpresa() != null 
					? impresa.getDatiPrincipaliImpresa().getRagioneSociale()
					: null
			);
			
			if(StringUtils.isNotEmpty(ragioneSociale)) {
				String subject = null;
				String text = null; 
				if(type == NOTIFY_ADD || type == NOTIFY_UPDATE) {
					// inserimento o modifica di un profilo 
					subject = formatI18nLabel("MAIL_OGGETTO_NOTIFICA_AGGIORNAMENTO_SSO", ragioneSociale);
					text = formatI18nLabel("MAIL_TESTO_NOTIFICA_AGGIORNAMENTO_SSO" 
											, delegate.getDescription(), delegate.getRolename().name(), ragioneSociale, baseUrl);
				} else if(type == NOTIFY_REMOVE) {
					// eliminazione di un profilo
					subject = formatI18nLabel("MAIL_OGGETTO_NOTIFICA_RIMOZIONE_SSO", ragioneSociale);
					text = formatI18nLabel("MAIL_TESTO_NOTIFICA_RIMOZIONE_SSO"
											, delegate.getDescription(), ragioneSociale, baseUrl);
				}
				
				mailManager.sendMail(
						text, 
						subject, 
						new String[] { delegate.getEmail() },
						null, 
						null, 
						CommonSystemConstants.SENDER_CODE);
			}			
		} catch(Throwable t) {
			ApsSystemUtils.getLogger().error(
					"Per errori durante la connessione al server di posta, non e' stato possibile inviare al soggetto impresa {0} " +
					"la variazione del profilo di accesso SSO per l'impresa {1}.",
					new Object[] { delegate.getDelegate(), delegate.getUsername() });
			ApsSystemUtils.logThrowable(t, this, "sendNotification");
		}
	}

}