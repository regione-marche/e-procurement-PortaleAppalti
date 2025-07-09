package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando;

import com.agiletec.aps.system.SystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.codifiche.ICodificheManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomConfigManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.IDatiPrincipaliImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo.WizardIscrizioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.validation.SkipValidation;
import java.math.BigDecimal;

/**
 * Action di gestione delle operazioni nella pagina dei componenti di una RTI o
 * di un consorzio.
 *
 * @author Stefano.Sabbadin
 */
@FlussiAccessiDistinti({ 
	EFlussiAccessiDistinti.OFFERTA_GARA, 
	EFlussiAccessiDistinti.ISCRIZIONE_ELENCO, EFlussiAccessiDistinti.RINNOVO_ELENCO, 
	EFlussiAccessiDistinti.ISCRIZIONE_CATALOGO, EFlussiAccessiDistinti.RINNOVO_CATALOGO
	})
public class ProcessPageComponentiAction extends AbstractProcessPageAction implements IComponente {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -7527283479159496491L;

	public static final int QUOTA_MAX_DECIMALS = 3;
	
	private ICodificheManager _codificheManager;
	private ICustomConfigManager customConfigManager;

	@Validate(EParamValidation.DIGIT)
	private String id;
	@Validate(EParamValidation.DIGIT)
	private String idDelete;
	
	/** Valorizzato quando si conferma di proseguire senza indicazione delle consorziate. */
	@Validate(EParamValidation.GENERIC)
	private String noConsorziate;

	@Validate(EParamValidation.RAGIONE_SOCIALE)
	private String ragioneSociale;
	@Validate(EParamValidation.TIPO_IMPRESA)
	private String tipoImpresa;
	@Validate(EParamValidation.AMBITO_TERRITORIALE)
	private String ambitoTerritoriale;		// 1=operatore italiano, 2=operatore UE o extra UE
	@Validate(EParamValidation.NAZIONE)
	private String nazione;
	@Validate(EParamValidation.CODICE_FISCALE)
	private String codiceFiscale;			// Codice fiscale consorziata o mandante
	@Validate(EParamValidation.PARTITA_IVA)
	private String partitaIVA;				// Partita IVA consorziata o mandante
	@Validate(EParamValidation.GENERIC)
	private String idFiscaleEstero;			// identificativo fiscale estero 
	private Double quota;					// Quota di partecipazione della mandante alla RTI nella gara
	@Validate(EParamValidation.IMPORTO)
	private String strQuota; 				// Quota acquisita in formato stringa
		
	private boolean delete;

	/** Viene impostato quando le consorziate esecutrici non sono obbligatorie e viene chiesto conferma di proseguire. */
	private boolean confirmNoConsorziate;

	@Validate(EParamValidation.IMPORTO)
	private String strQuotaRTI;				// Quota RTI della mandataria acquisita in formato stringa.
	private Double quotaRTI;				// Quota di partecipazione della mandataria dell'RTI nella gara.
	
	private boolean quotaVisibile;			// campi nascosti per la validazione di quota e quotaRTI
	private boolean quotaRTIVisibile;		// campi nascosti per la validazione di quota e quotaRTI
	private boolean readOnly;				//Caso: Concorso di progettazione, i dati vengono inseriti dalla FS11 del concorso di primo grado
	

	public void setCodificheManager(ICodificheManager manager) {
		this._codificheManager = manager;
	}

	public ICodificheManager getCodificheManager() {
		return _codificheManager;
	}
	
	public ICustomConfigManager getCustomConfigManager() {
		return customConfigManager;
	}

	public void setCustomConfigManager(ICustomConfigManager customConfigManager) {
		this.customConfigManager = customConfigManager;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIdDelete() {
		return idDelete;
	}

	public void setIdDelete(String idDelete) {
		this.idDelete = idDelete;
	}
	
	public String getNoConsorziate() {
		return noConsorziate;
	}

	public void setNoConsorziate(String noConsorziate) {
		this.noConsorziate = noConsorziate;
	}

	@Override
	public String getRagioneSociale() {
		return ragioneSociale;
	}

	@Override
	public void setRagioneSociale(String ragioneSociale) {
		this.ragioneSociale = ragioneSociale;
	}

	@Override
	public String getTipoImpresa() {
		return tipoImpresa;
	}

	@Override
	public void setTipoImpresa(String tipoImpresa) {
		this.tipoImpresa = tipoImpresa;
	}

	@Override
	public String getAmbitoTerritoriale() {
		return ambitoTerritoriale;
	}

	@Override
	public void setAmbitoTerritoriale(String ambitoTerritoriale) {
		this.ambitoTerritoriale = ambitoTerritoriale;
	}

	@Override
	public String getNazione() {
		return nazione;
	}

	@Override
	public void setNazione(String nazione) {
		this.nazione = nazione;
	}

	@Override
	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	@Override
	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	@Override
	public String getPartitaIVA() {
		return partitaIVA;
	}

	@Override
	public void setPartitaIVA(String partitaIVA) {
		this.partitaIVA = partitaIVA;
	}

	@Override
	public String getIdFiscaleEstero() {
		return this.idFiscaleEstero;
	}

	@Override
	public void setIdFiscaleEstero(String idFiscaleEstero) {
		this.idFiscaleEstero = idFiscaleEstero;
	}

	@Override
	public Double getQuota() {
		return quota;
	}

	@Override
	public void setQuota(Double quota) {
		this.quota = quota;
	}

	public String getStrQuota() {
		return strQuota;
	}

	public void setStrQuota(String strQuota) {
		this.strQuota = strQuota;
	}

	public boolean isDelete() {
		return delete;
	}

	public boolean isConfirmNoConsorziate() {
		return confirmNoConsorziate;
	}
	
	public void setConfirmNoConsorziate(boolean confirmNoConsorziate) {
		this.confirmNoConsorziate = confirmNoConsorziate;
	}

	public String getStrQuotaRTI() {
		return strQuotaRTI;
	}

	public void setStrQuotaRTI(String strQuotaRTI) {
		this.strQuotaRTI = strQuotaRTI;
	}

	public Double getQuotaRTI() {
		return quotaRTI;
	}

	public void setQuotaRTI(Double quotaRTI) {
		this.quotaRTI = quotaRTI;
	}

	public boolean isQuotaVisibile() {
		return quotaVisibile;
	}

	public void setQuotaVisibile(boolean quotaVisibile) {
		this.quotaVisibile = quotaVisibile;
	}

	public boolean isQuotaRTIVisibile() {
		return quotaRTIVisibile;
	}

	public void setQuotaRTIVisibile(boolean quotaRTIVisibile) {
		this.quotaRTIVisibile = quotaRTIVisibile;
	}
	
	public boolean isReadOnly() {
		return readOnly;
	}
	
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	/**
	 * Ritorna l'helper in sessione utilizzato per la memorizzazione dei dati
	 * sulla partecipazione in RTI per le offerte di gara
	 */
	protected IRaggruppamenti getSessionHelper() {
		return GestioneBuste.getPartecipazioneFromSession().getHelper();
	}

	/**
	 * inserisci/aggiorna un componente del raggruppamento RTI 
	 */
	private boolean addComponente(String id) {
		if (this.ragioneSociale.length() <= 0) {
			return false;
		}
		
		boolean insert = (StringUtils.isEmpty(this.id));
		
		WizardPartecipazioneHelper helper = (WizardPartecipazioneHelper) getSessionHelper();
		ComponentiValidator validator = new ComponentiValidator(
				helper, 
				helper.getDatiPrincipaliImpresa(),
				this);
				
		// dati del componente inserito/modificato
		IComponente componente = (insert
				? new ComponenteHelper(this)
				: helper.getComponenti().get(Integer.parseInt(this.id))
		);
		
		// per le offerte, verifica che le imprese ausiliarie non compaiano gia' nell'RTI
		IImpresa impresa = null;
		if(componente != null) {
			impresa = validator.isComponentiPresentiInImpreseAusiliarie(componente);
			if(impresa != null) {
				String cf = "2".equals(impresa.getAmbitoTerritoriale()) 
									   ? impresa.getIdFiscaleEstero()
									   : impresa.getCodiceFiscale();
				this.addActionError(this.getText("Errors.impreseAusiliarie.duplicatedCodiceFiscale", new String[] { cf }));
			} else { 
				if(insert) {
					helper.getComponenti().add(componente);
				} else {
					ComponenteHelper.copyTo(this, componente);
				}
			}
		}

		return (impresa == null);
	}

	/**
	 * ... 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void validate() {
		super.validate();
		
		if (readOnly) {
			// Caso readOnly, quindi, l'utente si ritrova gia' con i dati valorizzati
			clearFieldErrors();
		} else {
			try {
				// VALIDAZIONE E CONTROLLI CAMPI
				boolean controlliOK = true;
				
				IRaggruppamenti helper = getSessionHelper();
				IDatiPrincipaliImpresa datiImpresa = (helper instanceof WizardIscrizioneHelper
						? ((WizardIscrizioneHelper) helper).getDatiPrincipaliImpresa()
						: ((WizardPartecipazioneHelper) helper).getDatiPrincipaliImpresa()
				);
				
				ComponentiValidator validator = new ComponentiValidator(
						helper,
						datiImpresa,
						this);
				
				// controllo la partita iva, che puo' essere facoltativa per il
				// libero professionista ed impresa sociale se previsto da configurazione, mentre per
				// tutti gli altri casi risulta obbligatoria
				controlliOK = controlliOK && validator.validateRequiredInputField(this, id); 
	
				if (this.getFieldErrors().size() > 0) {
					return;
				}

				// CONTROLLI APPLICATIVI SUI DATI
				controlliOK = controlliOK && validator.validateInputRagioneSociale(this, id);
				controlliOK = controlliOK && validator.validateInputCodiceFiscale(this, id); 
				controlliOK = controlliOK && validator.validateInputPartitaIVA(this, id);
				controlliOK = controlliOK && validateInputQuota(this, helper);
				if( !controlliOK ) {
					return;
				}

				// per le offerte, verifica che le imprese ausiliarie non compaiano gia' nell'RTI
				IImpresa impresa = validator.isComponentiPresentiInImpreseAusiliarie();
				if(impresa != null) {
					String cf = "2".equals(impresa.getAmbitoTerritoriale()) 
										   ? impresa.getIdFiscaleEstero()
										   : impresa.getCodiceFiscale();
					this.addActionError(this.getText("Errors.impreseAusiliarie.duplicatedCodiceFiscale",
													 new String[] { cf }));
					return;
				}
				
			} catch (Throwable t) {
				throw new RuntimeException("Errore durante la verifica dei dati richiesti per l'impresa "
						+ this.getRagioneSociale(), t);
			}
		}
	}	
	
	/**
	 * passa al prossimo step del wizard 
	 */
	@Override
	public String next() {
		String target = SUCCESS;
		
		IRaggruppamenti helper = getSessionHelper();
		WizardPartecipazioneHelper partecipazioneHelper = (WizardPartecipazioneHelper) helper;
	
		if(helper != null 
		   && (null != this.getCurrentUser() && !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) ) 
		{
			// sono stati inseriti dei dati per una mandante, si procede al salvataggio
			if (!readOnly && this.ragioneSociale.length() > 0) {
				if( !addComponente(this.id) ) {
					target = INPUT; 
				}
			}
	
			if (helper.checkQuota()) {
				// controlli solo per gare telematiche e non per iscrizioni albo/catalogo
				if (helper.isRti() && StringUtils.isBlank(this.strQuotaRTI)) {
					this.addActionError(this.getText("Errors.requiredstring",
							new String[]{this.getTextFromDB("quotaRTI")}));
					target = "refresh";
				} else if (!helper.isRti() && (StringUtils.isNotBlank(this.strQuotaRTI))) {
					this.addActionError(this.getText("Errors.wrongQuotaRTI"));
					target = "refresh";
				} else if (helper.isRti() && StringUtils.isNotBlank(this.strQuotaRTI)) {
					try {
						this.quotaRTI = Double.parseDouble(this.strQuotaRTI);
					} catch (NumberFormatException ex) {
						this.quotaRTI = -1.0;
					}
					if (this.quotaRTI < 0) {
						this.addActionError(this.getTextFromDB("quotarangeRTI"));
						target = "refresh";
					} else {
						helper.setQuotaRTI(this.quotaRTI);
					}
				}
				
				// verifica il numero massimo di decimali ammessi
				if(!readOnly && !checkNumeroDecimali(this.strQuotaRTI, QUOTA_MAX_DECIMALS) ) {
					this.addActionError(this.getText("quotamaxdecimals", 
							new String[] {Integer.toString(QUOTA_MAX_DECIMALS), this.strQuotaRTI}));
					target = "refresh";
				}
			}

			if (helper.isRti() && helper.getComponenti().size() < 1) {
				this.addActionError(this.getText("Errors.numeroMandantiNonSufficiente"));
				target = "refresh";
			} else if (helper.getImpresa().isConsorzio() && helper.getComponenti().size() < 1) {
				try {
					if (this.getCustomConfigManager().isMandatory("GARE-CONSORZIATE", "ESECUTRICI")) {
						this.addActionError(this.getText("Errors.numeroConsorziateNonSufficiente"));
						target = "refresh";
					} else {
						// caso 0 consorziate esecutrici, configurazione senza obbligo
						if (StringUtils.isEmpty(this.getNoConsorziate())) {
							// entro in questo ramo se ho dato la conferma a proseguire
							this.setConfirmNoConsorziate(true);
							target = "refresh";
						}
					}
				} catch (Exception e) {
					this.addActionError("Missing configuration GARE-CONSORZIATE.ESECUTRICI with feature MAN");
					// il fatto che si faccia il check su una configurazione scritta
					// male deve provocare il fallimento della validazione in modo
					// da accorgermene
					target = CommonSystemConstants.PORTAL_ERROR;
				}
			}
				
			double sommaQuote = helper.getQuotaRTI() != null ? helper.getQuotaRTI() : 0;
			
			// siccome vado avanti, procedo con la verifica dei record in lista
			for (int i = 0; i < helper.getComponenti().size(); i++) {
				IComponente componente = helper.getComponenti().get(i);
				
				if (StringUtils.isBlank(componente.getRagioneSociale())
					|| StringUtils.isBlank(componente.getNazione())
					|| (StringUtils.isBlank(componente.getPartitaIVA()) 
						&& StringUtils.isBlank(componente.getCodiceFiscale())
						&& StringUtils.isBlank(componente.getIdFiscaleEstero()))
					|| (helper.checkQuota() && helper.isRti() && componente.getQuota() == null)
					|| StringUtils.isBlank(componente.getTipoImpresa())) 
				{
					if (helper.isRti()) {
						this.addActionError(this.getText("Errors.datiMandanteIncompleti",
								new String[] {i + 1 + ""}));
					} else {
						this.addActionError(this.getText("Errors.datiConsorziataIncompleti",
								new String[] {i + 1 + ""}));
					}
					target = "refresh";
				} else if (helper.isRti() && componente.getQuota() != null) {
					sommaQuote += componente.getQuota();
				}
			}
			BigDecimal bd = new BigDecimal(sommaQuote).setScale(5, BigDecimal.ROUND_HALF_UP);
			sommaQuote = bd.doubleValue();
	
			// Controllo QUOTA > 100%
			if (SUCCESS.equals(target) && helper.checkQuota() && helper.isRti()) {
				// somma < 100% 	=> errore
				// somma 100% 		=> OK
				// somma > 100% 	=> warning e proseguo
				if(sommaQuote < 100) {
					this.addActionError(this.getText("Errors.quotaComponentiNonRaggiunta"));
					target = "refresh";
				} else if(sommaQuote > 100) {
					// procedi comunque allo step successivo ma visualizza nel nuovo step un messaggio informativo 
					this.addActionMessage(this.getText("Warnings.quotaComponentiSuperiore100"));
				}
			}
			
			if(SUCCESS.equals(target)) {
				String newTarget = partecipazioneHelper.getNextStepTarget(WizardPartecipazioneHelper.STEP_COMPONENTI);
				target = (newTarget != null ? newTarget : target);
			}
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		}
		return target;
	}

	/**
	 * ...
	 */
	@SkipValidation
	public String editConsorziate() {
		return "refresh";
	}
	
	/**
	 * passa allo step precedente del wizard 
	 */
	@Override
	public String back() {
		String target = "back"; 

		WizardPartecipazioneHelper partecipazioneHelper = GestioneBuste.getPartecipazioneFromSession().getHelper();

		if (partecipazioneHelper != null 
			&& (null != this.getCurrentUser() 
				&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME))) 
		{
			String newTarget = partecipazioneHelper.getPreviousStepTarget(WizardPartecipazioneHelper.STEP_COMPONENTI);
			target = (newTarget != null ? newTarget : target);
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		}
		return target;
	}
	
	/**
	 * ... 
	 */
	@SkipValidation
	public String add() {
		return "refresh";
	}

	/**
	 * ... 
	 */
	public String insert() {
		String target = "refresh";
		
		IRaggruppamenti helper = getSessionHelper();
		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			// aggiorna i dati in sessione
			if( !addComponente(null) ) {
				target = INPUT; 
			}
		}
		return target;
	}

	/**
	 * aggiorna i dati di un componente del raggruppamento RTI
	 */
	public String savePartecipazione() {
		String target = "refresh";
		
		WizardPartecipazioneHelper helper = (WizardPartecipazioneHelper) getSessionHelper();
		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			// aggiorna i dati in sessione
			IComponente componente = helper.getComponenti().get(Integer.parseInt(this.id));
			ComponenteHelper.copyTo(this, componente);
		}
		
		return target;
	}

	/**
	 * ...
	 */
	public String save() {
		/*
		 * QUI NON DOVREI MAI ARRIVARCI !!!
		 * IL "save" E' FATTO NELLA ProcessPageComponentiAction.save() !!!
		 */
//		String target = "refresh";
//		WizardIscrizioneHelper helper = (WizardIscrizioneHelper) getSessionHelper();
//		if (helper == null) {
//			// la sessione e' scaduta, occorre riconnettersi
//			this.addActionError(this.getText("Errors.sessionExpired"));
//			target = CommonSystemConstants.PORTAL_ERROR;
//		} else {
//			// aggiorna i dati in sessione
//			if( !addComponente(this.id) ) {
//				target = INPUT; 
//			}
//		}
//		return target;
		return "refresh";
	}

	/**
	 * ... 
	 */
	@SkipValidation
	public String confirmDelete() {
		String target = "refresh";
		this.delete = true;
		return target;
	}

	/**
	 * ... 
	 */
	@SkipValidation
	public String delete() {
		String target = "refresh";
		
		IRaggruppamenti helper = getSessionHelper();
		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			// aggiorna i dati in sessione
			if (this.idDelete != null) {
				helper.getComponenti().remove(Integer.parseInt(this.idDelete));
			}
		}
		
		return target;
	}

	/**
	 * ... 
	 */
	@SkipValidation
	public String modify() {
		return "modify";
	}

	/**
	 * verifica se il numero di decimali di un double eccede il massimo consentito  
	 */
	public boolean checkNumeroDecimali(String value, int maxDecimali) {
		if(StringUtils.isNotEmpty(value)) {
			String decSep = Double.toString(0.1).substring(1, 2);
			int i = value.indexOf(decSep);
			String dec = (i >= 0 ? value.substring(i + 1) : "");
			if(dec.length() > maxDecimali) {
				return false;
			}
		}
		return true;
	}

	/**
	 * valida il campo quota 
	 */
	private boolean validateInputQuota(IComponente form, IRaggruppamenti raggruppamento) {
		// valida il campo quota
		if (raggruppamento.checkQuota()) {
			if(StringUtils.isBlank(this.strQuota)) {
				// quota e' vuoto
				if (raggruppamento.isRti()
					&& (StringUtils.isNotBlank(this.ragioneSociale)
						|| StringUtils.isNotBlank(this.codiceFiscale)
						|| StringUtils.isNotBlank(this.partitaIVA))) 
				{
					this.addFieldError("strQuota", 
							this.getText("Errors.requiredstring", 
										 new String[]{this.getTextFromDB("strQuota")}));
				}
			} else {
				// quota ha un valore
				if(!raggruppamento.isRti()) {
					this.addActionError(this.getText("Errors.wrongQuota"));
				} else if (raggruppamento.isRti()) {
					try {
						this.quota = Double.parseDouble(this.strQuota);
					} catch (NumberFormatException ex) {
						this.quota = -1.0;
					}
					if (this.quota < 0) {
						this.addFieldError("strQuota", this.getTextFromDB("quotarange"));
					}
				}
				
				// verifica il numero massimo di decimali ammessi
				if (!checkNumeroDecimali(this.strQuota, QUOTA_MAX_DECIMALS)) {
					this.addFieldError("strQuota", 
							this.getText("quotamaxdecimals", 
										 new String[]{Integer.toString(QUOTA_MAX_DECIMALS), this.strQuota}));
				}
			}
		}
		return (this.getFieldErrors().size() <= 0);
	}
	
}
