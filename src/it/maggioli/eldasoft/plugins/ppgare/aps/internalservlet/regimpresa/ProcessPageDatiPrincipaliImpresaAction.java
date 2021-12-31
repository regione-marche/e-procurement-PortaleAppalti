package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.regimpresa;

import org.apache.commons.lang.StringUtils;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;

import it.eldasoft.utils.utility.UtilityFiscali;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.InterceptorEncodedData;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.datiimpresa.DatiImpresaChecker;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.regimpresa.IRegistrazioneImpresaManager;

/**
 * Action di gestione delle operazioni nella pagina dei dati principali
 * dell'impresa nel wizard di registrazione di un'impresa
 * 
 * @author Stefano.Sabbadin
 * @since 1.2
 */
public class ProcessPageDatiPrincipaliImpresaAction
	extends it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.ProcessPageDatiPrincipaliImpresaAction {
    /**
     * UID
     */
    private static final long serialVersionUID = 5426584774628243103L;

    private IBandiManager bandiManager;
    private IRegistrazioneImpresaManager registrazioneImpresaManager;
	private IEventManager eventManager;
    
    private String tipoImport;
    
    private boolean checkRecuperaRegistrazione;
    
    public void setBandiManager(IBandiManager manager) {
    	bandiManager = manager;
    }
    
	public void setRegistrazioneImpresaManager(IRegistrazioneImpresaManager registrazioneImpresaManager) {
		this.registrazioneImpresaManager = registrazioneImpresaManager;
	}

	/**
	 * @param eventManager the eventManager to set
	 */
	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}
	
	public String getTipoImport() {
		return tipoImport;
	}

	public void setTipoImport(String tipoImport) {
		this.tipoImport = tipoImport;
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
    	
		//if (this.getFieldErrors().size() > 0) return;
		try {
			// il processo di registrazione viene immediatamente deviato al processo
			// di recover utente se rientra nella casistica di operatore registrato
			// in B.O. ma stranamente non piu' presente nel portale.
	    	if (this.isRecuperaRegistrazione()) {
				return;
			}
			
			super.validate();

			// controllo la partita iva, che puo essere facoltativa per il
			// libero professionista o l'impresa sociale se previsto da
			// configurazione, mentre per tutti gli altri casi risulta
			// sempre obbligatoria
			boolean isLiberoProfessionista = this
					.getCodificheManager()
					.getCodifiche()
					.get(InterceptorEncodedData.LISTA_TIPI_IMPRESA_LIBERO_PROFESSIONISTA)
					.containsKey(this.getTipoImpresa());
			boolean isImpresaSociale = this
					.getCodificheManager()
					.getCodifiche()
					.get(InterceptorEncodedData.LISTA_TIPI_IMPRESA_SOCIALE)
					.containsKey(this.getTipoImpresa());
			boolean isPartitaIVAObbligatoriaLiberoProfessionista = !this
					.getCodificheManager().getCodifiche()
					.get(InterceptorEncodedData.CHECK_PARTITA_IVA_FACOLTATIVA_LIBERO_PROFESSIONISTA)
					.containsValue("1");
			boolean isPartitaIVAObbligatoriaImpresaSociale = !this
					.getCodificheManager().getCodifiche()
					.get(InterceptorEncodedData.CHECK_PARTITA_IVA_FACOLTATIVA_IMPRESA_SOCIALE)
					.containsValue("1");

			boolean OEItaliano = "1".equalsIgnoreCase(this.getAmbitoTerritoriale());
			boolean impresaItaliana = "ITALIA".equalsIgnoreCase(this.getNazioneSedeLegale());			
			
			if(OEItaliano) {
				// verifica CF e PIVA solo in caso di operatore economico italiano
				
				if (this.getPartitaIVA().length() == 0 
						&& ((!isLiberoProfessionista && !isImpresaSociale)
								|| (isLiberoProfessionista && isPartitaIVAObbligatoriaLiberoProfessionista)
								|| (isImpresaSociale && isPartitaIVAObbligatoriaImpresaSociale))) {
					this.addFieldError(
							"partitaIVA",
							this.getText("Errors.requiredstring",
									new String[] { this.getTextFromDB("partitaIVA") }));
				}
				
				if (!"".equals(this.getCodiceFiscale())
						&& !((UtilityFiscali.isValidCodiceFiscale(this.getCodiceFiscale(), impresaItaliana)) 
								|| (UtilityFiscali.isValidPartitaIVA(this.getCodiceFiscale(), impresaItaliana)))) {
					if (impresaItaliana) {
						this.addFieldError(
								"codiceFiscale",
								this.getText("Errors.wrongField",
										new String[] { this.getTextFromDB("codiceFiscale") }));					
					} else {
						this.addFieldError(
								"codiceFiscale",
								this.getText("Errors.wrongForeignFiscalField",
										new String[] { this.getTextFromDB("codiceFiscale") }));					
					}
				}
	
				if (!"".equals(this.getCodiceFiscale())
						&& this._datiImpresaChecker.existsCodFiscale(this.getCodiceFiscale())) {
					this.addFieldError("codiceFiscale",
							this.getText("Errors.duplicateCF"));
				}
				
				if (!"".equals(this.getPartitaIVA())
						&& !UtilityFiscali.isValidPartitaIVA(this.getPartitaIVA(), impresaItaliana)) {
					if (impresaItaliana) {
						this.addFieldError(
								"partitaIVA",
								this.getText("Errors.wrongField",
										new String[] { this.getTextFromDB("partitaIVA") }));
					} else {
						this.addFieldError(
								"partitaIVA",
								this.getText("Errors.wrongForeignFiscalField",
										new String[] { this.getTextFromDB("partitaIVA") }));
					}
				}
				if (!"".equals(this.getPartitaIVA())
						&& this._datiImpresaChecker.existsPartitaIVA(this.getPartitaIVA())) {
					this.addFieldError("partitaIVA",
							this.getText("Errors.duplicatePI"));
				}
				if (this.getPartitaIVA().length() == 0
						&& ((isLiberoProfessionista && !isPartitaIVAObbligatoriaLiberoProfessionista) || (isImpresaSociale && !isPartitaIVAObbligatoriaImpresaSociale))) {
					// se siamo nel caso di libero professionista o impresa sociale
					// senza obbligo di indicare la partita IVA, allora controllo
					// solo mediante codice fiscale
					if (this.bandiManager.isImpresaRegistrata(
							this.getCodiceFiscale(), null).booleanValue()) {
						this.addFieldError("codiceFiscale",
								this.getText("Errors.impresaAlreadyPresent"));
					}
				} else {
					// altrimenti controllo la presenza in backoffice quando ho entrambe i dati valorizzati
					// (sono comunque obbligatori entrambe)
					if (!"".equals(this.getCodiceFiscale())
							&& !"".equals(this.getPartitaIVA())
							&& this.bandiManager.isImpresaRegistrata(
									this.getCodiceFiscale(), 
									this.getPartitaIVA()).booleanValue()) {
						this.addFieldError("codiceFiscale",
								this.getText("Errors.impresaAlreadyPresent"));
					}
				}
			}

		} catch (Throwable t) {
			throw new RuntimeException(
					"Errore durante la verifica dei dati richiesti per l'impresa "
							+ this.getRagioneSociale(), t);
		}
	}
    	
    /**
     * Verifica se esiste già una registrazione incompleta per un'impresa
     * (Es: dati presenti in BO ma cancellati lato Portale).
     * 
     * @return true se l'impresa va recuperata, false altrimenti
     * @throws ApsException 
     */
    private boolean isRecuperaRegistrazione() throws ApsException {
    	// se recupero un utente allora l'operatore risulta registrato in b.o.
		String username = this.bandiManager.getImpresaRegistrata(
				this.getCodiceFiscale(),
				this.getPartitaIVA(),
				this.getEmailRecapito(),
				this.getEmailPECRecapito());
		// se recupero la mail di riferimento allora l'operatore e' presente anche a portale
		boolean esisteMail = this._datiImpresaChecker.existsEmail(this.getMail());
		// recupero la registrazione SOLO SE l'operatore c'e' in B.O. e risulta registrato, ma non c'e' su portale
		this.checkRecuperaRegistrazione = (StringUtils.isNotEmpty(username) && !esisteMail);
		return this.checkRecuperaRegistrazione;
    }
    
    /**
     * "Avanti" nella form di compilazione dei dati impresa  
     */
	@Override
	public String next() {
		String target = SUCCESS;
		
		// verifica se recuperare i dati impresa 
		// o continuare con il secondo step della registrazione
		// dati dati impresa...
		if(this.checkRecuperaRegistrazione) {
			// vai al recovery della registrazione precedente
			target = "recover";
		} else {
			// vai al secondo step della registrazione impresa
			target = super.next();
		}
		
		return target;
	}
	
	/**
     * Recover di una registrazione impresa presente in BO come registrata ma sparita da portale (es. token scaduto).
     */
	public String questionRecovery() {
		String target = SUCCESS;
		return target;
	}
	
	/**
     * Annullo il processo di recovery e di registrazione.
     */
	public String cancelRecovery() {
		String target = "cancel";
		this.session.remove(PortGareSystemConstants.SESSION_ID_DETT_REGISTRAZIONE_IMPRESA);
		this.session.remove(PortGareSystemConstants.SESSION_ID_PAGINA);
		return target;
	}
	
    /**
     * Confermo il processo di recovery: se va tutto bene 
     */
	public String confirmRecovery() {
		String target = SUCCESS;
		
		String username = null;
		
		try {		
			username = this.bandiManager.getImpresaRegistrata(
					this.getCodiceFiscale(),
					this.getPartitaIVA(),
					this.getEmailRecapito(),
					this.getEmailPECRecapito());
			
			if(StringUtils.isEmpty(username)) {
				this.addActionError(this.getText("Errors.recoverRegistrazione.userNotFound"));
				target = CommonSystemConstants.PORTAL_ERROR;
			} else {
				Event evento = new Event();
				evento.setUsername(username);
				evento.setIpAddress(this.getCurrentUser().getIpAddress());
				evento.setSessionId(this.getRequest().getSession().getId());
				evento.setEventType(PortGareEventsConstants.CREAZIONE_ACCOUNT);
				evento.setLevel(Event.Level.INFO);
				String message = "Ripristino registrazione account per impresa con mail di riferimento "
						+ this.getMail()
						+ ", codice fiscale "
						+ this.getCodiceFiscale()
						+ ", partita IVA "
						+ this.getPartitaIVA();
				evento.setMessage(message);
				
				// reinserisce l'utente
				EsitoOutType inserimento = this.registrazioneImpresaManager.insertImpresa(
						username, 
						this.getEmailRecapito(), 
						this.getEmailPECRecapito(), 
						this.getRagioneSociale(), 
						this.getCodiceFiscale(), 
						this.getPartitaIVA());
				
				if (!inserimento.isEsitoOk()) {
					String msgErrore = this.getText("Errors.recoverRegistrazione.reinsertError", new String[]{username, inserimento.getCodiceErrore()});
					this.addActionError(msgErrore);
					target = CommonSystemConstants.PORTAL_ERROR;
					evento.setLevel(Event.Level.ERROR);
					evento.setDetailMessage(inserimento.getCodiceErrore());
				}
				this.eventManager.insertEvent(evento);
			}
		} catch (Throwable e) {
			this.addActionError(this.getText("Errors.recoverRegistrazione.unexpectedError", new String[] {username != null ? username : this.getRagioneSociale()}));
			ApsSystemUtils.logThrowable(e, this, "confirmRecovery");
			target = CommonSystemConstants.PORTAL_ERROR;
		}
		
		return target;
	}
	
	/**
	 * Metodo implementato per ritornare la mail a cui &egrave; stato inviato
	 * con successo il token in caso di recover utente.
	 * 
	 * @return indirizzo email
	 */
	public String getMail() {
		return DatiImpresaChecker.getEmailRiferimento(
				this.getEmailPECRecapito(), this.getEmailRecapito());
	}

    /**
     * Estrae l'helper del wizard dati dell'impresa da utilizzare nei controlli
     * 
     * @return helper contenente i dati dell'impresa
     */
    protected WizardDatiImpresaHelper getSessionHelper() {
		WizardDatiImpresaHelper helper = (WizardDatiImpresaHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_REGISTRAZIONE_IMPRESA);
		return helper;
    }
        
}
