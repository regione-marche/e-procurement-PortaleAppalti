package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.areapers;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.user.IUserManager;
import com.agiletec.aps.system.services.user.User;
import com.agiletec.aps.system.services.user.UserDetails;

import it.eldasoft.sil.portgare.datatypes.DatiImpresaDocument;
import it.eldasoft.www.WSOperazioniGenerali.AllegatoComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.CustomConfigManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.ComunicazioniUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.ImpresaAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.regimpresa.WizardRegistrazioneImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.apache.xmlbeans.XmlObject;

import java.util.Date;

/**
 * Azione per l'accettazione dei consensi 
 * 
 * @author 
 */
public class AccettazioneConsensiAction extends EncodedDataAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -2405136904293281445L;
	
	private IBandiManager bandiManager;
	private IComunicazioniManager comunicazioniManager;
	private IUserManager userManager;
	private IAppParamManager appParamManager;
	private CustomConfigManager customConfigManager;
	private IEventManager eventManager;
	
	private WizardRegistrazioneImpresaHelper datiImpresa;
	@Validate(EParamValidation.GENERIC)
	private String soggettoRichiedente;
	@Validate(EParamValidation.USERNAME)
	private String username;
	@Validate(EParamValidation.RAGIONE_SOCIALE)
	private String ragioneSociale;
	@Validate(EParamValidation.CODICE_FISCALE)
	private String codiceFiscale;
	private Integer utilizzoPiattaforma;
	private Integer consensoExtra;
	private Integer privacy;
	private Boolean visConsensoPrivacy;	
	private Boolean soggettiRichiedentiPresenti;

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}

	public void setUserManager(IUserManager userManager) {
		this.userManager = userManager;
	}

	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	public void setCustomConfigManager(CustomConfigManager customConfigManager) {
		this.customConfigManager = customConfigManager;
	}

	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}

	public WizardRegistrazioneImpresaHelper  getDatiImpresa() {
		return datiImpresa;
	}

	public void setDatiImpresa(WizardRegistrazioneImpresaHelper datiImpresa) {
		this.datiImpresa = datiImpresa;
	}

	public String getSoggettoRichiedente() {
		return soggettoRichiedente;
	}

	public void setSoggettoRichiedente(String soggettoRichiedente) {
		this.soggettoRichiedente = soggettoRichiedente;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getRagioneSociale() {
		return ragioneSociale;
	}

	public void setRagioneSociale(String ragioneSociale) {
		this.ragioneSociale = ragioneSociale;
	}

	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	public Integer getUtilizzoPiattaforma() {
		return utilizzoPiattaforma;
	}

	public void setUtilizzoPiattaforma(Integer utilizzoPiattaforma) {
		this.utilizzoPiattaforma = utilizzoPiattaforma;
	}

	public Integer getConsensoExtra() {
		return consensoExtra;
	}

	public void setConsensoExtra(Integer consensoExtra) {
		this.consensoExtra = consensoExtra;
	}

	public Boolean getVisConsensoPrivacy() {
		return visConsensoPrivacy;
	}

	public void setVisConsensoPrivacy(Boolean visConsensoPrivacy) {
		this.visConsensoPrivacy = visConsensoPrivacy;
	}

	public Integer getPrivacy() {
		return privacy;
	}

	public void setPrivacy(Integer privacy) {
		this.privacy = privacy;
	}

	public Boolean getSoggettiRichiedentiPresenti() {
		return soggettiRichiedentiPresenti;
	}

	public void setSoggettiRichiedentiPresenti(Boolean soggettiRichiedentiPresenti) {
		this.soggettiRichiedentiPresenti = soggettiRichiedentiPresenti;
	}

	/**
	 * Predispone l'apertura della pagina previo controllo autorizzazione
	 * 
	 * @return target struts di esito dell'operazione
	 */
	@SkipValidation
	public String open() {
		String target = SUCCESS;
		
		if (this.getCurrentUser() == null) {
			this.addActionError(this.getText("Errors.function.notEnabled"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			try {
				this.username = (String)this.getRequest().getSession().getAttribute(SystemConstants.SESSION_ACCETTAZIONE_CONSENSI);
				
				// verifica se e' stata appena eseguito il wizard dei dati impresa per modificare/completare i dati
				// recupera i dati dall'ultima FS5 inviata...
				this.datiImpresa = ImpresaAction.getLatestDatiImpresa(this.username, this.getRequest());
				
				// ...altrimenti recupera i dati dal B.O.
				if(this.datiImpresa == null) {
					DatiImpresaDocument impresa = this.bandiManager.getDatiImpresa(this.username, null);
					this.datiImpresa = new WizardRegistrazioneImpresaHelper(impresa.getDatiImpresa());
				}
				
				// verifica se e' sono presenti die "soggetto richiedenti" selezionabili...
				this.soggettiRichiedentiPresenti = false;
				
			} catch (Throwable t) {
				ApsSystemUtils.getLogger().error("AccettazioneConsensiAction", "open", t);
				this.addActionError(this.getText("Errors.function.notEnabled"));
				target = CommonSystemConstants.PORTAL_ERROR;
			}
		}
		return target;
	}
	
	@Override
    public void validate() {
		super.validate();
		
		try {
		    if(this.customConfigManager.isVisible("REG-IMPRESA", "CONSENSO-EXTRA")) {
		    	Integer consExtra = (this.consensoExtra != null && this.consensoExtra.intValue() == 1 ? 1 : 0);
		    	if(consExtra != 1) {
			    	this.addFieldError(
			    			this.getI18nLabel("LABEL_CONSENSO_EXTRA", this.getLocale().getLanguage()),
			    			this.getI18nLabel("LABEL_CONSENSO_EXTRA", this.getLocale().getLanguage()) +": " + this.getText("Errors.mandatory"));
		    	}
		    }
		} catch (Throwable t) {
		    throw new RuntimeException(
			    "Error validation of request for account registration"
				    + this.getUsername(), t);
		}
    }
	
	/**
	 * conferma l'accettazione dei nuovi consensi ed invia
	 * una nuova comunicazione FS1 per registrare la conferma
	 */
	public String accettaConsensi() {
		String target = SUCCESS;

		Event evento = new Event();
        evento.setLevel(Event.Level.INFO);
        evento.setEventType(PortGareEventsConstants.ACCETTAZIONE_CONSENSI);
        evento.setMessage("Accettazione consensi per l'utente " + this.username);
        evento.setSessionId(this.getRequest().getSession().getId());
        evento.setUsername(this.username);      
        evento.setIpAddress(this.getCurrentUser().getIpAddress());

        boolean removeFromSession = false;
		try {
			Integer version = (Integer) this.appParamManager.getConfigurationValue(AppParamManager.CLAUSOLEPIATTAFORMA_VERSIONE);
			evento.setMessage("Accettazione consensi " + version + " per l'utente " + this.username);
			
			DatiImpresaDocument impresa = this.bandiManager.getDatiImpresa(this.username, null);
			this.datiImpresa = new WizardRegistrazioneImpresaHelper(impresa.getDatiImpresa());
			
			User user = (User) this.userManager.getUser(this.username);
				
			if (user != null) {
				user.setAcceptanceVersion(version);
				user.setLastAccess(new Date());
				this.userManager.updateUser(user);
				
				this.sendComunicazione();

				removeFromSession = true;
				
				// si definisce un esito da inviare alla pagina
				String msgInfo = this.getText("Info.accettazioneConsensi.confirmed",
						new String[] { this.getUsername() });
				this.addActionMessage(msgInfo);
			} else {
				// se l'utente non esiste allora si genera un errore
				evento.setLevel(Event.Level.ERROR);
				evento.setDetailMessage(this.getTextFromDefaultLocale("Errors.user.notFound"));
				this.addActionError(this.getText("Errors.user.notFound"));
				target = INPUT;
			}
		} catch (Throwable t) {
			evento.setError(t);
			this.addActionError(this.getText("Errors.user.notFound"));
			target = INPUT;
		} finally {
			this.eventManager.insertEvent(evento);
		}
		
		if(removeFromSession) {
			this.getRequest().getSession().removeAttribute(SystemConstants.SESSION_ACCETTAZIONE_CONSENSI);		
		}

		return target;
	}

    /**
     * Compone i dati da inviare nella comunicazione ed invia la richiesta al
     * backoffice
     * @throws Exception 
     */
	private void sendComunicazione() throws Exception {
		/*
		pulsante "Conferma" che, se selezionato, crea un nuovo flusso in cui si trasmette
		l'accettazione delle nuove condizioni (una FS1 da porre in stato 6? oppure 
		un nuovo flusso, da decidere il codice, e soprattutto lo stato, visto che 
		lato backoffice non verrebbe processato) e si aggiorna authusers.acceptance_version 
		con il valore della configurazione; andrebbe anche tracciato un evento 
		che riporti l'accettazione della nuova versione delle clausole
		*/
		ComunicazioneType comunicazione = null;
		
		// FASE 1: 
		// estrazione dei parametri necessari per i dati di testata della comunicazione
		String statoComunicazione = CommonSystemConstants.STATO_COMUNICAZIONE_ACCETTAZIONE_CONSENSI;
		
		Event evento = new Event();
		evento.setUsername(this.username);
		evento.setIpAddress(this.getCurrentUser().getIpAddress());
		evento.setSessionId(this.getRequest().getSession().getId());
		evento.setEventType(PortGareEventsConstants.INVIO_COMUNICAZIONE_DA_PROCESSARE);
		evento.setLevel(Event.Level.INFO);
		evento.setMessage("Invio comunicazione accettazione consensi "
				+ PortGareSystemConstants.RICHIESTA_REGISTRAZIONE_PORTALE
				+ " in stato " + statoComunicazione);

		// Invio della comunicazione
		try {
			DatiImpresaDocument impresa = this.bandiManager.getDatiImpresa(this.username, null);
			WizardRegistrazioneImpresaHelper helper = new WizardRegistrazioneImpresaHelper(impresa.getDatiImpresa());
			helper.setUsername(this.username);
			helper.setPrivacy(this.privacy);
			helper.setUtilizzoPiattaforma(this.utilizzoPiattaforma);
			helper.setConsensoExtra(this.consensoExtra);
			helper.setSoggettoRichiedente(this.soggettoRichiedente);
			
			// FASE 2:
			// crea un nuova comunicazione per la registrazione
			comunicazione = new ComunicazioneType();
			DettaglioComunicazioneType dettaglioComunicazione = ComunicazioniUtilities
					.createDettaglioComunicazione(
							null,
							this.username,
							null,
							null,
							helper.getDatiPrincipaliImpresa().getRagioneSociale(),
							statoComunicazione,
							this.getTextFromDefaultLocale("label.registrazione.oggetto"),
							this.getTextFromDefaultLocale("label.registrazione.testo", StringUtils.left(helper.getDatiPrincipaliImpresa().getRagioneSociale(), 200)),
							PortGareSystemConstants.RICHIESTA_REGISTRAZIONE_PORTALE,
							null);
			comunicazione.setDettaglioComunicazione(dettaglioComunicazione);
			
			// FASE 3: 
			// creazione e popolamento dell'allegato con l'xml dei dati da inviare
	    	XmlObject xml = helper.getXmlDocumentRegistrazione(this.getI18nManager(), this.getLocale());
	    	
			AllegatoComunicazioneType allegato = ComunicazioniUtilities.createAllegatoComunicazione(
					PortGareSystemConstants.NOME_FILE_REGISTRAZIONE,
					this.getTextFromDefaultLocale("label.registrazione.allegatoRegistrazione.descrizione"),
					xml.toString().getBytes());
			comunicazione.setAllegato(new AllegatoComunicazioneType[] { allegato });
			
			// FASE 4: 
			// invio comunicazione
			comunicazione.getDettaglioComunicazione()
				.setId(this.comunicazioniManager.sendComunicazione(comunicazione));
			
			evento.setMessage("Invio comunicazione accettazione consensi "
					+ PortGareSystemConstants.RICHIESTA_REGISTRAZIONE_PORTALE
					+ " con id " + comunicazione.getDettaglioComunicazione().getId()
					+ " in stato " + comunicazione.getDettaglioComunicazione().getStato());
			
		} catch (ApsException e) {
			evento.setError(e);
			throw e;
		} catch (Throwable e) {
			evento.setError(e);
			throw new ApsException("Errore inaspettato in registrazione impresa", e);
		} finally {
			this.eventManager.insertEvent(evento);
		}
	}

}
