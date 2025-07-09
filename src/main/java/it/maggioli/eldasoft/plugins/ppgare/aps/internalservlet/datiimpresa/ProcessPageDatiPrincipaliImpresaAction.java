package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.plugins.jpmail.aps.services.mail.IMailManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.InterceptorEncodedData;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.codifiche.ICodificheManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.CustomConfigFeatures;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.CustomConfigManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.WithError;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.datiimpresa.DatiImpresaChecker;
import org.apache.commons.lang.StringUtils;

import java.util.LinkedHashMap;
import java.util.regex.Pattern;

/**
 * Action di gestione delle operazioni nella pagina dei dati principali
 * dell'impresa nel wizard di aggiornamento dati impresa
 *
 * @author Stefano.Sabbadin
 * @since 1.2
 */
@FlussiAccessiDistinti({ 
	EFlussiAccessiDistinti.MODIFICA_IMPRESA, EFlussiAccessiDistinti.REGISTRAZIONE_IMPRESA,
	EFlussiAccessiDistinti.ISCRIZIONE_ELENCO, EFlussiAccessiDistinti.RINNOVO_ELENCO, 
	EFlussiAccessiDistinti.ISCRIZIONE_CATALOGO, EFlussiAccessiDistinti.RINNOVO_CATALOGO
	})
public class ProcessPageDatiPrincipaliImpresaAction extends
				AbstractProcessPageAction implements IDatiPrincipaliImpresa {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -4838608462413593545L;
	public static final Pattern TELEFONO = Pattern.compile("^\\+\\d{1,3}\\s\\d+(\\-\\d+)*$");

	private ICodificheManager _codificheManager;
	private IMailManager mailManager;
	protected DatiImpresaChecker _datiImpresaChecker;
	private CustomConfigManager customConfigManager;

	@Validate(EParamValidation.RAGIONE_SOCIALE)
	private String ragioneSociale;
	@Validate(EParamValidation.NATURA_GIURIDICA)
	private String naturaGiuridica;
	@Validate(EParamValidation.TIPO_IMPRESA)
	private String tipoImpresa;
	@Validate(EParamValidation.AMBITO_TERRITORIALE)
	private String ambitoTerritoriale;
	@Validate(value = EParamValidation.CODICE_FISCALE_O_IDENTIFICATIVO, error = @WithError(fieldLabel = "CODICE_FISCALE_O_IDENTIFICATIVO"))
	private String codiceFiscale;
	@Validate(EParamValidation.PARTITA_IVA)
	private String partitaIVA;
	@Validate(EParamValidation.SI_NO)
	private String microPiccolaMediaImpresa;
	@Validate(EParamValidation.INDIRIZZO)
	private String indirizzoSedeLegale;
	@Validate(EParamValidation.NUM_CIVICO)
	private String numCivicoSedeLegale;
	@Validate(EParamValidation.CAP)
	private String capSedeLegale;
	@Validate(EParamValidation.COMUNE)
	private String comuneSedeLegale;
	@Validate(EParamValidation.PROVINCIA)
	private String provinciaSedeLegale;
	@Validate(EParamValidation.NAZIONE)
	private String nazioneSedeLegale;
	@Validate(EParamValidation.URL)
	private String sitoWeb;
	@Validate(value = EParamValidation.TELEFONO)
	private String telefonoRecapito;
	@Validate(EParamValidation.TELEFONO)
	private String faxRecapito;
	@Validate(EParamValidation.FAX)
	private String cellulareRecapito;
	@Validate(EParamValidation.EMAIL)
	private String emailRecapito;
	@Validate(EParamValidation.EMAIL)
	private String emailPECRecapito;
	@Validate(EParamValidation.EMAIL)
	private String emailRecapitoConferma;
	@Validate(EParamValidation.EMAIL)
	private String emailPECRecapitoConferma;
	
	private boolean obblFaxRecapito;
	private boolean visEmailRecapito;
	private boolean obblEmailRecapito;
	private boolean obblEmailPECRecapito;
	private boolean obblPECEstero;
	private String vatGroup;

	private boolean obblCellulareRecapito;
	private boolean gruppoIvaAbilitato;
	@Validate(EParamValidation.TIPO_SOCIETA_COOPERATIVA)
	private String tipoSocietaCooperativa;
	
	public void setCustomConfigManager(CustomConfigManager customConfigManager) {
		this.customConfigManager = customConfigManager;
	}
	
	public void setCodificheManager(ICodificheManager manager) {
		this._codificheManager = manager;
	}

	public ICodificheManager getCodificheManager() {
		return _codificheManager;
	}

	public void setMailManager(IMailManager mailManager) {
		this.mailManager = mailManager;
	}
	
    public IMailManager getMailManager() {
      return mailManager;
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
	public String getIdAnagraficaEsterna() {
		return null;
	}

	@Override
	public void setIdAnagraficaEsterna(String idAnagraficaEsterna) {
		// non viene gestito a video
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
		this.telefonoRecapito =
				StringUtils.isNotEmpty(telefonoRecapito)
					? telefonoRecapito.trim()
					: telefonoRecapito;
	}

	public String getFaxRecapito() {
		return faxRecapito;
	}

	public void setFaxRecapito(String faxRecapito) {
		this.faxRecapito =
				StringUtils.isNotEmpty(faxRecapito)
					? faxRecapito.trim()
					: faxRecapito;
	}

	public String getCellulareRecapito() {
		return cellulareRecapito;
	}

	public void setCellulareRecapito(String cellulareRecapito) {
		this.cellulareRecapito =
				StringUtils.isNotEmpty(cellulareRecapito)
					? cellulareRecapito.trim()
					: cellulareRecapito;
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
	
	public String getVatGroup() {
		return vatGroup;
	}

	public void setVatGroup(String vatGroup) {
		this.vatGroup = vatGroup;
	}
	
	public boolean isGruppoIvaAbilitato() {
		return gruppoIvaAbilitato;
	}

	public void setGruppoIvaAbilitato(boolean gruppoIvaAbilitato) {
		this.gruppoIvaAbilitato = gruppoIvaAbilitato;
	}

	public String getTipoSocietaCooperativa() {
		return tipoSocietaCooperativa;
	}

	public void setTipoSocietaCooperativa(String tipoSocietaCooperativa) {
		this.tipoSocietaCooperativa = tipoSocietaCooperativa;
	}

	/**
	 * Si verifica che l'eventuale modifica degli indirizzi email non vada in
	 * conflitto con gli indirizzi email usati come riferimento per altre imprese.
	 */
	@Override
	public void validate() {
		boolean isGruppoIVAAbilitato = this
				.getCodificheManager().getCodifiche()
				.get(InterceptorEncodedData.CHECK_GRUPPO_IVA)
				.containsValue("1");
		this.setGruppoIvaAbilitato(isGruppoIVAAbilitato);

		boolean OEItaliano = ("1".equals(this.ambitoTerritoriale));
		
		try {
			LinkedHashMap<String, String> formeGiuridicheCooperative =
					InterceptorEncodedData.get(InterceptorEncodedData.LISTA_FORME_GIURIDICHE_COOPERATIVE);
			if (StringUtils.isEmpty(tipoSocietaCooperativa)
					&& !formeGiuridicheCooperative.isEmpty()
					&& formeGiuridicheCooperative.containsValue(naturaGiuridica))
				addFieldError("tipoSocietaCooperativa", getText("Errors.requiredstring", new String[] { getTextFromDB("tipoSocietaCooperativa") }));
		} catch (Exception e) {
			addFieldError("tipoSocietaCooperativa", getText("Errors.requiredstring", new String[] { getTextFromDB("tipoSocietaCooperativa") }));
		}

		try {
			if (customConfigManager.isActiveFunction(CustomConfigFeatures.PREFISSO_TEL.getKey(), CustomConfigFeatures.PREFISSO_TEL.getValue())) {
				if (StringUtils.isNotEmpty(telefonoRecapito) && !TELEFONO.matcher(telefonoRecapito).matches())
					addFieldError("telefonoRecapito"
							, getText("telefono.prefisso.richiesto", new String[]{ getI18nLabel("LABEL_TELEFONO") }));
				if (StringUtils.isNotEmpty(faxRecapito) && !TELEFONO.matcher(faxRecapito).matches())
					addFieldError("faxRecapito"
							, getText("telefono.prefisso.richiesto", new String[]{ getI18nLabel("LABEL_FAX") }));
				if (StringUtils.isNotEmpty(cellulareRecapito) && !TELEFONO.matcher(cellulareRecapito).matches())
					addFieldError("cellulareRecapito"
							, getText("telefono.prefisso.richiesto", new String[]{ getI18nLabel("LABEL_CELLULARE") }));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		// NB: spostata la validazione da ProcessPageDatiPrincipaliImpresaAction-validation.xml alla action
		//     aggiungi all'elenco dei messaggi di validazione anche questo eventuale per cf/id fiscale estero 
		if(StringUtils.isEmpty(this.codiceFiscale)) {
			String cf = ("1".equals(this.ambitoTerritoriale) ? "codiceFiscale" : "idFiscaleEstero");
			this.addFieldError(cf, this.getText("Errors.requiredstring", new String[] { this.getTextFromDB(cf) }));
		}
		
		// OE Italiano: verifica la lunghezza del CF (16 caratteri)
		if(OEItaliano && StringUtils.isNotEmpty(this.codiceFiscale) && this.codiceFiscale.length() > 16) {
			this.addFieldError("codiceFiscale", this.getText("Errors.stringlength", 
															 new String[] { this.getTextFromDB("codiceFiscale"), "16" }));
		}
		
		super.validate();
		if (this.getFieldErrors().size() > 0) {
			return;
		}
		
		try {
			// verifica la correttezza deli indirizzi email
			if( !StringUtils.equals(this.emailRecapito, this.emailRecapitoConferma) ) {
				this.addFieldError("emailRecapito", this.getText("Errors.mailConfirmNotEqual", new String[]{this.emailRecapito}));
			}
			
			if(!"ITALIA".equals(nazioneSedeLegale.trim().toUpperCase())) {
				this.provinciaSedeLegale = "";
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
			
			if(OEItaliano) {
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
			// verifica se i gruppi iva sono abilitati
			if( !this.gruppoIvaAbilitato ) {
				helper.getDatiPrincipaliImpresa().setVatGroup(null);
			}
			
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
					} catch (Throwable t) {
						ApsSystemUtils.logThrowable(t, this, "next",
										"Errore durante l'invio della mail di prova per la verifica dell'indirizzo "
										+ email);
					}
				} else {
					helper.setMailVariataDopoInvio(true);
				}
			}

			// aggiorna i dati in sessione
			OpenPageDatiPrincipaliImpresaAction.synchronizeDatiPrincipaliImpresa(
					this,
					helper.getDatiPrincipaliImpresa());
		}
		
		try {
			if(!customConfigManager.isVisible("IMPRESA-INDIRIZZI", "STEP")) {
				if(helper.isLiberoProfessionista()){
					target = "successNoIndirizziLiberoProf";
				} else {
					target = "successNoIndirizzi";
				}
			}
		} catch (Exception e) {
			// Configurazione sbagliata
			ApsSystemUtils.logThrowable(e, this, "next",
					"Errore durante la ricerca delle propriet� di visualizzazione dello step indirizzi");
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
