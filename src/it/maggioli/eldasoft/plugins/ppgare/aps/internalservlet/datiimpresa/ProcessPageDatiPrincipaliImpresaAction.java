package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa;

import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.InterceptorEncodedData;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.codifiche.ICodificheManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.datiimpresa.DatiImpresaChecker;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.CustomConfigManager;
import org.apache.commons.lang.StringUtils;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.plugins.jpmail.aps.services.mail.IMailManager;

/**
 * Action di gestione delle operazioni nella pagina dei dati principali
 * dell'impresa nel wizard di aggiornamento dati impresa
 *
 * @author Stefano.Sabbadin
 * @since 1.2
 */
public class ProcessPageDatiPrincipaliImpresaAction extends
				AbstractProcessPageAction implements IDatiPrincipaliImpresa {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -4838608462413593545L;

	private ICodificheManager _codificheManager;

	private IMailManager mailManager;

	protected DatiImpresaChecker _datiImpresaChecker;
	private CustomConfigManager customConfigManager;
	private String ragioneSociale;
	private String naturaGiuridica;
	private String tipoImpresa;
	private String ambitoTerritoriale;
	private String codiceFiscale;
	private String partitaIVA;
	private String microPiccolaMediaImpresa;

	private String indirizzoSedeLegale;
	private String numCivicoSedeLegale;
	private String capSedeLegale;
	private String comuneSedeLegale;
	private String provinciaSedeLegale;
	private String nazioneSedeLegale;
	private String sitoWeb;
	private String telefonoRecapito;
	private String faxRecapito;
	private String cellulareRecapito;
	private String emailRecapito;
	private String emailPECRecapito;
	private String emailRecapitoConferma;
	private String emailPECRecapitoConferma;
	
	private boolean obblFaxRecapito;
	private boolean visEmailRecapito;
	private boolean obblEmailRecapito;
	private boolean obblEmailPECRecapito;
	private boolean obblPECEstero;

	public void setCustomConfigManager(CustomConfigManager customConfigManager) {
		this.customConfigManager = customConfigManager;
	}
	
	private boolean obblCellulareRecapito;

	public void setCodificheManager(ICodificheManager manager) {
		this._codificheManager = manager;
	}

	public ICodificheManager getCodificheManager() {
		return _codificheManager;
	}

	public void setMailManager(IMailManager mailManager) {
		this.mailManager = mailManager;
	}

	public void setDatiImpresaChecker(DatiImpresaChecker datiImpresaChecker) {
		this._datiImpresaChecker = datiImpresaChecker;
	}

	public String getRagioneSociale() {
		return ragioneSociale;
	}

	public void setRagioneSociale(String ragioneSociale) {
		this.ragioneSociale = ragioneSociale.trim();
	}

	public String getNaturaGiuridica() {
		return naturaGiuridica;
	}

	public void setNaturaGiuridica(String naturaGiuridica) {
		this.naturaGiuridica = naturaGiuridica;
	}

	public String getTipoImpresa() {
		return tipoImpresa;
	}

	public void setTipoImpresa(String tipoImpresa) {
		this.tipoImpresa = tipoImpresa;
	}

	public String getAmbitoTerritoriale() {
		return ambitoTerritoriale;
	}

	public void setAmbitoTerritoriale(String ambitoTerritoriale) {
		this.ambitoTerritoriale = ambitoTerritoriale;
	}

	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale.trim().toUpperCase();
	}

	public String getPartitaIVA() {
		return partitaIVA;
	}

	public void setPartitaIVA(String partitaIVA) {
		this.partitaIVA = partitaIVA.trim().toUpperCase();
	}

	@Override
	public String getMicroPiccolaMediaImpresa() {
		return this.microPiccolaMediaImpresa;
	}

	@Override
	public void setMicroPiccolaMediaImpresa(String microPiccolaMediaImpresa) {
		this.microPiccolaMediaImpresa = microPiccolaMediaImpresa;
	}
	
	public String getIndirizzoSedeLegale() {
		return indirizzoSedeLegale;
	}

	public void setIndirizzoSedeLegale(String indirizzoSedeLegale) {
		this.indirizzoSedeLegale = indirizzoSedeLegale.trim();
	}

	public String getNumCivicoSedeLegale() {
		return numCivicoSedeLegale;
	}

	public void setNumCivicoSedeLegale(String numCivicoSedeLegale) {
		this.numCivicoSedeLegale = numCivicoSedeLegale.trim();
	}

	public String getCapSedeLegale() {
		return capSedeLegale;
	}

	public void setCapSedeLegale(String capSedeLegale) {
		this.capSedeLegale = capSedeLegale;
	}

	public String getComuneSedeLegale() {
		return comuneSedeLegale;
	}

	public void setComuneSedeLegale(String comuneSedeLegale) {
		this.comuneSedeLegale = comuneSedeLegale.trim();
	}

	public String getProvinciaSedeLegale() {
		return provinciaSedeLegale;
	}

	public void setProvinciaSedeLegale(String provinciaSedeLegale) {
		this.provinciaSedeLegale = provinciaSedeLegale;
	}

	public String getNazioneSedeLegale() {
		return nazioneSedeLegale;
	}

	public void setNazioneSedeLegale(String nazioneSedeLegale) {
		this.nazioneSedeLegale = nazioneSedeLegale.trim();
	}

	public String getSitoWeb() {
		return sitoWeb;
	}

	public void setSitoWeb(String sitoWeb) {
		this.sitoWeb = sitoWeb;
	}

	// /**
	// * @return the modalitaComunicazioneRecapito
	// */
	// public String getModalitaComunicazioneRecapito() {
	// return modalitaComunicazioneRecapito;
	// }
	//
	// /**
	// * @param modalitaComunicazioneRecapito the modalitaComunicazioneRecapito
	// to set
	// */
	// public void setModalitaComunicazioneRecapito(
	// String modalitaComunicazioneRecapito) {
	// this.modalitaComunicazioneRecapito = modalitaComunicazioneRecapito;
	// }
	
	public String getTelefonoRecapito() {
		return telefonoRecapito;
	}

	public void setTelefonoRecapito(String telefonoRecapito) {
		this.telefonoRecapito = telefonoRecapito;
	}

	public String getFaxRecapito() {
		return faxRecapito;
	}

	public void setFaxRecapito(String faxRecapito) {
		this.faxRecapito = faxRecapito;
	}

	public String getCellulareRecapito() {
		return cellulareRecapito;
	}

	public void setCellulareRecapito(String cellulareRecapito) {
		this.cellulareRecapito = cellulareRecapito;
	}

	public String getEmailRecapito() {
		return emailRecapito;
	}

	public void setEmailRecapito(String emailRecapito) {
		this.emailRecapito = emailRecapito.trim();
	}

	@Override
	public String getEmailPECRecapito() {
		return this.emailPECRecapito;
	}

	@Override
	public void setEmailPECRecapito(String emailPECRecapito) {
		this.emailPECRecapito = emailPECRecapito.trim();
	}

	public String getEmailRecapitoConferma() {
		return emailRecapitoConferma;
	}

	public void setEmailRecapitoConferma(String emailRecapitoConferma) {
		this.emailRecapitoConferma = emailRecapitoConferma;
	}

	public String getEmailPECRecapitoConferma() {
		return emailPECRecapitoConferma;
	}

	public void setEmailPECRecapitoConferma(String emailPECRecapitoConferma) {
		this.emailPECRecapitoConferma = emailPECRecapitoConferma;
	}

	public boolean isObblFaxRecapito() {
		return obblFaxRecapito;
	}

	public void setObblFaxRecapito(boolean obblFaxRecapito) {
		this.obblFaxRecapito = obblFaxRecapito;
	}

	public boolean isVisEmailRecapito() {
		return visEmailRecapito;
	}

	public void setVisEmailRecapito(boolean visEmailRecapito) {
		this.visEmailRecapito = visEmailRecapito;
	}

	public boolean isObblEmailRecapito() {
		return obblEmailRecapito;
	}

	public void setObblEmailRecapito(boolean obblEmailRecapito) {
		this.obblEmailRecapito = obblEmailRecapito;
	}

	public boolean isObblEmailPECRecapito() {
		return obblEmailPECRecapito;
	}

	public void setObblEmailPECRecapito(boolean obblEmailPECRecapito) {
		this.obblEmailPECRecapito = obblEmailPECRecapito;
	}

	public boolean isObblPECEstero() {
		return obblPECEstero;
	}

	public void setObblPECEstero(boolean obblPECEstero) {
		this.obblPECEstero = obblPECEstero;
	}
	
	public boolean isObblCellulareRecapito() {
		return obblCellulareRecapito;
	}

	public void setObblCellulareRecapito(boolean obblCellulareRecapito) {
		this.obblCellulareRecapito = obblCellulareRecapito;
	}

	/**
	 * Si verifica che l'eventuale modifica degli indirizzi email non vada in
	 * conflitto con gli indirizzi email usati come riferimento per altre imprese.
	 */
	@Override
	public void validate() {

		super.validate();
		if (this.getFieldErrors().size() > 0) {
			return;
		}
		try {
			// verifica la correttezza deli indirizzi email			
			if( !StringUtils.equals(this.emailRecapito, this.emailRecapitoConferma) ) {
				this.addFieldError("emailRecapito", this.getText("Errors.mailConfirmNotEqual", new String[]{this.emailRecapito}));				
			}
			
			if( !StringUtils.equals( this.emailPECRecapito, this.emailPECRecapitoConferma) ) {
				this.addFieldError("emailPECRecapito", this.getText("Errors.pecConfirmNotEqual", new String[]{this.emailPECRecapito}));
			}
						
			String email = DatiImpresaChecker.getEmailRiferimento(
							this.getEmailPECRecapito(), this.getEmailRecapito());
			if (email != null
				&& this._datiImpresaChecker.existsEmail(this.getCurrentUser().getUsername(), email)) {
				this.addFieldError("emailRecapito", this.getText("Errors.mailAlreadyPresent", new String[] { email}));
			}
			
			boolean invalidCap = (this.capSedeLegale != null && StringUtils.isNotEmpty(this.capSedeLegale) && !this.capSedeLegale.matches("[0-9]+"));
			boolean invalidAlphaCap = (this.capSedeLegale != null && StringUtils.isNotEmpty(this.capSedeLegale) && !this.capSedeLegale.matches("[0-9a-zA-Z]+"));
			
			if("1".equals(this.ambitoTerritoriale)) {
				// operatore economico italiano
				if(invalidCap) {
					this.addFieldError("capSedeLegale", this.getTextFromDB("capSedeLegale") + " " + 
														this.getText("localstrings.wrongCharacters"));
				}				
			} else {
				// operatore economico UE/extra UE 
				this.partitaIVA = this.codiceFiscale;
			}
			
		} catch (ApsSystemException t) {
			throw new RuntimeException(
							"Errore durante la verifica dei dati richiesti per l'impresa "
							+ this.getRagioneSociale(), t);
		}
	}

	@Override
	public String next() {

		String target = SUCCESS;
		WizardDatiImpresaHelper helper = getSessionHelper();

		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			helper.setDittaIndividuale(this._codificheManager
							.getCodifiche()
							.get(InterceptorEncodedData.LISTA_TIPI_IMPRESA_DITTA_INDIVIDUALE)
							.containsKey(this.getTipoImpresa()));
			helper.setLiberoProfessionista(this._codificheManager
							.getCodifiche()
							.get(InterceptorEncodedData.LISTA_TIPI_IMPRESA_LIBERO_PROFESSIONISTA)
							.containsKey(this.getTipoImpresa()));
			// verifica se e' cambiata la mail di riferimento
			String email = DatiImpresaChecker.getEmailRiferimento(
							this.getEmailPECRecapito(), this.getEmailRecapito());
			if (!email.equals(helper.getMailUtentePrecedente())) {
				if (!helper.isMailVariata()) {
					helper.setMailVariata(true);
					helper.setMailUtentePrecedente(email);
					helper.setMailUtenteImpostata(email);
					// invia la mail
					String text = this.getText("label.testMail.testo");
					String subject = this.getText("label.testMail.oggetto");
					try {
						this.mailManager.sendMail(text, subject,
										new String[]{email}, null, null,
										CommonSystemConstants.SENDER_CODE);
					} catch (ApsSystemException e) {
						ApsSystemUtils.logThrowable(e, this, "next",
										"Errore durante l'invio della mail di prova per la verifica dell'indirizzo "
										+ email);
					}
				} else {
					helper.setMailVariataDopoInvio(true);
				}
			}

			// aggiorna i dati in sessione
			OpenPageDatiPrincipaliImpresaAction
							.synchronizeDatiPrincipaliImpresa(this,
											helper.getDatiPrincipaliImpresa());

		}
		try {
			if(!customConfigManager.isVisible("IMPRESA-INDIRIZZI", "STEP")){
				if(helper.isLiberoProfessionista()){
					target = "successNoIndirizziLiberoProf";
				} else {
					target = "successNoIndirizzi";
				}
			}
		}catch (Exception e) {
			// Configurazione sbagliata
			ApsSystemUtils.logThrowable(e, this, "next",
					"Errore durante la ricerca delle proprietà di visualizzazione dello step indirizzi");
		}
		
		return target;
	}

	/**
	 * Estrae l'helper del wizard dati dell'impresa da utilizzare nei controlli
	 *
	 * @return helper contenente i dati dell'impresa
	 */
	protected WizardDatiImpresaHelper getSessionHelper() {
		WizardDatiImpresaHelper helper = (WizardDatiImpresaHelper) this.session
						.get(PortGareSystemConstants.SESSION_ID_DETT_ANAGRAFICA_IMPRESA);
		return helper;
	}

}
