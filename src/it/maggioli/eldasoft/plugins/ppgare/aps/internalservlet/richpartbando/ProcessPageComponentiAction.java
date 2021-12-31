package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando;

import it.eldasoft.utils.utility.UtilityFiscali;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.InterceptorEncodedData;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.codifiche.ICodificheManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomConfigManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo.WizardIscrizioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo.WizardRinnovoHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.agiletec.aps.system.SystemConstants;

/**
 * Action di gestione delle operazioni nella pagina dei componenti di una RTI o
 * di un consorzio.
 *
 * @author Stefano.Sabbadin
 */
public class ProcessPageComponentiAction extends AbstractProcessPageAction implements IComponente {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -7527283479159496491L;

	public static final int QUOTA_MAX_DECIMALS = 3;
	
	private ICodificheManager _codificheManager;

	private ICustomConfigManager customConfigManager;

	private String id;
	private String idDelete;
	
	/** Valorizzato quando si conferma di proseguire senza indicazione delle consorziate. */
	private String noConsorziate;
	
	private String ragioneSociale;
	private String nazione;
	private String codiceFiscale;			// Codice fiscale consorziata o mandante.
	private String partitaIVA;				// Partita IVA consorziata o mandante.
	private String strQuota; 				// Quota acquisita in formato stringa.
	private Double quota;					// Quota di partecipazione della mandante alla RTI nella gara.

	private boolean delete;

	/** Viene impostato quando le consorziate esecutrici non sono obbligatorie e viene chiesto conferma di proseguire. */
	private boolean confirmNoConsorziate;

	private String strQuotaRTI;				// Quota RTI della mandataria acquisita in formato stringa.
	private Double quotaRTI;				// Quota di partecipazione della mandataria dell'RTI nella gara.
	
	private boolean quotaVisibile;			// campi nascosti per la validazione di quota e quotaRTI
	private boolean quotaRTIVisibile;		// campi nascosti per la validazione di quota e quotaRTI
	
	private String tipoImpresa;
	

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

	public String getStrQuota() {
		return strQuota;
	}

	public void setStrQuota(String strQuota) {
		this.strQuota = strQuota;
	}

	@Override
	public Double getQuota() {
		return quota;
	}

	@Override
	public void setQuota(Double quota) {
		this.quota = quota;
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

	@Override
	public String getTipoImpresa() {
		return tipoImpresa;
	}

	@Override
	public void setTipoImpresa(String tipoImpresa) {
		this.tipoImpresa = tipoImpresa;
	}

	/**
	 * ... 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void validate() {
		super.validate();

		// VALIDAZIONE E CONTROLLI CAMPI

		// controllo la partita iva, che puo' essere facoltativa per il
		// libero professionista ed impresa sociale se previsto da configurazione, mentre per
		// tutti gli altri casi risulta obbligatoria
		boolean isLiberoProfessionista = this.getCodificheManager()
				 .getCodifiche()
				 .get(InterceptorEncodedData.LISTA_TIPI_IMPRESA_LIBERO_PROFESSIONISTA)
				 .containsKey(this.getTipoImpresa());
		boolean isImpresaSociale = this.getCodificheManager()
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
		
		if (StringUtils.isBlank(this.id)) {
			if ((StringUtils.isNotBlank(this.ragioneSociale)
				 || StringUtils.isNotBlank(this.codiceFiscale)
				 || StringUtils.isNotBlank(this.strQuota)
				 || StringUtils.isNotBlank(this.tipoImpresa))
				&& (StringUtils.isBlank(this.partitaIVA) 
						&& ((!isLiberoProfessionista && !isImpresaSociale)
								|| (isLiberoProfessionista && isPartitaIVAObbligatoriaLiberoProfessionista)
								|| (isImpresaSociale && isPartitaIVAObbligatoriaImpresaSociale)))) 
			{
				this.addFieldError("partitaIVA", this.getText(
						"Errors.requiredstring",
						new String[] {this.getTextFromDB("partitaIVA")}));
			}
		}

		if (StringUtils.isNotBlank(this.id)) {
			if (StringUtils.isBlank(this.ragioneSociale)) {
				this.addFieldError("ragioneSociale", this.getText(
						"Errors.requiredstring",
						new String[] {this.getTextFromDB("ragioneSociale")}));
			}
			
			// controllo la partita iva, che puo' essere facoltativa per il
			// libero professionista e impresa sociale se previsto da
			// configurazione, mentre per tutti gli altri casi risulta
			// obbligatoria. 
			// si ripete il controllo perche' esiste il campo
			// Nazione sempre fillato che va trascurato, e bisogna capire se il
			// form e' di un record esistente che e' stato svuotato di tutti i
			// dati
			if (StringUtils.isBlank(this.partitaIVA) 
					&& ((!isLiberoProfessionista && !isImpresaSociale)
							|| (isLiberoProfessionista && isPartitaIVAObbligatoriaLiberoProfessionista)
							|| (isImpresaSociale && isPartitaIVAObbligatoriaImpresaSociale))) 
			{
				this.addFieldError("partitaIVA", this.getText(
						"Errors.requiredstring",
						new String[] {this.getTextFromDB("partitaIVA")}));
			}
			if (StringUtils.isBlank(this.codiceFiscale)) {
				this.addFieldError("codiceFiscale", this.getText(
						"Errors.requiredstring",
						new String[]{this.getTextFromDB("codiceFiscale")}));
			}
			if (StringUtils.isBlank(this.tipoImpresa)) {
				this.addFieldError("tipoImpresa", this.getText(
						"Errors.requiredstring",
						new String[]{this.getTextFromDB("tipoImpresa")}));
			}
		}

		if (this.getFieldErrors().size() > 0) {
			return;
		}

		// CONTROLLI APPLICATIVI SUI DATI

		try {
			boolean impresaItaliana = "ITALIA".equalsIgnoreCase(this.getNazione());
			
			// verifica del formato del codice fiscale
			if (!"".equals(this.getCodiceFiscale())
				&& !(UtilityFiscali.isValidCodiceFiscale(this.getCodiceFiscale(), impresaItaliana) || 
					 UtilityFiscali.isValidPartitaIVA(this.getCodiceFiscale(), impresaItaliana))) 
			{
				if (impresaItaliana) {
					this.addFieldError("codiceFiscale",
							this.getText("Errors.wrongField",
									new String[]{this.getTextFromDB("codiceFiscale")}));
				} else {
					this.addFieldError("codiceFiscale",
							this.getText("Errors.wrongForeignFiscalField",
									new String[]{this.getTextFromDB("codiceFiscale")}));
				}
			}

			// verifica del formato della partita iva
			if (!"".equals(this.getPartitaIVA())
				&& !UtilityFiscali.isValidPartitaIVA(this.getPartitaIVA(), impresaItaliana)) {
				if (impresaItaliana) {
					this.addFieldError("partitaIVA", this.getText(
							"Errors.wrongField",
							new String[] {this.getTextFromDB("partitaIVA")}));
				} else {
					this.addFieldError("partitaIVA", this.getText(
							"Errors.wrongForeignFiscalField",
							new String[] {this.getTextFromDB("partitaIVA")}));
				}
			}

			IRaggruppamenti helper = getSessionHelper();

			if (helper != null) {

				if (helper.checkQuota()) {
					if ((helper.isRti() && StringUtils.isBlank(this.strQuota))
						&& (StringUtils.isNotBlank(this.ragioneSociale)
						    || StringUtils.isNotBlank(this.codiceFiscale)
						    || StringUtils.isNotBlank(this.partitaIVA))) 
					{
						this.addFieldError("strQuota", this.getText(
								"Errors.requiredstring",
								new String[] {this.getTextFromDB("strQuota")}));
					} else if (!helper.isRti() && StringUtils.isNotBlank(this.strQuota)) {
						this.addActionError(this.getText("Errors.wrongQuota"));
					} else if (helper.isRti() && StringUtils.isNotBlank(this.strQuota)) {
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
					if( !checkNumeroDecimali(this.strQuota, QUOTA_MAX_DECIMALS) ) {
						this.addFieldError("strQuota", this.getText("quotamaxdecimals", 
								new String[] {Integer.toString(QUOTA_MAX_DECIMALS), this.strQuota}));
					}
				}

				if (StringUtils.isNotBlank(this.codiceFiscale)) {
					if (helper.getDatiPrincipaliImpresa().getCodiceFiscale().toUpperCase()
							.equals(this.codiceFiscale.toUpperCase())) 
					{
						this.addFieldError("codiceFiscale", this.getText(
								"Errors.sameCodiceFiscale",
								new String[] {this.codiceFiscale }));
					}
				}
				if (StringUtils.isNotBlank(this.partitaIVA) && StringUtils.isNotBlank(helper.getDatiPrincipaliImpresa().getPartitaIVA())) {
					if (helper.getDatiPrincipaliImpresa().getPartitaIVA().toUpperCase()
							.equals(this.partitaIVA.toUpperCase())) {
						this.addFieldError("partitaIVA", this.getText(
								"Errors.samePartitaIVA",
								new String[] {this.partitaIVA }));
					}
				}

				// si garantisce che non esistano duplicazioni di codice
				// fiscale/partita iva, e di ragioni sociali tra tutti i 
				// componenti inseriti				

				// NB: WizardRinnovoHelper eredita WizardIscrizioneHelper  
				//     ed eredita anche ComponentiRTI, Componenti!!!
				boolean isIscrizioneRinnovo = (helper instanceof WizardIscrizioneHelper ||
						                       helper instanceof WizardRinnovoHelper);
				
				List<IComponente> elenco = null;
				if(isIscrizioneRinnovo && helper.isRti()) {						
					elenco = ((WizardIscrizioneHelper) helper).getComponentiRTI();
				} else {
					elenco = helper.getComponenti();
				}					
				
				//String msgComponenteConsorziata = (helper.isRti() ? "un componente" : "una consorziata");
				//String msgPiuComponentiPiuConsorziate = (helper.isRti() ? "Uno o piu' componenti" : "Una o piu' consorziate");
				//String msgMandanteConsorziata = (helper.isRti() ? "una mandante" : "una consorziata");

				Set<String> setRagioniSociali = new HashSet<String>();
				Set<String> setCodiciFiscali = new HashSet<String>();
				Set<String> setPartiteIVA = new HashSet<String>();

//				if(isIscrizioneRinnovo) {

				for(int i = 0; i < elenco.size(); i++) {
					IComponente componente = elenco.get(i);

					if(isIscrizioneRinnovo && helper.isRti() && i == 0) {
						// in caso di RTI il I elemento è sempre la mandataria...
						// ...salta i controlli sulla mandataria
						continue;
					}

					if (!setRagioniSociali.contains(componente.getRagioneSociale().toUpperCase())) {
						if (StringUtils.isNotBlank(componente.getRagioneSociale())) {
							setRagioniSociali.add(componente.getRagioneSociale().toUpperCase());
						}
					} else {
						if (helper.isRti()) {
							this.addFieldError("ragioneSociale",
									this.getText("Errors.rti.moreDuplicatedRagioneSociale",
											new String[] {componente.getRagioneSociale() }));
						} else {
							this.addFieldError("ragioneSociale",
									this.getText("Errors.consorzio.moreDuplicatedRagioneSociale",
											new String[] {componente.getRagioneSociale() })); //msgComponenteConsorziata
						}
						return;
					}
											
					if (!setCodiciFiscali.contains(componente.getCodiceFiscale().toUpperCase())) {
						if (StringUtils.isNotBlank(componente.getCodiceFiscale())) {
							setCodiciFiscali.add(componente.getCodiceFiscale().toUpperCase());
						}
					} else {
						if (helper.isRti()) {
							this.addFieldError("codiceFiscale",
									this.getText("Errors.rti.moreDuplicatedCodiceFiscale",
											new String[] {componente.getCodiceFiscale() }));
						} else {
							this.addFieldError("codiceFiscale",
									this.getText("Errors.consorzio.moreDuplicatedCodiceFiscale",
											new String[] {componente.getCodiceFiscale() })); //msgComponenteConsorziata
						}
						return;
					}
					
					if (!setPartiteIVA.contains(componente.getPartitaIVA().toUpperCase())) {
						if (StringUtils.isNotBlank(componente.getPartitaIVA())) {
							setPartiteIVA.add(componente.getPartitaIVA().toUpperCase());
						}
					} else {
						if (helper.isRti()) {
							this.addFieldError("partitaIVA",
									this.getText("Errors.rti.moreDuplicatedPartitaIVA",
											new String[] {componente.getPartitaIVA() }));
						} else {
							this.addFieldError("partitaIVA",
									this.getText("Errors.consorzio.moreDuplicatedPartitaIVA",
											new String[] {componente.getPartitaIVA() })); //msgComponenteConsorziata
						}
						return;
					}

					if (componente.getCodiceFiscale().equalsIgnoreCase(helper.getDatiPrincipaliImpresa().getCodiceFiscale() )) {
						this.addFieldError("codiceFiscale",
								this.getText("Errors.sameCodiceFiscaleOperatore",
										new String[] {componente.getCodiceFiscale() }));
						return;
					}
					
					if (StringUtils.isNotBlank(helper.getDatiPrincipaliImpresa().getPartitaIVA()) && 
						helper.getDatiPrincipaliImpresa().getPartitaIVA().equalsIgnoreCase(componente.getPartitaIVA())) {
						this.addFieldError("partitaIVA",
								this.getText("Errors.samePartitaIVAOperatore",
										new String[] {componente.getPartitaIVA() }));
						return;
					}			
												
					// verifica se il componente è in aggiornamento o inserimento...
					if (StringUtils.isNotBlank(this.id)) {
						IComponente oggettoInAggiornamento = elenco.get(new Integer(this.id));
						setRagioniSociali.remove(oggettoInAggiornamento.getRagioneSociale().toUpperCase());
						setCodiciFiscali.remove(oggettoInAggiornamento.getCodiceFiscale().toUpperCase());
						setPartiteIVA.remove(oggettoInAggiornamento.getPartitaIVA().toUpperCase());
					}

					if (StringUtils.isNotBlank(this.ragioneSociale)
						&& setRagioniSociali.contains(this.ragioneSociale.toUpperCase())) {
						if (helper.isRti()) {
							this.addFieldError("ragioneSociale",
									this.getText("Errors.rti.duplicatedRagioneSociale",
											new String[] {componente.getRagioneSociale() }));
						} else {
							this.addFieldError("ragioneSociale",
									this.getText("Errors.consorzio.duplicatedRagioneSociale",
											new String[] {this.ragioneSociale })); //msgComponenteConsorziata
						}
					}
					
					if (StringUtils.isNotBlank(this.codiceFiscale)) {
						if (setCodiciFiscali.contains(this.codiceFiscale.toUpperCase())) {
							if (helper.isRti()) {
								this.addFieldError("codiceFiscale",
										this.getText("Errors.rti.duplicatedCodiceFiscale",
												new String[] {this.codiceFiscale }));
							} else {
								this.addFieldError("codiceFiscale",
										this.getText("Errors.consorzio.duplicatedCodiceFiscale",
												new String[] {this.codiceFiscale })); //msgComponenteConsorziata
							}
						}
					}
					
					if (StringUtils.isNotBlank(this.partitaIVA)) {
						if (setPartiteIVA.contains(this.partitaIVA.toUpperCase())) {
							if (helper.isRti()) {
								this.addFieldError("partitaIVA",
										this.getText("Errors.rti.duplicatedPartitaIVA",
												new String[] {this.partitaIVA }));
							} else {
								this.addFieldError("partitaIVA",
										this.getText("Errors.consorzio.duplicatedPartitaIVA",
												new String[] {this.partitaIVA })); //msgComponenteConsorziata
							}
						}
					}	
				}
			}
		} catch (Throwable t) {
			throw new RuntimeException("Errore durante la verifica dei dati richiesti per l'impresa "
					+ this.getRagioneSociale(), t);
		}
	}
	
	/**
	 * ... 
	 */
	@Override
	public String next() {
		String target = SUCCESS;
		
		IRaggruppamenti helper = getSessionHelper();
		
		WizardPartecipazioneHelper partecipazioneHelper = (WizardPartecipazioneHelper) helper;
	
		if(helper != null 
		   && (null != this.getCurrentUser() 
			   && !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME))) 
		{
			if (this.ragioneSociale.length() > 0) {
				// sono stati inseriti dei dati, si procede al salvataggio
				if (StringUtils.isNotBlank(this.id)) {
					// aggiorna i dati in sessione (codice preso da modify)
					IComponente componente = helper.getComponenti().get(Integer.parseInt(this.id));
					ProcessPageComponentiAction.synchronizeComponente(this, componente);
				} else {
					// aggiunge i dati in sessione (codice preso da insert)
					ComponenteHelper componente = new ComponenteHelper();
					ProcessPageComponentiAction.synchronizeComponente(this, componente);
					helper.getComponenti().add(componente);
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
				if( !checkNumeroDecimali(this.strQuotaRTI, QUOTA_MAX_DECIMALS) ) {
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
								&& StringUtils.isBlank(componente.getCodiceFiscale()))
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
	 * ... 
	 */
	@Override
	public String back() {
		String target = "back"; 

		WizardPartecipazioneHelper partecipazioneHelper = (WizardPartecipazioneHelper)this.session
				.get(PortGareSystemConstants.SESSION_ID_DETT_PART_GARA);

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
			if (this.ragioneSociale.length() > 0) {
				ComponenteHelper componente = new ComponenteHelper();
				ProcessPageComponentiAction.synchronizeComponente(this, componente);
				helper.getComponenti().add(componente);
			}
		}
		return target;
	}

	/**
	 * ... 
	 */
	public String save() {
		String target = "refresh";
		
		WizardIscrizioneHelper helper = (WizardIscrizioneHelper) getSessionHelper();
		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			// aggiorna i dati in sessione
			IComponente componente = null;
			int id = Integer.parseInt(this.id);
			if(!helper.isRti()) {
				componente = helper.getComponenti().get(id);
			} else {
				componente = helper.getComponentiRTI().get(id);				
			}
			ProcessPageComponentiAction.synchronizeComponente(this, componente);
		}
		return target;
	}

	/**
	 * ... 
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
			IComponente componente = null;
			componente = helper.getComponenti().get(Integer.parseInt(this.id));
			if(!helper.isRti()) {
				ProcessPageComponentiAction.synchronizeComponente(this, componente);
			} else {
				ProcessPageComponentiAction.synchronizeComponente(this, componente);
			}
		}
		return target;
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
	 * Ritorna l'helper in sessione utilizzato per la memorizzazione dei dati
	 * sulla partecipazione in RTI.
	 * 
	 * @return helper contenente i dati per la gestione di RTI e componenti
	 */
	protected IRaggruppamenti getSessionHelper() {
		return (WizardPartecipazioneHelper) this.session
				.get(PortGareSystemConstants.SESSION_ID_DETT_PART_GARA);
	}

	/**
	 * ... 
	 */
	public static void resetComponente(IComponente componente) {
		componente.setRagioneSociale(null);
		componente.setNazione("Italia");
		componente.setCodiceFiscale(null);
		componente.setPartitaIVA(null);
		componente.setTipoImpresa(null);
		componente.setQuota(null);
	}

	/**
	 * ... 
	 */
	public static void synchronizeComponente(IComponente from, IComponente to) {
		to.setRagioneSociale(from.getRagioneSociale());
		to.setNazione(from.getNazione());
		to.setCodiceFiscale(from.getCodiceFiscale());
		to.setPartitaIVA(from.getPartitaIVA());
		to.setTipoImpresa(from.getTipoImpresa());
		to.setQuota(from.getQuota());
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
	
}
